INSERT INTO teachers (id, first_name, last_name, created_at, updated_at)
VALUES (1, 'Teacher-1', 'test-1', NOW(), NOW()),
       (2, 'Teacher-2', 'test-2', NOW(), NOW()),
       (3, 'Teacher-3', 'test-3', NOW(), NOW());
SELECT setval(pg_get_serial_sequence('teachers', 'id'), (SELECT MAX(id) FROM teachers));

INSERT INTO student_groups (id, group_name, created_at, updated_at)
VALUES (1, 'X-101', NOW(), NOW()),
       (2, 'Y-202', NOW(), NOW()),
       (3, 'Z-303', NOW(), NOW());
SELECT setval(pg_get_serial_sequence('student_groups', 'id'), (SELECT MAX(id) FROM student_groups));

INSERT INTO students (id, first_name, last_name, created_at, updated_at)
VALUES (1, 'Student-1', 'test-1', NOW(), NOW()),
       (2, 'Student-2', 'test-2', NOW(), NOW()),
       (3, 'Student-3', 'test-3', NOW(), NOW());
SELECT setval(pg_get_serial_sequence('students', 'id'), (SELECT MAX(id) FROM students));

INSERT INTO student_group_members (student_id, group_id)
VALUES (1, 1),
       (2, 1),
       (2, 2),
       (3, 3);

INSERT INTO courses (id, course_name, description, teacher_id, created_at, updated_at)
VALUES (1, 'Course-1', 'test-1', 1, NOW(), NOW()),
       (2, 'Course-2', 'test-2', 2, NOW(), NOW());
SELECT setval(pg_get_serial_sequence('courses', 'id'), (SELECT MAX(id) FROM courses));

INSERT INTO course_groups (course_id, group_id)
VALUES (1, 1),
       (2, 2);

INSERT INTO schedules (id, group_id, teacher_id, course_id, start_time, end_time, created_at, updated_at)
VALUES (1, 1, 1, 1, '2026-11-10 14:00:00', '2026-11-10 15:30:00', NOW(), NOW()),
       (2, 1, 1, 1, '2026-11-10 10:00:00', '2026-11-10 11:30:00', NOW(), NOW());
SELECT setval(pg_get_serial_sequence('schedules', 'id'), (SELECT MAX(id) FROM schedules));