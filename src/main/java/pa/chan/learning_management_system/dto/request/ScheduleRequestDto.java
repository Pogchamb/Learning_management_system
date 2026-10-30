package pa.chan.learning_management_system.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ScheduleRequestDto(
        @NotNull(message = "Id группы обязателен")
        Long groupId,
        @NotNull(message = "Id перподователя обязателен")
        Long teacherId,
        @NotNull(message = "Id курса обязателен")
        Long courseId,
        @NotNull(message = "Время начала занятия обязательно")
        LocalDateTime startTime,
        @NotNull(message = "Время окончания занятия обязательно")
        LocalDateTime endTime
) {
    @AssertTrue(message = "Время окончания занятия, должно быть позже времени начала.")
    private boolean isValidTimeRange() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return endTime.isAfter(startTime);
    }
}
