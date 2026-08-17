package com.rodjuan.chatapi.model;

import lombok.Data;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "messages")
public class Message {

    @Id
    private ObjectId id;
    private String sender;
    private String text;
    private Binary image;
    private LocalDateTime date;
}