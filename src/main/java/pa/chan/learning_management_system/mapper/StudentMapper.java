package pa.chan.learning_management_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pa.chan.learning_management_system.dto.request.StudentRequestDto;
import pa.chan.learning_management_system.dto.response.StudentResponseDto;
import pa.chan.learning_management_system.entity.Student;

@Mapper(componentModel = "spring", uses = {GroupMapper.class})
public interface StudentMapper {

    StudentResponseDto toDto(Student student);

    @Mapping(target = "groups", ignore = true)
    Student toEntity(StudentRequestDto studentRequestDto);

    @Mapping(target = "groups", ignore = true)
    void updateEntity(StudentRequestDto studentRequestDto, @MappingTarget Student student);
}
