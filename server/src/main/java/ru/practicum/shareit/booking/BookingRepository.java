package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT booking.* FROM booking " +
            "WHERE booking.booker_id = ?1 " +
            "AND booking.item_id = ?2 " +
            "AND booking.status like ?3 " +
            "AND booking.end_booking < now() " +
            "ORDER BY booking.end_booking DESC limit 1",
            nativeQuery = true)
    Optional<Booking> findBookingByBookerIdAndItemId(long bookerId, long itemId, String status);

    @Query(value = "SELECT booking.* FROM booking " +
            "inner join item on booking.item_id = item.id " +
            "WHERE item.owner_id = ?1 " +
            "ORDER BY booking.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByOwnerId(long ownerId, int from, int size);

    @Query(value = "SELECT booking.* FROM booking " +
            "inner join item on booking.item_id = item.id " +
            "WHERE item.owner_id = ?1 " +
            "AND booking.status = ?2 " +
            "ORDER BY booking.end_booking DESC " +
            "limit ?4 OFFSET ?3", nativeQuery = true)
    List<Booking> findAllByOwnerIdWithStatus(long ownerId, String status, int from, int size);

    @Query(value = "SELECT booking.* FROM booking " +
            "inner join item on booking.item_id = item.id " +
            "WHERE item.owner_id = ?1 " +
            "AND booking.start_booking < now() " +
            "AND booking.end_booking > now() " +
            "ORDER BY booking.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByOwnerIdWithStatusCurrent(long ownerId, int from, int size);

    @Query(value = "SELECT booking.* FROM booking " +
            "inner join item on booking.item_id = item.id " +
            "WHERE item.owner_id = ?1 " +
            "AND booking.start_booking > now() " +
            "ORDER BY booking.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByOwnerIdWithStatusFuture(long ownerId, int from, int size);

    @Query(value = "SELECT booking.* FROM booking " +
            "inner join item on booking.item_id = item.id " +
            "WHERE item.owner_id = ?1 " +
            "AND booking.end_booking < now() " +
            "ORDER BY booking.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByOwnerIdWithStatusPast(long ownerId, int from, int size);

    @Query(value = "SELECT * FROM booking " +
            "WHERE booking.booker_id = ?1 " +
            "ORDER BY booking.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByBookerId(long bookerId, int from, int size);

    @Query(value = "SELECT * FROM booking " +
            "WHERE booking.booker_id = ?1 " +
            "AND booking.status = ?2 " +
            "ORDER BY booking.end_booking DESC " +
            "limit ?4 OFFSET ?3", nativeQuery = true)
    List<Booking> findAllByBookerIdWithStatus(long bookerId, String status, int from, int size);

    @Query(value = "SELECT * FROM booking " +
            "WHERE booking.booker_id = ?1 " +
            "AND booking.start_booking < now() " +
            "AND booking.end_booking > now() " +
            "ORDER BY booking.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByBookerIdWithStatusCurrent(
            long bookerId, int from, int size);

    @Query(value = "SELECT * FROM booking " +
            "WHERE booking.booker_id = ?1 " +
            "AND booking.start_booking > now() " +
            "ORDER BY booking.end_booking DESC " +
            "limit ?3 OFFSET ?2", nativeQuery = true)
    List<Booking> findAllByBookerIdWithStatusFuture(long bookerId, int from, int size);

    @Query(value = "SELECT * FROM booking " +
            "WHERE booking.booker_id = ?1 " +
            "AND booking.end_booking < now() " +
            "ORDER BY booking.end_booking DESC " +
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
