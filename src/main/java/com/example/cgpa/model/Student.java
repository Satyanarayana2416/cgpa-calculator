package com.example.cgpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String enrollmentId;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(nullable = false, length = 120)
    private String program;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Student() {
    }

    public Student(String enrollmentId, String fullName, String email, String program) {
        this.enrollmentId = enrollmentId;
        this.fullName = fullName;
        this.email = email;
        this.program = program;
    }

    @PrePersist
    void setCreatedAt() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getProgram() {
        return program;
    }
}