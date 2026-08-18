package com.rodjuan.chatapi.exception;

import org.bson.types.ObjectId;

public class ChatNotFoundException extends RuntimeException {

    public ChatNotFoundException(ObjectId id) {
        super("Could not find chat with id " + id);
    }
}
