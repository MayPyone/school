package com.school.school.repository;
import com.school.school.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SchoolRepository extends JpaRepository<School, UUID> {
    // Custom query example:
    boolean existsBySchoolEmail(String email);

}