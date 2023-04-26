package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.PagesForSort;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto addNewRequest(ItemRequestDto requestDto, long userId) {
        final User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        requestDto.setCreated(LocalDateTime.now());

        final ItemRequest request = RequestMapper.map2Request(requestDto, requester);
        final ItemRequest savedRequest = requestRepository.save(request);

        return RequestMapper.map2RequestDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        final List<ItemRequest> requests = requestRepository.findAllByRequester_Id(userId);

        return requests.stream()
                .map(RequestMapper::map2RequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, String from, String size) {
        userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));

        if (PagesForSort.createPage(from, size)) {
            return requestRepository.findAllForOtherUserWithPagination(userId, from, size)
                    .stream()
                    .map(RequestMapper::map2RequestDto)
                    .collect(Collectors.toList());
        } else {
            return requestRepository.findAllForOtherUser(userId)
                    .stream()
                    .map(RequestMapper::map2RequestDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public ItemRequestDto getRequestById(long requestId, long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        final ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request id = %d not found", requestId)));

        final ItemRequestDto requestDto = RequestMapper.map2RequestDto(request);

        return requestDto;
    }
}
