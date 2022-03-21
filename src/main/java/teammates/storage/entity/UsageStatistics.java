package teammates.storage.entity;

import java.time.Instant;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Translate;

/**
 * Represents a system usage statistics for a specified period of time.
 *
 * <p>Note that "system usage" here is defined as user-facing usages, such as number of entities created
 * and number of actions, as opposed to system resources such as hardware and network.
 */
@Entity
@Index
public class UsageStatistics extends BaseEntity {

    @Id
    private String id;

    @Translate(InstantTranslatorFactory.class)
    private Instant startTime;
    private int timePeriod;

    private int numResponses;
    private int numCourses;
    private int numStudents;
    private int numInstructors;
    private int numAccountRequests;
    private int numEmails;
    private int numSubmissions;

    @SuppressWarnings("unused")
    private UsageStatistics() {
        // required by Objectify
    }

    public UsageStatistics(Instant startTime, int timePeriod, int numResponses, int numCourses, int numStudents,
                           int numInstructors, int numAccountRequests, int numEmails, int numSubmissions) {
        this.id = generateId(startTime, timePeriod);
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

    /**
     * Generates a unique ID for the usage statistics object.
     */
    public static String generateId(Instant startTime, int timePeriod) {
        return startTime.toEpochMilli() + "%" + timePeriod;
    }

}
