package pa.chan.learning_management_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pa.chan.learning_management_system.dto.request.TeacherRequestDto;
import pa.chan.learning_management_system.dto.response.TeacherResponseDto;
import pa.chan.learning_management_system.entity.Teacher;
import pa.chan.learning_management_system.exception.BusinessConflictException;
import pa.chan.learning_management_system.mapper.TeacherMapper;
import pa.chan.learning_management_system.repository.CourseRepository;
import pa.chan.learning_management_system.repository.ScheduleRepository;
import pa.chan.learning_management_system.repository.TeacherRepository;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    private final CourseRepository courseRepository;

    private final ScheduleRepository scheduleRepository;

    private final TeacherMapper teacherMapper;

    @Transactional
    public TeacherResponseDto createTeacher(TeacherRequestDto teacherRequestDto) {
        Teacher teacher = teacherMapper.toEntity(teacherRequestDto);
        teacherRepository.save(teacher);
        return teacherMapper.toDto(teacher);
    }

    @Transactional
    public TeacherResponseDto updateStudent(Long id, TeacherRequestDto teacherRequestDto) {
        Teacher teacherForUpdate = teacherRepository.findByIdOrThrow(id);
        teacherMapper.updateEntity(teacherRequestDto, teacherForUpdate);
        return teacherMapper.toDto(teacherForUpdate);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacherToDelete = teacherRepository.findByIdOrThrow(id);

        if (courseRepository.existsByTeacherId(teacherToDelete.getId())) {
            throw new BusinessConflictException(
                    "Невозможно удалить преподавателя с id " + id + ", так как за ним закреплены курсы"
            );
        }
        if (scheduleRepository.existsByTeacherId(id)) {
            throw new BusinessConflictException(
                    "Невозможно удалить преподавателя с id " + id + ", так как он числится в расписании"
            );
        }

        teacherRepository.delete(teacherToDelete);
    }

    @Transactional(readOnly = true)
    public TeacherResponseDto getTeacher(Long id) {
        return teacherMapper.toDto(teacherRepository.findByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<TeacherResponseDto> getTeachers(Pageable pageable) {
        return teacherRepository
                .findAll(pageable)
                .map(teacherMapper::toDto);
    }

}
