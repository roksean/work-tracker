package ca.seanokeefe.worktracker.dto;

import java.time.Instant;

import ca.seanokeefe.worktracker.model.WorkSession;

public class WorkSessionResponseDTO {

    private Long id;
    private Long personId;
    private String subject;
    private Instant startTime;
    private Instant endTime;
    private String name;
    private String description;

    @SuppressWarnings("unused")
    private WorkSessionResponseDTO() {
    }

    public WorkSessionResponseDTO(WorkSession session) {
        this.id = session.getId();
        this.personId = session.getPerson() != null ? session.getPerson().getId() : null;
        this.subject = session.getSubject();
        this.startTime = session.getStartTime();
        this.endTime = session.getEndTime();
        this.name = session.getName();
        this.description = session.getDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
