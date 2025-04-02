package com.Synchrome.user.User.Service;

import com.Synchrome.user.User.Domain.User;
import com.Synchrome.user.User.Dto.AccessTokendto;
import com.Synchrome.user.User.Dto.GoogleProfileDto;
import com.Synchrome.user.User.Dto.UserInfoDto;
import com.Synchrome.user.User.Dto.UserSaveReqDto;
import com.Synchrome.user.User.Repository.UserRepository;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);

    @Value("${oauth.google.client-id}")
    private String googleClientId;
    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;
    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;

    public UserService(UserRepository userRepository, @Qualifier("userInfoDB") RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    public User save(UserSaveReqDto userSaveReqDto){
        User user = User.builder().name(userSaveReqDto.getName()).email(userSaveReqDto.getEmail()).build();
        userRepository.save(user);
        return user;
    }

    public AccessTokendto getAccessToken(String code){
        RestClient restClient = RestClient.create();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code",code);
        params.add("client_id",googleClientId);
        params.add("client_secret",googleClientSecret);
        params.add("redirect_uri",googleRedirectUri);
        params.add("grant_type", "authorization_code");
        ResponseEntity<AccessTokendto> response = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .header("Content-Type","application/x-www-form-urlencoded")
                .body(params)
                .retrieve()
                .toEntity(AccessTokendto.class);
        return response.getBody();
    }

    public GoogleProfileDto getGoogleProfile(String token){
        RestClient restClient = RestClient.create();
        ResponseEntity<GoogleProfileDto> response = restClient.post()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .header("Authorization","Bearer " +token)
                .retrieve()
                .toEntity(GoogleProfileDto.class);
        return response.getBody();
    }

    public User getUserByEmail(String email){
        User user = userRepository.findByEmail(email).orElse(null);
        return user;
    }

    public void userInfoCaching(User loginuser){
        String redisKey = String.valueOf(loginuser.getId());
        UserInfoDto userInfoDto = UserInfoDto.builder().id(loginuser.getId()).name(loginuser.getName()).email(loginuser.getEmail()).build();
        try {
            String userInfoJson = objectMapper.writeValueAsString(userInfoDto);
            redisTemplate.opsForValue().set(redisKey, userInfoJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 저장 중 오류 발생", e);
        }
    }

    public UserInfoDto getUserInfo(Long id){
        String redisKey = String.valueOf(id);
        try {
            String cachedUserInfoJson = (String) redisTemplate.opsForValue().get(redisKey);
            if (cachedUserInfoJson != null) {
                return objectMapper.readValue(cachedUserInfoJson, UserInfoDto.class);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 조회 중 오류 발생", e);
        }
        return null;
    }

    public void deleteUserInfo(Long id){
        String redisKey = String.valueOf(id);
        redisTemplate.delete(redisKey);
    }
}
