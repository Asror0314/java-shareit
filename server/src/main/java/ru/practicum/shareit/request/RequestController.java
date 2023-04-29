package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    ItemRequestDto addNewRequest(
            @RequestBody ItemRequestDto requestDto,
            @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return requestService.addNewRequest(requestDto, userId);
    }

    @GetMapping
    List<ItemRequestDto> getOwnRequests(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return requestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getAllRequests(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestParam Integer from,
            @RequestParam Integer size
    ) {
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getRequestById(@RequestHeader(value = "X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return requestService.getRequestById(requestId, userId);
    }
}
