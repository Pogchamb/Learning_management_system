package pa.chan.learning_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CourseRequestDto(
        @NotBlank
        String courseName,
        String description
) {
}
