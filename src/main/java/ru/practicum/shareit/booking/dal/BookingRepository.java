package ru.practicum.shareit.booking.dal;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
            "where u.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByBookerId(int bookerId);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item i " +
            "join fetch b.booker u " +
            "where (:bookerId IS NULL OR u.id = :bookerId) " +
            "AND (:ownerId IS NULL OR i.userId = :ownerId) " +
            "AND (:approved = FALSE OR b.approved = true) " +
            "AND (:approvedNull = FALSE OR b.approved IS NULL) " +
            "AND (:isFuture = FALSE OR CAST(b.start AS timestamp) > CURRENT_TIMESTAMP) " +
            "AND (:isPast = FALSE OR CAST(b.end AS timestamp) < CURRENT_TIMESTAMP) " +
            "AND (:isCurrent = FALSE OR CAST(b.start AS timestamp) < CURRENT_TIMESTAMP) " +
            "AND (:isCurrent = FALSE OR CAST(b.end AS timestamp) > CURRENT_TIMESTAMP)" +
            "order by b.start desc")
    List<Booking> findAllByUserAndState(@Param("bookerId") Integer bookerId,
                                        @Param("ownerId") Integer ownerId,
                                        @Param("approved") boolean approved,
                                        @Param("approvedNull") boolean approvedNull,
                                        @Param("isFuture") boolean isFuture,
                                        @Param("isPast") boolean isPast,
                                        @Param("isCurrent") boolean isCurrent);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item i " +
            "join fetch b.booker u " +
            "where u.id = ?1 " +
            "and (b.approved = false " +
            "or (b.approved is null " +
            "and cast(b.start as timestamp) < current_timestamp)) " +
            "order by b.start desc")
    List<Booking> findAllRejectedByBookerId(int bookerId);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item i " +
            "join fetch b.booker u " +
            "where i.userId = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByItemOwnerId(int ownerId);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item i " +
            "join fetch b.booker u " +
            "where (i.userId = ?1) " +
            "and (b.approved = false " +
            "or (b.approved is null " +
            "and cast(b.start as timestamp) < current_timestamp)) " +
            "order by b.start desc")
    List<Booking> findAllRejectedByItemOwnerId(int ownerId);

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
