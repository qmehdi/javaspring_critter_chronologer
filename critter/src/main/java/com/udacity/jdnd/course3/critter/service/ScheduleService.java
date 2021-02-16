package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.exception.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private CustomerRepository customerRepository;

    public Optional<Schedule> findSchedule(Long id) {
        return scheduleRepository.findById(id);
    }

    public List<Schedule> findScheduleByPet(Long petId) {
        return scheduleRepository.findByPetsId(petId);
    }

    public List<Schedule> findSchedulesForEmployee(long employeeId) {
        Employee e = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("ID: " + employeeId));
        return (new ArrayList<>(e.getSchedules()));
    }

    public List<Schedule> findSchedulesForCustomer(long customerId) throws CustomerNotFoundException {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("ID: " + customerId));

        // What is going on here???
        // .map produces 1 output value for each input value wheres .flatMap produces 0 or more values for each input value
        return c.getPets()
                .stream()
//                .map(Pet::getSchedules) // This statement is same as next one
                .map(pet -> pet.getSchedules())
                // We want to have a stream of schedules so we can convert them into a list.
                // We want to take the stream of schedules from above .map line and convert into a stream of schedules
                // .flatMap(Collection::stream) // This statement is same as next one
                .flatMap(schedules -> schedules.stream())
                .collect(Collectors.toList());
    }

    // As part of the save schedule operation, we have to update the records for employees and pets in their own tables, that's why there are three save operations.
    @Transactional
    public Schedule save(Schedule schedule) {
        schedule = scheduleRepository.save(schedule);

        for (Employee employee : schedule.getEmployees()) {
            employee.getSchedules().add(schedule);
            employeeRepository.save(employee);
        }

        for (Pet pet : schedule.getPets()) {
            pet.getSchedules().add(schedule);
            petRepository.save(pet);
        }

        return schedule;
    }
}
