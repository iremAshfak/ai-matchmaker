package ru.petproject.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.petproject.model.enums.AuthRole;
import ru.petproject.model.enums.Gender;

@Data
public class UpdateProfileRequest{
    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @NotNull
    private Gender gender;

    private Gender preferredGender;

    @Min(value = 18)
    private Integer age;

    @Size(max = 500)
    private String description;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;

    private AuthRole role;

    public boolean isLocationValid() {
        if (latitude == null && longitude == null) {
            return true;
        }
        return latitude != null && longitude != null;
    }
}