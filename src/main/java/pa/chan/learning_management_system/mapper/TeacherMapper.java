package pa.chan.learning_management_system.mapper;

import org.mapstruct.Mapper;
import pa.chan.learning_management_system.dto.request.TeacherRequestDto;
import pa.chan.learning_management_system.dto.response.TeacherResponseDto;
import pa.chan.learning_management_system.entity.Teacher;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    TeacherResponseDto toDto(Teacher teacher);

    Teacher toEntity(TeacherRequestDto teacherRequestDto);
}
