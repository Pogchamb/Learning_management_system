package pa.chan.learning_management_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pa.chan.learning_management_system.dto.request.GroupRequestDto;
import pa.chan.learning_management_system.dto.response.GroupResponseDto;
import pa.chan.learning_management_system.dto.response.GroupShortResponseDto;
import pa.chan.learning_management_system.entity.Group;

@Mapper(componentModel = "spring", uses = {CourseMapper.class})
public interface GroupMapper {

    GroupResponseDto toDto(Group group);

    GroupShortResponseDto toShortDto(Group group);

    @Mapping(target = "studentsInGroup", ignore = true)
    @Mapping(target = "courses", ignore = true)
    Group toEntity(GroupRequestDto groupRequestDto);

    @Mapping(target = "studentsInGroup", ignore = true)
    @Mapping(target = "courses", ignore = true)
    void updateEntity(GroupRequestDto groupRequestDto, @MappingTarget Group group);
}
