package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.logic.api.Logic;
import teammates.logic.core.CoursesLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorHomePageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorHomePageData;

/**
 * SUT: {@link InstructorHomePageAction}.
 */
public class InstructorHomePageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "something"
        };

        ______TS("persistence issue");

        gaeSimulation.loginUser("unreg_user");
        InstructorHomePageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);
        assertFalse(a.account.isInstructor);
        assertEquals(Const.StatusMessages.INSTRUCTOR_PERSISTENCE_ISSUE, r.getStatusMessage());

        ______TS("instructor with no courses, right after registration (ie no persistence issue)");

        gaeSimulation.loginAsInstructor(typicalBundle.accounts.get("instructorWithoutCourses").googleId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);
        AssertHelper.assertContainsRegex(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_HOME, false, "instructorWithoutCourses"),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.HINT_FOR_NEW_INSTRUCTOR, r.getStatusMessage());

        InstructorHomePageData data = (InstructorHomePageData) r.data;
        assertEquals(0, data.getCourseTables().size());

        String expectedLogMessage = "TEAMMATESLOG|||instructorHomePage|||instructorHomePage"
                                     + "|||true|||Instructor|||Instructor Without Courses"
                                     + "|||instructorWithoutCourses|||iwc@yahoo.tmt"
                                     + "|||instructorHome Page Load<br>Total Courses: 0"
                                     + "|||/page/instructorHomePage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        submissionParams = new String[] {};

        ______TS("instructor with multiple courses, sort by course id, masquerade mode");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_SORTING_CRITERIA, Const.SORT_BY_COURSE_ID
        };
        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        //access page in masquerade mode
        String instructorWithMultipleCourses = typicalBundle.accounts.get("instructor3").googleId;

        //create another course for sorting
        Logic logic = new Logic();
        String newCourseIdForSorting = "idOfTypicalCourse"; // should be 1st if sort by course id
        String newCourseNameForSorting = "Typical Course 3"; //should be 3rd if sort by course name
        logic.createCourseAndInstructor(instructorWithMultipleCourses, newCourseIdForSorting,
                                        newCourseNameForSorting, "UTC");

        a = getAction(addUserIdToParams(instructorWithMultipleCourses, submissionParams));
        r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_HOME, false, instructorWithMultipleCourses),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        data = (InstructorHomePageData) r.data;
        assertEquals(3, data.getCourseTables().size());
        String expectedCourse1IdAfterSortByCourseId = "idOfTypicalCourse";
        String expectedCourse2IdAfterSortByCourseId = "idOfTypicalCourse1";
        String expectedCourse3IdAfterSortByCourseId = "idOfTypicalCourse2";
        String actualCourse1AfterSortByCourseId = data.getCourseTables().get(0).getCourseId();
        String actualCourse2AfterSortByCourseId = data.getCourseTables().get(1).getCourseId();
        String actualCourse3AfterSortByCourseId = data.getCourseTables().get(2).getCourseId();
        assertEquals(expectedCourse1IdAfterSortByCourseId, actualCourse1AfterSortByCourseId);
        assertEquals(expectedCourse2IdAfterSortByCourseId, actualCourse2AfterSortByCourseId);
        assertEquals(expectedCourse3IdAfterSortByCourseId, actualCourse3AfterSortByCourseId);
        assertEquals(Const.SORT_BY_COURSE_ID, data.getSortCriteria());

        expectedLogMessage = "TEAMMATESLOG|||instructorHomePage|||instructorHomePage|||true"
                              + "|||Instructor(M)|||Instructor 3 of Course 1 and 2"
                              + "|||idOfInstructor3|||instr3@course1n2.tmt"
                              + "|||instructorHome Page Load<br>Total Courses: 3"
                              + "|||/page/instructorHomePage";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedLogMessage, a.getLogMessage(), adminUserId);

        ______TS("instructor with multiple courses, sort by course name, masquerade mode");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_SORTING_CRITERIA, Const.SORT_BY_COURSE_NAME
        };

        a = getAction(addUserIdToParams(instructorWithMultipleCourses, submissionParams));
        r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_HOME, false, instructorWithMultipleCourses),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        data = (InstructorHomePageData) r.data;
        assertEquals(3, data.getCourseTables().size());
        String expectedCourse1IdAfterSortByCourseName = "idOfTypicalCourse1";
        String expectedCourse2IdAfterSortByCourseName = "idOfTypicalCourse2";
        String expectedCourse3IdAfterSortByCourseName = "idOfTypicalCourse";
        String actualCourse1AfterSortByCourseName = data.getCourseTables().get(0).getCourseId();
        String actualCourse2AfterSortByCourseName = data.getCourseTables().get(1).getCourseId();
        String actualCourse3AfterSortByCourseName = data.getCourseTables().get(2).getCourseId();
        assertEquals(expectedCourse1IdAfterSortByCourseName, actualCourse1AfterSortByCourseName);
        assertEquals(expectedCourse2IdAfterSortByCourseName, actualCourse2AfterSortByCourseName);
        assertEquals(expectedCourse3IdAfterSortByCourseName, actualCourse3AfterSortByCourseName);
        assertEquals(Const.SORT_BY_COURSE_NAME, data.getSortCriteria());

        ______TS("instructor with multiple courses, sort by course name, masquerade mode");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_SORTING_CRITERIA, "haha"
        };

        try {
            a = getAction(addUserIdToParams(instructorWithMultipleCourses, submissionParams));
            r = getShowPageResult(a);
            signalFailureToDetectException("The Assertion error is not thrown as expected");
        } catch (AssertionError e) {
            assertNotNull(e);
        }

        ______TS("instructor with multiple courses, sort by creation date, masquerade mode");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_SORTING_CRITERIA, Const.SORT_BY_COURSE_CREATION_DATE
        };

        a = getAction(addUserIdToParams(instructorWithMultipleCourses, submissionParams));
        r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_HOME, false, instructorWithMultipleCourses),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        data = (InstructorHomePageData) r.data;
        assertEquals(3, data.getCourseTables().size());
        String expectedCourse1IdAfterSortByCourseCreationDate = "idOfTypicalCourse";
        String expectedCourse2IdAfterSortByCourseCreationDate = "idOfTypicalCourse2";
        String expectedCourse3IdAfterSortByCourseCreationDate = "idOfTypicalCourse1";
        String actualCourse1AfterSortByCourseCreationDate = data.getCourseTables().get(0).getCourseId();
        String actualCourse2AfterSortByCourseCreationDate = data.getCourseTables().get(1).getCourseId();
        String actualCourse3AfterSortByCourseCreationDate = data.getCourseTables().get(2).getCourseId();
        assertEquals(expectedCourse1IdAfterSortByCourseCreationDate, actualCourse1AfterSortByCourseCreationDate);
        assertEquals(expectedCourse2IdAfterSortByCourseCreationDate, actualCourse2AfterSortByCourseCreationDate);
        assertEquals(expectedCourse3IdAfterSortByCourseCreationDate, actualCourse3AfterSortByCourseCreationDate);
        assertEquals(Const.SORT_BY_COURSE_CREATION_DATE, data.getSortCriteria());

        // delete the new course
        CoursesLogic.inst().deleteCourseCascade(newCourseIdForSorting);
    }

    @Override
    protected InstructorHomePageAction getAction(String... params) {
        return (InstructorHomePageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyOnlyInstructorsCanAccess(submissionParams);

        // check for persistence issue
        submissionParams = new String[] {
                Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "random_course"
        };

        verifyOnlyLoggedInUsersCanAccess(submissionParams);
    }

}
