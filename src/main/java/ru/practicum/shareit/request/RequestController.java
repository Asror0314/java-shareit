package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    ItemRequestDto addNewRequest(
            @RequestBody @Valid ItemRequestDto requestDto,
            @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return requestService.addNewRequest(requestDto, userId);
    }

    @GetMapping
    List<ItemRequestDto> getYourRequests(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return requestService.getYourRequests(userId);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getAllRequests(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String size
    ) {
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getRequestById(@RequestHeader(value = "X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return requestService.getRequestById(requestId, userId);
    }
}
