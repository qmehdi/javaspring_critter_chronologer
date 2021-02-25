package com.udacity.jdnd.course3.critter.controller;

import com.udacity.jdnd.course3.critter.dto.ScheduleDTO;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.exception.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.exception.PetNotFoundException;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import com.udacity.jdnd.course3.critter.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private UserService userService;
    @Autowired
    private PetService petService;

    // Individual scheduleDTO
    private ScheduleDTO transformScheduleEntityToScheduleDTO(Schedule s) {
        ScheduleDTO dto = new ScheduleDTO();

        BeanUtils.copyProperties(s, dto);

        // Since the DTO requires Ids instead of objects, we have to do this.
        // In each schedule item, loop thru employees and then for each employee, take the Id for employee and add it to DTO's employee Ids
        // s is the passed in schedule object
        s.getEmployees().forEach(employee -> {

            // why isn't this setEmployeeIds????
            dto.getEmployeeIds().add(employee.getId());
        });

        s.getPets().forEach(pet -> {
            dto.getPetIds().add(pet.getId());
        });

        dto.getActivities().addAll(new HashSet<>(s.getActivities()));

        return dto;
    }

    // List of scheduleDTOs
    private List<ScheduleDTO> transformScheduleEntitiesListToScheduleDTOs(List<Schedule> schedules) {
        List<ScheduleDTO> scheduleDTOs = new ArrayList<>();
        schedules.forEach(sch -> {
            scheduleDTOs.add(new ScheduleDTO(
                            sch.getId(),
                            sch.getEmployees().stream().map(employee -> employee.getId()).collect(Collectors.toList()),
                            sch.getPets().stream().map(pet -> pet.getId()).collect(Collectors.toList()),
                            sch.getDate(),
                            new HashSet<>(sch.getActivities())));
        });

        return scheduleDTOs;

        // This is another way to do it
//         return schedules.stream().map(s -> transformScheduleEntityToScheduleDTO(s)).collect(Collectors.toList());
    }

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) throws PetNotFoundException {

        // We're using this controller method for both create and update schedule.
        // In the case of creating a new schedule, the scheduleDTO.getId() is expected to return empty since the Id field is blank.
        Schedule schedule = scheduleService.findSchedule(scheduleDTO.getId()).orElseGet(Schedule::new);

        schedule.setDate(scheduleDTO.getDate());

        // The Activities attribute on the scheduleDTO is a set while it's a list on the entity that's why we need to cast the activities field into an ArrayList that the schedule entity expects.
        schedule.setActivities(new ArrayList<>(scheduleDTO.getActivities()));

        // Similar to above
        schedule.setEmployees(new HashSet<>(userService.findEmployees(scheduleDTO.getEmployeeIds())));

        // Similar to above
        schedule.setPets(new HashSet<>(petService.findPets(scheduleDTO.getPetIds())));

        schedule = scheduleService.save(schedule);

        return transformScheduleEntityToScheduleDTO(schedule);
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> schedules = scheduleService.findAllSchedules();
        return transformScheduleEntitiesListToScheduleDTOs(schedules);
    }

    // Returns list of schedules for the given petId
    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) throws PetNotFoundException {

//        // The findScheduleByPet only takes in a single pet Id and returns a list of schedules
//        List<Schedule> schedules = scheduleService.findScheduleByPet(petId);

        // First, find the pet entity using the given petId
//        Pet p = petService.

        return transformScheduleEntitiesListToScheduleDTOs(scheduleService.findSchedulesForPet(petId));
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        return transformScheduleEntitiesListToScheduleDTOs(scheduleService.findSchedulesForEmployee(employeeId));
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) throws CustomerNotFoundException
    {
        return transformScheduleEntitiesListToScheduleDTOs(scheduleService.findSchedulesForCustomer(customerId));
    }
}
