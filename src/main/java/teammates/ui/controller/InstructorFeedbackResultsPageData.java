package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.ui.template.InstructorResultsQuestionTable;
import teammates.ui.template.InstructorResultsResponseRow;
import teammates.ui.template.ModerationButton;

public class InstructorFeedbackResultsPageData extends PageData {
    public static final String EXCEEDING_RESPONSES_ERROR_MESSAGE = "Sorry, we could not retrieve results. "
                                                                 + "Please try again in a few minutes. If you continue to see this message, it could be because the report you are trying to display contains too much data to display in one page. e.g. more than 2,500 entries."
                                                                 + "<ul><li>If that is the case, you can still use the 'By question' report to view responses. You can also download the results as a spreadsheet. If you would like to see the responses in other formats (e.g. 'Group by - Giver'), you can try to divide the course into smaller sections so that we can display responses one section at a time.</li>"
                                                                 + "<li>If you believe the report you are trying to view is unlikely to have more than 2,500 entries, please contact us at <a href='mailto:teammates@comp.nus.edu.sg'>teammates@comp.nus.edu.sg</a> so that we can investigate.</li></ul>";

    
    public FeedbackSessionResultsBundle bundle = null;
    public InstructorAttributes instructor = null;
    public List<String> sections = null;
    public String selectedSection = null;
    public String sortType = null;
    public String groupByTeam = null;
    public String showStats = null;
    public int startIndex;
    private boolean shouldCollapsed;


    // used for html table ajax loading
    public String courseId = null;
    public String feedbackSessionName = null;
    public String ajaxStatus = null;
    public String sessionResultsHtmlTableAsString = null;

    
    List<InstructorResultsQuestionTable> questionPanels;  
    
    public InstructorFeedbackResultsPageData(AccountAttributes account) {
        super(account);
        startIndex = -1;
    }
    
    public void init(FeedbackSessionResultsBundle bundle) {
        this.bundle = bundle;
       
        
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionToResponseMap = bundle.getQuestionResponseMap();
        questionPanels = new ArrayList<InstructorResultsQuestionTable>();
        
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry : questionToResponseMap.entrySet()) {
            FeedbackQuestionAttributes question = entry.getKey();
            List<FeedbackResponseAttributes> responses = entry.getValue();
            
            questionPanels.add(buildQuestionTable(question, responses));
        }
        
    }
    
    private InstructorResultsQuestionTable buildQuestionTable(FeedbackQuestionAttributes question,
                                                              List<FeedbackResponseAttributes> responses) {
        
        String statisticsTable = question.getQuestionDetails().
                                              getQuestionResultStatisticsHtml(responses, 
                                                                              question, 
                                                                              this, 
                                                                              bundle, 
                                                                              "question");
        List<InstructorResultsResponseRow> responseRows = buildResponseRowsForExistingResponses(question, responses);
        
        InstructorResultsQuestionTable questionTable = new InstructorResultsQuestionTable(this, responses, statisticsTable, responseRows, question);
        
        
        return questionTable;
    }
    
    private List<InstructorResultsResponseRow> buildResponseRowsForExistingResponses(FeedbackQuestionAttributes question,
                                                                 List<FeedbackResponseAttributes> responses) {
        List<InstructorResultsResponseRow> responseRows = new ArrayList<InstructorResultsResponseRow>();
        for (FeedbackResponseAttributes response : responses) {
            ModerationButton moderationButton = buildModerationButtonForResponse(question, response);
            
            InstructorResultsResponseRow responseRow 
                    = new InstructorResultsResponseRow(
                                                   bundle.getGiverNameForResponse(question, response), 
                                                   bundle.getTeamNameForEmail(response.giverEmail), 
                                                   bundle.getRecipientNameForResponse(question, response), 
                                                   bundle.getTeamNameForEmail(response.recipientEmail), 
                                                   bundle.getResponseAnswerHtml(response, question), 
                                                   true, 
                                                   moderationButton);
            responseRows.add(responseRow);
        }
        return responseRows;
    }

    private ModerationButton buildModerationButtonForResponse(FeedbackQuestionAttributes question,
                                                              FeedbackResponseAttributes response) {
        boolean isAllowedToModerate = instructor.isAllowedForPrivilege(bundle.getSectionFromRoster(response.giverEmail), 
                                                                       feedbackSessionName, 
                                                                       Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        boolean isDisabled = !isAllowedToModerate;
        String giverIdentifier = question.giverType.isTeam() ? 
                                 response.giverEmail.replace(Const.TEAM_OF_EMAIL_OWNER,"") : 
                                 response.giverEmail;
        ModerationButton moderationButton 
                             = new ModerationButton( isAllowedToModerate, isDisabled, 
                                                     question.questionNumber, 
                                                     giverIdentifier, 
                                                     courseId, feedbackSessionName, 
                                                     question);
        return moderationButton;
    }

    /* 
     * The next three methods are not covered in action test, but covered in UI tests.
     */

    @Override
    public String getInstructorFeedbackSessionPublishAndUnpublishAction(FeedbackSessionAttributes session,
                                                                        boolean isHome,
                                                                        InstructorAttributes instructor) {
        boolean hasPublish = !session.isWaitingToOpen() && !session.isPublished();
        boolean hasUnpublish = !session.isWaitingToOpen() && session.isPublished();
        String disabledStr = "disabled=\"disabled\"";
        String disableUnpublishSessionStr =
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)
                        ? "" : disabledStr;
        String disablePublishSessionStr =
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)
                        ? "" : disabledStr;
        String result = "";
        if (hasUnpublish) {
            result = "<a class=\"btn btn-primary btn-block btn-tm-actions session-unpublish-for-test\""
                   + "href=\"" + getInstructorFeedbackSessionUnpublishLink(session.courseId,
                                                                           session.feedbackSessionName, isHome)
                   + "\" " + "title=\"" + Const.Tooltips.FEEDBACK_SESSION_UNPUBLISH
                   + "\" data-toggle=\"tooltip\" data-placement=\"top\""
                   + "onclick=\"return toggleUnpublishEvaluation('"
                   + session.feedbackSessionName + "');\" "
                   + disableUnpublishSessionStr + ">Unpublish results</a> ";
        } else {
            result = "<a class=\"btn btn-primary btn-block btn-tm-actions session-publish-for-test"
                   + (hasPublish ? "\"" : DISABLED) + "href=\""
                   + getInstructorFeedbackSessionPublishLink(session.courseId, session.feedbackSessionName,
                                                             isHome)
                   + "\" " + "title=\""
                   + (hasPublish ? Const.Tooltips.FEEDBACK_SESSION_PUBLISH
                                 : Const.Tooltips.FEEDBACK_SESSION_AWAITING)
                   + "\"" + "data-toggle=\"tooltip\" data-placement=\"top\""
                   + (hasPublish ? "onclick=\"return togglePublishEvaluation('" + session.feedbackSessionName + "');\" "
                                 : " ")
                   + disablePublishSessionStr + ">Publish results</a> ";
        }
        return result;
    }

    public String getResultsVisibleFromText() {
        if (bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            if (bundle.feedbackSession.sessionVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
                return TimeHelper.formatTime(bundle.feedbackSession.startTime);
            } else if (bundle.feedbackSession.sessionVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER)) {
                return "Never";
            } else {
                return TimeHelper.formatTime(bundle.feedbackSession.sessionVisibleFromTime);
            }
        } else if (bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_LATER)) {
            return "I want to manually publish the results.";
        } else if (bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER)) {
            return "Never";
        } else {
            return TimeHelper.formatTime(bundle.feedbackSession.resultsVisibleFromTime);
        }
    }

    public String getProfilePictureLink(String studentEmail) {
        return Const.ActionURIs.STUDENT_PROFILE_PICTURE
                + "?" + Const.ParamsNames.STUDENT_EMAIL + "="
                + StringHelper.encrypt(studentEmail)
                + "&" + Const.ParamsNames.COURSE_ID + "="
                + StringHelper.encrypt(instructor.courseId)
                + "&" + Const.ParamsNames.USER_ID + "=" + account.googleId;
    }

    public static String getExceedingResponsesErrorMessage() {
        return EXCEEDING_RESPONSES_ERROR_MESSAGE;
    }

    public FeedbackSessionResultsBundle getBundle() {
        return bundle;
    }

    public InstructorAttributes getInstructor() {
        return instructor;
    }

    public List<String> getSections() {
        return sections;
    }

    public String getSelectedSection() {
        return selectedSection;
    }

    public String getSortType() {
        return sortType;
    }

    public String getGroupByTeam() {
        return groupByTeam;
    }

    public String getShowStats() {
        return showStats;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getAjaxStatus() {
        return ajaxStatus;
    }

    public String getSessionResultsHtmlTableAsString() {
        return sessionResultsHtmlTableAsString;
    }
    
    public boolean isShouldCollapsed() {
        return shouldCollapsed;
    }

    public void setShouldCollapsed(boolean shouldCollapsed) {
        this.shouldCollapsed = shouldCollapsed;
    }

    public List<InstructorResultsQuestionTable> getQuestionPanels() {
        return questionPanels;
    }

    
    
    
    
}
