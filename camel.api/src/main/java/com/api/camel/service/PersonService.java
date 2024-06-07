package com.api.camel.service;

import com.api.camel.exception.ResourceNotFoundException;
import com.api.camel.model.Person;
import com.api.camel.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public Person getPerson(Long id) {
        Optional<Person> personId = personRepository.findById(id);
        return personId.orElse(null);

    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Person addPerson(Person person) {
        return personRepository.save(person);
    }

    public Person updatePerson(Long id, Person newPerson) {
        if (personRepository.existsById(id)) {
            newPerson.setId(id);
            return personRepository.save(newPerson);
        } else {
            throw new ResourceNotFoundException("Person not found with id " + id);
        }
    }

    public void deletePerson(Long id) {
        try {
            personRepository.existsById(id);
            personRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error delete" + id);
        }
    }
}

