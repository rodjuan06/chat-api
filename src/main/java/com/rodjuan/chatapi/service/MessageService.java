package com.rodjuan.chatapi.service;

import com.rodjuan.chatapi.model.Chat;
import com.rodjuan.chatapi.model.Message;
import com.rodjuan.chatapi.repository.MessageRepository;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MongoTemplate mongoTemplate;

    public List<Message> findAll() {
        return messageRepository.findAll();
    }

//    Ignore for now.
//    public List<Message> findAllByChat(ObjectId chatId) {
//        Chat chat = mongoTemplate.findById(chatId, Chat.class);
//    }

    public Optional<Message> findById(ObjectId id) {
        return messageRepository.findById(id);
    }

    @Transactional
    public Message save(ObjectId chatId, Message message) {
        try {
            message.setDate(LocalDateTime.now());
            messageRepository.insert(message);
            mongoTemplate.update(Chat.class).matching(Criteria.where("_id").is(chatId)).apply(new Update().push("messages", message)).first();
            return message;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Message update(ObjectId chatId, Message message) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(chatId)), Chat.class) != null ?
                messageRepository.save(message): message;
    }

    @Transactional
    public void delete(ObjectId chatId, ObjectId id) {
        try {
            messageRepository.deleteById(id);
            mongoTemplate.update(Chat.class).matching(Criteria.where("_id").is(chatId)).apply(new Update().pull("messages", id)).first();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}