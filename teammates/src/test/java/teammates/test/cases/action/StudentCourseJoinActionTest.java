package teammates.test.cases.action;

import static teammates.ui.controller.StudentCourseJoinAction.getPageTypeOfUrl;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.StudentsDb;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentCourseJoinAction;
import teammates.ui.pagedata.StudentCourseJoinConfirmationPageData;

/**
 * SUT: {@link StudentCourseJoinAction}.
 */
public class StudentCourseJoinActionTest extends BaseActionTest {

    @BeforeClass
    public void classSetup() throws Exception {
        addUnregStudentToCourse1();
    }

    @AfterClass
    public void classTearDown() {
        StudentsLogic.inst().deleteStudentCascade("idOfTypicalCourse1", "student6InCourse1@gmail.tmt");
    }

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.STUDENT_COURSE_JOIN_NEW;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {

        StudentCourseJoinAction joinAction;
        RedirectResult redirectResult;
        String[] submissionParams;

        StudentAttributes student1InCourse1 = typicalBundle.students
                .get("student1InCourse1");
        StudentsDb studentsDb = new StudentsDb();
        student1InCourse1 = studentsDb.getStudentForGoogleId(
                student1InCourse1.course, student1InCourse1.googleId);

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        ______TS("not enough parameters");

        verifyAssumptionFailure();

        ______TS("typical case");

        String idOfNewStudent = "idOfNewStudent";
        StudentAttributes newStudentData = StudentAttributes
                .builder(student1InCourse1.course, "nameOfNewStudent", "newStudent@course1.com")
                .withSection(student1InCourse1.section)
                .withTeam(student1InCourse1.team)
                .withComments("This is a new student")
                .build();

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

        assertEquals(
                getPageResultDestination(Const.ViewURIs.STUDENT_COURSE_JOIN_CONFIRMATION, false, idOfNewStudent),
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED,
                        newStudentKey,
                        Const.ActionURIs.STUDENT_PROFILE_PAGE),
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

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED,
                        newStudentKey,
                        Const.ActionURIs.STUDENT_PROFILE_PAGE.replace("/", "%2F"),
                        false),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);

        // delete the new student
        studentsDb.deleteStudentWithoutDocument(newStudentData.course, newStudentData.email);

        ______TS("Non-existent student attempting to join course displays error");

        gaeSimulation.loginUser(idOfNewStudent);
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, newStudentKey,
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_PROFILE_PAGE,
                Const.ParamsNames.STUDENT_EMAIL, newStudentData.email,
                Const.ParamsNames.COURSE_ID, newStudentData.course
        };
        joinAction = getAction(submissionParams);
        redirectResult = getRedirectResult(joinAction);

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE, redirectResult.destination);
        assertEquals(
                String.format(Const.StatusMessages.NON_EXISTENT_STUDENT_ATTEMPTING_TO_JOIN_COURSE, newStudentData.course),
                redirectResult.getStatusMessage());
        assertEquals("warning", redirectResult.getStatusMessageColor());
        assertTrue(redirectResult.isError);
    }

    @Test
    public void testGetUrlType() {
        assertEquals("/page/somePage", getPageTypeOfUrl("/page/somePage"));
        assertEquals("/page/somePage", getPageTypeOfUrl("/page/somePage?key=abcdef"));
        // captures the nearest ?
        assertEquals("/page/somePage", getPageTypeOfUrl("/page/somePage?key=abcdef&next=/page/anotherPage?"));
        // the starting keyword /page/ must be strictly followed
        assertEquals("/pag/somePage?key=abcdef", getPageTypeOfUrl("/pag/somePage?key=abcdef"));
        assertEquals("page/somePage?key=abcdef", getPageTypeOfUrl("page/somePage?key=abcdef"));
        // and must strictly be at the start of the string
        assertEquals("abc.com/page/somePage?key=abcdef", getPageTypeOfUrl("abc.com/page/somePage?key=abcdef"));
        // non-alphabet characters are not handled
        assertEquals("/page/somePage123?key=abcdef", getPageTypeOfUrl("/page/somePage123?key=abcdef"));
        assertEquals("/page/somePage/somePage?key=abcdef", getPageTypeOfUrl("/page/somePage/somePage?key=abcdef"));
    }

    @Override
    protected StudentCourseJoinAction getAction(String... params) {
        return (StudentCourseJoinAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    protected String getPageResultDestination(String parentUri, String regKey, String nextUrl, boolean isError) {
        String pageDestination = parentUri;
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.REGKEY, regKey);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.NEXT_URL, nextUrl);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.ERROR, Boolean.toString(isError));
        return pageDestination;
    }

    protected String getPageResultDestination(String parentUri, String regKey, String nextUrl) {
        String pageDestination = parentUri;
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.REGKEY, regKey);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.NEXT_URL, nextUrl);
        return pageDestination;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.courses.get("typicalCourse1").getId()
        };
        verifyAccessibleWithoutLogin(submissionParams);

        StudentAttributes unregStudent1 = typicalBundle.students.get("student1InUnregisteredCourse");
        String key = StudentsLogic.inst().getStudentForEmail(unregStudent1.course, unregStudent1.email).key;
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(key),
                Const.ParamsNames.COURSE_ID, unregStudent1.course,
                Const.ParamsNames.STUDENT_EMAIL, unregStudent1.email
        };
        verifyAccessibleForUnregisteredUsers(submissionParams);
        verifyAccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
}
