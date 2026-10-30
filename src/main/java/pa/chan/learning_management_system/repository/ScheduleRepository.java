package pa.chan.learning_management_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pa.chan.learning_management_system.entity.Schedule;
import pa.chan.learning_management_system.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends BaseRepository<Schedule> {

    @Override
    default String getEntityName() {
        return "Расписание";
    }

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

    @Query("""
                SELECT case WHEN count(s) > 0 THEN true ELSE false END
                FROM Schedule s
                WHERE (:excludeId is null or s.id <> :excludeId)
                AND (s.group.id = :groupId or s.teacher.id = :teacherId)
                AND s.startTime < :endTime
                AND s.endTime > :startTime
            """)
    boolean existsOverlappingSchedule(
            @Param("groupId") Long groupId,
            @Param("teacherId") Long teacherId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeId") Long excludeId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Schedule s where s.endTime < :threshold")
    int deleteSchedulesOlderThen(@Param("threshold") LocalDateTime threshold);
}
