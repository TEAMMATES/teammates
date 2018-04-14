package teammates.test.cases.action;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.logic.api.Logic;
import teammates.logic.core.StudentsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorStudentRecordsPageAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorStudentRecordsPageData;

/**
 * SUT: {@link InstructorStudentRecordsPageAction}.
 */
public class InstructorStudentRecordsPageActionTest extends BaseActionTest {

    private final Logic logic = new Logic();

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor3OfCourse1");
        StudentAttributes student = typicalBundle.students.get("student2InCourse1");
        String instructorId = instructor.googleId;

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Invalid parameters");

        // no params
        verifyAssumptionFailure();

        // null courseId
        String[] invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };

        verifyAssumptionFailure(invalidParams);

        // null student email
        invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId
        };

        verifyAssumptionFailure(invalidParams);

        // student not in course
        String studentEmailOfStudent1InCourse2 = typicalBundle.students.get("student1InCourse2").email;
        invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, studentEmailOfStudent1InCourse2
        };

        RedirectResult redirect = getRedirectResult(getAction(invalidParams));

        AssertHelper.assertContains(Const.ActionURIs.INSTRUCTOR_HOME_PAGE,
                                    redirect.getDestinationWithParams());
        AssertHelper.assertContains(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_RECORDS,
                                    redirect.getStatusMessage());

        ______TS("Typical case: student has some records and has profile");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };

        InstructorStudentRecordsPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS, false, "idOfInstructor3"),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        InstructorStudentRecordsPageData actualData = (InstructorStudentRecordsPageData) r.data;
        StudentProfileAttributes expectedProfile = StudentProfileAttributes.builder(student.googleId).build();
        expectedProfile.modifiedDate = actualData.spa.modifiedDate;
        expectedProfile.pictureKey = actualData.spa.pictureKey;

        assertEquals(instructorId, actualData.account.googleId);
        assertEquals(instructor.courseId, actualData.getCourseId());
        assertEquals(6, actualData.getSessionNames().size());
        assertEquals(student.googleId, actualData.spa.googleId);

        String expectedLogMessage = "TEAMMATESLOG|||instructorStudentRecordsPage|||instructorStudentRecordsPage"
                                  + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                                  + "|||instr3@course1n2.tmt|||instructorStudentRecords Page Load<br>"
                                  + "Viewing <span class=\"bold\">" + student.email + "'s</span> records "
                                  + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                                  + "Number of sessions: 6<br>"
                                  + "Student Profile: " + SanitizationHelper.sanitizeForHtmlTag(expectedProfile.toString())
                                  + "|||/page/instructorStudentRecordsPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Typical case: instructor cannot view sections");

        instructor = typicalBundle.instructors.get("helperOfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };

        a = getAction(submissionParams);
        r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS, false, "idOfHelperOfCourse1"),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("Normally, we would show the student’s profile here. "
                         + "However, you do not have access to view this student's profile<br>"
                         + "No records were found for this student",
                     r.getStatusMessage());

        ______TS("Typical case: student has no records, no profiles");

        String instructor4Id = typicalBundle.instructors.get("instructor4").googleId;
        // re-login as another instructor for new test
        gaeSimulation.loginAsInstructor(instructor4Id);
        String courseIdWithNoSession = "idOfCourseNoEvals";

        StudentAttributes testStudent = createStudentInTypicalDataBundleForCourseWithNoSession();

        String[] submissionParamsWithNoSession = new String[] {
                Const.ParamsNames.COURSE_ID, courseIdWithNoSession,
                Const.ParamsNames.STUDENT_EMAIL, "emailTemp@gmail.tmt"
        };

        InstructorStudentRecordsPageAction aWithNoSession = getAction(submissionParamsWithNoSession);
        ShowPageResult rWithNoSession = getShowPageResult(aWithNoSession);
        List<String> expectedMessages = new ArrayList<>();
        expectedMessages.add("No records were found for this student");
        expectedMessages.add(Const.StatusMessages.STUDENT_NOT_JOINED_YET_FOR_RECORDS);
        AssertHelper.assertContains(expectedMessages, rWithNoSession.getStatusMessage());

        ______TS("Typical case: student has profile but no records");

        testStudent.googleId = "valid.no.sessions";
        StudentsLogic.inst().updateStudentCascadeWithoutDocument(testStudent.email, testStudent);
        logic.createAccount(testStudent.googleId, testStudent.name, false, testStudent.email,
                            "valid institute");

        a = getAction(submissionParamsWithNoSession);
        r = getShowPageResult(a);

        AssertHelper.assertContains("No records were found for this student", r.getStatusMessage());

        ______TS("Typical case: student has profile with script injection");

        instructor = typicalBundle.instructors.get("instructor1OfTestingSanitizationCourse");
        instructorId = instructor.googleId;
        String studentId = "student1InTestingSanitizationCourse";
        student = typicalBundle.students.get(studentId);
        expectedProfile = typicalBundle.accounts.get(studentId).studentProfile;

        gaeSimulation.loginAsInstructor(instructorId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };

        a = getAction(submissionParams);
        r = getShowPageResult(a);
        actualData = (InstructorStudentRecordsPageData) r.data;
        expectedProfile.modifiedDate = actualData.spa.modifiedDate;

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS, false, instructorId),
                r.getDestinationWithParams());
        assertFalse(r.isError);

        expectedLogMessage = "TEAMMATESLOG|||instructorStudentRecordsPage|||instructorStudentRecordsPage"
                + "|||true|||Instructor|||Instructor&lt;script&gt; alert(&#39;hi!&#39;); &lt;&#x2f;script&gt;"
                + "|||" + instructorId
                + "|||instructor1@sanitization.tmt|||instructorStudentRecords Page Load<br>"
                + "Viewing <span class=\"bold\">" + student.email + "'s</span> records "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "Number of sessions: 1<br>"
                + "Student Profile: " + SanitizationHelper.sanitizeForHtmlTag(expectedProfile.toString())
                + "|||/page/instructorStudentRecordsPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
    }

    private StudentAttributes createStudentInTypicalDataBundleForCourseWithNoSession()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        StudentAttributes student = StudentAttributes
                .builder("idOfCourseNoEvals", "nameOfStudent", "emailTemp@gmail.tmt")
                .withSection("section")
                .withTeam("team")
                .withComments("No comment")
                .build();

        logic.createStudentWithoutDocument(student);
        return student;
    }

    @Override
    protected InstructorStudentRecordsPageAction getAction(String... params) {
        return (InstructorStudentRecordsPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
