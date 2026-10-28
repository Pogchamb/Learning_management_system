package pa.chan.learning_management_system.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pa.chan.learning_management_system.entity.Group;
import pa.chan.learning_management_system.exception.ResourceNotFoundException;

import java.util.Collection;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    default Group findGroupByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group " + id + " not found"));
    }

    default List<Group> findGroupByIdsOrThrow(Collection<Long> ids) {
        List<Group> foundGroup = findAllById(ids);

        if (findAllById(ids).size() != ids.size()) {
            throw new ResourceNotFoundException("Одна или несколько указанных групп не найдены");
        }

        return foundGroup;
    }
}
