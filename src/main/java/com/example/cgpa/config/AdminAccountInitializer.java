package com.example.cgpa.config;

import com.example.cgpa.model.AppUser;
import com.example.cgpa.model.Role;
import com.example.cgpa.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminAccountInitializer.class);

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminAccountInitializer(AppUserRepository users, PasswordEncoder passwordEncoder,
                                   @Value("${app.admin.username:}") String username,
                                   @Value("${app.admin.password:}") String password) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (username.isBlank() || password.isBlank()) {
            log.warn("No admin account configured. Set ADMIN_USERNAME and ADMIN_PASSWORD to enable admin access.");
            return;
        }
        if (!users.existsByUsernameIgnoreCase(username)) {
            users.save(new AppUser(username, passwordEncoder.encode(password), Role.ADMIN, null));
            log.info("Created admin account '{}'.", username);
        }
    }
}