package pa.chan.learning_management_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pa.chan.learning_management_system.dto.request.StudentRequestDto;
import pa.chan.learning_management_system.dto.response.StudentResponseDto;
import pa.chan.learning_management_system.service.StudentService;

import java.net.URI;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Tag(name = "Студенты", description = "Управление студентами и их привязкой к группам")
public class StudentController {

    private final StudentService studentService;


    @Operation(summary = "Добавить нового студента")
    @PostMapping
    public ResponseEntity<StudentResponseDto> createStudent(
            @Valid @RequestBody StudentRequestDto studentRequestDto
    ) {
        StudentResponseDto created = studentService.createStudent(studentRequestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Изменить данные студента и список его групп")
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDto studentRequestDto) {
        return ResponseEntity.ok(studentService.updateStudent(id, studentRequestDto));
    }

    @Operation(summary = "Удалить студента")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long id
    ) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить студента по Id вместе со списком его групп")
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getStudentById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(studentService.getStudent(id));
    }

    @Operation(summary = "Получить список всех студентов")
    @GetMapping
    public ResponseEntity<Page<StudentResponseDto>> getAllStudents(
            @ParameterObject @PageableDefault(size = 20, sort = "lastName") Pageable pageable
    ) {
        return ResponseEntity.ok(studentService.getStudents(pageable));
    }
}
