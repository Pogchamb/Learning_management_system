package pa.chan.learning_management_system.controller;

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
public class ScheduleController {
    private final ScheduleService scheduleService;


    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> getScheduleById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<Page<ScheduleResponseDto>> getScheduleByGroup(
            @PathVariable Long groupId,
            @ParameterObject @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleByGroup(groupId, pageable));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<Page<ScheduleResponseDto>> getScheduleByTeacher(
            @PathVariable Long teacherId,
            @ParameterObject @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleByTeacher(teacherId, pageable));
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequestDto scheduleRequestDto
    ) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, scheduleRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long id
    ) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

}
