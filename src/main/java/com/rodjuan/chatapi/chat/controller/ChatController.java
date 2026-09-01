package com.rodjuan.chatapi.chat.controller;

import com.rodjuan.chatapi.chat.model.Chat;
import com.rodjuan.chatapi.chat.service.ChatService;
import com.rodjuan.chatapi.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chats")
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<Chat>> getAllChats(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatService.getAllChats(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChatById(@AuthenticationPrincipal User user, @PathVariable ObjectId id) {
        return ResponseEntity.ok(chatService.getChatById(id, user.getId()));
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(@AuthenticationPrincipal User user, @Valid @RequestBody Chat chat) {
        Chat createdChat = chatService.createChat(chat, user.getId());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdChat.getId()).toUri();
        return ResponseEntity.created(location).body(createdChat);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chat> updateChat(@AuthenticationPrincipal User user, @PathVariable ObjectId id, @Valid @RequestBody Chat chat) {
        return ResponseEntity.ok(chatService.updateChat(id, chat, user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Chat> deleteChatById(@AuthenticationPrincipal User user, @PathVariable ObjectId id) {
        chatService.deleteChatById(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
