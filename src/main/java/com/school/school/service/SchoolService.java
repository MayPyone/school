package com.school.school.service;

import com.school.school.entity.School;
import com.school.school.entity.User;
import com.school.school.pojo.SchoolRequest;
import com.school.school.pojo.SchoolResponse;
import com.school.school.pojo.SchoolUpdate;
import com.school.school.repository.SchoolRepository;
import com.school.school.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SchoolService {
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;

    public SchoolService(SchoolRepository schoolRepository, UserRepository userRepository) {
        this.schoolRepository = schoolRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SchoolResponse createSchool(SchoolRequest request) {
        User owner = userRepository.findById(request.userId())
                     .orElseThrow(() -> new IllegalStateException("User not found"));
        School school = new School();
        school.setSchoolName(request.schoolName());
        school.setSchoolEmail(request.schoolEmail());
        school.setSchoolAddress(request.schoolAddress());
        school.setLogoUrl(request.logoUrl());
        school.setPhoneNumbers(request.phoneNumbers());
        school.setDescription(request.description());
        school.setSubTitle(request.subTitle());
        school.setCustomizeSchoolId(resolveUniqueCustomizeSchoolId(request.customizeSchoolId(), request.schoolName(), null));
        school.setOwner(owner);

        return mapToResponse(schoolRepository.save(school));
    }

    @Transactional
    public SchoolResponse updateSchool (SchoolUpdate request, UUID id) {
        School school = schoolRepository.findById(id)  .orElseThrow(() -> new IllegalStateException(
                "school with id " + id + " does not exist"
        ));;

        if(request.schoolName() !=null) {
            school.setSchoolName(request.schoolName());
        }

        if(request.schoolEmail() != null) {
            school.setSchoolEmail(request.schoolEmail());
        }

        if(request.schoolAddress() !=null) {
            school.setSchoolAddress(request.schoolAddress());
        }


        if(request.description() !=null) {
            school.setDescription(request.description());
        }


        if(request.phoneNumbers() !=null) {
            school.setPhoneNumbers(request.phoneNumbers());
        }


        if(request.logoUrl() !=null) {
            school.setLogoUrl(request.logoUrl());
        }


        if(request.subTitle() !=null) {
            school.setSubTitle(request.subTitle());
        }

        if(request.customizeSchoolId() != null) {
            school.setCustomizeSchoolId(resolveUniqueCustomizeSchoolId(request.customizeSchoolId(), school.getSchoolName(), school.getId()));
        }

        return mapToResponse(school);

    }

    public void deleteSchool (UUID id){
        School school = schoolRepository.findById(id). orElseThrow(() -> new IllegalStateException(
                "school with id" + id + "does not exist"
        ));

        schoolRepository.deleteById(school.getId());
    }


    public List<SchoolResponse> getAllSchools() {
        return schoolRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SchoolResponse getSchool(UUID id) {
        return mapToResponse(schoolRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("school with id " + id + " does not exist")));
    }

    public SchoolResponse getSchool(String idOrCustomizeSchoolId) {
        return mapToResponse(resolveSchool(idOrCustomizeSchoolId));
    }

    public School resolveSchool(String idOrCustomizeSchoolId) {
        try {
            return schoolRepository.findById(UUID.fromString(idOrCustomizeSchoolId))
                    .orElseThrow(() -> new IllegalStateException("school with id " + idOrCustomizeSchoolId + " does not exist"));
        } catch (IllegalArgumentException ignored) {
            return schoolRepository.findByCustomizeSchoolId(idOrCustomizeSchoolId)
                    .orElseThrow(() -> new IllegalStateException("school with customizeSchoolId " + idOrCustomizeSchoolId + " does not exist"));
        }
    }

    private SchoolResponse mapToResponse(School school) {
        return new SchoolResponse(
                school.getId(),
                school.getSchoolName(),
                school.getSchoolEmail(),
                school.getSchoolAddress(),
                school.getLogoUrl(),
                school.getPhoneNumbers(),
                school.getDescription(),
                school.getSubTitle(),
                school.getCustomizeSchoolId()
        );
    }

    private String resolveUniqueCustomizeSchoolId(String requestedCustomizeSchoolId, String schoolName, UUID currentSchoolId) {
        String base = slugify(Optional.ofNullable(requestedCustomizeSchoolId)
                .filter(value -> !value.isBlank())
                .orElse(schoolName));
        String candidate = base;
        int suffix = 1;

        while (isCustomizeSchoolIdTaken(candidate, currentSchoolId)) {
            candidate = base + "-" + suffix;
            suffix++;
        }

        return candidate;
    }

    private boolean isCustomizeSchoolIdTaken(String customizeSchoolId, UUID currentSchoolId) {
        Optional<School> existingSchool = schoolRepository.findByCustomizeSchoolId(customizeSchoolId);
        return existingSchool.isPresent() && !existingSchool.get().getId().equals(currentSchoolId);
    }

    private String slugify(String value) {
        if (value == null) {
            return "school";
        }

        String slug = value.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return slug.isBlank() ? "school" : slug;
    }
}
