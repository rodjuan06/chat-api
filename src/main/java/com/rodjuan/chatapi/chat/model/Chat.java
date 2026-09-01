package com.rodjuan.chatapi.chat.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "chats")
public class Chat {

    @Id
    private ObjectId id;
    @NotBlank(message = "Chat name is required")
    private String name;
    private List<Long> memberIds;
    private LocalDateTime createdAt;
}
