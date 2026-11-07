package pa.chan.learning_management_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pa.chan.learning_management_system.dto.request.ScheduleRequestDto;
import pa.chan.learning_management_system.dto.response.ScheduleResponseDto;
import pa.chan.learning_management_system.service.ScheduleService;

import java.net.URI;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
@Tag(name = "Расписание", description = "Управление расписанием")
public class ScheduleController {
    private final ScheduleService scheduleService;


    @Operation(summary = "Получить расписание по Id")
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> getScheduleById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @Operation(summary = "Получить всё расписание")
    @GetMapping
    public ResponseEntity<Page<ScheduleResponseDto>> getAllSchedules(
            @ParameterObject @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(scheduleService.getAllSchedules(pageable));
    }

    @Operation(summary = "Создать новое расписание")
    @PostMapping
    public ResponseEntity<ScheduleResponseDto> creteSchedule(
            @Valid @RequestBody ScheduleRequestDto scheduleRequestDto
    ) {
        ScheduleResponseDto created = scheduleService.createSchedule(scheduleRequestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Обновить расписание")
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequestDto scheduleRequestDto
    ) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, scheduleRequestDto));
    }

    @Operation(summary = "Удалить расписание")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long id
    ) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

}
