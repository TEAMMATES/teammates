package teammates.ui.output;

/**
 * The API output format for usage statistics for a single time bucket.
 */
public class UsageStatisticsData implements ApiOutput {

    private final long startTime;
    private final int numResponses;
    private final int numCourses;
    private final int numStudents;
    private final int numInstructors;
    private final int numAccountVerificationRequests;

    public UsageStatisticsData(
            long startTime, int numResponses, int numCourses,
            int numStudents, int numInstructors, int numAccountVerificationRequests) {
        this.startTime = startTime;
        this.numResponses = numResponses;
        this.numCourses = numCourses;
        this.numStudents = numStudents;
        this.numInstructors = numInstructors;
        this.numAccountVerificationRequests = numAccountVerificationRequests;
    }

    public long getStartTime() {
        return startTime;
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

    public int getNumAccountVerificationRequests() {
        return numAccountVerificationRequests;
    }

}
