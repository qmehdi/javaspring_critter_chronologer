package com.udacity.jdnd.course3.critter.dto;

import com.udacity.jdnd.course3.critter.entity.EmployeeSkill;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the form that schedule request and response data takes. Does not map
 * to the database directly.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDTO {
    private long id;

    // It's better to do new ArrayList<>() when initializing these variables to avoid getting random null pointer exceptions
    private List<Long> employeeIds = new ArrayList<>();
    private List<Long> petIds = new ArrayList<>();
    private LocalDate date;
    private Set<EmployeeSkill> activities = new HashSet<>();
}
