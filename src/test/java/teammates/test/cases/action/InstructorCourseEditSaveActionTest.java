package teammates.test.cases.action;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.ui.controller.InstructorCourseEditSaveAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorCourseEditSaveAction}.
 */
public class InstructorCourseEditSaveActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_SAVE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor.googleId;
        String courseId = instructor.courseId;
        String courseName = CoursesLogic.inst().getCourse(courseId).getName();
        String courseTimeZone = "UTC";
        String statusMessage = "";
        String[] submissionParams;
        InstructorCourseEditSaveAction courseEditSaveAction;
        RedirectResult redirectResult;

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyAssumptionFailure();

        ______TS("Typical case: edit course name with same name");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_NAME, courseName,
                Const.ParamsNames.COURSE_TIME_ZONE, courseTimeZone
        };

        // verify time zone will be changed
        String oldCourseTimeZone = typicalBundle.courses.get("typicalCourse1").getTimeZone().getId();
        assertNotEquals(courseTimeZone, oldCourseTimeZone);
        verifySessionsInCourseHaveTimeZone(courseId, oldCourseTimeZone);

        // execute the action
        courseEditSaveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(courseEditSaveAction);

        // get updated results and compare
        statusMessage = Const.StatusMessages.COURSE_EDITED;
        assertEquals(statusMessage, redirectResult.getStatusMessage());
        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, false, instructorId, courseId),
                redirectResult.getDestinationWithParams());
        verifySessionsInCourseHaveTimeZone(courseId, courseTimeZone);

        ______TS("Typical case: edit course name with valid characters");
        String courseNameWithValidCharacters = courseName + " valid";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_NAME, courseNameWithValidCharacters,
                Const.ParamsNames.COURSE_TIME_ZONE, courseTimeZone
        };

        // execute the action
        courseEditSaveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(courseEditSaveAction);

        // get updated results and compare
        statusMessage = Const.StatusMessages.COURSE_EDITED;
        assertEquals(statusMessage, redirectResult.getStatusMessage());
        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, false, instructorId, courseId),
                redirectResult.getDestinationWithParams());

        ______TS("Failure case: edit course name with empty string");
        courseName = "";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_NAME, courseName,
                Const.ParamsNames.COURSE_TIME_ZONE, courseTimeZone
        };

        // execute the action
        courseEditSaveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(courseEditSaveAction);

        // get updated results and compare
        statusMessage = getPopulatedEmptyStringErrorMessage(
                            FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                            FieldValidator.COURSE_NAME_FIELD_NAME, FieldValidator.COURSE_NAME_MAX_LENGTH);
        assertEquals(statusMessage, redirectResult.getStatusMessage());
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, true, instructorId, courseId),
                redirectResult.getDestinationWithParams());

        ______TS("Failure case: edit course name with non-alphanumeric start character");
        courseName = "@#$@#$";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_NAME, courseName,
                Const.ParamsNames.COURSE_TIME_ZONE, courseTimeZone
        };

        // execute the action
        courseEditSaveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(courseEditSaveAction);

        // get updated results and compare
        statusMessage = getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                            courseName, FieldValidator.COURSE_NAME_FIELD_NAME,
                            FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR);
        assertEquals(statusMessage, redirectResult.getStatusMessage());
        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, true, instructorId, courseId),
                redirectResult.getDestinationWithParams());

        ______TS("Failure case: edit course name with name containing | and %");
        courseName = "normal|name%";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_NAME, courseName,
                Const.ParamsNames.COURSE_TIME_ZONE, courseTimeZone
        };

        // execute the action
        courseEditSaveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(courseEditSaveAction);

        // get updated results and compare
        statusMessage = getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                            courseName, FieldValidator.COURSE_NAME_FIELD_NAME,
                            FieldValidator.REASON_CONTAINS_INVALID_CHAR);
        assertEquals(statusMessage, redirectResult.getStatusMessage());
        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, true, instructorId, courseId),
                redirectResult.getDestinationWithParams());

        ______TS("Failure case: invalid time zone");
        courseName = CoursesLogic.inst().getCourse(courseId).getName();
        courseTimeZone = "InvalidTimeZone";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_NAME, courseName,
                Const.ParamsNames.COURSE_TIME_ZONE, courseTimeZone
        };

        courseEditSaveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(courseEditSaveAction);

        statusMessage = getPopulatedErrorMessage(FieldValidator.TIME_ZONE_ERROR_MESSAGE,
                            courseTimeZone, FieldValidator.TIME_ZONE_FIELD_NAME,
                            FieldValidator.REASON_UNAVAILABLE_AS_CHOICE);
        assertEquals(statusMessage, redirectResult.getStatusMessage());
        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, true, instructorId, courseId),
                redirectResult.getDestinationWithParams());
    }

    private void verifySessionsInCourseHaveTimeZone(String courseId, String courseTimeZone) {
        List<FeedbackSessionAttributes> sessions = FeedbackSessionsLogic.inst().getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes session : sessions) {
            System.out.println(session);
            assertEquals(courseTimeZone, session.getTimeZone().getId());
        }
    }

    @Override
    protected InstructorCourseEditSaveAction getAction(String... params) {
        return (InstructorCourseEditSaveAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    protected String getPageResultDestination(String parentUri, boolean isError, String userId, String courseId) {
        String pageDestination = parentUri;
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.ERROR, Boolean.toString(isError));
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.COURSE_ID, courseId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.USER_ID, userId);
        return pageDestination;
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
                Const.ParamsNames.COURSE_TIME_ZONE, courseTimeZone
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifyCoursePrivilege(submissionParams);
    }
}
