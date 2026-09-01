package com.rodjuan.chatapi.chat.controller;

import com.rodjuan.chatapi.chat.model.Message;
import com.rodjuan.chatapi.chat.service.MessageService;
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
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Message>> getAllMessage(@AuthenticationPrincipal User user, @PathVariable ObjectId chatId) {
        return ResponseEntity.ok(messageService.findAllByChat(chatId, user.getId()));
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<Message> sendMessage(@AuthenticationPrincipal User user, @PathVariable ObjectId chatId,
                                               @Valid @RequestBody Message message) {
        Message savedMessage = messageService.save(chatId, message, user.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedMessage.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedMessage);
    }

    @PutMapping("/{chatId}/messages/{id}")
    public ResponseEntity<Message> editMessage(@AuthenticationPrincipal User user, @PathVariable ObjectId chatId,
                                               @PathVariable ObjectId id, @Valid @RequestBody Message message) {
        return ResponseEntity.ok(messageService.update(chatId, id, message, user.getId()));
    }

    @DeleteMapping("/{chatId}/messages/{id}")
    public ResponseEntity<Void> deleteMessageById(@AuthenticationPrincipal User user, @PathVariable ObjectId chatId,
                                                     @PathVariable ObjectId id) {
        messageService.delete(chatId, id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
