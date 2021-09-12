package teammates.ui.webapi;

import org.testng.annotations.Test;

import com.google.common.collect.Sets;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionSubmittedGiverSet;

/**
 * SUT: {@link GetFeedbackSessionSubmittedGiverSetAction}.
 */
public class GetFeedbackSessionSubmittedGiverSetActionTest
        extends BaseActionTest<GetFeedbackSessionSubmittedGiverSetAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_SUBMITTED_GIVER_SET;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyHttpParameterFailure();

        ______TS("Typical case");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName(),
        };

        GetFeedbackSessionSubmittedGiverSetAction pageAction = getAction(submissionParams);
        JsonResult result = getJsonResult(pageAction);

        FeedbackSessionSubmittedGiverSet output = (FeedbackSessionSubmittedGiverSet) result.getOutput();
        assertEquals(Sets.newHashSet("student1InCourse1@gmail.tmt", "student2InCourse1@gmail.tmt",
                "student5InCourse1@gmail.tmt", "student3InCourse1@gmail.tmt", "instructor1@course1.tmt"),
                output.getGiverIdentifiers());
    }

    @Test
    @Override
    protected void testAccessControl() {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName(),
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
