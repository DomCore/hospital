package com.faifly.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Entity(name = "visits")
@NoArgsConstructor
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ZonedDateTime start;

    private ZonedDateTime end;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    public Visit(ZonedDateTime start, ZonedDateTime end, Patient patient, Doctor doctor) {
        this.start = start;
        this.end = end;
        this.patient = patient;
        this.doctor = doctor;
    }
}
