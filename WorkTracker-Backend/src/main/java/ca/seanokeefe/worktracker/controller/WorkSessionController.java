package ca.seanokeefe.worktracker.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.seanokeefe.worktracker.dto.WorkSessionRequestDTO;
import ca.seanokeefe.worktracker.dto.WorkSessionResponseDTO;
import ca.seanokeefe.worktracker.model.Person;
import ca.seanokeefe.worktracker.model.WorkSession;
import ca.seanokeefe.worktracker.service.PersonService;
import ca.seanokeefe.worktracker.service.WorkSessionService;

@RestController
@RequestMapping("/api/work-sessions")
public class WorkSessionController {

    private final WorkSessionService workSessionService;
    private final PersonService personService;

    public WorkSessionController(WorkSessionService workSessionService, PersonService personService) {
        this.workSessionService = workSessionService;
        this.personService = personService;
    }

    @GetMapping
    public List<WorkSessionResponseDTO> getAll(@RequestParam(required = false) String subject) {
        List<WorkSession> sessions;
        if (subject != null && !subject.isBlank()) {
            sessions = workSessionService.findBySubject(subject);
        } else {
            sessions = workSessionService.findAll();
        }
        List<WorkSessionResponseDTO> result = new ArrayList<>();
        for (WorkSession session : sessions) {
            result.add(new WorkSessionResponseDTO(session));
        }
        return result;
    }

    @GetMapping("/{id}")
    public WorkSessionResponseDTO getById(@PathVariable Long id) {
        WorkSession session = workSessionService.findByIdOrThrow(id);
        return new WorkSessionResponseDTO(session);
    }

    @PostMapping
    public ResponseEntity<WorkSessionResponseDTO> create(@RequestBody WorkSessionRequestDTO request) {
        Person person = personService.getPersonOrThrow();
        WorkSession session = request.toWorkSession(person);
        WorkSession saved = workSessionService.save(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(new WorkSessionResponseDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workSessionService.deleteByIdOrThrow(id);
        return ResponseEntity.noContent().build();
    }
}
