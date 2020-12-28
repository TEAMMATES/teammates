package teammates.ui.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.NullHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.ui.output.CourseData;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.CourseUpdateRequest;

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

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        CourseUpdateRequest courseUpdateRequest = new CourseUpdateRequest();
        courseUpdateRequest.setCourseName(courseName);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(submissionParams);
        UpdateCourseAction updateCourseActionWithoutParam = getAction(courseUpdateRequest);
        assertThrows(NullHttpParameterException.class, () -> getJsonResult(updateCourseActionWithoutParam));

        ______TS("Typical case: edit course name with same name");

        // verify time zone will be changed
        String oldCourseTimeZone = typicalBundle.courses.get("typicalCourse1").getTimeZone().getId();
        assertNotEquals(courseTimeZone, oldCourseTimeZone);
        verifySessionsInCourseHaveTimeZone(courseId, oldCourseTimeZone);

        UpdateCourseAction updateCourseAction = getAction(courseUpdateRequest, submissionParams);
        JsonResult result = getJsonResult(updateCourseAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CourseData courseData = (CourseData) result.getOutput();
        verifyCourseData(courseData, courseId, courseName, courseTimeZone);

        verifySessionsInCourseHaveTimeZone(courseId, courseTimeZone);

        ______TS("Typical case: edit course name with valid characters");

        String courseNameWithValidCharacters = courseName + " valid";

        courseUpdateRequest.setCourseName(courseNameWithValidCharacters);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        updateCourseAction = getAction(courseUpdateRequest, submissionParams);
        result = getJsonResult(updateCourseAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        courseData = (CourseData) result.getOutput();
        verifyCourseData(courseData, courseId, courseNameWithValidCharacters, courseTimeZone);

        verifySessionsInCourseHaveTimeZone(courseId, courseTimeZone);
        assertEquals(logic.getCourse(courseId).getName(), courseNameWithValidCharacters);

        ______TS("Failure case: edit course name with empty string");

        courseName = "";
        courseUpdateRequest.setCourseName(courseName);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        updateCourseAction = getAction(courseUpdateRequest, submissionParams);
        result = getJsonResult(updateCourseAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());

        MessageOutput message = (MessageOutput) result.getOutput();
        statusMessage = getPopulatedEmptyStringErrorMessage(
                FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                FieldValidator.COURSE_NAME_FIELD_NAME, FieldValidator.COURSE_NAME_MAX_LENGTH);
        assertEquals(statusMessage, message.getMessage());
        assertNotEquals(logic.getCourse(courseId).getName(), courseName);

        ______TS("Failure case: edit course name with non-alphanumeric start character");

        courseName = "@#$@#$";
        courseUpdateRequest.setCourseName(courseName);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        updateCourseAction = getAction(courseUpdateRequest, submissionParams);
        result = getJsonResult(updateCourseAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());

        message = (MessageOutput) result.getOutput();
        statusMessage = getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                courseName, FieldValidator.COURSE_NAME_FIELD_NAME,
                FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR);
        assertEquals(statusMessage, message.getMessage());
        assertNotEquals(logic.getCourse(courseId).getName(), courseName);

        ______TS("Failure case: edit course name with name containing | and %");

        courseName = "normal|name%";
        courseUpdateRequest.setCourseName(courseName);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        updateCourseAction = getAction(courseUpdateRequest, submissionParams);
        result = getJsonResult(updateCourseAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());

        message = (MessageOutput) result.getOutput();
        statusMessage = getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                courseName, FieldValidator.COURSE_NAME_FIELD_NAME,
                FieldValidator.REASON_CONTAINS_INVALID_CHAR);
        assertEquals(statusMessage, message.getMessage());
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

        updateCourseAction = getAction(courseUpdateRequest, submissionParams);
        result = getJsonResult(updateCourseAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());

        message = (MessageOutput) result.getOutput();
        statusMessage = getPopulatedErrorMessage(FieldValidator.TIME_ZONE_ERROR_MESSAGE,
                courseTimeZone, FieldValidator.TIME_ZONE_FIELD_NAME,
                FieldValidator.REASON_UNAVAILABLE_AS_CHOICE);
        assertEquals(statusMessage, message.getMessage());
        verifySessionsInCourseHaveTimeZone(courseId, oldCourseTimeZone);
    }

    private void verifySessionsInCourseHaveTimeZone(String courseId, String courseTimeZone) {
        List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes session : sessions) {
            assertEquals(courseTimeZone, session.getTimeZone().getId());
        }
    }

    private void verifyCourseData(CourseData data, String courseId, String courseName, String timeZone) {
        assertEquals(data.getCourseId(), courseId);
        assertEquals(data.getCourseName(), courseName);
        assertEquals(data.getTimeZone(), timeZone);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String courseId = instructor.courseId;
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_COURSE, submissionParams);
    }

}
