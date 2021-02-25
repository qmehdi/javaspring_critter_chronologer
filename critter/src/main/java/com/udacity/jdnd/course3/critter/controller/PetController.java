package com.udacity.jdnd.course3.critter.controller;

import com.udacity.jdnd.course3.critter.dto.PetDTO;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.exception.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.exception.PetNotFoundException;
import com.udacity.jdnd.course3.critter.service.PetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {

        Pet pet = petService.findPet(petDTO.getId()).orElseGet(Pet::new);

        BeanUtils.copyProperties(petDTO, pet, "id");

        try {
            pet = petService.save(pet, petDTO.getOwnerId());
        }
        catch (CustomerNotFoundException e) {
            e.printStackTrace();
        }

        return transformPetEntityToPetDTO(pet);
    }

    private PetDTO transformPetEntityToPetDTO(Pet p) {
        PetDTO dto = new PetDTO();
        BeanUtils.copyProperties(p, dto);
        dto.setOwnerId(p.getOwner().getId());
        return dto;
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) throws PetNotFoundException {
        PetDTO dto = new PetDTO();
        Pet p = petService.findPet(petId).orElseThrow(() -> new PetNotFoundException("ID: " + petId));
        BeanUtils.copyProperties(p, dto);
        dto.setOwnerId(p.getOwner().getId());
        return dto;
    }

    private List<PetDTO> transformPetsEntitiesListToPetsDTOs(List<Pet> pets) {
        List<PetDTO> petDTO = new ArrayList<>();
        pets.forEach(pet -> {
            petDTO.add(new PetDTO(
                    pet.getId(),
                    pet.getType(),
                    pet.getName(),
                    pet.getOwner().getId(),
                    pet.getBirthDate(),
                    pet.getNotes()));
        });
        return petDTO;
    }

    // Get all pets
    @GetMapping
    public List<PetDTO> getPets(){
        List<Pet> pets = petService.findAllPets();
        return transformPetsEntitiesListToPetsDTOs(pets);
    }

    private List<PetDTO> transformPetsEntitiesListToPetsDTO(List<Pet> pets) {
        List<PetDTO> petsDTO = new ArrayList<>();
        pets.forEach(pet -> {
            petsDTO.add(new PetDTO(
                    pet.getId(),
                    pet.getType(),
                    pet.getName(),
                    pet.getOwner().getId(),
                    pet.getBirthDate(),
                    pet.getNotes()));
        });
        return petsDTO;
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {

        // Find all pets by the provided owner id then transform the retrieved entity objects into DTO objects, then return in an http response.
        return transformPetsEntitiesListToPetsDTO(petService.findPetsByOwner(ownerId));
    }
}
