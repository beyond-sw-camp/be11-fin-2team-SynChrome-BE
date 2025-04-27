package com.Synchrome.user.User.Service;

import com.Synchrome.user.Common.config.S3Uploader;
import com.Synchrome.user.User.Domain.Enum.Paystatus;
import com.Synchrome.user.User.Domain.Pay;
import com.Synchrome.user.User.Domain.User;
import com.Synchrome.user.User.Dto.*;
import com.Synchrome.user.User.Repository.PaymentRepository;
import com.Synchrome.user.User.Repository.UserRepository;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
    private final IamportClient iamportClient;
    private final PaymentRepository paymentRepository;
    private final S3Uploader s3Uploader;

    @Value("${oauth.google.client-id}")
    private String googleClientId;
    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;
    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;

    public UserService(UserRepository userRepository, @Qualifier("userInfoDB") RedisTemplate<String, Object> redisTemplate, IamportClient iamportClient, PaymentRepository paymentRepository, S3Uploader s3Uploader) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.iamportClient = iamportClient;
        this.paymentRepository = paymentRepository;
        this.s3Uploader = s3Uploader;
    }

    public User save(UserSaveReqDto userSaveReqDto){
        User user = User.builder().profile(userSaveReqDto.getProfile()).name(userSaveReqDto.getName()).email(userSaveReqDto.getEmail()).build();
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
        UserInfoDto userInfoDto = UserInfoDto.builder().profile(loginuser.getProfile()).id(loginuser.getId()).name(loginuser.getName()).email(loginuser.getEmail()).subscribe(loginuser.getSubscribe()).build();
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

    private String extractImpUid(String impUidJson) {
        try {
            // JSON 파싱을 통해 impUid 값 추출 (예: Jackson 사용)
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> map = objectMapper.readValue(impUidJson, Map.class);
            return map.get("impUid");
        } catch (Exception e) {
            throw new RuntimeException("impUid 값을 추출하는 중 오류 발생", e);
        }
    }

    public void deleteUserInfo(Long id){
        String redisKey = String.valueOf(id);
        redisTemplate.delete(redisKey);
    }

    public void processPayment(String impUid,Long userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(()->new EntityNotFoundException("없는 유저"));  // 현재 로그인한 사용자 정보

        int fee = 100;

        String actualImpUid = extractImpUid(impUid);  // impUid 값 추출 함수 사용

        // impUid를 통해 결제 정보 확인
        IamportResponse<Payment> paymentResponse = iamportClient.paymentByImpUid(actualImpUid);

        if (paymentResponse.getResponse() == null) {
            throw new Exception("결제 정보 없음: " + paymentResponse.getMessage());
        }

        BigDecimal amount = paymentResponse.getResponse().getAmount();
        if (amount.compareTo(BigDecimal.valueOf(fee)) != 0) {
            throw new Exception("결제 금액 불일치");
        }

        try {
            // 결제 정보가 존재하는지 확인
            if (paymentResponse.getResponse() == null) {
                throw new Exception("결제 요청 실패: " + paymentResponse.getMessage());
            }

            // 결제 금액 및 기타 검증 로직
            if (paymentResponse.getResponse().getAmount().intValue() != fee) {
                throw new Exception("결제 금액 불일치");
            }
            Pay pay = Pay.builder().user(user).amount(BigDecimal.valueOf(fee)).impUid(actualImpUid).build();
            paymentRepository.save(pay);
            user.subscribe();
            deleteUserInfo(user.getId());
            userInfoCaching(user);

        } catch (Exception e) {
            throw new RuntimeException("결제중 오류", e);
        }
    }

    public IamportResponse<Payment> cancelPayment(Long userId) throws Exception {
        Pay myPay = paymentRepository.findByUserAndPaystatus(userRepository.findById(userId).orElseThrow(()->new EntityNotFoundException("없는유저")), Paystatus.PAY).orElseThrow(()->new EntityNotFoundException(("결제 안한 회원입니다.")));
        String actualImpUid = myPay.getImpUid();// impUid 값 추출 함수 사용

        // DB에서 결제 정보를 조회
        Pay pay = paymentRepository.findByImpUid(actualImpUid);
        User user = pay.getUser();
        if (pay == null) {
            throw new Exception("해당 impUid에 대한 결제 정보가 없습니다.");
        }

        // 아임포트 서버에서 결제 정보를 조회
        IamportResponse<Payment> paymentResponse = iamportClient.paymentByImpUid(actualImpUid);

        // 아임포트와 DB 저장 금액이 일치하는지 확인하는 로직
        if (paymentResponse.getResponse() != null) {
            BigDecimal iamportAmount = paymentResponse.getResponse().getAmount();
            if (pay.getAmount().compareTo(iamportAmount) != 0) {
                throw new Exception("DB에 저장된 결제 금액과 아임포트에서 확인된 금액이 일치하지 않습니다.");
            }
        } else {
            throw new Exception("아임포트에서 해당 결제 정보를 찾을 수 없습니다.");
        }

        // 결제 취소 요청(impuid 또는 merchantUid를 통해 취소할 수 있음
        CancelData cancelData = new CancelData(actualImpUid, true);
        IamportResponse<Payment> cancelResponse = iamportClient.cancelPaymentByImpUid(cancelData);


        if (cancelResponse.getResponse() != null) {
            log.info("결제 취소 성공: {}", cancelResponse.getResponse());

            // 결제 상태를 CANCEL !
            pay.cancelPaymentStatus();
            user.cancelSubscribe();
            String redisKey = String.valueOf(user.getId());
            redisTemplate.delete(redisKey);
            userInfoCaching(user);

        } else {
            log.error("결제 취소 실패: {}", cancelResponse.getMessage());
            throw new Exception("결제 취소 실패: " + cancelResponse.getMessage());
        }
        return cancelResponse;
    }


    public List<MyPayListDto> payList(Long userId) {
        Optional<List<Pay>> optionalPays = paymentRepository.findByUserId(userId);

        if (optionalPays.isEmpty() || optionalPays.get().isEmpty()) {
            // 결제 내역이 없으면 빈 리스트 반환
            return new ArrayList<>();
        }

        List<Pay> payList = optionalPays.get();

        return payList.stream()
                .map(pay -> MyPayListDto.builder()
                        .paystatus(pay.getPaystatus())
                        .createdTime(pay.getCreatedTime())
                        .updatedTime(pay.getUpdatedTime())
                        .build())
                .toList();
    }

    public Long newProfile(UpdateProfileDto dto) throws IOException {
        User user = userRepository.findById(dto.getUserId()).orElseThrow(()-> new EntityNotFoundException("없는 유저"));
        MultipartFile profileImage = dto.getNewProfile();

        if (profileImage != null && !profileImage.isEmpty()) {
            // ✅ S3에 파일 업로드
            String profileUrl = s3Uploader.uploadFile(profileImage);

            // ✅ 업로드한 URL로 user의 profile 업데이트
            user.updateMyProfile(profileUrl);
        }
        String redisKey = String.valueOf(user.getId());
        redisTemplate.delete(redisKey);

        // ✅ Redis에 새로운 유저 정보 다시 저장
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profile(user.getProfile())
                .subscribe(user.getSubscribe())
                .build();

        try {
            String userInfoJson = objectMapper.writeValueAsString(userInfoDto);
            redisTemplate.opsForValue().set(redisKey, userInfoJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 저장 중 오류 발생", e);
        }

        return user.getId();
    }

}
