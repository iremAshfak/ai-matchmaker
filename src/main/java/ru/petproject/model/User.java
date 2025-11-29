package ru.petproject.model;

import lombok.Data;
import ru.petproject.model.enums.Genders;

@Data
public class User {
    private Long id;

    private String name;

    private Genders gender;

    private Genders preferredGender;

    private Integer age;

    private String description;

    private String city;

    private Double latitude;

    private Double longtitude;
}
