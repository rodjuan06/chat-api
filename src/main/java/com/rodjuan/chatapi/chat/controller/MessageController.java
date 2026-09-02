package com.rodjuan.chatapi.chat.controller;

import com.rodjuan.chatapi.chat.model.Message;
import com.rodjuan.chatapi.chat.service.MessageService;
import com.rodjuan.chatapi.user.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(
        name = "Messages",
        description = "Message operations inside chats"
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chats")
public class MessageController {

    private final MessageService messageService;

    @Operation(
            summary = "List chat messages",
            description = "Returns the messages when the authenticated user belongs to the chat"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Messages returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Chat not found or unavailable to the user")
    })
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Message>> getAllMessage(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable ObjectId chatId) {
        return ResponseEntity.ok(messageService.findAllByChat(chatId, user.getId()));
    }

    @Operation(
            summary = "Send a message",
            description = "Sends a message using the authenticated user as the sender"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid message data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Chat not found or unavailable to the user")
    })
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<Message> sendMessage(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable ObjectId chatId,
                                               @Valid @RequestBody Message message) {
        Message savedMessage = messageService.save(chatId, message, user.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedMessage.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedMessage);
    }

    @Operation(
            summary = "Edit a message",
            description = "Updates a message owned by the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid message data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "The user is not the message author"),
            @ApiResponse(responseCode = "404", description = "Chat or message not found")
    })
    @PutMapping("/{chatId}/messages/{id}")
    public ResponseEntity<Message> editMessage(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable ObjectId chatId,
                                               @PathVariable ObjectId id, @Valid @RequestBody Message message) {
        return ResponseEntity.ok(messageService.update(chatId, id, message, user.getId()));
    }

    @Operation(
            summary = "Delete a message",
            description = "Deletes a message owned by the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Message deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "The user is not the message author"),
            @ApiResponse(responseCode = "404", description = "Chat or message not found")
    })
    @DeleteMapping("/{chatId}/messages/{id}")
    public ResponseEntity<Void> deleteMessageById(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable ObjectId chatId,
                                                     @PathVariable ObjectId id) {
        messageService.delete(chatId, id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
