package com.rodjuan.chatapi.chat.repository;

import com.rodjuan.chatapi.chat.model.Chat;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, ObjectId> {

    List<Chat> findAllByMemberIdsContaining(long memberId);

    Optional<Chat> findByIdAndMemberIdsContaining(ObjectId id, long memberId);

    boolean existsByIdAndMemberIdsContaining(ObjectId id, long memberId);
}
