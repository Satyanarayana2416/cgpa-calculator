package com.example.cgpa.service;

import java.math.BigDecimal;

public final class GradeScale {

    private GradeScale() {
    }

    public static int pointsFor(BigDecimal marks) {
        if (marks.compareTo(BigDecimal.valueOf(90)) >= 0) return 10;
        if (marks.compareTo(BigDecimal.valueOf(80)) >= 0) return 9;
        if (marks.compareTo(BigDecimal.valueOf(70)) >= 0) return 8;
        if (marks.compareTo(BigDecimal.valueOf(60)) >= 0) return 7;
        if (marks.compareTo(BigDecimal.valueOf(50)) >= 0) return 6;
        if (marks.compareTo(BigDecimal.valueOf(45)) >= 0) return 5;
        if (marks.compareTo(BigDecimal.valueOf(40)) >= 0) return 4;
        return 0;
    }

    public static String letterFor(BigDecimal marks) {
        if (marks.compareTo(BigDecimal.valueOf(90)) >= 0) return "O";
        if (marks.compareTo(BigDecimal.valueOf(80)) >= 0) return "A+";
        if (marks.compareTo(BigDecimal.valueOf(70)) >= 0) return "A";
        if (marks.compareTo(BigDecimal.valueOf(60)) >= 0) return "B+";
        if (marks.compareTo(BigDecimal.valueOf(50)) >= 0) return "B";
        if (marks.compareTo(BigDecimal.valueOf(45)) >= 0) return "C";
        if (marks.compareTo(BigDecimal.valueOf(40)) >= 0) return "P";
        return "F";
    }
}