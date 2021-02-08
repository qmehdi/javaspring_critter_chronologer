package com.udacity.jdnd.course3.critter.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "pet")
public class Pet
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private LocalDate birthDate;
    private String notes;

    @Enumerated(EnumType.STRING)
    private PetType type;

    @ManyToOne
    @LazyCollection(LazyCollectionOption.TRUE)
    @JoinColumn(name="customer_id")
    @JsonIgnoreProperties("pets")
    @JsonBackReference
    private Customer owner;

    @ManyToMany(mappedBy = "pets")
    @JsonManagedReference
    private Set<Schedule> schedules = new HashSet<>();
}

