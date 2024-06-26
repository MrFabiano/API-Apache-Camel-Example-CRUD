package com.api.camel.service;

import com.api.camel.exception.ResourceNotFoundException;
import com.api.camel.model.Person;
import com.api.camel.model.PersonDTO;
import com.api.camel.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public PersonDTO getPerson(Long id) {
        Optional<Person> person = personRepository.findById(id);
        return person.map(this::convertToDto).orElse(null);
    }

    public PersonDTO addPerson(Person person) {
        Person savedPerson = personRepository.save(person);
        return convertToDto(savedPerson);
    }

    public PersonDTO updatePerson(Long id, Person updatedPerson) {
        Optional<Person> existingPerson = personRepository.findById(id);
        if (existingPerson.isPresent()) {
            Person person = existingPerson.get();
            person.setName(updatedPerson.getName());
            person.setAgeInWeeks(updatedPerson.getAgeInWeeks());
            Person savedPerson = personRepository.save(person);
            return convertToDto(savedPerson);
        } else {
            throw new ResourceNotFoundException("Person not found with id: " + id);
        }
    }

    public void deletePerson(Long id) {
        if (personRepository.existsById(id)) {
            personRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Person not found with id: " + id);
        }
    }

    public List<PersonDTO> getAllPersons() {
        return personRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PersonDTO convertToDto(Person person) {
        PersonDTO dto = new PersonDTO();
        dto.setName(person.getName());
        dto.setAge(person.getAge());
        return dto;
    }
}

