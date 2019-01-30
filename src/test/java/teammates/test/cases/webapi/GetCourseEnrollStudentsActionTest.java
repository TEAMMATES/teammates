package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetCourseEnrollStudentsAction;
import teammates.ui.webapi.action.GetCourseEnrollStudentsAction.StudentList;
import teammates.ui.webapi.action.JsonResult;

/**
 * SUT: {@link GetCourseEnrollStudentsAction}.
 */
public class GetCourseEnrollStudentsActionTest extends BaseActionTest<GetCourseEnrollStudentsAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_ENROLL_STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor.googleId;

        loginAsInstructor(instructorId);

        ______TS("Invalid parameters");
        //no parameters
        verifyHttpParameterFailure();

        ______TS("Typical successful case");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
        };
        GetCourseEnrollStudentsAction action = getAction(submissionParams);
        JsonResult jsonResult = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());

        String expectedString = "Section 1|Team 1.1</td></div>'\"|student1 In Course1</td></div>'\""
                + "|student1InCourse1@gmail.tmt|comment for student1InCourse1</td></div>'\""
                + "Section 1|Team 1.1</td></div>'\"|student2 In Course1|student2InCourse1@gmail.tmt|"
                + "Section 1|Team 1.1</td></div>'\"|student3 In Course1|student3InCourse1@gmail.tmt|"
                + "Section 1|Team 1.1</td></div>'\"|student4 In Course1|student4InCourse1@gmail.tmt|"
                + "Section 2|Team 1.2|student5 In Course1|student5InCourse1@gmail.tmt|";

        StudentList output = (StudentList) jsonResult.getOutput();
        StringBuilder resultString = new StringBuilder();

        for (StudentAttributes student : output.getEnrolledStudents()) {
            resultString.append(student.toEnrollmentString());
        }
        assertEquals(expectedString, resultString.toString());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
