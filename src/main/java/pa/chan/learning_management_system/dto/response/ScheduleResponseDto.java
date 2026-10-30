package pa.chan.learning_management_system.dto.response;

import java.time.LocalDateTime;

public record ScheduleResponseDto(
        Long id,
        GroupShortResponseDto group,
        TeacherResponseDto teacher,
        CourseShortResponseDto course,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
