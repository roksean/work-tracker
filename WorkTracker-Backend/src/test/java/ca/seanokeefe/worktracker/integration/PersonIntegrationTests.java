package ca.seanokeefe.worktracker.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import ca.seanokeefe.worktracker.repository.PersonRepository;
import ca.seanokeefe.worktracker.repository.WorkSessionRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class PersonIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private WorkSessionRepository workSessionRepository;

    private static final String VALID_NAME = "Alice";
    private static final String VALID_EMAIL = "alice@example.com";
    private static final String VALID_PASSWORD = "password123";

    private Long validId;

    @BeforeAll
    public void clearDatabase() {
        workSessionRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void testCreateValidPerson() {
        // Arrange
        PersonRequestDTO request = new PersonRequestDTO();
        request.setName(VALID_NAME);
        request.setEmail(VALID_EMAIL);
        request.setPassword(VALID_PASSWORD);

        // Act
        ResponseEntity<PersonResponseDTO> response = client.postForEntity("/api/people", request, PersonResponseDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        PersonResponseDTO created = response.getBody();
        assertNotNull(created);
        assertEquals(VALID_NAME, created.getName());
        assertEquals(VALID_EMAIL, created.getEmail());
        assertNotNull(created.getId());
        assertTrue(created.getId() > 0, "Response should have a positive ID.");

        this.validId = created.getId();
    }

    @Test
    @Order(2)
    public void testReadPersonByValidId() {
        // Arrange
        String url = "/api/people/" + this.validId;

        // Act
        ResponseEntity<PersonResponseDTO> response = client.getForEntity(url, PersonResponseDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PersonResponseDTO person = response.getBody();
        assertNotNull(person);
        assertEquals(VALID_NAME, person.getName());
        assertEquals(VALID_EMAIL, person.getEmail());
        assertEquals(this.validId, person.getId());
    }

    @Test
    @Order(3)
    public void testGetAllPeople() {
        // Act
        ResponseEntity<List<PersonResponseDTO>> response = client.exchange(
                "/api/people",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PersonResponseDTO>>() {});

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PersonResponseDTO> list = response.getBody();
        assertNotNull(list);
        assertTrue(list.size() >= 1);
        boolean found = false;
        for (PersonResponseDTO p : list) {
            if (p.getId().equals(this.validId)) {
                assertEquals(VALID_NAME, p.getName());
                assertEquals(VALID_EMAIL, p.getEmail());
                found = true;
                break;
            }
        }
        assertTrue(found, "List should contain the created person.");
    }

    @Test
    @Order(4)
    public void testUpdatePerson() {
        // Arrange
        String url = "/api/people/" + this.validId;
        PersonRequestDTO request = new PersonRequestDTO();
        request.setName("Alice Updated");
        request.setEmail("alice.updated@example.com");
        request.setPassword("newpassword");

        // Act (PUT with body)
        ResponseEntity<PersonResponseDTO> response = client.exchange(
                url,
                HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(request),
                PersonResponseDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PersonResponseDTO updated = response.getBody();
        assertNotNull(updated);
        assertEquals("Alice Updated", updated.getName());
        assertEquals("alice.updated@example.com", updated.getEmail());
        assertEquals(this.validId, updated.getId());
    }

    @Test
    @Order(5)
    public void testReadPersonByInvalidId_Returns404() {
        // Arrange
        String url = "/api/people/99999";

        // Act
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("not found"), "Body should contain error message.");
    }
}
