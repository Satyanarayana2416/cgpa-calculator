package com.example.cgpa.web;

import com.example.cgpa.model.AppUser;
import com.example.cgpa.model.Role;
import com.example.cgpa.model.Student;
import com.example.cgpa.repository.AppUserRepository;
import com.example.cgpa.repository.StudentRepository;
import com.example.cgpa.web.form.RegistrationForm;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final StudentRepository students;
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(StudentRepository students, AppUserRepository users,
                                  PasswordEncoder passwordEncoder) {
        this.students = students;
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    String register(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    String createAccount(@Valid @ModelAttribute RegistrationForm registrationForm,
                         BindingResult result, RedirectAttributes redirectAttributes) {
        if (registrationForm.getPassword() == null
                || !registrationForm.getPassword().equals(registrationForm.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match.");
        }
        if (result.hasErrors()) return "register";

        Student student = students.findByEnrollmentIdIgnoreCase(registrationForm.getEnrollmentId().trim())
                .orElse(null);
        if (student == null || !student.getEmail().equalsIgnoreCase(registrationForm.getEmail().trim())) {
            result.reject("registration.invalidStudent", "Enrollment ID and email do not match a student record.");
        } else if (users.existsByUsernameIgnoreCase(student.getEnrollmentId())) {
            result.reject("registration.exists", "An account already exists for this enrollment ID.");
        }

        if (result.hasErrors()) return "register";

        users.save(new AppUser(student.getEnrollmentId(), passwordEncoder.encode(registrationForm.getPassword()),
                Role.STUDENT, student));
        redirectAttributes.addFlashAttribute("registered", true);
        return "redirect:/login";
    }
}