package com.example.cgpa.web;

import com.example.cgpa.model.CourseMark;
import com.example.cgpa.model.Student;
import com.example.cgpa.repository.CourseMarkRepository;
import com.example.cgpa.repository.StudentRepository;
import com.example.cgpa.service.AcademicSummaryService;
import com.example.cgpa.web.form.MarkForm;
import com.example.cgpa.web.form.StudentForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentRepository students;
    private final CourseMarkRepository marks;
    private final AcademicSummaryService summaries;

    public AdminController(StudentRepository students, CourseMarkRepository marks,
                           AcademicSummaryService summaries) {
        this.students = students;
        this.marks = marks;
        this.summaries = summaries;
    }

    @GetMapping
    String dashboard(@RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("studentCount", students.count());
        model.addAttribute("courseCount", marks.count());
        model.addAttribute("query", q);
        model.addAttribute("students", q.isBlank()
                ? students.findTop8ByOrderByCreatedAtDesc()
                : students.findTop8ByEnrollmentIdContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrderByFullName(
                        q.trim(), q.trim()));
        return "admin/dashboard";
    }

    @GetMapping("/students/new")
    String newStudent(Model model) {
        model.addAttribute("studentForm", new StudentForm());
        return "admin/student-new";
    }

    @PostMapping("/students")
    String createStudent(@Valid @ModelAttribute StudentForm studentForm, BindingResult result) {
        String enrollmentId = studentForm.getEnrollmentId() == null ? "" : studentForm.getEnrollmentId().trim();
        String email = studentForm.getEmail() == null ? "" : studentForm.getEmail().trim();
        if (!enrollmentId.isBlank() && students.findByEnrollmentIdIgnoreCase(enrollmentId).isPresent()) {
            result.rejectValue("enrollmentId", "student.duplicateEnrollment", "This enrollment ID already exists.");
        }
        if (!email.isBlank() && students.findByEmailIgnoreCase(email).isPresent()) {
            result.rejectValue("email", "student.duplicateEmail", "This email is already assigned to a student.");
        }
        if (result.hasErrors()) return "admin/student-new";

        students.save(new Student(enrollmentId, studentForm.getFullName().trim(), email,
                studentForm.getProgram().trim()));
        return "redirect:/admin?created";
    }

    @GetMapping("/students/{id}")
    String studentDetail(@PathVariable Long id, Model model) {
        return populateStudentDetail(id, new MarkForm(), model);
    }

    @PostMapping("/students/{id}/marks")
    String saveMark(@PathVariable Long id, @Valid @ModelAttribute MarkForm markForm,
                    BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        Student student = getStudent(id);
        if (result.hasErrors()) {
            populateStudentDetail(student, markForm, model);
            return "admin/student-detail";
        }

        String code = markForm.getCourseCode().trim();
        CourseMark mark = marks.findByStudentAndSemesterAndCourseCodeIgnoreCase(
                        student, markForm.getSemester(), code)
                .orElseGet(() -> new CourseMark(student, markForm.getSemester(), code,
                        markForm.getCourseName().trim(), markForm.getCredits(), markForm.getMarks()));
        mark.update(markForm.getCourseName().trim(), markForm.getCredits(), markForm.getMarks());
        marks.save(mark);
        redirectAttributes.addFlashAttribute("markSaved", true);
        return "redirect:/admin/students/" + id;
    }

    private String populateStudentDetail(Long id, MarkForm form, Model model) {
        return populateStudentDetail(getStudent(id), form, model);
    }

    private String populateStudentDetail(Student student, MarkForm form, Model model) {
        var studentMarks = marks.findByStudentOrderBySemesterAscCourseCodeAsc(student);
        model.addAttribute("student", student);
        model.addAttribute("marks", studentMarks);
        model.addAttribute("semesters", summaries.bySemester(studentMarks));
        model.addAttribute("cgpa", summaries.cumulativeGpa(studentMarks));
        model.addAttribute("markForm", form);
        return "admin/student-detail";
    }

    private Student getStudent(Long id) {
        return students.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}