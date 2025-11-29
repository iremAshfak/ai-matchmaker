package ru.petproject.model;

import lombok.Data;
import ru.petproject.model.enums.Genders;

@Data
public class User {
    private Long id;

    private String email;

    private String password;

    private String name;

    private Genders gender;

    private Genders preferredGender;

    private Integer age;

    private String description;

    private Double latitude;

    private Double longitude;
}
