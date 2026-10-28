package pa.chan.learning_management_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pa.chan.learning_management_system.dto.request.GroupRequestDto;
import pa.chan.learning_management_system.dto.response.GroupResponseDto;
import pa.chan.learning_management_system.entity.Group;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    GroupResponseDto toDto(Group group);


    @Mapping(target = "studentsInGroup", ignore = true)
    Group toEntity(GroupRequestDto groupRequestDto);

}
