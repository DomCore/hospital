package com.faifly.demo;

import com.faifly.demo.dao.PatientVisitDoctorStatsDto;
import com.faifly.demo.dao.PatientWithVisitsDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.*;

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
    private Pageable pageable;

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
        pageable = PageRequest.of(0, 10);
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
    void getPatientsWithLatestVisits_shouldReturnFormattedData_whenVisitsExist() {
        String search = "Alice";
        List<Long> doctorIds = Arrays.asList(101L, 102L);

        ZonedDateTime visit1Start = ZonedDateTime.parse("2025-09-10T10:00:00Z");
        ZonedDateTime visit1End = ZonedDateTime.parse("2025-09-10T10:30:00Z");
        ZonedDateTime visit2Start = ZonedDateTime.parse("2025-09-17T10:00:00Z");
        ZonedDateTime visit2End = ZonedDateTime.parse("2025-09-17T10:30:00Z");
        ZonedDateTime visit3Start = ZonedDateTime.parse("2025-09-18T11:00:00Z");
        ZonedDateTime visit3End = ZonedDateTime.parse("2025-09-18T11:30:00Z");
        ZonedDateTime visit4Start = ZonedDateTime.parse("2025-09-10T11:00:00Z");
        ZonedDateTime visit4End = ZonedDateTime.parse("2025-09-10T11:20:00Z");

        List<PatientVisitDoctorStatsDto> mockStats = Arrays.asList(
                new PatientVisitDoctorStatsDto(
                        1L, "Alice", "Smith",
                        visit1Start, visit1End,
                        101L, "Ethan", "Hughes", 3),
                new PatientVisitDoctorStatsDto(
                        1L, "Alice", "Smith",
                        visit2Start, visit2End,
                        102L, "Elijah", "Nelson", 1),
                new PatientVisitDoctorStatsDto(
                        2L, "Bob", "Johnson",
                        visit3Start, visit3End,
                        103L, "Charlotte", "Carter", 1),
                new PatientVisitDoctorStatsDto(
                        2L, "Bob", "Johnson",
                        visit4Start, visit4End,
                        104L, "Sophia", "Lewis", 3)
        );
        Page<PatientVisitDoctorStatsDto> mockPage = new PageImpl<>(mockStats, pageable, mockStats.size());

        when(visitService.findPatientsWithLatestVisitsAndDoctorStats(search, doctorIds, pageable))
                .thenReturn(mockPage);

        VisitsResponseDto response = visitUtil.getPatientsWithLatestVisits(pageable, search, doctorIds);

        assertEquals(2, response.count());
        assertEquals(2, response.data().size());

        PatientWithVisitsDto alice = response.data().stream()
                .filter(p -> "Alice".equals(p.firstName()) && "Smith".equals(p.lastName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Alice Smith not found"));

        assertEquals(2, alice.lastVisits().size());
        assertTrue(alice.lastVisits().stream()
                .anyMatch(v -> v.doctor().firstName().equals("Ethan") && v.doctor().totalPatients() == 3 && v.start().equals(visit1Start)));
        assertTrue(alice.lastVisits().stream()
                .anyMatch(v -> v.doctor().firstName().equals("Elijah") && v.doctor().totalPatients() == 1 && v.start().equals(visit2Start)));


        PatientWithVisitsDto bob = response.data().stream()
                .filter(p -> "Bob".equals(p.firstName()) && "Johnson".equals(p.lastName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Bob Johnson not found"));

        assertEquals(2, bob.lastVisits().size());
        assertTrue(bob.lastVisits().stream()
                .anyMatch(v -> v.doctor().firstName().equals("Charlotte") && v.doctor().totalPatients() == 1 && v.start().equals(visit3Start)));
        assertTrue(bob.lastVisits().stream()
                .anyMatch(v -> v.doctor().firstName().equals("Sophia") && v.doctor().totalPatients() == 3 && v.start().equals(visit4Start)));
    }

    @Test
    void getPatientsWithLatestVisits_shouldReturnEmptyResponse_whenNoVisitsExist() {
        String search = "NonExistent";
        List<Long> doctorIds = Collections.emptyList();
        Page<PatientVisitDoctorStatsDto> emptyPage = Page.empty(pageable);

        when(visitService.findPatientsWithLatestVisitsAndDoctorStats(search, doctorIds, pageable))
                .thenReturn(emptyPage);

        VisitsResponseDto response = visitUtil.getPatientsWithLatestVisits(pageable, search, doctorIds);

        assertTrue(response.data().isEmpty());
        assertEquals(0, response.count());
    }

}
