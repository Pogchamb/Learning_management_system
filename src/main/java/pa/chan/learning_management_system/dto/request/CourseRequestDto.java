package pa.chan.learning_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseRequestDto(
        @NotBlank(message = "Название курса не может быть пустым")
        String courseName,
        @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
        String description,
        @NotNull(message = "У курса должен быть преподаватель")
        Long teacherId
) {
}
