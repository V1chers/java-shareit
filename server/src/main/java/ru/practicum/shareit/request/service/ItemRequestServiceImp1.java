package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.AnswerDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.DescriptionDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.validation.ValidationUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImp1 implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDto createRequest(int userId, DescriptionDto description) {
        log.info("Начинаем создание запроса: пользователь {}, писание {}", userId, description);
        ValidationUtils.isExist(userRepository, userId, "Данный пользователь не найден");

        ItemRequest itemRequest = createItemRequest(userId, description);
        itemRequest = itemRequestRepository.save(itemRequest);

        log.info("Запрос успешно создан: {}", itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    public List<ItemRequestDto> getRequests(int userId) {
        log.info("Начинается поиск запросов пользователя: {}", userId);
        ValidationUtils.isExist(userRepository, userId, "Данный пользователь не найден");

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByUserId(userId);

        return fillAnswers(itemRequests);
    }

    public List<ItemRequestDto> getAllRequests(int userId) {
        ValidationUtils.isExist(userRepository, userId, "Данный пользователь не найден");

        List<ItemRequest> itemRequests = itemRequestRepository.findAllExceptUserId(userId);

        return fillAnswers(itemRequests);
    }

    public ItemRequestDto getRequest(int requestId) {
        ItemRequest itemRequest = findById(requestId);

        return fillAnswers(itemRequest);
    }

    private ItemRequest findById(int requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с таким id не был найден"));
    }

    private ItemRequest createItemRequest(int userId, DescriptionDto description) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(Instant.now());
        itemRequest.setDescription(description.getDescription());
        User user = new User();
        user.setId(userId);
        itemRequest.setRequester(user);

        return itemRequest;
    }

    private List<ItemRequestDto> fillAnswers(List<ItemRequest> itemRequests) {
        // Попробовал уменьшить асимптотическую сложность
        List<Integer> itemRequestsIds = itemRequests.stream().map(ItemRequest::getId).toList();
        List<Item> answers = itemRepository.findAllByRequestIdIn(itemRequestsIds);

        HashMap<Integer, List<Item>> itemsAnswers = new HashMap<>();
        answers.forEach(answer -> {
            List<Item> itemList = itemsAnswers.computeIfAbsent(answer.getRequestId(), k -> new ArrayList<>());

            itemList.add(answer);
        });

        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        itemRequests
                .forEach(itemRequest -> {
                    List<Item> itemList = itemsAnswers.get(itemRequest.getId());
                    ItemRequestDto itemRequestDto;

                    if (itemList == null) {
                        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
                    } else {
                        List<AnswerDto> answersDto = ItemMapper.toAnswerDto(itemsAnswers.get(itemRequest.getId()));

                        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, answersDto);
                    }

                    itemRequestDtos.add(itemRequestDto);
                });

        return itemRequestDtos;
    }

    private ItemRequestDto fillAnswers(ItemRequest itemRequest) {
        List<Item> answers = itemRepository.findAllByRequestId(itemRequest.getId());
        List<AnswerDto> answersDto = ItemMapper.toAnswerDto(answers);

        return ItemRequestMapper.toItemRequestDto(itemRequest, answersDto);
    }
}
