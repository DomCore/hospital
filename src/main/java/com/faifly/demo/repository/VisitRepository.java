package com.faifly.demo.repository;

import com.faifly.demo.model.Patient;
import com.faifly.demo.model.Visit;
import com.faifly.demo.repository.projection.DoctorPatientCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN TRUE ELSE FALSE END FROM visits v " +
            " WHERE v.doctor.id = :doctorId AND " +
            "(v.start < :end AND v.end > :start)")
    boolean existsOverlappingVisit(@Param("doctorId") Long doctorId,
                                   @Param("start") ZonedDateTime start,
                                   @Param("end") ZonedDateTime end);

    @Query("""
                SELECT v.doctor.id AS doctorId,
                       COUNT(DISTINCT v.patient.id) AS totalPatients
                FROM visits v
                WHERE (:doctorIds IS NULL OR v.doctor.id IN :doctorIds)
                  AND v.end <= CURRENT_TIMESTAMP
                GROUP BY v.doctor.id
            """)
    List<DoctorPatientCount> countPastPatientsForAllDoctors(@Param("doctorIds") List<Long> doctorIds);

    @Query("SELECT v FROM visits v " +
            "JOIN FETCH v.patient p " +
            "JOIN FETCH v.doctor d " +
            "WHERE v.patient IN :patients " +
            "AND (:doctorIds IS NULL OR v.doctor.id IN :doctorIds)")
    List<Visit> findByPatientsAndDoctors(@Param("patients") List<Patient> patients,
                                         @Param("doctorIds") List<Long> doctorIds);
}
