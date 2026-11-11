package com.example.cgpa.repository;

import com.example.cgpa.model.CourseMark;
import com.example.cgpa.model.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseMarkRepository extends JpaRepository<CourseMark, Long> {

    List<CourseMark> findByStudentOrderBySemesterAscCourseCodeAsc(Student student);

    List<CourseMark> findByStudentId(Long studentId);

    Optional<CourseMark> findByStudentAndSemesterAndCourseCodeIgnoreCase(
            Student student, int semester, String courseCode);
}