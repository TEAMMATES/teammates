package teammates.test.cases.action;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.core.StudentsLogic;
import teammates.ui.controller.FileDownloadResult;
import teammates.ui.controller.InstructorCourseStudentListDownloadAction;

/**
 * SUT: {@link InstructorCourseStudentListDownloadAction}.
 */
public class InstructorCourseStudentListDownloadActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_LIST_DOWNLOAD;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        String instructorId = typicalBundle.instructors.get("instructor1OfCourse1").googleId;
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Invalid params");
        String[] submissionParams = {};
        verifyAssumptionFailure(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId()
        };

        ______TS("Typical case: student list downloaded successfully");
        InstructorCourseStudentListDownloadAction a = getAction(submissionParams);
        FileDownloadResult r = getFileDownloadResult(a);

        String expectedFileName = "idOfTypicalCourse1_studentList";
        assertEquals(expectedFileName, r.getFileName());
        // look at LogicTest.testGetCourseStudentListAsCsv. the logic api to generate Csv file content is tested in LogicTest
        String fileContent = r.getFileContent();

        String[] expected = {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course ID," + "\"" + course.getId() + "\"",
                "Course Name," + "\"" + course.getName() + "\"",
                "",
                "",
                "Section,Team,Full Name,Last Name,Status,Email",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"Joined\",\"student1InCourse1@gmail.tmt\"",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"Joined\",\"student2InCourse1@gmail.tmt\"",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"Joined\",\"student3InCourse1@gmail.tmt\"",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"Joined\",\"student4InCourse1@gmail.tmt\"",
                "\"Section 2\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"Joined\",\"student5InCourse1@gmail.tmt\"",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, System.lineSeparator()), fileContent);
        assertEquals("", r.getStatusMessage());

        ______TS("Typical case: student list downloaded successfully with student last name specified within braces");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        student1InCourse1.name = "new name {new last name}";
        StudentsLogic.inst().updateStudentCascade(student1InCourse1.email, student1InCourse1);

        a = getAction(submissionParams);
        r = getFileDownloadResult(a);

        expectedFileName = "idOfTypicalCourse1_studentList";
        assertEquals(expectedFileName, r.getFileName());
        // look at LogicTest.testGetCourseStudentListAsCsv. the logic api to generate Csv file content is tested in LogicTest
        fileContent = r.getFileContent();

        expected = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course ID," + "\"" + course.getId() + "\"",
                "Course Name," + "\"" + course.getName() + "\"",
                "",
                "",
                "Section,Team,Full Name,Last Name,Status,Email",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"new name new last name\",\"new last name\",\"Joined\",\"student1InCourse1@gmail.tmt\"",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"Joined\",\"student2InCourse1@gmail.tmt\"",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"Joined\",\"student3InCourse1@gmail.tmt\"",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"Joined\",\"student4InCourse1@gmail.tmt\"",
                "\"Section 2\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"Joined\",\"student5InCourse1@gmail.tmt\"",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, System.lineSeparator()), fileContent);
        assertEquals("", r.getStatusMessage());

        removeAndRestoreTypicalDataBundle();

        ______TS("Typical case: student list downloaded successfully with special team name");

        student1InCourse1 = StudentsLogic.inst().getStudentForEmail("idOfTypicalCourse1", "student1InCourse1@gmail.tmt");
        student1InCourse1.team = "N/A";
        StudentsLogic.inst().updateStudentCascade("student1InCourse1@gmail.tmt", student1InCourse1);

        a = getAction(submissionParams);
        r = getFileDownloadResult(a);

        expectedFileName = "idOfTypicalCourse1_studentList";
        assertEquals(expectedFileName, r.getFileName());
        // look at LogicTest.testGetCourseStudentListAsCsv. the logic api to generate Csv file content is tested in LogicTest
        fileContent = r.getFileContent();

        expected = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course ID," + "\"" + course.getId() + "\"",
                "Course Name," + "\"" + course.getName() + "\"",
                "",
                "",
                "Section,Team,Full Name,Last Name,Status,Email",
                "\"Section 1\",\"N/A\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"Joined\",\"student1InCourse1@gmail.tmt\"",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"Joined\",\"student2InCourse1@gmail.tmt\"",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"Joined\",\"student3InCourse1@gmail.tmt\"",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"Joined\",\"student4InCourse1@gmail.tmt\"",
                "\"Section 2\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"Joined\",\"student5InCourse1@gmail.tmt\"",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, System.lineSeparator()), fileContent);
        assertEquals("", r.getStatusMessage());

    }

    @Override
    protected InstructorCourseStudentListDownloadAction getAction(String... params) {
        return (InstructorCourseStudentListDownloadAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId()
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
