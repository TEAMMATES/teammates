package teammates.test.cases.action;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseEditPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorCourseEditPageData;
import teammates.ui.template.CourseEditInstructorPanel;

/**
 * SUT: {@link InstructorCourseEditPageAction}.
 */
public class InstructorCourseEditPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyAssumptionFailure();

        ______TS("Typical case: open the course edit page");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId
        };

        InstructorCourseEditPageAction editAction = getAction(submissionParams);
        ShowPageResult pageResult = getShowPageResult(editAction);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_COURSE_EDIT, false, "idOfInstructor1OfCourse1"),
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        InstructorCourseEditPageData data = (InstructorCourseEditPageData) pageResult.data;
        assertEquals(CoursesLogic.inst().getCourse(courseId).toString(), data.getCourse().toString());
        verifySameInstructorList(InstructorsLogic.inst().getInstructorsForCourse(courseId), data.getInstructorPanelList());

        String expectedLogSegment = "instructorCourseEdit Page Load<br>"
                                    + "Editing information for Course <span class=\"bold\">[" + courseId + "]</span>";
        AssertHelper.assertContains(expectedLogSegment, editAction.getLogMessage());

        ______TS("Typical case: open the course edit page with instructor's email");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, "instr1@course1.tmt",
                Const.ParamsNames.COURSE_EDIT_MAIN_INDEX, "1"
        };

        editAction = getAction(submissionParams);
        pageResult = getShowPageResult(editAction);
        assertEquals(getPageResultDestination(Const.ViewURIs.INSTRUCTOR_COURSE_EDIT, false,
                "idOfInstructor1OfCourse1"), pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        data = (InstructorCourseEditPageData) pageResult.data;
        assertEquals(CoursesLogic.inst().getCourse(courseId).toString(), data.getCourse().toString());
        assertEquals(1, data.getInstructorPanelList().size());

        expectedLogSegment = "instructorCourseEdit Page Load<br>"
                             + "Editing information for Course <span class=\"bold\">[" + courseId + "]</span>";
        AssertHelper.assertContains(expectedLogSegment, editAction.getLogMessage());

        ______TS("Masquerade mode");

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor4");
        instructorId = instructor.googleId;
        courseId = instructor.courseId;

        gaeSimulation.loginAsAdmin("admin.user");

        submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId
        };

        editAction = getAction(submissionParams);
        pageResult = getShowPageResult(editAction);
        assertEquals(getPageResultDestination(Const.ViewURIs.INSTRUCTOR_COURSE_EDIT, false, "idOfInstructor4"),
                     pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        data = (InstructorCourseEditPageData) pageResult.data;
        assertEquals(CoursesLogic.inst().getCourse(courseId).toString(), data.getCourse().toString());
        verifySameInstructorList(InstructorsLogic.inst().getInstructorsForCourse(courseId), data.getInstructorPanelList());

        expectedLogSegment = "instructorCourseEdit Page Load<br>"
                             + "Editing information for Course <span class=\"bold\">[" + courseId + "]</span>";
        AssertHelper.assertContains(expectedLogSegment, editAction.getLogMessage());

        ______TS("Failure case: edit a non-existing course");

        CoursesLogic.inst().deleteCourseCascade(courseId);

        submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId
        };

        try {
            editAction = getAction(submissionParams);
            pageResult = getShowPageResult(editAction);
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Trying to access system using a non-existent instructor entity", e.getMessage());
        }
    }

    @Override
    protected InstructorCourseEditPageAction getAction(String... params) {
        return (InstructorCourseEditPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    private void verifySameInstructorList(List<InstructorAttributes> list1, List<CourseEditInstructorPanel> list2) {
        assertEquals(list1.size(), list2.size());

        for (int i = 0; i < list1.size(); i++) {
            assertEquals(list1.get(i).toString(), list2.get(i).getInstructor().toString());
        }
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").courseId
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
