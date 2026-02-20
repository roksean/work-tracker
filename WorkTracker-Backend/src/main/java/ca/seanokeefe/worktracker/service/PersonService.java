package ca.seanokeefe.worktracker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ca.seanokeefe.worktracker.exception.PersonNotFoundException;
import ca.seanokeefe.worktracker.model.Person;
import ca.seanokeefe.worktracker.repository.PersonRepository;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> findAll() {
        List<Person> result = new ArrayList<>();
        for (Person p : personRepository.findAll()) {
            result.add(p);
        }
        return result;
    }

    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    public Person findByIdOrThrow(Long id) {
        Optional<Person> person = personRepository.findById(id);
        if (person.isPresent()) {
            return person.get();
        } else {
            throw new PersonNotFoundException("Person with id " + id + " not found.");
        }
    }

    public Person save(Person person) {
        return personRepository.save(person);
    }
}
