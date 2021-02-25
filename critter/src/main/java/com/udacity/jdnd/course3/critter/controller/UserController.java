package com.udacity.jdnd.course3.critter.controller;

import com.udacity.jdnd.course3.critter.dto.CustomerDTO;
import com.udacity.jdnd.course3.critter.dto.EmployeeDTO;
import com.udacity.jdnd.course3.critter.dto.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.exception.PetNotFoundException;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Users.
 *
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PetService petService;

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO) throws PetNotFoundException {

        // Check if customer already exists; if none exists, get an empty customer object
        // This is a Customer Entity
        Customer customer = userService.findCustomer(customerDTO.getId()).orElseGet(Customer::new);

        // Customer object has been found in the DB. So copy the customer object (entity) into the same CustomerDTO that was passed in. Also, ignore the id attribute since it was already copied on line 48.
        BeanUtils.copyProperties(customerDTO, customer, "id");

        // We can either create a customer first with empty list of pets, or you could already have a pet without any customer
        // From the passed in customer object, take all the pet Ids. Note that this doesn't involve the database. If no pet Ids then attach an empty list to the customerDTO
        List<Long> petIds = Optional.ofNullable(customerDTO.getPetIds()).orElseGet(ArrayList::new);

        // Send in the customer entity object and list of petIds associated with said customer to the service layer. Until this point, no comms with DB have occurred other than finding the customer. Note that findCustomer retrieves the Customer Entity object which also retrieves the list of pets attached to the customer entity; see customer Entity model and List of Pets attribute.
        customer = userService.save(customer, petIds);

        return transformCustomerEntityToCustomerDTO(customer);
    }

    private CustomerDTO transformCustomerEntityToCustomerDTO(Customer c) {
        CustomerDTO dto = new CustomerDTO();

        // We copy the customer entity object into the dto; we do this because the controller should deal with Request/Response of strictly DTO objects, not entities. So we transform the entity object into a proper dto object and return it to the creator of the customer object in an http response. For copying, first parameter is source, second is destination.
        BeanUtils.copyProperties(c, dto);

        // This is a lambda expression
        // Since the customer entity has pet objects attribute and customerDTO has petIds attribute, we need to extract the pet objects from customer entity and store only pet Ids in customerDTO.
        // Loop through the attributes of each pet, get the petId and add it to the customerDTO.
        c.getPets().forEach(pet -> {
            dto.getPetIds().add(pet.getId());
        });
        return dto;
    }

    private List<CustomerDTO> transformCustomerEntityListToCustomerDTOs(List<Customer> customers) {
        List dtos = new ArrayList<CustomerDTO>();
        customers.forEach(customer -> {
            dtos.add(this.transformCustomerEntityToCustomerDTO(customer));
        });
        return dtos;
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){
        List<Customer> customers = userService.getAllCustomers();
        return transformCustomerEntityListToCustomerDTOs(customers);
    }

    // Pass in a petId and find the owner that the pet belongs to.
    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId) throws PetNotFoundException {
        Pet p = petService.findPet(petId).orElseThrow(() -> new PetNotFoundException("ID: " + petId));
        return transformCustomerEntityToCustomerDTO(p.getOwner());
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee e = userService.findEmployee(employeeDTO.getId()).orElseGet(Employee::new);
        BeanUtils.copyProperties(employeeDTO, e, "id");
        e = userService.save(e);
        return transformEmployeeEntityToDTO(e);
    }

    private EmployeeDTO transformEmployeeEntityToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        BeanUtils.copyProperties(employee, dto);

        // // //
//        if(!CollectionUtils.isEmpty(employee.getDaysAvailable())) {
//            dto.setDaysAvailable(new HashSet<>(employee.getDaysAvailable()));
//        }
        return dto;
    }

    // This is not in the postman collection
    @GetMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) throws RuntimeException {
        Employee e = userService.findEmployee(employeeId).orElseThrow(() -> new RuntimeException("ID: " + employeeId));
        return transformEmployeeEntityToDTO(e);
    }

    // Accepts a list of days that the employee is available to work
    // Overwrites the existing list of days that the employee has set in the database.
    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId)   {
        Employee e = userService.findEmployee(employeeId).orElseThrow(() -> new RuntimeException("ID: " + employeeId));
        e.setDaysAvailable(daysAvailable);
        userService.save(e);
    }

    // `Check Availability` (employee) Endpoint.
    // This endpoint is used to figure out which employees are available given a skill and a date; for example, to find out which employees are available for pet feeding on 2021-2-21.
    // In order for this endpoint to return the correct data, 1) hit Save Employee. 2) hit Add Employee Schedule.
    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeRequestDTO) {
        List<Employee> employees = userService.findAvailableEmployees(employeeRequestDTO.getSkills(), employeeRequestDTO.getDate());
        return employees.stream().map(this::transformEmployeeEntityToDTO).collect(Collectors.toList());
    }
}
