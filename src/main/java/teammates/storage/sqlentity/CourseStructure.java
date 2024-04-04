package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a Course Structure.
 */
@Entity
@Table(name = "CourseStructures")
public class CourseStructure extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    @UpdateTimestamp
    private Instant updatedAt;

    protected CourseStructure() {
        // required by Hibernate
    }

    public CourseStructure(UUID id, Course course, String name) {
        this.id = id;
        this.course = course;
        this.name = name;
    }

    public UUID getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "CourseStructure [id=" + id + ", course=" + course.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        return errors;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            CourseStructure otherCourseStructure = (CourseStructure) other;
            return Objects.equals(this.getId(), otherCourseStructure.getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}