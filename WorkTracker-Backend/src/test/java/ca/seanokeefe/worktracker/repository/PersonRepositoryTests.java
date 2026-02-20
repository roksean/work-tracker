package ca.seanokeefe.worktracker.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.seanokeefe.worktracker.model.Person;

@SpringBootTest
public class PersonRepositoryTests {

    @Autowired
    private PersonRepository personRepository;

    @AfterEach
    public void clearDatabase() {
        personRepository.deleteAll();
    }

    @Test
    public void testPersistAndLoadPerson() {
        // create person (name, email, password)
        Person person = new Person("John Doe", "john@example.com", "password123");

        // save person
        personRepository.save(person);
        Long id = person.getId();
        assertNotNull(id, "save should assign an id");

        // read from database
        Person personFromDb = personRepository.findById(id).orElseThrow();

        // assert correct response
        assertEquals(person.getName(), personFromDb.getName());
        assertEquals(person.getEmail(), personFromDb.getEmail());
        assertEquals(person.getPassword(), personFromDb.getPassword());
        assertEquals(person.getId(), personFromDb.getId());
    }

}
