package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackResendPublishedEmailPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorFeedbackAjaxStudentsListPageData;

/**
 * SUT: {@link InstructorFeedbackResendPublishedEmailPageAction}.
 */
public class InstructorFeedbackResendPublishedEmailPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESEND_PUBLISHED_EMAIL_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("closedSession");

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyAssumptionFailure();

        ______TS("Typical case");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName()
        };

        InstructorFeedbackResendPublishedEmailPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        InstructorFeedbackAjaxStudentsListPageData pageData =
                (InstructorFeedbackAjaxStudentsListPageData) r.data;
        assertEquals(4, pageData.getResponseStatus().studentsWhoDidNotRespond.size());
        assertEquals(0, pageData.getResponseStatus().studentsWhoResponded.size());

        assertFalse(pageData.getResponseStatus().studentsWhoResponded.contains("student1InCourse1@gmail.tmt"));
        assertFalse(pageData.getResponseStatus().studentsWhoResponded.contains("student2InCourse1@gmail.tmt"));
        assertFalse(pageData.getResponseStatus().studentsWhoDidNotRespond.contains("student3InCourse1@gmail.tmt"));
        assertFalse(pageData.getResponseStatus().studentsWhoDidNotRespond.contains("student4InCourse1@gmail.tmt"));
        assertFalse(pageData.getResponseStatus().studentsWhoResponded.contains("student5InCourse1@gmail.tmt"));
    }

    @Override
    protected InstructorFeedbackResendPublishedEmailPageAction getAction(String... params) {
        return (InstructorFeedbackResendPublishedEmailPageAction)
                gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("closedSession");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName()
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
