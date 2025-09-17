package com.faifly.demo.dao;

import java.time.ZonedDateTime;

public record VisitDto(
        ZonedDateTime start,
        ZonedDateTime end,
        DoctorDto doctor
) {}