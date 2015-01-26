package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.ProfilesDb;
import teammates.storage.api.StudentsDb;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentCourseJoinAuthenticatedAction;

public class StudentCourseJoinAuthenticatedActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        StudentsDb studentsDb = new StudentsDb();
        AccountsDb accountsDb = new AccountsDb();
        ProfilesDb profilesDb = new ProfilesDb();
        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        student1InCourse1 = studentsDb.getStudentForGoogleId(
                student1InCourse1.course, student1InCourse1.googleId);

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        ______TS("not enough parameters");

        verifyAssumptionFailure();

        ______TS("invalid key");

        String invalidKey = "invalid key";
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

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?error=true&user=" + student1InCourse1.googleId,
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
                        + "<a href=\"mailto:teammates@comp.nus.edu.sg?body=Your name:%0AYour course:%0AYour university:\">"
                        + "contact us</a> so that we can investigate.",
                redirectResult.getStatusMessage());
*/
        ______TS("join course with no feedback sessions, profile is empty");
        AccountAttributes studentInCourseNoFeedbackSessions = new AccountAttributes(
                "idOfNoFSStudent", "nameOfNoFSStudent", false,
                "noFSStudent@gmail.com", "TEAMMATES Test Institute 5");
        accountsDb.createAccount(studentInCourseNoFeedbackSessions);

        StudentAttributes studentInCourseNoFeedbackSessionsAttributes = new StudentAttributes(
                student1InCourse1.section,
                student1InCourse1.team,
                "nameOfNoFSStudent", "noFSStudent@gmail.com",
                "", "idOfCourseNoEvals");

        studentsDb.createEntity(studentInCourseNoFeedbackSessionsAttributes);
        studentInCourseNoFeedbackSessionsAttributes = studentsDb.getStudentForEmail(
                studentInCourseNoFeedbackSessionsAttributes.course, studentInCourseNoFeedbackSessionsAttributes.email);

        gaeSimulation.loginUser("idOfNoFSStudent");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(studentInCourseNoFeedbackSessionsAttributes.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_HOME_PAGE
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?persistencecourse=idOfCourseNoEvals"
                + "&error=false&user=idOfNoFSStudent",
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(
                "You have been successfully added to the course [idOfCourseNoEvals] Typical Course 3 with 0 Evals.<br />Currently, there are no open evaluation/feedback sessions in the course [idOfCourseNoEvals] Typical Course 3 with 0 Evals. When a session is open for submission you will be notified.<br />Meanwhile, you can update your profile <a href=\"/page/studentProfilePage\">here</a>.", 
                redirectResult.getStatusMessage());

        ______TS("join course with no feedback sessions, profile has one missing field");
        AccountAttributes studentInCourseNoFeedbackSessions2 = new AccountAttributes(
                "idOfNoFSStudent2", "nameOfNoFSStudent2", false,
                "noFSStudent@gmail.com", "TEAMMATES Test Institute 5");
        accountsDb.createAccount(studentInCourseNoFeedbackSessions2);
        
        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.googleId = studentInCourseNoFeedbackSessions2.googleId;
        spa.institute = studentInCourseNoFeedbackSessions2.institute;
        spa.shortName = "student";
        spa.email = "noFSStudent2@gmail.com";
        spa.moreInfo = "I am a student";
        spa.nationality = "Singaporean";
        profilesDb.updateStudentProfile(spa);

        StudentAttributes studentInCourseNoFeedbackSessionsAttributes2 = new StudentAttributes(
                student1InCourse1.section,
                student1InCourse1.team,
                "nameOfNoFSStudent2", "noFSStudent2@gmail.com",
                "", "idOfCourseNoEvals");

        studentsDb.createEntity(studentInCourseNoFeedbackSessionsAttributes2);
        studentInCourseNoFeedbackSessionsAttributes2 = studentsDb.getStudentForEmail(
                studentInCourseNoFeedbackSessionsAttributes2.course, studentInCourseNoFeedbackSessionsAttributes2.email);

        gaeSimulation.loginUser("idOfNoFSStudent2");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(studentInCourseNoFeedbackSessionsAttributes2.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_HOME_PAGE
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?persistencecourse=idOfCourseNoEvals"
                + "&error=false&user=idOfNoFSStudent2",
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(
                "You have been successfully added to the course [idOfCourseNoEvals] Typical Course 3 with 0 Evals.<br />Currently, there are no open evaluation/feedback sessions in the course [idOfCourseNoEvals] Typical Course 3 with 0 Evals. When a session is open for submission you will be notified.<br />Meanwhile, you can upload a profile picture <a href=\"/page/studentProfilePage\">here</a>.", 
                redirectResult.getStatusMessage());

        
        ______TS("typical case");

        AccountAttributes newStudentAccount = new AccountAttributes(
                "idOfNewStudent", "nameOfNewStudent", false,
                "newStudent@gmail.com", "TEAMMATES Test Institute 5");
        accountsDb.createAccount(newStudentAccount);

        StudentAttributes newStudentAttributes = new StudentAttributes(
                student1InCourse1.section,
                student1InCourse1.team,
                "nameOfNewStudent", "newStudent@course1.com",
                "This is a new student", student1InCourse1.course);

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

        assertEquals(Const.ActionURIs.STUDENT_PROFILE_PAGE
                + "?persistencecourse=idOfTypicalCourse1"
                + "&error=false&user=idOfNewStudent",
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(
                "You have been successfully added to the course [idOfTypicalCourse1] Typical Course 1 with 2 Evals.", 
                redirectResult.getStatusMessage());

    }

    private StudentCourseJoinAuthenticatedAction getAction(String... params)
            throws Exception {

        return (StudentCourseJoinAuthenticatedAction) (gaeSimulation
                .getActionObject(uri, params));

    }
}
