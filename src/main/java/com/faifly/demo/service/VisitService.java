package com.faifly.demo.service;

import com.faifly.demo.model.Patient;
import com.faifly.demo.model.Visit;
import com.faifly.demo.repository.projection.DoctorPatientCount;
import com.faifly.demo.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;

    public Visit save(Visit visit) {
        return visitRepository.save(visit);
    }

    public boolean existsOverlappingVisit(Long doctorId, ZonedDateTime start, ZonedDateTime end) {
        return visitRepository.existsOverlappingVisit(doctorId, start, end);
    }

    public Map<Long, Integer> countPastPatientsByDoctorId(List<Long> doctorIds) {
        return visitRepository.countPastPatientsForAllDoctors(doctorIds)
                .stream()
                .collect(Collectors.toMap(DoctorPatientCount::getDoctorId,
                        DoctorPatientCount::getTotalPatients));
    }

    public List<Visit> findByPatientsAndDoctors(List<Patient> patients, List<Long> doctorIds) {
        return visitRepository.findByPatientsAndDoctors(patients, doctorIds);
    }

}
