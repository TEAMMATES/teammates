package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCoursesPageAction;
import teammates.ui.controller.InstructorCoursesPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorCoursesPageActionTest extends BaseActionTest {

    /* Explanation: The parent class has method for @BeforeTest and @AfterTest
     */
    
    /* Explanation: we obtain an object, containing the typical data,
     * to be used as a quick access to the values that are expected to be
     * found in the database. We specify final so that multiple tests, if any,
     * can use these values without fear of dependency caused by modification */
    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public void classSetup() {
        
        /* Explanation: This is just to display the test class name in the console */
        printTestClassHeader();
        
        /* Explanation: we set the Action URI once as a static variable, to avoid passing
         * it as a parameter multiple times. This is for convenience. Any other
         * test code can pick up the URI from this variable.
         */
        uri = Const.ActionURIs.INSTRUCTOR_COURSES_PAGE;
        /* Explanation: Before every test-class, we put a standard set of test data into the
         * simulated GAE datastore. A replica of this can be found in the 'dataBundle' variable
         * declared above
         */
        removeAndRestoreTypicalDataBundle();
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        //TODO: find a way to test status message from session
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        
        String[] submissionParams = new String[]{Const.ParamsNames.IS_USING_AJAX, "true"};
        
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        
        /* Explanation: If the action is supposed to verify parameters,
         * we should check here the correctness of parameter verification.
         * e.g.
         
             ______TS("Invalid parameters");
            //both parameters missing.
            verifyAssumptionFailure(new String[]{});
            
            //null student email, only course ID is set
            String[] invalidParams = new String[]{
                    Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
            };
            verifyAssumptionFailure(invalidParams);
         
         * In this action, there is no parameter verification.
         */
        
        ______TS("Typical case, 2 courses");
        if (CoursesLogic.inst().isCoursePresent("new-course")) {
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }
        
        CoursesLogic.inst().createCourseAndInstructor(instructorId, "new-course", "New course", "UTC");
        gaeSimulation.loginAsInstructor(instructorId);
        InstructorCoursesPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSES + "?error=false&user=idOfInstructor1OfCourse1",
                     r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());
        
        InstructorCoursesPageData pageData = (InstructorCoursesPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(2, pageData.getActiveCourses().getRows().size() + pageData.getArchivedCourses().getRows().size());
        assertEquals(0, pageData.getArchivedCourses().getRows().size());
        assertEquals("", pageData.getCourseIdToShow());
        assertEquals("", pageData.getCourseNameToShow());
        
        String expectedLogMessage = "TEAMMATESLOG|||instructorCoursesPage|||instructorCoursesPage"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1"
                + "|||instr1@course1.tmt|||instructorCourse Page Load<br>Total courses: 2"
                + "|||/page/instructorCoursesPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Masquerade mode, 0 courses");
        
        CoursesLogic.inst().deleteCourseCascade(instructor1ofCourse1.courseId);
        CoursesLogic.inst().deleteCourseCascade("new-course");
        gaeSimulation.loginAsAdmin("admin.user");
        a = getAction(addUserIdToParams(instructorId, submissionParams));
        r = getShowPageResult(a);
        
        assertEquals(
                Const.ViewURIs.INSTRUCTOR_COURSES + "?error=false&user=idOfInstructor1OfCourse1",
                r.getDestinationWithParams());
        assertEquals("You have not created any courses yet. Use the form above to create a course.", r.getStatusMessage());
        assertFalse(r.isError);
        
        pageData = (InstructorCoursesPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(0, pageData.getArchivedCourses().getRows().size());
        assertEquals(0, pageData.getActiveCourses().getRows().size());
        assertEquals("", pageData.getCourseIdToShow());
        assertEquals("", pageData.getCourseNameToShow());
        
        expectedLogMessage = "TEAMMATESLOG|||instructorCoursesPage|||instructorCoursesPage"
                + "|||true|||Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1"
                + "|||instr1@course1.tmt|||instructorCourse Page Load<br>Total courses: 0"
                + "|||/page/instructorCoursesPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
    }

    private InstructorCoursesPageAction getAction(String... params) {
        return (InstructorCoursesPageAction) gaeSimulation.getActionObject(uri, params);
    }
    
}
