package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.EventAttendance;

import java.util.Optional;

public interface EventAttendanceRepository extends JpaRepository<EventAttendance, Long> {
    boolean existsByRequestId(Long requestId);

    @Query("""
            SELECT ea FROM EventAttendance ea
            JOIN ea.request pr
            WHERE pr.event.id = :eventId
              AND pr.requester.id = :userId
              AND ea.attended = true
            """)
    Optional<EventAttendance> findByEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);
}