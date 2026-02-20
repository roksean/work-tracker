package ca.seanokeefe.worktracker.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import ca.seanokeefe.worktracker.exception.PersonNotFoundException;
import ca.seanokeefe.worktracker.model.Person;
import ca.seanokeefe.worktracker.repository.PersonRepository;

@Service
public class PersonService {
    
    // dependency injection
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /** Returns the single user, if they exist. */
    public Optional<Person> getPerson() {
        return personRepository.findFirstByOrderByIdAsc();
    }

    /** Returns the single user, or throws PersonNotFoundException if none exist. */
    public Person getPersonOrThrow() {
        Optional<Person> person = personRepository.findFirstByOrderByIdAsc();
        if (person.isPresent()) {
            return person.get();
        } else {
            throw new PersonNotFoundException("No user has been set up yet.");
        }
    }

    /** Updates the single user's name. Throws PersonNotFoundException if no person exists. */
    public Person updatePerson(String name) {
        Person person = getPersonOrThrow();
        person.setName(name);
        return personRepository.save(person);
    }

    /** Saves (create or update) the person. */
    public Person save(Person person) {
        return personRepository.save(person);
    }

    /** For single-user MVP: returns the one person, or creates one with the given name if none exist. */
    public Person getOrCreateDefault(String name) {
        Optional<Person> person = personRepository.findFirstByOrderByIdAsc();
        if (person.isPresent()) {
            return person.get();
        } else {
            return personRepository.save(new Person(name));
        }
    }
}
