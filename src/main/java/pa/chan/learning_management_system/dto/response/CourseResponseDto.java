package pa.chan.learning_management_system.dto.response;

import java.util.Set;

public record CourseResponseDto(
        Long id,
        String courseName,
        String description,
        TeacherResponseDto teacher,
        Set<GroupShortResponseDto> groups
) {
}
