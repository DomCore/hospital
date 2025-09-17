package com.faifly.demo.repository;

import com.faifly.demo.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("""
                SELECT p
                FROM patients p
                WHERE (:search IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Patient> findBySearch(@Param("search") String search, Pageable pageable);
}
