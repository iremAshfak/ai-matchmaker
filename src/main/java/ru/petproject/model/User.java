package ru.petproject.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.petproject.model.enums.AuthRole;
import ru.petproject.model.enums.Genders;

import java.util.Collection;
import java.util.List;

@Data
public class User  {
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

    private AuthRole role;
}
