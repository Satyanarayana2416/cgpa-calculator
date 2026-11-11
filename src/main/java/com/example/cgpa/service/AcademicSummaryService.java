package com.example.cgpa.service;

import com.example.cgpa.model.CourseMark;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Service;

@Service
public class AcademicSummaryService {

    public BigDecimal cumulativeGpa(List<CourseMark> marks) {
        int totalCredits = marks.stream().mapToInt(CourseMark::getCredits).sum();
        if (totalCredits == 0) return BigDecimal.ZERO.setScale(2);

        int weightedPoints = marks.stream()
                .mapToInt(mark -> mark.getCredits() * mark.getGradePoint())
                .sum();
        return BigDecimal.valueOf(weightedPoints)
                .divide(BigDecimal.valueOf(totalCredits), 2, RoundingMode.HALF_UP);
    }

    public List<SemesterSummary> bySemester(List<CourseMark> marks) {
        Map<Integer, List<CourseMark>> grouped = new TreeMap<>();
        marks.forEach(mark -> grouped.computeIfAbsent(mark.getSemester(), key -> new java.util.ArrayList<>())
                .add(mark));
        return grouped.entrySet().stream()
                .map(entry -> new SemesterSummary(entry.getKey(), entry.getValue().size(),
                        entry.getValue().stream().mapToInt(CourseMark::getCredits).sum(),
                        cumulativeGpa(entry.getValue())))
                .toList();
    }

    public record SemesterSummary(int semester, int courseCount, int credits, BigDecimal sgpa) {
    }
}