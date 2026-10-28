package pa.chan.learning_management_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pa.chan.learning_management_system.dto.request.CourseRequestDto;
import pa.chan.learning_management_system.dto.response.CourseResponseDto;
import pa.chan.learning_management_system.entity.Course;
import pa.chan.learning_management_system.mapper.CourseMapper;
import pa.chan.learning_management_system.repository.CourseRepository;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    // TODO create
    @Transactional
    public CourseResponseDto createCourse(CourseRequestDto courseRequestDto) {
        Course newCourse = courseMapper.toEntity(courseRequestDto);
        courseRepository.save(newCourse);

        return courseMapper.toDto(newCourse);
    }

    // TODO update
    @Transactional
    public CourseResponseDto updateCourse(Long id, CourseRequestDto courseRequestDto) {
        Course courseForUpdate = courseRepository.findCourseByIdOrThrow(id);
        courseForUpdate.setCourseName(courseRequestDto.courseName());
        courseForUpdate.setDescription(courseRequestDto.description());

        return courseMapper.toDto(courseForUpdate);
    }

    // TODO delete
    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    // TODO
    @Transactional(readOnly = true)
    public CourseResponseDto getCourse(Long id) {
        return courseMapper.toDto(courseRepository.findCourseByIdOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<CourseResponseDto> getCourses(Pageable pageable) {
        return courseRepository
                .findAll(pageable)
                .map(courseMapper::toDto);
    }

}
