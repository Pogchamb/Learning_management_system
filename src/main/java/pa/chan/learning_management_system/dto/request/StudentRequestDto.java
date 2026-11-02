package pa.chan.learning_management_system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "Запрос на создание или обновление студента")
public record StudentRequestDto(
        @Schema(description = "Имя студента")
        @NotBlank(message = "Имя не может быть пустым")
        @Size(max = 255, message = "Имя слишком длинное")
        String firstName,
        @Schema(description = "Фамилия студента")
        @NotBlank(message = "Фамилия не может быть пустой")
        @Size(max = 255, message = "Фамилия слишком длинная")
        String lastName,
        @Schema(description = "Id групп (минимум одна)")
        @NotEmpty(message = "Студент должен состоять минимум в одной группе")
        Set<@NotNull(message = "Id группы не может быть null") Long> groupIds
) {
}
