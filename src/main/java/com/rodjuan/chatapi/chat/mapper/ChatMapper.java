package com.rodjuan.chatapi.chat.mapper;

import com.rodjuan.chatapi.chat.dto.ChatCreateRequest;
import com.rodjuan.chatapi.chat.dto.ChatResponse;
import com.rodjuan.chatapi.chat.dto.ChatUpdateRequest;
import com.rodjuan.chatapi.chat.model.Chat;

public final class ChatMapper {

    private ChatMapper() {
    }

    public static Chat toEntity(ChatCreateRequest request) {
        Chat chat = new Chat();
        chat.setName(request.name());
        chat.setMemberIds(request.memberIds());
        return chat;
    }

    public static Chat toEntity(ChatUpdateRequest request) {
        Chat chat = new Chat();
        chat.setName(request.name());
        return chat;
    }

    public static ChatResponse toResponse(Chat chat) {
        return new ChatResponse(
                chat.getId() == null ? null : chat.getId().toHexString(),
                chat.getName(),
                chat.getMemberIds(),
                chat.getCreatedAt()
        );
    }
}
