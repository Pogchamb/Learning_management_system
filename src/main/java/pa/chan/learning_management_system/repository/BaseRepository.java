package pa.chan.learning_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import pa.chan.learning_management_system.entity.BaseEntity;
import pa.chan.learning_management_system.exception.ResourceNotFoundException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    String getEntityName();

    default T findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        getEntityName() + " с, id" + id + " не найден"
                ));
    }

    default Set<T> findAllByIdsOrThrow(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }

        Set<Long> uniqueIds = Set.copyOf(ids);
        List<T> foundEntities = findAllById(uniqueIds);

        if (foundEntities.size() != uniqueIds.size()) {
            if (ids.isEmpty()) {
                return new HashSet<>();
            }

            Set<Long> foundIds = foundEntities.stream()
                    .map(BaseEntity::getId)
                    .collect(Collectors.toSet());

            List<Long> missingIds = uniqueIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new ResourceNotFoundException(
                    getEntityName() + " со следующими id не найдены: " + missingIds
            );
        }

        return new HashSet<>(foundEntities);
    }

}
