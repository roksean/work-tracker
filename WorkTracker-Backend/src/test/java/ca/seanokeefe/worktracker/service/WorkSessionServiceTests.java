package ca.seanokeefe.worktracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.seanokeefe.worktracker.exception.WorkSessionNotFoundException;
import ca.seanokeefe.worktracker.model.Person;
import ca.seanokeefe.worktracker.model.WorkSession;
import ca.seanokeefe.worktracker.repository.WorkSessionRepository;

@ExtendWith(MockitoExtension.class)
public class WorkSessionServiceTests {

    @Mock
    private WorkSessionRepository workSessionRepository;

    @Mock
    private PersonService personService;

    @InjectMocks
    private WorkSessionService workSessionService;

    @Test
    public void testSave_DelegatesToRepository() {
        // Arrange
        Person person = new Person("Bob", "bob@example.com", "pass");
        person.setId(1L);
        Instant start = Instant.now();
        Instant end = start.plusSeconds(3600);
        WorkSession session = new WorkSession(person, "calculus", start, end, "HW1", "Done");
        when(workSessionRepository.save(any(WorkSession.class))).thenReturn(session);

        // Act
        WorkSession saved = workSessionService.save(session);

        // Assert
        assertNotNull(saved);
        assertEquals("calculus", saved.getSubject());
        assertEquals("HW1", saved.getName());
        verify(workSessionRepository, times(1)).save(session);
    }

    @Test
    public void testFindByIdOrThrow_ValidId_ReturnsWorkSession() {
        // Arrange
        Long id = 1L;
        Person person = new Person("Alice", "alice@example.com", "pwd");
        WorkSession session = new WorkSession(person, "french", Instant.now(), Instant.now().plusSeconds(100), "Essay", null);
        session.setId(id);
        when(workSessionRepository.findById(id)).thenReturn(Optional.of(session));

        // Act
        WorkSession result = workSessionService.findByIdOrThrow(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("french", result.getSubject());
        assertEquals("Essay", result.getName());
        verify(workSessionRepository, times(1)).findById(id);
    }

    @Test
    public void testFindByIdOrThrow_InvalidId_ThrowsWorkSessionNotFoundException() {
        // Arrange
        Long id = 999L;
        when(workSessionRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        WorkSessionNotFoundException ex = assertThrows(WorkSessionNotFoundException.class, () -> workSessionService.findByIdOrThrow(id));
        assertEquals("Work session with id 999 not found.", ex.getMessage());
        verify(workSessionRepository, times(1)).findById(id);
    }

    @Test
    public void testFindById_DelegatesToRepository() {
        // Arrange
        Long id = 2L;
        Person person = new Person("Charlie", "charlie@example.com", "pwd");
        WorkSession session = new WorkSession(person, "math", Instant.now(), Instant.now().plusSeconds(200), "Quiz", "");
        session.setId(id);
        when(workSessionRepository.findById(id)).thenReturn(Optional.of(session));

        // Act
        Optional<WorkSession> result = workSessionService.findById(id);

        // Assert
        assertNotNull(result);
        assertEquals(true, result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("math", result.get().getSubject());
        verify(workSessionRepository, times(1)).findById(id);
    }

    @Test
    public void testDeleteByIdOrThrow_ExistingId_Deletes() {
        // Arrange
        Long id = 1L;
        when(workSessionRepository.existsById(id)).thenReturn(true);

        // Act
        workSessionService.deleteByIdOrThrow(id);

        // Assert
        verify(workSessionRepository, times(1)).existsById(id);
        verify(workSessionRepository, times(1)).deleteById(id);
    }

    @Test
    public void testDeleteByIdOrThrow_NonExistingId_ThrowsWorkSessionNotFoundException() {
        // Arrange
        Long id = 999L;
        when(workSessionRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        WorkSessionNotFoundException ex = assertThrows(WorkSessionNotFoundException.class, () -> workSessionService.deleteByIdOrThrow(id));
        assertEquals("Work session with id 999 not found.", ex.getMessage());
        verify(workSessionRepository, times(1)).existsById(id);
        verify(workSessionRepository, times(0)).deleteById(any());
    }

    @Test
    public void testFindAllByPersonId_CallsPersonServiceAndRepository() {
        // Arrange
        Long personId = 1L;
        Person person = new Person("Jane", "jane@example.com", "secret");
        person.setId(personId);
        List<WorkSession> sessions = new ArrayList<>();
        sessions.add(new WorkSession(person, "calculus", Instant.now(), Instant.now().plusSeconds(60), "HW", null));
        when(personService.findByIdOrThrow(personId)).thenReturn(person);
        when(workSessionRepository.findByPersonOrderByStartTimeDesc(person)).thenReturn(sessions);

        // Act
        List<WorkSession> result = workSessionService.findAllByPersonId(personId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("calculus", result.get(0).getSubject());
        verify(personService, times(1)).findByIdOrThrow(personId);
        verify(workSessionRepository, times(1)).findByPersonOrderByStartTimeDesc(person);
    }

    @Test
    public void testFindBySubject_CallsPersonServiceAndRepository() {
        // Arrange
        Long personId = 1L;
        String subject = "french";
        Person person = new Person("Dave", "dave@example.com", "pwd");
        person.setId(personId);
        List<WorkSession> sessions = new ArrayList<>();
        sessions.add(new WorkSession(person, subject, Instant.now(), Instant.now().plusSeconds(120), "Reading", null));
        when(personService.findByIdOrThrow(personId)).thenReturn(person);
        when(workSessionRepository.findByPersonAndSubjectOrderByStartTimeDesc(eq(person), eq(subject))).thenReturn(sessions);

        // Act
        List<WorkSession> result = workSessionService.findBySubject(personId, subject);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("french", result.get(0).getSubject());
        assertEquals("Reading", result.get(0).getName());
        verify(personService, times(1)).findByIdOrThrow(personId);
        verify(workSessionRepository, times(1)).findByPersonAndSubjectOrderByStartTimeDesc(person, subject);
    }
}
