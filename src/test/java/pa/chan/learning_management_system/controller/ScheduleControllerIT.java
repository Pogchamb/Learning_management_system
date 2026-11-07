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
import pa.chan.learning_management_system.dto.request.ScheduleRequestDto;
import pa.chan.learning_management_system.dto.response.ScheduleResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduleControllerIT extends BaseIT {
    private static final String SCHEDULES_PATH = "/schedules";

    @Test
    @DisplayName("POST /api/v1/schedules — должен создать слот в расписании и вернуть 201 Created")
    void createSchedule_whenValidRequest_shouldReturnCreatedAndPersistSchedule() {
        LocalDateTime start = LocalDateTime.of(2026, 11, 20, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 11, 20, 11, 30);
        ScheduleRequestDto requestDto = new ScheduleRequestDto(
                1L,
                1L,
                1L,
                start,
                end
        );


        ResponseEntity<ScheduleResponseDto> response = restTemplate.postForEntity(
                getUrl(SCHEDULES_PATH),
                requestDto,
                ScheduleResponseDto.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().getPath()).endsWith("/api/v1/schedules/3");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(3L);
        assertThat(response.getBody().group().id()).isEqualTo(1L);
        assertThat(response.getBody().teacher().id()).isEqualTo(1L);
        assertThat(response.getBody().course().id()).isEqualTo(1L);
        assertThat(response.getBody().startTime()).isEqualTo(start);
        assertThat(response.getBody().endTime()).isEqualTo(end);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM schedules WHERE id = 3",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("POST /api/v1/schedules — должен вернуть 400 Bad Request, если endTime <= startTime")
    void createSchedule_whenEndTimeNotAfterStartTime_shouldReturnBadRequest() {
        ScheduleRequestDto invalidRequest = new ScheduleRequestDto(
                1L,
                1L,
                1L,
                LocalDateTime.of(2026, 11, 20, 12, 0),
                LocalDateTime.of(2026, 11, 20, 10, 0)
        );


        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                getUrl(SCHEDULES_PATH),
                invalidRequest,
                ApiErrorDto.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    @DisplayName("POST /api/v1/schedules — должен вернуть 409 Conflict, если курс ведет другой преподаватель")
    void createSchedule_whenTeacherDoesNotTeachCourse_shouldReturnConflict() {
        ScheduleRequestDto requestDto = new ScheduleRequestDto(
                1L,
                2L,
                1L,
                LocalDateTime.of(2026, 11, 20, 10, 0),
                LocalDateTime.of(2026, 11, 20, 11, 30)
        );


        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                getUrl(SCHEDULES_PATH),
                requestDto,
                ApiErrorDto.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("POST /api/v1/schedules — должен вернуть 409 Conflict, если группа не записана на курс")
    void createSchedule_whenGroupIsNotEnrolledInCourse_shouldReturnConflict() {
        ScheduleRequestDto requestDto = new ScheduleRequestDto(
                2L,
                1L,
                1L,
                LocalDateTime.of(2026, 11, 20, 10, 0),
                LocalDateTime.of(2026, 11, 20, 11, 30)
        );


        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                getUrl(SCHEDULES_PATH),
                requestDto,
                ApiErrorDto.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("POST /api/v1/schedules — должен вернуть 409 Conflict при пересечении времени занятия")
    void createSchedule_whenTimeOverlapsWithExistingSchedule_shouldReturnConflict() {
        ScheduleRequestDto overlappingRequest = new ScheduleRequestDto(
                1L,
                1L,
                1L,
                LocalDateTime.of(2026, 11, 10, 11, 0),
                LocalDateTime.of(2026, 11, 10, 12, 30)
        );


        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                getUrl(SCHEDULES_PATH),
                overlappingRequest,
                ApiErrorDto.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("PUT /api/v1/schedules/{id} — должен обновить расписание (200 OK)")
    void updateSchedule_whenValidRequest_shouldUpdateScheduleAndReturnOk() {
        LocalDateTime newStart = LocalDateTime.of(2026, 11, 10, 10, 30);
        LocalDateTime newEnd = LocalDateTime.of(2026, 11, 10, 12, 0);

        ScheduleRequestDto updateRequest = new ScheduleRequestDto(
                1L,
                1L,
                1L,
                newStart,
                newEnd
        );


        ResponseEntity<ScheduleResponseDto> response = restTemplate.exchange(
                getUrl(SCHEDULES_PATH + "/{id}"),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                ScheduleResponseDto.class,
                2L
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(2L);
        assertThat(response.getBody().startTime()).isEqualTo(newStart);
        assertThat(response.getBody().endTime()).isEqualTo(newEnd);
    }

    @Test
    @DisplayName("DELETE /api/v1/schedules/{id} — должен удалить занятие из расписания (204 No Content)")
    void deleteSchedule_whenScheduleExists_shouldReturnNoContentAndDelete() {
        ResponseEntity<Void> response = restTemplate.exchange(
                getUrl(SCHEDULES_PATH + "/{id}"),
                HttpMethod.DELETE,
                null,
                Void.class,
                1L
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM schedules WHERE id = 1",
                Integer.class
        );
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("GET /api/v1/schedules/{id} — должен вернуть занятие по ID (200 OK)")
    void getScheduleById_whenScheduleExists_shouldReturnSchedule() {
        ResponseEntity<ScheduleResponseDto> response = restTemplate.getForEntity(
                getUrl(SCHEDULES_PATH + "/{id}"),
                ScheduleResponseDto.class,
                2L
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(2L);
        assertThat(response.getBody().group().id()).isEqualTo(1L);
        assertThat(response.getBody().teacher().id()).isEqualTo(1L);
        assertThat(response.getBody().course().id()).isEqualTo(1L);
        assertThat(response.getBody().startTime()).isEqualTo(LocalDateTime.of(2026, 11, 10, 10, 0));
        assertThat(response.getBody().endTime()).isEqualTo(LocalDateTime.of(2026, 11, 10, 11, 30));
    }

    @Test
    @DisplayName("GET /api/v1/schedules — должен вернуть все занятия по умолчанию, отсортированные по startTime")
    void getAllSchedules_whenDefaultPageable_shouldReturnAllSchedulesSortedByStartTimeAsc() {
        ResponseEntity<RestPage<ScheduleResponseDto>> response = restTemplate.exchange(
                getUrl(SCHEDULES_PATH),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<ScheduleResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(20);
        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isTrue();
        assertThat(page.getContent())
                .hasSize(2)
                .extracting(ScheduleResponseDto::startTime)
                .containsExactly(
                        LocalDateTime.of(2026, 11, 10, 10, 0),
                        LocalDateTime.of(2026, 11, 10, 14, 0)
                );
    }

    @Test
    @DisplayName("GET /api/v1/schedules?page=0&size=1 — должен вернуть первую страницу расписания")
    void getAllSchedules_whenFirstPageRequested_shouldReturnFirstScheduleAndMetadata() {
        ResponseEntity<RestPage<ScheduleResponseDto>> response = restTemplate.exchange(
                getUrl(SCHEDULES_PATH + "?page=0&size=1"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<ScheduleResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(1);
        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isFalse();
        assertThat(page.getContent())
                .singleElement()
                .extracting(ScheduleResponseDto::startTime)
                .isEqualTo(LocalDateTime.of(2026, 11, 10, 10, 0));
    }

    @Test
    @DisplayName("GET /api/v1/schedules?page=1&size=1 — должен вернуть последнюю страницу расписания")
    void getAllSchedules_whenLastPageRequested_shouldReturnLastScheduleAndMetadata() {
        ResponseEntity<RestPage<ScheduleResponseDto>> response = restTemplate.exchange(
                getUrl(SCHEDULES_PATH + "?page=1&size=1"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<ScheduleResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getSize()).isEqualTo(1);
        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isFalse();
        assertThat(page.isLast()).isTrue();
        assertThat(page.getContent())
                .singleElement()
                .extracting(ScheduleResponseDto::startTime)
                .isEqualTo(LocalDateTime.of(2026, 11, 10, 14, 0));
    }

    @Test
    @DisplayName("GET /api/v1/schedules?page=5&size=1 — должен вернуть пустую страницу")
    void getAllSchedules_whenPageOutOfBounds_shouldReturnEmptyContent() {
        ResponseEntity<RestPage<ScheduleResponseDto>> response = restTemplate.exchange(
                getUrl(SCHEDULES_PATH + "?page=5&size=1"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RestPage<ScheduleResponseDto> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getNumber()).isEqualTo(5);
        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isEmpty()).isTrue();
        assertThat(page.getContent()).isEmpty();
    }

    @Test
    @DisplayName("GET /api/v1/schedules?sort=startTime,desc,обратная сортировка по времени начала")
    void getAllSchedules_whenSortedByStartTimeDesc_shouldReturnSchedulesInReverseOrder() {
        ResponseEntity<RestPage<ScheduleResponseDto>> response = restTemplate.exchange(
                getUrl(SCHEDULES_PATH + "?sort=startTime,desc"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent())
                .hasSize(2)
                .extracting(ScheduleResponseDto::startTime)
                .containsExactly(
                        LocalDateTime.of(2026, 11, 10, 14, 0),
                        LocalDateTime.of(2026, 11, 10, 10, 0)
                );
    }
}
