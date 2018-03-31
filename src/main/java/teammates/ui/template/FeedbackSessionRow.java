package teammates.ui.template;

import java.util.List;

public class FeedbackSessionRow {
    private String feedbackSessionName;
    private String courseId;
    private List<QuestionTable> questionTables;

    public FeedbackSessionRow(String feedbackSessionName, String courseId,
                                    List<QuestionTable> questionTables) {
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;
        this.questionTables = questionTables;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getCourseId() {
        return courseId;
    }

    public List<QuestionTable> getQuestionTables() {
        return questionTables;
    }
}
