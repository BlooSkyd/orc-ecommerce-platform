package com.membership.users.application.service;

import com.membership.users.application.dto.UserRequestDTO;
import com.membership.users.application.dto.UserResponseDTO;
import com.membership.users.application.mapper.UserMapper;
import com.membership.users.domain.entity.User;
import com.membership.users.domain.repository.UserRepository;
import com.membership.users.infrastructure.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private SimpleMeterRegistry meterRegistry;

    private UserService userService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        userService = new UserService(userRepository, userMapper, meterRegistry);
    }

    @Test
    void createUser_shouldSaveAndReturnDto_whenEmailNotExists() {
        UserRequestDTO req = UserRequestDTO.builder()
                .firstName("Jean")
                .lastName("Valjean")
                .email("jean@example.com")
                .build();

        User entity = User.builder()
                .firstName("Jean")
                .lastName("Valjean")
                .email("jean@example.com")
                .build();

        User saved = User.builder()
                .id(7L)
                .firstName("Jean")
                .lastName("Valjean")
                .email("jean@example.com")
                .build();

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(userMapper.toEntity(req)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toDto(saved)).thenReturn(UserResponseDTO.builder()
                .id(7L)
                .email("jean@example.com")
                .firstName("Jean")
                .lastName("Valjean")
                .active(true)
                .build());

        UserResponseDTO result = userService.createUser(req);

        assertNotNull(result);
        assertEquals(7L, result.getId());
        verify(userRepository).save(entity);
        // metric should have been registered
        assertTrue(meterRegistry.find("users.created").counter() != null);
    }

    @Test
    void createUser_shouldThrow_whenEmailExists() {
        UserRequestDTO req = UserRequestDTO.builder()
                .firstName("X")
                .lastName("Y")
                .email("exists@example.com")
                .build();

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.createUser(req));

        verify(userRepository, never()).save(any());
    }
}
