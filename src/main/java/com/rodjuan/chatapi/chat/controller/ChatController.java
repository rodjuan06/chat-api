package com.rodjuan.chatapi.chat.controller;

import com.rodjuan.chatapi.chat.model.Chat;
import com.rodjuan.chatapi.chat.service.ChatService;
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
        name = "Chats",
        description = "Chat creation and management"
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chats")
public class ChatController {
    private final ChatService chatService;

    @Operation(
            summary = "List the user's chats",
            description = "Returns only chats containing the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chats returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    public ResponseEntity<List<Chat>> getAllChats(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(chatService.getAllChats(user.getId()));
    }

    @Operation(
            summary = "Find a chat",
            description = "Returns the chat when the authenticated user is a member"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chat returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Chat not found or unavailable to the user")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChatById(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable ObjectId id) {
        return ResponseEntity.ok(chatService.getChatById(id, user.getId()));
    }

    @Operation(
            summary = "Create a chat",
            description = "Creates a chat and automatically includes the authenticated user as a member"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chat created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid chat data or unknown member"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PostMapping
    public ResponseEntity<Chat> createChat(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody Chat chat) {
        Chat createdChat = chatService.createChat(chat, user.getId());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdChat.getId()).toUri();
        return ResponseEntity.created(location).body(createdChat);
    }

    @Operation(
            summary = "Update a chat",
            description = "Updates a chat accessible to the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chat updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid chat data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Chat not found or unavailable to the user")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Chat> updateChat(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable ObjectId id, @Valid @RequestBody Chat chat) {
        return ResponseEntity.ok(chatService.updateChat(id, chat, user.getId()));
    }

    @Operation(
            summary = "Delete a chat",
            description = "Deletes the chat and all of its messages"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Chat deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Chat not found or unavailable to the user")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Chat> deleteChatById(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable ObjectId id) {
        chatService.deleteChatById(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
