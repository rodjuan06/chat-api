package com.rodjuan.chatapi.controller;

import com.rodjuan.chatapi.model.Chat;
import com.rodjuan.chatapi.service.ChatService;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Data
@RestController
@RequestMapping("api/v1/chats")
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<Chat>> getAllChats() {
        return ResponseEntity.ok(chatService.getAllChats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChatById(@PathVariable ObjectId id) {
        return ResponseEntity.ok(chatService.getChatById(id));
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat) {
        Chat createdChat = chatService.createChat(chat);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdChat.getId()).toUri();
        return ResponseEntity.created(location).body(createdChat);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chat> updateChat(@PathVariable ObjectId id, @RequestBody Chat chat) {
        return ResponseEntity.ok(chatService.updateChat(id, chat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Chat> deleteChatById(@PathVariable ObjectId id) {
        chatService.deleteChatById(id);
        return ResponseEntity.noContent().build();
    }
}
