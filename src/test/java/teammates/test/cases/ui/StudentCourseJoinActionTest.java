package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
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
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentCourseJoinAction;

public class StudentCourseJoinActionTest extends BaseActionTest {
    DataBundle dataBundle;

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.STUDENT_COURSE_JOIN;
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
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        StudentsDb studentsDb = new StudentsDb();
        AccountsDb accountsDb = new AccountsDb();
        student1InCourse1 = studentsDb.getStudentForGoogleId(
                student1InCourse1.course, student1InCourse1.googleId);

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        ______TS("not enough parameters");

        verifyAssumptionFailure();

        ______TS("invalid key");

        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, "invalid key"
        };
        StudentCourseJoinAction joinAction = getAction(submissionParams);
        ShowPageResult pageResult = getShowPageResult(joinAction);

        assertEquals(Const.ViewURIs.STUDENT_COURSE_JOIN_CONFIRMATION
                + "?error=false&user=" + student1InCourse1.googleId,
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        ______TS("already registered student");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(student1InCourse1.key)
        };

        joinAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(joinAction);

        assertEquals(Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED
                + "?regkey=" + StringHelper.encrypt(student1InCourse1.key)
                + "&error=false&user=" + student1InCourse1.googleId,
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);

        ______TS("typical case");

        AccountAttributes accountToAdd = new AccountAttributes(
                "idOfNewStudnet", "nameOfNewStudent", false,
                "newStudent@gmail.com", "NUS");
        
        accountsDb.createAccount(accountToAdd);
        
        StudentAttributes newStudentData = new StudentAttributes(
                student1InCourse1.section,
                student1InCourse1.team,
                "nameOfNewStudent", "newStudent@course1.com",
                "This is a new student", student1InCourse1.course);
        studentsDb.createEntity(newStudentData);

        gaeSimulation.loginUser("idOfNewStudent");

        StudentAttributes newStudent = studentsDb.getStudentForEmail(
                newStudentData.course, newStudentData.email);
        /*
         * Reason why get student attributes for student just added again from
         * StudentsDb:below test needs the student's key, which is auto
         * generated when creating the student instance.So the reg key needs to
         * be obtained by calling the getter from logic to retrieve again
         */
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(newStudent.key)
        };

        joinAction = getAction(submissionParams);
        pageResult = getShowPageResult(joinAction);

        assertEquals(Const.ViewURIs.STUDENT_COURSE_JOIN_CONFIRMATION
                + "?error=false&user=idOfNewStudent",
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

    }

    private StudentCourseJoinAction getAction(String... params)
            throws Exception {
        return (StudentCourseJoinAction) (gaeSimulation.getActionObject(uri,
                params));
    }

}
