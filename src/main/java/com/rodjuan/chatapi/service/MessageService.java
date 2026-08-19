package com.rodjuan.chatapi.service;

import com.rodjuan.chatapi.exception.ChatNotFoundException;
import com.rodjuan.chatapi.exception.MessageNotFoundException;
import com.rodjuan.chatapi.model.Chat;
import com.rodjuan.chatapi.model.Message;
import com.rodjuan.chatapi.repository.ChatRepository;
import com.rodjuan.chatapi.repository.MessageRepository;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    public List<Message> findAllByChat(ObjectId chatId) {
        ensureChatExists(chatId);
        return messageRepository.findByChatId(chatId);
    }

    public Optional<Message> findById(ObjectId id) {
        return messageRepository.findById(id);
    }

    public Message save(ObjectId chatId, Message message) {
        ensureChatExists(chatId);
        message.setId(null);
        message.setChatId(chatId);
        message.setDate(LocalDateTime.now());
        return messageRepository.insert(message);
    }

    public Message update(ObjectId chatId, ObjectId id, Message message) {
        Message existingMessage = findInChat(chatId, id);
        existingMessage.setText(message.getText());
        existingMessage.setImage(message.getImage());
        return messageRepository.save(existingMessage);
    }

    public void delete(ObjectId chatId, ObjectId id) {
        Message existingMessage = findInChat(chatId, id);
        messageRepository.delete(existingMessage);
    }

    private Message findInChat(ObjectId chatId, ObjectId id) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException(id));

        if (!message.getChatId().equals(chatId)) {
            throw new MessageNotFoundException(chatId);
        }

        return message;
    }

    private void ensureChatExists(ObjectId chatId) {
        if (!chatRepository.existsById(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
    }
}