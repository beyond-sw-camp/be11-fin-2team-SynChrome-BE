package com.Synchrome.collabcontent.livechat.controller;

import java.util.Map;


import com.Synchrome.collabcontent.livechat.domain.Participants;
import com.Synchrome.collabcontent.livechat.dtos.*;
import com.Synchrome.collabcontent.livechat.service.LiveChatService;
import io.openvidu.java.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/save")
    public ResponseEntity<?> saveSession(@RequestBody SessionCreateDto dto){
        liveChatService.save(dto);
        String response = "ok";
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteSession(@RequestBody SessionDeleteDto sessionDeleteDto){
        Boolean response = liveChatService.delete(sessionDeleteDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/addParticipant")
    public ResponseEntity<?> addParticipant(@RequestBody ParticipantAddDto participantAddDto){
        Participants participants = liveChatService.participants(participantAddDto);
        Long response = participants.getId();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/getSessionId")
    public ResponseEntity<?> findSessionId(@RequestBody FindSessionIdDto dto){
        SessionIdResDto response = liveChatService.findSession(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("deleteParticipant")
    public ResponseEntity<?> deleteParticipant(@RequestBody ParticipantDeleteDto dto){
        Long response = liveChatService.deleteParticipant(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("isJoined/{sessionId}/{userId}")
    public ResponseEntity<?> isJoined(@PathVariable String sessionId,@PathVariable Long userId){
        boolean response = liveChatService.isUserJoined(sessionId,userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
