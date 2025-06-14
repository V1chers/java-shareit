package ru.practicum.shareit.item.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByUserId(int userId);

    List<Item> findByNameLikeIgnoreCaseAndAvailableTrueOrDescriptionLikeIgnoreCaseAndAvailableTrue
            (String text, String sameText);
}
