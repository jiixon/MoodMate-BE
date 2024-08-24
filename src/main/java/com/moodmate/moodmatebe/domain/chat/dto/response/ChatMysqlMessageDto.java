package com.moodmate.moodmatebe.domain.chat.dto.response;

import com.moodmate.moodmatebe.domain.chat.domain.ChatMessage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMysqlMessageDto {
    private Long messageId;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;

    public ChatMysqlMessageDto(ChatMessage chatMessage){
        this.messageId = chatMessage.getMessageId();
        this.content = chatMessage.getContent();
        this.userId = chatMessage.getSender().getUserId();
        this.createdAt = chatMessage.getCreatedAt();
    }
}
