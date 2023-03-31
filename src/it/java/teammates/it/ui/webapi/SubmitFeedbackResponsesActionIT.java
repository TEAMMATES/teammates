package teammates.it.ui.webapi;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.request.Intent;
import teammates.ui.webapi.SubmitFeedbackResponsesAction;

/**
 * SUT: {@link SubmitFeedbackResponsesAction}.
 */
public class SubmitFeedbackResponsesActionIT extends BaseActionIT<SubmitFeedbackResponsesAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    String getActionUri() {
        return Const.ResourceURIs.RESPONSES;
    }

    @Override
    String getRequestMethod() {
        return PUT;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testExecute'");
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        // See each independent test case.
    }

    @Test
    public void testAccessControl_instructorSubmissionPastEndTime_shouldAllowIfBeforeDeadline()
            throws Exception {
        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion feedbackQuestion = typicalBundle.feedbackQuestions.get("qn4InSession1InCourse1");

        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        
        loginAsInstructor(instructor.getGoogleId());

        ______TS("Typical Success Case for Instructor submitting before deadline");
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyCanAccess(params);

        ______TS("Instructor submitting after deadline; should fail");
        feedbackSession.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-2));

        logic.updateFeedbackSession(feedbackSession);

        DeadlineExtension deadlineExtension =
                logic.getDeadlineExtension(instructor.getId(), feedbackSession.getId());

        deadlineExtension.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        logic.updateDeadlineExtension(deadlineExtension);

        verifyCannotAccess(params);

        ______TS("No selective deadline; should fail.");
        List<DeadlineExtension> instructorDEs =
                feedbackSession
                        .getDeadlineExtensions()
                        .stream()
                        .filter(de -> de.getUser().equals(instructor))
                        .collect(Collectors.toList());
        
        for (DeadlineExtension de: instructorDEs) {
            logic.deleteDeadlineExtension(de);
        }

        verifyCannotAccess(params);
    }

    @Test
    public void testAccessControl_studentSubmissionPastEndTime_shouldAllowIfBeforeDeadline()
            throws Exception {
        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion feedbackQuestion = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");

        Student student = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(student.getGoogleId());

        ______TS("Typical Success Case for Student submitting before deadline");
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyCanAccess(params);

        ______TS("Student submitting after deadline; should fail");
        feedbackSession.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-2));

        logic.updateFeedbackSession(feedbackSession);

        DeadlineExtension deadlineExtension =
                logic.getDeadlineExtension(student.getId(), feedbackSession.getId());

        deadlineExtension.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        logic.updateDeadlineExtension(deadlineExtension);

        verifyCannotAccess(params);

        ______TS("No selective deadline; should fail.");
        List<DeadlineExtension> studentDEs =
                feedbackSession
                        .getDeadlineExtensions()
                        .stream()
                        .filter(de -> de.getUser().equals(student))
                        .collect(Collectors.toList());
        
        for (DeadlineExtension de: studentDEs) {
            logic.deleteDeadlineExtension(de);
        }

        verifyCannotAccess(params);
    }
}
