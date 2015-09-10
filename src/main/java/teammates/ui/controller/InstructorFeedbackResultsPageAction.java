package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Const.StatusMessageColor;
import teammates.common.util.StatusMessage;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;
import teammates.ui.controller.InstructorFeedbackResultsPageData.ViewType;

public class InstructorFeedbackResultsPageAction extends Action {

    private static final String ALL_SECTION_OPTION = "All";
    private static final int DEFAULT_QUERY_RANGE = 1000;
    private static final int DEFAULT_SECTION_QUERY_RANGE = 2500;
    private static final int QUERY_RANGE_FOR_AJAX_TESTING = 5;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String needAjax = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX);

        int queryRange;
        if (needAjax != null) {
            queryRange = QUERY_RANGE_FOR_AJAX_TESTING;
        } else {
            queryRange = DEFAULT_QUERY_RANGE;
        }

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);

        statusToAdmin = "Show instructor feedback result page<br>"
                      + "Session Name: " + feedbackSessionName + "<br>"
                      + "Course ID: " + courseId;

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = true;

        new GateKeeper().verifyAccessible(instructor, session, !isCreatorOnly);

        InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(account);
        String selectedSection = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION);

        String showStats = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS);
        String groupByTeam = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM);
        String sortType = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE);

        if (selectedSection == null) {
            selectedSection = ALL_SECTION_OPTION;
        }
        
        // this is for ajax loading of the html table in the modal 
        // "(Non-English characters not displayed properly in the downloaded file? click here)"
        // TODO move into another action and another page data class 
        boolean isLoadingCsvResultsAsHtml = getRequestParamAsBoolean(Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED);
        if (isLoadingCsvResultsAsHtml) {
            return createAjaxResultForCsvTableLoadedInHtml(courseId, feedbackSessionName, instructor, data, selectedSection);
        } else {
            data.setSessionResultsHtmlTableAsString("");
            data.setAjaxStatus("");
        }

        String startIndex = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_MAIN_INDEX);
        if (startIndex != null) {
            data.setStartIndex(Integer.parseInt(startIndex));
        }

        if (sortType == null) {
            // default view: sort by question, statistics shown, grouped by team.
            showStats = new String("on");
            groupByTeam = new String("on");
            sortType = new String("question");
        }
        
        data.setSections(logic.getSectionNamesForCourse(courseId));
        
        String questionNumStr = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
        
        if (ALL_SECTION_OPTION.equals(selectedSection) && questionNumStr == null && !sortType.equals("question")) {
            // bundle for all questions and all sections  
            data.setBundle(
                     logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                                                           feedbackSessionName, courseId,
                                                                           instructor.email,
                                                                           queryRange, sortType));
        } else if (sortType.equals("question")) {
            data.setBundle(getBundleForQuestionView(needAjax, courseId, feedbackSessionName, instructor, data,
                                                    selectedSection, sortType, questionNumStr));
        } else if (sortType.equals("giver-question-recipient")
                || sortType.equals("giver-recipient-question")) {
            data.setBundle(logic
                    .getFeedbackSessionResultsForInstructorFromSectionWithinRange(feedbackSessionName, courseId,
                                                                                  instructor.email,
                                                                                  selectedSection,
                                                                                  DEFAULT_SECTION_QUERY_RANGE));
        } else if (sortType.equals("recipient-question-giver")
                || sortType.equals("recipient-giver-question")) {
            data.setBundle(logic
                    .getFeedbackSessionResultsForInstructorToSectionWithinRange(feedbackSessionName, courseId,
                                                                                instructor.email,
                                                                                selectedSection,
                                                                                DEFAULT_SECTION_QUERY_RANGE));
        }

        if (data.getBundle() == null) {
            throw new EntityDoesNotExistException("Feedback session " + feedbackSessionName
                                                  + " does not exist in " + courseId + ".");
        }

        // Warning for section wise viewing in case of many responses.
        boolean isShowSectionWarningForQuestionView = data.isLargeNumberOfRespondents() 
                                                   && sortType.equals("question");
        boolean isShowSectionWarningForParticipantView = !data.getBundle().isComplete
                                                   && !sortType.equals("question");
        if (selectedSection.equals(ALL_SECTION_OPTION) && (isShowSectionWarningForParticipantView
                                                           || isShowSectionWarningForQuestionView)) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_RESULTS_SECTIONVIEWWARNING, StatusMessageColor.WARNING));
            isError = true;
        }
        

        switch (sortType) {
            case "question":
                data.initForViewByQuestion(instructor, selectedSection, showStats, groupByTeam);
                return createShowPageResult(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION, data);
            case "recipient-giver-question":
                data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam, 
                                              ViewType.RECIPIENT_GIVER_QUESTION);
                return createShowPageResult(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
            case "giver-recipient-question":
                data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam,
                                              ViewType.GIVER_RECIPIENT_QUESTION);
                return createShowPageResult(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_RECIPIENT_QUESTION, data);
            case "recipient-question-giver":
                data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam,
                                              ViewType.RECIPIENT_QUESTION_GIVER);
                return createShowPageResult(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_QUESTION_GIVER, data);
            case "giver-question-recipient":
                data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam, 
                                              ViewType.GIVER_QUESTION_RECIPIENT);
                return createShowPageResult(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_QUESTION_RECIPIENT, data);
            default:
                sortType = "recipient-giver-question";
                data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam, 
                                              ViewType.RECIPIENT_GIVER_QUESTION);
                return createShowPageResult(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
        }
    }

    private FeedbackSessionResultsBundle getBundleForQuestionView(String needAjax, String courseId, String feedbackSessionName,
                                                                  InstructorAttributes instructor, InstructorFeedbackResultsPageData data,
                                                                  String selectedSection, String sortType, String questionNumStr)
                                                                  throws EntityDoesNotExistException {
        FeedbackSessionResultsBundle bundle;
        if (questionNumStr == null) {
            if (ALL_SECTION_OPTION.equals(selectedSection) ) {
                // load page structure without responses
                
                data.setLargeNumberOfRespondents(needAjax != null);
                
                // all sections and all questions for question view
                // set up question tables, responses to load by ajax
                bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                               feedbackSessionName, courseId,
                                               instructor.email,
                                               1, sortType);
                // set isComplete to true to prevent behavior when there are too many responses, 
                // such as the display of warning messages
                bundle.isComplete = true;
            } else {
                // bundle for all questions, with a selected section
                bundle = logic.getFeedbackSessionResultsForInstructorInSection(feedbackSessionName, courseId,
                                                                                    instructor.email,
                                                                                    selectedSection);
            }
        } else {
            if (ALL_SECTION_OPTION.equals(selectedSection)) {
                // bundle for a specific question, with all sections
                int questionNum = Integer.parseInt(questionNumStr);
                bundle = logic.getFeedbackSessionResultsForInstructorFromQuestion(feedbackSessionName, courseId, 
                                                                                  instructor.email, questionNum);
            } else {
                // bundle for a specific question and a specific section
                int questionNum = Integer.parseInt(questionNumStr);
                bundle = logic.getFeedbackSessionResultsForInstructorFromQuestionInSection(
                                                feedbackSessionName, courseId, 
                                                instructor.email, questionNum, selectedSection);
            }
        }
        
        return bundle;
    }

    private ActionResult createAjaxResultForCsvTableLoadedInHtml(String courseId, String feedbackSessionName,
                                    InstructorAttributes instructor, InstructorFeedbackResultsPageData data, 
                                    String selectedSection)
                                    throws EntityDoesNotExistException {
        try {
            if (!selectedSection.contentEquals(ALL_SECTION_OPTION)) {
                data.setSessionResultsHtmlTableAsString(StringHelper.csvToHtmlTable(
                        logic.getFeedbackSessionResultSummaryInSectionAsCsv(courseId, feedbackSessionName,
                                                                            instructor.email, selectedSection)));
            } else {
                data.setSessionResultsHtmlTableAsString(StringHelper.csvToHtmlTable(
                        logic.getFeedbackSessionResultSummaryAsCsv(courseId, feedbackSessionName,
                                                                   instructor.email)));
            }
        } catch (ExceedingRangeException e) {
            // not tested as the test file is not large enough to reach this catch block
            data.setSessionResultsHtmlTableAsString("");
            data.setAjaxStatus("There are too many responses. Please download the feedback results by section.");
        }

        return createAjaxResult(data);
    }

}
