package ca.seanokeefe.worktracker.dto;

import java.time.Instant;

import ca.seanokeefe.worktracker.model.Person;
import ca.seanokeefe.worktracker.model.WorkSession;

public class WorkSessionRequestDTO {

    private Instant startTime;
    private Instant endTime;
    private String subject;
    private String name;
    private String description;

    public WorkSession toWorkSession(Person person) {
        return new WorkSession(person, subject, startTime, endTime, name, description);
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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
