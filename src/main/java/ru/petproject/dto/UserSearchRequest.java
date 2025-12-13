package ru.petproject.dto;

import lombok.Data;
import ru.petproject.model.enums.Gender;

@Data
public class UserSearchRequest {
    private Integer minAge = 18;
    private Gender gender;
    private Boolean hasLocation;
    private Double maxDistance;
}