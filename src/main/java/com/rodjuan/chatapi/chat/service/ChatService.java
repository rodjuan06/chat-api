package com.rodjuan.chatapi.chat.service;

import com.rodjuan.chatapi.exception.ChatNotFoundException;
import com.rodjuan.chatapi.chat.model.Chat;
import com.rodjuan.chatapi.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    public Chat getChatById(ObjectId id) {
        return chatRepository.findById(id).orElseThrow(() -> new ChatNotFoundException(id));
    }

    public Chat createChat(Chat chat) {
        chat.setId(null);
        chat.setCreatedAt(LocalDateTime.now());
        return chatRepository.save(chat);
    }

    public Chat updateChat(ObjectId id, Chat chat) {
        Chat existingChat = getChatById(id);
        existingChat.setName(chat.getName());
        return chatRepository.save(existingChat);
    }

    public void deleteChatById(ObjectId id) {
        Chat existingChat = getChatById(id);
        chatRepository.delete(existingChat);
    }
}

