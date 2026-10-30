package pa.chan.learning_management_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import pa.chan.learning_management_system.entity.Course;
import pa.chan.learning_management_system.exception.ResourceNotFoundException;

import java.util.Optional;

@Repository
public interface CourseRepository extends BaseRepository<Course> {
    @Override
    default String getEntityName() {
        return "Курс";
    }

    @Override
    @EntityGraph(attributePaths = "teacher")
    Page<Course> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"teacher", "groups"})
    Optional<Course> findWithDetailsById(Long id);

    default Course findWithDetailsByIdOrThrow(Long id) {
        return findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Курс с id " + id + " не найден"));
    }

    boolean existsByCourseNameIgnoreCase(String courseName);

    boolean existsByCourseNameIgnoreCaseAndIdNot(String courseName, Long id);

    boolean existsByTeacherId(Long teacherId);

    boolean existsByIdAndGroupsId(Long courseId, Long groupId);
}
