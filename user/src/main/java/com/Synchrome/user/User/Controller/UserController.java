package com.Synchrome.user.User.Controller;

import com.Synchrome.user.Common.auth.JwtTokenProvider;
import com.Synchrome.user.User.Domain.User;
import com.Synchrome.user.User.Dto.*;
import com.Synchrome.user.User.Service.UserService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
        userService.userInfoCaching(originalUser);
        return new ResponseEntity<>(loginInfo,HttpStatus.OK);
    }

    @PostMapping("/userInfo")
    public ResponseEntity<?> userInfo(@RequestBody FindUserDto findUserDto){
        UserInfoDto response = userService.getUserInfo(findUserDto.getId());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/deleteUserInfo")
    public ResponseEntity<?> deleteUserInfo(@RequestBody FindUserDto findUserDto){
        userService.deleteUserInfo(findUserDto.getId());
        return new ResponseEntity<>(findUserDto,HttpStatus.OK);
    }
}