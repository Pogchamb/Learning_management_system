package pa.chan.learning_management_system.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pa.chan.learning_management_system.entity.Student;

public class StudentSpecifications {

    public static Specification<Student> studentHasOnlyOneGroup(
            Long groupId
    ) {

        return (root, query, criteriaBuilder) -> {
            Join<Object, Object> groupsJoin = root.join("groups");

            Predicate groupMatch = criteriaBuilder.equal(groupsJoin.get("id"), groupId);

            Predicate sizeMatch = criteriaBuilder.equal(
                    criteriaBuilder.size(root.get("groups")),
                    1
            );

            return criteriaBuilder.and(groupMatch, sizeMatch);
        };
    }
}
