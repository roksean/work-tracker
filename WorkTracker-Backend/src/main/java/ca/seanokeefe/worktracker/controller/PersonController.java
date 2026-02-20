package ca.seanokeefe.worktracker.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.seanokeefe.worktracker.dto.PersonRequestDTO;
import ca.seanokeefe.worktracker.dto.PersonResponseDTO;
import ca.seanokeefe.worktracker.model.Person;
import ca.seanokeefe.worktracker.service.PersonService;

/**
 * REST API for people (users). Supports list, get by id, create, and update.
 */
@RestController
@RequestMapping("/api/people")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /** Returns all people. */
    @GetMapping
    public List<PersonResponseDTO> getAll() {
        List<Person> people = personService.findAll();
        List<PersonResponseDTO> result = new ArrayList<>();
        for (Person person : people) {
            result.add(new PersonResponseDTO(person));
        }
        return result;
    }

    /** Returns one person by id. Returns 404 if not found. */
    @GetMapping("/{id}")
    public PersonResponseDTO getById(@PathVariable Long id) {
        Person person = personService.findByIdOrThrow(id);
        return new PersonResponseDTO(person);
    }

    /** Creates a new person. Returns 201 with the created person (including id). */
    @PostMapping
    public ResponseEntity<PersonResponseDTO> create(@RequestBody PersonRequestDTO request) {
        Person person = request.toPerson();
        Person saved = personService.save(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PersonResponseDTO(saved));
    }

    /** Updates an existing person by id. Returns 404 if not found. */
    @PutMapping("/{id}")
    public PersonResponseDTO update(@PathVariable Long id, @RequestBody PersonRequestDTO request) {
        Person person = personService.findByIdOrThrow(id);
        person.setName(request.getName());
        person.setEmail(request.getEmail());
        person.setPassword(request.getPassword());
        Person updated = personService.save(person);
        return new PersonResponseDTO(updated);
    }
}
