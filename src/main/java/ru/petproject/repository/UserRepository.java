package ru.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.petproject.model.User;
import ru.petproject.model.enums.Gender;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM users u WHERE " +
            "u.latitude IS NOT NULL AND u.longitude IS NOT NULL AND " +
            "earth_distance(ll_to_earth(u.latitude, u.longitude), " +
            "ll_to_earth(:lat, :lon)) <= :radius * 1000 AND " +
            "u.id != :excludeId",
            nativeQuery = true)
    List<User> findNearbyUsers(@Param("lat") Double latitude,
                               @Param("lon") Double longitude,
                               @Param("radius") Double radiusKm,
                               @Param("excludeId") Long excludeId);

    @Query(value = "SELECT * FROM users u WHERE " +
            "u.latitude IS NOT NULL AND u.longitude IS NOT NULL AND " +
            "u.gender = :#{#gender?.name()} AND " +
            "u.age >= :minAge AND " +
            "earth_distance(ll_to_earth(u.latitude, u.longitude), " +
            "ll_to_earth(:lat, :lon)) <= :radius * 1000 AND " +
            "u.id != :excludeId",
            nativeQuery = true)
    List<User> findNearbyUsersByGender(@Param("lat") Double latitude,
                                       @Param("lon") Double longitude,
                                       @Param("radius") Double radiusKm,
                                       @Param("gender") Gender gender,
                                       @Param("minAge") Integer minAge,
                                       @Param("excludeId") Long excludeId);

    @Query(value = "SELECT * FROM users u WHERE u.id != :excludeId ORDER BY RANDOM() LIMIT :limit",
            nativeQuery = true)
    List<User> findRandomUsers(@Param("excludeId") Long excludeId,
                               @Param("limit") int limit);

    @Query("SELECT u FROM User u WHERE " +
            "u.id != :excludeId AND " +
            "(:gender IS NULL OR u.gender = :gender)")
    List<User> searchUsers(@Param("excludeId") Long excludeId,
                           @Param("gender") Gender gender);
}