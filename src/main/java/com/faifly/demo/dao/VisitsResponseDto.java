package com.faifly.demo.dao;

import java.util.List;

public record VisitsResponseDto(
        List<PatientWithVisitsDto> data,
        long count
) {}