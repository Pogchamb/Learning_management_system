package pa.chan.learning_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record StudentRequestDto(
        @NotBlank(message = "Имя не может быть пустым")
        @Size(max = 255, message = "firstName is too long")
        String firstName,
        @NotBlank(message = "Фамилия не может быть пустой")
        @Size(max = 255, message = "lastName is too long")
        String lastName,
        @NotEmpty(message = "Студент должен состоять минимум в одной группе")
        Set<@NotNull(message = "ID группы не может быть null") Long> groupIds
) {
}
