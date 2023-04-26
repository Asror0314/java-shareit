package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;


@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    @Mock
    private RequestService service;
    @InjectMocks
    private RequestController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemRequestDto responseDto = new ItemRequestDto();
    private ItemRequestDto requestDto = new ItemRequestDto();

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        responseDto.setId(1L);
        responseDto.setDescription("Description");
        responseDto.setCreated(LocalDateTime.now());
        requestDto.setDescription("Description");
    }

    @Test
    @SneakyThrows
    void addNewRequest() {
        Mockito
                .when(service.addNewRequest(Mockito.any(), Mockito.anyLong()))
                .thenReturn(responseDto);

        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(requestDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())));
    }

    @Test
    @SneakyThrows
    void getOwnRequests() {
        Mockito
                .when(service.getOwnRequests(Mockito.anyLong()))
                .thenReturn(Arrays.asList(responseDto));

        mvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getAllRequests() {
        Mockito
                .when(service.getAllRequests(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Arrays.asList(responseDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getRequestById() {
        Mockito
                .when(service.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())));
    }
}