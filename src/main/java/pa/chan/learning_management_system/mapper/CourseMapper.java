package pa.chan.learning_management_system.mapper;

import org.mapstruct.Mapper;
import pa.chan.learning_management_system.dto.request.CourseRequestDto;
import pa.chan.learning_management_system.dto.response.CourseResponseDto;
import pa.chan.learning_management_system.entity.Course;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    Course toEntity(CourseRequestDto courseRequestDto);

    CourseResponseDto toDto(Course course);

}
