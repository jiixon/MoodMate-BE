package com.moodmate.moodmatebe.domain.chat.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatResponseDto {
    private ChatPageableDto pageable;
    private List<MessageDto> chatList;

    public ChatResponseDto(ChatPageableDto chatPageableDto, List<MessageDto> chatList){
        this.pageable = chatPageableDto;
        this.chatList = chatList;
    }
}
