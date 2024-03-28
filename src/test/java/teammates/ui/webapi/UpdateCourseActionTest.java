package teammates.ui.webapi;

import java.util.List;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.ui.output.CourseData;
import teammates.ui.request.CourseUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * SUT: {@link UpdateCourseAction}.
 */
@Ignore
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
        String instructorId = instructor.getGoogleId();
        String courseId = instructor.getCourseId();
        String courseName = logic.getCourse(courseId).getName();
        String courseTimeZone = "UTC";
        String[] submissionParams;

        loginAsInstructor(instructorId);

        ______TS("Not enough parameters");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        CourseUpdateRequest courseUpdateRequest = new CourseUpdateRequest();
        courseUpdateRequest.setCourseName(courseName);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        verifyHttpRequestBodyFailure(null);
        verifyHttpParameterFailure(courseUpdateRequest);

        ______TS("Typical case: edit course name with same name");

        // verify time zone will be changed
        String oldCourseTimeZone = typicalBundle.courses.get("typicalCourse1").getTimeZone();
        assertNotEquals(courseTimeZone, oldCourseTimeZone);
        verifySessionsInCourseHaveTimeZone(courseId, oldCourseTimeZone);

        UpdateCourseAction updateCourseAction = getAction(courseUpdateRequest, submissionParams);
        JsonResult result = getJsonResult(updateCourseAction);

        CourseData courseData = (CourseData) result.getOutput();
        verifyCourseData(courseData, courseId, courseName, courseTimeZone);

        verifySessionsInCourseHaveTimeZone(courseId, courseTimeZone);

        ______TS("Typical case: edit course name with valid characters");

        String courseNameWithValidCharacters = courseName + " valid";

        courseUpdateRequest.setCourseName(courseNameWithValidCharacters);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        updateCourseAction = getAction(courseUpdateRequest, submissionParams);
        result = getJsonResult(updateCourseAction);

        courseData = (CourseData) result.getOutput();
        verifyCourseData(courseData, courseId, courseNameWithValidCharacters, courseTimeZone);

        verifySessionsInCourseHaveTimeZone(courseId, courseTimeZone);
        assertEquals(logic.getCourse(courseId).getName(), courseNameWithValidCharacters);

        ______TS("Failure case: edit course name with empty string");

        courseName = "";
        courseUpdateRequest.setCourseName(courseName);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(courseUpdateRequest, submissionParams);
        String statusMessage = getPopulatedEmptyStringErrorMessage(
                FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                FieldValidator.COURSE_NAME_FIELD_NAME, FieldValidator.COURSE_NAME_MAX_LENGTH);
        assertEquals(statusMessage, ihrbe.getMessage());
        assertNotEquals(logic.getCourse(courseId).getName(), courseName);

        ______TS("Failure case: edit course name with non-alphanumeric start character");

        courseName = "@#$@#$";
        courseUpdateRequest.setCourseName(courseName);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        ihrbe = verifyHttpRequestBodyFailure(courseUpdateRequest, submissionParams);
        statusMessage = getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                courseName, FieldValidator.COURSE_NAME_FIELD_NAME,
                FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR);
        assertEquals(statusMessage, ihrbe.getMessage());
        assertNotEquals(logic.getCourse(courseId).getName(), courseName);

        ______TS("Failure case: edit course name with name containing | and %");

        courseName = "normal|name%";
        courseUpdateRequest.setCourseName(courseName);
        courseUpdateRequest.setTimeZone(courseTimeZone);

        ihrbe = verifyHttpRequestBodyFailure(courseUpdateRequest, submissionParams);
        statusMessage = getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                courseName, FieldValidator.COURSE_NAME_FIELD_NAME,
                FieldValidator.REASON_CONTAINS_INVALID_CHAR);
        assertEquals(statusMessage, ihrbe.getMessage());
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

        ihrbe = verifyHttpRequestBodyFailure(courseUpdateRequest, submissionParams);
        statusMessage = getPopulatedErrorMessage(FieldValidator.TIME_ZONE_ERROR_MESSAGE,
                courseTimeZone, FieldValidator.TIME_ZONE_FIELD_NAME,
                FieldValidator.REASON_UNAVAILABLE_AS_CHOICE);
        assertEquals(statusMessage, ihrbe.getMessage());
        verifySessionsInCourseHaveTimeZone(courseId, oldCourseTimeZone);
    }

    private void verifySessionsInCourseHaveTimeZone(String courseId, String courseTimeZone) {
        List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes session : sessions) {
            assertEquals(courseTimeZone, session.getTimeZone());
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
        String courseId = instructor.getCourseId();
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_COURSE, submissionParams);
    }

}
