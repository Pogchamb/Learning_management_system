package pa.chan.learning_management_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pa.chan.learning_management_system.dto.request.TeacherRequestDto;
import pa.chan.learning_management_system.dto.response.TeacherResponseDto;
import pa.chan.learning_management_system.service.TeacherService;

import java.net.URI;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;


    @PostMapping
    public ResponseEntity<TeacherResponseDto> createStudent(
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
    public ResponseEntity<TeacherResponseDto> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody TeacherRequestDto teacherRequestDto) {
        return ResponseEntity.ok(teacherService.updateStudent(id, teacherRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
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
    public ResponseEntity<Page<TeacherResponseDto>> getCourses(Pageable pageable) {
        return ResponseEntity.ok(teacherService.getTeachers(pageable));
    }
}
