package teammates.ui.template;

import java.util.List;

/**
 * Data model for the copy question modal on instructorFeedbackEdit.jsp
 *  
 *
 */
public class FeedbackQuestionCopyTable {
    private String courseId;
    private String fsName;
    private List<FeedbackQuestionTableRow> questionRows;

    public FeedbackQuestionCopyTable(String courseId, String fsName, List<FeedbackQuestionTableRow> questionRows) {
        this.courseId = courseId;
        this.fsName = fsName;
        this.questionRows = questionRows;
    }

    public List<FeedbackQuestionTableRow> getQuestionRows() {
        return questionRows;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFsName() {
        return fsName;
    }
 
    
}
