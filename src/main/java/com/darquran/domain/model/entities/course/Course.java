package com.darquran.domain.model.entities.course;


import com.darquran.domain.model.enums.courses.CourseLevel;
import com.darquran.domain.model.enums.courses.CourseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true)
    private String slug;

    @Column(length = 2000)
    private String description;

    private String miniature;

    private boolean isPublic = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CourseStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CourseLevel level;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")

    @Builder.Default
    private List<Lesson> lessons = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = CourseStatus.DRAFT; // Default status
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}