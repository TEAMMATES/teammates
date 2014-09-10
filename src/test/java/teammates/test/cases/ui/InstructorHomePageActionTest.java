package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.logic.api.Logic;
import teammates.logic.core.CoursesLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorHomePageAction;
import teammates.ui.controller.InstructorHomePageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorHomePageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        String[] submissionParams = new String[]{};
        
        ______TS("instructor with no courses");
        
        gaeSimulation.loginAsInstructor(dataBundle.accounts.get("instructorWithoutCourses").googleId);
        InstructorHomePageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);
        AssertHelper.assertContainsRegex("/jsp/instructorHome.jsp?"
                + "error=false&user=instructorWithoutCourses", r.getDestinationWithParams());
        assertEquals(false, r.isError);
        assertEquals(Const.StatusMessages.HINT_FOR_NEW_INSTRUCTOR, r.getStatusMessage());
        
        InstructorHomePageData data = (InstructorHomePageData)r.data;
        assertEquals(0, data.courses.size());
        
        String expectedLogMessage = "TEAMMATESLOG|||instructorHomePage|||instructorHomePage|||true" +
                "|||Instructor|||Instructor Without Courses|||instructorWithoutCourses" +
                "|||iwc@yahoo.tmt|||instructorHome Page Load<br>Total Courses: 0|||/page/instructorHomePage" ;
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        
        ______TS("instructor with multiple courses, sort by course id, masquerade mode");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_SORTING_CRITERIA, Const.SORT_BY_COURSE_ID
        };
        gaeSimulation.loginAsAdmin("admin.user");
        
        //access page in masquerade mode
        String instructorWithMultipleCourses = dataBundle.accounts.get("instructor3").googleId;
        
        //create another course for sorting
        Logic logic = new Logic();
        String newCourseIdForSorting = "idOfTypicalCourse"; // should be 1st if sort by course id
        String newCourseNameForSorting = "Typical Course 3"; //should be 3rd if sort by course name 
        logic.createCourseAndInstructor(instructorWithMultipleCourses, newCourseIdForSorting, newCourseNameForSorting);
        
        a = getAction(addUserIdToParams(instructorWithMultipleCourses, submissionParams));
        r = getShowPageResult(a);
        
        assertEquals("/jsp/instructorHome.jsp?error=false&user="+instructorWithMultipleCourses, r.getDestinationWithParams());
        assertEquals(false, r.isError);
        assertEquals("",r.getStatusMessage());
        
        data = (InstructorHomePageData)r.data;
        assertEquals(3, data.courses.size());
        String expectedCourse1IdAfterSortByCourseId = "idOfTypicalCourse";
        String expectedCourse2IdAfterSortByCourseId = "idOfTypicalCourse1";
        String expectedCourse3IdAfterSortByCourseId = "idOfTypicalCourse2";
        CourseAttributes actualCourse1AfterSortByCourseId = data.courses.get(0).course;
        CourseAttributes actualCourse2AfterSortByCourseId = data.courses.get(1).course;
        CourseAttributes actualCourse3AfterSortByCourseId = data.courses.get(2).course;
        assertEquals(expectedCourse1IdAfterSortByCourseId, actualCourse1AfterSortByCourseId.id);
        assertEquals(expectedCourse2IdAfterSortByCourseId, actualCourse2AfterSortByCourseId.id);
        assertEquals(expectedCourse3IdAfterSortByCourseId, actualCourse3AfterSortByCourseId.id);
        assertEquals(Const.SORT_BY_COURSE_ID, data.sortCriteria);
        
        expectedLogMessage = "TEAMMATESLOG|||instructorHomePage|||instructorHomePage|||true" +
                "|||Instructor(M)|||Instructor 3 of Course 1 and 2|||idOfInstructor3" +
                "|||instr3@course1n2.tmt|||instructorHome Page Load<br>Total Courses: 3|||/page/instructorHomePage" ;
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        
        ______TS("instructor with multiple courses, sort by course name, masquerade mode");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_SORTING_CRITERIA, Const.SORT_BY_COURSE_NAME
        };
        
        a = getAction(addUserIdToParams(instructorWithMultipleCourses, submissionParams));
        r = getShowPageResult(a);
        
        assertEquals("/jsp/instructorHome.jsp?error=false&user="+instructorWithMultipleCourses, r.getDestinationWithParams());
        assertEquals(false, r.isError);
        assertEquals("",r.getStatusMessage());
        
        data = (InstructorHomePageData)r.data;
        assertEquals(3, data.courses.size());
        String expectedCourse1IdAfterSortByCourseName = "idOfTypicalCourse1";
        String expectedCourse2IdAfterSortByCourseName = "idOfTypicalCourse2";
        String expectedCourse3IdAfterSortByCourseName = "idOfTypicalCourse";
        CourseAttributes actualCourse1AfterSortByCourseName = data.courses.get(0).course;
        CourseAttributes actualCourse2AfterSortByCourseName = data.courses.get(1).course;
        CourseAttributes actualCourse3AfterSortByCourseName = data.courses.get(2).course;
        assertEquals(expectedCourse1IdAfterSortByCourseName, actualCourse1AfterSortByCourseName.id);
        assertEquals(expectedCourse2IdAfterSortByCourseName, actualCourse2AfterSortByCourseName.id);
        assertEquals(expectedCourse3IdAfterSortByCourseName, actualCourse3AfterSortByCourseName.id);
        assertEquals(Const.SORT_BY_COURSE_NAME, data.sortCriteria);
        
        
        ______TS("instructor with multiple courses, sort by course name, masquerade mode");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_SORTING_CRITERIA, "haha"
        };
        
        try {
            a = getAction(addUserIdToParams(instructorWithMultipleCourses, submissionParams));
            r = getShowPageResult(a);
            fail("The run time exception is not thrown as expected");
        } catch(RuntimeException e) {
            assertNotNull(e);
        }
        
        // delete the new course
        CoursesLogic.inst().deleteCourseCascade(newCourseIdForSorting);
    }
    
    private InstructorHomePageAction getAction(String... params) throws Exception{
            return (InstructorHomePageAction) (gaeSimulation.getActionObject(uri, params));
    }
    
}
