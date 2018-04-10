package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.api.InstructorsDb;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseJoinAuthenticatedAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorCourseJoinAuthenticatedAction}.
 */
public class InstructorCourseJoinAuthenticatedActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorsDb instrDb = new InstructorsDb();
        instructor = instrDb.getInstructorForEmail(instructor.courseId, instructor.email);
        String invalidEncryptedKey = StringHelper.encrypt("invalidKey");

        gaeSimulation.loginAsInstructor(instructor.googleId);

        ______TS("Failure: Invalid key");

        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, invalidEncryptedKey
        };

        InstructorCourseJoinAuthenticatedAction joinAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(joinAction);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE,
                        true,
                        "idOfInstructor1OfCourse1",
                        invalidEncryptedKey),
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
        redirectResult = getRedirectResult(joinAction);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE,
                        instructor.courseId,
                        true,
                        "idOfInstructor1OfCourse1",
                        StringHelper.encrypt(instructor.key)),
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals(instructor.googleId + " has already joined this course", redirectResult.getStatusMessage());

        expectedLogSegment = "Servlet Action Failure : " + instructor.googleId + " has already joined this course"
                            + "<br><br>Action Instructor Joins Course<br>Google ID: " + instructor.googleId
                            + "<br>Key : " + instructor.key;
        AssertHelper.assertContains(expectedLogSegment, joinAction.getLogMessage());

        ______TS("Failure: the current key has been registered by another account");

        InstructorAttributes instructor2 = typicalBundle.instructors.get("instructor2OfCourse1");
        instructor2 = instrDb.getInstructorForGoogleId(instructor2.courseId, instructor2.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(instructor2.key)
        };

        joinAction = getAction(submissionParams);
        redirectResult = getRedirectResult(joinAction);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE,
                        instructor2.courseId,
                        true,
                        "idOfInstructor1OfCourse1",
                        StringHelper.encrypt(instructor2.key)),
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        AssertHelper.assertContains("The join link used belongs to a different user", redirectResult.getStatusMessage());

        expectedLogSegment = "Servlet Action Failure : The join link used belongs to a different user";
        AssertHelper.assertContains(expectedLogSegment, joinAction.getLogMessage());

        ______TS("Typical case: authenticate for new instructor with corresponding key");

        instructor = InstructorAttributes
                .builder(null, instructor.courseId, "New Instructor", "ICJAAT.instr@email.com")
                .build();
        InstructorsLogic.inst().createInstructor(instructor);
        instructor.googleId = "ICJAAT.instr";

        AccountAttributes newInstructorAccount = AccountAttributes.builder()
                .withGoogleId(instructor.googleId)
                .withName(instructor.name)
                .withEmail(instructor.email)
                .withInstitute("TEAMMATES Test Institute 5")
                .withIsInstructor(false)
                .withDefaultStudentProfileAttributes(instructor.googleId)
                .build();
        AccountsLogic.inst().createAccount(newInstructorAccount);

        InstructorAttributes newInstructor = instrDb.getInstructorForEmail(instructor.courseId, instructor.email);

        gaeSimulation.loginUser(instructor.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(newInstructor.key)
        };

        joinAction = getAction(submissionParams);
        redirectResult = getRedirectResult(joinAction);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE,
                        "idOfTypicalCourse1",
                        false,
                        "ICJAAT.instr",
                        StringHelper.encrypt(newInstructor.key)),
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
        instructor = InstructorAttributes
                .builder(null, instructor.courseId, "New Instructor 2", "ICJAAT2.instr@email.com")
                .build();
        InstructorsLogic.inst().createInstructor(instructor);
        instructor.googleId = "ICJAAT2.instr";

        newInstructorAccount = AccountAttributes.builder()
                .withGoogleId(instructor.googleId)
                .withName(instructor.name)
                .withEmail(instructor.email)
                .withInstitute("TEAMMATES Test Institute 5")
                .withIsInstructor(false)
                .withDefaultStudentProfileAttributes(instructor.googleId)
                .build();
        AccountsLogic.inst().createAccount(newInstructorAccount);

        newInstructor = instrDb.getInstructorForEmail(instructor.courseId, instructor.email);

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(newInstructor.key)
        };

        joinAction = getAction(submissionParams);
        redirectResult = getRedirectResult(joinAction);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_HOME_PAGE,
                        "idOfTypicalCourse1",
                        true,
                        "ICJAAT.instr",
                        StringHelper.encrypt(newInstructor.key)),
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

    @Override
    protected InstructorCourseJoinAuthenticatedAction getAction(String... params) {
        return (InstructorCourseJoinAuthenticatedAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    protected String getPageResultDestination(String parentUri, boolean isError, String userId, String key) {
        String pageDestination = parentUri;
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.ERROR, Boolean.toString(isError));
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.USER_ID, userId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.REGKEY, key);
        return pageDestination;
    }

    protected String getPageResultDestination(
            String parentUri, String persistenceCourse, boolean isError, String userId, String key) {
        String pageDestination = parentUri;
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.CHECK_PERSISTENCE_COURSE, persistenceCourse);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.ERROR, Boolean.toString(isError));
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.USER_ID, userId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.REGKEY, key);
        return pageDestination;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String invalidEncryptedKey = StringHelper.encrypt("invalidKey");
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, invalidEncryptedKey
        };

        verifyOnlyLoggedInUsersCanAccess(submissionParams);
    }
}
