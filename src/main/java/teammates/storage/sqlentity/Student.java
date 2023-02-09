package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.JsonUtils;

/**
 * An association class that represents the association Account -->
 * [enrolled in] --> Course.
 */
@Entity
@Table(name = "Student")
public class Student extends BaseEntity {
    // TODO
    // [ ] Map FKs
    // [ ] Ancestor User
    // [ ] Fill other CourseStudent related methods
    // [ ] Fill sanitizeForSaving and getInvalidityInfo if necessary
    
    // should this be auto incremented or generateId from CourseStudent
    @Id
    private int id;

    @Column(nullable = false)
    private int teamId;

    @Column(nullable = false)
    private int userId;

    @Column
    private String comments;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    protected Student() {
        // required by Hibernate
    }

    public Student(StudentBuilder builder) {
        this.setId(builder.id);
        this.setUserId(builder.userId);
        this.setTeamId(builder.teamId);
        this.setComments(builder.comments);

        if (createdAt == null) {
            this.setCreatedAt(Instant.now());
        } else {
            this.setCreatedAt(createdAt);
        }

        this.setUpdatedAt(updatedAt);
    }

    @Override
    public void sanitizeForSaving() {
        // TODO Auto-generated method stub
    }

    @Override
    public List<String> getInvalidityInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this, StudentAttributes.class);
    }

    @Override
    public int hashCode() {
        // Student Id uniquely identifies a Student
        return this.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (this.getClass() == obj.getClass()) {
            Student otherStudent = (Student) obj;

            return Objects.equals(this.id, otherStudent.id);
        } else {
            return false;
        }
    }

    /**
     * Builder for Student
     */
    public static class StudentBuilder {
        private int id;
        private int teamId;
        private int userId;
        private String comments;

        public StudentBuilder(int id) {
            this.id = id;
        }

        public StudentBuilder withTeamId(int teamId) {
            this.teamId = teamId;
            return this;
        }

        public StudentBuilder withUserId(int userId) {
            this.userId = userId;
            return this;
        }

        public StudentBuilder withComments(String comments) {
            this.comments = comments;
            return this;
        }

        public Student build() {
            return new Student(this);
        }
    }
}
