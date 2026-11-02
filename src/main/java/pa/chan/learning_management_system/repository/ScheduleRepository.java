package pa.chan.learning_management_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pa.chan.learning_management_system.entity.Schedule;
import pa.chan.learning_management_system.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends BaseRepository<Schedule>, JpaSpecificationExecutor<Schedule> {

    @Override
    default String getEntityName() {
        return "Расписание";
    }

    @Override
    @EntityGraph(attributePaths = {"group", "teacher", "course"})
    Page<Schedule> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"group", "teacher", "course"})
    Optional<Schedule> findWithDetailsById(Long id);

    default Schedule findWithDetailsByIdOrThrow(Long id) {
        return findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Занятие в расписании с id " + id + " не найдено"));
    }

    @EntityGraph(attributePaths = {"group", "teacher", "course"})
    Page<Schedule> findAllByGroupId(Long groupId, Pageable pageable);

    @EntityGraph(attributePaths = {"group", "teacher", "course"})
    Page<Schedule> findAllByTeacherId(Long teacherId, Pageable pageable);

    boolean existsByTeacherId(Long teacherId);

    boolean existsByCourseId(Long courseId);

    boolean existsByGroupId(Long groupId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Schedule s where s.endTime < :limit")
    void deleteSchedulesOlderThen(@Param("limit") LocalDateTime limit);
}
