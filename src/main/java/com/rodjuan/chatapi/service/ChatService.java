package com.rodjuan.chatapi.service;

import com.rodjuan.chatapi.model.Chat;
import com.rodjuan.chatapi.repository.ChatRepository;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    public Chat getChatById(ObjectId id) {
        return chatRepository.findById(id).orElse(null);
    }

    public Chat createChat(Chat chat) {
        chat.setCreatedAt(LocalDateTime.now());
        return chatRepository.save(chat);
    }

    public Chat updateChat(Chat chat) {
        if  (chatRepository.findById(chat.getId()).isPresent()) {
            return chatRepository.save(chat);
        }

        return null;
    }

    public void deleteChatById(ObjectId id) {
        chatRepository.deleteById(id);
    }
}
