package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackResultsPageAction extends Action {

    private static final String ALL_SECTION_OPTION = "All";
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);
        
        statusToAdmin = "Show instructor feedback result page<br>" +
                "Session Name: " + feedbackSessionName + "<br>" + 
                "Course ID: " + courseId;
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = true;
        new GateKeeper().verifyAccessible(
                instructor, 
                session,
                !isCreatorOnly);
        
        InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(account);
        data.selectedSection = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION);
        if(data.selectedSection == null) {
            data.selectedSection = ALL_SECTION_OPTION;
        } 
        
        List<String> filteredEmails = getFilteredEmails(data.selectedSection, courseId);

        data.instructor = instructor;
        if(data.selectedSection.equals(ALL_SECTION_OPTION)){
            data.bundle = logic.getFeedbackSessionResultsForInstructor(feedbackSessionName, courseId, data.instructor.email);
        } else {
            data.bundle = logic.getFeedbackSessionResultsForInstructorInSection(feedbackSessionName, courseId, data.instructor.email, filteredEmails);
        }
        data.sections = logic.getSectionsNameForCourse(courseId);
        if(data.bundle == null) {
            throw new EntityDoesNotExistException(
                    "Feedback session " + feedbackSessionName + " does not exist in " + courseId + ".");
        }
        

        data.sortType = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE);
        data.groupByTeam = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM);
        
        if (data.sortType == null) {
            // default: sort by recipients
            data.sortType = new String("recipient-giver-question");
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
        }
        
        switch(data.sortType){
        case "question":
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION, data);
        case "recipient-giver-question":
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
        case "giver-recipient-question":
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_RECIPIENT_QUESTION, data);
        case "recipient-question-giver":
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_QUESTION_GIVER, data);
        case "giver-question-recipient":
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_QUESTION_RECIPIENT, data);
        default:
            data.sortType = "recipient-giver-question";
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
        }
    }

    private List<String> getFilteredEmails(String section, String courseId) throws EntityDoesNotExistException {
        if(section.equals(ALL_SECTION_OPTION)){
            return null;
        }

        List<String> emails = new ArrayList<String>();
        SectionDetailsBundle sectionDetails = logic.getSectionForCourse(section, courseId);
        for(TeamDetailsBundle team : sectionDetails.teams){
            emails.add(team.name);
            for(StudentAttributes student : team.students){
                emails.add(student.email);
            }
        }
        Collections.sort(emails);
        return emails;
    }
}
