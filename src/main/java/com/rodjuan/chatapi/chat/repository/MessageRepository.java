package com.rodjuan.chatapi.chat.repository;

import com.rodjuan.chatapi.chat.model.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, ObjectId> {
    List<Message> findByChatId(ObjectId chatId);
    List<Message> findByTextLike(String message);
}
