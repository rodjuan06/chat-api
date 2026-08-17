package com.rodjuan.chatapi.repository;

import com.rodjuan.chatapi.model.Chat;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends MongoRepository<Chat, ObjectId> {}
