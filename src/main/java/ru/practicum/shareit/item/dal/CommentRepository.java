package ru.practicum.shareit.item.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("select c " +
            "from Comment c " +
            "join fetch c.author a " +
            "where c.itemId = ?1")
    List<Comment> findAllByItemId(int itemId);

    @Query("select c " +
            "from Comment c " +
            "join fetch c.author a " +
            "where c.itemId in ?1")
    List<Comment> findAllByItemId(List<Integer> itemIds);
}
