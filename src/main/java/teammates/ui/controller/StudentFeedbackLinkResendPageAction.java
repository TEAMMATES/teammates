package teammates.ui.controller;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.ui.pagedata.StudentHomePageData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentFeedbackLinkResendPageAction extends Action {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    @Override
    public ActionResult execute() {
        String userToResend = getRequestParamValue(Const.ParamsNames.SUBMISSION_RESEND_LINK_USER);
        Assumption.assertNotNull(Const.ParamsNames.SUBMISSION_RESEND_LINK_USER, userToResend);

        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date startTime = calendar.getTime();
        List<FeedbackSessionAttributes> sessions = fsLogic.getAllOpenFeedbackSessions(startTime, endTime);

        StudentHomePageData data;
        List<CourseDetailsBundle> courses = new ArrayList<>();
        Map<FeedbackSessionAttributes, Boolean> sessionSubmissionStatusMap = new HashMap<>();
        try {
            for (FeedbackSessionAttributes session : sessions) {
                CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
                StudentAttributes student = logic.getStudentForEmail(course.getId(), userToResend);
                courses.add(logic.getCourseDetails(course.getId()));
            }
        } catch (EntityDoesNotExistException e) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_FIRST_TIME, StatusMessageColor.WARNING));
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " :" + e.getMessage();
        }

        sessionSubmissionStatusMap = generateFeedbackSessionSubmissionStatusMap(courses, userToResend);
        AccountAttributes account = logic.getAccount(userToResend);

        if (account == null) {
            data = new StudentHomePageData(sessionToken, courses, sessionSubmissionStatusMap);
        } else {
            data = new StudentHomePageData(account, sessionToken, courses, sessionSubmissionStatusMap);
        }

        return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_LINK_RESEND, data);
    }

    private Map<FeedbackSessionAttributes, Boolean> generateFeedbackSessionSubmissionStatusMap(
            List<CourseDetailsBundle> courses, String studentEmail) {
        Map<FeedbackSessionAttributes, Boolean> returnValue = new HashMap<>();

        for (CourseDetailsBundle c : courses) {
            for (FeedbackSessionDetailsBundle fsb : c.feedbackSessions) {
                FeedbackSessionAttributes f = fsb.feedbackSession;
                returnValue.put(f, getStudentStatusForSession(f, studentEmail));
            }
        }
        return returnValue;
    }

    private boolean getStudentStatusForSession(FeedbackSessionAttributes fs, String studentEmail) {
        return logic.hasStudentSubmittedFeedback(fs, studentEmail);
    }

}
