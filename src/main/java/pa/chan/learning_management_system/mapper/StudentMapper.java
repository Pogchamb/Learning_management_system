package pa.chan.learning_management_system.mapper;

import org.mapstruct.Mapper;
import pa.chan.learning_management_system.dto.request.StudentRequestDto;
import pa.chan.learning_management_system.dto.response.StudentResponseDto;
import pa.chan.learning_management_system.entity.Student;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentResponseDto toDto(Student student);

    Student toEntity(StudentRequestDto studentRequestDto);

}
