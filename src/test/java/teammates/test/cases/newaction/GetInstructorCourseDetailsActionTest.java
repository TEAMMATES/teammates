package teammates.test.cases.newaction;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.newcontroller.GetInstructorCourseDetailsAction;
import teammates.ui.newcontroller.GetInstructorCourseDetailsAction.CourseInfo;
import teammates.ui.newcontroller.JsonResult;

/**
 * SUT: {@link GetInstructorCourseDetailsAction}.
 */
public class GetInstructorCourseDetailsActionTest extends BaseActionTest<GetInstructorCourseDetailsAction>  {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_COURSE_DETAILS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("Not enough parameters");
        verifyHttpParameterFailure();

        ______TS("Typical Case, Course with at least one student and HTML table");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
        };

        GetInstructorCourseDetailsAction pageAction = getAction(submissionParams);
        JsonResult result = getJsonResult(pageAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CourseInfo output = (CourseInfo) result.getOutput();
        assertEquals(5, output.getInstructors().size());

        assertEquals("idOfTypicalCourse1", output.getCourseDetails().course.getId());
        assertEquals("Typical Course 1 with 2 Evals", output.getCourseDetails().course.getName());
        assertEquals(2, output.getCourseDetails().stats.teamsTotal);
        assertEquals(5, output.getCourseDetails().stats.studentsTotal);
        assertEquals(0, output.getCourseDetails().stats.unregisteredTotal);
        assertEquals(0, output.getCourseDetails().feedbackSessions.size());

        assertEquals("<table class=\"table table-bordered table-striped table-condensed\">"
                        + "<tr>"
                        + "<td>Course ID</td>"
                        + "<td>idOfTypicalCourse1</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td>Course Name</td>"
                        + "<td>Typical Course 1 with 2 Evals</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td>Section</td>"
                        + "<td>Team</td>"
                        + "<td>Full Name</td>"
                        + "<td>Last Name</td>"
                        + "<td>Status</td>"
                        + "<td>Email</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td>Section 1</td>"
                        + "<td>Team 1.1&lt;&#x2f;td&gt;&lt;&#x2f;div&gt;&#39;&quot;</td>"
                        + "<td>student1 In Course1&lt;&#x2f;td&gt;&lt;&#x2f;div&gt;&#39;&quot;</td>"
                        + "<td>Course1&lt;&#x2f;td&gt;&lt;&#x2f;div&gt;&#39;&quot;</td>"
                        + "<td>Joined</td>"
                        + "<td>student1InCourse1@gmail.tmt</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td>Section 1</td>"
                        + "<td>Team 1.1&lt;&#x2f;td&gt;&lt;&#x2f;div&gt;&#39;&quot;</td>"
                        + "<td>student2 In Course1</td>"
                        + "<td>Course1</td>"
                        + "<td>Joined</td>"
                        + "<td>student2InCourse1@gmail.tmt</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td>Section 1</td>"
                        + "<td>Team 1.1&lt;&#x2f;td&gt;&lt;&#x2f;div&gt;&#39;&quot;</td>"
                        + "<td>student3 In Course1</td>"
                        + "<td>Course1</td>"
                        + "<td>Joined</td>"
                        + "<td>student3InCourse1@gmail.tmt</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td>Section 1</td>"
                        + "<td>Team 1.1&lt;&#x2f;td&gt;&lt;&#x2f;div&gt;&#39;&quot;</td>"
                        + "<td>student4 In Course1</td>"
                        + "<td>Course1</td>"
                        + "<td>Joined</td>"
                        + "<td>student4InCourse1@gmail.tmt</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td>Section 2</td>"
                        + "<td>Team 1.2</td>"
                        + "<td>student5 In Course1</td>"
                        + "<td>Course1</td>"
                        + "<td>Joined</td>"
                        + "<td>student5InCourse1@gmail.tmt</td>"
                        + "</tr>"
                        + "</table>",
                output.getStudentListHtmlTableAsString());

        ______TS("Masquerade mode, Course with no student");

        loginAsAdmin();

        InstructorAttributes instructor4 = typicalBundle.instructors.get("instructor4");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor4.courseId
        };
        pageAction = getAction(addUserIdToParams(instructor4.googleId, submissionParams));
        result = getJsonResult(pageAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        output = (CourseInfo) result.getOutput();
        assertEquals(1, output.getInstructors().size());

        assertEquals("idOfCourseNoEvals", output.getCourseDetails().course.getId());
        assertEquals("Typical Course 3 with 0 Evals", output.getCourseDetails().course.getName());
        assertEquals(0, output.getCourseDetails().stats.teamsTotal);
        assertEquals(0, output.getCourseDetails().stats.studentsTotal);
        assertEquals(0, output.getCourseDetails().stats.unregisteredTotal);
        assertEquals(0, output.getCourseDetails().feedbackSessions.size());

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").courseId
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
