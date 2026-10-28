package pa.chan.learning_management_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pa.chan.learning_management_system.dto.request.GroupRequestDto;
import pa.chan.learning_management_system.dto.response.GroupResponseDto;
import pa.chan.learning_management_system.entity.Group;
import pa.chan.learning_management_system.mapper.GroupMapper;
import pa.chan.learning_management_system.repository.GroupRepository;
import pa.chan.learning_management_system.repository.StudentRepository;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final GroupMapper groupMapper;


    // TODO добавить группу
    public GroupResponseDto createGroup(GroupRequestDto groupRequestDto) {
        Group newGroup = Group.builder()
                .groupName(groupRequestDto.groupName())
                .build();
        groupRepository.save(newGroup);

        return groupMapper.toDto(newGroup);
    }

    // TODO изменить группу
    public GroupResponseDto updateGroup(Long id, GroupRequestDto groupRequestDto) {
        Group groupForUpdate = groupRepository.findGroupByIdOrThrow(id);

        groupForUpdate.setGroupName(groupRequestDto.groupName());

        return groupMapper.toDto(groupForUpdate);
    }

    // TODO удалить группу
    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    // TODO
    @Transactional(readOnly = true)
    public GroupResponseDto getGroup(Long id) {
        return groupMapper.toDto(groupRepository.findGroupByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<GroupResponseDto> getGroups(Pageable pageable) {
        return groupRepository
                .findAll(pageable)
                .map(groupMapper::toDto);
    }


}
