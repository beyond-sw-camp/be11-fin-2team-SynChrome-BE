package com.Synchrome.collabcontent.liveChat.Controller;

import java.util.Map;


import com.Synchrome.collabcontent.liveChat.Dtos.SessionCreateDto;
import com.Synchrome.collabcontent.liveChat.Dtos.SessionDeleteDto;
import com.Synchrome.collabcontent.liveChat.Service.LiveChatService;
import io.openvidu.java.client.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/liveChat")
public class liveChatController {
    private final LiveChatService liveChatService;
    @Value("${openvidu.url}")
    private String OPENVIDU_URL;

    @Value("${openvidu.secret}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;

    public liveChatController(LiveChatService liveChatService) {
        this.liveChatService = liveChatService;
    }

    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    @PostMapping("/sessions")
    public ResponseEntity<String> initializeSession(@RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        System.out.println(params.get("customSessionId"));
        SessionProperties properties = SessionProperties.fromJson(params).build();
        Session session = openvidu.createSession(properties); // session 생성
        SessionCreateDto dto = SessionCreateDto.builder().sessionId(session.getSessionId()).build();
        liveChatService.save(dto);
        System.out.println(session.getSessionId());
        return new ResponseEntity<>(session.getSessionId(), HttpStatus.OK);
    }

    @PostMapping("/sessions/{sessionId}/connections")
    public ResponseEntity<String> createConnection(@PathVariable("sessionId") String sessionId,
                                                   @RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openvidu.getActiveSession(sessionId);
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
        Connection connection = session.createConnection(properties);
        return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteSession(@RequestBody SessionDeleteDto sessionDeleteDto){
        Boolean response = liveChatService.delete(sessionDeleteDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
