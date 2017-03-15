package teammates.ui.template;

/**
 * Data model for an individual question in the copying question modal on instructorFeedbackEdit.jsp.
 */
public class FeedbackQuestionTableRow {
    private String courseId;
    private String fsName;
    private String qnType;
    private String qnText;
    private String qnId;

    public FeedbackQuestionTableRow(String courseId, String fsName, String qnType, String qnText, String qnId) {
        this.courseId = courseId;
        this.fsName = fsName;
        this.qnType = qnType;
        this.qnText = qnText;
        this.qnId = qnId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFsName() {
        return fsName;
    }

    public String getQnType() {
        return qnType;
    }

    public String getQnText() {
        return qnText;
    }

    public String getQnId() {
        return qnId;
    }

}
