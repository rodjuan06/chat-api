package com.rodjuan.chatapi.chat.controller;

import com.rodjuan.chatapi.chat.model.Message;
import com.rodjuan.chatapi.chat.service.MessageService;
import com.rodjuan.chatapi.security.JwtService;
import com.rodjuan.chatapi.support.ControllerTestConfiguration;
import com.rodjuan.chatapi.user.CustomUserDetailsService;
import com.rodjuan.chatapi.user.Role;
import com.rodjuan.chatapi.user.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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

@WebMvcTest(MessageController.class)
@ContextConfiguration(classes = {ControllerTestConfiguration.class, MessageController.class})
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void returns401WhenAuthenticationIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/chats/{chatId}/messages", new ObjectId().toHexString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication is required"));

        verifyNoInteractions(messageService);
    }

    @Test
    void listsMessagesForAuthenticatedMember() throws Exception {
        ObjectId chatId = new ObjectId();
        Message message = new Message();
        message.setText("Hello");
        when(messageService.findAllByChat(chatId, 7L)).thenReturn(List.of(message));

        mockMvc.perform(get("/api/v1/chats/{chatId}/messages", chatId.toHexString())
                        .with(user(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Hello"));

        verify(messageService).findAllByChat(chatId, 7L);
    }

    @Test
    void returns400ForBlankMessage() throws Exception {
        ObjectId chatId = new ObjectId();

        mockMvc.perform(post("/api/v1/chats/{chatId}/messages", chatId.toHexString())
                        .with(user(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "text": " "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.text").value("Message text is required"));

        verifyNoInteractions(messageService);
    }

    @Test
    void returns201AndLocationWhenMessageIsCreated() throws Exception {
        ObjectId chatId = new ObjectId();
        ObjectId messageId = new ObjectId();
        Message savedMessage = new Message();
        savedMessage.setId(messageId);
        savedMessage.setChatId(chatId);
        savedMessage.setSenderId(7L);
        savedMessage.setText("Hello");
        when(messageService.save(eq(chatId), any(Message.class), eq(7L))).thenReturn(savedMessage);

        mockMvc.perform(post("/api/v1/chats/{chatId}/messages", chatId.toHexString())
                        .with(user(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "text": "Hello"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "http://localhost/api/v1/chats/" + chatId.toHexString()
                                + "/messages/" + messageId.toHexString()
                ))
                .andExpect(jsonPath("$.senderId").value(7))
                .andExpect(jsonPath("$.text").value("Hello"));
    }

    @Test
    void returns403WhenUserCannotDeleteMessage() throws Exception {
        ObjectId chatId = new ObjectId();
        ObjectId messageId = new ObjectId();
        doThrow(new AccessDeniedException("Only the message author can delete it"))
                .when(messageService).delete(chatId, messageId, 7L);

        mockMvc.perform(delete("/api/v1/chats/{chatId}/messages/{id}",
                                chatId.toHexString(), messageId.toHexString())
                        .with(user(authenticatedUser())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Only the message author can delete it"));
    }

    @Test
    void returns204WhenMessageIsDeleted() throws Exception {
        ObjectId chatId = new ObjectId();
        ObjectId messageId = new ObjectId();

        mockMvc.perform(delete("/api/v1/chats/{chatId}/messages/{id}",
                                chatId.toHexString(), messageId.toHexString())
                        .with(user(authenticatedUser())))
                .andExpect(status().isNoContent());

        verify(messageService).delete(chatId, messageId, 7L);
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
