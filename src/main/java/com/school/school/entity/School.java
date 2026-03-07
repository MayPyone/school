package com.school.school.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.List;

@Entity
@Table(name = "schools")

public class School extends BaseEntity {

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolEmail() {
        return schoolEmail;
    }

    public void setSchoolEmail(String schoolEmail) {
        this.schoolEmail = schoolEmail;
    }

    public List<String> getSchoolAddress() {
        return schoolAddress;
    }

    public void setSchoolAddress( List<String>  schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @Column(name = "school_name", nullable = false)
    private String schoolName;

    @Column(name = "school_email", unique = true)
    private String schoolEmail;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "school_addresses", columnDefinition = "text[]")
    private List<String> schoolAddress;

    @Column(name = "logo_url")
    private String logoUrl;

    @JdbcTypeCode(SqlTypes.ARRAY) // Handles the TEXT[] in Postgres
    @Column(name = "phone_numbers", columnDefinition = "text[]")
    private List<String> phoneNumbers;

    private String description;

    @Column(name = "subtitle")
    private String subTitle;
}