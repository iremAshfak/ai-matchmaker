package ru.petproject.dto;

import lombok.Data;
import ru.petproject.model.User;
import ru.petproject.model.enums.AuthRole;
import ru.petproject.model.enums.Gender;

@Data
public class UserProfileResponse {
    private Long id;

    private String name;

    private Gender gender;

    private Gender preferredGender;

    private Integer age;

    private String description;

    private Double latitude;

    private Double longitude;

    private AuthRole role;

    private Boolean hasLocation;

    private Double distance;

    public static UserProfileResponse fromUser(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setGender(user.getGender());
        response.setPreferredGender(user.getPreferredGender());
        response.setAge(user.getAge());
        response.setDescription(user.getDescription());
        response.setLatitude(user.getLatitude());
        response.setLongitude(user.getLongitude());
        response.setRole(user.getRole());
        response.setHasLocation(user.hasLocation());
        return response;
    }
}