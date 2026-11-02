package pa.chan.learning_management_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pa.chan.learning_management_system.dto.request.CourseRequestDto;
import pa.chan.learning_management_system.dto.response.CourseResponseDto;
import pa.chan.learning_management_system.dto.response.CourseShortResponseDto;
import pa.chan.learning_management_system.entity.Course;

@Mapper(componentModel = "spring", uses = {TeacherMapper.class})
public interface CourseMapper {

    CourseResponseDto toDto(Course course);

    CourseShortResponseDto toShortDto(Course course);

    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "groups", ignore = true)
    Course toEntity(CourseRequestDto courseRequestDto);

    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "groups", ignore = true)
    void updateEntity(CourseRequestDto courseRequestDto, @MappingTarget Course course);
}
