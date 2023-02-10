package teammates.storage.sqlentity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Represents an Instructor entity.
 */
@Entity
@Table(name = "Instructors")
// Might have to change this. Should follow User's PK. Same for Student
@PrimaryKeyJoinColumn(name = "userId")
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private String registrationKey;

    @Column(nullable = false)
    private boolean isDisplayedToStudents;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private instructorPrivilegesAsText;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;
}
