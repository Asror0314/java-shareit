package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    private RequestService service;
    private User user = new User(1L, "name", "email");
    private Item item = new Item(1L, "itemName", "description", true,
                                user, null);
    private final Long wrongUserId = 100L;
    private ItemRequestDto requestDto = new ItemRequestDto();
    private ItemRequest request = new ItemRequest(1L, "description", LocalDateTime.now(),
                                        user, null);

    @BeforeEach
    void beforeEach() {
        service = new RequestServiceImpl(requestRepository, userRepository);
        requestDto.setDescription("description");
    }

    @Test
    void addNewRequest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(request.getRequester()));
        Mockito
                .when(requestRepository.save(any()))
                .thenReturn(request);

        final ItemRequestDto newRequest = service.addNewRequest(requestDto, user.getId());

        assertEquals(request.getId(), newRequest.getId());
        assertEquals(request.getDescription(), newRequest.getDescription());
        assertNotNull(newRequest.getCreated());
    }

    @Test
    void addNewRequestWithWrongUserId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User id = %d not found", wrongUserId)));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.addNewRequest(requestDto, wrongUserId));
        assertEquals(String.format("User id = %d not found", wrongUserId), exception.getMessage());
    }

    @Test
    void getOwnRequests() {
        request.setItems(Arrays.asList(item));
        item.setRequest(request);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.findAllByRequester_Id(anyLong()))
                .thenReturn(Arrays.asList(request));

        final List<ItemRequestDto> ownRequests = service.getOwnRequests(user.getId());

        assertEquals(1, ownRequests.size());
        assertEquals(request.getId(), ownRequests.get(0).getId());
        assertEquals(request.getDescription(), ownRequests.get(0).getDescription());
        assertEquals(request.getItems().get(0).getId(), ownRequests.get(0).getItems().get(0).getId());
        assertEquals(request.getItems().get(0).getName(), ownRequests.get(0).getItems().get(0).getName());
        assertEquals(request.getItems().get(0).getDescription(), ownRequests.get(0).getItems().get(0).getDescription());
        assertEquals(request.getId(), ownRequests.get(0).getItems().get(0).getRequestId());
        assertTrue(ownRequests.get(0).getItems().get(0).getAvailable());
    }

    @Test
    void getOwnRequestsWithWrongUserId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User id = %d not found", wrongUserId)));

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getOwnRequests(wrongUserId));

        assertEquals(String.format("User id = %d not found", wrongUserId), exception.getMessage());
    }

    @Test
    void getOwnRequestsWithoutRequest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.findAllByRequester_Id(anyLong()))
                .thenReturn(Arrays.asList());

        final List<ItemRequestDto> ownRequests = service.getOwnRequests(user.getId());

        assertEquals(0, ownRequests.size());
    }

    @Test
    void getAllRequestsWithoutPagination() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.findAllForOtherUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(Arrays.asList(request));

        final List<ItemRequestDto> ownRequests = service.getAllRequests(user.getId(), 0, 10);

        assertEquals(1, ownRequests.size());
        assertEquals(request.getId(), ownRequests.get(0).getId());
        assertEquals(request.getDescription(), ownRequests.get(0).getDescription());
    }

    @Test
    void getAllRequestsWithPagination() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.findAllForOtherUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(Arrays.asList(request));

        final List<ItemRequestDto> ownRequests = service.getAllRequests(user.getId(), 0, 1);

        assertEquals(1, ownRequests.size());
        assertEquals(request.getId(), ownRequests.get(0).getId());
        assertEquals(request.getDescription(), ownRequests.get(0).getDescription());
    }

    @Test
    void getAllRequestsWithWrongUserId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User id = %d not found", wrongUserId)));

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getOwnRequests(wrongUserId));

        assertEquals(String.format("User id = %d not found", wrongUserId), exception.getMessage());
    }

    @Test
    void getRequestById() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));

        final ItemRequestDto requestById = service.getRequestById(request.getId(), user.getId());

        assertEquals(request.getId(), requestById.getId());
        assertEquals(request.getDescription(), requestById.getDescription());
    }

    @Test
    void getRequestByIdWithWrongUserId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User id = %d not found", wrongUserId)));

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getRequestById(request.getId(), user.getId()));

        assertEquals(String.format("User id = %d not found", wrongUserId), exception.getMessage());
    }

    @Test
    void getRequestByIdWithoutRequest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Request id = %d not found", request.getId())));

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getRequestById(request.getId(), user.getId()));

        assertEquals(String.format("Request id = %d not found", request.getId()), exception.getMessage());
    }

}