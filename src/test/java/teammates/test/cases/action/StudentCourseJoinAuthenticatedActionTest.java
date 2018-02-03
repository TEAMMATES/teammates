package teammates.test.cases.action;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.StudentsDb;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentCourseJoinAuthenticatedAction;

/**
 * SUT: {@link StudentCourseJoinAuthenticatedAction}.
 */
public class StudentCourseJoinAuthenticatedActionTest extends BaseActionTest {
    private DataBundle dataBundle = loadDataBundle("/StudentCourseJoinAuthenticatedTest.json");

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED;
    }

    @BeforeClass
    public void classSetup() {
        // extra test data used on top of typical data bundle
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        dataBundle = loadDataBundle("/StudentCourseJoinAuthenticatedTest.json");
        StudentsDb studentsDb = new StudentsDb();
        AccountsDb accountsDb = new AccountsDb();

        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        student1InCourse1 = studentsDb.getStudentForGoogleId(
                student1InCourse1.course, student1InCourse1.googleId);

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        ______TS("not enough parameters");

        verifyAssumptionFailure();

        ______TS("invalid key");

        String invalidKey = StringHelper.encrypt("invalid key");
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, invalidKey,
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_HOME_PAGE
        };

        try {
            StudentCourseJoinAuthenticatedAction authenticatedAction = getAction(submissionParams);
            getRedirectResult(authenticatedAction);
        } catch (UnauthorizedAccessException uae) {
            assertEquals("No student with given registration key:" + invalidKey, uae.getMessage());
        }

        ______TS("already registered student");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(student1InCourse1.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_PROFILE_PAGE
        };

        StudentCourseJoinAuthenticatedAction authenticatedAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(
                getPageResultDestination(Const.ActionURIs.STUDENT_HOME_PAGE, true, student1InCourse1.googleId),
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals("You (student1InCourse1) have already joined this course",
                redirectResult.getStatusMessage());

        /*______TS("student object belongs to another account");

        StudentAttributes student2InCourse1 = dataBundle.students
                .get("student2InCourse1");
        student2InCourse1 = studentsDb.getStudentForGoogleId(
                student2InCourse1.course, student2InCourse1.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(student2InCourse1.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_HOME_PAGE
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(
                Const.ActionURIs.STUDENT_HOME_PAGE
                        + "?persistencecourse=" + student1InCourse1.course
                        + "&error=true&user=" + student1InCourse1.googleId,
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals(
                "The join link used belongs to a different user"
                        + " whose Google ID is stude..ourse1 "
                        + "(only part of the Google ID is shown to protect privacy)."
                        + " If that Google ID is owned by you, "
                        + "please logout and re-login using that Google account."
                        + " If it doesn’t belong to you, please "
                        + "<a href=\"mailto:" + Config.SUPPORT_EMAIL
                        + "?body=Your name:%0AYour course:%0AYour university:\">"
                        + "contact us</a> so that we can investigate.",
                redirectResult.getStatusMessage());
*/
        ______TS("join course with no feedback sessions, profile is empty");
        AccountAttributes studentWithEmptyProfile = dataBundle.accounts.get("noFSStudent");
        studentWithEmptyProfile = accountsDb.getAccount(studentWithEmptyProfile.googleId, true);
        assertNotNull(studentWithEmptyProfile.studentProfile);
        assertEquals("", studentWithEmptyProfile.studentProfile.pictureKey);
        assertEquals("", studentWithEmptyProfile.studentProfile.shortName);
        assertEquals("", studentWithEmptyProfile.studentProfile.nationality);
        assertEquals("", studentWithEmptyProfile.studentProfile.moreInfo);
        assertEquals("", studentWithEmptyProfile.studentProfile.email);

        StudentAttributes studentWithEmptyProfileAttributes = dataBundle.students.get("noFSStudentWithNoProfile");
        studentWithEmptyProfileAttributes = studentsDb.getStudentForEmail(
                studentWithEmptyProfileAttributes.course, studentWithEmptyProfileAttributes.email);

        gaeSimulation.loginUser("idOfNoFSStudent");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(studentWithEmptyProfileAttributes.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_HOME_PAGE
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.STUDENT_HOME_PAGE, "idOfCourseNoEvals", false, "idOfNoFSStudent"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL,
                              "[idOfCourseNoEvals] Typical Course 3 with 0 Evals")
                + "<br>"
                + String.format(Const.StatusMessages.HINT_FOR_NO_SESSIONS_STUDENT,
                                "[idOfCourseNoEvals] Typical Course 3 with 0 Evals")
                + "<br>"
                + Const.StatusMessages.STUDENT_UPDATE_PROFILE,
                redirectResult.getStatusMessage());

        ______TS("join course with no feedback sessions, profile has only one missing field");
        AccountAttributes studentWithoutProfilePicture = dataBundle.accounts.get("noFSStudent2");
        studentWithoutProfilePicture = accountsDb.getAccount(studentWithoutProfilePicture.googleId, true);
        assertNotNull(studentWithoutProfilePicture.studentProfile);
        assertEquals("", studentWithoutProfilePicture.studentProfile.pictureKey);
        assertFalse(studentWithoutProfilePicture.studentProfile.nationality.isEmpty());
        assertFalse(studentWithoutProfilePicture.studentProfile.shortName.isEmpty());
        assertFalse(studentWithoutProfilePicture.studentProfile.moreInfo.isEmpty());
        assertFalse(studentWithoutProfilePicture.studentProfile.email.isEmpty());

        StudentAttributes studentWithoutProfilePictureAttributes = dataBundle.students.get("noFSStudentWithPartialProfile");

        studentWithoutProfilePictureAttributes = studentsDb.getStudentForEmail(
                studentWithoutProfilePictureAttributes.course, studentWithoutProfilePictureAttributes.email);

        gaeSimulation.loginUser("idOfNoFSStudent2");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(studentWithoutProfilePictureAttributes.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_HOME_PAGE
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.STUDENT_HOME_PAGE,
                        "idOfCourseNoEvals",
                        false,
                        "idOfNoFSStudent2"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL,
                              "[idOfCourseNoEvals] Typical Course 3 with 0 Evals")
                + "<br>"
                + String.format(Const.StatusMessages.HINT_FOR_NO_SESSIONS_STUDENT,
                                "[idOfCourseNoEvals] Typical Course 3 with 0 Evals")
                + "<br>"
                + Const.StatusMessages.STUDENT_UPDATE_PROFILE_PICTURE,
                redirectResult.getStatusMessage());

        ______TS("join course with no feedback sessions, profile has no missing field");
        AccountAttributes studentWithFullProfile = dataBundle.accounts.get("noFSStudent3");

        studentWithFullProfile = accountsDb.getAccount(studentWithFullProfile.googleId, true);
        assertNotNull(studentWithFullProfile.studentProfile);
        assertFalse(studentWithFullProfile.studentProfile.pictureKey.isEmpty());
        assertFalse(studentWithoutProfilePicture.studentProfile.nationality.isEmpty());
        assertFalse(studentWithoutProfilePicture.studentProfile.shortName.isEmpty());
        assertFalse(studentWithoutProfilePicture.studentProfile.moreInfo.isEmpty());
        assertFalse(studentWithoutProfilePicture.studentProfile.email.isEmpty());

        StudentAttributes studentWithFullProfileAttributes = dataBundle.students.get("noFSStudentWithFullProfile");
        studentWithFullProfileAttributes = studentsDb.getStudentForEmail(
                studentWithFullProfileAttributes.course, studentWithFullProfileAttributes.email);

        gaeSimulation.loginUser("idOfNoFSStudent3");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(studentWithFullProfileAttributes.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_HOME_PAGE
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.STUDENT_HOME_PAGE, "idOfCourseNoEvals", false, "idOfNoFSStudent3"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL,
                              "[idOfCourseNoEvals] Typical Course 3 with 0 Evals")
                + "<br>"
                + String.format(Const.StatusMessages.HINT_FOR_NO_SESSIONS_STUDENT,
                                "[idOfCourseNoEvals] Typical Course 3 with 0 Evals"),
                redirectResult.getStatusMessage());

        ______TS("typical case");

        AccountAttributes newStudentAccount = AccountAttributes.builder()
                .withGoogleId("idOfNewStudent")
                .withName("nameOfNewStudent")
                .withEmail("newStudent@gmail.com")
                .withInstitute("TEAMMATES Test Institute 5")
                .withIsInstructor(false)
                .withDefaultStudentProfileAttributes("idOfNewStudent")
                .build();

        accountsDb.createAccount(newStudentAccount);

        StudentAttributes newStudentAttributes = StudentAttributes
                .builder(student1InCourse1.course, "nameOfNewStudent", "newStudent@course1.com")
                .withSection(student1InCourse1.section)
                .withTeam(student1InCourse1.team)
                .withComments("This is a new student")
                .build();

        studentsDb.createEntity(newStudentAttributes);
        newStudentAttributes = studentsDb.getStudentForEmail(
                newStudentAttributes.course, newStudentAttributes.email);

        gaeSimulation.loginUser("idOfNewStudent");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(newStudentAttributes.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_PROFILE_PAGE
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.STUDENT_PROFILE_PAGE, "idOfTypicalCourse1", false, "idOfNewStudent"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL,
                              "[idOfTypicalCourse1] Typical Course 1 with 2 Evals"),
                redirectResult.getStatusMessage());

        ______TS("typical case: data requires sanitization");

        AccountAttributes accountTestSanitization = dataBundle.accounts.get("student1InTestingSanitizationCourse");
        StudentAttributes studentTestSanitization = dataBundle.students.get("student1InTestingSanitizationCourse");
        CourseAttributes courseTestSanitization = dataBundle.courses.get("testingSanitizationCourse");

        gaeSimulation.loginUser(accountTestSanitization.googleId);

        // retrieve student from datastore to get regkey
        studentTestSanitization =
                studentsDb.getStudentForEmail(studentTestSanitization.course, studentTestSanitization.email);

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(studentTestSanitization.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_PROFILE_PAGE
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(
                Const.ActionURIs.STUDENT_PROFILE_PAGE
                        + "?persistencecourse=" + courseTestSanitization.getId()
                        + "&error=false&user=" + accountTestSanitization.googleId,
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        String courseIdentifier = "[" + courseTestSanitization.getId() + "] "
                + SanitizationHelper.sanitizeForHtml(courseTestSanitization.getName());
        String expectedStatusMessage =
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, courseIdentifier) + "<br>"
                + String.format(Const.StatusMessages.HINT_FOR_NO_SESSIONS_STUDENT, courseIdentifier) + "<br>"
                + accountTestSanitization.studentProfile.generateUpdateMessageForStudent();
        assertEquals(expectedStatusMessage, redirectResult.getStatusMessage());
    }

    @Override
    protected StudentCourseJoinAuthenticatedAction getAction(String... params) {
        return (StudentCourseJoinAuthenticatedAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    protected String getPageResultDestination(String parentUri, String persistenceCourse, boolean isError, String userId) {
        String pageDestination = parentUri;
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.CHECK_PERSISTENCE_COURSE, persistenceCourse);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.ERROR, Boolean.toString(isError));
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.USER_ID, userId);
        return pageDestination;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {

        StudentAttributes unregStudent1 = typicalBundle.students.get("student1InUnregisteredCourse");
        String key = StudentsLogic.inst().getStudentForEmail(unregStudent1.course, unregStudent1.email).key;
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(key),
                Const.ParamsNames.NEXT_URL, "randomUrl"
        };

        verifyUnaccessibleWithoutLogin(submissionParams);

        unregStudent1.googleId = "";
        StudentsLogic.inst().updateStudentCascade(unregStudent1.email, unregStudent1);
        verifyAccessibleForUnregisteredUsers(submissionParams);

        unregStudent1.googleId = "";
        StudentsLogic.inst().updateStudentCascade(unregStudent1.email, unregStudent1);
        verifyAccessibleForStudents(submissionParams);

        unregStudent1.googleId = "";
        StudentsLogic.inst().updateStudentCascade(unregStudent1.email, unregStudent1);
        verifyAccessibleForInstructorsOfOtherCourses(submissionParams);

        unregStudent1.googleId = "";
        StudentsLogic.inst().updateStudentCascade(unregStudent1.email, unregStudent1);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
}
