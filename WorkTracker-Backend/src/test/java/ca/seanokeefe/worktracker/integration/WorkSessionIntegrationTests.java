package ca.seanokeefe.worktracker.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.seanokeefe.worktracker.dto.PersonRequestDTO;
import ca.seanokeefe.worktracker.dto.PersonResponseDTO;
import ca.seanokeefe.worktracker.dto.WorkSessionRequestDTO;
import ca.seanokeefe.worktracker.dto.WorkSessionResponseDTO;
import ca.seanokeefe.worktracker.repository.PersonRepository;
import ca.seanokeefe.worktracker.repository.WorkSessionRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class WorkSessionIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private WorkSessionRepository workSessionRepository;

    private Long personId;
    private Long sessionId;

    @BeforeAll
    public void clearDatabaseAndCreatePerson() {
        workSessionRepository.deleteAll();
        personRepository.deleteAll();

        // Create one person so we have a valid personId for work sessions
        PersonRequestDTO personRequest = new PersonRequestDTO();
        personRequest.setName("Test User");
        personRequest.setEmail("test@example.com");
        personRequest.setPassword("password");
        ResponseEntity<PersonResponseDTO> personResponse = client.postForEntity("/api/people", personRequest, PersonResponseDTO.class);
        assertNotNull(personResponse.getBody());
        this.personId = personResponse.getBody().getId();
    }

    @Test
    @Order(1)
    public void testCreateValidWorkSession() {
        // Arrange
        WorkSessionRequestDTO request = new WorkSessionRequestDTO();
        request.setPersonId(personId);
        request.setStartTime(Instant.parse("2025-02-18T19:00:00Z"));
        request.setEndTime(Instant.parse("2025-02-18T19:30:00Z"));
        request.setSubject("calculus");
        request.setName("Homework 3");
        request.setDescription("Derivatives");

        // Act
        ResponseEntity<WorkSessionResponseDTO> response = client.postForEntity("/api/work-sessions", request, WorkSessionResponseDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        WorkSessionResponseDTO created = response.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        assertTrue(created.getId() > 0);
        assertEquals(personId, created.getPersonId());
        assertEquals("calculus", created.getSubject());
        assertEquals("Homework 3", created.getName());

        this.sessionId = created.getId();
    }

    @Test
    @Order(2)
    public void testGetWorkSessionById() {
        // Arrange
        String url = "/api/work-sessions/" + this.sessionId;

        // Act
        ResponseEntity<WorkSessionResponseDTO> response = client.getForEntity(url, WorkSessionResponseDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        WorkSessionResponseDTO session = response.getBody();
        assertNotNull(session);
        assertEquals(this.sessionId, session.getId());
        assertEquals(personId, session.getPersonId());
        assertEquals("calculus", session.getSubject());
        assertEquals("Homework 3", session.getName());
    }

    @Test
    @Order(3)
    public void testGetAllWorkSessionsByPersonId() {
        // Arrange
        String url = "/api/work-sessions?personId=" + this.personId;

        // Act
        ResponseEntity<List<WorkSessionResponseDTO>> response = client.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<WorkSessionResponseDTO>>() {});

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<WorkSessionResponseDTO> list = response.getBody();
        assertNotNull(list);
        assertTrue(list.size() >= 1);
        boolean found = false;
        for (WorkSessionResponseDTO s : list) {
            if (s.getId().equals(this.sessionId)) {
                assertEquals("calculus", s.getSubject());
                found = true;
                break;
            }
        }
        assertTrue(found, "List should contain the created work session.");
    }

    @Test
    @Order(4)
    public void testDeleteWorkSession() {
        // Arrange
        String url = "/api/work-sessions/" + this.sessionId;

        // Act
        ResponseEntity<Void> response = client.exchange(url, HttpMethod.DELETE, null, Void.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Order(5)
    public void testGetWorkSessionByInvalidId_Returns404() {
        // Arrange
        String url = "/api/work-sessions/99999";

        // Act
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("not found"), "Body should contain error message.");
    }
}
