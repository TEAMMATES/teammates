package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorData;
import teammates.ui.request.Intent;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetInstructorAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetInstructorAction}.
 */
public class GetInstructorActionIT extends BaseActionIT<GetInstructorAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() {
        Course course = typicalBundle.courses.get("course1");
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor.getAccount().getGoogleId());

        ______TS("Typical Success Case with INSTRUCTOR_SUBMISSION");
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        GetInstructorAction getInstructorAction = getAction(params);
        JsonResult actionOutput = getJsonResult(getInstructorAction);

        InstructorData response = (InstructorData) actionOutput.getOutput();
        assertEquals(instructor.getName(), response.getName());
        assertNull(response.getGoogleId());
        assertNull(response.getKey());

        ______TS("Typical Success Case with FULL_DETAIL");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        getInstructorAction = getAction(params);
        actionOutput = getJsonResult(getInstructorAction);
        response = (InstructorData) actionOutput.getOutput();
        assertEquals(instructor.getName(), response.getName());

        ______TS("Course ID given but Course is non existent (INSTRUCTOR_SUBMISSION)");

        String[] invalidCourseParams = new String[] {
                Const.ParamsNames.COURSE_ID, "does-not-exist-id",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        EntityNotFoundException enfe = verifyEntityNotFound(invalidCourseParams);
        assertEquals("Instructor could not be found for this course", enfe.getMessage());

        ______TS("Instructor not found case with FULL_DETAIL");
        invalidCourseParams = new String[] {
                Const.ParamsNames.COURSE_ID, "does-not-exist-id",
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        enfe = verifyEntityNotFound(invalidCourseParams);
        assertEquals("Instructor could not be found for this course", enfe.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("only instructors of the same course with correct privilege can access");
        loginAsInstructor(instructor.getAccount().getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyCanAccess(submissionParams);

        ______TS("unregistered instructor is accessible with key");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.REGKEY, instructor.getRegKey(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyAccessibleForUnregisteredUsers(submissionParams);

        ______TS("need login for FULL_DETAILS intent");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyAnyLoggedInUserCanAccess(submissionParams);
    }

}
