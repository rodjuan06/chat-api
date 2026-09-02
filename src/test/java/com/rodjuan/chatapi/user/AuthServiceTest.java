package com.rodjuan.chatapi.user;

import com.rodjuan.chatapi.exception.EmailAlreadyExistsException;
import com.rodjuan.chatapi.security.JwtService;
import com.rodjuan.chatapi.user.dto.AuthResponse;
import com.rodjuan.chatapi.user.dto.LoginRequest;
import com.rodjuan.chatapi.user.dto.RegisterRequest;
import com.rodjuan.chatapi.user.model.Role;
import com.rodjuan.chatapi.user.model.User;
import com.rodjuan.chatapi.user.repository.UserRepository;
import com.rodjuan.chatapi.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, authenticationManager);
    }

    @Test
    void registersUserWithEncodedPasswordAndDefaultRole() {
        RegisterRequest request = new RegisterRequest("Maria", "maria@example.com", "password");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("Maria", savedUser.getName());
        assertEquals("maria@example.com", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());
    }

    @Test
    void rejectsDuplicateEmail() {
        RegisterRequest request = new RegisterRequest("Maria", "maria@example.com", "password");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticatesUserAndReturnsToken() {
        LoginRequest request = new LoginRequest("maria@example.com", "password");
        User user = User.builder()
                .email(request.email())
                .password("encoded-password")
                .role(Role.USER)
                .build();
        when(userRepository.findByEmail(request.email())).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("jwt-token", response.token());
    }

    @Test
    void normalizesEmailAndNameWhenRegistering() {
        RegisterRequest request = new RegisterRequest(" Maria ", " Maria@Example.COM ", "password");
        when(userRepository.existsByEmail("maria@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertEquals("Maria", userCaptor.getValue().getName());
        assertEquals("maria@example.com", userCaptor.getValue().getEmail());
    }

    @Test
    void normalizesEmailWhenLoggingIn() {
        LoginRequest request = new LoginRequest(" Maria@Example.COM ", "password");
        User user = User.builder()
                .email("maria@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();
        when(userRepository.findByEmail("maria@example.com")).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authenticationCaptor.capture());
        assertEquals("maria@example.com", authenticationCaptor.getValue().getPrincipal());
        assertEquals("jwt-token", response.token());
    }
}
