package com.ppm.backend.service;

import com.ppm.backend.dto.LoginRequest;
import com.ppm.backend.dto.UserDTO;
import com.ppm.backend.entity.Role;
import com.ppm.backend.entity.User;
import com.ppm.backend.entity.UserRole;
import com.ppm.backend.repository.RoleRepository;
import com.ppm.backend.repository.UserRepository;
import com.ppm.backend.repository.UserRoleRepository;
import com.ppm.backend.security.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SystemLogService systemLogService;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            SystemLogService systemLogService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.systemLogService = systemLogService;
    }

    // CREATE USER + bcrypt + ROLE_USER assign
    public User createUser(User user) {

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // 🔐 Encrypt password
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        // Save user first
        User savedUser = userRepository.save(user);

        // 🔑 Assign default role
        Role role = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(role);

        userRoleRepository.save(userRole);

        return savedUser;
    }

    // LOGIN + AUDIT LOGGING
    public String login(LoginRequest request, String ipAddress) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    systemLogService.log(
                            request.getUsername(),
                            "LOGIN_FAILED",
                            "FAILED",
                            "/api/auth/login",
                            ipAddress
                    );
                    return new RuntimeException("Invalid username or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {

            systemLogService.log(
                    user.getUsername(),
                    "LOGIN_FAILED",
                    "FAILED",
                    "/api/auth/login",
                    ipAddress
            );

            throw new RuntimeException("Invalid username or password");
        }

        // ✅ SAFE role resolution (ADMIN > USER)
        String role = user.getRoles()
                .stream()
                .map(userRole -> userRole.getRole().getRoleName())
                .filter(r -> r.equals("ROLE_ADMIN"))
                .findFirst()
                .orElse("ROLE_USER");

        // ✅ LOGIN SUCCESS LOG
        systemLogService.log(
                user.getUsername(),
                "LOGIN_SUCCESS",
                "SUCCESS",
                "/api/auth/login",
                ipAddress
        );

        return jwtUtil.generateToken(user.getUsername(), role);
    }

    // GET ALL USERS (DTO)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // GET USER BY ID (DTO)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    // Entity → DTO
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setBio(user.getBio());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
