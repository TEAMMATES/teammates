package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.ui.controller.ImageResult;
import teammates.ui.controller.StudentProfilePictureAction;

public class StudentProfilePictureActionTest extends BaseActionTest {

    private final DataBundle _dataBundle = getTypicalDataBundle();
    private StudentProfilePictureAction _action;
    private ImageResult _result;
    private final AccountAttributes _account = _dataBundle.accounts.get("student1InCourse1");
    private final StudentAttributes _student = _dataBundle.students.get("student1InCourse1");

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_PROFILE_PICTURE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        testActionWithNoParams();
        testActionWithBlobKey();
        testActionWithEmailAndCourse();
    }

    public void testActionWithNoParams() throws Exception {

        ______TS("Failure case: no parameters given");
        gaeSimulation.loginAsStudent(_account.googleId);

        String[] submissionParams = new String[] {};

        _action = getAction(submissionParams);
        try {
            _action.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals("expected blob-key, or student email with courseId", ae.getMessage());
        }

    }

    /**
     * Tests the branch of the Action handling a request from the Student
     * directly, where the parameters are simply the blobKey of the picture
     * itself.
     * 
     * @throws Exception
     */
    public void testActionWithBlobKey() throws Exception {
        testActionWithBlobKeySuccess();
        testActionWithBlobKeySuccessMasquerade();
    }

    protected void testActionWithBlobKeySuccess() throws Exception {
        ______TS("Typical case: using blobkey");
        gaeSimulation.loginAsStudent(_account.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.BLOB_KEY, _account.studentProfile.pictureKey
        };
        _action = getAction(submissionParams);
        _result = (ImageResult) _action.executeAndPostProcess();

        assertFalse(_result.isError);
        assertEquals("", _result.getStatusMessage());
        assertEquals(_account.studentProfile.pictureKey, _result.blobKey);
        verifyLogMessageForActionWithBlobKey(false);
    }

    protected void testActionWithBlobKeySuccessMasquerade() throws Exception {
        ______TS("Typical case: masquerade mode");
        gaeSimulation.loginAsAdmin("admin.user");

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, _account.googleId,
                Const.ParamsNames.BLOB_KEY, _account.studentProfile.pictureKey
        };
        _action = getAction(addUserIdToParams(_account.googleId, submissionParams));
        _result = (ImageResult) _action.executeAndPostProcess();

        assertFalse(_result.isError);
        assertEquals("", _result.getStatusMessage());
        verifyLogMessageForActionWithBlobKey(true);
    }

    /**
     * Tests the branch of the Action handling a request from an Instructor,
     * where the parameters are the student's course and email
     * 
     * @throws Exception
     */
    public void testActionWithEmailAndCourse() throws Exception {
        AccountAttributes instructor = _dataBundle.accounts.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor("idOfInstructor1OfCourse1");

        testActionWithEmailAndCourseSuccessTypical(instructor);
        testActionWithEmailAndCourseNoStudent();
        testActionWithEmailAndCourseForUnregStudent();
        testActionWithEmailAndCourseUnauthorisedInstructor();
    }

    protected void testActionWithEmailAndCourseSuccessTypical(AccountAttributes instructor)
            throws Exception {

        ______TS("Typical case: using email and course");

        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, StringHelper.encrypt(_student.email),
                Const.ParamsNames.COURSE_ID, StringHelper.encrypt(_student.course)
        };

        _action = getAction(submissionParams);
        _result = (ImageResult) _action.executeAndPostProcess();

        assertFalse(_result.isError);
        assertEquals("", _result.getStatusMessage());
        assertEquals("asdf34&hfn3!@", _result.blobKey);
        verifyLogMessageForActionWithEmailAndCourse(instructor, false);
    }

    protected void testActionWithEmailAndCourseNoStudent() throws Exception {
        ______TS("Failure case: student does not exist");

        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, StringHelper.encrypt("random-email"),
                Const.ParamsNames.COURSE_ID, StringHelper.encrypt(_student.course)
        };

        _action = getAction(submissionParams);
        try {
            _action.executeAndPostProcess();
            signalFailureToDetectException("Entity Does not exist");
        } catch (EntityDoesNotExistException uae) {
            assertEquals("student with " + _student.course + "/random-email", uae.getMessage());
        }
    }

    protected void testActionWithEmailAndCourseForUnregStudent() throws Exception {
        InstructorAttributes unregCourseInstructor = createNewInstructorForUnregCourse();
        gaeSimulation.loginAsInstructor(unregCourseInstructor.googleId);

        testActionForStudentWithEmptyGoogleId();

        // remove new instructor
        AccountsLogic.inst().deleteAccountCascade(unregCourseInstructor.googleId);
    }

    @SuppressWarnings("deprecation")
    private InstructorAttributes createNewInstructorForUnregCourse()
            throws Exception {
        String course = _dataBundle.courses.get("unregisteredCourse").id;
        AccountsLogic.inst().createAccount(new AccountAttributes("unregInsId", "unregName", true,
                                                                 "unregIns@unregcourse.com", "unregInstitute"));
        InstructorAttributes instructor = new InstructorAttributes("unregInsId", course, "unregName",
                                                                   "unregIns@unregcourse.com");
        InstructorsLogic.inst().createInstructor(instructor);
        return instructor;
    }

    private void testActionForStudentWithEmptyGoogleId() throws Exception {
        ______TS("Failure case: no profile available (unreg student)");

        StudentAttributes student = _dataBundle.students.get("student2InUnregisteredCourse");
        Assumption.assertIsEmpty(student.googleId);
        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, StringHelper.encrypt(student.email),
                Const.ParamsNames.COURSE_ID, StringHelper.encrypt(student.course)
        };

        _action = getAction(submissionParams);
        _result = (ImageResult) _action.executeAndPostProcess();

        assertEquals("", _result.blobKey);
    }

    protected void testActionWithEmailAndCourseUnauthorisedInstructor()
            throws Exception {
        ______TS("Failure case: instructor not from same course");
        AccountAttributes unauthInstructor = _dataBundle.accounts.get("instructor1OfCourse2");
        gaeSimulation.loginAsInstructor(unauthInstructor.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, StringHelper.encrypt(_student.email),
                Const.ParamsNames.COURSE_ID, StringHelper.encrypt(_student.course)
        };

        _action = getAction(submissionParams);
        try {
            _action.executeAndPostProcess();
            signalFailureToDetectException("Unauthorised Access");
        } catch (UnauthorizedAccessException uae) {
            assertEquals("User is not instructor of the course that student belongs to", uae.getMessage());
        }
    }

    // -------------------------------------------------------------------------------------------------------
    // -------------------------------------- Helper Functions
    // -----------------------------------------------
    // -------------------------------------------------------------------------------------------------------

    private void verifyLogMessageForActionWithEmailAndCourse(AccountAttributes instructor,
                                                             boolean isMasquerade) {
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePic|||studentProfilePic"
                                  + "|||true|||Instructor" + (isMasquerade ? "(M)" : "") + "|||"
                                  + instructor.name + "|||" + instructor.googleId + "|||"+ instructor.email
                                  + "|||Requested Profile Picture by instructor/other students|||/page/studentProfilePic";
        assertEquals(expectedLogMessage, _action.getLogMessage());
    }

    private void verifyLogMessageForActionWithBlobKey(boolean isMasquerade) {
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePic|||studentProfilePic"
                                  + "|||true|||Student" + (isMasquerade ? "(M)" : "") + "|||"
                                  + _account.name + "|||" + _account.googleId + "|||" + _student.email
                                  + "|||Requested Profile Picture by student directly|||/page/studentProfilePic";
        assertEquals(expectedLogMessage, _action.getLogMessage());
    }

    private StudentProfilePictureAction getAction(String... params) throws Exception {
        return (StudentProfilePictureAction) (gaeSimulation.getActionObject(uri, params));
    }

}