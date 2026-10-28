package pa.chan.learning_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record GroupRequestDto(
        @NotBlank
        @Size(max = 255, message = "GroupName is too long")
        String groupName
) {
}
