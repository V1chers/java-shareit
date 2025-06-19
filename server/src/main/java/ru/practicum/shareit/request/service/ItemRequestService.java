package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.DescriptionDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(int userId, DescriptionDto description);

    List<ItemRequestDto> getRequests(int userId);

    List<ItemRequestDto> getAllRequests(int userId);

    ItemRequestDto getRequest(int requestId);
}
