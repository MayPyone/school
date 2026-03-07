package com.school.school.entity;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User extends BaseEntity{
    @Column(name="first_name", nullable = false)
    private String firstName;

    @Column(name="last_name", nullable = false)
    private  String lastName;

    private String email;

    private String password;



    @Enumerated(EnumType.STRING) // <--- THIS IS THE FIX
    @Column(name = "role", nullable = false)
    private StaffRole role;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public StaffRole getRole() {
        return role;
    }

    public void setRole(StaffRole role) {
        this.role = role;
    }
}
