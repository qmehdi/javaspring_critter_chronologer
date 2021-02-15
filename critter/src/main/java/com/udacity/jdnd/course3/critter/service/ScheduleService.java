package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PetRepository petRepository;

    public Optional<Schedule> findSchedule(Long id) {
        return scheduleRepository.findById(id);
    }

    public List<Schedule> findScheduleByPet(Long petId) {
        return scheduleRepository.findByPetsId(petId);
    }

    // /customer/{customerId}
    public List<Schedule> findScheduleByOwner(Long ownerId) {
        return scheduleRepository.findByOwnersId(ownerId);
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
