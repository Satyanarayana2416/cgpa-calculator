package com.example.cgpa.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.cgpa.model.CourseMark;
import com.example.cgpa.model.Student;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class AcademicSummaryServiceTest {

    private final AcademicSummaryService summaries = new AcademicSummaryService();
    private final Student student = new Student("ENR-1", "Test Student", "test@example.com", "BSc");

    @Test
    void calculatesCreditWeightedCgpaAndSemesterGpa() {
        List<CourseMark> marks = List.of(
                new CourseMark(student, 1, "CS101", "Programming", 4, new BigDecimal("95")),
                new CourseMark(student, 1, "MA101", "Mathematics", 2, new BigDecimal("75")),
                new CourseMark(student, 2, "CS201", "Data Structures", 3, new BigDecimal("85")));

        assertThat(summaries.cumulativeGpa(marks)).isEqualByComparingTo("9.22");
        assertThat(summaries.bySemester(marks)).extracting(AcademicSummaryService.SemesterSummary::sgpa)
                .containsExactly(new BigDecimal("9.33"), new BigDecimal("9.00"));
    }
}