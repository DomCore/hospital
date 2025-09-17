package com.faifly.demo.dao;

import java.util.List;

public record PatientWithVisitsDto(
        String firstName,
        String lastName,
        List<VisitDto> lastVisits
) {}