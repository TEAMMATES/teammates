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
        testActionWithMasqueradeMode();
    }

    private void testActionWithNoParams() throws Exception {
        
        ______TS("failure: no parameters given");
        
        String[] submissionParams = new String[]{};
        
        gaeSimulation.loginAsStudent(_account.googleId);
        
        _action = getAction(submissionParams);
        try {
            _action.executeAndPostProcess();
            signalFailureToDetectException();
        } catch(AssertionError ae) {
            assertEquals("expected blob-key, or student email with courseId", ae.getMessage());
        }
        
    }

    private void testActionWithBlobKey() throws Exception {        
        ______TS("using blobkey");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.BLOB_KEY, _account.studentProfile.pictureKey
        };
        
        _action = getAction(submissionParams);
        _result = (ImageResult) _action.executeAndPostProcess();
        
        assertFalse(_result.isError);
        assertEquals("", _result.getStatusMessage());
        
        assertEquals(_account.studentProfile.pictureKey, _result.blobKey);
        
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePic|||studentProfilePic" +
                "|||true|||Student|||"+ _account.name +"|||" + _account.googleId + "|||" + _student.email +
                "|||Requested Profile Picture by student directly|||/page/studentProfilePic";
        
        assertEquals(expectedLogMessage, _action.getLogMessage());
    }
    
    private void testActionWithEmailAndCourse() throws Exception {
        
        AccountAttributes instructor = _dataBundle.accounts.get("instructor1OfCourse1");
        
        ______TS("using email and course");
        gaeSimulation.loginAsInstructor("idOfInstructor1OfCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.STUDENT_EMAIL, StringHelper.encrypt(_student.email),
                Const.ParamsNames.COURSE_ID, StringHelper.encrypt(_student.course)
        };
        
        _action = getAction(submissionParams);
        _result = (ImageResult) _action.executeAndPostProcess();
        
        assertFalse(_result.isError);
        assertEquals("", _result.getStatusMessage());
        
        assertEquals("asdf34&hfn3!@", _result.blobKey);
        
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePic|||studentProfilePic" +
                "|||true|||Instructor|||"+ instructor.name +"|||" + instructor.googleId + "|||" + instructor.email +
                "|||Requested Profile Picture by instructor/other students|||/page/studentProfilePic";
        
        assertEquals(expectedLogMessage, _action.getLogMessage());
        
        ______TS("failure: student does not exist");
        
        submissionParams = new String[]{
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
        
        ______TS("failuer: no profile available (unreg student)");
        
        StudentAttributes student = _dataBundle.students.get("student1InUnregisteredCourse");
        
        AccountsLogic.inst().createAccount(new AccountAttributes("unregInsId", "unregName", true, 
                "unregIns@unregcourse.com", "unregInstitute"));
        InstructorAttributes unregCourseInstructor = 
                new InstructorAttributes("unregInsId",student.course,"unregName", "unregIns@unregcourse.com");
        
        InstructorsLogic.inst().createInstructor(unregCourseInstructor);
        
        
        // googleId is null
        gaeSimulation.loginAsInstructor(unregCourseInstructor.googleId);
        
        submissionParams = new String[]{
                Const.ParamsNames.STUDENT_EMAIL, StringHelper.encrypt(student.email),
                Const.ParamsNames.COURSE_ID, StringHelper.encrypt(student.course)
        };
        
        _action = getAction(submissionParams);
        _result = (ImageResult) _action.executeAndPostProcess();
        
        assertEquals("", _result.blobKey);
        
        // googleId is empty
        student = _dataBundle.students.get("student2InUnregisteredCourse");
        
        submissionParams = new String[]{
                Const.ParamsNames.STUDENT_EMAIL, StringHelper.encrypt(student.email),
                Const.ParamsNames.COURSE_ID, StringHelper.encrypt(student.course)
        };
        
        _action = getAction(submissionParams);
        _result = (ImageResult) _action.executeAndPostProcess();
        
        assertEquals("", _result.blobKey);
        
        ______TS("failure: instructor not from same course");
        
        submissionParams = new String[]{
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
        
        // remove new instructor
        AccountsLogic.inst().deleteAccountCascade(unregCourseInstructor.googleId);
        
    }

    private void testActionWithMasqueradeMode() throws Exception {
        ______TS("masquerade mode");
        
        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.USER_ID, _account.googleId,
                Const.ParamsNames.BLOB_KEY, _account.studentProfile.pictureKey
        };
        
        _action = getAction(addUserIdToParams(_account.googleId, submissionParams));
        _result = (ImageResult) _action.executeAndPostProcess();
        
        assertFalse(_result.isError);
        assertEquals("", _result.getStatusMessage());
        
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePic|||studentProfilePic" +
                "|||true|||Student(M)|||"+ _account.name +"|||" + _account.googleId + "|||" + _student.email +
                "|||Requested Profile Picture by student directly|||/page/studentProfilePic" ;
        assertEquals(expectedLogMessage, _action.getLogMessage());
        
    }

    private StudentProfilePictureAction getAction(String... params) throws Exception{
            return (StudentProfilePictureAction) (gaeSimulation.getActionObject(uri, params));
    }

}