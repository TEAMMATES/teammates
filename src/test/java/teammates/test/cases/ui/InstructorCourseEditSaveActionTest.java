package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.core.CoursesLogic;
import teammates.ui.controller.InstructorCourseEditSaveAction;
import teammates.ui.controller.RedirectResult;

public class InstructorCourseEditSaveActionTest extends BaseActionTest {
    
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_SAVE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor.googleId;
        String courseId = instructor.courseId;
        String courseName = CoursesLogic.inst().getCourse(courseId).getName();
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
                Const.ParamsNames.COURSE_NAME, courseName
        };
        
        // execute the action
        courseEditSaveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(courseEditSaveAction);
        
        // get updated results and compare
        statusMessage = Const.StatusMessages.COURSE_EDITED;
        assertEquals(statusMessage, redirectResult.getStatusMessage());
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE
                     + "?error=false&user=" + instructorId + "&courseid=" + courseId,
                     redirectResult.getDestinationWithParams());

        ______TS("Typical case: edit course name with valid characters");
        String courseNameWithValidCharacters = courseName + " valid";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_NAME, courseNameWithValidCharacters
        };

        // execute the action
        courseEditSaveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(courseEditSaveAction);

        // get updated results and compare
        statusMessage = Const.StatusMessages.COURSE_EDITED;
        assertEquals(statusMessage, redirectResult.getStatusMessage());
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE
                     + "?error=false&user=" + instructorId + "&courseid=" + courseId,
                     redirectResult.getDestinationWithParams());

        ______TS("Failure case: edit course name with empty string");
        courseName = "";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_NAME, courseName
        };

        // execute the action
        courseEditSaveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(courseEditSaveAction);

        // get updated results and compare
        statusMessage = getPopulatedErrorMessage(FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                            courseName, FieldValidator.COURSE_NAME_FIELD_NAME,
                            FieldValidator.REASON_EMPTY, FieldValidator.COURSE_NAME_MAX_LENGTH);
        assertEquals(statusMessage, redirectResult.getStatusMessage());
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE
                     + "?error=true&user=" + instructorId + "&courseid=" + courseId,
                     redirectResult.getDestinationWithParams());

        ______TS("Failure case: edit course name with non-alphanumeric start character");
        courseName = "@#$@#$";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_NAME, courseName
        };

        // execute the action
        courseEditSaveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(courseEditSaveAction);

        // get updated results and compare
        statusMessage = getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                            courseName, FieldValidator.COURSE_NAME_FIELD_NAME,
                            FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR);
        assertEquals(statusMessage, redirectResult.getStatusMessage());
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE
                     + "?error=true&user=" + instructorId + "&courseid=" + courseId,
                     redirectResult.getDestinationWithParams());

        ______TS("Failure case: edit course name with name containing | and %");
        courseName = "normal|name%";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.COURSE_NAME, courseName
        };

        // execute the action
        courseEditSaveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(courseEditSaveAction);

        // get updated results and compare
        statusMessage = getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                            courseName, FieldValidator.COURSE_NAME_FIELD_NAME,
                            FieldValidator.REASON_CONTAINS_INVALID_CHAR);
        assertEquals(statusMessage, redirectResult.getStatusMessage());
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE
                     + "?error=true&user=" + instructorId + "&courseid=" + courseId,
                     redirectResult.getDestinationWithParams());
    }

    private InstructorCourseEditSaveAction getAction(String... params) {
        return (InstructorCourseEditSaveAction) (gaeSimulation.getActionObject(uri, params));
    }
}
