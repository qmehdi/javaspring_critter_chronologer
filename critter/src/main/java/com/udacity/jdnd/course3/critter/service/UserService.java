package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.EmployeeSkill;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.exception.PetNotFoundException;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeManagedRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeManagedRepository employeeManagedRepository;

    // This is a wrapper method to simply make the findById method in the CrudRepository available to the controller. We put this here in the Service class because the controller should never talk to the Repository directly.
    public Optional<Customer> findCustomer(Long id) {
        return customerRepository.findById(id);
    }

    // Wrapper method for employee find by id
    public Optional<Employee> findEmployee(Long id) {
        return employeeRepository.findById(id);
    }

    @Transactional
    public Customer save(Customer customer, List<Long> petIds) throws PetNotFoundException {

        // We're clearing the pet objects that were first fetched while doing the findCustomer call. (Customer Entity object automatically calls list of pets).
        customer.getPets().clear();

        // This for loop will only run if any pet Ids were passed in. We are for looping thru all the petIds and retrieving the associated pet objects and attaching them to the customer object.
        for (Long petId : petIds) {
            Pet pet = petRepository.findById(petId).orElseThrow(() -> new PetNotFoundException("ID: " + petId));
            customer.getPets().add(pet);
        }

        // SAVE the customer object into the Db
        return customerRepository.save(customer);
    }

    @Transactional
    public Employee save(Employee e) {
        return employeeRepository.save(e);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Employee> findEmployees(List<Long> employeeIds) throws RuntimeException {
        List<Employee> employees = employeeRepository.findAllById(employeeIds);
        if (employeeIds.size() != employees.size())
        {
            List<Long> found = employees.stream().map(e -> e.getId()).collect(Collectors.toList());
            String missing = employeeIds
                    .stream()
                    .filter(id -> !found.contains(id))
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("Could not find employee(s) with id(s): " + missing);
        }
        return employees;
    }

    public List<Employee> findAvailableEmployees(Set<EmployeeSkill> skills, LocalDate date) {
        List<Long> employeesIds = employeeManagedRepository.findEmployeeIdsWithAllSkillsOnDay(skills, date.getDayOfWeek());
        return employeeRepository.findAllById(employeesIds);
    }
}
