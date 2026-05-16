package com.school.school.service;

import com.school.school.entity.School;
import com.school.school.entity.Staff;
import com.school.school.entity.StaffStatus;
import com.school.school.entity.User;
import com.school.school.pojo.StaffRequest;
import com.school.school.pojo.StaffResponse;
import com.school.school.repository.SchoolRepository;
import com.school.school.repository.StaffRepository;
import com.school.school.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class StaffService {
    private final StaffRepository staffRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public StaffService(StaffRepository staffRepository, SchoolRepository schoolRepository, UserRepository userRepository) {
        this.staffRepository = staffRepository;
        this.schoolRepository = schoolRepository;
        this.userRepository = userRepository;
    }

    public List<StaffResponse> getStaff(UUID schoolId) {
        List<Staff> staff = schoolId == null
                ? staffRepository.findAllByOrderByCreatedAtDesc()
                : staffRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId);

        return staff.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public StaffResponse createStaff(StaffRequest request) {
        validateRequiredFields(request);

        School school = schoolRepository.findById(request.schoolId())
                .orElseThrow(() -> new IllegalStateException("School not found"));
        User user = userRepository.findByEmail(request.email())
                .orElseGet(() -> createUserForStaff(request));

        if (staffRepository.findByUserIdAndSchoolId(user.getId(), school.getId()).isPresent()) {
            throw new IllegalStateException("This user is already staff for this school");
        }

        user.setRole(request.role());

        Staff staff = new Staff();
        staff.setSchool(school);
        staff.setUser(user);
        applyRequest(staff, request);

        return mapToResponse(staffRepository.save(staff));
    }

    @Transactional
    public StaffResponse updateStaff(UUID staffId, StaffRequest request) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalStateException("Staff member not found"));

        if (request.schoolId() != null) {
            School school = schoolRepository.findById(request.schoolId())
                    .orElseThrow(() -> new IllegalStateException("School not found"));
            staff.setSchool(school);
        }

        User user = staff.getUser();
        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            userRepository.findByEmail(request.email()).ifPresent(existing -> {
                throw new IllegalStateException("Email already taken!");
            });
            user.setEmail(request.email());
        }
        if (request.role() != null) {
            user.setRole(request.role());
        }

        applyRequest(staff, request);
        return mapToResponse(staff);
    }

    public void deleteStaff(UUID staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalStateException("Staff member not found"));
        staffRepository.deleteById(staff.getId());
    }

    private void validateRequiredFields(StaffRequest request) {
        if (request.schoolId() == null || isBlank(request.firstName()) || isBlank(request.lastName())
                || isBlank(request.email()) || request.role() == null) {
            throw new IllegalStateException("schoolId, firstName, lastName, email, and role are required");
        }
    }

    private User createUserForStaff(StaffRequest request) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setPassword(generateTemporaryPassword());
        return userRepository.save(user);
    }

    private void applyRequest(Staff staff, StaffRequest request) {
        if (request.role() != null) {
            staff.setRole(request.role());
            staff.getUser().setRole(request.role());
        }
        if (request.phone() != null) {
            staff.setPhone(request.phone());
        }
        if (request.hireDate() != null) {
            staff.setHireDate(request.hireDate());
        }
        if (request.status() != null) {
            staff.setStatus(request.status());
        } else if (staff.getStatus() == null) {
            staff.setStatus(StaffStatus.ACTIVE);
        }
    }

    private StaffResponse mapToResponse(Staff staff) {
        User user = staff.getUser();
        return new StaffResponse(
                staff.getId(),
                staff.getSchool().getId(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                staff.getPhone(),
                staff.getRole(),
                staff.getHireDate(),
                staff.getStatus(),
                staff.getCreatedAt(),
                staff.getUpdatedAt()
        );
    }

    private String generateTemporaryPassword() {
        byte[] bytes = new byte[18];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
