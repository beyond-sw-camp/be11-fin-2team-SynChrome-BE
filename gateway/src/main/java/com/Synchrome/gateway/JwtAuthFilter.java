package com.Synchrome.gateway;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthFilter implements GlobalFilter {

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> ALLOWED_PATHS = List.of(
            "/user/google/doLogin",
            "/connect/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // token 검증
        System.out.println("-----------------------------------");
        System.out.println("gateway token 검증 시작");
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        System.out.println(exchange.getRequest().getHeaders());
        String path = exchange.getRequest().getURI().getRawPath();
        System.out.println("path : " + exchange.getRequest().getURI().getRawPath());

        // ✅ 경로 패턴 검사
        boolean isAllowed = ALLOWED_PATHS.stream().anyMatch(allowed -> pathMatcher.match(allowed, path));

//        exchange.getAttributes().forEach((key, value) -> {
//            System.out.println("🪵 Gateway attr - " + key + " = " + value);
//        });


//        boolean isAllowed = ALLOWED_PATHS.stream()
//                .anyMatch(allowed -> pathMatcher.match(allowed, originalPath));

        if (isAllowed) {
            System.out.println("인증 우회");
            return chain.filter(exchange); // 인증 우회
        }
        System.out.println("인증 수행");
        try {
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                System.out.println("bearerToken : " + bearerToken);
                throw new IllegalArgumentException("token 관련 예외 발생");
            }
            String token = bearerToken.substring(7);

            // token 검증 및 claims 추출
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 사용자 ID 추출
            String userId = claims.getSubject();

            // 헤더에 X-User-Id변수로 id값 추가 및 ROLE 추가
            // X를 붙이는 것은 custom header라는 것을 의미하는 널리 쓰이는 관례
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(builder -> builder
                            .header("X-User-Id", userId)
                    )
                    .build();

            // Spring Cloud Gateway는 여러 필터를 GatewayFilterChain이라는 구조로 관리
            // 다시 filter chain으로 되돌아 가는 로직.
            return chain.filter(modifiedExchange);
        } catch (IllegalArgumentException | MalformedJwtException | ExpiredJwtException | SignatureException |
                 UnsupportedJwtException e) {
            e.printStackTrace();
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}