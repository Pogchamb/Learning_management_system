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
import pa.chan.learning_management_system.dto.request.TeacherRequestDto;
import pa.chan.learning_management_system.dto.response.ScheduleResponseDto;
import pa.chan.learning_management_system.dto.response.TeacherResponseDto;
import pa.chan.learning_management_system.service.ScheduleService;
import pa.chan.learning_management_system.service.TeacherService;

import java.net.URI;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final ScheduleService scheduleService;


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

    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponseDto> updateTeacher(
            @PathVariable Long id,
            @Valid @RequestBody TeacherRequestDto teacherRequestDto) {
        return ResponseEntity.ok(teacherService.updateStudent(id, teacherRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(
            @PathVariable Long id
    ) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponseDto> getCourseById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(teacherService.getTeacher(id));
    }

    @GetMapping
    public ResponseEntity<Page<TeacherResponseDto>> getTeachers(
            @ParameterObject @PageableDefault(size = 20, sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(teacherService.getTeachers(pageable));
    }

    @GetMapping({"/{id}/schedules"})
    public ResponseEntity<Page<ScheduleResponseDto>> getTeacherSchedules(
            @PathVariable Long id,
            @ParameterObject @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleByTeacher(id, pageable));
    }
}
