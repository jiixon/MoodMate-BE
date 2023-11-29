package com.moodmate.moodmatebe.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageDto {
    private Long userId;
    private Long roomId;
    private String content;
}