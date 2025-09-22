package com.faifly.demo.repository;

import com.faifly.demo.dao.PatientVisitDoctorStatsDto;
import com.faifly.demo.model.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        SELECT new com.faifly.demo.dao.PatientVisitDoctorStatsDto(
            p.id,
            p.firstName,
            p.lastName,
            v.start,
            v.end,
            d.id,
            d.firstName,
            d.lastName,
            CAST((SELECT COUNT(DISTINCT sv.patient.id)
                  FROM visits sv
                  WHERE sv.doctor.id = d.id AND sv.end <= CURRENT_TIMESTAMP) AS integer)
        )
        FROM visits v
        JOIN v.patient p
        JOIN v.doctor d
        WHERE v.end = (
            SELECT MAX(v2.end)
            FROM visits v2
            WHERE v2.patient = p AND v2.doctor = d
        )
        AND (:doctorIds IS NULL OR d.id IN :doctorIds)
        AND (:search IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
               OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', TRIM(:search), '%')))
        ORDER BY p.lastName, p.firstName, d.lastName, d.firstName, v.end DESC
        """)
    Page<PatientVisitDoctorStatsDto> findPatientsWithLatestVisitsAndDoctorStats(
            @Param("search") String search,
            @Param("doctorIds") List<Long> doctorIds,
            Pageable pageable
    );
}
