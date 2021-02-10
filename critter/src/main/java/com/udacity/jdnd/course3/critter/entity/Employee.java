package com.udacity.jdnd.course3.critter.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "employee")
public class Employee
{
    //
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<EmployeeSkill> skills;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<DayOfWeek> daysAvailable;

    @ManyToMany(mappedBy = "pets")
    @JsonManagedReference
    private Set<Schedule> schedules = new HashSet<>();
}
