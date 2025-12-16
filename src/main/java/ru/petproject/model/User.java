package ru.petproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.petproject.model.enums.AuthRole;
import ru.petproject.model.enums.Gender;


@Entity
@Data
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
