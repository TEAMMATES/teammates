package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackPublishEmailParticularStudentsPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorFeedbackAjaxStudentsListPageData;

/**
 * SUT: {@link InstructorFeedbackPublishEmailParticularStudentsPageAction}.
 */
public class InstructorFeedbackPublishEmailParticularStudentsPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESEND_PUBLISH_EMAIL_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("publishedSession1InCourse1");

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyAssumptionFailure();

        ______TS("Typical case");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName()
        };

        InstructorFeedbackPublishEmailParticularStudentsPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        InstructorFeedbackAjaxStudentsListPageData pageData =
                (InstructorFeedbackAjaxStudentsListPageData) r.data;
        assertEquals(5, pageData.getResponseStatus().noResponse.size());
        assertEquals(0, pageData.getResponseStatus().studentsWhoResponded.size());

        assertFalse(pageData.getResponseStatus().studentsWhoResponded.contains("student1InCourse1@gmail.tmt"));
        assertFalse(pageData.getResponseStatus().studentsWhoResponded.contains("student2InCourse1@gmail.tmt"));
        assertTrue(pageData.getResponseStatus().noResponse.contains("student3InCourse1@gmail.tmt"));
        assertTrue(pageData.getResponseStatus().noResponse.contains("student4InCourse1@gmail.tmt"));
        assertFalse(pageData.getResponseStatus().studentsWhoResponded.contains("student5InCourse1@gmail.tmt"));
    }

    @Override
    protected InstructorFeedbackPublishEmailParticularStudentsPageAction getAction(String... params) {
        return (InstructorFeedbackPublishEmailParticularStudentsPageAction)
                gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("publishedSession1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName()
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
