package com.school.school.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name="school_id", nullable = false)
    private UUID school_id;

    @Column(name="title" , nullable = false)
    private  String title;

    @Column (name="level_id", nullable = false)
    private Byte level_id;

    @Column (name="category", nullable = false)
    private  String category;

    @Column (name="content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSchool_id() {
        return school_id;
    }

    public void setSchool_id(UUID school_id) {
        this.school_id = school_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Byte getLevel_id() {
        return level_id;
    }

    public void setLevel_id(Byte level_id) {
        this.level_id = level_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
