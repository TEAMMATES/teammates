package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the preview data for a deletion operation, showing what will be affected.
 */
public class DeletionPreviewData {

    private EntityType entityType;
    private String entityIdentifier;
    private int coursesAffected;
    private int studentsAffected;
    private int instructorsAffected;
    private int feedbackSessionsAffected;
    private int feedbackQuestionsAffected;
    private int feedbackResponsesAffected;
    private int feedbackCommentsAffected;
    private int deadlineExtensionsAffected;
    private int accountRequestsAffected;
    private int accountsAffected;
    private int notificationsAffected;

    private boolean isLastInstructor;
    private boolean willOrphanCourse;
    private List<String> warnings;
    private List<CascadedDeletionInfo> cascadedDeletions;

    /**
     * Enum representing the type of entity being deleted.
     */
    public enum EntityType {
        COURSE,
        STUDENT,
        INSTRUCTOR,
        ACCOUNT,
        FEEDBACK_SESSION,
        NOTIFICATION,
        ACCOUNT_REQUEST
    }

    public DeletionPreviewData(EntityType entityType, String entityIdentifier) {
        this.entityType = entityType;
        this.entityIdentifier = entityIdentifier;
        this.coursesAffected = 0;
        this.studentsAffected = 0;
        this.instructorsAffected = 0;
        this.feedbackSessionsAffected = 0;
        this.feedbackQuestionsAffected = 0;
        this.feedbackResponsesAffected = 0;
        this.feedbackCommentsAffected = 0;
        this.deadlineExtensionsAffected = 0;
        this.accountRequestsAffected = 0;
        this.accountsAffected = 0;
        this.notificationsAffected = 0;
        this.isLastInstructor = false;
        this.willOrphanCourse = false;
        this.warnings = new ArrayList<>();
        this.cascadedDeletions = new ArrayList<>();
    }

    // Getters and Setters

    public EntityType getEntityType() {
        return entityType;
    }

    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    public int getCoursesAffected() {
        return coursesAffected;
    }

    public void setCoursesAffected(int coursesAffected) {
        this.coursesAffected = coursesAffected;
    }

    public int getStudentsAffected() {
        return studentsAffected;
    }

    public void setStudentsAffected(int studentsAffected) {
        this.studentsAffected = studentsAffected;
    }

    public int getInstructorsAffected() {
        return instructorsAffected;
    }

    public void setInstructorsAffected(int instructorsAffected) {
        this.instructorsAffected = instructorsAffected;
    }

    public int getFeedbackSessionsAffected() {
        return feedbackSessionsAffected;
    }

    public void setFeedbackSessionsAffected(int feedbackSessionsAffected) {
        this.feedbackSessionsAffected = feedbackSessionsAffected;
    }

    public int getFeedbackQuestionsAffected() {
        return feedbackQuestionsAffected;
    }

    public void setFeedbackQuestionsAffected(int feedbackQuestionsAffected) {
        this.feedbackQuestionsAffected = feedbackQuestionsAffected;
    }

    public int getFeedbackResponsesAffected() {
        return feedbackResponsesAffected;
    }

    public void setFeedbackResponsesAffected(int feedbackResponsesAffected) {
        this.feedbackResponsesAffected = feedbackResponsesAffected;
    }

    public int getFeedbackCommentsAffected() {
        return feedbackCommentsAffected;
    }

    public void setFeedbackCommentsAffected(int feedbackCommentsAffected) {
        this.feedbackCommentsAffected = feedbackCommentsAffected;
    }

    public int getDeadlineExtensionsAffected() {
        return deadlineExtensionsAffected;
    }

    public void setDeadlineExtensionsAffected(int deadlineExtensionsAffected) {
        this.deadlineExtensionsAffected = deadlineExtensionsAffected;
    }

    public int getAccountRequestsAffected() {
        return accountRequestsAffected;
    }

    public void setAccountRequestsAffected(int accountRequestsAffected) {
        this.accountRequestsAffected = accountRequestsAffected;
    }

    public int getAccountsAffected() {
        return accountsAffected;
    }

    public void setAccountsAffected(int accountsAffected) {
        this.accountsAffected = accountsAffected;
    }

    public int getNotificationsAffected() {
        return notificationsAffected;
    }

    public void setNotificationsAffected(int notificationsAffected) {
        this.notificationsAffected = notificationsAffected;
    }

    public boolean isLastInstructor() {
        return isLastInstructor;
    }

    public void setLastInstructor(boolean lastInstructor) {
        isLastInstructor = lastInstructor;
    }

    public boolean willOrphanCourse() {
        return willOrphanCourse;
    }

    public void setWillOrphanCourse(boolean willOrphanCourse) {
        this.willOrphanCourse = willOrphanCourse;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public List<CascadedDeletionInfo> getCascadedDeletions() {
        return cascadedDeletions;
    }

    public void addCascadedDeletion(EntityType entityType, int count, String description) {
        if (count > 0) {
            this.cascadedDeletions.add(new CascadedDeletionInfo(entityType, count, description));
        }
    }

    /**
     * Returns the total number of entities that will be affected by this deletion.
     */
    public int getTotalEntitiesAffected() {
        return coursesAffected + studentsAffected + instructorsAffected
                + feedbackSessionsAffected + feedbackQuestionsAffected
                + feedbackResponsesAffected + feedbackCommentsAffected
                + deadlineExtensionsAffected + accountRequestsAffected
                + accountsAffected + notificationsAffected;
    }

    /**
     * Returns whether this deletion has any warnings.
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty() || isLastInstructor || willOrphanCourse;
    }

    /**
     * Represents information about cascaded deletions.
     */
    public static class CascadedDeletionInfo {
        private EntityType entityType;
        private int count;
        private String description;

        public CascadedDeletionInfo(EntityType entityType, int count, String description) {
            this.entityType = entityType;
            this.count = count;
            this.description = description;
        }

        public EntityType getEntityType() {
            return entityType;
        }

        public int getCount() {
            return count;
        }

        public String getDescription() {
            return description;
        }
    }
}
