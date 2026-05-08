package com.Cross_BorderDataTransferManager.backend;

import com.Cross_BorderDataTransferManager.backend.entity.User;
import com.Cross_BorderDataTransferManager.backend.repository.UserRepository;
import com.Cross_BorderDataTransferManager.backend.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl service;

    @Test
    void loadUserByUsername_shouldReturnSpringSecurityUser() {
        User user = new User();
        user.setUsername("demo");
        user.setPassword("encoded");
        user.setRole("USER");
        when(userRepository.findByUsername("demo")).thenReturn(Optional.of(user));

        var details = service.loadUserByUsername("demo");

        assertThat(details.getUsername()).isEqualTo("demo");
        assertThat(details.getAuthorities()).extracting("authority").contains("ROLE_USER");
    }

    @Test
    void loadUserByUsername_shouldThrowWhenUserMissing() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
