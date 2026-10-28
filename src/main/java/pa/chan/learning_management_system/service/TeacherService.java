package pa.chan.learning_management_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pa.chan.learning_management_system.dto.request.TeacherRequestDto;
import pa.chan.learning_management_system.dto.response.TeacherResponseDto;
import pa.chan.learning_management_system.entity.Teacher;
import pa.chan.learning_management_system.mapper.TeacherMapper;
import pa.chan.learning_management_system.repository.TeacherRepository;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    // TODO Добавление
    @Transactional
    public TeacherResponseDto createTeacher(TeacherRequestDto teacherRequestDto) {

        Teacher newTeacher = Teacher.builder()
                .firstName(teacherRequestDto.firstName())
                .lastName(teacherRequestDto.lastName())
                .build();
        teacherRepository.save(newTeacher);
        return teacherMapper.toDto(newTeacher);
    }

    // TODO Изменение

    @Transactional
    public TeacherResponseDto updateStudent(Long id, TeacherRequestDto teacherRequestDto) {
        Teacher teacherForUpdate = teacherRepository.findTeacherByIdOrThrow(id);

        teacherForUpdate.setFirstName(teacherRequestDto.firstName());
        teacherForUpdate.setLastName(teacherRequestDto.lastName());

        return teacherMapper.toDto(teacherForUpdate);
    }

    // TODO delete
    @Transactional
    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }

    // TODO
    @Transactional(readOnly = true)
    public TeacherResponseDto getTeacher(Long id) {
        return teacherMapper.toDto(teacherRepository.findTeacherByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<TeacherResponseDto> getTeachers(Pageable pageable) {
        return teacherRepository
                .findAll(pageable)
                .map(teacherMapper::toDto);
    }

}
