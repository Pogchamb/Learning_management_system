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
import pa.chan.learning_management_system.dto.request.TeacherRequestDto;
import pa.chan.learning_management_system.dto.response.ScheduleResponseDto;
import pa.chan.learning_management_system.dto.response.TeacherResponseDto;
import pa.chan.learning_management_system.service.ScheduleService;
import pa.chan.learning_management_system.service.TeacherService;

import java.net.URI;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
@Tag(name = "Преподаватели", description = "Управление преподавателями и просмотр их расписания")
public class TeacherController {

    private final TeacherService teacherService;
    private final ScheduleService scheduleService;


    @Operation(summary = "Добавить нового преподавателя")
    @PostMapping
    public ResponseEntity<TeacherResponseDto> createTeacher(
            @Valid @RequestBody TeacherRequestDto teacherRequestDto
    ) {
        TeacherResponseDto created = teacherService.createTeacher(teacherRequestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Изменить данные преподавателя")
    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponseDto> updateTeacher(
            @PathVariable Long id,
            @Valid @RequestBody TeacherRequestDto teacherRequestDto) {
        return ResponseEntity.ok(teacherService.updateStudent(id, teacherRequestDto));
    }

    @Operation(summary = "Удалить преподавателя")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(
            @PathVariable Long id
    ) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить преподавателя по Id")
    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponseDto> getCourseById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(teacherService.getTeacher(id));
    }

    @Operation(summary = "Получить список всех преподавателей")
    @GetMapping
    public ResponseEntity<Page<TeacherResponseDto>> getTeachers(
            @ParameterObject @PageableDefault(size = 20, sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(teacherService.getTeachers(pageable));
    }

    @Operation(summary = "Поличить расписание для преподователя")
    @GetMapping({"/{id}/schedules"})
    public ResponseEntity<Page<ScheduleResponseDto>> getTeacherSchedules(
            @PathVariable Long id,
            @ParameterObject @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleByTeacher(id, pageable));
    }
}
