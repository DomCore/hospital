package com.faifly.demo.dao;

public record DoctorDto(
        String firstName,
        String lastName,
        int totalPatients
) {}