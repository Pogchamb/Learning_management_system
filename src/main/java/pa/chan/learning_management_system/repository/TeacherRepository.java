package pa.chan.learning_management_system.repository;


import org.springframework.stereotype.Repository;
import pa.chan.learning_management_system.entity.Teacher;

@Repository
public interface TeacherRepository extends BaseRepository<Teacher> {
    @Override
    default String getEntityName() {
        return "Преподователь";
    }

}
