package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.HasResponsesData;
import teammates.ui.webapi.GetHasResponsesAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetHasResponsesAction}.
 */
public class GetHasResponsesActionIT extends BaseActionIT<GetHasResponsesAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.HAS_RESPONSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() {
        ______TS("typical case: Question with responses");

        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        FeedbackQuestion fq = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");

        loginAsInstructor(instructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId().toString(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertTrue(hasResponsesData.getHasResponses());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        ______TS("Only instructors of the course can check if there are responses.");
        Course course1 = typicalBundle.courses.get("course1");
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(course1, params);
    }

}
