package com.example.cgpa.repository;

import com.example.cgpa.model.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEnrollmentIdIgnoreCase(String enrollmentId);

    Optional<Student> findByEmailIgnoreCase(String email);

    List<Student> findTop8ByOrderByCreatedAtDesc();

    List<Student> findTop8ByEnrollmentIdContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrderByFullName(
            String enrollmentId, String fullName);
}