package com.ppm.backend.service;

import com.ppm.backend.entity.SystemLog;
import com.ppm.backend.repository.SystemLogRepository;
import org.springframework.stereotype.Service;

@Service
public class SystemLogService {

    private final SystemLogRepository systemLogRepository;

    public SystemLogService(SystemLogRepository systemLogRepository) {
        this.systemLogRepository = systemLogRepository;
    }

    public void log(
            String username,
            String action,
            String status,
            String endpoint,
            String ipAddress
    ) {
        SystemLog log = new SystemLog();
        log.setUsername(username);
        log.setAction(action);
        log.setStatus(status);
        log.setEndpoint(endpoint);
        log.setIpAddress(ipAddress);

        systemLogRepository.save(log);
    }
}

