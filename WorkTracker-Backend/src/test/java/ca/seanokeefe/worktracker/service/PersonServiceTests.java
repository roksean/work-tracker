package ca.seanokeefe.worktracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.seanokeefe.worktracker.exception.PersonNotFoundException;
import ca.seanokeefe.worktracker.model.Person;
import ca.seanokeefe.worktracker.repository.PersonRepository;

@ExtendWith(MockitoExtension.class) //dont need @SpringBoottest annotation because we're not testing the whole application
public class PersonServiceTests {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    @Test
    public void testFindByIdOrThrow_ValidId_ReturnsPerson() {
        // Arrange
        Long id = 1L;
        Person person = new Person("Bob", "bob@example.com", "password123");
        person.setId(id);
        when(personRepository.findById(id)).thenReturn(Optional.of(person));

        // Act
        Person result = personService.findByIdOrThrow(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Bob", result.getName());
        assertEquals("bob@example.com", result.getEmail());
        assertEquals("password123", result.getPassword());
        verify(personRepository, times(1)).findById(id);
    }

    @Test
    public void testFindByIdOrThrow_InvalidId_ThrowsPersonNotFoundException() {
        // Arrange
        Long id = 999L;
        when(personRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        PersonNotFoundException ex = assertThrows(PersonNotFoundException.class, () -> personService.findByIdOrThrow(id));
        assertEquals("Person with id 999 not found.", ex.getMessage());
        verify(personRepository, times(1)).findById(id);
    }

    @Test
    public void testSave_DelegatesToRepository() {
        // Arrange
        Person person = new Person("Jane", "jane@example.com", "secret");
        when(personRepository.save(any(Person.class))).thenReturn(person);

        // Act
        Person saved = personService.save(person);

        // Assert
        assertNotNull(saved);
        assertEquals("Jane", saved.getName());
        assertEquals("jane@example.com", saved.getEmail());
        verify(personRepository, times(1)).save(person);
    }

    @Test
    public void testFindAll_ReturnsListFromRepository() {
        // Arrange
        List<Person> people = new ArrayList<>();
        people.add(new Person("Alice", "alice@example.com", "pass"));
        when(personRepository.findAll()).thenReturn(people);

        // Act
        List<Person> result = personService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getName());
        assertEquals("alice@example.com", result.get(0).getEmail());
        verify(personRepository, times(1)).findAll();
    }

    @Test
    public void testFindById_DelegatesToRepository() {
        // Arrange
        Long id = 2L;
        Person person = new Person("Charlie", "charlie@example.com", "pwd");
        person.setId(id);
        when(personRepository.findById(id)).thenReturn(Optional.of(person));

        // Act
        Optional<Person> result = personService.findById(id);

        // Assert
        assertNotNull(result);
        assertEquals(true, result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("Charlie", result.get().getName());
        verify(personRepository, times(1)).findById(id);
    }
}
