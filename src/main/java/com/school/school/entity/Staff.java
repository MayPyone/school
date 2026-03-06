package com.school.school.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "staffs",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","school_id"}))
public class Staff extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    public Staff() {}

    public StaffRole getRole() {
        return role;
    }

    public void setRole(StaffRole role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }
}