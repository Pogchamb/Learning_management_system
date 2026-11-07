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
import pa.chan.learning_management_system.dto.request.TeacherRequestDto;
import pa.chan.learning_management_system.dto.response.ScheduleResponseDto;
import pa.chan.learning_management_system.dto.response.TeacherResponseDto;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;


public class TeacherControllerIT extends BaseIT {

    private static final String TEACHERS_PATH = "/teachers";

    @Test
    @DisplayName("POST /api/v1/teachers — должен создать преподавателя и вернуть 201 Created")
    void createTeacher_whenValidRequest_shouldReturnCreatedAndPersistInDb() {
        TeacherRequestDto requestDto = new TeacherRequestDto("Teacher-create", "create");

        ResponseEntity<TeacherResponseDto> response = restTemplate.postForEntity(
                getUrl(TEACHERS_PATH),
                requestDto,
                TeacherResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).getPath()).endsWith("/api/v1/teachers/4");
        assertThat(response.getBody())
                .isNotNull()
                .isEqualTo(new TeacherResponseDto(4L, "Teacher-create", "create"));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM teachers WHERE id = 4 AND first_name = 'Teacher-create' AND last_name = 'create'",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("POST /api/v1/teachers — должен вернуть 400 Bad Request при пустом имени или фамилии")
    void createTeacher_whenInvalidRequest_shouldReturnBadRequest() {
        TeacherRequestDto invalidRequest = new TeacherRequestDto(" ", "");

        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                getUrl(TEACHERS_PATH),
                invalidRequest,
                ApiErrorDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().status()).isEqualTo(400);

        Integer totalCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teachers", Integer.class);
        assertThat(totalCount).isEqualTo(3);
    }

    @Test
    @DisplayName("PUT /api/v1/teachers/{id} — должен обновить данные преподавателя и вернуть 200 OK")
    void updateTeacher_whenTeacherExists_shouldReturnUpdatedTeacher() {
        TeacherRequestDto updateRequest = new TeacherRequestDto("Teacher-update", "update");

        ResponseEntity<TeacherResponseDto> response = restTemplate.exchange(
                getUrl(TEACHERS_PATH + "/{id}"),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                TeacherResponseDto.class,
                1L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .isEqualTo(new TeacherResponseDto(1L, "Teacher-update", "update"));

        String updatedLastName = jdbcTemplate.queryForObject(
                "SELECT last_name FROM teachers WHERE id = 1",
                String.class
        );
        assertThat(updatedLastName).isEqualTo("update");
    }

    @Test
    @DisplayName("PUT /api/v1/teachers/{id} — должен вернуть 404 Not Found, если преподаватель не существует")
    void updateTeacher_whenTeacherNotFound_shouldReturnNotFound() {
        TeacherRequestDto updateRequest = new TeacherRequestDto("Teacher-1", "test-1");

        ResponseEntity<ApiErrorDto> response = restTemplate.exchange(
                getUrl(TEACHERS_PATH + "/{id}"),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                ApiErrorDto.class,
                999L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    @DisplayName("DELETE /api/v1/teachers/{id} — должен удалить преподавателя без курсов и расписания (204 No Content)")
    void deleteTeacher_whenNoCoursesOrSchedules_shouldReturnNoContentAndDelete() {
        ResponseEntity<Void> response = restTemplate.exchange(
                getUrl(TEACHERS_PATH + "/{id}"),
                HttpMethod.DELETE,
                null,
                Void.class,
                3L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM teachers WHERE id = 3",
                Integer.class
        );
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("DELETE /api/v1/teachers/{id} — должен вернуть 409 Conflict, если за преподавателем закреплен курс")
    void deleteTeacher_whenTeacherHasAssignedCourse_shouldReturnConflict() {
        ResponseEntity<ApiErrorDto> response = restTemplate.exchange(
                getUrl(TEACHERS_PATH + "/{id}"),
                HttpMethod.DELETE,
                null,
                ApiErrorDto.class,
                1L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().status()).isEqualTo(409);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM teachers WHERE id = 1",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("DELETE /api/v1/teachers/{id} — должен вернуть 404 Not Found при удалении несуществующего преподавателя")
    void deleteTeacher_whenTeacherNotFound_shouldReturnNotFound() {
        ResponseEntity<ApiErrorDto> response = restTemplate.exchange(
                getUrl(TEACHERS_PATH + "/{id}"),
                HttpMethod.DELETE,
                null,
                ApiErrorDto.class,
                10L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    @DisplayName("GET /api/v1/teachers/{id} — должен вернуть преподавателя по ID (200 OK)")
    void getTeacherById_whenTeacherExists_shouldReturnTeacher() {
        ResponseEntity<TeacherResponseDto> response = restTemplate.getForEntity(
                getUrl(TEACHERS_PATH + "/{id}"),
                TeacherResponseDto.class,
                1L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .isEqualTo(new TeacherResponseDto(1L, "Teacher-1", "test-1"));
    }

    @Test
    @DisplayName("GET /api/v1/teachers/{id} — должен вернуть 404 Not Found, если преподаватель не найден")
    void getTeacherById_whenTeacherNotFound_shouldReturnNotFound() {
        ResponseEntity<ApiErrorDto> response = restTemplate.getForEntity(
                getUrl(TEACHERS_PATH + "/{id}"),
                ApiErrorDto.class,
                10L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    @DisplayName("GET /api/v1/teachers — должен вернуть все элементы по умолчанию, отсортированные по фамилии")
    void getTeachers_whenDefaultPageable_shouldReturnAllTeachersSortedByLastNameAsc() {
        ResponseEntity<RestPage<TeacherResponseDto>> response = restTemplate.exchange(
                getUrl(TEACHERS_PATH),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<TeacherResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(20);
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isTrue();
        assertThat(page.getContent())
                .hasSize(3)
                .extracting(TeacherResponseDto::lastName)
                .containsExactly("test-1", "test-2", "test-3");
    }

    @Test
    @DisplayName("GET /api/v1/teachers?page=0&size=2 — должен вернуть первую страницу")
    void getTeachers_whenFirstPageRequested_shouldReturnFirstTwoItemsAndMetadata() {
        ResponseEntity<RestPage<TeacherResponseDto>> response = restTemplate.exchange(
                getUrl(TEACHERS_PATH + "?page=0&size=2"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<TeacherResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isFalse();
        assertThat(page.getContent())
                .hasSize(2)
                .extracting(TeacherResponseDto::lastName)
                .containsExactly("test-1", "test-2");
    }

    @Test
    @DisplayName("GET /api/v1/teachers?page=1&size=2 — должен вернуть последнюю страницу с оставшимся элементом")
    void getTeachers_whenLastPageRequested_shouldReturnRemainingItemAndLastFlag() {
        ResponseEntity<RestPage<TeacherResponseDto>> response = restTemplate.exchange(
                getUrl(TEACHERS_PATH + "?page=1&size=2"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<TeacherResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getSize()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isFalse();
        assertThat(page.isLast()).isTrue();
        assertThat(page.getContent())
                .hasSize(1)
                .extracting(TeacherResponseDto::lastName)
                .containsExactly("test-3");
    }

    @Test
    @DisplayName("GET /api/v1/teachers?page=5&size=2 — должен вернуть пустую страницу при выходе за пределы totalPages")
    void getTeachers_whenPageOutOfBounds_shouldReturnEmptyContent() {
        ResponseEntity<RestPage<TeacherResponseDto>> response = restTemplate.exchange(
                getUrl(TEACHERS_PATH + "?page=5&size=2"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<TeacherResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isEqualTo(5);
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isEmpty()).isTrue();
        assertThat(page.getContent()).isEmpty();
    }

    @Test
    @DisplayName("GET /api/v1/teachers?sort=lastName,desc — должен поддерживать обратную сортировку по фамилии")
    void getTeachers_whenSortedByLastNameDesc_shouldReturnTeachersInReverseOrder() {
        ResponseEntity<RestPage<TeacherResponseDto>> response = restTemplate.exchange(
                getUrl(TEACHERS_PATH + "?sort=lastName,desc"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent())
                .hasSize(3)
                .extracting(TeacherResponseDto::lastName)
                .containsExactly("test-3", "test-2", "test-1");
    }

    @Test
    @DisplayName("GET /api/v1/teachers/{id}/schedules — должен вернуть расписание преподавателя")
    void getTeacherSchedules_whenPaginated_shouldReturnFirstAndLastPagesCorrectly() {
        ResponseEntity<RestPage<ScheduleResponseDto>> firstPageResponse = restTemplate.exchange(
                getUrl(TEACHERS_PATH + "/{id}/schedules?page=0&size=1"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                1L
        );


        ResponseEntity<RestPage<ScheduleResponseDto>> lastPageResponse = restTemplate.exchange(
                getUrl(TEACHERS_PATH + "/{id}/schedules?page=1&size=1"),
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

        RestPage<ScheduleResponseDto> lastPage = lastPageResponse.getBody();
        assertThat(lastPage).isNotNull();
        assertThat(lastPage.isFirst()).isFalse();
        assertThat(lastPage.isLast()).isTrue();
        assertThat(lastPage.getContent())
                .singleElement()
                .extracting(ScheduleResponseDto::startTime)
                .isEqualTo(LocalDateTime.of(2026, 11, 10, 14, 0));
    }

    @Test
    @DisplayName("GET /api/v1/teachers/{id}/schedules — должен вернуть 404 Not Found, если преподаватель не существует")
    void getTeacherSchedules_whenTeacherNotFound_shouldReturnNotFound() {
        ResponseEntity<ApiErrorDto> response = restTemplate.getForEntity(
                getUrl(TEACHERS_PATH + "/{id}/schedules"),
                ApiErrorDto.class,
                10L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
    }

}
