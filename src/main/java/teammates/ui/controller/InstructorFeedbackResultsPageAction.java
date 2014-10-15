package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackResultsPageAction extends Action {

    private static final String ALL_SECTION_OPTION = "All";
    private static final int DEFAULT_QUERY_RANGE = 1000;
    private static final int DEFAULT_SECTION_QUERY_RANGE = 2500;
    private static final int QUERY_RANGE_FOR_AJAX_TESTING = 5;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String needAjax = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX);
        int queryRange;
        if(needAjax != null){
            queryRange = QUERY_RANGE_FOR_AJAX_TESTING;
        } else {
            queryRange = DEFAULT_QUERY_RANGE;
        }
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);

        statusToAdmin = "Show instructor feedback result page<br>" +
                "Session Name: " + feedbackSessionName + "<br>" +
                "Course ID: " + courseId;

        InstructorAttributes instructor = logic.getInstructorForGoogleId(
                courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(
                feedbackSessionName, courseId);
        boolean isCreatorOnly = true;

        new GateKeeper().verifyAccessible(instructor, session, !isCreatorOnly);

        InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(
                account);
        data.selectedSection = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION);
        if (data.selectedSection == null) {
            data.selectedSection = ALL_SECTION_OPTION;
        }
        data.instructor = instructor;
        data.showStats = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS);
        data.groupByTeam = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM);
        data.sortType = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE);
        String startIndex = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_MAIN_INDEX);
        if(startIndex != null){
            data.startIndex = Integer.parseInt(startIndex);
        }

        if (data.sortType == null) {
            // default: sort by question, stats shown, grouped by team.
            data.showStats = new String("on");
            data.groupByTeam = new String("on");
            data.sortType = new String("question");
        }
        data.sections = logic.getSectionNamesForCourse(courseId);
        String questionNumStr = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
        if (data.selectedSection.equals(ALL_SECTION_OPTION) && questionNumStr == null) {
            data.bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                    feedbackSessionName, courseId, data.instructor.email, queryRange, data.sortType);
        } else if (data.sortType.equals("question")) {
            if(questionNumStr == null){
                data.bundle = logic.getFeedbackSessionResultsForInstructorInSection(
                                feedbackSessionName, courseId,
                                data.instructor.email, data.selectedSection);
            } else {
                int questionNum = Integer.parseInt(questionNumStr);
                data.bundle = logic.getFeedbackSessionResultsForInstructorFromQuestion(
                                feedbackSessionName, courseId, 
                                data.instructor.email, questionNum);
            }
        } else if (data.sortType.equals("giver-question-recipient")
                || data.sortType.equals("giver-recipient-question")) {
            data.bundle = logic
                    .getFeedbackSessionResultsForInstructorFromSectionWithinRange(
                            feedbackSessionName, courseId,
                            data.instructor.email, data.selectedSection, DEFAULT_SECTION_QUERY_RANGE);
        } else if (data.sortType.equals("recipient-question-giver")
                || data.sortType.equals("recipient-giver-question")) {
            data.bundle = logic
                    .getFeedbackSessionResultsForInstructorToSectionWithinRange(
                            feedbackSessionName, courseId,
                            data.instructor.email, data.selectedSection, DEFAULT_SECTION_QUERY_RANGE);
        }

        if (data.bundle == null) {
            throw new EntityDoesNotExistException(
                    "Feedback session " + feedbackSessionName + " does not exist in " + courseId + ".");
        }
        
        switch (data.sortType) {
        case "question":
            return createShowPageResult(
                    Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION,
                    data);
        case "recipient-giver-question":
            return createShowPageResult(
                    Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION,
                    data);
        case "giver-recipient-question":
            return createShowPageResult(
                    Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_RECIPIENT_QUESTION,
                    data);
        case "recipient-question-giver":
            return createShowPageResult(
                    Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_QUESTION_GIVER,
                    data);
        case "giver-question-recipient":
            return createShowPageResult(
                    Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_QUESTION_RECIPIENT,
                    data);
        default:
            data.sortType = "recipient-giver-question";
            return createShowPageResult(
                    Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION,
                    data);
        }
    }
}
