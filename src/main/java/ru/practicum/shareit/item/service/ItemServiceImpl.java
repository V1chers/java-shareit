package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.validation.ValidationService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto createItem(CreateItemDto itemDto, int userId) {
        log.info("начинаем создания предмета: {}, userId = {}", itemDto, userId);
        ValidationService.isExist(userRepository, userId, "Данный пользователь не найден");

        Item item = ItemMapper.fromCreateDto(itemDto, userId);
        item = itemRepository.save(item);

        log.info("Предмет создан: {}", item);
        return ItemMapper.toDto(item);
    }

    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        ValidationService.isExist(userRepository, userId, "Данный пользователь не найден");

        Item updatingItem = findItemById(itemId);

        if (itemDto.getName() != null) {
            updatingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatingItem.setAvailable(itemDto.getAvailable());
        }

        updatingItem = itemRepository.save(updatingItem);

        return ItemMapper.toDto(updatingItem);
    }

    public ItemCommentsDto getItem(int itemId) {
        Item item = findItemById(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        ItemCommentsDto itemDto = ItemMapper.toItemCommentsDto(item, comments);
        fillBookingFields(itemDto);
        return itemDto;
    }

    public List<ItemCommentsDto> getUserItems(int userId) {
        ValidationService.isExist(userRepository, userId, "Данный пользователь не найден");

        List<Item> items = itemRepository.findAllByUserId(userId);

        List<Integer> itemIds = items.stream().map(Item::getId).toList();
        List<Comment> comments = commentRepository.findAllByItemId(itemIds);
        List<ItemCommentsDto> itemCommentsDtoList = fillComments(items, comments);
        fillBookingFields(itemCommentsDtoList);

        return itemCommentsDtoList;
    }

    public List<ItemDto> searchItems(String text) {
        log.info("Происходит поиск вещей по запросу : {}", text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        text = "%" + text + "%";

        return ItemMapper.toDto(
                itemRepository.findByNameLikeIgnoreCaseAndAvailableTrueOrDescriptionLikeIgnoreCaseAndAvailableTrue
                        (text, text)
        );
    }

    @Transactional
    public CommentDto addComment(TextDto text, int authorId, int itemId) {
        log.info("Происходит добавление комментария: текст {}, автор {}, вещь {}", text, authorId, itemId);
        ValidationService.isExist(itemRepository, itemId, "Данный предмет не найден");
        ValidationService.isExist(userRepository, authorId, "Данный пользователь не найден");
        isUserBookedItem(itemId, authorId);

        Comment comment = new Comment(text.getText(), authorId, itemId);
        comment = commentRepository.save(comment);
        comment = commentRepository.findById(comment.getId()).get();
        // Не получилось решить проблему, из-за которой
        // в некоторых(вроде при поиске одной сущности) запросах не загружаются данные связанных сущностей
        comment.setAuthor(userRepository.findById(comment.getAuthor().getId()).get());

        log.info("Комментарий был успешно добавлен: {}", comment);
        return CommentMapper.toDto(comment);
    }

    private Item findItemById(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным id не был найден"));
    }

    private void isUserBookedItem(int itemId, int userId) {
        List<Booking> bookingList = bookingRepository.findByBookerAndItemId(userId, itemId);
        if (bookingList.isEmpty()) {
            throw new ConditionsNotMetException("Пользователь не заказывал данный товар");
        }

        boolean isItemUsed = false;

        for (Booking booking : bookingList) {
            if (booking.getApproved() && booking.getStart().isBefore(Instant.now())) {
                isItemUsed = true;
            }
        }

        if (!isItemUsed) {
            throw new ConditionsNotMetException("Пользователь не успел воспользоваться товаром");
        }
    }

    private void fillBookingFields(List<ItemCommentsDto> itemCommentsDtoList) {
        List<Integer> itemIds = itemCommentsDtoList.stream().map(ItemCommentsDto::getId).toList();
        List<Booking> bookingList = bookingRepository.getAllBookingsOfItems(itemIds);

        itemCommentsDtoList
                .forEach(itemCommentsDto -> fillLastAndNextBooking(itemCommentsDto, bookingList));
    }

    private void fillBookingFields(ItemCommentsDto itemCommentsDto) {
        List<Booking> bookingList = bookingRepository.getAllBookingsOfItems(itemCommentsDto.getId());

        fillLastAndNextBooking(itemCommentsDto, bookingList);
    }

    private void fillLastAndNextBooking(ItemCommentsDto item, List<Booking> BookingList) {
        List<Booking> itemBookingList = BookingList.stream()
                .filter(booking -> booking.getItem().getId() == item.getId()).toList();
        Instant currentTime = Instant.now();
        Booking last = null;
        Booking next = null;

        for (Booking booking : itemBookingList) {
            if (last == null && booking.getApproved() && booking.getEnd().isBefore(currentTime)) {
                last = booking;
            }
            if (next == null && booking.getApproved() && booking.getStart().isAfter(currentTime)) {
                next = booking;
            }
            if (last != null && booking.getApproved() && booking.getEnd().isBefore(currentTime)) {
                last = last.getEnd().isBefore(booking.getEnd()) ? booking : last;
            }
            if (next != null && booking.getApproved() && booking.getStart().isAfter(currentTime)) {
                next = next.getStart().isAfter(booking.getStart()) ? booking : next;
            }
        }

        if (next != null) {
            item.setNextBooking(BookingMapper.toDto(next));
        }
        if (last != null) {
            item.setLastBooking(BookingMapper.toDto(last));
        }
    }

    private List<ItemCommentsDto> fillComments(List<Item> itemList, List<Comment> comments) {
        return itemList.stream()
                .map(item -> {
                    List<Comment> itemComments = comments.stream()
                            .filter(comment -> comment.getItemId() == item.getId())
                            .toList();
                    return ItemMapper.toItemCommentsDto(item, itemComments);
                })
                .toList();
    }
}
