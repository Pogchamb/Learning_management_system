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
import pa.chan.learning_management_system.dto.request.CourseRequestDto;
import pa.chan.learning_management_system.dto.response.CourseResponseDto;
import pa.chan.learning_management_system.service.CourseService;

import java.net.URI;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Tag(name = "Курсы", description = "Управление курсами и запись групп на курсы")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Создать новый курс")
    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(
            @Valid @RequestBody CourseRequestDto courseRequestDto
    ) {
        CourseResponseDto createdCourse = courseService.createCourse(courseRequestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCourse.id())
                .toUri();

        return ResponseEntity.created(location).body(createdCourse);
    }

    @Operation(summary = "Изменить курс или сменить преподавателя курса")
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequestDto courseRequestDto
    ) {
        return ResponseEntity.ok(courseService.updateCourse(id, courseRequestDto));
    }

    @Operation(summary = "Удалить курс")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id
    ) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить курс по ID вместе с преподавателем и записанными группами")
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getCourseById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(courseService.getCourse(id));
    }

    @Operation(summary = "Получить список всех курсов")
    @GetMapping
    public ResponseEntity<Page<CourseResponseDto>> getAllCourses(
            @ParameterObject @PageableDefault(size = 20, sort = "courseName") Pageable pageable
    ) {
        return ResponseEntity.ok(courseService.getCourses(pageable));
    }

    @Operation(summary = "Добавить группу на курс")
    @PostMapping("/{courseId}/groups/{groupId}")
    public ResponseEntity<CourseResponseDto> addGroupToCourse(
            @PathVariable Long courseId,
            @PathVariable Long groupId
    ) {
        return ResponseEntity.ok(courseService.addGroupToCourse(courseId, groupId));
    }

    @Operation(summary = "Открепить группу от курса")
    @DeleteMapping("/{courseId}/groups/{groupId}")
    public ResponseEntity<CourseResponseDto> removeGroupFromCourse(
            @PathVariable Long courseId,
            @PathVariable Long groupId
    ) {
        return ResponseEntity.ok(courseService.removeGroupFromCourse(courseId, groupId));
    }

}
