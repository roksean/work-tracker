package ca.seanokeefe.worktracker.dto;

import ca.seanokeefe.worktracker.model.Person;

public class PersonRequestDTO {

    private String name;

    public Person toPerson() {
        return new Person(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
