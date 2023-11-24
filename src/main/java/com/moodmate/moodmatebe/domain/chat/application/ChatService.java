package com.moodmate.moodmatebe.domain.chat.application;

import com.moodmate.moodmatebe.domain.chat.domain.ChatMessage;
import com.moodmate.moodmatebe.domain.chat.domain.ChatRoom;
import com.moodmate.moodmatebe.domain.chat.dto.RedisChatMessageDto;
import com.moodmate.moodmatebe.domain.chat.exception.ChatRoomNotFoundException;
import com.moodmate.moodmatebe.domain.chat.repository.MessageRepository;
import com.moodmate.moodmatebe.domain.chat.repository.RoomRepository;
import com.moodmate.moodmatebe.domain.user.domain.User;
import com.moodmate.moodmatebe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final RedisTemplate<String, RedisChatMessageDto> chatRedistemplate;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public void saveMessage(RedisChatMessageDto chatMessageDto){
        Optional<ChatRoom> roomId = roomRepository.findByRoomId(chatMessageDto.getRoomId());
        Optional<User> userId = userRepository.findById(chatMessageDto.getUserId());
        if (roomId.isPresent()) {
            ChatRoom chatRoom = roomId.get();
            ChatMessage chatMessage = new ChatMessage(chatRoom, userId.get(), true, chatMessageDto.getContent(), LocalDateTime.now());
            messageRepository.save(chatMessage);
            chatRedistemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
            chatRedistemplate.opsForList().rightPush(chatMessageDto.getRoomId().toString(), chatMessageDto);
        } else {
            throw new ChatRoomNotFoundException();
        }
    }
}