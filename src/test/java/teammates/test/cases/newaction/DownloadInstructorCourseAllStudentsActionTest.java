package teammates.test.cases.newaction;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.core.StudentsLogic;
import teammates.test.driver.CsvChecker;
import teammates.ui.newcontroller.CsvResult;
import teammates.ui.newcontroller.DownloadInstructorCourseAllStudentsAction;

/**
 * SUT: {@link DownloadInstructorCourseAllStudentsAction}.
 */
public class DownloadInstructorCourseAllStudentsActionTest extends
        BaseActionTest<DownloadInstructorCourseAllStudentsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_COURSE_DETAILS_DOWNLOAD_ALL_STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        String instructorId = typicalBundle.instructors.get("instructor1OfCourse1").googleId;
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Invalid params");
        String[] submissionParams = {};
        verifyHttpParameterFailure(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId()
        };

        ______TS("Typical case: student list downloaded successfully");
        DownloadInstructorCourseAllStudentsAction downloadAction = getAction(submissionParams);
        CsvResult result = getDownloadFileResult(downloadAction);

        // TODO: fileName and status message moved to front end for future testing
        String fileContent = result.getFileContent();
        CsvChecker.verifyCsvContent(fileContent, "/courseStudentList_actionTest.csv");

        ______TS("Typical case: student list downloaded successfully with student last name specified within braces");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        student1InCourse1.name = "new name {new last name}";
        StudentsLogic.inst().updateStudentCascade(student1InCourse1.email, student1InCourse1);

        downloadAction = getAction(submissionParams);
        result = getDownloadFileResult(downloadAction);

        fileContent = result.getFileContent();

        CsvChecker.verifyCsvContent(fileContent, "/courseStudentListStudentLastName_actionTest.csv");

        removeAndRestoreTypicalDataBundle();

        ______TS("Typical case: student list downloaded successfully with special team name");

        student1InCourse1 = StudentsLogic.inst().getStudentForEmail("idOfTypicalCourse1", "student1InCourse1@gmail.tmt");
        student1InCourse1.team = "N/A";
        StudentsLogic.inst().updateStudentCascade("student1InCourse1@gmail.tmt", student1InCourse1);

        downloadAction = getAction(submissionParams);
        result = getDownloadFileResult(downloadAction);

        fileContent = result.getFileContent();

        CsvChecker.verifyCsvContent(fileContent, "/courseStudentListSpecialTeamName_actionTest.csv");

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
