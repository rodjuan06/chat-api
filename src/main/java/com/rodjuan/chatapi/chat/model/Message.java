package com.rodjuan.chatapi.chat.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "messages")
public class Message {

    @Id
    private ObjectId id;
    @Indexed
    private ObjectId chatId;
    private long senderId;
    @NotBlank(message = "Message text is required")
    @Size(max = 5000, message = "Message text is too long")
    private String text;
    private Binary image;
    private LocalDateTime date;
}