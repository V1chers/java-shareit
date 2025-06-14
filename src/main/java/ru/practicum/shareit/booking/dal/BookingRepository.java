package ru.practicum.shareit.booking.dal;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Transactional
    @Modifying
    @Query("update Booking b set b.approved = ?1 where b.id = ?2")
    int approveBooking(boolean isApproved, int bookingId);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item i " +
            "join fetch b.booker u " +
            "where u.id = ?1")
    List<Booking> findAllByBookerId(int bookerId);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item i " +
            "join fetch b.booker u " +
            "where i.userId = ?1")
    List<Booking> findAllByItemOwnerId(int ownerId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "join b.booker u " +
            "where u.id = ?1 " +
            "and i.id = ?2 ")
    List<Booking> findByBookerAndItemId(int bookerId, int itemId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "join b.booker u " +
            "where u.id in ?1")
    List<Booking> getAllBookingsOfItems(List<Integer> itemIds);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "join b.booker u " +
            "where u.id = ?1")
    List<Booking> getAllBookingsOfItems(Integer itemIds);
}
