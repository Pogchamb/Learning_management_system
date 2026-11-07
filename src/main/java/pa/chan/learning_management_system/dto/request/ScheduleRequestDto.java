package pa.chan.learning_management_system.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Запрос на создание или изменение слота в расписании")
public record ScheduleRequestDto(
        @Schema(description = "Id группы")
        @NotNull(message = "Id группы обязателен")
        Long groupId,
        @Schema(description = "Id преподователя")
        @NotNull(message = "Id перподователя обязателен")
        Long teacherId,
        @Schema(description = "Id курса")
        @NotNull(message = "Id курса обязателен")
        Long courseId,
        @Schema(description = "Время начала занятия")
        @NotNull(message = "Время начала занятия обязательно")
        LocalDateTime startTime,
        @Schema(description = "Время окончания занятия")
        @NotNull(message = "Время окончания занятия обязательно")
        LocalDateTime endTime
) {
    @JsonIgnore
    @AssertTrue(message = "Время окончания занятия, должно быть позже времени начала.")
    private boolean isValidTimeRange() {
        return endTime.isAfter(startTime);
    }
}
