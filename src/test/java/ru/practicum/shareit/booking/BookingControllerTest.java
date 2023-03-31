package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService service;
    @InjectMocks
    private BookingController controller;
    private ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private BookingRequestDto requestDto = new BookingRequestDto(1L, LocalDateTime.now(), LocalDateTime.now(), Status.WAITING);
    private BookingDto responseDto = new BookingDto();


    @BeforeEach
    void beforeEach() {
        final User user = new User(1L, "name", "email");
        final ItemDto item = new ItemDto(1L,"itemName","descr","true",
                        null,null,null,0L);
        responseDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now(),
                                Status.WAITING, user, item);
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void getAllBooking() throws Exception {
        Mockito
                .when(service.getAllBooking(anyLong(), anyString(), anyString(), anyString()))
                .thenReturn(Arrays.asList(responseDto));

        mvc.perform(get("/bookings")
                .param("state", "")
                .param("from", "")
                .param("size", "")
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()))
                .andExpect(jsonPath("$[0].status", is(responseDto.getStatus().toString())));
    }

    @Test
    void getAllBookingForOwner() throws Exception {
        Mockito
                .when(service.getAllBookingForOwner(anyLong(), anyString(), anyString(), anyString()))
                .thenReturn(Arrays.asList(responseDto));

        mvc.perform(get("/bookings/owner")
                        .param("state", "")
                        .param("from", "")
                        .param("size", "")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()))
                .andExpect(jsonPath("$[0].status", is(responseDto.getStatus().toString())));
    }

    @Test
    void getBookingById() throws Exception {
        Mockito
                .when(service.getBookingById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.status", is(responseDto.getStatus().toString())));
    }

    @Test
    void addNewBooking() throws Exception {
        Mockito
                .when(service.addNewBooking(any(), anyLong()))
                .thenReturn(responseDto);

        mapper.registerModule(new JavaTimeModule());

        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(requestDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getItem().getId()), Long.class));
    }

    @Test
    void updateBookingStatus() throws Exception {
        Mockito
                .when(service.updateBookingStatus(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(patch("/bookings/{bookingId}", responseDto.getId())
                .header("X-Sharer-User-Id", 1L)
                .param("approved", "true")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.status", is(responseDto.getStatus().toString())));
    }
}