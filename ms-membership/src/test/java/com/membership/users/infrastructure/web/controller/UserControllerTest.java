package com.membership.users.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.membership.users.application.dto.UserRequestDTO;
import com.membership.users.application.dto.UserResponseDTO;
import com.membership.users.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        UserController controller = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        UserResponseDTO dto = UserResponseDTO.builder()
                .id(1L)
                .firstName("Toto")
                .lastName("Tata")
                .email("toto@example.com")
                .active(true)
                .build();

        when(userService.getAllUsers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void createUser_shouldReturnCreated_withLocation() throws Exception {
        UserRequestDTO req = UserRequestDTO.builder()
                .firstName("New")
                .lastName("User")
                .email("new@example.com")
                .build();

        UserResponseDTO res = UserResponseDTO.builder()
                .id(99L)
                .firstName("New")
                .lastName("User")
                .email("new@example.com")
                .active(true)
                .build();

        when(userService.createUser(org.mockito.ArgumentMatchers.any(UserRequestDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/99")))
                .andExpect(jsonPath("$.id").value(99));
    }
}
