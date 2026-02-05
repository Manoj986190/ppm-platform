package com.ppm.backend.config;

import com.ppm.backend.entity.Role;
import com.ppm.backend.repository.RoleRepository;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        if (roleRepository.findByRoleName("ROLE_USER").isEmpty()) {
            Role role = new Role();
            role.setRoleName("ROLE_USER");
            role.setDescription("Default user role");
            roleRepository.save(role);
        }
    }
}

