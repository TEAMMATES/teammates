package teammates.common.datatransfer.statistics;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

/**
 * Course-wide contribution statistics for session results.
 */
public class FeedbackContributionCourseWideStatistics extends FeedbackQuestionResultsStatistics {
    private List<CourseWideRow> rows = new ArrayList<>();

    public FeedbackContributionCourseWideStatistics() {
        super(FeedbackQuestionType.CONTRIB);
    }

    public List<CourseWideRow> getRows() {
        return rows;
    }

    public void setRows(List<CourseWideRow> rows) {
        this.rows = rows;
    }

    /**
     * One row in the course-wide contribution statistics table.
     */
    public static class CourseWideRow {
        private String teamName;
        private String recipientName;
        @Nullable
        private String recipientEmail;
        private int claimed;
        private int perceived;
        private int diff;
        private List<Integer> ratingsReceived = new ArrayList<>();

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getRecipientName() {
            return recipientName;
        }

        public void setRecipientName(String recipientName) {
            this.recipientName = recipientName;
        }

        @Nullable
        public String getRecipientEmail() {
            return recipientEmail;
        }

        public void setRecipientEmail(@Nullable String recipientEmail) {
            this.recipientEmail = recipientEmail;
        }

        public int getClaimed() {
            return claimed;
        }

        public void setClaimed(int claimed) {
            this.claimed = claimed;
        }

        public int getPerceived() {
            return perceived;
        }

        public void setPerceived(int perceived) {
            this.perceived = perceived;
        }

        public int getDiff() {
            return diff;
        }

        public void setDiff(int diff) {
            this.diff = diff;
        }

        public List<Integer> getRatingsReceived() {
            return ratingsReceived;
        }

        public void setRatingsReceived(List<Integer> ratingsReceived) {
            this.ratingsReceived = ratingsReceived;
        }
    }
}
