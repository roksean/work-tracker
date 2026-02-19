package ca.seanokeefe.worktracker.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import ca.seanokeefe.worktracker.model.Person;

public interface PersonRepository extends CrudRepository<Person, Long> {

    /** Returns the single person for MVP (first by id). */
    Optional<Person> findFirstByOrderByIdAsc();
}