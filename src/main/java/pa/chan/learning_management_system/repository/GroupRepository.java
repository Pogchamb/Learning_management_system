package pa.chan.learning_management_system.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pa.chan.learning_management_system.entity.Group;
import pa.chan.learning_management_system.entity.Student;

@Repository
public interface GroupRepository extends BaseRepository<Group>, JpaSpecificationExecutor<Student> {
    @Override
    default String getEntityName() {
        return "Группа";
    }

    boolean existsByGroupNameIgnoreCase(String groupName);

    boolean existsByGroupNameIgnoreCaseAndIdNot(String name, Long id);


}
