package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {

    ItemRequestDto addNewRequest(ItemRequestDto requestDto, long userId);

    List<ItemRequestDto> getOwnRequests(long userId);

    List<ItemRequestDto> getAllRequests(long userId, String from, String size);

    ItemRequestDto getRequestById(long requestId, long userId);
}
