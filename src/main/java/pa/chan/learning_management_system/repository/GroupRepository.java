package pa.chan.learning_management_system.repository;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pa.chan.learning_management_system.entity.Group;

import java.util.Optional;

@Repository
public interface GroupRepository extends BaseRepository<Group> {
    @Override
    default String getEntityName() {
        return "Группа";
    }

    @EntityGraph(attributePaths = {"studentsGroup"})
    Optional<Group> findWithStudentsById(Long id);

    boolean existsByGroupNameIgnoreCase(String groupName);

    boolean existsByGroupNameIgnoreCaseAndIdNot(String name, Long id);

    @Query("""
            SELECT CASE WHEN count (s) > 0 THEN true ELSE false END
            FROM Student s
            JOIN s.groups g
            WHERE g.id = :groupId
            AND size(s.groups) = 1
            """)
    boolean studentHasOnlyOneGroup(@Param("groupId") Long groupId);
}
