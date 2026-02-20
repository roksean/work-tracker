package ca.seanokeefe.worktracker.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import ca.seanokeefe.worktracker.model.Person;

public interface PersonRepository extends CrudRepository<Person, Long> {

    Optional<Person> findByEmail(String email);
}