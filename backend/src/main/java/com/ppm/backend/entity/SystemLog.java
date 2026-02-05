package com.ppm.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // who performed the action
    @Column(nullable = false)
    private String username;

    // LOGIN_SUCCESS, LOGIN_FAILED, ACCESS_DENIED, etc.
    @Column(nullable = false)
    private String action;

    // SUCCESS / FAILED
    @Column(nullable = false)
    private String status;

    // /api/users, /api/profile, etc.
    private String endpoint;

    // client IP
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

