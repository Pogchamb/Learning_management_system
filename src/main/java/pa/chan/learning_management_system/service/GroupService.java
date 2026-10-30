package pa.chan.learning_management_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pa.chan.learning_management_system.dto.request.GroupRequestDto;
import pa.chan.learning_management_system.dto.response.GroupResponseDto;
import pa.chan.learning_management_system.entity.Course;
import pa.chan.learning_management_system.entity.Group;
import pa.chan.learning_management_system.entity.Student;
import pa.chan.learning_management_system.exception.BusinessConflictException;
import pa.chan.learning_management_system.mapper.GroupMapper;
import pa.chan.learning_management_system.repository.GroupRepository;
import pa.chan.learning_management_system.repository.ScheduleRepository;
import pa.chan.learning_management_system.repository.StudentRepository;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupMapper groupMapper;


    // TODO добавить группу
    @Transactional
    public GroupResponseDto createGroup(GroupRequestDto groupRequestDto) {
        if (groupRepository.existsByGroupNameIgnoreCase(groupRequestDto.groupName())) {
            throw new BusinessConflictException("Группа с названием '" + groupRequestDto.groupName() + "' уже существует");
        }

        Group newGroup = groupMapper.toEntity(groupRequestDto);
        groupRepository.save(newGroup);
        return groupMapper.toDto(newGroup);
    }

    // TODO изменить группу
    @Transactional
    public GroupResponseDto updateGroup(Long id, GroupRequestDto groupRequestDto) {
        Group groupForUpdate = groupRepository.findByIdOrThrow(id);
        if (groupRepository.existsByGroupNameIgnoreCaseAndIdNot(groupRequestDto.groupName(), id)) {
            throw new BusinessConflictException("Группа с названием '" + groupRequestDto.groupName() + "' уже существует");
        }

        groupMapper.updateEntity(groupRequestDto, groupForUpdate);
        return groupMapper.toDto(groupForUpdate);
    }

    // TODO удалить группу
    @Transactional
    public void deleteGroup(Long id) {
        Group groupToDelete = groupRepository.findByIdOrThrow(id);

        if (scheduleRepository.existsByGroupId(id)) {
            throw new BusinessConflictException(
                    "Невозможно удалить группу с id " + id + ", так как для нее назначено расписание"
            );
        }

        if (groupRepository.studentHasOnlyOneGroup(id)) {
            throw new BusinessConflictException(
                    "Невозможно удалить группу с id " + id + ": в ней есть студенты, не состоящие в других группах"
            );
        }

        for (Student student : new HashSet<>(groupToDelete.getStudentsInGroup())) {
            student.removeGroup(groupToDelete);
        }

        for (Course course : new HashSet<>(groupToDelete.getCourses())) {
            course.removeGroup(groupToDelete);
        }

        groupRepository.delete(groupToDelete);
    }

    // TODO
    @Transactional(readOnly = true)
    public GroupResponseDto getGroup(Long id) {
        return groupMapper.toDto(groupRepository.findByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<GroupResponseDto> getGroups(Pageable pageable) {
        return groupRepository
                .findAll(pageable)
                .map(groupMapper::toDto);
    }

    @Transactional
    public void addStudentToGroup(Long groupId, Long studentId) {
        Group group = groupRepository.findByIdOrThrow(groupId);
        Student student = studentRepository.findWithGroupsByIdOrThrow(studentId);

        if (!student.getGroups().contains(group)) {
            student.addGroup(group);
        }
    }

    @Transactional
    public void removeStudentsFromGroup(Long groupId, Long studentId) {
        Group group = groupRepository.findByIdOrThrow(groupId);
        Student student = studentRepository.findWithGroupsByIdOrThrow(studentId);

        if (!student.getGroups().contains(group)) {
            throw new BusinessConflictException(
                    "Студент с id " + studentId + " не состоит в группе с id " + groupId
            );
        }

        if (student.getGroups().size() <= 1) {
            throw new BusinessConflictException(
                    "Нельзя удалить студента из его единственной группы"
            );
        }

        student.removeGroup(group);
    }


}
