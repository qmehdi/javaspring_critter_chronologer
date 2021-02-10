package com.udacity.jdnd.course3.critter.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // schedule_id | schedule_employee_id  | schedule_pet_id | date     | activities
    // 1           | 1                     | 1               | 02-02-21 | PETTING, WALKING
    /**
     * tableName: schedule_employee
     * schedule_employee_id  |schedule_id | employee_id
     * 1                    | 1   | 1 //Huzaifa
     * 2                    | 1   | 2 // Qmber
     * 3                    | 2   | 1
     * 4                    | 2   | 3
     *
     *  tableName: schedule_pets
     *  schedule_id | pet_id
     *  1 | 1 // CAT
     *  1 | 2 //DOG
     *
     */
    // Select * from schedule inner join employee where employee.schedule.id = schedule.id ;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "schedule_employees",
            joinColumns = { @JoinColumn(name = "schedule_id")},
            inverseJoinColumns = { @JoinColumn(name = "employee_id")}
    )
    @JsonBackReference
    private Set<Employee> employees = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "schedule_pets",
            joinColumns = { @JoinColumn(name = "schedule_id")},
            inverseJoinColumns = { @JoinColumn(name = "pet_id")}
    )
    @JsonBackReference
    private Set<Pet> pets = new HashSet<>();

    private LocalDate date;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<EmployeeSkill> activities;
}
