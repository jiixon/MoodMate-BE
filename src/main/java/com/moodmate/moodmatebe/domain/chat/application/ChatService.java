package com.moodmate.moodmatebe.domain.chat.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moodmate.moodmatebe.domain.chat.domain.ChatMessage;
import com.moodmate.moodmatebe.domain.chat.domain.ChatRoom;
import com.moodmate.moodmatebe.domain.chat.dto.ChatPageableDto;
import com.moodmate.moodmatebe.domain.chat.dto.ChatUserDto;
import com.moodmate.moodmatebe.domain.chat.dto.MessageDto;
import com.moodmate.moodmatebe.domain.chat.dto.RedisChatMessageDto;
import com.moodmate.moodmatebe.domain.chat.exception.ChatRoomNotFoundException;
import com.moodmate.moodmatebe.domain.user.exception.UserNotFoundException;
import com.moodmate.moodmatebe.domain.chat.redis.RedisMessageIdGenerator;
import com.moodmate.moodmatebe.domain.chat.repository.MessageRepository;
import com.moodmate.moodmatebe.domain.chat.repository.RoomRepository;
import com.moodmate.moodmatebe.domain.user.domain.User;
import com.moodmate.moodmatebe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final RedisTemplate<String, RedisChatMessageDto> chatRedistemplate;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final RedisMessageIdGenerator redisMessageIdGenerator;
    private final MessageDtoConverter messageDtoConverter;

    @Transactional
    public void saveMessage(RedisChatMessageDto chatMessageDto) {
        ChatRoom chatRoom = getChatRoom(chatMessageDto.getRoomId());
        User user = getUser(chatMessageDto.getUserId());
        ChatMessage chatMessage = new ChatMessage(chatRoom, user, true, chatMessageDto.getContent(), LocalDateTime.now());
        messageRepository.save(chatMessage);
        Long messageId = redisMessageIdGenerator.generateUniqueId(chatMessageDto.getRoomId().toString());
        chatMessageDto.setMessageId(messageId);
        chatRedistemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        chatRedistemplate.opsForList().rightPush(chatMessageDto.getRoomId().toString(), chatMessageDto);
    }

    public List<MessageDto> getMessage(Long roomId, int size, int page) throws JsonProcessingException {
        List<MessageDto> messageList = new ArrayList<>();
        List<RedisChatMessageDto> redisMessageList = getRedisMessages(roomId, size, page);
        if (redisMessageList == null || redisMessageList.isEmpty()) {
            List<ChatMessage> dbMessageList = getDbMessages(roomId, size, page);
            for (ChatMessage message : dbMessageList) {
                MessageDto messageDto = messageDtoConverter.fromChatMessage(message);
                messageList.add(messageDto);
            }
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            for (int i = 0; i < redisMessageList.size(); i++) {
                RedisChatMessageDto chatMessageDto = objectMapper.readValue(objectMapper.writeValueAsString(redisMessageList.get(i)), RedisChatMessageDto.class);
                MessageDto messageDto = messageDtoConverter.fromRedisChatMessageDto(chatMessageDto);
                messageList.add(messageDto);
            }
        }
        return messageList;
    }
    public ChatPageableDto getPageable(Long roomId, int size, int page){
        ChatRoom room = getChatRoom(roomId);
        int totalElements = messageRepository.countByRoom(room);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        ChatPageableDto chatPageableDto = new ChatPageableDto(size,page,totalPages,totalElements);
        return chatPageableDto;
    }
    public ChatUserDto getUserInfo(Long userId){
        User user = getUser(userId);
        ChatUserDto chatUserDto = new ChatUserDto(user.getUserGender(),"testNickname");
        return chatUserDto;
    }

    private List<RedisChatMessageDto> getRedisMessages(Long roomId, int size, int page) {
        int start = (page - 1) * size;
        int end = start + size - 1;
        return chatRedistemplate.opsForList().range(roomId.toString(), start, end);
    }
    private List<ChatMessage> getDbMessages(Long roomId, int size, int page) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        ChatRoom chatRoom = getChatRoom(roomId);
        Page<ChatMessage> byRoomIdOrderByCreatedAt = messageRepository.findByRoomOrderByCreatedAt(chatRoom, pageable);
        return byRoomIdOrderByCreatedAt.getContent();
    }
    private ChatRoom getChatRoom(Long roomId) {
        Optional<ChatRoom> byRoomId = roomRepository.findByRoomId(roomId);
        if (byRoomId.isPresent()) {
            return byRoomId.get();
        } else {
            throw new ChatRoomNotFoundException();
        }
    }
    private User getUser(Long userId) {
        Optional<User> byId = userRepository.findById(userId);
        if(byId.isPresent()){
            return byId.get();
        }else {
            throw new UserNotFoundException();
        }
    }
}