package com.faifly.demo.dao;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        int status,
        String error,
        List<String> messages,
        LocalDateTime timestamp
) {}