package teammates.test.cases.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.UpdateCourseAction;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.CourseUpdateRequest;

/**
 * SUT: {@link UpdateCourseAction}.
 */
public class UpdateCourseActionTest extends BaseActionTest<UpdateCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor.googleId;
        String courseId = instructor.courseId;
        String courseName = logic.getCourse(courseId).getName();
        String courseTimeZone = "UTC";
        String statusMessage = "";
        String[] submissionParams;

        loginAsInstructor(instructorId);

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case: edit course name with same name");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        CourseUpdateRequest courseUpdateRequest = new CourseUpdateRequest();
        courseUpdateRequest.setCourseName(courseName);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        // verify time zone will be changed
        String oldCourseTimeZone = typicalBundle.courses.get("typicalCourse1").getTimeZone().getId();
        assertNotEquals(courseTimeZone, oldCourseTimeZone);
        verifySessionsInCourseHaveTimeZone(courseId, oldCourseTimeZone);

        UpdateCourseAction courseEditSaveAction = getAction(courseUpdateRequest, submissionParams);
        JsonResult r = getJsonResult(courseEditSaveAction);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        MessageOutput msg = (MessageOutput) r.getOutput();
        assertEquals("Updated course [" + courseId + "] details: Name: " + courseName
                + ", Time zone: " + courseTimeZone, msg.getMessage());

        verifySessionsInCourseHaveTimeZone(courseId, courseTimeZone);

        ______TS("Typical case: edit course name with valid characters");

        String courseNameWithValidCharacters = courseName + " valid";

        courseUpdateRequest.setCourseName(courseNameWithValidCharacters);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        courseEditSaveAction = getAction(courseUpdateRequest, submissionParams);
        r = getJsonResult(courseEditSaveAction);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals("Updated course [" + courseId + "] details: Name: " + courseNameWithValidCharacters
                + ", Time zone: " + courseTimeZone, msg.getMessage());

        verifySessionsInCourseHaveTimeZone(courseId, courseTimeZone);
        assertEquals(logic.getCourse(courseId).getName(), courseNameWithValidCharacters);

        ______TS("Failure case: edit course name with empty string");

        courseName = "";
        courseUpdateRequest.setCourseName(courseName);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        courseEditSaveAction = getAction(courseUpdateRequest, submissionParams);
        r = getJsonResult(courseEditSaveAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        statusMessage = getPopulatedEmptyStringErrorMessage(
                FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                FieldValidator.COURSE_NAME_FIELD_NAME, FieldValidator.COURSE_NAME_MAX_LENGTH);
        assertEquals(statusMessage, msg.getMessage());
        assertNotEquals(logic.getCourse(courseId).getName(), courseName);

        ______TS("Failure case: edit course name with non-alphanumeric start character");

        courseName = "@#$@#$";
        courseUpdateRequest.setCourseName(courseName);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        courseEditSaveAction = getAction(courseUpdateRequest, submissionParams);
        r = getJsonResult(courseEditSaveAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        statusMessage = getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                courseName, FieldValidator.COURSE_NAME_FIELD_NAME,
                FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR);
        assertEquals(statusMessage, msg.getMessage());
        assertNotEquals(logic.getCourse(courseId).getName(), courseName);

        ______TS("Failure case: edit course name with name containing | and %");

        courseName = "normal|name%";
        courseUpdateRequest.setCourseName(courseName);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        courseEditSaveAction = getAction(courseUpdateRequest, submissionParams);
        r = getJsonResult(courseEditSaveAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        statusMessage = getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                courseName, FieldValidator.COURSE_NAME_FIELD_NAME,
                FieldValidator.REASON_CONTAINS_INVALID_CHAR);
        assertEquals(statusMessage, msg.getMessage());
        assertNotEquals(logic.getCourse(courseId).getName(), courseName);

        ______TS("Failure case: invalid time zone");

        // verify time zone did not change
        oldCourseTimeZone = courseTimeZone;

        courseName = logic.getCourse(courseId).getName();
        courseTimeZone = "InvalidTimeZone";
        courseUpdateRequest.setCourseName(courseName);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        assertNotEquals(courseTimeZone, oldCourseTimeZone);
        verifySessionsInCourseHaveTimeZone(courseId, oldCourseTimeZone);

        courseEditSaveAction = getAction(courseUpdateRequest, submissionParams);
        r = getJsonResult(courseEditSaveAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        statusMessage = getPopulatedErrorMessage(FieldValidator.TIME_ZONE_ERROR_MESSAGE,
                courseTimeZone, FieldValidator.TIME_ZONE_FIELD_NAME,
                FieldValidator.REASON_UNAVAILABLE_AS_CHOICE);
        assertEquals(statusMessage, msg.getMessage());
        verifySessionsInCourseHaveTimeZone(courseId, oldCourseTimeZone);
    }

    private void verifySessionsInCourseHaveTimeZone(String courseId, String courseTimeZone) {
        List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes session : sessions) {
            assertEquals(courseTimeZone, session.getTimeZone().getId());
        }
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String courseId = instructor.courseId;
        String courseName = "Typical Course 1 with 2 Evals";
        String courseTimeZone = typicalBundle.courses.get("typicalCourse1").getTimeZone().getId();
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_NAME, courseName,
                Const.ParamsNames.COURSE_TIME_ZONE, courseTimeZone,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyInaccessibleWithoutModifyCoursePrivilege(submissionParams);
    }

}
