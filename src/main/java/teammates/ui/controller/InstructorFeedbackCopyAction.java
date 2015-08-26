package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackCopyAction extends InstructorFeedbacksPageAction {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String copiedFeedbackSessionName = getRequestParamValue(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME);
        String copiedCourseId = getRequestParamValue(Const.ParamsNames.COPIED_COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        Assumption.assertNotNull(copiedFeedbackSessionName);
        Assumption.assertNotNull(copiedCourseId);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId); 
        
        new GateKeeper().verifyAccessible(
                instructor, 
                logic.getCourse(courseId),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(account);
        
        FeedbackSessionAttributes fs = null;
        try {
            fs = logic.copyFeedbackSession(copiedFeedbackSessionName,
                                           copiedCourseId,
                                           feedbackSessionName,
                                           courseId,
                                           instructor.email);
            
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_COPIED, StatusMessageColor.SUCCESS));
            statusToAdmin =
                    "New Feedback Session <span class=\"bold\">(" + fs.feedbackSessionName + ")</span> " +
                    "for Course <span class=\"bold\">[" + fs.courseId + "]</span> created.<br>" +
                    "<span class=\"bold\">From:</span> " + fs.startTime +
                    "<span class=\"bold\"> to</span> " + fs.endTime + "<br>" +
                    "<span class=\"bold\">Session visible from:</span> " + fs.sessionVisibleFromTime + "<br>" +
                    "<span class=\"bold\">Results visible from:</span> " + fs.resultsVisibleFromTime + "<br><br>" +
                    "<span class=\"bold\">Instructions:</span> " + fs.instructions;
            
            //TODO: add a condition to include the status due to inconsistency problem of database 
            //      (similar to the one below)
            return createRedirectResult(
                    new PageData(account).getInstructorFeedbackEditLink(
                            fs.courseId, fs.feedbackSessionName));
            
        } catch (EntityAlreadyExistsException e) {
            setStatusForException(e, Const.StatusMessages.FEEDBACK_SESSION_EXISTS);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
        // isError == true if an exception occurred above
        
        boolean omitArchived = true;
        Map<String, InstructorAttributes> instructors = loadCourseInstructorMap(omitArchived);
        List<InstructorAttributes> instructorList =
                new ArrayList<InstructorAttributes>(instructors.values());
        List<CourseAttributes> courses = loadCoursesList(instructorList);
        
        List<FeedbackSessionAttributes> feedbackSessions = loadFeedbackSessionsList(instructorList);
        FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(feedbackSessions);
        
        if (feedbackSessions.isEmpty()) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_ADD_DB_INCONSISTENCY, StatusMessageColor.WARNING));
        }
        
        Map<String, List<String>> courseIdToSectionName = logic.getCourseIdToSectionNamesMap(courses);
        
        data.initWithoutHighlightedRow(courses, null, feedbackSessions, instructors, fs, null, courseIdToSectionName);
       
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACKS, data);
    }

}
