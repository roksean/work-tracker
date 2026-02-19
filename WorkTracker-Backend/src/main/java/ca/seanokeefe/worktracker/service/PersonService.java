package ca.seanokeefe.worktracker.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

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
