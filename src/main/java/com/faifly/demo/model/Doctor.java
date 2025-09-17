package com.faifly.demo.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.TimeZone;

@Data
@Entity(name = "doctors")
@EqualsAndHashCode(callSuper = true)
public class Doctor extends Human {

    private TimeZone timeZone;
}
