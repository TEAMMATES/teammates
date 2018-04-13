package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackRemindParticularStudentsPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorFeedbackAjaxStudentsListPageData;

/**
 * SUT: {@link InstructorFeedbackRemindParticularStudentsPageAction}.
 */
public class InstructorFeedbackRemindParticularStudentsPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyAssumptionFailure();

        ______TS("Typical case");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName()
        };

        InstructorFeedbackRemindParticularStudentsPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        InstructorFeedbackAjaxStudentsListPageData pageData =
                (InstructorFeedbackAjaxStudentsListPageData) r.data;
        assertEquals(6, pageData.getResponseStatus().studentsWhoDidNotRespond.size());
        assertEquals(3, pageData.getResponseStatus().studentsWhoResponded.size());

        assertTrue(pageData.getResponseStatus().studentsWhoResponded.contains("student1InCourse1@gmail.tmt"));
        assertFalse(pageData.getResponseStatus().studentsWhoDidNotRespond.contains("student2InCourse1@gmail.tmt"));
        assertTrue(pageData.getResponseStatus().studentsWhoResponded.contains("student3InCourse1@gmail.tmt"));
        assertTrue(pageData.getResponseStatus().studentsWhoDidNotRespond.contains("student4InCourse1@gmail.tmt"));
        assertFalse(pageData.getResponseStatus().studentsWhoResponded.contains("student5InCourse1@gmail.tmt"));
        assertFalse(pageData.getResponseStatus().studentsWhoDidNotRespond.contains("student6InCourse1@gmail.tmt"));

        assertFalse(pageData.getResponseStatus().studentsWhoDidNotRespond.contains("instructor1@course1.tmt"));
        assertTrue(pageData.getResponseStatus().studentsWhoDidNotRespond.contains("instructor2@course1.tmt"));
        assertTrue(pageData.getResponseStatus().studentsWhoDidNotRespond.contains("instructor3@course1.tmt"));
        assertFalse(pageData.getResponseStatus().studentsWhoDidNotRespond.contains("instructor4@course1.tmt"));
        assertTrue(pageData.getResponseStatus().studentsWhoDidNotRespond.contains("helper@course1.tmt"));
    }

    @Override
    protected InstructorFeedbackRemindParticularStudentsPageAction getAction(String... params) {
        return (InstructorFeedbackRemindParticularStudentsPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName()
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
