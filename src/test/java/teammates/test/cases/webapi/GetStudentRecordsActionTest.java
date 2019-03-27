package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.ui.webapi.action.GetStudentRecordsAction;
import teammates.ui.webapi.action.GetStudentRecordsAction.StudentRecords;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link GetStudentRecordsAction}.
 */
public class GetStudentRecordsActionTest extends BaseActionTest<GetStudentRecordsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_RECORDS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor3OfCourse1");
        StudentAttributes student = typicalBundle.students.get("student2InCourse1");
        String instructorId = instructor.googleId;

        loginAsInstructor(instructorId);

        ______TS("Invalid parameters");

        // no params
        verifyHttpParameterFailure();

        // null courseId
        String[] invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student.email,
        };

        verifyHttpParameterFailure(invalidParams);

        // null student email
        invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
        };

        verifyHttpParameterFailure(invalidParams);

        // student not in course
        String studentEmailOfStudent1InCourse2 = typicalBundle.students.get("student1InCourse2").email;
        invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, studentEmailOfStudent1InCourse2,
        };

        GetStudentRecordsAction a = getAction(invalidParams);
        JsonResult result = getJsonResult(a);

        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());
        MessageOutput invalidParamsOutput = (MessageOutput) result.getOutput();

        assertEquals("No student with given email in given course.",
                invalidParamsOutput.getMessage());

        ______TS("Typical case: student has some records and has no profile");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
        };

        a = getAction(submissionParams);
        result = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        StudentRecords output = (StudentRecords) result.getOutput();

        assertEquals(instructor.courseId, output.getCourseId());
        assertEquals(student.name, output.getStudentName());
        assertEquals(student.email, output.getStudentEmail());
        assertNull(output.getStudentProfile());
        assertEquals(6, output.getSessionNames().size());

        ______TS("Typical case: instructor cannot view sections");

        instructor = typicalBundle.instructors.get("helperOfCourse1");
        loginAsInstructor(instructor.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
        };

        a = getAction(submissionParams);
        result = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        output = (StudentRecords) result.getOutput();

        assertEquals(0, output.getSessionNames().size());

        ______TS("Typical case: student has no records, no profiles");

        String instructor4Id = typicalBundle.instructors.get("instructor4").googleId;
        // re-login as another instructor for new test
        loginAsInstructor(instructor4Id);
        String courseIdWithNoSession = "idOfCourseNoEvals";

        StudentAttributes testStudent = createStudentInTypicalDataBundleForCourseWithNoSession();

        String[] submissionParamsWithNoSession = new String[] {
                Const.ParamsNames.COURSE_ID, courseIdWithNoSession,
                Const.ParamsNames.STUDENT_EMAIL, "emailTemp@gmail.tmt",
        };

        a = getAction(submissionParamsWithNoSession);
        result = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        output = (StudentRecords) result.getOutput();

        assertNull(output.getStudentProfile());
        assertEquals(0, output.getSessionNames().size());

        ______TS("Typical case: student has no records");

        testStudent.googleId = "valid.no.sessions";
        StudentsLogic.inst().updateStudentCascade(
                StudentAttributes.updateOptionsBuilder(testStudent.course, testStudent.email)
                        .withGoogleId(testStudent.googleId)
                        .build());
        AccountsLogic.inst().createAccount(
                AccountAttributes.builder(testStudent.googleId)
                        .withName(testStudent.name)
                        .withIsInstructor(false)
                        .withEmail(testStudent.email)
                        .withInstitute("valid institue")
                        .build());

        a = getAction(submissionParamsWithNoSession);
        result = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        output = (StudentRecords) result.getOutput();

        assertEquals(0, output.getSessionNames().size());

        ______TS("Typical case: student has profile with script injection");

        instructor = typicalBundle.instructors.get("instructor1OfTestingSanitizationCourse");
        instructorId = instructor.googleId;
        String studentId = "student1InTestingSanitizationCourse";
        student = typicalBundle.students.get(studentId);
        StudentProfileAttributes expectedProfile = typicalBundle.profiles.get(studentId);

        loginAsInstructor(instructorId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
        };

        a = getAction(submissionParams);
        result = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        output = (StudentRecords) result.getOutput();

        expectedProfile.modifiedDate = null;
        expectedProfile.googleId = null;
        assertEquals(expectedProfile.toString(), output.getStudentProfile().toString());

    }

    private StudentAttributes createStudentInTypicalDataBundleForCourseWithNoSession()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        StudentAttributes student = StudentAttributes
                .builder("idOfCourseNoEvals", "emailTemp@gmail.tmt")
                .withName("nameOfStudent")
                .withSectionName("section")
                .withTeamName("team")
                .withComment("No comment")
                .build();

        StudentsLogic.inst().createStudent(student);
        return student;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
