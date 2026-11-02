package pa.chan.learning_management_system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "запрос на создание или обновление курса")
public record CourseRequestDto(
        @Schema(description = "Название курса")
        @NotBlank(message = "Название курса не может быть пустым")
        @Size(max = 255)
        String courseName,
        @Schema(description = "Описание курса")
        @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
        String description,
        @Schema(description = "Id преподователя ведущего курс")
        @NotNull(message = "У курса должен быть преподаватель")
        Long teacherId
) {
}
