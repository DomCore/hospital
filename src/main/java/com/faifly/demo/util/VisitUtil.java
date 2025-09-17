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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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


    public VisitsResponseDto createVisitsResponseDto(List<Visit> visits) {
        Map<Long, PatientWithVisitsDto> patientsMap = new LinkedHashMap<>();

        Map<Long, Integer> countPastPatientsByDoctorId = visitService.countPastPatientsByDoctorId(visits
                .stream().map(v -> v.getDoctor().getId()).toList());

        for (Visit v : visits) {
            var patient = v.getPatient();
            var doctor = v.getDoctor();
            int doctorsPatients = countPastPatientsByDoctorId.getOrDefault(doctor.getId(), 0);

            DoctorDto doctorDto = new DoctorDto(
                    doctor.getFirstName(),
                    doctor.getLastName(),
                    doctorsPatients
            );

            VisitDto visitDto = new VisitDto(
                    v.getStart(),
                    v.getEnd(),
                    doctorDto
            );

            patientsMap.computeIfAbsent(patient.getId(), _ ->
                    new PatientWithVisitsDto(
                            patient.getFirstName(),
                            patient.getLastName(),
                            new ArrayList<>()
                    )
            ).lastVisits().add(visitDto);
        }

        return new VisitsResponseDto(new ArrayList<>(patientsMap.values()), patientsMap.size());
    }

    public VisitsResponseDto getPatientsWithVisits(Pageable pageable, String search, List<Long> doctorIds) {
        Page<Patient> patients = patientService.findAll(search, pageable);
        List<Visit> visits = visitService.findByPatientsAndDoctors(patients.getContent(), doctorIds);
        return createVisitsResponseDto(visits);
    }

}
