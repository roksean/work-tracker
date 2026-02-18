package ca.seanokeefe.worktracker.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.seanokeefe.worktracker.model.Person;
import ca.seanokeefe.worktracker.model.WorkSession;

@SpringBootTest
public class WorkSessionRepositoryTests {

    @Autowired
    private WorkSessionRepository workSessionRepository;

    @Autowired
    private PersonRepository personRepository;

    @AfterEach
    public void clearDatabase() {
        workSessionRepository.deleteAll(); 
        personRepository.deleteAll();
    }
    @Test
    public void testPersistAndLoadWorkSession() {
        // create person
        Person person = new Person("John Doe");
        personRepository.save(person);

        // create work session
        WorkSession workSession = new WorkSession(person, "Test Subject", Instant.now(), Instant.now().plusSeconds(10), "Test Name", "Test Description");
        workSessionRepository.save(workSession);
        Long id = workSession.getId();
        assertNotNull(id, "save should assign an id");

        // read from database
        WorkSession workSessionFromDb = workSessionRepository.findById(id).orElseThrow();

        // assert correct response 
        assertEquals(workSession.getPerson().getId(), workSessionFromDb.getPerson().getId());
        assertEquals(workSession.getSubject(), workSessionFromDb.getSubject());
        assertEquals(workSession.getStartTime(), workSessionFromDb.getStartTime());
        assertEquals(workSession.getEndTime(), workSessionFromDb.getEndTime());
        assertEquals(workSession.getName(), workSessionFromDb.getName());
        assertEquals(workSession.getDescription(), workSessionFromDb.getDescription());
    }
}
