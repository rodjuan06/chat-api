package com.rodjuan.chatapi.exception;

import org.bson.types.ObjectId;

public class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(ObjectId id) {
        super("Could not find message with id " + id);
    }
}
