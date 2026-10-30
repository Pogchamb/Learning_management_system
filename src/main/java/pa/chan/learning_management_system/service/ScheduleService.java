package pa.chan.learning_management_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pa.chan.learning_management_system.dto.request.ScheduleRequestDto;
import pa.chan.learning_management_system.dto.response.ScheduleResponseDto;
import pa.chan.learning_management_system.entity.Course;
import pa.chan.learning_management_system.entity.Group;
import pa.chan.learning_management_system.entity.Schedule;
import pa.chan.learning_management_system.entity.Teacher;
import pa.chan.learning_management_system.exception.BusinessConflictException;
import pa.chan.learning_management_system.mapper.ScheduleMapper;
import pa.chan.learning_management_system.repository.CourseRepository;
import pa.chan.learning_management_system.repository.GroupRepository;
import pa.chan.learning_management_system.repository.ScheduleRepository;
import pa.chan.learning_management_system.repository.TeacherRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public ScheduleResponseDto getScheduleById(Long id) {
        return scheduleMapper.toDto(scheduleRepository.findWithDetailsByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<ScheduleResponseDto> getScheduleByGroup(Long groupId, Pageable pageable) {
        groupRepository.findByIdOrThrow(groupId);
        return scheduleRepository.findAllByGroupId(groupId, pageable)
                .map(scheduleMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ScheduleResponseDto> getScheduleByTeacher(Long teacherId, Pageable pageable) {
        teacherRepository.findByIdOrThrow(teacherId);
        return scheduleRepository.findAllByTeacherId(teacherId, pageable)
                .map(scheduleMapper::toDto);
    }

    @Transactional
    public ScheduleResponseDto createSchedule(ScheduleRequestDto scheduleRequestDto) {
        Group group = groupRepository.findByIdOrThrow(scheduleRequestDto.groupId());
        Course course = courseRepository.findWithDetailsByIdOrThrow(scheduleRequestDto.courseId());
        Teacher teacher = teacherRepository.findByIdOrThrow(scheduleRequestDto.teacherId());

        validateScheduleRules(scheduleRequestDto, course, null);

        Schedule schedule = scheduleMapper.toEntity(scheduleRequestDto);
        schedule.setCourse(course);
        schedule.setGroup(group);
        schedule.setTeacher(teacher);

        scheduleRepository.save(schedule);
        return scheduleMapper.toDto(schedule);
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(Long id, ScheduleRequestDto scheduleRequestDto) {
        Schedule schedule = scheduleRepository.findWithDetailsByIdOrThrow(id);
        Group group = groupRepository.findByIdOrThrow(scheduleRequestDto.groupId());
        Course course = courseRepository.findWithDetailsByIdOrThrow(scheduleRequestDto.courseId());
        Teacher teacher = teacherRepository.findByIdOrThrow(scheduleRequestDto.teacherId());

        validateScheduleRules(scheduleRequestDto, course, id);

        scheduleMapper.updateEntity(scheduleRequestDto, schedule);
        schedule.setCourse(course);
        schedule.setGroup(group);
        schedule.setTeacher(teacher);

        return scheduleMapper.toDto(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findByIdOrThrow(id);
        scheduleRepository.delete(schedule);
    }

    @Scheduled(cron = "${app.schedule.cleanup-cron:0 0 3 * * *}")
    @Transactional
    public void deleteSchedulesOlderThanOneYear() {
        LocalDateTime threshold = LocalDateTime.now().minusYears(1);
        int deleteCount = scheduleRepository.deleteSchedulesOlderThen(threshold);
    }


    private void validateScheduleRules(ScheduleRequestDto requestDto, Course course, Long scheduleId) {
        if (!course.getTeacher().getId().equals(requestDto.teacherId())) {
            throw new BusinessConflictException(
                    "Курс '" + course.getCourseName() + "' ведет другой преподаватель (id=" + course.getTeacher().getId() + ")"
            );
        }

        if (!courseRepository.existsByIdAndGroupsId(requestDto.courseId(), requestDto.groupId())) {
            throw new BusinessConflictException(
                    "Группа с id " + requestDto.groupId() + " не записана на курс '" + course.getCourseName()
            );
        }

        boolean hasOverlap = scheduleRepository.existsOverlappingSchedule(
                requestDto.groupId(),
                requestDto.teacherId(),
                requestDto.startTime(),
                requestDto.endTime(),
                scheduleId
        );

        if (hasOverlap) {
            throw new BusinessConflictException(
                    "На указанное время у группы или преподавателя уже назначено другое занятие"
            );
        }
    }


}
