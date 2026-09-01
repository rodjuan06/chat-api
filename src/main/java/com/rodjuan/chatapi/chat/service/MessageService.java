package com.rodjuan.chatapi.chat.service;

import com.rodjuan.chatapi.exception.ChatNotFoundException;
import com.rodjuan.chatapi.exception.MessageNotFoundException;
import com.rodjuan.chatapi.chat.model.Message;
import com.rodjuan.chatapi.chat.repository.ChatRepository;
import com.rodjuan.chatapi.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    public List<Message> findAllByChat(ObjectId chatId, long userId) {
        ensureChatExists(chatId, userId);
        return messageRepository.findByChatId(chatId);
    }

    public Message save(ObjectId chatId, Message message, long userId) {
        ensureChatExists(chatId, userId);
        message.setId(null);
        message.setSenderId(userId);
        message.setChatId(chatId);
        message.setDate(LocalDateTime.now());
        return messageRepository.insert(message);
    }

    public Message update(ObjectId chatId, ObjectId id, Message message, long userId) {
        ensureChatExists(chatId, userId);

        Message existingMessage = findInChat(chatId, id);

        if (existingMessage.getSenderId() != userId) {
            throw new AccessDeniedException("You cannot edit this message");
        }

        existingMessage.setText(message.getText());
        existingMessage.setImage(message.getImage());

        return messageRepository.save(existingMessage);
    }

    public void delete(ObjectId chatId, ObjectId id, long userId) {
        ensureChatExists(chatId, userId);

        Message existingMessage = findInChat(chatId, id);

        if (existingMessage.getSenderId() != userId) {
            throw new AccessDeniedException("You cannot delete this message");
        }

        messageRepository.delete(existingMessage);
    }

    private Message findInChat(ObjectId chatId, ObjectId id) {

        return messageRepository.findByIdAndChatId(id, chatId).orElseThrow(() -> new MessageNotFoundException(id));
    }

    private void ensureChatExists(ObjectId chatId, long userId) {
        if (!chatRepository.existsByIdAndMemberIdsContaining(chatId, userId)) {
            throw new ChatNotFoundException(chatId);
        }
    }
}