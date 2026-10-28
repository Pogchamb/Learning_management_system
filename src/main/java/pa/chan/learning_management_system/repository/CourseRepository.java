package pa.chan.learning_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pa.chan.learning_management_system.entity.Course;
import pa.chan.learning_management_system.exception.ResourceNotFoundException;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    default Course findCourseByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course " + id + " not found"));
    }

}
