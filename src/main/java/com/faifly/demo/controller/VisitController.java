package com.faifly.demo.controller;

import com.faifly.demo.dao.VisitRequest;
import com.faifly.demo.dao.VisitsResponseDto;
import com.faifly.demo.model.Visit;
import com.faifly.demo.service.PatientService;
import com.faifly.demo.service.VisitService;
import com.faifly.demo.util.VisitUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;
    private final VisitUtil visitUtil;

    @GetMapping
    public ResponseEntity<VisitsResponseDto> getPatients(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int size,
                                                         @RequestParam(required = false) String search,
                                                         @RequestParam(required = false) List<Long> doctorIds) {
        Pageable pageable = PageRequest.of(page, size);
        VisitsResponseDto response = visitUtil.getPatientsWithVisits(pageable, search, doctorIds);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Visit> save(@Valid @RequestBody VisitRequest visitRequest) {
        Visit visit = visitUtil.createVisit(visitRequest);
        return ResponseEntity.ok(visitService.save(visit));
    }

}
