package com.rodjuan.chatapi.controller;

import com.rodjuan.chatapi.model.Chat;
import com.rodjuan.chatapi.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/chats")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public ResponseEntity<List<Chat>> getAllChats() {
        return ResponseEntity.ok(chatService.getAllChats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChatById(@PathVariable String id) {
        return ResponseEntity.ok(chatService.getChatById(id));
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat) {
        return ResponseEntity.ok(chatService.createChat(chat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chat> updateChat(@RequestBody Chat chat) {
        return ResponseEntity.ok(chatService.updateChat(chat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Chat> deleteChatById(@PathVariable String id) {
        chatService.deleteChatById(id);
        return ResponseEntity.ok().build();
    }
}
