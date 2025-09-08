package ru.practicum.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.event.EventState;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public class EventSpecifications {

    public static Specification<Event> hasState(EventState state) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), state);
    }

    public static Specification<Event> containsText(String text) {
        return (root, query, criteriaBuilder) -> {
            if (text == null || text.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            String likeText = "%" + text.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), likeText),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likeText)
            );
        };
    }

    public static Specification<Event> hasCategories(List<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("category").get("id").in(categoryIds);
        };
    }

    public static Specification<Event> hasPaid(Boolean paid) {
        return (root, query, criteriaBuilder) -> {
            if (paid == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("paid"), paid);
        };
    }

    public static Specification<Event> hasStartDateTime(LocalDateTime startDateTime) {
        return (root, query, criteriaBuilder) -> {
            if (startDateTime == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), startDateTime);
        };
    }

    public static Specification<Event> hasEndDateTime(LocalDateTime endDateTime) {
        return (root, query, criteriaBuilder) -> {
            if (endDateTime == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), endDateTime);
        };
    }

    public static Specification<Event> isAvailable(Boolean onlyAvailable) {
        return (root, query, criteriaBuilder) -> {
            if (onlyAvailable == null || !onlyAvailable) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("participantLimit"), 0),
                    criteriaBuilder.lessThan(root.get("confirmedRequests"), root.get("participantLimit"))
            );
        };
    }

    public static Specification<Event> isPublished() {
        return hasState(EventState.PUBLISHED);
    }

    public static Specification<Event> hasUsers(List<Long> userIds) {
        return (root, query, criteriaBuilder) -> {
            if (userIds == null || userIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("initiator").get("id").in(userIds);
        };
    }

    public static Specification<Event> hasStates(List<EventState> states) {
        return (root, query, criteriaBuilder) -> {
            if (states == null || states.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("state").in(states);
        };
    }

    public static Specification<Event> afterStartTime(LocalDateTime startTime) {
        return (root, query, criteriaBuilder) -> {
            if (startTime == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), startTime);
        };
    }

    public static Specification<Event> beforeEndTime(LocalDateTime endTime) {
        return (root, query, criteriaBuilder) -> {
            if (endTime == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), endTime);
        };
    }
}