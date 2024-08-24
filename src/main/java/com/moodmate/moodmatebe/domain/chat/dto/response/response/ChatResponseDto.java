package com.moodmate.moodmatebe.domain.chat.dto.response.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatResponseDto {
    private com.moodmate.moodmatebe.domain.chat.dto.response.ChatPageableDto pageable;
    private List<com.moodmate.moodmatebe.domain.chat.dto.response.MessageDto> chatList;

    public ChatResponseDto(ChatPageableDto chatPageableDto, List<MessageDto> chatList){
        this.pageable = chatPageableDto;
        this.chatList = chatList;
    }
}
