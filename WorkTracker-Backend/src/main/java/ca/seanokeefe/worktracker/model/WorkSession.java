package ca.seanokeefe.worktracker.model;

import java.time.Instant;

/**
 * A completed work session: from when the user started the timer to when they stopped it.
 * Name and description are optional (filled in when they stop the timer).
 */
public class WorkSession {

    private Long id;
    private User user;
    private Instant startTime;
    private Instant endTime;
    private String name;
    private String description;

    public WorkSession() {}

    public WorkSession(User user, Instant startTime, Instant endTime, String name, String description) {
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
