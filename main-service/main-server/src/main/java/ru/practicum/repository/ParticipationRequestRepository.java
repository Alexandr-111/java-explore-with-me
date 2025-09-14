package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.participation.ParticipationRequestStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByIdInAndEventIdAndStatus(
            Collection<Long> ids,
            Long eventId,
            ParticipationRequestStatus status
    );

    List<ParticipationRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId);

    Optional<ParticipationRequest> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    @Query("SELECT pr FROM ParticipationRequest pr JOIN FETCH pr.event WHERE pr.id = :id")
    Optional<ParticipationRequest> findByIdWithEvent(@Param("id") Long id);
}