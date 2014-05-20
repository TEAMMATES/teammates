package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

import teammates.storage.api.AccountsDb;
import teammates.storage.api.StudentsDb;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentCourseJoinAuthenticatedAction;

public class StudentCourseJoinAuthenticatedActionTest extends BaseActionTest {
    DataBundle dataBundle;

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED;
    }

    @BeforeMethod
    public void methodSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }

    @Test
    public void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, "sample-key"
        };

        verifyOnlyLoggedInUsersCanAccess(submissionParams);
        verifyUnaccessibleWithoutLogin(submissionParams);
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
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

        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, "invalid key"
        };

        StudentCourseJoinAuthenticatedAction authenticatedAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?message=You+have+used+an+invalid+join+link"
                + "%3A+%2Fpage%2FstudentCourseJoin%3Fregkey%3Dinvalid+key"
                + "&error=true&user=" + student1InCourse1.googleId,
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals("You have used an invalid join link:"
                + " /page/studentCourseJoin?regkey=invalid key",
                redirectResult.getStatusMessage());

        ______TS("already registered student");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(student1InCourse1.key)
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?message=student1InCourse1+has+already+joined+this+course"
                + "&persistencecourse=" + student1InCourse1.course
                + "&error=true&user=" + student1InCourse1.googleId,
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals("student1InCourse1 has already joined this course", 
                redirectResult.getStatusMessage());

        ______TS("student object belongs to another account");

        StudentAttributes student2InCourse1 = dataBundle.students
                .get("student2InCourse1");
        student2InCourse1 = studentsDb.getStudentForGoogleId(
                student2InCourse1.course, student2InCourse1.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(student2InCourse1.key)
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(
                Const.ActionURIs.STUDENT_HOME_PAGE
                        + "?message=The+join+link+used+belongs+to+a+different+user"
                        + "+whose+Google+ID+is+stude..ourse1"
                        + "+%28only+part+of+the+Google+ID+is+shown+to+protect+privacy%29."
                        + "+If+that+Google+ID+is+owned+by+you%2C+please+logout+and"
                        + "+re-login+using+that+Google+account.+If+it+doesn%E2%80%99t"
                        + "+belong+to+you%2C+please+%3Ca+href%3D%22mailto"
                        + "%3Ateammates%40comp.nus.edu.sg%3Fbody%3D"
                        + "Your+name%3A%250AYour+course%3A%250AYour+university%3A%22%3E"
                        + "contact+us%3C%2Fa%3E+so+that+we+can+investigate."
                        + "&persistencecourse=" + student1InCourse1.course
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

        ______TS("typical case");

        AccountAttributes newStudentAccount = new AccountAttributes(
                "idOfNewStudent", "nameOfNewStudent", false,
                "newStudent@gmail.com", "NUS");
        accountsDb.createAccount(newStudentAccount);

        StudentAttributes newStudentAttributes = new StudentAttributes(
                student1InCourse1.team,
                "nameOfNewStudent", "newStudent@course1.com",
                "This is a new student", student1InCourse1.course);

        studentsDb.createEntity(newStudentAttributes);
        newStudentAttributes = studentsDb.getStudentForEmail(
                newStudentAttributes.course, newStudentAttributes.email);

        gaeSimulation.loginUser("idOfNewStudent");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(newStudentAttributes.key)
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?persistencecourse=idOfTypicalCourse1"
                + "&error=false&user=idOfNewStudnet",
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals("", redirectResult.getStatusMessage());

    }

    private StudentCourseJoinAuthenticatedAction getAction(String... params)
            throws Exception {

        return (StudentCourseJoinAuthenticatedAction) (gaeSimulation
                .getActionObject(uri, params));

    }
}
