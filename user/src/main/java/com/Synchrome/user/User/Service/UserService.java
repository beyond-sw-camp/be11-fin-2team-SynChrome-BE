package com.Synchrome.user.User.Service;

import com.Synchrome.user.User.Domain.User;
import com.Synchrome.user.User.Dto.AccessTokendto;
import com.Synchrome.user.User.Dto.GoogleProfileDto;
import com.Synchrome.user.User.Dto.UserSaveReqDto;
import com.Synchrome.user.User.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Value("${oauth.google.client-id}")
    private String googleClientId;
    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;
    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        System.out.println("서비스 : " + user);
        return user;
    }

}
