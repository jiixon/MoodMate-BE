package com.moodmate.moodmatebe.domain.chat.api;

import com.moodmate.moodmatebe.domain.chat.application.ChatRoomService;
import com.moodmate.moodmatebe.domain.chat.application.ChatService;
import com.moodmate.moodmatebe.domain.chat.dto.request.ChatMessageDto;
import com.moodmate.moodmatebe.domain.chat.dto.response.ChatMessageResponseDto;
import com.moodmate.moodmatebe.domain.chat.dto.response.ChatResponseDto;
import com.moodmate.moodmatebe.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;
    private final ChatRoomService chatRoomService;

    @Operation(summary = "실시간 채팅", description = "실시간으로 채팅 메시지를 보냅니다.")
    @MessageMapping("/chat")
    public void handleChatMessage(ChatMessageDto messageDto) {
        chatService.handleMessage(messageDto);
    }

    @Operation(summary = "채팅내역 조회", description = "채팅내역을 조회합니다.")
    @GetMapping("/chat")
    ResponseEntity<ChatResponseDto> getMessage(@RequestParam Long roomId,
                                               @RequestParam int size, @RequestParam int page){
        return ResponseEntity.ok(chatService.getMessage(roomId, size, page));
    }

    @Operation(summary = "채팅 종료", description = "채팅을 종료합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/chat")
    ResponseEntity<Void> closeChatRoom(@RequestHeader("Authorization") String authorizationHeader) {
        chatRoomService.exitChatRoom(authorizationHeader);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅내역 조회(MYSQL)", description = "채팅내역을 조회합니다.")
    @GetMapping("/chat/mysql")
    ResponseEntity<ChatMessageResponseDto> getChatMessage(
            @RequestParam Long roomId, @RequestParam int size, @RequestParam int page){
        return ResponseEntity.ok(chatService.getChatMessage(roomId, size, page));

    }
}