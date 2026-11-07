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
import pa.chan.learning_management_system.dto.request.GroupRequestDto;
import pa.chan.learning_management_system.dto.response.GroupResponseDto;
import pa.chan.learning_management_system.dto.response.ScheduleResponseDto;
import pa.chan.learning_management_system.service.GroupService;
import pa.chan.learning_management_system.service.ScheduleService;

import java.net.URI;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@Tag(name = "Группы", description = "Управление группами, составом студентов и расписанием групп")
public class GroupController {

    private final GroupService groupService;
    private final ScheduleService scheduleService;

    @Operation(summary = "Создать новую группу")
    @PostMapping
    public ResponseEntity<GroupResponseDto> createGroup(
            @Valid @RequestBody GroupRequestDto groupRequestDto
    ) {
        GroupResponseDto created = groupService.createGroup(groupRequestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Изменить название группы")
    @PutMapping("/{id}")
    public ResponseEntity<GroupResponseDto> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody GroupRequestDto groupRequestDto
    ) {
        return ResponseEntity.ok(groupService.updateGroup(id, groupRequestDto));
    }

    @Operation(summary = "Удалить группу (если она не является единственной у своих студентов)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long id
    ) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить группу по ID")
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> getGroupById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(groupService.getGroup(id));
    }

    @Operation(summary = "Получить список всех групп")
    @GetMapping
    public ResponseEntity<Page<GroupResponseDto>> getAllGroups(
            @ParameterObject @PageableDefault(size = 20, sort = "groupName") Pageable pageable
    ) {
        return ResponseEntity.ok(groupService.getGroups(pageable));
    }

    @Operation(summary = "Добавить студента в группу")
    @PostMapping("/{groupId}/students/{studentId}")
    public ResponseEntity<Void> addStudentToGroup(
            @PathVariable Long groupId,
            @PathVariable Long studentId
    ) {
        groupService.addStudentToGroup(groupId, studentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Исключить студента из группы (У студента должно быть больше 1 группы)")
    @DeleteMapping("/{groupId}/students/{studentId}")
    public ResponseEntity<Void> removeStudentFromGroup(
            @PathVariable Long groupId,
            @PathVariable Long studentId
    ) {
        groupService.removeStudentsFromGroup(groupId, studentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Просмотреть график проведения курсов для группы")
    @GetMapping("/{groupId}/schedules")
    public ResponseEntity<Page<ScheduleResponseDto>> getGroupSchedules(
            @PathVariable Long groupId,
            @ParameterObject @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleByGroup(groupId, pageable));
    }
}
