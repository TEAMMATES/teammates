package teammates.ui.output;

import java.util.HashMap;
import java.util.Map;

import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * The API output format for deadline extensions of a {@link FeedbackSession}.
 */
public class FeedbackSessionDeadlineExtensionsData extends ApiOutput {
    private Map<String, Long> studentDeadlines;
    private Map<String, Long> instructorDeadlines;

    public FeedbackSessionDeadlineExtensionsData(FeedbackSession feedbackSession) {
        String timeZone = feedbackSession.getCourse().getTimeZone();
        this.studentDeadlines = new HashMap<>();
        this.instructorDeadlines = new HashMap<>();

        for (DeadlineExtension de : feedbackSession.getDeadlineExtensions()) {
            if (de.getUser() instanceof Student) {
                this.studentDeadlines.put(de.getUser().getEmail(),
                        TimeHelper.getMidnightAdjustedInstantBasedOnZone(de.getEndTime(), timeZone, true).toEpochMilli());
            }
            if (de.getUser() instanceof Instructor) {
                this.instructorDeadlines.put(de.getUser().getEmail(),
                        TimeHelper.getMidnightAdjustedInstantBasedOnZone(de.getEndTime(), timeZone, true).toEpochMilli());
            }
        }
    }

    public Map<String, Long> getStudentDeadlines() {
        return studentDeadlines;
    }

    public Map<String, Long> getInstructorDeadlines() {
        return instructorDeadlines;
    }

    public void setStudentDeadlines(Map<String, Long> studentDeadlines) {
        this.studentDeadlines = studentDeadlines;
    }

    public void setInstructorDeadlines(Map<String, Long> instructorDeadlines) {
        this.instructorDeadlines = instructorDeadlines;
    }

    /**
     * Hides deadline information from a student (shows only their own deadline).
     */
    public void hideInformationForStudent() {
        instructorDeadlines.clear();
    }

    /**
     * Hides deadline information from a student (shows only their own deadline).
     */
    public void hideInformationForStudent(String studentEmail) {
        studentDeadlines.keySet().removeIf(email -> !(email.equals(studentEmail)));
        instructorDeadlines.clear();
    }

    /**
     * Hides deadline information from an instructor without appropriate privilege.
     */
    public void hideInformationForInstructor() {
        studentDeadlines.clear();
    }

    /**
     * Hides deadline information from an instructor without appropriate privilege.
     */
    public void hideInformationForInstructor(String instructorEmail) {
        instructorDeadlines.keySet().removeIf(email -> !(email.equals(instructorEmail)));
        studentDeadlines.clear();
    }
}
