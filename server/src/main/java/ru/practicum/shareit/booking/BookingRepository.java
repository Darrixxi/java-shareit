package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.status = 'APPROVED' AND b.start < :now AND b.end > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByBookerId(Long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastByBookerId(Long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureByBookerId(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.status = 'APPROVED' AND b.start < :now AND b.end > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByItemOwnerId(Long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastByItemOwnerId(Long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureByItemOwnerId(Long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    Optional<Booking> findTop1ByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(
            Long itemId, LocalDateTime now, BookingStatus status);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' AND b.start <= :now AND b.end >= :now " +
            "ORDER BY b.start DESC")
    Optional<Booking> findCurrentByItemId(Long itemId, LocalDateTime now);
}