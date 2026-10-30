package pa.chan.learning_management_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pa.chan.learning_management_system.dto.request.ScheduleRequestDto;
import pa.chan.learning_management_system.dto.response.ScheduleResponseDto;
import pa.chan.learning_management_system.entity.Schedule;

@Mapper(componentModel = "spring", uses = {GroupMapper.class, TeacherMapper.class, CourseMapper.class})
public interface ScheduleMapper {

    ScheduleResponseDto toDto(Schedule schedule);

    @Mapping(target = "group", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "course", ignore = true)
    Schedule toEntity(ScheduleRequestDto scheduleRequestDto);

    @Mapping(target = "group", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "course", ignore = true)
    void updateEntity(ScheduleRequestDto scheduleRequestDto, @MappingTarget Schedule schedule);

}
