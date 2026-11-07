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
import pa.chan.learning_management_system.dto.request.CourseRequestDto;
import pa.chan.learning_management_system.dto.response.CourseResponseDto;
import pa.chan.learning_management_system.dto.response.GroupShortResponseDto;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CourseControllerIT extends BaseIT {
    private static final String COURSES_PATH = "/courses";

    @Test
    @DisplayName("POST /api/v1/courses — должен создать курс и вернуть 201 Created")
    void createCourse_whenValidRequest_shouldReturnCreatedAndPersistCourse() {
        CourseRequestDto requestDto = new CourseRequestDto(
                "Course_create",
                "Course_create",
                1L
        );

        ResponseEntity<CourseResponseDto> response = restTemplate.postForEntity(
                getUrl(COURSES_PATH),
                requestDto,
                CourseResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).getPath()).endsWith("/api/v1/courses/3");
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().id()).isEqualTo(3L);
        assertThat(response.getBody().courseName()).isEqualTo("Course_create");
        assertThat(response.getBody().description()).isEqualTo("Course_create");
        assertThat(response.getBody().teacher().id()).isEqualTo(1L);
        assertThat(response.getBody().groups().isEmpty());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM courses WHERE id = 3 AND course_name = 'Course_create'",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("POST /api/v1/courses — должен вернуть 409 Conflict при дублировании названия курса")
    void createCourse_whenDuplicateCourseName_shouldReturnConflict() {
        CourseRequestDto duplicateRequest = new CourseRequestDto(
                "Course-1",
                "test-1",
                1L
        );

        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                getUrl(COURSES_PATH),
                duplicateRequest,
                ApiErrorDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().status()).isEqualTo(409);

        Integer totalCourses = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM courses", Integer.class);
        assertThat(totalCourses).isEqualTo(2);
    }

    @Test
    @DisplayName("POST /api/v1/courses — должен вернуть 404 Not Found, если преподаватель не существует")
    void createCourse_whenTeacherNotFound_shouldReturnNotFound() {
        CourseRequestDto requestDto = new CourseRequestDto("Not", "Found", 999L);

        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                getUrl(COURSES_PATH),
                requestDto,
                ApiErrorDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    @DisplayName("POST /api/v1/courses — должен вернуть 400 Bad Request при невалидных данных")
    void createCourse_whenInvalidRequest_shouldReturnBadRequest() {
        CourseRequestDto invalidRequest = new CourseRequestDto("", "description", null);

        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                getUrl(COURSES_PATH),
                invalidRequest,
                ApiErrorDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    @DisplayName("PUT /api/v1/courses/{id} — должен обновить курс и сменить преподавателя (200 OK)")
    void updateCourse_whenValidRequest_shouldUpdateCourseAndTeacher() {
        CourseRequestDto updateRequest = new CourseRequestDto(
                "Course_update",
                "Course_update_description",
                2L
        );

        ResponseEntity<CourseResponseDto> response = restTemplate.exchange(
                getUrl(COURSES_PATH + "/{id}"),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                CourseResponseDto.class,
                1L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().courseName()).isEqualTo("Course_update");
        assertThat(response.getBody().description()).isEqualTo("Course_update_description");
        assertThat(response.getBody().teacher().id()).isEqualTo(2L);

        Long teacherIdInDb = jdbcTemplate.queryForObject(
                "SELECT teacher_id FROM courses WHERE id = 1",
                Long.class
        );
        assertThat(teacherIdInDb).isEqualTo(2L);
    }

    @Test
    @DisplayName("PUT /api/v1/courses/{id} — должен вернуть 409 Conflict при переименовании в уже существующий курс")
    void updateCourse_whenNameConflictsWithAnotherCourse_shouldReturnConflict() {
        CourseRequestDto conflictRequest = new CourseRequestDto(
                "Course-1",
                "test-1",
                1L
        );

        ResponseEntity<ApiErrorDto> response = restTemplate.exchange(
                getUrl(COURSES_PATH + "/{id}"),
                HttpMethod.PUT,
                new HttpEntity<>(conflictRequest),
                ApiErrorDto.class,
                2L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("DELETE /api/v1/courses/{id} — должен удалить курс без занятий в расписании")
    void deleteCourse_whenNoSchedules_shouldReturnNoContentAndDelete() {
        ResponseEntity<Void> response = restTemplate.exchange(
                getUrl(COURSES_PATH + "/{id}"),
                HttpMethod.DELETE,
                null,
                Void.class,
                2L
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM courses WHERE id = 2",
                Integer.class
        );
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("DELETE /api/v1/courses/{id} — должен вернуть 409 Conflict, если по курсу есть занятия в расписании")
    void deleteCourse_whenCourseHasSchedules_shouldReturnConflict() {
        ResponseEntity<ApiErrorDto> response = restTemplate.exchange(
                getUrl(COURSES_PATH + "/{id}"),
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
                "SELECT COUNT(*) FROM courses WHERE id = 1",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("GET /api/v1/courses/{id} — должен вернуть курс по ID вместе с преподавателем и записанными группами")
    void getCourseById_whenCourseExists_shouldReturnCourseWithTeacherAndGroups() {
        ResponseEntity<CourseResponseDto> response = restTemplate.getForEntity(
                getUrl(COURSES_PATH + "/{id}"),
                CourseResponseDto.class,
                1L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().courseName()).isEqualTo("Course-1");
        assertThat(response.getBody().teacher().id()).isEqualTo(1L);
        assertThat(response.getBody().groups().contains(new GroupShortResponseDto(1L, "X-101")));
    }

    @Test
    @DisplayName("GET /api/v1/courses/{id} — должен вернуть 404 Not Found, если курс не найден")
    void getCourseById_whenCourseNotFound_shouldReturnNotFound() {
        ResponseEntity<ApiErrorDto> response = restTemplate.getForEntity(
                getUrl(COURSES_PATH + "/{id}"),
                ApiErrorDto.class,
                10L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    @DisplayName("POST /api/v1/courses/{courseId}/groups/{groupId} — должен записать группу на курс (200 OK)")
    void addGroupToCourse_whenCourseAndGroupExist_shouldEnrollGroupAndReturnUpdatedCourse() {
        ResponseEntity<CourseResponseDto> response = restTemplate.postForEntity(
                getUrl(COURSES_PATH + "/{courseId}/groups/{groupId}"),
                null,
                CourseResponseDto.class,
                1L,
                2L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assert response.getBody() != null;
        List<Long> groupIds = response.getBody().groups().stream()
                .map(GroupShortResponseDto::id)
                .collect(Collectors.toList());
        assertThat(groupIds).containsExactlyInAnyOrder(1L, 2L);


        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM course_groups WHERE course_id = 1 AND group_id = 2",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("DELETE /api/v1/courses/{courseId}/groups/{groupId} — должен открепить группу от курса (200 OK)")
    void removeGroupFromCourse_whenGroupIsEnrolled_shouldRemoveGroupAndReturnUpdatedCourse() {
        ResponseEntity<CourseResponseDto> response = restTemplate.exchange(
                getUrl(COURSES_PATH + "/{courseId}/groups/{groupId}"),
                HttpMethod.DELETE,
                null,
                CourseResponseDto.class,
                1L,
                1L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().groups()).isEmpty();

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM course_groups WHERE course_id = 1 AND group_id = 1",
                Integer.class
        );
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("DELETE /api/v1/courses/{courseId}/groups/{groupId} — должен вернуть 409 Conflict, если группа не записана на курс")
    void removeGroupFromCourse_whenGroupNotEnrolled_shouldReturnConflict() {
        ResponseEntity<ApiErrorDto> response = restTemplate.exchange(
                getUrl(COURSES_PATH + "/{courseId}/groups/{groupId}"),
                HttpMethod.DELETE,
                null,
                ApiErrorDto.class,
                1L,
                3L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("GET /api/v1/courses — должен вернуть все курсы")
    void getAllCourses_whenDefaultPageable_shouldReturnAllCoursesSortedByCourseName() {
        ResponseEntity<RestPage<CourseResponseDto>> response = restTemplate.exchange(
                getUrl(COURSES_PATH),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<CourseResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(20);
        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isTrue();
        assertThat(page.getContent())
                .hasSize(2)
                .extracting(CourseResponseDto::courseName)
                .containsExactly("Course-1", "Course-2");
    }

    @Test
    @DisplayName("GET /api/v1/courses?page=0&size=1 — должен вернуть первую страницу курсов")
    void getAllCourses_whenFirstPageRequested_shouldReturnFirstCourseAndMetadata() {
        ResponseEntity<RestPage<CourseResponseDto>> response = restTemplate.exchange(
                getUrl(COURSES_PATH + "?page=0&size=1"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<CourseResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(1);
        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isFalse();
        assertThat(page.getContent())
                .singleElement()
                .extracting(CourseResponseDto::courseName)
                .isEqualTo("Course-1");
    }

    @Test
    @DisplayName("GET /api/v1/courses?page=1&size=1 — должен вернуть последнюю страницу курсов")
    void getAllCourses_whenLastPageRequested_shouldReturnLastCourseAndMetadata() {
        ResponseEntity<RestPage<CourseResponseDto>> response = restTemplate.exchange(
                getUrl(COURSES_PATH + "?page=1&size=1"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<CourseResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getSize()).isEqualTo(1);
        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isFalse();
        assertThat(page.isLast()).isTrue();
        assertThat(page.getContent())
                .singleElement()
                .extracting(CourseResponseDto::courseName)
                .isEqualTo("Course-2");
    }

    @Test
    @DisplayName("GET /api/v1/courses?page=5&size=1 — должен вернуть пустую страницу")
    void getAllCourses_whenPageOutOfBounds_shouldReturnEmptyContent() {
        ResponseEntity<RestPage<CourseResponseDto>> response = restTemplate.exchange(
                getUrl(COURSES_PATH + "?page=5&size=1"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<CourseResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isEqualTo(5);
        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isEmpty()).isTrue();
        assertThat(page.getContent()).isEmpty();
    }

    @Test
    @DisplayName("GET /api/v1/courses?sort=courseName,desc — должен поддерживать обратную сортировку по названию курса")
    void getAllCourses_whenSortedByCourseNameDesc_shouldReturnCoursesInReverseOrder() {
        ResponseEntity<RestPage<CourseResponseDto>> response = restTemplate.exchange(
                getUrl(COURSES_PATH + "?sort=courseName,desc"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent())
                .hasSize(2)
                .extracting(CourseResponseDto::courseName)
                .containsExactly("Course-2", "Course-1");
    }

}


