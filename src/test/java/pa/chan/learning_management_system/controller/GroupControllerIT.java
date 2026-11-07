package pa.chan.learning_management_system.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pa.chan.learning_management_system.BaseIT;
import pa.chan.learning_management_system.dto.ApiErrorDto;
import pa.chan.learning_management_system.dto.RestPage;
import pa.chan.learning_management_system.dto.request.GroupRequestDto;
import pa.chan.learning_management_system.dto.response.GroupResponseDto;
import pa.chan.learning_management_system.dto.response.ScheduleResponseDto;
import pa.chan.learning_management_system.dto.response.StudentShortResponseDto;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupControllerIT extends BaseIT {
    private static final String GROUPS_PATH = "/groups";

    @Test
    @DisplayName("POST /api/v1/groups — должен создать новую группу и вернуть 201 Created")
    void createGroup_whenValidRequest_shouldReturnCreatedAndPersistGroup() {
        GroupRequestDto requestDto = new GroupRequestDto("Test_g_4");

        ResponseEntity<GroupResponseDto> response = restTemplate.postForEntity(
                getUrl(GROUPS_PATH),
                requestDto,
                GroupResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).getPath()).endsWith("/api/v1/groups/4");
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().id()).isEqualTo(4L);
        assertThat(response.getBody().groupName()).isEqualTo("Test_g_4");
        assertThat(response.getBody().studentsInGroup().isEmpty());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM student_groups WHERE id = 4 AND group_name = 'Test_g_4'",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("POST /api/v1/groups — должен вернуть 409 Conflict при дублировании названия группы")
    void createGroup_whenDuplicateGroupName_shouldReturnConflict() {
        GroupRequestDto duplicateRequest = new GroupRequestDto("X-101");

        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                getUrl(GROUPS_PATH),
                duplicateRequest,
                ApiErrorDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("POST /api/v1/groups — должен вернуть 400 Bad Request при пустом названии группы")
    void createGroup_whenBlankName_shouldReturnBadRequest() {
        GroupRequestDto invalidRequest = new GroupRequestDto("   ");

        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                getUrl(GROUPS_PATH),
                invalidRequest,
                ApiErrorDto.class
        );

        assert response.getBody() != null;
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    @DisplayName("PUT /api/v1/groups/{id} — должен обновить название группы и вернуть 200 OK")
    void updateGroup_whenValidRequest_shouldUpdateGroupAndReturnOk() {
        GroupRequestDto updateRequest = new GroupRequestDto("X-102");

        ResponseEntity<GroupResponseDto> response = restTemplate.exchange(
                getUrl(GROUPS_PATH + "/{id}"),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                GroupResponseDto.class,
                1L
        );


        assert response.getBody() != null;
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().groupName()).isEqualTo("X-102");

        String nameInDb = jdbcTemplate.queryForObject(
                "SELECT group_name FROM student_groups WHERE id = 1",
                String.class
        );
        assertThat(nameInDb).isEqualTo("X-102");
    }

    @Test
    @DisplayName("DELETE /api/v1/groups/{id} — должен удалить группу, если ее студенты состоят и в других группах и нет расписания (204 No Content)")
    void deleteGroup_whenStudentsHaveOtherGroupsAndNoSchedules_shouldReturnNoContentAndDelete() {
        ResponseEntity<Void> response = restTemplate.exchange(
                getUrl(GROUPS_PATH + "/{id}"),
                HttpMethod.DELETE,
                null,
                Void.class,
                2L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM student_groups WHERE id = 2",
                Integer.class
        );
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("DELETE /api/v1/groups/{id} — должен вернуть 409 Conflict, если группа является единственной у студента")
    void deleteGroup_whenStudentHasOnlyThisGroup_shouldReturnConflict() {
        ResponseEntity<ApiErrorDto> response = restTemplate.exchange(
                getUrl(GROUPS_PATH + "/{id}"),
                HttpMethod.DELETE,
                null,
                ApiErrorDto.class,
                3L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().status()).isEqualTo(409);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM student_groups WHERE id = 3",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("DELETE /api/v1/groups/{id} — должен вернуть 409 Conflict, если для группы назначено расписание")
    void deleteGroup_whenGroupHasSchedule_shouldReturnConflict() {
        ResponseEntity<ApiErrorDto> response = restTemplate.exchange(
                getUrl(GROUPS_PATH + "/{id}"),
                HttpMethod.DELETE,
                null,
                ApiErrorDto.class,
                1L
        );

        assert response.getBody() != null;
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("GET /api/v1/groups/{id} — должен вернуть группу со списком студентов (200 OK)")
    void getGroupById_whenGroupExists_shouldReturnGroupWithStudents() {
        ResponseEntity<GroupResponseDto> response = restTemplate.getForEntity(
                getUrl(GROUPS_PATH + "/{id}"),
                GroupResponseDto.class,
                1L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().groupName()).isEqualTo("X-101");
        assertThat(response.getBody().studentsInGroup())
                .extracting(StudentShortResponseDto::id)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("POST /api/v1/groups/{groupId}/students/{studentId} — должен добавить студента в группу (204 No Content)")
    void addStudentToGroup_whenGroupAndStudentExist_shouldAddStudentAndReturnNoContent() {
        ResponseEntity<Void> response = restTemplate.postForEntity(
                getUrl(GROUPS_PATH + "/{groupId}/students/{studentId}"),
                null,
                Void.class,
                2L,
                1L
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM student_group_members WHERE student_id = 1 AND group_id = 2",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("DELETE /api/v1/groups/{groupId}/students/{studentId} — должен исключить студента из группы, если у него больше 1 группы (204 No Content)")
    void removeStudentFromGroup_whenStudentHasMultipleGroups_shouldRemoveAndReturnNoContent() {
        ResponseEntity<Void> response = restTemplate.exchange(
                getUrl(GROUPS_PATH + "/{groupId}/students/{studentId}"),
                HttpMethod.DELETE,
                null,
                Void.class,
                2L,
                2L
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM student_group_members WHERE student_id = 2 AND group_id = 2",
                Integer.class
        );
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("DELETE /api/v1/groups/{groupId}/students/{studentId} — должен вернуть 409 Conflict при попытке удалить студента из его единственной группы")
    void removeStudentFromGroup_whenStudentHasOnlyOneGroup_shouldReturnConflict() {
        ResponseEntity<ApiErrorDto> response = restTemplate.exchange(
                getUrl(GROUPS_PATH + "/{groupId}/students/{studentId}"),
                HttpMethod.DELETE,
                null,
                ApiErrorDto.class,
                1L,
                1L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("GET /api/v1/groups — должен вернуть все группы по умолчанию")
    void getAllGroups_whenDefaultPageable_shouldReturnAllGroupsSortedByGroupNameAsc() {
        ResponseEntity<RestPage<GroupResponseDto>> response = restTemplate.exchange(
                getUrl(GROUPS_PATH),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<GroupResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(20);
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isTrue();
        assertThat(page.getContent())
                .hasSize(3)
                .extracting(GroupResponseDto::groupName)
                .containsExactly("X-101", "Y-202", "Z-303");
    }

    @Test
    @DisplayName("GET /api/v1/groups?page=0&size=2 — должен вернуть первую страницу групп")
    void getAllGroups_whenFirstPageRequested_shouldReturnFirstTwoGroupsAndMetadata() {
        ResponseEntity<RestPage<GroupResponseDto>> response = restTemplate.exchange(
                getUrl(GROUPS_PATH + "?page=0&size=2"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<GroupResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isFalse();
        assertThat(page.getContent())
                .hasSize(2)
                .extracting(GroupResponseDto::groupName)
                .containsExactly("X-101", "Y-202");
    }

    @Test
    @DisplayName("GET /api/v1/groups?page=1&size=2 — должен вернуть последнюю страницу")
    void getAllGroups_whenLastPageRequested_shouldReturnRemainingGroupAndLastFlag() {
        ResponseEntity<RestPage<GroupResponseDto>> response = restTemplate.exchange(
                getUrl(GROUPS_PATH + "?page=1&size=2"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<GroupResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getSize()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isFalse();
        assertThat(page.isLast()).isTrue();
        assertThat(page.getContent())
                .singleElement()
                .extracting(GroupResponseDto::groupName)
                .isEqualTo("Z-303");
    }

    @Test
    @DisplayName("GET /api/v1/groups?page=5&size=2 — должен вернуть пустую страницу")
    void getAllGroups_whenPageOutOfBounds_shouldReturnEmptyContent() {
        ResponseEntity<RestPage<GroupResponseDto>> response = restTemplate.exchange(
                getUrl(GROUPS_PATH + "?page=5&size=2"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<GroupResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isEqualTo(5);
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isEmpty()).isTrue();
        assertThat(page.getContent()).isEmpty();
    }

    @Test
    @DisplayName("GET /api/v1/groups?sort=groupName, должен поддерживать обратную сортировку по названию группы")
    void getAllGroups_whenSortedByGroupNameDesc_shouldReturnGroupsInReverseOrder() {
        ResponseEntity<RestPage<GroupResponseDto>> response = restTemplate.exchange(
                getUrl(GROUPS_PATH + "?sort=groupName,desc"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent())
                .hasSize(3)
                .extracting(GroupResponseDto::groupName)
                .containsExactly("Z-303", "Y-202", "X-101");
    }


    @Test
    @DisplayName("GET /api/v1/groups/{groupId}/schedules — должен корректно вывести 1 страницу")
    void getGroupSchedules_whenPaginated_shouldReturnFirstPageCorrectly() {
        ResponseEntity<RestPage<ScheduleResponseDto>> firstPageResponse = restTemplate.exchange(
                getUrl(GROUPS_PATH + "/{groupId}/schedules?page=0&size=1"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                1L
        );

        RestPage<ScheduleResponseDto> firstPage = firstPageResponse.getBody();
        assertThat(firstPage).isNotNull();
        assertThat(firstPage.isFirst()).isTrue();
        assertThat(firstPage.isLast()).isFalse();
        assertThat(firstPage.getTotalElements()).isEqualTo(2L);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.getContent())
                .singleElement()
                .extracting(ScheduleResponseDto::startTime)
                .isEqualTo(LocalDateTime.of(2026, 11, 10, 10, 0));
    }

    @Test
    @DisplayName("GET /api/v1/groups/{groupId}/schedules — должен корректно вывести последнюю страницу")
    void getGroupSchedules_whenPaginated_shouldReturnLastPageCorrectly() {
        ResponseEntity<RestPage<ScheduleResponseDto>> lastPageResponse = restTemplate.exchange(
                getUrl(GROUPS_PATH + "/{groupId}/schedules?page=1&size=1"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                1L
        );

        RestPage<ScheduleResponseDto> lastPage = lastPageResponse.getBody();
        assertThat(lastPage).isNotNull();
        assertThat(lastPage.isFirst()).isFalse();
        assertThat(lastPage.isLast()).isTrue();
        assertThat(lastPage.getContent())
                .singleElement()
                .extracting(ScheduleResponseDto::startTime)
                .isEqualTo(LocalDateTime.of(2026, 11, 10, 14, 0));
    }


}
