package com.faifly.demo.service;

import com.faifly.demo.dao.PatientVisitDoctorStatsDto;
import com.faifly.demo.model.Visit;
import com.faifly.demo.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

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

    public Page<PatientVisitDoctorStatsDto> findPatientsWithLatestVisitsAndDoctorStats(String search, List<Long> doctorIds, Pageable pageable) {
        return visitRepository.findPatientsWithLatestVisitsAndDoctorStats(search, doctorIds, pageable);
    }

}
