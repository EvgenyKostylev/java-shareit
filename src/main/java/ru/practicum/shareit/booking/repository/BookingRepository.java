package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    long countByItemOwnerId(long ownerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.end = (" +
            "SELECT MAX(bm.end) FROM Booking AS bm " +
            "WHERE bm.item.id = b.item.id " +
            "AND bm.end < :now" +
            ")")
    List<Booking> findLastBookings(
            @Param("itemIds") List<Long> itemIds,
            @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.end = (" +
            "SELECT MIN(bm.end) FROM Booking AS bm " +
            "WHERE bm.item.id = b.item.id " +
            "AND bm.start > :now)")
    List<Booking> findNextBookings(
            @Param("itemIds") List<Long> itemIds,
            @Param("now") LocalDateTime now);

    Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(
            long itemId,
            LocalDateTime now);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(
            long itemId,
            LocalDateTime now);

    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(
            long itemId,
            long bookerId,
            LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(
            long userId,
            Status status);

    List<Booking> findAllByItemOwnerIdAndStatusAndStartAfterOrderByStartDesc(
            long userId,
            Status status,
            LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatusAndEndBeforeOrderByStartDesc(
            long userId,
            Status status,
            LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status " +
            "AND :date BETWEEN b.start AND b.end")
    List<Booking> findAllByItemOwnerIdAndContainingDateOrderByStartDesc(
            @Param("ownerId") long ownerId,
            @Param("date") LocalDateTime now,
            @Param("status") Status status);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.status = :status " +
            "AND :date BETWEEN b.start AND b.end")
    List<Booking> findAllByBookerIdAndContainingDateOrderByStartDesc(
            @Param("bookerId") long bookerId,
            @Param("date") LocalDateTime now,
            @Param("status") Status status);

    List<Booking> findAllByBookerIdAndStatusAndStartAfterOrderByStartDesc(
            long bookerId,
            Status status,
            LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
            long bookerId,
            Status status,
            LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(
            long bookerId,
            Status status);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.start < :endDt " +
            "AND b.end > :startDt")
    boolean existsOverlappingBookings(
            @Param("itemId") long itemId,
            @Param("startDt") LocalDateTime startDt,
            @Param("endDt") LocalDateTime endDt);
}