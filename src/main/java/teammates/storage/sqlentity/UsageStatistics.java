package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a system usage statistics for a specified period of time.
 *
 * <p>Note that "system usage" here is defined as user-facing usages, such as number of entities created
 * and number of actions, as opposed to system resources such as hardware and network.
 */
@Entity
@Table(name = "UsageStatistics")
public class UsageStatistics extends BaseEntity {
    @Id
    private String id;

    @Column
    private Instant startTime;

    @Column
    private int timePeriod;
    @Column
    private int numResponses;
    @Column
    private int numCourses;
    @Column
    private int numStudents;
    @Column
    private int numInstructors;
    @Column
    private int numAccountRequests;
    @Column
    private int numEmails;
    @Column
    private int numSubmissions;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    protected UsageStatistics() {
        // required by Hibernate
    }

    private UsageStatistics(
            Instant startTime, int timePeriod, int numResponses, int numCourses,
            int numStudents, int numInstructors, int numAccountRequests, int numEmails, int numSubmissions) {
        this.startTime = startTime;
        this.timePeriod = timePeriod;
        this.numResponses = numResponses;
        this.numCourses = numCourses;
        this.numStudents = numStudents;
        this.numInstructors = numInstructors;
        this.numAccountRequests = numAccountRequests;
        this.numEmails = numEmails;
        this.numSubmissions = numSubmissions;
    }

    public String getId() {
        return id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public int getTimePeriod() {
        return timePeriod;
    }

    public int getNumResponses() {
        return numResponses;
    }

    public int getNumCourses() {
        return numCourses;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public int getNumInstructors() {
        return numInstructors;
    }

    public int getNumAccountRequests() {
        return numAccountRequests;
    }

    public int getNumEmails() {
        return numEmails;
    }

    public int getNumSubmissions() {
        return numSubmissions;
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
    public void sanitizeForSaving() {
        // required by BaseEntity
    }

    @Override
    public List<String> getInvalidityInfo() {
        return new ArrayList<>();
    }
}
