package teammates.ui.template;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

public class InstructorFeedbackResultsQuestionTable {

    private String courseId;
    private String feedbackSessionName;

    private String panelClass;
    private String ajaxClass;

    private List<InstructorFeedbackResultsResponseRow> responses;

    // store the attributes of the question for the non-display purposes
    // such as form inputs
    private FeedbackQuestionAttributes question;

    private String questionText;
    private String additionalInfoText;
    private String questionStatisticsTable;

    private boolean isHasResponses;
    private boolean isShowResponseRows;

    private boolean isCollapsible;
    private boolean isBoldQuestionNumber;

    private List<ElementTag> columns;
    private Map<String, Boolean> isColumnSortable;

    public InstructorFeedbackResultsQuestionTable(
                                          boolean isHasResponses,
                                          String questionStatisticsHtml,
                                          List<InstructorFeedbackResultsResponseRow> responseRows,
                                          FeedbackQuestionAttributes question,
                                          String questionText,
                                          String additionalInfoText,
                                          List<ElementTag> columns,
                                          Map<String, Boolean> isColumnSortable) {
        this.courseId = question.courseId;
        this.feedbackSessionName = question.feedbackSessionName;

        this.questionStatisticsTable = questionStatisticsHtml;
        this.responses = responseRows;

        this.isHasResponses = isHasResponses;

        this.question = question;

        this.questionText = questionText;

        this.panelClass = "panel-info";

        this.additionalInfoText = additionalInfoText;

        this.columns = columns;

        this.isBoldQuestionNumber = true;
        this.isColumnSortable = isColumnSortable;
    }

    public List<InstructorFeedbackResultsResponseRow> getResponses() {
        return responses;
    }

    public String getPanelClass() {
        return panelClass;
    }

    public FeedbackQuestionAttributes getQuestion() {
        return question;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getAdditionalInfoText() {
        return additionalInfoText;
    }

    public String getQuestionStatisticsTable() {
        return questionStatisticsTable;
    }

    public boolean isHasResponses() {
        return isHasResponses;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public boolean isShowResponseRows() {
        return isShowResponseRows;
    }

    public List<ElementTag> getColumns() {
        return columns;
    }

    public boolean isCollapsible() {
        return isCollapsible;
    }

    public boolean isBoldQuestionNumber() {
        return isBoldQuestionNumber;
    }

    public Map<String, Boolean> getIsColumnSortable() {
        return isColumnSortable;
    }

    public void setShowResponseRows(boolean isShowResponseRows) {
        this.isShowResponseRows = isShowResponseRows;
    }

    public void setCollapsible(boolean isCollapsible) {
        this.isCollapsible = isCollapsible;
    }

    public void setBoldQuestionNumber(boolean isBoldQuestionNumber) {
        this.isBoldQuestionNumber = isBoldQuestionNumber;
    }

    public void setAjaxClass(String ajaxClass) {
        this.ajaxClass = ajaxClass;
    }

    public String getAjaxClass() {
        return ajaxClass;
    }

    public void setHasResponses(boolean isHasResponses) {
        this.isHasResponses = isHasResponses;
    }

    public static void sortByQuestionNumber(List<InstructorFeedbackResultsQuestionTable> questionTables) {
        questionTables.sort(Comparator.comparing(questionTable -> questionTable.question.questionNumber));
    }

}
