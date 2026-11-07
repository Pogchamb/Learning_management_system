package pa.chan.learning_management_system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание или обновление группы")
public record GroupRequestDto(
        @Schema(description = "Название группы")
        @NotBlank(message = "Название группы не может быть пустым")
        @Size(max = 255, message = "Название группы слишком длинное")
        String groupName
) {
}
