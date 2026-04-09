package teammates.it.ui.webapi;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorsData;
import teammates.ui.request.Intent;
import teammates.ui.webapi.GetInstructorsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetInstructorsAction}.
 */
public class GetInstructorsActionIT extends BaseActionIT<GetInstructorsAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTORS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor.getGoogleId());

        ______TS("Typical Success Case with FULL_DETAIL");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        GetInstructorsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);

        InstructorsData output = (InstructorsData) jsonResult.getOutput();
        List<InstructorData> instructors = output.getInstructors();

        assertEquals(3, instructors.size());

        ______TS("Typical Success Case with no intent");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, null,
        };

        action = getAction(params);
        jsonResult = getJsonResult(action);

        output = (InstructorsData) jsonResult.getOutput();
        instructors = output.getInstructors();

        assertEquals(3, instructors.size());

        for (InstructorData instructorData : instructors) {
            assertNull(instructorData.getGoogleId());
            assertNull(instructorData.getJoinState());
            assertNull(instructorData.getIsDisplayedToStudents());
            assertNull(instructorData.getRole());
        }

        ______TS("Unknown intent");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, "Unknown",
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        Student student = typicalBundle.students.get("student1InCourse1");

        ______TS("Course not found, logged in as instructor, intent FULL_DETAIL");
        loginAsInstructor(instructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, "does-not-exist-id",
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyEntityNotFoundAcl(params);

        ______TS("Course not found, logged in as student, intent undefined");
        loginAsStudent(student.getGoogleId());

        params = new String[] {
                Const.ParamsNames.COURSE_ID, "does-not-exist-id",
        };

        verifyEntityNotFoundAcl(params);

        ______TS("Unknown login entity, intent FULL_DETAIL");
        loginAsUnregistered("unregistered");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyCannotAccess(params);

        ______TS("Unknown login entity, intent undefined");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        verifyCannotAccess(params);

        ______TS("Unknown intent, logged in as instructor");
        loginAsInstructor(instructor.getGoogleId());

        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, "Unknown",
        };

        verifyHttpParameterFailureAcl(params);

        ______TS("Intent FULL_DETAIL, should authenticate as instructor");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(instructor.getCourse(), params);

        ______TS("Intent undefined, should authenticate as student, access own course");
        loginAsStudent(student.getGoogleId());

        params = new String[] {
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
        };

        verifyCanAccess(params);

        ______TS("Intent undefined, should authenticate as student, access other course");
        Student otherStudent = typicalBundle.students.get("student1InCourse2");

        assertNotEquals(otherStudent.getCourse(), student.getCourse());

        params = new String[] {
                Const.ParamsNames.COURSE_ID, otherStudent.getCourseId(),
        };

        verifyCannotAccess(params);
    }

}
