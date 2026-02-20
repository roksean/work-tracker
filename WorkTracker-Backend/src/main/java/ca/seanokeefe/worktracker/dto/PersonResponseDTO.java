package ca.seanokeefe.worktracker.dto;

import ca.seanokeefe.worktracker.model.Person;

public class PersonResponseDTO {

    private Long id;
    private String name;
    private String email;

    @SuppressWarnings("unused")
    private PersonResponseDTO() {
    }

    public PersonResponseDTO(Person person) {
        this.id = person.getId();
        this.name = person.getName();
        this.email = person.getEmail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
