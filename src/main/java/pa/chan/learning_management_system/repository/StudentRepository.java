package pa.chan.learning_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pa.chan.learning_management_system.entity.Student;
import pa.chan.learning_management_system.exception.ResourceNotFoundException;

import java.util.Collection;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    default Student findStudentByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student " + id + " not found"));
    }


    default List<Student> findStudentsByIdsOrThrow(Collection<Long> ids) {
        List<Student> foundStudents = findAllById(ids);

        if (findAllById(ids).size() != ids.size()) {
            throw new ResourceNotFoundException("Один или несколько указанных студентов не найдены");
        }

        return foundStudents;
    }

}
