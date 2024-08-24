package com.moodmate.moodmatebe.domain.chat.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatMessageResponseDto {
//    private ChatUserDto user;
    private ChatPageableDto pageable;
    private List<ChatMysqlMessageDto> chatList;

    public ChatMessageResponseDto(ChatPageableDto chatPageableDto, List<ChatMysqlMessageDto> chatList){
        this.pageable = chatPageableDto;
        this.chatList = chatList;
    }
}
