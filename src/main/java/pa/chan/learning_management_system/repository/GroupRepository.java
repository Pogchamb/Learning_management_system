package pa.chan.learning_management_system.repository;


import org.springframework.stereotype.Repository;
import pa.chan.learning_management_system.entity.Group;

@Repository
public interface GroupRepository extends BaseRepository<Group> {
    @Override
    default String getEntityName() {
        return "Группа";
    }

    boolean existsByGroupNameIgnoreCase(String groupName);

    boolean existsByGroupNameIgnoreCaseAndIdNot(String name, Long id);


}
