package com.example.cgpa.model;

import com.example.cgpa.service.GradeScale;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "course_marks", uniqueConstraints = @UniqueConstraint(
        columnNames = {"student_id", "semester", "course_code"}))
public class CourseMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Student student;

    @Column(nullable = false)
    private int semester;

    @Column(nullable = false, length = 20)
    private String courseCode;

    @Column(nullable = false, length = 120)
    private String courseName;

    @Column(nullable = false)
    private int credits;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal marks;

    protected CourseMark() {
    }

    public CourseMark(Student student, int semester, String courseCode, String courseName,
                      int credits, BigDecimal marks) {
        this.student = student;
        this.semester = semester;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.marks = marks;
    }

    public void update(String courseName, int credits, BigDecimal marks) {
        this.courseName = courseName;
        this.credits = credits;
        this.marks = marks;
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public int getSemester() {
        return semester;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCredits() {
        return credits;
    }

    public BigDecimal getMarks() {
        return marks;
    }

    public int getGradePoint() {
        return GradeScale.pointsFor(marks);
    }

    public String getLetterGrade() {
        return GradeScale.letterFor(marks);
    }
}