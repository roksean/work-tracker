package ca.seanokeefe.worktracker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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

    /** Delete a session by id. */
    public void deleteById(Long id) {
        workSessionRepository.deleteById(id);
    }
}
