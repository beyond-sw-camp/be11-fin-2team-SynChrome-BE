package com.Synchrome.user.User.Controller;

import com.Synchrome.user.Common.auth.JwtTokenProvider;
import com.Synchrome.user.User.Domain.User;
import com.Synchrome.user.User.Dto.*;
import com.Synchrome.user.User.Service.UserService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.hibernate.annotations.processing.Find;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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

    @PostMapping("/pay")
    public ResponseEntity<?> processPayment(@RequestBody PaymentReqDto paymentReqDto) {
        try {
            System.out.println("Received impUid: " + paymentReqDto.getImpUid());
            userService.processPayment(paymentReqDto.getImpUid(), paymentReqDto.getUserId());
            return new ResponseEntity<>("결제성공",HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(@RequestBody CancelPayDto cancelPayDto) {
        try {
            IamportResponse<Payment> cancelResponse = userService.cancelPayment(cancelPayDto.getUserId());
            return new ResponseEntity<>("결제 취소 성공: " + cancelResponse.getResponse().getImpUid(),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/myPayList")
    public ResponseEntity<?> myPayList(@RequestBody FindUserDto findUserDto){
        List<MyPayListDto> response = userService.payList(findUserDto.getId());
        return ResponseEntity.ok(response);
    }
}