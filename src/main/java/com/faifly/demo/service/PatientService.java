package com.faifly.demo.service;

import com.faifly.demo.model.Patient;
import com.faifly.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    public Page<Patient> findAll(String search, Pageable pageable) {
        return patientRepository.findBySearch(search, pageable);
    }

}
