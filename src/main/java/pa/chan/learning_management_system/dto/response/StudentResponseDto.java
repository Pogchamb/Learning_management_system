package pa.chan.learning_management_system.dto.response;


import java.util.Set;

public record StudentResponseDto(
        Long id,
        String firstName,
        String lastName,
        Set<GroupShortResponseDto> groups
) {
}
