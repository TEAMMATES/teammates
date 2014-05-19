package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static teammates.common.util.FieldValidator.EMAIL_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.logic.api.Logic;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.Emails;
import teammates.logic.core.EvaluationsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.logic.core.SubmissionsLogic;
import teammates.storage.api.StudentsDb;
import teammates.storage.entity.Student;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.cases.storage.EvaluationsDbTest;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

import com.google.appengine.api.datastore.KeyFactory;

public class StudentsLogicTest extends BaseComponentTestCase{
    
    protected static StudentsLogic studentsLogic = StudentsLogic.inst();
    protected static SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();
    protected static AccountsLogic accountsLogic = AccountsLogic.inst();
    protected static CoursesLogic coursesLogic = CoursesLogic.inst();
    protected static EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(StudentsLogic.class);
    }
    
    /*
     * NOTE: enrollStudents() tested in SubmissionsAdjustmentTest.
     * This is because it uses Task Queues for scheduling and therefore has to be
     * tested in a separate class.
     */
    
    @SuppressWarnings("deprecation")
    @Test
    public void testEnrollStudent() throws Exception {

        String instructorId = "instructorForEnrollTesting";
        String instructorCourse = "courseForEnrollTesting";
        
        //delete leftover data, if any
        accountsLogic.deleteAccountCascade(instructorId);
        coursesLogic.deleteCourseCascade(instructorCourse);
        
        //create fresh test data
        accountsLogic.createAccount(
                new AccountAttributes(instructorId, "ICET Instr Name", true,
                        "instructor@icet.com", "National University of Singapore"));
        coursesLogic.createCourseAndInstructor(instructorId, instructorCourse, "Course for Enroll Testing");

        ______TS("add student into empty course");

        StudentAttributes student1 = new StudentAttributes("t1", "n", "e@g", "c", instructorCourse);

        // check if the course is empty
        assertEquals(0, studentsLogic.getStudentsForCourse(instructorCourse).size());

        // add a new student and verify it is added and treated as a new student
        StudentEnrollDetails enrollmentResult = invokeEnrollStudent(student1);
        assertEquals(1, studentsLogic.getStudentsForCourse(instructorCourse).size());
        TestHelper.verifyEnrollmentDetailsForStudent(student1, null, enrollmentResult,
                StudentAttributes.UpdateStatus.NEW);
        TestHelper.verifyPresentInDatastore(student1);

        ______TS("add existing student");

        // Verify it was not added
        enrollmentResult = invokeEnrollStudent(student1);
        TestHelper.verifyEnrollmentDetailsForStudent(student1, null, enrollmentResult,
                StudentAttributes.UpdateStatus.UNMODIFIED);
        assertEquals(1, studentsLogic.getStudentsForCourse(instructorCourse).size());

        ______TS("add student into non-empty course");
        StudentAttributes student2 = new StudentAttributes("t1", "n2", "e2@g", "c", instructorCourse);
        enrollmentResult = invokeEnrollStudent(student2);
        TestHelper.verifyEnrollmentDetailsForStudent(student2, null, enrollmentResult,
                StudentAttributes.UpdateStatus.NEW);
        
        //add some more students to the same course (we add more than one 
        //  because we can use them for testing cascade logic later in this test case)
        invokeEnrollStudent(new StudentAttributes("t2", "n3", "e3@g", "c", instructorCourse));
        invokeEnrollStudent(new StudentAttributes("t2", "n4", "e4@g", "", instructorCourse));
        assertEquals(4, studentsLogic.getStudentsForCourse(instructorCourse).size());
        
        ______TS("modify info of existing student");
        //add some more details to the student
        student1.googleId = "googleId";
        studentsLogic.updateStudentCascade(student1.email, student1);
        
        ______TS("add evaluation and modify team of existing student" +
                "(to check the cascade logic of the SUT)");
        EvaluationAttributes e = EvaluationsDbTest.generateTypicalEvaluation();
        e.courseId = instructorCourse;
        evaluationsLogic.createEvaluationCascadeWithoutSubmissionQueue(e);

        //add some more details to the student
        String oldTeam = student2.team;
        student2.team = "t2";
        
        //Save student structure before changing of teams
        //This allows for verification of existence of old submissions 
        List<StudentAttributes> studentDetailsBeforeModification = studentsLogic
                .getStudentsForCourse(instructorCourse);
        
        enrollmentResult = invokeEnrollStudent(student2);
        TestHelper.verifyPresentInDatastore(student2);
        TestHelper.verifyEnrollmentDetailsForStudent(student2, oldTeam, enrollmentResult,
                StudentAttributes.UpdateStatus.MODIFIED);
        
        //verify that submissions have not been adjusted
        //i.e no new submissions have been added
        List<SubmissionAttributes> student2Submissions = submissionsLogic
                .getSubmissionsForEvaluation(instructorCourse, e.name);
        
        for (SubmissionAttributes submission : student2Submissions) {
            boolean isStudent2Participant = (submission.reviewee == student2.email) ||
                                           (submission.reviewer == student2.email);
            boolean isNewTeam = submission.team == student2.team;
            
            assertFalse(isNewTeam && isStudent2Participant);
        }
        
        //also, verify that the datastore still has the old team structure
        TestHelper.verifySubmissionsExistForCurrentTeamStructureInEvaluation(e.name,
                studentDetailsBeforeModification, submissionsLogic.getSubmissionsForCourse(instructorCourse));

        ______TS("error during enrollment");

        StudentAttributes student5 = new StudentAttributes("", "n6", "e6@g@", "", instructorCourse);
        enrollmentResult = invokeEnrollStudent(student5);
        assertEquals (StudentAttributes.UpdateStatus.ERROR, enrollmentResult.updateStatus);
        assertEquals(4, studentsLogic.getStudentsForCourse(instructorCourse).size());
        
    }
    
    @Test
    public void testUpdateStudentCascade() throws Exception {
            
        ______TS("typical edit");

        restoreTypicalDataInDatastore();
        dataBundle = getTypicalDataBundle();

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        TestHelper.verifyPresentInDatastore(student1InCourse1);
        String originalEmail = student1InCourse1.email;
        student1InCourse1.name = student1InCourse1.name + "x";
        student1InCourse1.googleId = student1InCourse1.googleId + "x";
        student1InCourse1.comments = student1InCourse1.comments + "x";
        student1InCourse1.email = student1InCourse1.email + "x";
        student1InCourse1.team = "Team 1.2"; // move to a different team

        // take a snapshot of submissions before
        List<SubmissionAttributes> submissionsBeforeEdit = submissionsLogic.getSubmissionsForCourse(student1InCourse1.course);

        // verify student details changed correctly
        studentsLogic.updateStudentCascade(originalEmail, student1InCourse1);
        TestHelper.verifyPresentInDatastore(student1InCourse1);

        // take a snapshot of submissions after the edit
        List<SubmissionAttributes> submissionsAfterEdit = submissionsLogic.getSubmissionsForCourse(student1InCourse1.course);
        
        // We moved a student from a 4-person team to an existing 1-person team.
        // We have 2 evaluations in the course.
        // Therefore, submissions that will be deleted = 7*2 = 14
        //              submissions that will be added = 3*2
        assertEquals(submissionsBeforeEdit.size() - 14  + 6,
                submissionsAfterEdit.size()); 
        
        // verify new submissions were created to match new team structure
        TestHelper.verifySubmissionsExistForCurrentTeamStructureInAllExistingEvaluations(submissionsAfterEdit,
                student1InCourse1.course);

        ______TS("check for KeepExistingPolicy : change email only");
        
        // create an empty student and then copy course and email attributes
        StudentAttributes copyOfStudent1 = new StudentAttributes();
        copyOfStudent1.course = student1InCourse1.course;
        originalEmail = student1InCourse1.email;

        String newEmail = student1InCourse1.email + "y";
        student1InCourse1.email = newEmail;
        copyOfStudent1.email = newEmail;

        studentsLogic.updateStudentCascade(originalEmail, copyOfStudent1);
        TestHelper.verifyPresentInDatastore(student1InCourse1);

        ______TS("check for KeepExistingPolicy : change nothing");    
        
        originalEmail = student1InCourse1.email;
        copyOfStudent1.email = null;
        studentsLogic.updateStudentCascade(originalEmail, copyOfStudent1);
        TestHelper.verifyPresentInDatastore(copyOfStudent1);
        
        ______TS("non-existent student");
        
        try {
            studentsLogic.updateStudentCascade("non-existent@email", student1InCourse1);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals(StudentsDb.ERROR_UPDATE_NON_EXISTENT_STUDENT
                    + student1InCourse1.course + "/" + "non-existent@email",
                    e.getMessage());
        }

        ______TS("check for InvalidParameters");
        try {
            copyOfStudent1.email = "invalid email";
            studentsLogic.updateStudentCascade(originalEmail, copyOfStudent1);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(FieldValidator.REASON_INCORRECT_FORMAT,
                    e.getMessage());
        }
    }
    
    @Test
    public void testSendRegistrationInviteToStudent() throws Exception {
        
        ______TS("typical case: send invite to one student");
        
        restoreTypicalDataInDatastore();
        dataBundle = getTypicalDataBundle();

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String studentEmail = student1InCourse1.email;
        String courseId = student1InCourse1.course;
        MimeMessage msgToStudent = studentsLogic.sendRegistrationInviteToStudent(courseId, studentEmail);
        Emails emailMgr = new Emails();
        @SuppressWarnings("static-access")
        String emailInfo = emailMgr.getEmailInfo(msgToStudent);
        String expectedEmailInfo = "[Email sent]to=student1InCourse1@gmail.com|from=" + 
                "\"TEAMMATES Admin (noreply)\" <noreply@null.appspotmail.com>|subject=TEAMMATES:" + 
                " Invitation to join course [Typical Course 1 with 2 Evals][Course ID: idOfTypicalCourse1]";
        assertEquals(expectedEmailInfo, emailInfo);
        
        
        ______TS("invalid course id");
        
        String invalidCourseId = "invalidCourseId";
        try {
            studentsLogic.sendRegistrationInviteToStudent(invalidCourseId, studentEmail);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
        }
        
        
        ______TS("invalid student email");
        
        String invalidStudentEmail = "invalidStudentEmail";
        try {
            studentsLogic.sendRegistrationInviteToStudent(courseId, invalidStudentEmail);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
        }
        
    }
    
    @Test
    public void testSendRegistrationInvliteForCourse() throws Exception {

        ______TS("typical case: send invite to one student");
        
        dataBundle = getTypicalDataBundle();
        String courseId = dataBundle.courses.get("typicalCourse1").id;
        StudentAttributes newsStudent0Info = new StudentAttributes("team", "n0", "e0@google.com", "", courseId);
        StudentAttributes newsStudent1Info = new StudentAttributes("team", "n1", "e1@google.com", "", courseId);
        StudentAttributes newsStudent2Info = new StudentAttributes("team", "n2", "e2@google.com", "", courseId);
        invokeEnrollStudent(newsStudent0Info);
        invokeEnrollStudent(newsStudent1Info);
        invokeEnrollStudent(newsStudent2Info);

        List<MimeMessage> msgsForCourse = studentsLogic.sendRegistrationInviteForCourse(courseId);
        assertEquals(3, msgsForCourse.size());
        Emails emailMgr = new Emails();
        @SuppressWarnings("static-access")
        String emailInfo0 = emailMgr.getEmailInfo(msgsForCourse.get(0));
        String expectedEmailInfoForEmail0 = "[Email sent]to=e0@google.com|from=\"TEAMMATES Admin (noreply)\" " + 
                "<noreply@null.appspotmail.com>|subject=TEAMMATES: Invitation to join course " + 
                "[Typical Course 1 with 2 Evals][Course ID: idOfTypicalCourse1]";
        assertEquals(expectedEmailInfoForEmail0, emailInfo0);
        @SuppressWarnings("static-access")
        String emailInfo1 = emailMgr.getEmailInfo(msgsForCourse.get(1));
        String expectedEmailInfoForEmail1 = "[Email sent]to=e1@google.com|from=\"TEAMMATES Admin (noreply)\" " + 
                "<noreply@null.appspotmail.com>|subject=TEAMMATES: Invitation to join course " + 
                "[Typical Course 1 with 2 Evals][Course ID: idOfTypicalCourse1]";
        assertEquals(expectedEmailInfoForEmail1, emailInfo1);
        @SuppressWarnings("static-access")
        String emailInfo2 = emailMgr.getEmailInfo(msgsForCourse.get(2));
        String expectedEmailInfoForEmail2 = "[Email sent]to=e2@google.com|from=" + 
                "\"TEAMMATES Admin (noreply)\" <noreply@null.appspotmail.com>|subject=TEAMMATES:" + 
                " Invitation to join course [Typical Course 1 with 2 Evals][Course ID: idOfTypicalCourse1]";
        assertEquals(expectedEmailInfoForEmail2, emailInfo2);
    }
    
    @Test
    public void testKeyGeneration() {
        long key = 5;
        String longKey = KeyFactory.createKeyString(
                Student.class.getSimpleName(), key);
        long reverseKey = KeyFactory.stringToKey(longKey).getId();
        assertEquals(key, reverseKey);
        assertEquals("Student", KeyFactory.stringToKey(longKey).getKind());
    }
    
    @Test
    public void testEnrollLinesChecking() throws Exception {
        String info;
        String enrollLines;
        String courseId = "CourseID";
        coursesLogic.createCourse(courseId, "CourseName");
        
        List<String> invalidInfo;
        List<String> expectedInvalidInfo = new ArrayList<String>();
        
        ______TS("enrollLines with invalid parameters");
        String invalidTeamName = StringHelper.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1);
        String invalidStudentName = StringHelper.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);
        
        String lineWithInvalidTeamName = invalidTeamName + "| John | john@email.com";
        String lineWithInvalidStudentName = "Team 1 |" + invalidStudentName + 
                "| student@email.com";
        String lineWithInvalidEmail = "Team 1 | James |" + "James_invalid_email.com";
        String lineWithInvalidStudentNameAndEmail = "Team 2 |" + invalidStudentName + 
                "|" + "student_invalid_email.com";
        String lineWithInvalidTeamNameAndEmail = invalidTeamName + "| Paul |" + 
                "Paul_invalid_email.com";
        String lineWithInvalidTeamNameAndStudentNameAndEmail = invalidTeamName + "|" + 
                invalidStudentName + "|" + "invalid_email.com";
        
        enrollLines = lineWithInvalidTeamName + Const.EOL + lineWithInvalidStudentName + Const.EOL +
                    lineWithInvalidEmail + Const.EOL + lineWithInvalidStudentNameAndEmail + Const.EOL +
                    lineWithInvalidTeamNameAndEmail + Const.EOL + lineWithInvalidTeamNameAndStudentNameAndEmail;
        
        invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);

        StudentAttributesFactory saf = new StudentAttributesFactory();
        expectedInvalidInfo.clear();
        info = StringHelper.toString(saf.makeStudent(lineWithInvalidTeamName, courseId).getInvalidityInfo(), 
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamName, info));
        info = StringHelper.toString(saf.makeStudent(lineWithInvalidStudentName, courseId).getInvalidityInfo(), 
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidStudentName, info));
        info = StringHelper.toString(saf.makeStudent(lineWithInvalidEmail, courseId).getInvalidityInfo(), 
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidEmail, info));
        info = StringHelper.toString(saf.makeStudent(lineWithInvalidStudentNameAndEmail, courseId).getInvalidityInfo(), 
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidStudentNameAndEmail, info));
        info = StringHelper.toString(saf.makeStudent(lineWithInvalidTeamNameAndEmail, courseId).getInvalidityInfo(), 
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamNameAndEmail, info));
        info = StringHelper.toString(saf.makeStudent(lineWithInvalidTeamNameAndStudentNameAndEmail, 
                courseId).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, 
                lineWithInvalidTeamNameAndStudentNameAndEmail, info));
        
        for (int i = 0; i < invalidInfo.size(); i++) {
            assertEquals(expectedInvalidInfo.get(i), invalidInfo.get(i));
        }
        
        ______TS("enrollLines with too few");
        String lineWithNoEmailInput = "Team 4 | StudentWithNoEmailInput";
        String lineWithExtraParameters = "Team 4 | StudentWithExtraParameters | " + 
               " studentWithExtraParameters@email.com | comment | extra_parameter";
        
        enrollLines = lineWithNoEmailInput + Const.EOL + lineWithExtraParameters;
        
        invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);

        expectedInvalidInfo.clear();
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithNoEmailInput, 
                StudentAttributesFactory.ERROR_ENROLL_LINE_TOOFEWPARTS));
        
        for (int i = 0; i < invalidInfo.size(); i++) {
            assertEquals(expectedInvalidInfo.get(i), invalidInfo.get(i));
        }
        
        ______TS("enrollLines with some empty fields");
        String lineWithTeamNameEmpty = "    | StudentWithTeamFieldEmpty | student@email.com";
        String lineWithStudentNameEmpty = "Team 5 |  | no_name@email.com";
        String lineWithEmailEmpty = "Team 5 | StudentWithEmailFieldEmpty | |";
        
        enrollLines = lineWithTeamNameEmpty + Const.EOL + lineWithStudentNameEmpty + Const.EOL + lineWithEmailEmpty;

        invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);
        expectedInvalidInfo.clear();
        info = StringHelper.toString(saf.makeStudent(lineWithTeamNameEmpty, courseId).getInvalidityInfo(), 
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithTeamNameEmpty, info));
        info = StringHelper.toString(saf.makeStudent(lineWithStudentNameEmpty, courseId).getInvalidityInfo(), 
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithStudentNameEmpty, info));
        info = StringHelper.toString(saf.makeStudent(lineWithEmailEmpty, courseId).getInvalidityInfo(), 
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithEmailEmpty, info));

        for (int i = 0; i < invalidInfo.size(); i++) {
            assertEquals(expectedInvalidInfo.get(i), invalidInfo.get(i));
        }

        ______TS("enrollLines with correct input");
        String lineWithCorrectInput = "Team 3 | Mary | mary@email.com";
        String lineWithCorrectInputWithComment = "Team 4 | Benjamin | benjamin@email.com | Foreign student";
        
        enrollLines = lineWithCorrectInput + Const.EOL + lineWithCorrectInputWithComment;
        
        invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);

        assertEquals(0, invalidInfo.size());
        
        ______TS("enrollLines with only whitespaces");
        // not tested as enroll lines must be trimmed before passing to the method
        
        ______TS("enrollLines with a mix of all above cases");
        enrollLines = lineWithInvalidTeamName + Const.EOL + lineWithInvalidTeamNameAndStudentNameAndEmail + 
                Const.EOL + lineWithExtraParameters + Const.EOL +
                lineWithTeamNameEmpty + Const.EOL + lineWithCorrectInput + Const.EOL + "\t";

        invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);
        
        expectedInvalidInfo.clear();
        info = StringHelper.toString(saf.makeStudent(lineWithInvalidTeamName, courseId).getInvalidityInfo(), 
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamName, info));
        info = StringHelper.toString(saf.makeStudent(lineWithInvalidTeamNameAndStudentNameAndEmail, courseId).getInvalidityInfo(), 
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamNameAndStudentNameAndEmail, info));
        info = StringHelper.toString(saf.makeStudent(lineWithTeamNameEmpty, courseId).getInvalidityInfo(), 
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithTeamNameEmpty, info));
        info = StringHelper.toString(saf.makeStudent(lineWithCorrectInput, courseId).getInvalidityInfo(), 
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithCorrectInput, info));
        
        for (int i = 0; i < invalidInfo.size(); i++) {
            assertEquals(expectedInvalidInfo.get(i), invalidInfo.get(i));
        }
        
    }
    
    @Test
    public void testEnrollStudents() throws Exception {
        //TODO: finish this test
        
        ______TS("typical case: enroll students");
        
        String info = "";
        String enrollLines = "";
        String courseId = "CourseID";
        coursesLogic.deleteCourseCascade(courseId);
        coursesLogic.createCourse(courseId, "CourseName");
        
        
        ______TS("invalid course id");
        String invalidCourseId = "invalidCourseId";
        try {
            studentsLogic.enrollStudents(enrollLines, invalidCourseId);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
        }
        
        
        ______TS("empty enroll line");
        
        try {
            studentsLogic.enrollStudents(enrollLines, courseId);
            signalFailureToDetectException();
        } catch (EnrollException e) {
        }
        
        
        // method testEnrollLinesChecking is doing more checking. here is just to
        // cover this path
        ______TS("invalidity info in enroll line");
        
        enrollLines = "invalidline0\ninvalidline1\n";
        try {
            studentsLogic.enrollStudents(enrollLines, courseId);
            signalFailureToDetectException();
        } catch (EnrollException e) {
        }
        
    }
    
    @Test
    public void testcreateStudentWithSubmissionAdjustment() throws Exception {

        restoreTypicalDataInDatastore();

        ______TS("typical case");

        restoreTypicalDataInDatastore();
        //reuse existing student to create a new student
        StudentAttributes newStudent = dataBundle.students.get("student1InCourse1");
        newStudent.email = "new@student.com";
        TestHelper.verifyAbsentInDatastore(newStudent);
        
        List<SubmissionAttributes> submissionsBeforeAdding = submissionsLogic.getSubmissionsForCourse(newStudent.course);
        
        invokeEnrollStudent(newStudent);
        TestHelper.verifyPresentInDatastore(newStudent);
        
        List<SubmissionAttributes> submissionsAfterAdding = submissionsLogic.getSubmissionsForCourse(newStudent.course);
        
        //expected increase in submissions = 2*(1+4+4)
        //2 is the number of evaluations in the course
        //4 is the number of existing members in the team
        //1 is the self evaluation
        //We simply check the increase in submissions. A deeper check is 
        //  unnecessary because adjusting existing submissions should be 
        //  checked elsewhere.
        
        assertEquals(submissionsBeforeAdding.size() + 18, submissionsAfterAdding.size());

        ______TS("duplicate student");

        // try to create the same student
        try {
            invokeEnrollStudent(newStudent);
            Assert.fail();
        } catch (EntityAlreadyExistsException e) {
        }

        ______TS("invalid parameter");

        // Only checking that exception is thrown at logic level
        newStudent.email = "invalid email";
        
        try {
            invokeEnrollStudent(newStudent);
            Assert.fail();
        } catch (InvalidParametersException e) {
            assertEquals(
                    String.format(EMAIL_ERROR_MESSAGE, "invalid email", REASON_INCORRECT_FORMAT),
                    e.getMessage());
        }
        
        ______TS("null parameters");
        
        try {
            invokeEnrollStudent(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }

        // other combination of invalid data should be tested against
        // StudentAttributes

    }

    @Test
    public void testGetStudentForEmail() throws Exception {
        // mostly tested in testcreateStudentWithSubmissionAdjustment

        restoreTypicalDataInDatastore();


        ______TS("null parameters");

        try {
            studentsLogic.getStudentForEmail(null, "valid@email.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals("Supplied parameter was null\n", a.getMessage());
        }
    }

    @Test
    public void testGetStudentsForGoogleId() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        ______TS("student in one course");
    
        
    
        restoreTypicalDataInDatastore();
        StudentAttributes studentInOneCourse = dataBundle.students
                .get("student1InCourse1");
        assertEquals(1, studentsLogic.getStudentsForGoogleId(studentInOneCourse.googleId).size());
        assertEquals(studentInOneCourse.email,
                studentsLogic.getStudentsForGoogleId(studentInOneCourse.googleId).get(0).email);
        assertEquals(studentInOneCourse.name,
                studentsLogic.getStudentsForGoogleId(studentInOneCourse.googleId).get(0).name);
        assertEquals(studentInOneCourse.course,
                studentsLogic.getStudentsForGoogleId(studentInOneCourse.googleId).get(0).course);
    
        ______TS("student in two courses");
    
        // this student is in two courses, course1 and course 2.
    
        // get list using student data from course 1
        StudentAttributes studentInTwoCoursesInCourse1 = dataBundle.students
                .get("student2InCourse1");
        List<StudentAttributes> listReceivedUsingStudentInCourse1 = studentsLogic
                .getStudentsForGoogleId(studentInTwoCoursesInCourse1.googleId);
        assertEquals(2, listReceivedUsingStudentInCourse1.size());
    
        // get list using student data from course 2
        StudentAttributes studentInTwoCoursesInCourse2 = dataBundle.students
                .get("student2InCourse2");
        List<StudentAttributes> listReceivedUsingStudentInCourse2 = studentsLogic
                .getStudentsForGoogleId(studentInTwoCoursesInCourse2.googleId);
        assertEquals(2, listReceivedUsingStudentInCourse2.size());
    
        // check the content from first list (we assume the content of the
        // second list is similar.
    
        StudentAttributes firstStudentReceived = listReceivedUsingStudentInCourse1.get(0);
        // First student received turned out to be the one from course 2
        assertEquals(studentInTwoCoursesInCourse2.email,
                firstStudentReceived.email);
        assertEquals(studentInTwoCoursesInCourse2.name,
                firstStudentReceived.name);
        assertEquals(studentInTwoCoursesInCourse2.course,
                firstStudentReceived.course);
    
        // then the second student received must be from course 1
        StudentAttributes secondStudentReceived = listReceivedUsingStudentInCourse1
                .get(1);
        assertEquals(studentInTwoCoursesInCourse1.email,
                secondStudentReceived.email);
        assertEquals(studentInTwoCoursesInCourse1.name,
                secondStudentReceived.name);
        assertEquals(studentInTwoCoursesInCourse1.course,
                secondStudentReceived.course);
    
        ______TS("non existent student");
    
        assertEquals(0, studentsLogic.getStudentsForGoogleId("non-existent").size());
    
        ______TS("null parameters");
    
        try {
            studentsLogic.getStudentsForGoogleId(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals("Supplied parameter was null\n", a.getMessage());
        }
    }

    @Test
    public void testGetStudentForGoogleId() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        ______TS("student in two courses");
    
        restoreTypicalDataInDatastore();
        
        StudentAttributes studentInTwoCoursesInCourse1 = dataBundle.students
                .get("student2InCourse1");
    
        String googleIdOfstudentInTwoCourses = studentInTwoCoursesInCourse1.googleId;
        assertEquals(studentInTwoCoursesInCourse1.email,
                studentsLogic.getStudentForGoogleId(
                        studentInTwoCoursesInCourse1.course,
                        googleIdOfstudentInTwoCourses).email);
    
        StudentAttributes studentInTwoCoursesInCourse2 = dataBundle.students
                .get("student2InCourse2");
        assertEquals(studentInTwoCoursesInCourse2.email,
                studentsLogic.getStudentForGoogleId(
                        studentInTwoCoursesInCourse2.course,
                        googleIdOfstudentInTwoCourses).email);
    
        ______TS("student in zero courses");
    
        assertEquals(null, studentsLogic.getStudentForGoogleId("non-existent",
                "random-google-id"));
    
        ______TS("null parameters");
    
        try {
            studentsLogic.getStudentForGoogleId("valid.course", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals("Supplied parameter was null\n", a.getMessage());
        }
    }

    @Test
    public void testGetStudentsForCourse() throws Exception {
    
        restoreTypicalDataInDatastore();
        
        ______TS("course with multiple students");
    
        restoreTypicalDataInDatastore();
    
        
    
        CourseAttributes course1OfInstructor1 = dataBundle.courses.get("typicalCourse1");
        List<StudentAttributes> studentList = studentsLogic
                .getStudentsForCourse(course1OfInstructor1.id);
        assertEquals(5, studentList.size());
        for (StudentAttributes s : studentList) {
            assertEquals(course1OfInstructor1.id, s.course);
        }
    
        ______TS("course with 0 students");
    
        CourseAttributes course2OfInstructor1 = dataBundle.courses.get("courseNoEvals");
        studentList = studentsLogic.getStudentsForCourse(course2OfInstructor1.id);
        assertEquals(0, studentList.size());
    
        ______TS("null parameter");
    
        try {
            studentsLogic.getStudentsForCourse(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals("Supplied parameter was null\n", a.getMessage());
        }
    
        ______TS("non-existent course");
    
        studentList = studentsLogic.getStudentsForCourse("non-existent");
        assertEquals(0, studentList.size());
        
    }

    

    @Test
    public void testGetKeyForStudent() throws Exception {
        // mostly tested in testJoinCourse()
    
        restoreTypicalDataInDatastore();
    
        ______TS("null parameters");
    
        try {
            studentsLogic.getKeyForStudent("valid.course.id", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals("Supplied parameter was null\n", a.getMessage());
        }
    
        ______TS("non-existent student");
        
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        assertEquals(null,
                studentsLogic.getKeyForStudent(student.course, "non@existent"));
    }

    @Test
    public void testSendRegistrationInviteForCourse() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        ______TS("all students already registered");
   
        restoreTypicalDataInDatastore();
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
    
        // send registration key to a class in which all are registered
        List<MimeMessage> emailsSent = studentsLogic
                .sendRegistrationInviteForCourse(course1.id);
        assertEquals(0, emailsSent.size());
    
        ______TS("some students not registered");
    
        // modify two students to make them 'unregistered' and send again
        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        student1InCourse1.googleId = "";
        studentsLogic.updateStudentCascade(student1InCourse1.email, student1InCourse1);
        StudentAttributes student2InCourse1 = dataBundle.students
                .get("student2InCourse1");
        student2InCourse1.googleId = "";
        studentsLogic.updateStudentCascade(student2InCourse1.email, student2InCourse1);
        emailsSent = studentsLogic.sendRegistrationInviteForCourse(course1.id);
        assertEquals(2, emailsSent.size());
        TestHelper.verifyJoinInviteToStudent(student2InCourse1, emailsSent.get(0));
        TestHelper.verifyJoinInviteToStudent(student1InCourse1, emailsSent.get(1));
    
        ______TS("null parameters");
    
        try {
            studentsLogic.sendRegistrationInviteForCourse(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals("Supplied parameter was null\n", a.getMessage());
        }
    }

    private static StudentEnrollDetails invokeEnrollStudent(StudentAttributes student)
            throws Exception {
        Method privateMethod = StudentsLogic.class.getDeclaredMethod("enrollStudent",
                new Class[] { StudentAttributes.class });
        privateMethod.setAccessible(true);
        Object[] params = new Object[] { student };
        return (StudentEnrollDetails) privateMethod.invoke(StudentsLogic.inst(), params);
    }
    
    @SuppressWarnings("unchecked")
    private static List<String> invokeGetInvalidityInfoInEnrollLines(String lines, String courseID)
            throws Exception {
        Method privateMethod = StudentsLogic.class.getDeclaredMethod("getInvalidityInfoInEnrollLines",
                                    new Class[] { String.class, String.class });
        privateMethod.setAccessible(true);
        Object[] params = new Object[] { lines, courseID };
        return (List<String>) privateMethod.invoke(StudentsLogic.inst(), params);
    }
        
    @AfterClass()
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(StudentsLogic.class);
    }

}
