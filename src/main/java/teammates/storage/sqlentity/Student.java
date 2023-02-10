package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
    // [X] Map FKs (Done for Student)
    // [ ] Ancestor User
    // [ ] Fill other CourseStudent related methods
    // [ ] Fill sanitizeForSaving and getInvalidityInfo if necessary

    // should this be auto incremented or generateId from CourseStudent
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    // note: set @OneToMany(mappedBy = "student") in Team
    @ManyToOne
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "TEAM_ID_FK"), referencedColumnName = "id", nullable = false)
    private Team team;

    // to cascade?
    // note: to place @OneToOne annotation in User class for bidirectional r/s
    @OneToOne
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "USER_ID_FK"), referencedColumnName = "id", nullable = false)
    private User user;

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
        this.setTeam(builder.team);
        this.setUser(builder.user);
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        private Team team;
        private User user;
        private String comments;

        public StudentBuilder(int id) {
            this.id = id;
        }

        public StudentBuilder withTeam(Team team) {
            this.team = team;
            return this;
        }

        public StudentBuilder withUser(User user) {
            this.user = user;
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
