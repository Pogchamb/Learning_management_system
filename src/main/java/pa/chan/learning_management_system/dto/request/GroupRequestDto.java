package pa.chan.learning_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GroupRequestDto(
        @NotBlank(message = "Название группы не может быть пустым")
        @Size(max = 255, message = "GroupName is too long")
        String groupName
) {
}
