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
import pa.chan.learning_management_system.dto.request.GroupRequestDto;
import pa.chan.learning_management_system.dto.response.GroupResponseDto;
import pa.chan.learning_management_system.dto.response.ScheduleResponseDto;
import pa.chan.learning_management_system.service.GroupService;
import pa.chan.learning_management_system.service.ScheduleService;

import java.net.URI;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final ScheduleService scheduleService;

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

    @PutMapping("/{id}")
    public ResponseEntity<GroupResponseDto> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody GroupRequestDto groupRequestDto
    ) {
        return ResponseEntity.ok(groupService.updateGroup(id, groupRequestDto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long id
    ) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> getCourseById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(groupService.getGroup(id));
    }

    @GetMapping
    public ResponseEntity<Page<GroupResponseDto>> getAllCourses(
            @ParameterObject @PageableDefault(size = 20, sort = "groupName") Pageable pageable
    ) {
        return ResponseEntity.ok(groupService.getGroups(pageable));
    }

    @PostMapping("/{groupId}/students/{studentId}")
    public ResponseEntity<Void> addStudentsToGroup(
            @PathVariable Long groupId,
            @PathVariable Long studentId
    ) {
        groupService.addStudentToGroup(groupId, studentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/students/{studentId}")
    public ResponseEntity<Void> removeStudentFromGroup(
            @PathVariable Long groupId,
            @PathVariable Long studentId
    ) {
        groupService.removeStudentsFromGroup(groupId, studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/schedules")
    public ResponseEntity<Page<ScheduleResponseDto>> getGroupSchedules(
            @PathVariable Long groupId,
            @ParameterObject @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleByGroup(groupId, pageable));
    }
}
