package com.example.cgpa.web;

import com.example.cgpa.model.Student;
import com.example.cgpa.repository.CourseMarkRepository;
import com.example.cgpa.repository.StudentRepository;
import com.example.cgpa.service.AcademicSummaryService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class StudentController {

    private final StudentRepository students;
    private final CourseMarkRepository marks;
    private final AcademicSummaryService summaries;

    public StudentController(StudentRepository students, CourseMarkRepository marks,
                              AcademicSummaryService summaries) {
        this.students = students;
        this.marks = marks;
        this.summaries = summaries;
    }

    @GetMapping("/student/dashboard")
    String dashboard(Authentication authentication, Model model) {
        Student student = students.findByEnrollmentIdIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var studentMarks = marks.findByStudentOrderBySemesterAscCourseCodeAsc(student);
        model.addAttribute("student", student);
        model.addAttribute("marks", studentMarks);
        model.addAttribute("semesters", summaries.bySemester(studentMarks));
        model.addAttribute("cgpa", summaries.cumulativeGpa(studentMarks));
        model.addAttribute("totalCredits", studentMarks.stream().mapToInt(mark -> mark.getCredits()).sum());
        model.addAttribute("courseCount", studentMarks.size());
        return "student/dashboard";
    }
}