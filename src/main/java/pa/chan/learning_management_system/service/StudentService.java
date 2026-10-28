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

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    final private StudentRepository studentRepository;

    final private GroupRepository groupRepository;

    final private StudentMapper studentMapper;


    // TODO Добавление Student
    @Transactional
    public StudentResponseDto createStudent(StudentRequestDto studentRequestDto) {
        List<Group> foundGroup = groupRepository.findGroupByIdsOrThrow(studentRequestDto.groupIds());

        Student newStudent = new Student();
        newStudent.setFirstName(studentRequestDto.firstName());
        newStudent.setLastName(studentRequestDto.lastName());
        newStudent.addGroups(foundGroup);

        return studentMapper.toDto(studentRepository.save(newStudent));
    }

    // TODO Изменение Student

    @Transactional
    public StudentResponseDto updateStudent(Long id, StudentRequestDto studentRequestDto) {
        Student studentForUpdate = studentRepository.findStudentByIdOrThrow(id);
        List<Group> foundGroup = groupRepository.findGroupByIdsOrThrow(studentRequestDto.groupIds());

        studentForUpdate.setFirstName(studentRequestDto.firstName());
        studentForUpdate.setLastName(studentRequestDto.lastName());
        studentForUpdate.addGroups(foundGroup);

        return studentMapper.toDto(studentForUpdate);
    }

    // TODO deleteStudent
    @Transactional
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    // TODO Добавление Student в Группы
    @Transactional(readOnly = true)
    public StudentResponseDto getStudent(Long id) {
        return studentMapper.toDto(studentRepository.findStudentByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<StudentResponseDto> getStudents(Pageable pageable) {
        return studentRepository
                .findAll(pageable)
                .map(studentMapper::toDto);
    }


}
