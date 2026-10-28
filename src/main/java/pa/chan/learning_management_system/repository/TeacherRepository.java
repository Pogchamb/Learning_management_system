package pa.chan.learning_management_system.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pa.chan.learning_management_system.entity.Course;
import pa.chan.learning_management_system.entity.Teacher;
import pa.chan.learning_management_system.exception.ResourceNotFoundException;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    default Teacher findTeacherByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher " + id + " not found"));
    }

}
