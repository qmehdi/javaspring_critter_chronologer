package com.udacity.jdnd.course3.critter.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
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

    /**
     * id | skill
     * 1  | WALKING
     * 1 | Feeding
     * 2 | WALKING
     * This table will contain only unique values for skills so there can't be another row like 1 | Feeding
     */
    // Whenever we use an enum (<EmployeeSkill>), we need to add the @ElementCollection annotation
    @ElementCollection
    // Create a new table called `employee_skill` and for each employee ID there will be a list of employee skills.
    @CollectionTable(
            name="employee_skill",
            joinColumns = @JoinColumn(name="id"), uniqueConstraints = @UniqueConstraint(columnNames = {"ID", "SKILL"}))
    @Column(name="skill")
    private Set<EmployeeSkill> skills;

    /**
     *  id | day
     *  1  | SUNDAY
     *  1  | MONDAY
     *  2  | SUNDAY
     */
    @ElementCollection
    @CollectionTable(
            name="day_of_week",
            joinColumns = @JoinColumn(name="id"), uniqueConstraints = @UniqueConstraint(columnNames = {"ID", "DAY"}))
    @Column(name="day")
    private Set<DayOfWeek> daysAvailable;

    @ManyToMany(mappedBy = "pets")
    @JsonManagedReference
    private List<Schedule> schedules = new ArrayList<>();
}
