package teammates.ui.output;

import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;
import teammates.storage.sqlentity.UsageStatistics;

/**
 * The API output format of {@link UsageStatisticsAttributes}.
 */
public class UsageStatisticsData extends ApiOutput {

    private final long startTime;
    private final int timePeriod;
    private final int numResponses;
    private final int numCourses;
    private final int numStudents;
    private final int numInstructors;
    private final int numAccountRequests;
    private final int numEmails;
    private final int numSubmissions;

    public UsageStatisticsData(UsageStatisticsAttributes attributes) {
        this.startTime = attributes.getStartTime().toEpochMilli();
        this.timePeriod = attributes.getTimePeriod();
        this.numResponses = attributes.getNumResponses();
        this.numCourses = attributes.getNumCourses();
        this.numStudents = attributes.getNumStudents();
        this.numInstructors = attributes.getNumInstructors();
        this.numAccountRequests = attributes.getNumAccountRequests();
        this.numEmails = attributes.getNumEmails();
        this.numSubmissions = attributes.getNumSubmissions();
    }

    public UsageStatisticsData(UsageStatistics usageStatistics) {
        this.startTime = usageStatistics.getStartTime().toEpochMilli();
        this.timePeriod = usageStatistics.getTimePeriod();
        this.numResponses = usageStatistics.getNumResponses();
        this.numCourses = usageStatistics.getNumCourses();
        this.numStudents = usageStatistics.getNumStudents();
        this.numInstructors = usageStatistics.getNumInstructors();
        this.numAccountRequests = usageStatistics.getNumAccountRequests();
        this.numEmails = usageStatistics.getNumEmails();
        this.numSubmissions = usageStatistics.getNumSubmissions();
    }

    public long getStartTime() {
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

}
