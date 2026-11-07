package pa.chan.learning_management_system.dto.response;

import java.util.Set;

public record GroupResponseDto(
        Long id,
        String groupName,
        Set<StudentShortResponseDto> studentsInGroup
) {
}
