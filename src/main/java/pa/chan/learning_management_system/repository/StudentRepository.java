package pa.chan.learning_management_system.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import pa.chan.learning_management_system.entity.Student;
import pa.chan.learning_management_system.exception.ResourceNotFoundException;

import java.util.Optional;

@Repository
public interface StudentRepository extends BaseRepository<Student> {
    @Override
    default String getEntityName() {
        return "Студент";
    }

    @EntityGraph(attributePaths = "groups")
    Optional<Student> findWithGroupsById(Long id);

    default Student findWithGroupsByIdOrThrow(Long id) {
        return findWithGroupsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Студент с id " + id + " не найден"));
    }
}
