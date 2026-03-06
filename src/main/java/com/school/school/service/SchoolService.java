package com.school.school.service;

import com.school.school.entity.School;
import com.school.school.pojo.SchoolRequest;
import com.school.school.pojo.SchoolResponse;
import com.school.school.pojo.SchoolUpdate;
import com.school.school.repository.SchoolRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SchoolService {
    private final SchoolRepository schoolRepository;

    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    public SchoolResponse createSchool(SchoolRequest request) {
        School school = new School();
        school.setSchoolName(request.schoolName());
        school.setSchoolEmail(request.schoolEmail());
        school.setSchoolAddress(request.schoolAddress());
        school.setLogoUrl(request.logoUrl());
        school.setPhoneNumbers(request.phoneNumbers());
        school.setDescription(request.description());
        school.setSubTitle(request.subTitle());

        return mapToResponse(schoolRepository.save(school));
    }

    @Transactional
    public SchoolResponse updateSchool (SchoolUpdate request, UUID id) {
        School school = schoolRepository.findById(id)  .orElseThrow(() -> new IllegalStateException(
                "school with id " + id + " does not exist"
        ));;

        school.setSchoolName(request.schoolName());
        school.setSchoolAddress(request.schoolAddress());
        school.setDescription(request.description());
        school.setPhoneNumbers(request.phoneNumbers());
        school.setLogoUrl(request.logoUrl());
        school.setSubTitle(request.subTitle());

        return mapToResponse(school);

    }

    public void deleteSchool (UUID id){
        School school = schoolRepository.findById(id). orElseThrow(() -> new IllegalStateException(
                "school with id" + id + "does not exist"
        ));

        schoolRepository.deleteById(school.getId());
    }


    public List<School> getAllSchools() {
        return schoolRepository.findAll();
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
                school.getSubTitle()
        );
    }
}