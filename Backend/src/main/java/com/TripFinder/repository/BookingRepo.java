package com.TripFinder.repository;

import com.TripFinder.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Booking entity.
 * Provides database operations for booking management.
 */
@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    /**
     * Find all bookings for a specific user
     * @param userId the user ID
     * @return list of bookings
     */
    List<Booking> findByUserId(int userId);

    /**
     * Find booking by booking reference
     * @param bookingReference the booking reference
     * @return booking if found
     */
    Optional<Booking> findByBookingReference(String bookingReference);

    /**
     * Find bookings by status
     * @param status the booking status
     * @return list of bookings with the specified status
     */
    List<Booking> findByStatus(Booking.BookingStatus status);

    /**
     * Find bookings created between dates
     * @param startDate start date
     * @param endDate end date
     * @return list of bookings
     */
    List<Booking> findByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get booking statistics query
     * @return booking count by status
     */
    @Query("SELECT b.status, COUNT(b) FROM Booking b GROUP BY b.status")
    List<Object[]> getBookingStatsByStatus();

    /**
     * Find bookings with user and itinerary information for admin view
     * @return list of bookings with related entities
     */
    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.user u " +
           "JOIN FETCH b.itinerary i " +
           "ORDER BY b.bookingDate DESC")
    List<Booking> findAllWithUserAndItinerary();

    /**
     * Count total bookings for a user
     * @param userId the user ID
     * @return number of bookings
     */
    long countByUserId(int userId);
}