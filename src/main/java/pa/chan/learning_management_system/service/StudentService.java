package pa.chan.learning_management_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pa.chan.learning_management_system.dto.request.StudentRequestDto;
import pa.chan.learning_management_system.dto.response.StudentResponseDto;
import pa.chan.learning_management_system.entity.Group;
import pa.chan.learning_management_system.entity.Student;
import pa.chan.learning_management_system.mapper.StudentMapper;
import pa.chan.learning_management_system.repository.GroupRepository;
import pa.chan.learning_management_system.repository.StudentRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentService {

    final private StudentRepository studentRepository;

    final private GroupRepository groupRepository;

    final private StudentMapper studentMapper;


    // TODO Добавление Student
    @Transactional
    public StudentResponseDto createStudent(StudentRequestDto studentRequestDto) {
        Set<Group> foundGroups = groupRepository.findAllByIdsOrThrow(studentRequestDto.groupIds());

        Student student = studentMapper.toEntity(studentRequestDto);
        for (Group group : foundGroups) {
            student.addGroup(group);
        }

        return studentMapper.toDto(studentRepository.save(student));
    }

    // TODO Изменение Student

    @Transactional
    public StudentResponseDto updateStudent(Long id, StudentRequestDto studentRequestDto) {
        Student studentForUpdate = studentRepository.findByIdOrThrow(id);
        Set<Group> foundGroup = groupRepository.findAllByIdsOrThrow(studentRequestDto.groupIds());

        studentMapper.updateEntity(studentRequestDto, studentForUpdate);
        studentForUpdate.getGroups().clear();
        foundGroup.forEach(studentForUpdate::addGroup);

        return studentMapper.toDto(studentForUpdate);
    }

    // TODO deleteStudent
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findByIdOrThrow(id);
        studentRepository.delete(student);
    }

    // TODO Добавление Student в Группы
    @Transactional(readOnly = true)
    public StudentResponseDto getStudent(Long id) {
        return studentMapper.toDto(studentRepository.findWithGroupsByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<StudentResponseDto> getStudents(Pageable pageable) {
        return studentRepository
                .findAll(pageable)
                .map(studentMapper::toDto);
    }


}
