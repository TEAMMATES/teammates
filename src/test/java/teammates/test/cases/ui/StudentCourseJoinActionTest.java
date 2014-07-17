package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.api.StudentsDb;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentCourseJoinAction;
import teammates.ui.controller.StudentCourseJoinConfirmationPageData;

public class StudentCourseJoinActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		restoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_COURSE_JOIN;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        StudentsDb studentsDb = new StudentsDb();
        student1InCourse1 = studentsDb.getStudentForGoogleId(
                student1InCourse1.course, student1InCourse1.googleId);

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        ______TS("not enough parameters");

        verifyAssumptionFailure();

        ______TS("invalid key");
        String invalidKey = "invalid key";
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, invalidKey
        };
        try {
            StudentCourseJoinAction joinAction = getAction(submissionParams);
            joinAction.executeAndPostProcess();
            signalFailureToDetectException(" - Unauthorised Exception");
        } catch (UnauthorizedAccessException uae) {
            assertEquals("No student with given registration key:" + invalidKey, uae.getMessage());
        }

        ______TS("already registered student");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(student1InCourse1.key)
        };

        StudentCourseJoinAction joinAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(joinAction);

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?" + Const.ParamsNames.REGKEY + "=" + StringHelper.encrypt(student1InCourse1.key)
                + "&" + Const.ParamsNames.STUDENT_EMAIL + "=" + student1InCourse1.email.replace("@", "%40")
                + "&" + Const.ParamsNames.ERROR + "=false"
                + "&" + Const.ParamsNames.COURSE_ID + "=" + student1InCourse1.course,
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals("You are already a student of Course: " + student1InCourse1.course, redirectResult.getStatusMessage());

        ______TS("typical case");
        
        StudentAttributes newStudentData = new StudentAttributes(
                student1InCourse1.section,
                student1InCourse1.team,
                "nameOfNewStudent", "newStudent@course1.com",
                "This is a new student", student1InCourse1.course);
        studentsDb.createEntity(newStudentData);

        gaeSimulation.loginUser("idOfNewStudent");

        String newStudentKey = StringHelper.encrypt(studentsDb.getStudentForEmail(
                newStudentData.course, newStudentData.email).key);
        /*
         * Reason why get student attributes for student just added again from
         * StudentsDb:below test needs the student's key, which is auto
         * generated when creating the student instance.So the reg key needs to
         * be obtained by calling the getter from logic to retrieve again
         */
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, newStudentKey,
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_PROFILE_PAGE
        };

        joinAction = getAction(submissionParams);
        ShowPageResult pageResult = getShowPageResult(joinAction);

        assertEquals(Const.ViewURIs.STUDENT_COURSE_JOIN_CONFIRMATION
                + "?" + Const.ParamsNames.STUDENT_EMAIL + "=" + newStudentData.email.replace("@", "%40")
                + "&" + Const.ParamsNames.REGKEY + "=" + newStudentKey
                + "&" + Const.ParamsNames.ERROR + "=false"
                + "&" + Const.ParamsNames.COURSE_ID + "=" + newStudentData.course,
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals(Const.ActionURIs.STUDENT_PROFILE_PAGE, 
                ((StudentCourseJoinConfirmationPageData) pageResult.data).nextUrl);
        assertEquals("", pageResult.getStatusMessage());
        
        ______TS("skip confirmation");
        
        gaeSimulation.logoutUser();
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, newStudentKey,
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_PROFILE_PAGE,
                Const.ParamsNames.STUDENT_EMAIL, newStudentData.email,
                Const.ParamsNames.COURSE_ID, newStudentData.course
        };
        
        joinAction = getAction(submissionParams);
        redirectResult = getRedirectResult(joinAction);

        assertEquals(Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED
                + "?" + Const.ParamsNames.REGKEY + "=" + newStudentKey
                + "&" + Const.ParamsNames.NEXT_URL + "=" + Const.ActionURIs.STUDENT_PROFILE_PAGE.replace("/", "%2F")
                + "&" + Const.ParamsNames.STUDENT_EMAIL + "=" + newStudentData.email.replace("@", "%40")
                + "&" + Const.ParamsNames.ERROR + "=false"
                + "&" + Const.ParamsNames.COURSE_ID + "=" + newStudentData.course,
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        
        // delete the new student
        studentsDb.deleteStudent(newStudentData.course, newStudentData.email);

    }

    private StudentCourseJoinAction getAction(String... params)
            throws Exception {
        return (StudentCourseJoinAction) (gaeSimulation.getActionObject(uri,
                params));
    }

}
