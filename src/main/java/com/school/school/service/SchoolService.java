package com.school.school.service;

import com.school.school.entity.School;
import com.school.school.pojo.SchoolRequest;
import com.school.school.repository.SchoolRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SchoolService {
    private final SchoolRepository schoolRepository;

    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    public School createSchool(SchoolRequest request) {
        School school = new School();
        school.setSchoolName(request.schoolName());
        school.setSchoolEmail(request.schoolEmail());
        school.setSchoolAddress(request.schoolAddress());
        school.setLogoUrl(request.logoUrl());
        school.setPhoneNumbers(request.phoneNumbers());
        school.setDescription(request.description());
        school.setSubTitle(request.subTitle());

        return schoolRepository.save(school);
    }

    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }
}