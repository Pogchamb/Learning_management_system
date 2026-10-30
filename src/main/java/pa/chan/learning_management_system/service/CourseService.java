package pa.chan.learning_management_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pa.chan.learning_management_system.dto.request.CourseRequestDto;
import pa.chan.learning_management_system.dto.response.CourseResponseDto;
import pa.chan.learning_management_system.entity.Course;
import pa.chan.learning_management_system.entity.Group;
import pa.chan.learning_management_system.entity.Teacher;
import pa.chan.learning_management_system.exception.BusinessConflictException;
import pa.chan.learning_management_system.mapper.CourseMapper;
import pa.chan.learning_management_system.repository.CourseRepository;
import pa.chan.learning_management_system.repository.GroupRepository;
import pa.chan.learning_management_system.repository.ScheduleRepository;
import pa.chan.learning_management_system.repository.TeacherRepository;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final GroupRepository groupRepository;
    private final ScheduleRepository scheduleRepository;
    private final CourseMapper courseMapper;

    // TODO create
    @Transactional
    public CourseResponseDto createCourse(CourseRequestDto courseRequestDto) {
        if (courseRepository.existsByCourseNameIgnoreCase(courseRequestDto.courseName())) {
            throw new BusinessConflictException("Курс с названием '" + courseRequestDto.courseName() + "' уже существует");
        }

        Teacher teacher = teacherRepository.findByIdOrThrow(courseRequestDto.teacherId());
        Course newCourse = courseMapper.toEntity(courseRequestDto);
        newCourse.setTeacher(teacher);

        courseRepository.save(newCourse);
        return courseMapper.toDto(newCourse);
    }

    // TODO update
    @Transactional
    public CourseResponseDto updateCourse(Long id, CourseRequestDto courseRequestDto) {
        Course courseForUpdate = courseRepository.findWithDetailsByIdOrThrow(id);
        if (courseRepository.existsByCourseNameIgnoreCaseAndIdNot(courseRequestDto.courseName(), id)) {
            throw new BusinessConflictException("Курс с названием '" + courseRequestDto.courseName() + "' уже существует");
        }

        Teacher teacher = teacherRepository.findByIdOrThrow(courseRequestDto.teacherId());
        courseMapper.updateEntity(courseRequestDto, courseForUpdate);
        courseForUpdate.setTeacher(teacher);

        return courseMapper.toDto(courseForUpdate);
    }

    // TODO delete
    @Transactional
    public void deleteCourse(Long id) {
        Course courseToDelete = courseRepository.findByIdOrThrow(id);

        if (scheduleRepository.existsByCourseId(id)) {
            throw new BusinessConflictException(
                    "Невозможно удалить курс с id " + id + ", так как по нему есть занятия в расписании"
            );
        }

        courseRepository.delete(courseToDelete);
    }

    // TODO
    @Transactional(readOnly = true)
    public CourseResponseDto getCourse(Long id) {
        return courseMapper.toDto(courseRepository.findWithDetailsByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<CourseResponseDto> getCourses(Pageable pageable) {
        return courseRepository
                .findAll(pageable)
                .map(courseMapper::toDto);
    }

    @Transactional
    public CourseResponseDto addGroupToCourse(Long courseId, Long groupId) {
        Course course = courseRepository.findWithDetailsByIdOrThrow(courseId);
        Group group = groupRepository.findByIdOrThrow(groupId);

        course.addGroup(group);
        return courseMapper.toDto(course);
    }

    @Transactional
    public CourseResponseDto removeGroupFromCourse(Long courseId, Long groupId) {
        Course course = courseRepository.findWithDetailsByIdOrThrow(courseId);
        Group group = groupRepository.findByIdOrThrow(groupId);

        if (!course.getGroups().contains(group)) {
            throw new BusinessConflictException(
                    "Группа с id " + groupId + " не записана на курс с id " + courseId
            );
        }

        course.removeGroup(group);
        return courseMapper.toDto(course);
    }

}
