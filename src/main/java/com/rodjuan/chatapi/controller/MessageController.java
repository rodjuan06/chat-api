package com.rodjuan.chatapi.controller;

import com.rodjuan.chatapi.model.Message;
import com.rodjuan.chatapi.service.MessageService;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/chats")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Message>> getAllMessages(@PathVariable ObjectId chatId) {
        return ResponseEntity.ok(messageService.findAllByChat(chatId));
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<Message> sendMessage(@PathVariable ObjectId chatId, @RequestBody Message message) {
        return ResponseEntity.ok(messageService.save(chatId, message));
    }

    @PutMapping("/{chatId}/messages")
    public ResponseEntity<Message> editMessage(@PathVariable ObjectId chatId, @RequestBody Message message) {
        return ResponseEntity.ok(messageService.update(chatId, message));
    }

    @DeleteMapping("/{chatId}/messages/{id}")
    public ResponseEntity<Message> deleteMessage(@PathVariable ObjectId chatId, @PathVariable ObjectId id) {
        messageService.delete(chatId, id);
        return ResponseEntity.ok().build();
    }
}
