package com.Cross_BorderDataTransferManager.backend;

import com.Cross_BorderDataTransferManager.backend.config.JwtUtil;
import com.Cross_BorderDataTransferManager.backend.controller.AuthController;
import com.Cross_BorderDataTransferManager.backend.dto.LoginRequest;
import com.Cross_BorderDataTransferManager.backend.dto.RegisterRequest;
import com.Cross_BorderDataTransferManager.backend.entity.User;
import com.Cross_BorderDataTransferManager.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController controller;

    @Test
    void login_shouldAuthenticateAndReturnJwt() {
        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername("demo")
                .password("encoded")
                .roles("USER")
                .build();
        when(userDetailsService.loadUserByUsername("demo")).thenReturn(userDetails);
        when(jwtUtil.generateToken("demo")).thenReturn("jwt-token");

        var response = controller.login(new LoginRequest("demo", "password"));

        assertThat(response.getBody().token()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void register_shouldRejectDuplicateUsername() {
        when(userRepository.findByUsername("demo")).thenReturn(Optional.of(new User()));

        var response = controller.register(new RegisterRequest("demo", "password", "demo@example.com"));

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_shouldSaveNewUser() {
        when(userRepository.findByUsername("demo")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded");

        var response = controller.register(new RegisterRequest("demo", "password", "demo@example.com"));

        assertThat(response.getBody()).isEqualTo("User registered successfully");
        verify(userRepository).save(any(User.class));
    }
}
