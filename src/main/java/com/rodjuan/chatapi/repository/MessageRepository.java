package com.rodjuan.chatapi.repository;

import com.rodjuan.chatapi.model.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, ObjectId> {}
