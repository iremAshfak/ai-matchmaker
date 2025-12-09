package ru.petproject.model;

import lombok.Data;
import ru.petproject.model.enums.AuthRole;
import ru.petproject.model.enums.Gender;

@Data
public class User  {
    private Long id;

    private String email;

    private String password;

    private String name;

    private Gender gender;

    private Gender preferredGender;

    private Integer age;

    private String description;

    private Double latitude;

    private Double longitude;

    private AuthRole role;

    private Boolean enabled;

    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }
}
