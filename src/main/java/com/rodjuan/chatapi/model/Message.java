package com.rodjuan.chatapi.model;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat.messages")
public class Message {

    @Id
    private ObjectId id;
    private String sender;
    private String text;
    private Binary image;
    private LocalDateTime date;

//    Default
    public Message() {}

//    Message with only text
    public Message(String sender, String text, LocalDateTime date) {
        this.sender = sender;
        this.text = text;
        this.date = date;
    }

//    Message only with an image
    public Message(String sender, Binary image, LocalDateTime date) {
        this.sender = sender;
        this.image = image;
        this.date = date;
    }

//    Message with image and text
    public Message(String sender, Binary image, String text, LocalDateTime date) {
        this.sender = sender;
        this.image = image;
        this.text = text;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Binary getImage() {
        return image;
    }

    public void setImage(Binary image) {
        this.image = image;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}