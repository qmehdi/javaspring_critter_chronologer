package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.exception.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.exception.PetNotFoundException;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private CustomerRepository customerRepository;

    // Wrapper method
    public Optional<Pet> findPet(Long id) {
        return petRepository.findById(id);
    }

    @Transactional
    public Pet save(Pet pet, Long ownerId) throws CustomerNotFoundException {
        Customer owner = customerRepository.findById(ownerId)
                .orElseThrow(() -> new CustomerNotFoundException("ID: " + ownerId));

        pet.setOwner(owner);
        pet = petRepository.save(pet);

        owner.getPets().add(pet);
        customerRepository.save(owner);

        return pet;
    }

    public List<Pet> findAllPets() {
        return petRepository.findAll();
    }

    public List<Pet> findPetsByOwner(Long ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    public List<Pet> findPets(List<Long> petIds) throws PetNotFoundException {
        List<Pet> pets = petRepository.findAllById(petIds);

        if (petIds.size() != pets.size()) {
            List<Long> found = pets.stream().map(p -> p.getId()).collect(Collectors.toList());
            String missing = petIds
                    .stream()
                    .filter(id -> !found.contains(id))
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new PetNotFoundException("Could not find pet(s) with id(s): " + missing);
        }
        return pets;
    }
}
