package pa.chan.learning_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TeacherRequestDto(
        @NotBlank(message = "Имя не может быть пустым")
        @Size(max = 255, message = "firstName is too long")
        String firstName,
        @NotBlank(message = "Фамилия не может быть пустой")
        @Size(max = 255, message = "lastName is too long")
        String lastName
) {
}
