package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.api.InstructorsDb;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseJoinAuthenticatedAction;
import teammates.ui.controller.RedirectResult;

public class InstructorCourseJoinAuthenticatedActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();
    private final String invalidEncryptedKey = StringHelper.encrypt("invalidKey");

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED;
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorsDb instrDb = new InstructorsDb();
        instructor = instrDb.getInstructorForEmail(instructor.courseId, instructor.email);
        
        gaeSimulation.loginAsInstructor(instructor.googleId);
            
        ______TS("Failure: Invalid key");
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, invalidEncryptedKey
        };
        
        InstructorCourseJoinAuthenticatedAction joinAction = getAction(submissionParams);
        RedirectResult redirectResult = (RedirectResult) joinAction.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                + "?error=true&user=idOfInstructor1OfCourse1"
                + "&key=" + invalidEncryptedKey,
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals("You have used an invalid join link: "
                             + Const.ActionURIs.INSTRUCTOR_COURSE_JOIN + "?key=" + invalidEncryptedKey,
                     redirectResult.getStatusMessage());

        String expectedLogSegment = "Servlet Action Failure : You have used an invalid join link: "
                                    + Const.ActionURIs.INSTRUCTOR_COURSE_JOIN
                                    + "?key=" + invalidEncryptedKey + "<br><br>Action Instructor Joins Course<br>"
                                    + "Google ID: idOfInstructor1OfCourse1<br>Key : invalidKey";
        AssertHelper.assertContains(expectedLogSegment, joinAction.getLogMessage());
        
        ______TS("Failure: Instructor already registered");
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(instructor.key)
        };
        
        joinAction = getAction(submissionParams);
        redirectResult = (RedirectResult) joinAction.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                + "?persistencecourse=" + instructor.courseId
                + "&error=true&user=idOfInstructor1OfCourse1"
                + "&" + Const.ParamsNames.REGKEY + "=" + StringHelper.encrypt(instructor.key),
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals(instructor.googleId + " has already joined this course", redirectResult.getStatusMessage());

        expectedLogSegment = "Servlet Action Failure : " + instructor.googleId + " has already joined this course"
                            + "<br><br>Action Instructor Joins Course<br>Google ID: " + instructor.googleId
                            + "<br>Key : " + instructor.key;
        AssertHelper.assertContains(expectedLogSegment, joinAction.getLogMessage());
        
        ______TS("Failure: the current key has been registered by another account");
        
        InstructorAttributes instructor2 = dataBundle.instructors.get("instructor2OfCourse1");
        instructor2 = instrDb.getInstructorForGoogleId(instructor2.courseId, instructor2.googleId);
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(instructor2.key)
        };
        
        joinAction = getAction(submissionParams);
        redirectResult = (RedirectResult) joinAction.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                + "?persistencecourse=" + instructor2.courseId
                + "&error=true&user=idOfInstructor1OfCourse1"
                + "&" + Const.ParamsNames.REGKEY + "=" + StringHelper.encrypt(instructor2.key),
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        AssertHelper.assertContains("The join link used belongs to a different user", redirectResult.getStatusMessage());

        expectedLogSegment = "Servlet Action Failure : The join link used belongs to a different user";
        AssertHelper.assertContains(expectedLogSegment, joinAction.getLogMessage());
        
        ______TS("Typical case: authenticate for new instructor with corresponding key");
        
        instructor = new InstructorAttributes(null, instructor.courseId, "New Instructor", "ICJAAT.instr@email.com");
        InstructorsLogic.inst().createInstructor(instructor);
        instructor.googleId = "ICJAAT.instr";
        
        AccountAttributes newInstructorAccount = new AccountAttributes(
                instructor.googleId, instructor.name, false,
                instructor.email, "TEAMMATES Test Institute 5");
        AccountsLogic.inst().createAccount(newInstructorAccount);
        
        InstructorAttributes newInstructor = instrDb.getInstructorForEmail(instructor.courseId, instructor.email);
        
        gaeSimulation.loginUser(instructor.googleId);
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(newInstructor.key)
        };
        
        joinAction = getAction(submissionParams);
        redirectResult = (RedirectResult) joinAction.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                + "?persistencecourse=idOfTypicalCourse1"
                + "&error=false&user=ICJAAT.instr"
                + "&" + Const.ParamsNames.REGKEY + "=" + StringHelper.encrypt(newInstructor.key),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals("", redirectResult.getStatusMessage());
    
        InstructorAttributes retrievedInstructor = instrDb.getInstructorForEmail(instructor.courseId, instructor.email);
        assertEquals(instructor.googleId, retrievedInstructor.googleId);

        expectedLogSegment = "Action Instructor Joins Course<br>Google ID: " + instructor.googleId
                            + "<br>Key : " + newInstructor.key;
        AssertHelper.assertContains(expectedLogSegment, joinAction.getLogMessage());

        ______TS("Failure case: the current unused key is not for this account ");
        
        String currentLoginId = instructor.googleId;
        instructor = new InstructorAttributes(null, instructor.courseId, "New Instructor 2", "ICJAAT2.instr@email.com");
        InstructorsLogic.inst().createInstructor(instructor);
        instructor.googleId = "ICJAAT2.instr";
        
        newInstructorAccount = new AccountAttributes(
                instructor.googleId, instructor.name, false,
                instructor.email, "TEAMMATES Test Institute 5");
        AccountsLogic.inst().createAccount(newInstructorAccount);
        
        newInstructor = instrDb.getInstructorForEmail(instructor.courseId, instructor.email);
            
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(newInstructor.key)
        };
        
        joinAction = getAction(submissionParams);
        redirectResult = (RedirectResult) joinAction.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                + "?persistencecourse=idOfTypicalCourse1"
                + "&error=true&user=ICJAAT.instr"
                + "&" + Const.ParamsNames.REGKEY + "=" + StringHelper.encrypt(newInstructor.key),
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.JOIN_COURSE_GOOGLE_ID_BELONGS_TO_DIFFERENT_USER, currentLoginId),
                     redirectResult.getStatusMessage());

        expectedLogSegment = "Servlet Action Failure : "
                             + String.format(Const.StatusMessages.JOIN_COURSE_GOOGLE_ID_BELONGS_TO_DIFFERENT_USER,
                                             currentLoginId)
                             + "<br><br>Action Instructor Joins Course<br>Google ID: "
                             + currentLoginId + "<br>Key : " + newInstructor.key;
        AssertHelper.assertContains(expectedLogSegment, joinAction.getLogMessage());
    }
    
    private InstructorCourseJoinAuthenticatedAction getAction(String... params) {
        return (InstructorCourseJoinAuthenticatedAction) gaeSimulation.getActionObject(uri, params);
    }
}
