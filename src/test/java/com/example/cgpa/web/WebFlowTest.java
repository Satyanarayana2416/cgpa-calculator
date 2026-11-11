package com.example.cgpa.web;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.example.cgpa.model.CourseMark;
import com.example.cgpa.model.Student;
import com.example.cgpa.repository.CourseMarkRepository;
import com.example.cgpa.repository.StudentRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:cgpa-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "app.admin.username=test-admin",
        "app.admin.password=test-password"
})
@AutoConfigureMockMvc
class WebFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository students;

    @Autowired
    private CourseMarkRepository marks;

    private Student student;

    @BeforeEach
    void setUp() {
        marks.deleteAll();
        students.deleteAll();
        student = students.save(new Student("ENR-2026-01", "Avery Student", "avery@example.edu",
                "BSc Computer Science"));
    }

    @Test
    void publicPagesRenderAndAdminRoutesRequireAuthentication() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
        mockMvc.perform(get("/register")).andExpect(status().isOk()).andExpect(view().name("register"));
        mockMvc.perform(get("/admin")).andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/register").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    @WithMockUser(username = "test-admin", roles = "ADMIN")
    void adminDashboardRendersForAdmins() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(authenticated().withUsername("test-admin"));
        mockMvc.perform(get("/admin/students/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/student-new"));
        mockMvc.perform(get("/admin/students/" + student.getId()))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/student-detail"));
        mockMvc.perform(post("/admin/students").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/student-new"));
    }

    @Test
    @WithMockUser(username = "ENR-2026-01", roles = "STUDENT")
    void studentCanViewOwnCreditWeightedTranscript() throws Exception {
        marks.save(new CourseMark(student, 1, "CS101", "Programming", 3, new BigDecimal("95")));

        mockMvc.perform(get("/student/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/dashboard"));
        mockMvc.perform(get("/admin"))
            .andExpect(status().isForbidden());
    }
}