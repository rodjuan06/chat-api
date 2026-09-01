package com.rodjuan.chatapi.chat.service;

import com.rodjuan.chatapi.chat.model.Chat;
import com.rodjuan.chatapi.chat.repository.ChatRepository;
import com.rodjuan.chatapi.chat.repository.MessageRepository;
import com.rodjuan.chatapi.exception.ChatNotFoundException;
import com.rodjuan.chatapi.user.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(chatRepository, messageRepository, userRepository);
    }

    @Test
    void listsOnlyChatsContainingCurrentUser() {
        long userId = 7L;
        List<Chat> expected = List.of(new Chat());
        when(chatRepository.findAllByMemberIdsContaining(userId)).thenReturn(expected);

        List<Chat> result = chatService.getAllChats(userId);

        assertSame(expected, result);
        verify(chatRepository).findAllByMemberIdsContaining(userId);
        verify(chatRepository, never()).findAll();
    }

    @Test
    void getsChatOnlyWhenCurrentUserIsMember() {
        ObjectId chatId = new ObjectId();
        long userId = 7L;
        Chat chat = new Chat();
        chat.setId(chatId);
        when(chatRepository.findByIdAndMemberIdsContaining(chatId, userId)).thenReturn(Optional.of(chat));

        Chat result = chatService.getChatById(chatId, userId);

        assertSame(chat, result);
    }

    @Test
    void rejectsChatWhenCurrentUserIsNotMember() {
        ObjectId chatId = new ObjectId();
        long userId = 7L;
        when(chatRepository.findByIdAndMemberIdsContaining(chatId, userId)).thenReturn(Optional.empty());

        assertThrows(ChatNotFoundException.class, () -> chatService.getChatById(chatId, userId));
    }

    @Test
    void createsChatWithCreatorAndWithoutDuplicateMembers() {
        long creatorId = 7L;
        Chat input = new Chat();
        input.setId(new ObjectId());
        input.setName("General");
        input.setMemberIds(List.of(2L, creatorId, 2L));
        when(userRepository.countByIdIn(anyCollection())).thenReturn(2L);
        when(chatRepository.save(input)).thenReturn(input);

        Chat result = chatService.createChat(input, creatorId);

        assertNull(result.getId());
        assertEquals(List.of(2L, creatorId), result.getMemberIds());
        assertNotNull(result.getCreatedAt());
        verify(userRepository).countByIdIn(argThat(ids -> ids.size() == 2
                && ids.contains(2L) && ids.contains(creatorId)));
        verify(chatRepository).save(input);
    }

    @Test
    void addsCreatorWhenNoMembersAreProvided() {
        long creatorId = 7L;
        Chat input = new Chat();
        input.setName("Private");
        when(userRepository.countByIdIn(anyCollection())).thenReturn(1L);
        when(chatRepository.save(input)).thenReturn(input);

        Chat result = chatService.createChat(input, creatorId);

        assertEquals(List.of(creatorId), result.getMemberIds());
    }

    @Test
    void rejectsChatWhenAnyMemberDoesNotExist() {
        Chat input = new Chat();
        input.setMemberIds(List.of(2L, 999L));
        when(userRepository.countByIdIn(anyCollection())).thenReturn(2L);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> chatService.createChat(input, 7L)
        );

        assertEquals("One or more chat members do not exist", exception.getMessage());
        verify(chatRepository, never()).save(any());
    }

    @Test
    void deletesMessagesBeforeDeletingChat() {
        ObjectId chatId = new ObjectId();
        long userId = 7L;
        Chat chat = new Chat();
        chat.setId(chatId);
        when(chatRepository.findByIdAndMemberIdsContaining(chatId, userId)).thenReturn(Optional.of(chat));

        chatService.deleteChatById(chatId, userId);

        InOrder inOrder = inOrder(messageRepository, chatRepository);
        inOrder.verify(messageRepository).deleteAllByChatId(chatId);
        inOrder.verify(chatRepository).delete(chat);
    }
}
