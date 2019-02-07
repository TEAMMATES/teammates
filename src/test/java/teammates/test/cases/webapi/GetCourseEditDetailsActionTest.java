package teammates.test.cases.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.ui.webapi.action.GetCourseEditDetailsAction;
import teammates.ui.webapi.action.GetCourseEditDetailsAction.CourseEditDetails;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link GetCourseEditDetailsAction}.
 */
public class GetCourseEditDetailsActionTest extends BaseActionTest<GetCourseEditDetailsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_EDIT_DETAILS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        loginAsInstructor(instructorId);

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case: open the course edit page");

        String[] submissionParams = new String[] { Const.ParamsNames.COURSE_ID, courseId };

        GetCourseEditDetailsAction editAction = getAction(submissionParams);
        JsonResult r = getJsonResult(editAction);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        CourseEditDetails data = (CourseEditDetails) r.getOutput();
        assertEquals(CoursesLogic.inst().getCourse(courseId).toString(), data.getCourseToEdit().toString());
        verifySameInstructorList(InstructorsLogic.inst().getInstructorsForCourse(courseId), data.getInstructorList());

        ______TS("Typical case: open the course edit page with instructor's email");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, "instr1@course1.tmt",
                Const.ParamsNames.COURSE_EDIT_MAIN_INDEX, "1",
        };

        editAction = getAction(submissionParams);
        r = getJsonResult(editAction);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        data = (CourseEditDetails) r.getOutput();
        assertEquals(CoursesLogic.inst().getCourse(courseId).toString(), data.getCourseToEdit().toString());
        assertEquals(1, data.getInstructorList().size());

        ______TS("Masquerade mode");

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor4");
        instructorId = instructor.googleId;
        courseId = instructor.courseId;

        loginAsAdmin();

        submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
        };

        editAction = getAction(submissionParams);
        r = getJsonResult(editAction);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        data = (CourseEditDetails) r.getOutput();
        assertEquals(CoursesLogic.inst().getCourse(courseId).toString(), data.getCourseToEdit().toString());
        verifySameInstructorList(InstructorsLogic.inst().getInstructorsForCourse(courseId), data.getInstructorList());

        ______TS("Failure case: edit a soft-deleted course");

        CoursesLogic.inst().moveCourseToRecycleBin(courseId);

        submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
        };

        editAction = getAction(submissionParams);
        r = getJsonResult(editAction);

        assertEquals(HttpStatus.SC_NOT_FOUND, r.getStatusCode());
        MessageOutput msg = (MessageOutput) r.getOutput();
        assertEquals("The course has been deleted. Please restore it from the Recycle Bin first.",
                msg.getMessage());

        CoursesLogic.inst().restoreCourseFromRecycleBin(courseId);

        ______TS("Failure case: edit a non-existing course");

        CoursesLogic.inst().deleteCourseCascade(courseId);

        submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
        };

        editAction = getAction(submissionParams);
        r = getJsonResult(editAction);

        assertEquals(HttpStatus.SC_NOT_FOUND, r.getStatusCode());
        msg = (MessageOutput) r.getOutput();
        assertEquals("No sections for given course.", msg.getMessage());
    }

    private void verifySameInstructorList(List<InstructorAttributes> list1, List<InstructorAttributes> list2) {
        assertEquals(list1.size(), list2.size());

        for (int i = 0; i < list1.size(); i++) {
            assertEquals(list1.get(i).toString(), list2.get(i).toString());
        }
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").courseId,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
