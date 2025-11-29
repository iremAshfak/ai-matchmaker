package ru.petproject.model;

import lombok.Data;

@Data
public class User {
    private Long id;

    private String name;

    private enum gender;

    private enum preferredGender;

    private Integer age;

    private String description;

    private String city;

    private Double latitude;

    private Double longtitude;
}
