package com.membership.users.application.mapper;

import com.membership.users.application.dto.UserRequestDTO;
import com.membership.users.application.dto.UserResponseDTO;
import com.membership.users.domain.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toEntity_shouldMapFields_andSetActiveTrue() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .firstName("Alice")
                .lastName("Dupont")
                .email("alice@example.com")
                .build();

        User entity = mapper.toEntity(dto);

        assertEquals("Alice", entity.getFirstName());
        assertEquals("Dupont", entity.getLastName());
        assertEquals("alice@example.com", entity.getEmail());
        assertTrue(Boolean.TRUE.equals(entity.getActive()));
    }

    @Test
    void toDto_shouldMapAllFields() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(42L)
                .firstName("Bob")
                .lastName("Martin")
                .email("bob@example.com")
                .active(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        UserResponseDTO dto = mapper.toDto(user);

        assertEquals(42L, dto.getId());
        assertEquals("Bob", dto.getFirstName());
        assertEquals("Martin", dto.getLastName());
        assertEquals("bob@example.com", dto.getEmail());
        assertFalse(dto.getActive());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }
}
