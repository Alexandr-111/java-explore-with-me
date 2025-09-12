package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.EventReview;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface EventReviewRepository extends JpaRepository<EventReview, Long> {
    Optional<EventReview> findById(Long id);

    Page<EventReview> findByIdIn(List<Long> ids, Pageable pageable);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    List<EventReview> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
            SELECT er FROM EventReview er
            JOIN er.event e
            WHERE e.id = :eventId
            ORDER BY er.createdAt DESC
            """)
    List<EventReview> findLastPublicReviews(@Param("eventId") Long eventId, Pageable pageable);

    @Query("SELECT AVG(er.rating) FROM EventReview er WHERE er.event.id = :eventId")
    BigDecimal findAverageRatingByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(er) FROM EventReview er WHERE er.event.id = :eventId")
    Long countByEventId(@Param("eventId") Long eventId);

    // Рейтинг организатора
    @Query(value = """
            SELECT
                COALESCE(SUM(avg_rating * review_count) / SUM(review_count), 0.0)
            FROM (
                SELECT
                    AVG(er.rating) AS avg_rating,
                    COUNT(er.id) AS review_count
                FROM event_reviews er
                JOIN events e ON er.event_id = e.id
                WHERE e.initiator_id = :organizerId
                GROUP BY e.id
            ) AS event_ratings
            """, nativeQuery = true)
    BigDecimal findWeightedAverageRatingByOrganizerId(@Param("organizerId") Long organizerId);

    // Общее количество отзывов на все события организатора
    @Query(value = """
            SELECT COALESCE(SUM(review_count), 0)
            FROM (
                SELECT COUNT(er.id) AS review_count
                FROM event_reviews er
                JOIN events e ON er.event_id = e.id
                WHERE e.initiator_id = :organizerId
                GROUP BY e.id
            ) AS event_reviews
            """, nativeQuery = true)
    Long countReviewsForRatedEventsByOrganizerId(@Param("organizerId") Long organizerId);
}