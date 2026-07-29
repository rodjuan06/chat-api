package com.rodjuan.chatapi.service;

import com.rodjuan.chatapi.model.Chat;
import com.rodjuan.chatapi.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    public Chat getChatById(String id) {
        return chatRepository.findById(id).orElse(null);
    }

    public Chat createChat(Chat chat) {
        return chatRepository.save(chat);
    }

    public Chat updateChat(Chat chat) {
        if  (chatRepository.findById(chat.getId()).isPresent()) {
            return chatRepository.save(chat);
        }

        return null;
    }

    public void deleteChatById(String id) {
        chatRepository.deleteById(id);
    }
}
