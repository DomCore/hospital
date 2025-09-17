package com.faifly.demo;

import com.faifly.demo.dao.VisitRequest;
import com.faifly.demo.dao.VisitsResponseDto;
import com.faifly.demo.exception.VisitNotCreatedException;
import com.faifly.demo.model.Doctor;
import com.faifly.demo.model.Patient;
import com.faifly.demo.model.Visit;
import com.faifly.demo.service.DoctorService;
import com.faifly.demo.service.PatientService;
import com.faifly.demo.service.VisitService;
import com.faifly.demo.util.VisitUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VisitUtilTest {

    @Mock
    private VisitService visitService;

    @Mock
    private DoctorService doctorService;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private VisitUtil visitUtil;

    private Doctor doctor;
    private Patient patient;
    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setTimeZone(TimeZone.getDefault());

        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Alice");
        patient.setLastName("Smith");
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void createVisit_success() {
        VisitRequest req = new VisitRequest();
        req.setDoctorId(1);
        req.setPatientId(1);
        req.setStart("2025-09-17 10:00");
        req.setEnd("2025-09-17 11:00");

        when(doctorService.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientService.findById(1L)).thenReturn(Optional.of(patient));
        when(visitService.existsOverlappingVisit(anyLong(), any(), any())).thenReturn(false);

        Visit visit = visitUtil.createVisit(req);

        assertNotNull(visit);
        assertEquals(doctor, visit.getDoctor());
        assertEquals(patient, visit.getPatient());
        assertTrue(visit.getStart().isBefore(visit.getEnd()));
    }

    @Test
    void createVisit_startAfterEnd_throws() {
        VisitRequest req = new VisitRequest();
        req.setDoctorId(1);
        req.setPatientId(1);
        req.setStart("2025-09-17 12:00");
        req.setEnd("2025-09-17 11:00");

        when(doctorService.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientService.findById(1L)).thenReturn(Optional.of(patient));

        assertThrows(VisitNotCreatedException.class, () -> visitUtil.createVisit(req));
    }

    @Test
    void createVisit_doctorBusy_throws() {
        VisitRequest req = new VisitRequest();
        req.setDoctorId(1);
        req.setPatientId(1);
        req.setStart("2025-09-17 10:00");
        req.setEnd("2025-09-17 11:00");

        when(doctorService.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientService.findById(1L)).thenReturn(Optional.of(patient));
        when(visitService.existsOverlappingVisit(anyLong(), any(), any())).thenReturn(true);

        assertThrows(VisitNotCreatedException.class, () -> visitUtil.createVisit(req));
    }

    @Test
    void createVisitsResponseDto_returnsCorrectStructure() {
        Visit visit = new Visit(
                ZonedDateTime.parse("2025-09-17T10:00:00Z"),
                ZonedDateTime.parse("2025-09-17T11:00:00Z"),
                patient,
                doctor
        );

        when(visitService.countPastPatientsByDoctorId(anyList()))
                .thenReturn(Map.of(1L, 1));

        VisitsResponseDto dto = visitUtil.createVisitsResponseDto(List.of(visit));

        assertNotNull(dto);
        assertEquals(1, dto.count());
        assertEquals("Alice", dto.data().getFirst().firstName());
        assertEquals(1, dto.data().getFirst().lastVisits().size());
        assertEquals("John", dto.data().getFirst().lastVisits().getFirst().doctor().firstName());
        assertEquals(1, dto.data().getFirst().lastVisits().getFirst().doctor().totalPatients());
    }

    @Test
    void getPatientsWithVisits_invokesServices() {
        Page<Patient> page = new PageImpl<>(List.of(patient));
        when(patientService.findAll(anyString(), any(Pageable.class))).thenReturn(page);
        when(visitService.findByPatientsAndDoctors(anyList(), anyList())).thenReturn(List.of(
                new Visit(ZonedDateTime.now(), ZonedDateTime.now().plusHours(1), patient, doctor)
        ));
        when(visitService.countPastPatientsByDoctorId(anyList())).thenReturn(Map.of(1L, 1));

        VisitsResponseDto dto = visitUtil.getPatientsWithVisits(Pageable.unpaged(), "", List.of(1L));

        assertNotNull(dto);
        assertEquals(1, dto.count());
    }
}
