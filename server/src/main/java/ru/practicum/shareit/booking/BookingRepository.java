package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT b.* FROM shareit.booking as b " +
            "WHERE b.booker_id = ?1 " +
            "AND b.item_id = ?2 " +
            "AND b.status = ?3 " +
            "AND b.end_booking < now() " +
            "ORDER BY b.end_booking DESC limit 1",
            nativeQuery = true)
    Optional<Booking> findBookingByBookerIdAndItemId(long bookerId, long itemId, String status);

    @Query(value = "SELECT b.* FROM shareit.booking as b " +
            "inner join shareit.item as i on b.item_id = i.id " +
            "WHERE i.owner_id = ?1 " +
            "ORDER BY b.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByOwnerId(long ownerId, int from, int size);

    @Query(value = "SELECT b.* FROM shareit.booking as b " +
            "inner join shareit.item as i on b.item_id = i.id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.end_booking DESC " +
            "limit ?4 OFFSET ?3", nativeQuery = true)
    List<Booking> findAllByOwnerIdWithStatus(long ownerId, String status, int from, int size);

    @Query(value = "SELECT b.* FROM shareit.booking as b " +
            "inner join shareit.item as i on b.item_id = i.id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.start_booking < now() " +
            "AND b.end_booking > now() " +
            "ORDER BY b.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByOwnerIdWithStatusCurrent(long ownerId, int from, int size);

    @Query(value = "SELECT b.* FROM shareit.booking as b " +
            "inner join shareit.item as i on b.item_id = i.id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.start_booking > now() " +
            "ORDER BY b.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByOwnerIdWithStatusFuture(long ownerId, int from, int size);

    @Query(value = "SELECT b.* FROM shareit.booking as b " +
            "inner join shareit.item as i on b.item_id = i.id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.end_booking < now() " +
            "ORDER BY b.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByOwnerIdWithStatusPast(long ownerId, int from, int size);

    @Query(value = "SELECT b.* FROM shareit.booking as b " +
            "WHERE b.booker_id = ?1 " +
            "ORDER BY b.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByBookerId(long bookerId, int from, int size);

    @Query(value = "SELECT b.* FROM shareit.booking as b " +
            "WHERE b.booker_id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.end_booking DESC " +
            "limit ?4 OFFSET ?3", nativeQuery = true)
    List<Booking> findAllByBookerIdWithStatus(long bookerId, String status, int from, int size);

    @Query(value = "SELECT b.* FROM shareit.booking as b " +
            "WHERE b.booker_id = ?1 " +
            "AND b.start_booking < now() " +
            "AND b.end_booking > now() " +
            "ORDER BY b.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByBookerIdWithStatusCurrent(
            long bookerId, int from, int size);

    @Query(value = "SELECT b.* FROM shareit.booking as b " +
            "WHERE b.booker_id = ?1 " +
            "AND b.start_booking > now() " +
            "ORDER BY b.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByBookerIdWithStatusFuture(long bookerId, int from, int size);

    @Query(value = "SELECT b.* FROM shareit.booking as b " +
            "WHERE b.booker_id = ?1 " +
            "AND b.end_booking < now() " +
            "ORDER BY b.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByBookerIdWithStatusPast(long bookerId, int from, int size);

    @Query("SELECT booking FROM Booking AS booking " +
            "WHERE booking.item.id = ?2 " +
            "AND booking.start <= ?3 " +
            "AND booking.item.owner.id = ?1 " +
            "AND booking.status <> 'REJECTED' " +
            "ORDER BY booking.end DESC ")
    List<Booking> findLastBookingByItem_Id(long userId, long itemId, LocalDateTime currentDate);

    @Query("SELECT booking FROM Booking AS booking " +
            "WHERE booking.item.id = ?2 " +
            "AND booking.item.owner.id = ?1 " +
            "AND booking.start > ?3 " +
            "AND booking.status <> 'REJECTED' " +
            "ORDER BY booking.end ASC")
    List<Booking> findNextBookingByItem_Id(long userId, long itemId, LocalDateTime currentDate);
}
