package ca.seanokeefe.worktracker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ca.seanokeefe.worktracker.exception.WorkSessionNotFoundException;
import ca.seanokeefe.worktracker.model.Person;
import ca.seanokeefe.worktracker.model.WorkSession;
import ca.seanokeefe.worktracker.repository.WorkSessionRepository;

@Service
public class WorkSessionService {

    private final WorkSessionRepository workSessionRepository;
    private final PersonService personService;

    public WorkSessionService(WorkSessionRepository workSessionRepository, PersonService personService) {
        this.workSessionRepository = workSessionRepository;
        this.personService = personService;
    }

    /** Save a completed work session (frontend sends startTime, endTime, etc.). */
    public WorkSession save(WorkSession workSession) {
        return workSessionRepository.save(workSession);
    }

    /** Get all sessions for the single user, ordered by start time (newest first). */
    public List<WorkSession> findAll() {
        Optional<Person> person = personService.getPerson();
        if (person.isPresent()) {
            return workSessionRepository.findByPersonOrderByStartTimeDesc(person.get());
        } else {
            return List.of();
        }
    }

    /** Get sessions filtered by subject for the single user, ordered by start time (newest first). */
    public List<WorkSession> findBySubject(String subject) {
        Optional<Person> person = personService.getPerson();
        if (person.isPresent()) {
            return workSessionRepository.findByPersonAndSubjectOrderByStartTimeDesc(person.get(), subject);
        } else {
            return List.of();
        }
    }

    /** Get a single session by id. */
    public Optional<WorkSession> findById(Long id) {
        return workSessionRepository.findById(id);
    }

    /** Get a single session by id, or throw WorkSessionNotFoundException if not found. */
    public WorkSession findByIdOrThrow(Long id) {
        Optional<WorkSession> session = workSessionRepository.findById(id);
        if (session.isPresent()) {
            return session.get();
        } else {
            throw new WorkSessionNotFoundException("Work session with id " + id + " not found.");
        }
    }

    /** Delete a session by id. Throws WorkSessionNotFoundException if not found. */
    public void deleteByIdOrThrow(Long id) {
        if (!workSessionRepository.existsById(id)) {
            throw new WorkSessionNotFoundException("Work session with id " + id + " not found.");
        }
        workSessionRepository.deleteById(id);
    }
}
