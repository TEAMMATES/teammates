package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.DeletionPreviewData;

/**
 * Output format for deletion preview endpoints.
 */
public class DeletionPreviewOutput extends ApiOutput {

    private final String entityType;
    private final String entityIdentifier;
    private final int coursesAffected;
    private final int studentsAffected;
    private final int instructorsAffected;
    private final int feedbackSessionsAffected;
    private final int feedbackQuestionsAffected;
    private final int feedbackResponsesAffected;
    private final int feedbackCommentsAffected;
    private final int deadlineExtensionsAffected;
    private final int accountRequestsAffected;
    private final int accountsAffected;
    private final int notificationsAffected;
    private final int totalEntitiesAffected;
    private final boolean isLastInstructor;
    private final boolean willOrphanCourse;
    private final boolean hasWarnings;
    private final List<String> warnings;
    private final List<CascadedDeletionOutput> cascadedDeletions;

    /**
     * Represents information about cascaded deletions in the output.
     */
    public static class CascadedDeletionOutput {
        private final String entityType;
        private final int count;
        private final String description;

        public CascadedDeletionOutput(DeletionPreviewData.CascadedDeletionInfo info) {
            this.entityType = info.getEntityType().toString();
            this.count = info.getCount();
            this.description = info.getDescription();
        }

        public String getEntityType() {
            return entityType;
        }

        public int getCount() {
            return count;
        }

        public String getDescription() {
            return description;
        }
    }

    public DeletionPreviewOutput(DeletionPreviewData data) {
        this.entityType = data.getEntityType().toString();
        this.entityIdentifier = data.getEntityIdentifier();
        this.coursesAffected = data.getCoursesAffected();
        this.studentsAffected = data.getStudentsAffected();
        this.instructorsAffected = data.getInstructorsAffected();
        this.feedbackSessionsAffected = data.getFeedbackSessionsAffected();
        this.feedbackQuestionsAffected = data.getFeedbackQuestionsAffected();
        this.feedbackResponsesAffected = data.getFeedbackResponsesAffected();
        this.feedbackCommentsAffected = data.getFeedbackCommentsAffected();
        this.deadlineExtensionsAffected = data.getDeadlineExtensionsAffected();
        this.accountRequestsAffected = data.getAccountRequestsAffected();
        this.accountsAffected = data.getAccountsAffected();
        this.notificationsAffected = data.getNotificationsAffected();
        this.totalEntitiesAffected = data.getTotalEntitiesAffected();
        this.isLastInstructor = data.isLastInstructor();
        this.willOrphanCourse = data.willOrphanCourse();
        this.hasWarnings = data.hasWarnings();
        this.warnings = data.getWarnings();
        this.cascadedDeletions = data.getCascadedDeletions().stream()
                .map(CascadedDeletionOutput::new)
                .collect(Collectors.toList());
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    public int getCoursesAffected() {
        return coursesAffected;
    }

    public int getStudentsAffected() {
        return studentsAffected;
    }

    public int getInstructorsAffected() {
        return instructorsAffected;
    }

    public int getFeedbackSessionsAffected() {
        return feedbackSessionsAffected;
    }

    public int getFeedbackQuestionsAffected() {
        return feedbackQuestionsAffected;
    }

    public int getFeedbackResponsesAffected() {
        return feedbackResponsesAffected;
    }

    public int getFeedbackCommentsAffected() {
        return feedbackCommentsAffected;
    }

    public int getDeadlineExtensionsAffected() {
        return deadlineExtensionsAffected;
    }

    public int getAccountRequestsAffected() {
        return accountRequestsAffected;
    }

    public int getAccountsAffected() {
        return accountsAffected;
    }

    public int getNotificationsAffected() {
        return notificationsAffected;
    }

    public int getTotalEntitiesAffected() {
        return totalEntitiesAffected;
    }

    public boolean isLastInstructor() {
        return isLastInstructor;
    }

    public boolean isWillOrphanCourse() {
        return willOrphanCourse;
    }

    public boolean isHasWarnings() {
        return hasWarnings;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<CascadedDeletionOutput> getCascadedDeletions() {
        return cascadedDeletions;
    }
}
