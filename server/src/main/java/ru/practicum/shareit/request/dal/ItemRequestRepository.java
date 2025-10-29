package ru.practicum.shareit.request.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    @Query("select distinct ir " +
            "from ItemRequest ir " +
            "join fetch ir.requester r " +
            "where r.id = ?1 " +
            "order by ir.created  desc")
    List<ItemRequest> findAllByUserId(int userId);

    @Query("select distinct ir " +
            "from ItemRequest ir " +
            "join fetch ir.requester r " +
            "where r.id <> ?1 " +
            "order by ir.created  desc")
    List<ItemRequest> findAllExceptUserId(int userId);

    @Query("select distinct ir " +
            "from ItemRequest ir " +
            "join fetch ir.requester r " +
            "where ir.id = ?1 ")
    Optional<ItemRequest> findItemRequestById(int userId);
}
