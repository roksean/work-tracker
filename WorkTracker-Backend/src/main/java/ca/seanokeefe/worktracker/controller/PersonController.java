package ca.seanokeefe.worktracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.seanokeefe.worktracker.dto.PersonRequestDTO;
import ca.seanokeefe.worktracker.dto.PersonResponseDTO;
import ca.seanokeefe.worktracker.model.Person;
import ca.seanokeefe.worktracker.service.PersonService;

@RestController
@RequestMapping("/api/people")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public PersonResponseDTO getPerson() {
        Person person = personService.getPersonOrThrow();
        return new PersonResponseDTO(person);
    }

    @PostMapping
    public ResponseEntity<PersonResponseDTO> createPerson(@RequestBody PersonRequestDTO request) {
        Person person = personService.getOrCreateDefault(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(new PersonResponseDTO(person));
    }

    @PutMapping
    public PersonResponseDTO updatePerson(@RequestBody PersonRequestDTO request) {
        Person updated = personService.updatePerson(request.getName());
        return new PersonResponseDTO(updated);
    }
}
