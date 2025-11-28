package ru.petproject.model;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String description;
    private String city;
    private Double latitude;
    private Double longtitude;
}
