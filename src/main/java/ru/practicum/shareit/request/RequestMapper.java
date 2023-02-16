package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {

    public static ItemRequest map2Request(ItemRequestDto requestDto, User requester) {
        final ItemRequest request = new ItemRequest();

        request.setDescription(requestDto.getDescription());
        request.setCreated(requestDto.getCreated());
        request.setRequester(requester);
        return request;
    }

    public static ItemRequestDto map2RequestDto(ItemRequest request) {
        final ItemRequestDto requestDto = new ItemRequestDto();

        if (request.getItems() != null) {
            final List<ItemDtoForRequest> items = request.getItems()
                    .stream()
                    .map(ItemMapper::map2ItemDtoForRequest)
                    .collect(Collectors.toList());
            requestDto.setItems(items);
        }

        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setCreated(request.getCreated());
        return requestDto;
    }

    public static List<ItemRequestDto> map2RequestDto(List<ItemRequest> requests) {
        return requests.stream()
                .map(RequestMapper::map2RequestDto)
                .collect(Collectors.toList());
    }
}
