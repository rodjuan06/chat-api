package com.rodjuan.chatapi.chat.repository;

import com.rodjuan.chatapi.chat.model.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, ObjectId> {

    List<Message> findByChatId(ObjectId chatId);

    Optional<Message> findByIdAndChatId(ObjectId id, ObjectId chatId);

    void deleteAllByChatId(ObjectId chatId);
}
