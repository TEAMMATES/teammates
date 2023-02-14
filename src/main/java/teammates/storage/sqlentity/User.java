package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne
    @JoinColumn(name = "accountId")
    private Account account;

    @OneToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teamId")
    private Team team;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    protected User() {
        // required by Hibernate
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        return "User [id=" + id + ", account=" + account + ", course=" + course
                + ", team=" + team + ", name=" + name + ", email=" + email
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public int hashCode() {
        // User Id uniquely identifies a User
        return this.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (this.getClass() == obj.getClass()) {
            User otherUser = (User) obj;

            return Objects.equals(this.id, otherUser.id);
        } else {
            return false;
        }
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
}