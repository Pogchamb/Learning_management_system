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
import pa.chan.learning_management_system.dto.request.StudentRequestDto;
import pa.chan.learning_management_system.dto.response.GroupShortResponseDto;
import pa.chan.learning_management_system.dto.response.StudentResponseDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentControllerIT extends BaseIT {
    private static final String STUDENTS_PATH = "/students";

    @Test
    @DisplayName("POST /api/v1/students — должен создать студента, привязать к группе и вернуть 201 Created")
    void createStudent_whenValidRequest_shouldReturnCreatedAndPersistStudent() {
        StudentRequestDto requestDto = new StudentRequestDto(
                "Student-1",
                "test-1",
                Set.of(1L)
        );


        ResponseEntity<StudentResponseDto> response = restTemplate.postForEntity(
                getUrl(STUDENTS_PATH),
                requestDto,
                StudentResponseDto.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().getPath()).endsWith("/api/v1/students/4");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(4L);
        assertThat(response.getBody().firstName()).isEqualTo("Student-1");
        assertThat(response.getBody().lastName()).isEqualTo("test-1");
        assertThat(response.getBody().groups())
                .containsExactly(new GroupShortResponseDto(1L, "X-101"));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM students WHERE id = 4 AND first_name = 'Student-1' AND last_name = 'test-1'",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("POST /api/v1/students — должен вернуть 400 Bad Request, если список groupIds пуст")
    void createStudent_whenEmptyGroups_shouldReturnBadRequest() {
        StudentRequestDto invalidRequest = new StudentRequestDto(
                "Student-1",
                "test-1",
                Set.of()
        );

        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                getUrl(STUDENTS_PATH),
                invalidRequest,
                ApiErrorDto.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    @DisplayName("POST /api/v1/students — должен вернуть 404 Not Found, если указанная группа не существует")
    void createStudent_whenGroupNotFound_shouldReturnNotFound() {
        StudentRequestDto requestDto = new StudentRequestDto(
                "Student-1",
                "test-1",
                Set.of(10L)
        );

        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                getUrl(STUDENTS_PATH),
                requestDto,
                ApiErrorDto.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    @DisplayName("PUT /api/v1/students/{id} — должен обновить данные студента и список его групп (200 OK)")
    void updateStudent_whenValidRequest_shouldUpdateStudentAndGroups() {
        StudentRequestDto updateRequest = new StudentRequestDto(
                "Student_Upd",
                "Student_Last_Upd",
                Set.of(2L)
        );


        ResponseEntity<StudentResponseDto> response = restTemplate.exchange(
                getUrl(STUDENTS_PATH + "/{id}"),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                StudentResponseDto.class,
                1L
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().firstName()).isEqualTo("Student_Upd");
        assertThat(response.getBody().lastName()).isEqualTo("Student_Last_Upd");
        assertThat(response.getBody().groups())
                .containsExactly(new GroupShortResponseDto(2L, "Y-202"));

        Integer groupCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM student_group_members WHERE student_id = 1 AND group_id = 2",
                Integer.class
        );
        assertThat(groupCount).isEqualTo(1);
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id} — должен удалить студента (204 No Content)")
    void deleteStudent_whenStudentExists_shouldReturnNoContentAndDelete() {
        ResponseEntity<Void> response = restTemplate.exchange(
                getUrl(STUDENTS_PATH + "/{id}"),
                HttpMethod.DELETE,
                null,
                Void.class,
                3L
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM students WHERE id = 3",
                Integer.class
        );
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} — должен вернуть студента со списком его групп (200 OK)")
    void getStudentById_whenStudentExists_shouldReturnStudentWithGroups() {
        ResponseEntity<StudentResponseDto> response = restTemplate.getForEntity(
                getUrl(STUDENTS_PATH + "/{id}"),
                StudentResponseDto.class,
                2L
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(2L);
        assertThat(response.getBody().firstName()).isEqualTo("Student-2");
        assertThat(response.getBody().lastName()).isEqualTo("test-2");
        assertThat(response.getBody().groups())
                .extracting(GroupShortResponseDto::id)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("GET /api/v1/students — должен вернуть всех студентов по умолчанию, отсортированных по фамилии")
    void getAllStudents_whenDefaultPageable_shouldReturnAllStudentsSortedByLastNameAsc() {
        ResponseEntity<RestPage<StudentResponseDto>> response = restTemplate.exchange(
                getUrl(STUDENTS_PATH),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<StudentResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(20);
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isTrue();
        assertThat(page.getContent())
                .hasSize(3)
                .extracting(StudentResponseDto::lastName)
                .containsExactly("test-1", "test-2", "test-3");
    }

    @Test
    @DisplayName("GET /api/v1/students?page=0&size=2 — должен вернуть первую страницу студентов")
    void getAllStudents_whenFirstPageRequested_shouldReturnFirstTwoStudentsAndMetadata() {
        ResponseEntity<RestPage<StudentResponseDto>> response = restTemplate.exchange(
                getUrl(STUDENTS_PATH + "?page=0&size=2"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<StudentResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isFalse();
        assertThat(page.getContent())
                .hasSize(2)
                .extracting(StudentResponseDto::lastName)
                .containsExactly("test-1", "test-2");
    }

    @Test
    @DisplayName("GET /api/v1/students?page=1&size=2 — должен вернуть последнюю страницу с оставшимся студентом")
    void getAllStudents_whenLastPageRequested_shouldReturnRemainingStudentAndLastFlag() {
        ResponseEntity<RestPage<StudentResponseDto>> response = restTemplate.exchange(
                getUrl(STUDENTS_PATH + "?page=1&size=2"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<StudentResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getSize()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isFalse();
        assertThat(page.isLast()).isTrue();
        assertThat(page.getContent())
                .singleElement()
                .extracting(StudentResponseDto::lastName)
                .isEqualTo("test-3");
    }

    @Test
    @DisplayName("GET /api/v1/students?page=5&size=2 — должен вернуть пустую страницу")
    void getAllStudents_whenPageOutOfBounds_shouldReturnEmptyContent() {
        ResponseEntity<RestPage<StudentResponseDto>> response = restTemplate.exchange(
                getUrl(STUDENTS_PATH + "?page=5&size=2"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<StudentResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isEqualTo(5);
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isEmpty()).isTrue();
        assertThat(page.getContent()).isEmpty();
    }

    @Test
    @DisplayName("GET /api/v1/students?sort=lastName,обратную сортировку по фамилии")
    void getAllStudents_whenSortedByLastNameDesc_shouldReturnStudentsInReverseOrder() {
        ResponseEntity<RestPage<StudentResponseDto>> response = restTemplate.exchange(
                getUrl(STUDENTS_PATH + "?sort=lastName,desc"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent())
                .hasSize(3)
                .extracting(StudentResponseDto::lastName)
                .containsExactly("test-3", "test-2", "test-1");
    }
}
