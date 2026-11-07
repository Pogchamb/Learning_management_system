package pa.chan.learning_management_system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание или обновление преподавателя")
public record TeacherRequestDto(
        @Schema(description = "Имя преподавателя")
        @NotBlank(message = "Имя не может быть пустым")
        @Size(max = 255, message = "Имя слишком длинное")
        String firstName,
        @Schema(description = "Фамилия преподавателя")
        @NotBlank(message = "Фамилия не может быть пустой")
        @Size(max = 255, message = "Фамилия слишком длинная")
        String lastName
) {
}
