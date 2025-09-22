package com.faifly.demo.dao;

import java.time.ZonedDateTime;

public record PatientVisitDoctorStatsDto(
    Long patientId,
    String patientFirstName,
    String patientLastName,
    ZonedDateTime visitStart,
    ZonedDateTime visitEnd,
    Long doctorId,
    String doctorFirstName,
    String doctorLastName,
    int doctorsTotalPatientsCount
) {}
