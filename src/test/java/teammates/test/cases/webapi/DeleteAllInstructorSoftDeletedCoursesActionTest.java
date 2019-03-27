package teammates.test.cases.webapi;

import java.time.ZoneId;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.webapi.action.DeleteAllInstructorSoftDeletedCoursesAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link DeleteAllInstructorSoftDeletedCoursesAction}.
 */
public class DeleteAllInstructorSoftDeletedCoursesActionTest
        extends BaseActionTest<DeleteAllInstructorSoftDeletedCoursesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_COURSES_PERMANENTLY_DELETE_ALL;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
        };

        ______TS("Typical case, delete all soft-deleted courses in Recycle Bin");

        if (CoursesLogic.inst().isCoursePresent("new-course")) {
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }
        if (CoursesLogic.inst().isCoursePresent("not-in-recycle-bin")) {
            CoursesLogic.inst().deleteCourseCascade("not-in-recycle-bin");
        }

        CoursesLogic.inst().createCourseAndInstructor(instructorId,
                CourseAttributes.builder("new-course")
                        .withName("New course")
                        .withTimezone(ZoneId.of("UTC"))
                        .build());
        CoursesLogic.inst().createCourseAndInstructor(instructorId,
                CourseAttributes.builder("not-in-recycle-bin")
                        .withName("Course Not In Recycle Bin")
                        .withTimezone(ZoneId.of("UTC"))
                        .build());
        loginAsInstructor(instructorId);
        assertEquals(3, CoursesLogic.inst().getCoursesForInstructor(instructorId).size());
        CoursesLogic.inst().moveCourseToRecycleBin(courseId);
        CoursesLogic.inst().moveCourseToRecycleBin("new-course");
        assertEquals(1, CoursesLogic.inst().getCoursesForInstructor(instructorId).size());

        DeleteAllInstructorSoftDeletedCoursesAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput message = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals("All courses in Recycle Bin have been permanently deleted.", message.getMessage());

        // should not delete course not in recycle bin
        assertNotNull(logic.getCourse("not-in-recycle-bin"));
        // should delete the courses
        assertNull(logic.getCourse(courseId));
        assertNull(logic.getCourse("new-course"));

        // cascade deletion should be done properly
        assertEquals(0, logic.getFeedbackSessionsForCourse(courseId).size());
        String typicalFeedbackSessionName = "First feedback session";
        assertNull(logic.getFeedbackSession(typicalFeedbackSessionName, courseId));
        assertEquals(0, logic.getStudentsForCourse(courseId).size());
        assertEquals(0, logic.getInstructorsForCourse(courseId).size());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};

        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}
