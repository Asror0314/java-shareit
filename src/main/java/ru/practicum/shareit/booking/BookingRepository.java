package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(long bookerId, Status status);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId, LocalDateTime currentDate, LocalDateTime dateTime);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime futureDate);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime pastDate);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(long ownerId, Status status);

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(long ownerId, LocalDateTime currentDate, LocalDateTime dateTime);

    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime futureDate);

    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime pastDate);

    Optional<Booking> findBookingByBooker_IdAndItem_IdAndStatusAndEndBefore(long bookerId, long itemId, Status status, LocalDateTime currentDate);
}
