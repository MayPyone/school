package com.school.school.service;

import com.school.school.entity.School;
import com.school.school.entity.Staff;
import com.school.school.entity.StaffRole;
import com.school.school.entity.StaffStatus;
import com.school.school.entity.User;
import com.school.school.entity.UserRole;
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

    public List<StaffResponse> getStaff(String schoolId) {
        UUID resolvedSchoolId = resolveSchoolId(schoolId);
        List<Staff> staff = resolvedSchoolId == null
                ? staffRepository.findAllByOrderByCreatedAtDesc()
                : staffRepository.findBySchoolIdOrderByCreatedAtDesc(resolvedSchoolId);

        return staff.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<StaffResponse> getActiveStaff(String schoolId) {
        UUID resolvedSchoolId = resolveSchoolId(schoolId);
        if (resolvedSchoolId == null) {
            return List.of();
        }

        return staffRepository.findBySchoolIdOrderByCreatedAtDesc(resolvedSchoolId)
                .stream()
                .filter(staff -> staff.getStatus() == StaffStatus.ACTIVE)
                .map(this::mapToResponse)
                .toList();
    }

    private UUID resolveSchoolId(String idOrCustomizeSchoolId) {
        if (idOrCustomizeSchoolId == null || idOrCustomizeSchoolId.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(idOrCustomizeSchoolId);
        } catch (IllegalArgumentException ignored) {
            return schoolRepository.findByCustomizeSchoolId(idOrCustomizeSchoolId)
                    .orElseThrow(() -> new IllegalStateException("School not found"))
                    .getId();
        }
    }

    @Transactional
    public StaffResponse createStaff(UserRole actorRole, StaffRequest request) {
        validateRequiredFields(request);
        validateRoleAssignment(actorRole, request.role());

        School school = schoolRepository.findById(request.schoolId())
                .orElseThrow(() -> new IllegalStateException("School not found"));
        User user = userRepository.findByEmail(request.email())
                .orElseGet(() -> createUserForStaff(request));
        validateExistingUserRoleAssignment(actorRole, user, request.role());

        if (staffRepository.findByUserIdAndSchoolId(user.getId(), school.getId()).isPresent()) {
            throw new IllegalStateException("This user is already staff for this school");
        }

        user.setRole(UserRole.fromStaffRole(request.role()));

        Staff staff = new Staff();
        staff.setSchool(school);
        staff.setUser(user);
        applyRequest(staff, request);

        return mapToResponse(staffRepository.save(staff));
    }

    @Transactional
    public StaffResponse updateStaff(UserRole actorRole, UUID staffId, StaffRequest request) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalStateException("Staff member not found"));
        validateStaffUpdate(actorRole, staff, request);

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
            user.setRole(UserRole.fromStaffRole(request.role()));
        }

        applyRequest(staff, request);
        return mapToResponse(staff);
    }

    @Transactional
    public StaffResponse revokeStaff(UserRole actorRole, UUID staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalStateException("Staff member not found"));
        validateStaffRemoval(actorRole, staff, "revoke");

        staff.setStatus(StaffStatus.INACTIVE);
        User user = staff.getUser();
        List<Staff> activeStaffMemberships = staffRepository.findByUserId(user.getId())
                .stream()
                .filter(membership -> !membership.getId().equals(staff.getId()))
                .filter(membership -> membership.getStatus() == StaffStatus.ACTIVE)
                .toList();

        if (activeStaffMemberships.isEmpty()) {
            user.setRole(UserRole.END_USER);
        } else {
            user.setRole(UserRole.fromStaffRole(activeStaffMemberships.get(0).getRole()));
        }

        return mapToResponse(staff);
    }

    @Transactional
    public StaffResponse restoreStaff(UserRole actorRole, UUID staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalStateException("Staff member not found"));
        validateStaffManagement(actorRole, staff);

        staff.setStatus(StaffStatus.ACTIVE);
        staff.getUser().setRole(UserRole.fromStaffRole(staff.getRole()));

        return mapToResponse(staff);
    }

    public void deleteStaff(UserRole actorRole, UUID staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalStateException("Staff member not found"));
        validateStaffRemoval(actorRole, staff, "delete");
        staffRepository.deleteById(staff.getId());
    }

    private void validateStaffUpdate(UserRole actorRole, Staff staff, StaffRequest request) {
        validateStaffManagement(actorRole, staff);

        if (request.role() != null) {
            validateRoleAssignment(actorRole, request.role());
            if (staff.getRole() == StaffRole.SUPER_ADMIN && request.role() != StaffRole.SUPER_ADMIN) {
                throw new IllegalStateException("Super admin cannot be removed");
            }
            if (staff.getRole() == StaffRole.ADMIN && request.role() != StaffRole.ADMIN && actorRole != UserRole.SUPER_ADMIN) {
                throw new IllegalStateException("Only super admin can change an admin role");
            }
        }

        if (request.status() == StaffStatus.INACTIVE) {
            validateStaffRemoval(actorRole, staff, "revoke");
        }
    }

    private void validateStaffRemoval(UserRole actorRole, Staff staff, String action) {
        if (staff.getRole() == StaffRole.SUPER_ADMIN) {
            throw new IllegalStateException("Super admin cannot be removed");
        }

        validateStaffManagement(actorRole, staff);

        if (staff.getRole() == StaffRole.ADMIN && actorRole != UserRole.SUPER_ADMIN) {
            throw new IllegalStateException("Only super admin can " + action + " an admin");
        }
    }

    private void validateStaffManagement(UserRole actorRole, Staff staff) {
        if (actorRole != UserRole.SUPER_ADMIN && staff.getRole() == StaffRole.ADMIN) {
            throw new IllegalStateException("Only super admin can manage an admin");
        }
    }

    private void validateRoleAssignment(UserRole actorRole, StaffRole targetRole) {
        if ((targetRole == StaffRole.SUPER_ADMIN || targetRole == StaffRole.ADMIN) && actorRole != UserRole.SUPER_ADMIN) {
            throw new IllegalStateException("Only super admin can assign admin roles");
        }
    }

    private void validateExistingUserRoleAssignment(UserRole actorRole, User user, StaffRole targetRole) {
        if (user.getRole() == UserRole.SUPER_ADMIN && targetRole != StaffRole.SUPER_ADMIN) {
            throw new IllegalStateException("Super admin cannot be removed");
        }

        if (user.getRole() == UserRole.ADMIN && actorRole != UserRole.SUPER_ADMIN) {
            throw new IllegalStateException("Only super admin can manage an admin");
        }
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
        user.setRole(UserRole.fromStaffRole(request.role()));
        user.setPassword(generateTemporaryPassword());
        return userRepository.save(user);
    }

    private void applyRequest(Staff staff, StaffRequest request) {
        if (request.role() != null) {
            staff.setRole(request.role());
            staff.getUser().setRole(UserRole.fromStaffRole(request.role()));
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
