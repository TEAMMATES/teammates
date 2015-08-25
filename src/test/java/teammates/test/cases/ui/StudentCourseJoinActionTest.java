package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
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
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_COURSE_JOIN_NEW;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        
        StudentCourseJoinAction joinAction;
        RedirectResult redirectResult;
        String[] submissionParams;
        
        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        StudentsDb studentsDb = new StudentsDb();
        student1InCourse1 = studentsDb.getStudentForGoogleId(
                student1InCourse1.course, student1InCourse1.googleId);

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        ______TS("not enough parameters");

        verifyAssumptionFailure();

        ______TS("typical case");
        
        String idOfNewStudent = "idOfNewStudent";
        StudentAttributes newStudentData = new StudentAttributes(
                student1InCourse1.section,
                student1InCourse1.team,
                "nameOfNewStudent", "newStudent@course1.com",
                "This is a new student", student1InCourse1.course);
        studentsDb.createEntity(newStudentData);

        gaeSimulation.loginUser(idOfNewStudent);

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
                + "?" + Const.ParamsNames.ERROR + "=false"
                + "&" + Const.ParamsNames.USER_ID + "=" + idOfNewStudent,
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals(Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED 
                + "?" + Const.ParamsNames.REGKEY + "=" + newStudentKey
                + "&" + Const.ParamsNames.NEXT_URL + "=" + Const.ActionURIs.STUDENT_PROFILE_PAGE, 
                ((StudentCourseJoinConfirmationPageData) pageResult.data).getConfirmUrl());
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
                + "&" + Const.ParamsNames.ERROR + "=false",
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        
        // delete the new student
        studentsDb.deleteStudentWithoutDocument(newStudentData.course, newStudentData.email);

    }

    private StudentCourseJoinAction getAction(String... params)
            throws Exception {
        return (StudentCourseJoinAction) (gaeSimulation.getActionObject(uri,
                params));
    }

}
