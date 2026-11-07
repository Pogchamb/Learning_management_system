package pa.chan.learning_management_system.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pa.chan.learning_management_system.entity.Schedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleSpecifications {

    public static Specification<Schedule> hasOverlappingSchedule(
            Long groupId,
            Long teacherId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long excludeId
    ) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (excludeId != null) {
                predicates.add(criteriaBuilder.notEqual(root.get("id"), excludeId));
            }

            Predicate groupMatch = criteriaBuilder.equal(root.get("group").get("id"), groupId);
            Predicate teacherMatch = criteriaBuilder.equal(root.get("teacher").get("id"), teacherId);
            predicates.add(criteriaBuilder.or(groupMatch, teacherMatch));

            predicates.add(criteriaBuilder.lessThan(root.get("startTime"), endTime));

            predicates.add(criteriaBuilder.greaterThan(root.get("endTime"), startTime));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

    }
}
