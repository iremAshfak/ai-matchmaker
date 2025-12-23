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

    public static Double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(R * c * 100.0) / 100.0;
    }
}