package ca.seanokeefe.worktracker.repository;

import org.springframework.data.repository.CrudRepository;

import ca.seanokeefe.worktracker.model.WorkSession;

public interface WorkSessionRepository extends CrudRepository<WorkSession, Long>{

}
