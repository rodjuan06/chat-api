package com.rodjuan.chatapi.chat.service;

import com.rodjuan.chatapi.chat.repository.MessageRepository;
import com.rodjuan.chatapi.exception.ChatNotFoundException;
import com.rodjuan.chatapi.chat.model.Chat;
import com.rodjuan.chatapi.chat.repository.ChatRepository;
import com.rodjuan.chatapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public List<Chat> getAllChats(long memberId) {
        return chatRepository.findAllByMemberIdsContaining(memberId);
    }

    public Chat getChatById(ObjectId id, long memberId) {
        return chatRepository.findByIdAndMemberIdsContaining(id, memberId).orElseThrow(() -> new ChatNotFoundException(id));
    }

    public Chat createChat(Chat chat, long creatorId) {
        Set<Long> memberIds = new LinkedHashSet<>();

        if (chat.getMemberIds() != null) {
            memberIds.addAll(chat.getMemberIds());
        }

        memberIds.add(creatorId);

        long existingUsers = userRepository.countByIdIn(memberIds);

        if (existingUsers != memberIds.size()) {
            throw new IllegalArgumentException("One or more chat members do not exist");
        }

        chat.setId(null);
        chat.setMemberIds(new ArrayList<>(memberIds));
        chat.setCreatedAt(LocalDateTime.now());

        return chatRepository.save(chat);
    }

    public Chat updateChat(ObjectId id, Chat chat, long memberId) {
        Chat existingChat = getChatById(id, memberId);
        existingChat.setName(chat.getName());
        return chatRepository.save(existingChat);
    }

    public void deleteChatById(ObjectId id, long memberId) {
        Chat existingChat = getChatById(id, memberId);

        messageRepository.deleteAllByChatId(id);
        chatRepository.delete(existingChat);
    }
}
