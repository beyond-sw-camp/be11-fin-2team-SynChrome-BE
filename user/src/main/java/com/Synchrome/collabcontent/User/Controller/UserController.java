package com.Synchrome.collabcontent.User.Controller;

import com.Synchrome.collabcontent.Common.auth.JwtTokenProvider;
import com.Synchrome.collabcontent.User.Domain.User;
import com.Synchrome.collabcontent.User.Dto.*;
import com.Synchrome.collabcontent.User.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    @Qualifier("rtdb")
    private final RedisTemplate<String,Object> redisTemplate;

    @Value("${jwt.secretKeyRT}")
    private String secretKeyRT;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, RedisTemplate<String, Object> redisTemplate) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/google/doLogin")
    public ResponseEntity<?> googleDoLogin(@RequestBody GoogleLoginDto dto){
        AccessTokendto accessTokendto = userService.getAccessToken(dto.getCode());
        GoogleProfileDto googleProfileDto = userService.getGoogleProfile(accessTokendto.getAccess_token());
        User originalUser = userService.getUserByEmail(googleProfileDto.getEmail());
        System.out.println("컨트롤러 : " + originalUser);
        if(originalUser == null){
            UserSaveReqDto response = UserSaveReqDto.builder().email(googleProfileDto.getEmail()).name(googleProfileDto.getName()).build();
            originalUser = userService.save(response);
        }
        String jwtToken = jwtTokenProvider.createToken(originalUser.getId());
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(originalUser.getId());
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id",originalUser.getId());
        loginInfo.put("token",jwtToken);
        loginInfo.put("refreshToken",jwtRefreshToken);
        return new ResponseEntity<>(loginInfo,HttpStatus.OK);
    }
}