package com.rodjuan.chatapi.controller;

import com.rodjuan.chatapi.model.Message;
import com.rodjuan.chatapi.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chats")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Message>> getAllMessages(@PathVariable ObjectId chatId) {
        return ResponseEntity.ok(messageService.findAllByChat(chatId));
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<Message> sendMessage(@PathVariable ObjectId chatId, @RequestBody Message message) {
        return ResponseEntity.ok(messageService.save(chatId, message));
    }

    @PutMapping("/{chatId}/messages/{id}")
    public ResponseEntity<Message> editMessage(@PathVariable ObjectId chatId, @PathVariable ObjectId id, @RequestBody Message message) {
        return ResponseEntity.ok(messageService.update(chatId, id, message));
    }

    @DeleteMapping("/{chatId}/messages/{id}")
    public ResponseEntity<Message> deleteMessageById(@PathVariable ObjectId chatId, @PathVariable ObjectId id) {
        messageService.delete(chatId, id);
        return ResponseEntity.noContent().build();
    }
}
