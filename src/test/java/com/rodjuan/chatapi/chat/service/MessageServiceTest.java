package com.rodjuan.chatapi.chat.service;

import com.rodjuan.chatapi.chat.model.Message;
import com.rodjuan.chatapi.chat.repository.ChatRepository;
import com.rodjuan.chatapi.chat.repository.MessageRepository;
import com.rodjuan.chatapi.exception.ChatNotFoundException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRepository chatRepository;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(messageRepository, chatRepository);
    }

    @Test
    void listsMessagesWhenCurrentUserIsChatMember() {
        ObjectId chatId = new ObjectId();
        long userId = 7L;
        List<Message> expected = List.of(new Message());
        when(chatRepository.existsByIdAndMemberIdsContaining(chatId, userId)).thenReturn(true);
        when(messageRepository.findByChatId(chatId)).thenReturn(expected);

        List<Message> result = messageService.findAllByChat(chatId, userId);

        assertSame(expected, result);
    }

    @Test
    void rejectsMessageListingWhenCurrentUserIsNotChatMember() {
        ObjectId chatId = new ObjectId();
        long userId = 7L;
        when(chatRepository.existsByIdAndMemberIdsContaining(chatId, userId)).thenReturn(false);

        assertThrows(ChatNotFoundException.class, () -> messageService.findAllByChat(chatId, userId));
        verify(messageRepository, never()).findByChatId(any());
    }

    @Test
    void savesMessageWithAuthenticatedSenderAndServerFields() {
        ObjectId chatId = new ObjectId();
        long userId = 7L;
        Message input = new Message();
        input.setId(new ObjectId());
        input.setSenderId(999L);
        input.setText("Hello");
        when(chatRepository.existsByIdAndMemberIdsContaining(chatId, userId)).thenReturn(true);
        when(messageRepository.insert(input)).thenReturn(input);

        Message result = messageService.save(chatId, input, userId);

        assertNull(result.getId());
        assertEquals(userId, result.getSenderId());
        assertEquals(chatId, result.getChatId());
        assertNotNull(result.getDate());
        verify(messageRepository).insert(input);
    }

    @Test
    void allowsAuthorToEditMessage() {
        ObjectId chatId = new ObjectId();
        ObjectId messageId = new ObjectId();
        long userId = 7L;
        Message existing = message(chatId, messageId, userId, "Before");
        Message changes = new Message();
        changes.setText("After");
        when(chatRepository.existsByIdAndMemberIdsContaining(chatId, userId)).thenReturn(true);
        when(messageRepository.findByIdAndChatId(messageId, chatId)).thenReturn(Optional.of(existing));
        when(messageRepository.save(existing)).thenReturn(existing);

        Message result = messageService.update(chatId, messageId, changes, userId);

        assertEquals("After", result.getText());
        verify(messageRepository).save(existing);
    }

    @Test
    void rejectsEditByAnotherUser() {
        ObjectId chatId = new ObjectId();
        ObjectId messageId = new ObjectId();
        long authorId = 7L;
        long anotherUserId = 8L;
        Message existing = message(chatId, messageId, authorId, "Before");
        when(chatRepository.existsByIdAndMemberIdsContaining(chatId, anotherUserId)).thenReturn(true);
        when(messageRepository.findByIdAndChatId(messageId, chatId)).thenReturn(Optional.of(existing));

        assertThrows(
                AccessDeniedException.class,
                () -> messageService.update(chatId, messageId, new Message(), anotherUserId)
        );
        verify(messageRepository, never()).save(any());
    }

    @Test
    void allowsAuthorToDeleteMessage() {
        ObjectId chatId = new ObjectId();
        ObjectId messageId = new ObjectId();
        long userId = 7L;
        Message existing = message(chatId, messageId, userId, "Hello");
        when(chatRepository.existsByIdAndMemberIdsContaining(chatId, userId)).thenReturn(true);
        when(messageRepository.findByIdAndChatId(messageId, chatId)).thenReturn(Optional.of(existing));

        messageService.delete(chatId, messageId, userId);

        verify(messageRepository).delete(existing);
    }

    @Test
    void rejectsDeleteByAnotherUser() {
        ObjectId chatId = new ObjectId();
        ObjectId messageId = new ObjectId();
        long authorId = 7L;
        long anotherUserId = 8L;
        Message existing = message(chatId, messageId, authorId, "Hello");
        when(chatRepository.existsByIdAndMemberIdsContaining(chatId, anotherUserId)).thenReturn(true);
        when(messageRepository.findByIdAndChatId(messageId, chatId)).thenReturn(Optional.of(existing));

        assertThrows(
                AccessDeniedException.class,
                () -> messageService.delete(chatId, messageId, anotherUserId)
        );
        verify(messageRepository, never()).delete(any());
    }

    private Message message(ObjectId chatId, ObjectId messageId, long senderId, String text) {
        Message message = new Message();
        message.setId(messageId);
        message.setChatId(chatId);
        message.setSenderId(senderId);
        message.setText(text);
        return message;
    }
}
