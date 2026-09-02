package com.rodjuan.chatapi.chat.mapper;

import com.rodjuan.chatapi.chat.dto.MessageCreateRequest;
import com.rodjuan.chatapi.chat.dto.MessageResponse;
import com.rodjuan.chatapi.chat.dto.MessageUpdateRequest;
import com.rodjuan.chatapi.chat.model.Message;
import org.bson.types.Binary;

public final class MessageMapper {

    private MessageMapper() {
    }

    public static Message toEntity(MessageCreateRequest request) {
        return toEntity(request.text(), request.image());
    }

    public static Message toEntity(MessageUpdateRequest request) {
        return toEntity(request.text(), request.image());
    }

    public static MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId() == null ? null : message.getId().toHexString(),
                message.getChatId() == null ? null : message.getChatId().toHexString(),
                message.getSenderId(),
                message.getText(),
                message.getImage() == null ? null : message.getImage().getData(),
                message.getDate()
        );
    }

    private static Message toEntity(String text, byte[] image) {
        Message message = new Message();
        message.setText(text);
        message.setImage(image == null ? null : new Binary(image));
        return message;
    }
}
