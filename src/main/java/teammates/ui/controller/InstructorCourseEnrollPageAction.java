package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.logic.api.GateKeeper;

/**
 * Action: showing page to enroll students into a course for an instructor
 */
public class InstructorCourseEnrollPageAction extends Action {
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentsInfo = getRequestParamValue(Const.ParamsNames.STUDENTS_ENROLLMENT_INFO);

        Assumption.assertNotNull(courseId);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        
        /* Setup page data for 'Enroll' page of a course */
        InstructorCourseEnrollPageData pageData = new InstructorCourseEnrollPageData(account, courseId, studentsInfo);

        statusToAdmin = String.format(Const.StatusMessages.ADMIN_LOG_INSTRUCTOR_COURSE_ENROLL_PAGE_LOAD,
                                      courseId);
        addDataLossWarningToStatusToUser(courseId);
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, pageData);
    }

    private void addDataLossWarningToStatusToUser(String courseId) throws EntityDoesNotExistException {
        if (hasExistingResponses(courseId)) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_ENROLL_POSSIBLE_DATA_LOSS,
                                               Const.StatusMessageColor.WARNING));
        }
    }

    private boolean hasExistingResponses(String courseId) throws EntityDoesNotExistException {
        List<FeedbackQuestionAttributes> questionsFromAllSessions = getQuestionsFromAllSessions(courseId);

        for (FeedbackQuestionAttributes question : questionsFromAllSessions) {
            if (logic.isQuestionHasResponses(question.getId())) {
                return true;
            }
        }
        return false;
    }

    private List<FeedbackQuestionAttributes> getQuestionsFromAllSessions(String courseId)
            throws EntityDoesNotExistException {
        List<FeedbackQuestionAttributes> questions = new ArrayList<FeedbackQuestionAttributes>();
        List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsForCourse(courseId);

        for (FeedbackSessionAttributes session : sessions) {
            List<FeedbackQuestionAttributes> questionsFromOneSession =
                    logic.getFeedbackQuestionsForSession(session.getFeedbackSessionName(), courseId);
            questions.addAll(questionsFromOneSession);
        }
        return questions;
    }
}
