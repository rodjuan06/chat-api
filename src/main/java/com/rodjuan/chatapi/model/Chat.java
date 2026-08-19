package com.rodjuan.chatapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "chats")
public class Chat {

    @Id
    private ObjectId id;

    private String name;
    private int members;
    private LocalDateTime createdAt;
}
