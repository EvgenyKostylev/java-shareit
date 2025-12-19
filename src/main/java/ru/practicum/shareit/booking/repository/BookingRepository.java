package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    long countByItemOwnerId(long ownerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(long itemId, LocalDateTime end);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(long itemId, LocalDateTime end);

    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(long itemId, long bookerId, LocalDateTime end);
}