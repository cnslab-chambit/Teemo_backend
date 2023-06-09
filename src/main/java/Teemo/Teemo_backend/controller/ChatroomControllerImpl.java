package Teemo.Teemo_backend.controller;

import Teemo.Teemo_backend.domain.Chat;
import Teemo.Teemo_backend.domain.dtos.ChatroomCreateRequest;
import Teemo.Teemo_backend.domain.dtos.ChatroomEnterResponse;
import Teemo.Teemo_backend.domain.dtos.ChatroomSearchResponse;
import Teemo.Teemo_backend.error.CustomErrorResponse;
import Teemo.Teemo_backend.error.CustomInvalidValueException;
import Teemo.Teemo_backend.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
@Slf4j
public class ChatroomControllerImpl {
    private final ChatroomService chatroomService;

    /**
     * 채팅방 생성
     *
     * @input   :   memberId, tagId, latitude, longitude
     * @output  :   chatroomId,nickname,age,gender,title
     */
    @PostMapping("/create")
    public ResponseEntity<ChatroomSearchResponse> createChatroom(@RequestBody ChatroomCreateRequest request){
        log.info("채팅방 생성");
        ChatroomSearchResponse response = null;
        try {
            response = chatroomService.create(request);
        }catch (CustomInvalidValueException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse(e.getField(), e.getMessage());log.info("비정상 응답");
            return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
        }log.info("정상 응답");
        return ResponseEntity.ok(response);
    }

    /**
     * 참여 가능한 채팅방 검색
     *
     * @input   :   memberId,tagId
     * @output  :   [ {chatroomId,nickname,age,gender,title},....,{chatroomId,nickname,age,gender,title} ]
     */
    @GetMapping("/search")
    public ResponseEntity<List<ChatroomSearchResponse>> searchChatroom(
            @RequestParam Long memberId,
            @RequestParam Long tagId
    ){
        log.info("참여 가능한 채팅방 검색");
        List<ChatroomSearchResponse> responses = null;
        try {
            responses = chatroomService.search(memberId, tagId);
        }catch (CustomInvalidValueException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse(e.getField(), e.getMessage());log.info("비정상 응답");
            return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
        }log.info("정상 응답");
        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 채팅방 입장 ( = 채팅 내역 불러오기 )
     *
     * @input   : chatroomId
     * @output  : [{chatId, nickname, msg} , ... , {chatId, nickname, msg}]
     */
    @GetMapping("/enter/{chatroomId}")
    public ResponseEntity<List<ChatroomEnterResponse>> enterChatroom(@PathVariable Long chatroomId){
        log.info("특정 채팅방 입장");
        List<Chat> chats = null;
        try {
            chats =chatroomService.load(chatroomId);
        }catch (CustomInvalidValueException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse(e.getField(), e.getMessage());log.info("비정상 응답");
            return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
        }
        List<ChatroomEnterResponse> responses = new ArrayList<>();
        for (Chat chat : chats) {
            responses.add(new ChatroomEnterResponse(chat.getId(),chat.getMsg(),chat.getSender()));
        }log.info("정상 응답");
        return ResponseEntity.ok(responses);
    }

    /**
     * 채팅방 삭제
     *
     * @input   : chatroomId
     * @output  : HttpStatus.NO_CONTENT
     */
    @DeleteMapping("/delete/{memberId}/{chatroomId}")
    public ResponseEntity deleteChatroom(
            @PathVariable Long memberId,
            @PathVariable Long chatroomId
    )
    {
        log.info("채팅방 삭제");
        try {
            chatroomService.remove(memberId, chatroomId);
        }catch (CustomInvalidValueException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse(e.getField(), e.getMessage());
            log.info("비정상 응답");
            return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
        }log.info("정상 응답");
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
