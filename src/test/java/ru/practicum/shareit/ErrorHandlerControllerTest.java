package ru.practicum.shareit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DateTimeException;
import ru.practicum.shareit.exception.MismatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class ErrorHandlerControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService service;
    @Autowired
    private MockMvc mvc;
    private User user = new User(1L, "name", "email@gmail.com");;

    @Test
    void handleNotFound() throws Exception {
        Mockito.when(service.addUser(any()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));
    }

    @Test
    void handleMismatch() throws Exception {
        Mockito.when(service.addUser(any()))
                .thenThrow(MismatchException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(404));
    }

    @Test
    void handleDateTime() throws Exception {
        Mockito.when(service.addUser(any()))
                .thenThrow(DateTimeException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));
    }

    @Test
    void handleValidation() throws Exception {
        Mockito.when(service.addUser(any()))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));
    }

    @Test
    void handleRuntime() throws Exception {
        Mockito.when(service.addUser(any()))
                .thenThrow(RuntimeException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(500));
    }
}