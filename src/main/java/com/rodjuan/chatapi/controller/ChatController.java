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

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat) {
        return ResponseEntity.ok(chatService.createChat(chat));
    }
}
