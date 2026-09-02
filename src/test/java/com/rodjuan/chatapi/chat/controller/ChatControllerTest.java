package com.rodjuan.chatapi.chat.controller;

import com.rodjuan.chatapi.chat.model.Chat;
import com.rodjuan.chatapi.chat.service.ChatService;
import com.rodjuan.chatapi.exception.ChatNotFoundException;
import com.rodjuan.chatapi.security.JwtService;
import com.rodjuan.chatapi.support.ControllerTestConfiguration;
import com.rodjuan.chatapi.user.service.CustomUserDetailsService;
import com.rodjuan.chatapi.user.model.Role;
import com.rodjuan.chatapi.user.model.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@ContextConfiguration(classes = {ControllerTestConfiguration.class, ChatController.class})
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void returns401WhenAuthenticationIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/chats"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication is required"));

        verifyNoInteractions(chatService);
    }

    @Test
    void listsChatsForAuthenticatedUser() throws Exception {
        Chat chat = new Chat();
        chat.setName("General");
        when(chatService.getAllChats(7L)).thenReturn(List.of(chat));

        mockMvc.perform(get("/api/v1/chats").with(user(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("General"));

        verify(chatService).getAllChats(7L);
    }

    @Test
    void returns400ForInvalidChat() throws Exception {
        mockMvc.perform(post("/api/v1/chats")
                        .with(user(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " ",
                                  "memberIds": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.name").value("Chat name is required"));

        verifyNoInteractions(chatService);
    }

    @Test
    void returns201AndLocationWhenChatIsCreated() throws Exception {
        ObjectId id = new ObjectId();
        Chat createdChat = new Chat();
        createdChat.setId(id);
        createdChat.setName("General");
        when(chatService.createChat(any(Chat.class), eq(7L))).thenReturn(createdChat);

        mockMvc.perform(post("/api/v1/chats")
                        .with(user(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "General",
                                  "memberIds": [8]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "http://localhost/api/v1/chats/" + id.toHexString()
                ))
                .andExpect(jsonPath("$.name").value("General"));
    }

    @Test
    void returns404WhenChatIsNotFound() throws Exception {
        ObjectId id = new ObjectId();
        when(chatService.getChatById(id, 7L)).thenThrow(new ChatNotFoundException(id));

        mockMvc.perform(get("/api/v1/chats/{id}", id.toHexString())
                        .with(user(authenticatedUser())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void returns204WhenChatIsDeleted() throws Exception {
        ObjectId id = new ObjectId();

        mockMvc.perform(delete("/api/v1/chats/{id}", id.toHexString())
                        .with(user(authenticatedUser())))
                .andExpect(status().isNoContent());

        verify(chatService).deleteChatById(id, 7L);
    }

    private User authenticatedUser() {
        return User.builder()
                .id(7L)
                .name("Rodjuan")
                .email("rodjuan@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();
    }
}
