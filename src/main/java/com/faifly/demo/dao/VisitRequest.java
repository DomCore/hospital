package com.faifly.demo.dao;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VisitRequest {

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$",
            message = "Start datetime must be in format yyyy-MM-dd HH:mm")
    private String start;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$",
            message = "End datetime must be in format yyyy-MM-dd HH:mm")
    private String end;

    @NotNull(message = "Patient ID must not be null")
    @Min(value = 1, message = "Patient ID must be greater than 0")
    private Integer patientId;

    @NotNull(message = "Doctor ID must not be null")
    @Min(value = 1, message = "Doctor ID must be greater than 0")
    private Integer doctorId;
}
