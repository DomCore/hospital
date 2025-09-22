package com.faifly.demo.util;

import com.faifly.demo.dao.*;
import com.faifly.demo.exception.VisitNotCreatedException;
import com.faifly.demo.model.Doctor;
import com.faifly.demo.model.Patient;
import com.faifly.demo.model.Visit;
import com.faifly.demo.service.DoctorService;
import com.faifly.demo.service.PatientService;
import com.faifly.demo.service.VisitService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.faifly.demo.util.Constants.DATETIME_FORMAT;


@Component
@RequiredArgsConstructor
public class VisitUtil {

    private final VisitService visitService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Visit createVisit(VisitRequest req) {
        Doctor doctor = doctorService.findById(Long.valueOf(req.getDoctorId()))
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
        Patient patient = patientService.findById(Long.valueOf(req.getPatientId()))
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        ZoneId zone = doctor.getTimeZone().toZoneId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
        ZonedDateTime start = LocalDateTime.parse(req.getStart(), formatter).atZone(zone);
        ZonedDateTime end = LocalDateTime.parse(req.getEnd(), formatter).atZone(zone);
        if (!start.isBefore(end)) {
            throw new VisitNotCreatedException("Start must be before end");
        }

        if (visitService.existsOverlappingVisit(doctor.getId(), start, end)) {
            throw new VisitNotCreatedException("Doctor is busy in this time range");
        }

        return new Visit(start, end, patient, doctor);
    }


    public VisitsResponseDto getPatientsWithLatestVisits(Pageable pageable, String search, List<Long> doctorIds) {
        Page<PatientVisitDoctorStatsDto> resultsPage = visitService.findPatientsWithLatestVisitsAndDoctorStats(search, doctorIds, pageable);
        List<PatientVisitDoctorStatsDto> statsDtos = resultsPage.getContent();

        if (statsDtos.isEmpty()) {
            return new VisitsResponseDto(Collections.emptyList(), 0);
        }

        Map<Long, PatientWithVisitsDto> patientsMap = new LinkedHashMap<>();

        for (PatientVisitDoctorStatsDto dto : statsDtos) {
            DoctorDto doctorDto = new DoctorDto(
                    dto.doctorFirstName(),
                    dto.doctorLastName(),
                    dto.doctorsTotalPatientsCount()
            );

            VisitDto visitDto = new VisitDto(
                    dto.visitStart(),
                    dto.visitEnd(),
                    doctorDto
            );

            patientsMap.computeIfAbsent(dto.patientId(), _ ->
                    new PatientWithVisitsDto(
                            dto.patientFirstName(),
                            dto.patientLastName(),
                            new ArrayList<>()
                    )
            ).lastVisits().add(visitDto);
        }

        return new VisitsResponseDto(new ArrayList<>(patientsMap.values()), patientsMap.size());
    }

}
