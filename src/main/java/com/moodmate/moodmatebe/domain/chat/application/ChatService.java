package com.moodmate.moodmatebe.domain.chat.application;

import com.moodmate.moodmatebe.domain.chat.domain.ChatMessage;
import com.moodmate.moodmatebe.domain.chat.domain.ChatRoom;
import com.moodmate.moodmatebe.domain.chat.domain.Message;
import com.moodmate.moodmatebe.domain.chat.dto.request.ChatMessageDto;
import com.moodmate.moodmatebe.domain.chat.dto.response.*;
import com.moodmate.moodmatebe.domain.chat.exception.ChatRoomNotFoundException;
import com.moodmate.moodmatebe.domain.chat.redis.RedisPublisher;
import com.moodmate.moodmatebe.domain.chat.repository.ChatMessageRepository;
import com.moodmate.moodmatebe.domain.chat.repository.MessageRepository;
import com.moodmate.moodmatebe.domain.chat.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final RedisPublisher redisPublisher;

    public void handleMessage(ChatMessageDto chatMessageDto) {
        Long roomId = getRoomId(chatMessageDto.getUserId());
        chatRoomService.enterChatRoom(roomId);
        redisPublisher.publish(new ChannelTopic("/sub/chat/" + roomId), chatMessageDto);

        Message message = Message.of(chatMessageDto);
        messageRepository.save(message);
    }

    //mongoDB
    public ChatResponseDto getMessage(Long roomId, int size, int page) {
        //Long validRoomId = chatRoomService.validateRoomIdAuthorization(roomId, authorizationHeader);
        //ChatUserDto user = userService.getChatPartnerInfo(authorizationHeader);

        Page<Message> dbMessages = getDbMessages(roomId, size, page);
        List<MessageDto> messageList = new ArrayList<>();


        for (Message message : dbMessages.getContent()) {
            MessageDto messageDto = new MessageDto(message);
            messageList.add(messageDto);
        }
        ChatPageableDto<Message> chatPageableDto = new ChatPageableDto<>(size, page, dbMessages);

        return new ChatResponseDto(chatPageableDto,messageList);
    }

    //mysql
    public ChatMessageResponseDto getChatMessage(Long roomId, int size, int page) {
        //Long validRoomId = chatRoomService.validateRoomIdAuthorization(roomId, authorizationHeader);
        //ChatUserDto user = userService.getChatPartnerInfo(authorizationHeader);

        Page<ChatMessage> rdbMessage = getRdbMessage(roomId, size, page);
        List<ChatMysqlMessageDto> messageList = new ArrayList<>();


        for (ChatMessage message : rdbMessage.getContent()) {
            ChatMysqlMessageDto messageDto = new ChatMysqlMessageDto(message);
            messageList.add(messageDto);
        }
        ChatPageableDto<ChatMessage> chatPageableDto = new ChatPageableDto<>(size, page, rdbMessage);

        return new ChatMessageResponseDto(chatPageableDto,messageList);
    }

    private Page<Message> getDbMessages(Long roomId, int size, int page) {
        Pageable pageable = PageRequest.of(page - 1, size);
        ChatRoom chatRoom = getChatRoom(roomId);
        return  messageRepository.findByRoomIdOrderByCreatedAtDesc(chatRoom.getRoomId(), pageable);
//        return  messageRepository.findByRoomIdOrderByCreatedAtDesc(1L, pageable);
    }

    private Page<ChatMessage> getRdbMessage(Long roomId, int size, int page){
        Pageable pageable = PageRequest.of(page - 1, size);
        ChatRoom chatRoom = getChatRoom(roomId);
        return  chatMessageRepository.findByRoomOrderByCreatedAt(chatRoom, pageable);
    }


    public ChatRoom getChatRoom(Long roomId) {
        Optional<ChatRoom> byRoomId = roomRepository.findByRoomId(roomId);
        if (byRoomId.isPresent()) {
            return byRoomId.get();
        } else {
            throw new ChatRoomNotFoundException();
        }
    }

    public Long getRoomId(Long userId) {
        Optional<ChatRoom> activeChatRoomByUserId = roomRepository.findActiveChatRoomByUserId(userId);
        return activeChatRoomByUserId.get().getRoomId();
    }
}