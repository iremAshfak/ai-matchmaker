package ru.petproject.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.petproject.model.enums.AuthRole;
import ru.petproject.model.enums.Gender;

import java.util.Objects;

@Entity
@Data
@Table(name = "users")
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email)
                && Objects.equals(password, user.password) && Objects.equals(name, user.name)
                && gender == user.gender && preferredGender == user.preferredGender
                && Objects.equals(age, user.age) && Objects.equals(description, user.description)
                && Objects.equals(latitude, user.latitude) && Objects.equals(longitude, user.longitude)
                && role == user.role && Objects.equals(enabled, user.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, name, gender, preferredGender, age, description, latitude, longitude, role, enabled);
    }
}
