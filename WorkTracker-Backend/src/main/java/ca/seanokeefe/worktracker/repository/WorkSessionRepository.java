package ca.seanokeefe.worktracker.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ca.seanokeefe.worktracker.model.Person;
import ca.seanokeefe.worktracker.model.WorkSession;

public interface WorkSessionRepository extends CrudRepository<WorkSession, Long> {

    /** Find all sessions for a person, ordered by start time (newest first). */
    List<WorkSession> findByPersonOrderByStartTimeDesc(Person person);

    /** Find sessions by person and subject, ordered by start time (newest first). */
    List<WorkSession> findByPersonAndSubjectOrderByStartTimeDesc(Person person, String subject);
}
