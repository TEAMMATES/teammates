package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorCourseDetailsPageAction;
import teammates.ui.controller.InstructorCourseDetailsPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorCourseDetailsPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor1OfCourse1.googleId);
        
        ______TS("Not enough parameters");
        verifyAssumptionFailure();

        ______TS("Typical Case, Course with at least one student");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
        };
        InstructorCourseDetailsPageAction pageAction = getAction(submissionParams);
        ShowPageResult pageResult = getShowPageResult(pageAction);

        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_DETAILS + "?error=false&user=idOfInstructor1OfCourse1",
                     pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        InstructorCourseDetailsPageData pageData = (InstructorCourseDetailsPageData) pageResult.data;
        assertEquals(5, pageData.getInstructors().size());

        assertEquals("idOfTypicalCourse1", pageData.getCourseDetails().course.getId());
        assertEquals("Typical Course 1 with 2 Evals", pageData.getCourseDetails().course.getName());
        assertEquals(2, pageData.getCourseDetails().stats.teamsTotal);
        assertEquals(5, pageData.getCourseDetails().stats.studentsTotal);
        assertEquals(0, pageData.getCourseDetails().stats.unregisteredTotal);
        assertEquals(0, pageData.getCourseDetails().feedbackSessions.size());

        String expectedLogMessage = "TEAMMATESLOG|||instructorCourseDetailsPage|||instructorCourseDetailsPage|||true"
                                    + "|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1"
                                    + "|||instr1@course1.tmt|||instructorCourseDetails Page Load<br>Viewing Course "
                                    + "Details for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                                    + "|||/page/instructorCourseDetailsPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, pageAction.getLogMessage());
        
        ______TS("Masquerade mode, Course with no student");
        gaeSimulation.loginAsAdmin("admin.user");
        InstructorAttributes instructor4 = dataBundle.instructors.get("instructor4");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor4.courseId
        };
        pageAction = getAction(addUserIdToParams(instructor4.googleId, submissionParams));
        pageResult = getShowPageResult(pageAction);

        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_DETAILS + "?error=false&user=idOfInstructor4",
                     pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals(String.format(Const.StatusMessages.INSTRUCTOR_COURSE_EMPTY,
                                   pageResult.data.getInstructorCourseEnrollLink(instructor4.courseId)),
                     pageResult.getStatusMessage());
        
        pageData = (InstructorCourseDetailsPageData) pageResult.data;
        assertEquals(1, pageData.getInstructors().size());

        assertEquals("idOfCourseNoEvals", pageData.getCourseDetails().course.getId());
        assertEquals("Typical Course 3 with 0 Evals", pageData.getCourseDetails().course.getName());
        assertEquals(0, pageData.getCourseDetails().stats.teamsTotal);
        assertEquals(0, pageData.getCourseDetails().stats.studentsTotal);
        assertEquals(0, pageData.getCourseDetails().stats.unregisteredTotal);
        assertEquals(0, pageData.getCourseDetails().feedbackSessions.size());

        expectedLogMessage = "TEAMMATESLOG|||instructorCourseDetailsPage|||instructorCourseDetailsPage|||true|||"
                             + "Instructor(M)|||Instructor 4 of CourseNoEvals|||idOfInstructor4|||"
                             + "instr4@coursenoevals.tmt|||instructorCourseDetails Page Load<br>Viewing Course "
                             + "Details for Course <span class=\"bold\">[idOfCourseNoEvals]</span>|||"
                             + "/page/instructorCourseDetailsPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, pageAction.getLogMessage());
        
        ______TS("HTML Table needed");
        instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor1OfCourse1.googleId);
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED, "true"
        };
        pageAction = getAction(submissionParams);
        AjaxResult ajaxResult = this.getAjaxResult(pageAction);

        assertEquals("?error=false&user=idOfInstructor1OfCourse1", ajaxResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", ajaxResult.getStatusMessage());
        
        pageData = (InstructorCourseDetailsPageData) ajaxResult.data;

        assertEquals("<table class=\"table table-bordered table-striped table-condensed\">\n"
                         + "<tr>"
                             + "<td>Course ID</td>\n"
                             + "<td>idOfTypicalCourse1</td>\n"
                         + "</tr>"
                         + "<tr>"
                             + "<td>Course Name</td>\n"
                             + "<td>Typical Course 1 with 2 Evals</td>\n"
                         + "</tr>"
                         + "<tr>"
                             + "<td>Section</td>\n"
                             + "<td>Team</td>\n"
                             + "<td>Full Name</td>\n"
                             + "<td>Last Name</td>\n"
                             + "<td>Status</td>\n"
                             + "<td>Email</td>\n"
                         + "</tr>"
                         + "<tr>"
                             + "<td>Section 1</td>\n"
                             + "<td>Team 1.1&lt;&#x2f;td&gt;&lt;&#x2f;div&gt;&#39;&quot;</td>\n"
                             + "<td>student1 In Course1&lt;&#x2f;td&gt;&lt;&#x2f;div&gt;&#39;&quot;</td>\n"
                             + "<td>Course1&lt;&#x2f;td&gt;&lt;&#x2f;div&gt;&#39;&quot;</td>\n"
                             + "<td>Joined</td>\n"
                             + "<td>student1InCourse1@gmail.tmt</td>\n"
                         + "</tr>"
                         + "<tr>"
                             + "<td>Section 1</td>\n"
                             + "<td>Team 1.1&lt;&#x2f;td&gt;&lt;&#x2f;div&gt;&#39;&quot;</td>\n"
                             + "<td>student2 In Course1</td>\n"
                             + "<td>Course1</td>\n"
                             + "<td>Joined</td>\n"
                             + "<td>student2InCourse1@gmail.tmt</td>\n"
                         + "</tr>"
                         + "<tr>"
                             + "<td>Section 1</td>\n"
                             + "<td>Team 1.1&lt;&#x2f;td&gt;&lt;&#x2f;div&gt;&#39;&quot;</td>\n"
                             + "<td>student3 In Course1</td>\n"
                             + "<td>Course1</td>\n"
                             + "<td>Joined</td>\n"
                             + "<td>student3InCourse1@gmail.tmt</td>\n"
                         + "</tr>"
                         + "<tr>"
                             + "<td>Section 1</td>\n"
                             + "<td>Team 1.1&lt;&#x2f;td&gt;&lt;&#x2f;div&gt;&#39;&quot;</td>\n"
                             + "<td>student4 In Course1</td>\n"
                             + "<td>Course1</td>\n"
                             + "<td>Joined</td>\n"
                             + "<td>student4InCourse1@gmail.tmt</td>\n"
                         + "</tr>"
                         + "<tr>"
                             + "<td>Section 2</td>\n"
                             + "<td>Team 1.2</td>\n"
                             + "<td>student5 In Course1</td>\n"
                             + "<td>Course1</td>\n"
                             + "<td>Joined</td>\n"
                             + "<td>student5InCourse1@gmail.tmt</td>\n"
                         + "</tr>"
                     + "</table>",
                     pageData.getStudentListHtmlTableAsString());
    }

    private InstructorCourseDetailsPageAction getAction(String... params) {
        return (InstructorCourseDetailsPageAction) gaeSimulation.getActionObject(uri, params);
    }
}
