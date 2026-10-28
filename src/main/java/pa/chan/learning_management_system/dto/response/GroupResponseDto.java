package pa.chan.learning_management_system.dto.response;

import pa.chan.learning_management_system.entity.Student;

import java.util.Set;

public record GroupResponseDto(
        Long id,
        String groupName,
        Set<Student> studentsInGroup
) {
}
