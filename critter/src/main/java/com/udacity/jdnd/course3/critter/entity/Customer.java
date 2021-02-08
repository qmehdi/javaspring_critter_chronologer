package com.udacity.jdnd.course3.critter.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer
{
    // What is the naming conventation that JPA/Hibernate use to persist into database?
    // customer_id, customer_phone_number, customer_notes
    // For instance you are using hibernate to generate schema for you. If CustomerID @ColumnName("CustomerID)

    //LAZY = fetch when needed
    //EAGER = fetch immediately

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String phoneNumber;
    private String notes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Pet> pets = new ArrayList<>();
}
