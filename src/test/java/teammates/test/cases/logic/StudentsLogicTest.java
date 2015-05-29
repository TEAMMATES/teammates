package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.mail.internet.MimeMessage;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.Emails;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.StudentsDb;
import teammates.storage.entity.Student;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

public class StudentsLogicTest extends BaseComponentTestCase{
    
    protected static StudentsLogic studentsLogic = StudentsLogic.inst();
    protected static AccountsLogic accountsLogic = AccountsLogic.inst();
    protected static CoursesLogic coursesLogic = CoursesLogic.inst();
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(StudentsLogic.class);
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAll() throws Exception{
        
        testGetStudentProfile();
        testGetStudentForEmail();
        testGetStudentForRegistrationKey();
        testGetStudentsForGoogleId();
        testGetStudentForCourseIdAndGoogleId();
        testGetStudentsForCourse();
        testGetKeyForStudent();
        testGetEncryptedKeyForStudent();
        testIsStudentInAnyCourse();
        testIsStudentInCourse();
        testIsStudentInTeam();
        testIsStudentsInSameTeam();
        
        testGetTeamForStudent();
        
        testEnrollStudent();
        testAdjustFeedbackResponseForEnrollments();

        testValidateSections();
        testupdateStudentCascadeWithoutDocument();
        testSendRegistrationInviteToStudent();
        testKeyGeneration();
        testEnrollLinesChecking();
        testEnrollStudents();
        
        testSendRegistrationInviteForCourse();
        
        testDeleteStudent();
        
        
    }

    /*
     * NOTE: enrollStudents() tested in SubmissionsAdjustmentTest.
     * This is because it uses Task Queues for scheduling and therefore has to be
     * tested in a separate class.
     */

    public void testGetTeamForStudent() {
        ______TS("Typical case: get team of existing student");
        
        String courseId = "idOfTypicalCourse1";
        String googleId = "student1InCourse1";
        StudentAttributes student = StudentsLogic.inst().getStudentForCourseIdAndGoogleId(courseId, googleId);
        TeamDetailsBundle team = StudentsLogic.inst().getTeamDetailsForStudent(student);
        
        assertEquals("Team 1.1", team.name);
        assertTrue(team.students != null);
        assertEquals(4, team.students.size());
        
        ______TS("Typical case: get team of non-existing student");
        courseId = "idOfTypicalCourse1";
        googleId = "idOfNonExistingStudent";
        student = StudentsLogic.inst().getStudentForCourseIdAndGoogleId(courseId, googleId);
        team = StudentsLogic.inst().getTeamDetailsForStudent(student);
        
        assertEquals(null, team);

    }

    public void testEnrollStudent() throws Exception {

        String instructorId = "instructorForEnrollTesting";
        String instructorCourse = "courseForEnrollTesting";
        
        //delete leftover data, if any
        accountsLogic.deleteAccountCascade(instructorId);
        coursesLogic.deleteCourseCascade(instructorCourse);
        
        //create fresh test data
        accountsLogic.createAccount(
                new AccountAttributes(instructorId, "ICET Instr Name", true,
                        "instructor@icet.tmt", "TEAMMATES Test Institute 1",
                        new StudentProfileAttributes(instructorId, "ICET", "", "", "", "other", "", "")));
        coursesLogic.createCourseAndInstructor(instructorId, instructorCourse, "Course for Enroll Testing");

        ______TS("add student into empty course");

        StudentAttributes student1 = new StudentAttributes("sect 1", "t1", "n", "e@g", "c", instructorCourse);

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
        StudentAttributes student2 = new StudentAttributes("sect 1", "t1", "n2", "e2@g", "c", instructorCourse);
        enrollmentResult = invokeEnrollStudent(student2);
        TestHelper.verifyEnrollmentDetailsForStudent(student2, null, enrollmentResult,
                StudentAttributes.UpdateStatus.NEW);
        
        //add some more students to the same course (we add more than one 
        //  because we can use them for testing cascade logic later in this test case)
        invokeEnrollStudent(new StudentAttributes("sect 2", "t2", "n3", "e3@g", "c", instructorCourse));
        invokeEnrollStudent(new StudentAttributes("sect 2", "t2", "n4", "e4@g", "", instructorCourse));
        assertEquals(4, studentsLogic.getStudentsForCourse(instructorCourse).size());
        
        ______TS("modify info of existing student");
        //add some more details to the student
        student1.googleId = "googleId";
        studentsLogic.updateStudentCascadeWithoutDocument(student1.email, student1);
        

    }
    
    public void testGetStudentProfile() throws Exception {

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        AccountAttributes student1 = dataBundle.accounts.get("student1InCourse1");
        
        ______TS("success: default profile");
        
        StudentProfileAttributes actualSpa = studentsLogic.getStudentProfile(student1InCourse1.googleId);
        StudentProfileAttributes expectedSpa = student1.studentProfile;
        
        // fill-in auto-generated and default values
        expectedSpa.institute = actualSpa.institute;
        expectedSpa.modifiedDate = actualSpa.modifiedDate;
        
        assertEquals(expectedSpa.toString(), actualSpa.toString());
        
        ______TS("success: edited profile");
        
        StudentProfileAttributes expectedStudentProfile = new StudentProfileAttributes();
        
        expectedStudentProfile.googleId = student1.googleId;
        expectedStudentProfile.shortName = "short";
        expectedStudentProfile.email = "personal@email.tmt";
        expectedStudentProfile.institute = "institute";
        expectedStudentProfile.nationality = "Valid Nationality";
        expectedStudentProfile.gender = "female";
        expectedStudentProfile.moreInfo = "This sentence may sound sound but it cannot make actual sound... :P";
        
        student1.studentProfile = expectedStudentProfile;
        accountsLogic.updateAccount(student1, true);
        
        StudentProfileAttributes actualStudentProfile = studentsLogic.getStudentProfile(student1InCourse1.googleId);
        expectedStudentProfile.modifiedDate = actualStudentProfile.modifiedDate;
        assertEquals(expectedStudentProfile.toString(), actualStudentProfile.toString());
    }
    
    public void testValidateSections() throws Exception {

        CourseAttributes typicalCourse1 = dataBundle.courses.get("typicalCourse1");
        String courseId = typicalCourse1.id;

        ______TS("Typical case");

        List<StudentAttributes> studentList = new ArrayList<StudentAttributes>();
        studentList.add(new StudentAttributes("Section 3", "Team 1.3", "New Student", "emailNew@com", "", courseId));
        studentList.add(new StudentAttributes("Section 2", "Team 1.4", "student2 In Course1", "student2InCourse1@gmail.tmt","",courseId));
        try {
            studentsLogic.validateSections(studentList, courseId);
        } catch (EnrollException e) {
            Assumption.fail("This exception is not expected: " + e.getMessage());
        }

        ______TS("Failure case: invalid section");

        studentList = new ArrayList<StudentAttributes>();
        for(int i = 0; i < 100; i++){
            StudentAttributes addedStudent = new StudentAttributes("Section 1", "Team " + i, "Name " + i, "email@com" + i, "cmt" + i, courseId);
            studentList.add(addedStudent);
        }
        try {
            studentsLogic.validateSections(studentList, courseId);
        } catch (EnrollException e) {
            assertEquals(String.format(Const.StatusMessages.SECTION_QUOTA_EXCEED, "Section 1"), e.getMessage());
        }

        ______TS("Failure case: invalid team");

        studentList = new ArrayList<StudentAttributes>();
        studentList.add(new StudentAttributes("Section 2","Team 1.1","New Student", "newemail@com", "", courseId));
        try {
            studentsLogic.validateSections(studentList, courseId);
        } catch (EnrollException e) {
            assertEquals(String.format(Const.StatusMessages.TEAM_INVALID_SECTION_EDIT,"Team 1.1") + "Please use the enroll page to edit multiple students"
                    , e.getMessage());
        }
    }

    public void testupdateStudentCascadeWithoutDocument() throws Exception {
        
        ______TS("typical edit");

        StudentAttributes student4InCourse1 = dataBundle.students.get("student4InCourse1");
        TestHelper.verifyPresentInDatastore(student4InCourse1);
        String originalEmail = student4InCourse1.email;
        student4InCourse1.name = student4InCourse1.name + "y";
        student4InCourse1.googleId = student4InCourse1.googleId + "y";
        student4InCourse1.comments = student4InCourse1.comments + "y";
        student4InCourse1.email = student4InCourse1.email + "y";
        student4InCourse1.section = "Section 2";
        student4InCourse1.team = "Team 1.2"; // move to a different team

        studentsLogic.updateStudentCascadeWithoutDocument(originalEmail, student4InCourse1);

        ______TS("check for KeepExistingPolicy : change email only");
        
        // create an empty student and then copy course and email attributes
        StudentAttributes copyOfStudent1 = new StudentAttributes();
        copyOfStudent1.course = student4InCourse1.course;
        originalEmail = student4InCourse1.email;

        String newEmail = student4InCourse1.email + "y";
        student4InCourse1.email = newEmail;
        copyOfStudent1.email = newEmail;

        studentsLogic.updateStudentCascadeWithoutDocument(originalEmail, copyOfStudent1);
        TestHelper.verifyPresentInDatastore(student4InCourse1);

        ______TS("check for KeepExistingPolicy : change nothing");    
        
        originalEmail = student4InCourse1.email;
        copyOfStudent1.email = null;
        studentsLogic.updateStudentCascadeWithoutDocument(originalEmail, copyOfStudent1);
        TestHelper.verifyPresentInDatastore(copyOfStudent1);
        
        ______TS("non-existent student");
        
        try {
            studentsLogic.updateStudentCascadeWithoutDocument("non-existent@email", student4InCourse1);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals(StudentsDb.ERROR_UPDATE_NON_EXISTENT_STUDENT
                    + student4InCourse1.course + "/" + "non-existent@email",
                    e.getMessage());
        }

        
        ______TS("check for InvalidParameters");
        copyOfStudent1.email = "invalid email";
        try {
            studentsLogic.updateStudentCascadeWithoutDocument(originalEmail, copyOfStudent1);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(FieldValidator.REASON_INCORRECT_FORMAT,
                    e.getMessage());
        }
        
        // delete student from db
        
    }
    
    public void testSendRegistrationInviteToStudent() throws Exception {
        
        ______TS("typical case: send invite to one student");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String studentEmail = student1InCourse1.email;
        String courseId = student1InCourse1.course;
        MimeMessage msgToStudent = studentsLogic.sendRegistrationInviteToStudent(courseId, studentEmail);
        Emails emailMgr = new Emails();
        @SuppressWarnings("static-access")
        String emailInfo = emailMgr.getEmailInfo(msgToStudent);
        String expectedEmailInfo = "[Email sent]to=student1InCourse1@gmail.tmt|from=" + 
                "TEAMMATES Admin <Admin@null.appspotmail.com>|subject=TEAMMATES:" + 
                " Invitation to join course [Typical Course 1 with 2 Evals][Course ID: idOfTypicalCourse1]";
        assertEquals(expectedEmailInfo, emailInfo);
        
        ______TS("invalid course id");
        
        String invalidCourseId = "invalidCourseId";
        try {
            studentsLogic.sendRegistrationInviteToStudent(invalidCourseId, studentEmail);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            String expectedMsg = "Course does not exist [" + invalidCourseId + 
                    "], trying to send invite email to student [" + studentEmail + "]";
            assertEquals(expectedMsg, e.getMessage());
        }
        
        
        ______TS("invalid student email");
        
        String invalidStudentEmail = "invalidStudentEmail";
        try {
            studentsLogic.sendRegistrationInviteToStudent(courseId, invalidStudentEmail);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            String expectedMsg = "Student [" + invalidStudentEmail + "] does not exist in course [" + courseId + "]";
            assertEquals(expectedMsg, e.getMessage());
        }
        
    }
    
    public void testKeyGeneration() {
        
        ______TS("key generation");
        
        long key = 5;
        String longKey = KeyFactory.createKeyString(Student.class.getSimpleName(), key);
        long reverseKey = KeyFactory.stringToKey(longKey).getId();
        assertEquals(key, reverseKey);
        assertEquals("Student", KeyFactory.stringToKey(longKey).getKind());
    }
    
    public void testAdjustFeedbackResponseForEnrollments() throws Exception {
        
        
        
        // the case below will not cause the response to be deleted
        // because the studentEnrollDetails'email is not the same as giver or recipient
        ______TS("adjust feedback response: no change of team");
        
        String course1Id = dataBundle.courses.get("typicalCourse1").id;
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        StudentAttributes student2InCourse1 = dataBundle.students.get("student2InCourse1");
        ArrayList<StudentEnrollDetails> enrollmentList = new ArrayList<StudentEnrollDetails>();
        StudentEnrollDetails studentDetails1 = new StudentEnrollDetails(StudentAttributes.UpdateStatus.MODIFIED,
                course1Id, student1InCourse1.email, student1InCourse1.team, student1InCourse1.team + "tmp", student1InCourse1.section, student1InCourse1.section + "tmp");
        enrollmentList.add(studentDetails1);
        
        FeedbackResponseAttributes feedbackResponse1InBundle = dataBundle.feedbackResponses.get("response1ForQ2S2C1");
        FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
        FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
        FeedbackQuestionAttributes feedbackQuestionInDb = fqLogic.getFeedbackQuestion(feedbackResponse1InBundle.feedbackSessionName, 
                feedbackResponse1InBundle.courseId, Integer.parseInt(feedbackResponse1InBundle.feedbackQuestionId));
        FeedbackResponseAttributes responseBefore = frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(),
                feedbackResponse1InBundle.giverEmail, feedbackResponse1InBundle.recipientEmail);
        
        studentsLogic.adjustFeedbackResponseForEnrollments(enrollmentList, responseBefore);
        
        FeedbackResponseAttributes responseAfter = frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(), 
                feedbackResponse1InBundle.giverEmail, feedbackResponse1InBundle.recipientEmail);
        assertEquals(responseBefore.getId(), responseAfter.getId());
        
        
        // the case below will not cause the response to be deleted
        // because the studentEnrollDetails'email is not the same as giver or recipient
        ______TS("adjust feedback response: unmodified status");
        
        enrollmentList = new ArrayList<StudentEnrollDetails>();
        studentDetails1 = new StudentEnrollDetails(StudentAttributes.UpdateStatus.UNMODIFIED,
                course1Id, student1InCourse1.email, student1InCourse1.team, student1InCourse1.team + "tmp", student1InCourse1.section, student1InCourse1.section + "tmp");
        enrollmentList.add(studentDetails1);
        
        feedbackQuestionInDb = fqLogic.getFeedbackQuestion(feedbackResponse1InBundle.feedbackSessionName, 
                feedbackResponse1InBundle.courseId, Integer.parseInt(feedbackResponse1InBundle.feedbackQuestionId));
        responseBefore = frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(),
                feedbackResponse1InBundle.giverEmail, feedbackResponse1InBundle.recipientEmail);
        
        studentsLogic.adjustFeedbackResponseForEnrollments(enrollmentList, responseBefore);
        
        responseAfter = frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(), 
                feedbackResponse1InBundle.giverEmail, feedbackResponse1InBundle.recipientEmail);
        assertEquals(responseBefore.getId(), responseAfter.getId());
        
        
        // the code below will cause the feedback to be deleted because
        // recipient's e-mail is the same as the one in studentEnrollDetails
        // and the question's recipient's type is own team members
        ______TS("adjust feedback response: delete after adjustment");
        
        studentDetails1 = new StudentEnrollDetails(StudentAttributes.UpdateStatus.MODIFIED,
                course1Id, student2InCourse1.email, student1InCourse1.team, student1InCourse1.team + "tmp", student1InCourse1.section, student1InCourse1.section + "tmp");
        enrollmentList = new ArrayList<StudentEnrollDetails>();
        enrollmentList.add(studentDetails1);
        
        feedbackQuestionInDb = fqLogic.getFeedbackQuestion(feedbackResponse1InBundle.feedbackSessionName, 
                feedbackResponse1InBundle.courseId, Integer.parseInt(feedbackResponse1InBundle.feedbackQuestionId));
        responseBefore = frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(),
                feedbackResponse1InBundle.giverEmail, feedbackResponse1InBundle.recipientEmail);
        
        studentsLogic.adjustFeedbackResponseForEnrollments(enrollmentList, responseBefore);
        
        responseAfter = frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(), 
                feedbackResponse1InBundle.giverEmail, feedbackResponse1InBundle.recipientEmail);
        assertEquals(null, responseAfter);
        
    }
    
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
        
        String headerLine = "Team  | Name | Email";
        String lineWithInvalidTeamName = invalidTeamName + "| John | john@email.tmt";
        String lineWithInvalidStudentName = "Team 1 |" + invalidStudentName + 
                "| student@email.tmt";
        String lineWithInvalidEmail = "Team 1 | James |" + "James_invalid_email.tmt";
        String lineWithInvalidStudentNameAndEmail = "Team 2 |" + invalidStudentName + 
                "|" + "student_invalid_email.tmt";
        String lineWithInvalidTeamNameAndEmail = invalidTeamName + "| Paul |" + 
                "Paul_invalid_email.tmt";
        String lineWithInvalidTeamNameAndStudentNameAndEmail = invalidTeamName + "|" + 
                invalidStudentName + "|" + "invalid_email.tmt";
        
        enrollLines = headerLine + Const.EOL + lineWithInvalidTeamName + Const.EOL + lineWithInvalidStudentName + Const.EOL +
                    lineWithInvalidEmail + Const.EOL + lineWithInvalidStudentNameAndEmail + Const.EOL +
                    lineWithInvalidTeamNameAndEmail + Const.EOL + lineWithInvalidTeamNameAndStudentNameAndEmail;
        
        invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);

        StudentAttributesFactory saf = new StudentAttributesFactory(headerLine);
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
               " studentWithExtraParameters@email.tmt | comment | extra_parameter";
        
        enrollLines = headerLine + Const.EOL + lineWithNoEmailInput + Const.EOL + lineWithExtraParameters;
        
        invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);

        expectedInvalidInfo.clear();
        expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithNoEmailInput, 
                StudentAttributesFactory.ERROR_ENROLL_LINE_TOOFEWPARTS));
        
        for (int i = 0; i < invalidInfo.size(); i++) {
            assertEquals(expectedInvalidInfo.get(i), invalidInfo.get(i));
        }
        
        ______TS("enrollLines with some empty fields");
       
        String lineWithTeamNameEmpty = "    | StudentWithTeamFieldEmpty | student@email.tmt";
        String lineWithStudentNameEmpty = "Team 5 |  | no_name@email.tmt";
        String lineWithEmailEmpty = "Team 5 | StudentWithEmailFieldEmpty | |";
        
        enrollLines = headerLine + Const.EOL + lineWithTeamNameEmpty + Const.EOL + lineWithStudentNameEmpty + Const.EOL + lineWithEmailEmpty;

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
        headerLine = "Team | Name | Email | Comment";
        String lineWithCorrectInput = "Team 3 | Mary | mary@email.tmt";
        String lineWithCorrectInputWithComment = "Team 4 | Benjamin | benjamin@email.tmt | Foreign student";
        
        enrollLines = headerLine + Const.EOL + lineWithCorrectInput + Const.EOL + lineWithCorrectInputWithComment;
        
        invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);

        assertEquals(0, invalidInfo.size());
        
        ______TS("enrollLines with only whitespaces");
        // not tested as enroll lines must be trimmed before passing to the method
        
        
        ______TS("enrollLines with duplicate emails");
        
        enrollLines = headerLine + Const.EOL + lineWithCorrectInput + Const.EOL + lineWithCorrectInput;
        
        invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);

        assertEquals(1, invalidInfo.size());
        
        
        ______TS("enrollLines with a mix of all above cases");
        enrollLines = headerLine + Const.EOL + lineWithInvalidTeamName + Const.EOL + lineWithInvalidTeamNameAndStudentNameAndEmail + 
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
    
    public void testEnrollStudents() throws Exception {
        
        String instructorId = "instructorForEnrollTesting";
        String courseIdForEnrollTest = "courseForEnrollTest";
        String instructorEmail = "instructor@email.tmt";      
        String EOL = Const.EOL;
        AccountAttributes accountToAdd = new AccountAttributes(instructorId, 
                "Instructor 1", true, instructorEmail, "TEAMMATES Test Institute 1",
                new StudentProfileAttributes(instructorId, "Ins1", "", "", "", "male", "", ""));
        
        accountsLogic.createAccount(accountToAdd);
        coursesLogic.createCourseAndInstructor(instructorId, courseIdForEnrollTest, "Course for Enroll Testing");
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        FeedbackSessionAttributes fsAttr = new FeedbackSessionAttributes("newFeedbackSessionName",
                courseIdForEnrollTest, instructorEmail, new Text("default instructions"),
                TimeHelper.getHoursOffsetToCurrentTime(0), TimeHelper.getHoursOffsetToCurrentTime(2), TimeHelper.getHoursOffsetToCurrentTime(5),
                TimeHelper.getHoursOffsetToCurrentTime(1), TimeHelper.getHoursOffsetToCurrentTime(6),
                8.0, 0, FeedbackSessionType.PRIVATE, false, false, false, false, false);
        fsLogic.createFeedbackSession(fsAttr);
        
        
        ______TS("all valid students, but contains blank lines and trailing spaces");
        
        String headerLine = "team | name | email | comment";
        String line0 = "t1   |  n1   |   e1@g  |   c1";
        String line1 = " t2|  n2|  e2@g|  c2";
        String line2 = "t3  |n3|  e3@g|c3  ";
        String line3 = "t4|n4|  e4@g|c4";
        String line4 = "t5|  n5|e5@g  |c5";
        String lines = headerLine + EOL + line0 + EOL + line1 + EOL + line2 + EOL
                    + "  \t \t \t \t           " + EOL + line3 + EOL + EOL + line4
                    + EOL + "    " + EOL + EOL;
        List<StudentAttributes> enrollResults = studentsLogic.enrollStudentsWithoutDocument(lines, courseIdForEnrollTest);
        
        StudentAttributesFactory saf = new StudentAttributesFactory(headerLine);
        assertEquals(5, enrollResults.size());
        assertEquals(5, studentsLogic.getStudentsForCourse(courseIdForEnrollTest).size());
        // Test enroll result
        line0 = "t1|n1|e1@g|c1";
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line0, courseIdForEnrollTest), 
                enrollResults.get(0), StudentAttributes.UpdateStatus.NEW);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line1, courseIdForEnrollTest),
                enrollResults.get(1), StudentAttributes.UpdateStatus.NEW);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line4, courseIdForEnrollTest),
                enrollResults.get(4), StudentAttributes.UpdateStatus.NEW);
            
        CourseDetailsBundle cd = coursesLogic.getCourseDetails(courseIdForEnrollTest);
        assertEquals(5, cd.stats.unregisteredTotal);
        
        
        ______TS("includes a mix of unmodified, modified, and new");
        
        String line0_1 = "t3|modified name|e3@g|c3";
        String line5 = "t6|n6|e6@g|c6";
        lines = headerLine + EOL + line0 + EOL + line0_1 + EOL + line1 + EOL + line5;
        enrollResults = studentsLogic.enrollStudentsWithoutDocument(lines, courseIdForEnrollTest);
        assertEquals(6, enrollResults.size());
        assertEquals(6, studentsLogic.getStudentsForCourse(courseIdForEnrollTest).size());
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line0, courseIdForEnrollTest),
                enrollResults.get(0), StudentAttributes.UpdateStatus.UNMODIFIED);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line0_1, courseIdForEnrollTest),
                enrollResults.get(1), StudentAttributes.UpdateStatus.MODIFIED);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line1, courseIdForEnrollTest),
                enrollResults.get(2), StudentAttributes.UpdateStatus.UNMODIFIED);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line5, courseIdForEnrollTest),
                enrollResults.get(3), StudentAttributes.UpdateStatus.NEW);
        assertEquals(StudentAttributes.UpdateStatus.NOT_IN_ENROLL_LIST,
                enrollResults.get(4).updateStatus);
        assertEquals(StudentAttributes.UpdateStatus.NOT_IN_ENROLL_LIST,
                enrollResults.get(5).updateStatus);
        
        
        ______TS("includes an incorrect line");
        
        // no changes should be done to the database
        String incorrectLine = "incorrectly formatted line";
        lines = headerLine + EOL +"t7|n7|e7@g|c7" + EOL + incorrectLine + EOL + line2 + EOL
                + line3;
        try {
            enrollResults = studentsLogic.enrollStudentsWithoutDocument(lines, courseIdForEnrollTest);
            signalFailureToDetectException("Did not throw exception for incorrectly formatted line");
        } catch (EnrollException e) {
            assertTrue(e.getMessage().contains(incorrectLine));
        }
        assertEquals(6, studentsLogic.getStudentsForCourse(courseIdForEnrollTest).size());
        
        
        ______TS("null parameters");
        
        try {
            studentsLogic.enrollStudentsWithoutDocument("a|b|c|d", null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        
        ______TS("same student added, modified and unmodified");
        
        accountToAdd = new AccountAttributes("tes.instructor", 
                "Instructor 1", true, "instructor@email.tmt", "TEAMMATES Test Institute 1",
                new StudentProfileAttributes("tes.instructor", "Ins 1", "", "", "", "male", "", ""));
        accountsLogic.createAccount(accountToAdd);
        coursesLogic.createCourseAndInstructor("tes.instructor", "tes.course", "TES Course");
            
        String line = headerLine + EOL + "t8|n8|e8@g|c1" ;
        enrollResults = studentsLogic.enrollStudentsWithoutDocument(line, "tes.course");
        assertEquals(1, enrollResults.size());
        assertEquals(StudentAttributes.UpdateStatus.NEW,
                enrollResults.get(0).updateStatus);
            
        line = headerLine + EOL + "t8|n8a|e8@g|c1";
        enrollResults = studentsLogic.enrollStudentsWithoutDocument(line, "tes.course");
        assertEquals(1, enrollResults.size());
        assertEquals(StudentAttributes.UpdateStatus.MODIFIED,
                enrollResults.get(0).updateStatus);
            
        line = headerLine + EOL + "t8|n8a|e8@g|c1";
        enrollResults = studentsLogic.enrollStudentsWithoutDocument(line, "tes.course");
        assertEquals(1, enrollResults.size());
        assertEquals(StudentAttributes.UpdateStatus.UNMODIFIED,
                enrollResults.get(0).updateStatus);

        ______TS("duplicated emails");
            
        String line_t9 = "t9|n9|e9@g|c9";
        String line_t10 = "t10|n10|e9@g|c10";
        lines = headerLine + EOL + line_t9 + EOL + line_t10;
        try {
            studentsLogic.enrollStudentsWithoutDocument(lines, "tes.course");
        } catch (EnrollException e) {
            assertTrue(e.getMessage().contains(line_t10));
            AssertHelper.assertContains("Same email address as the student in line \""+line_t9+"\"", e.getMessage());    
        }
        
        
        ______TS("invalid course id");
        
        String enrollLines = headerLine + EOL + "";
        String invalidCourseId = "invalidCourseId";
        try {
            studentsLogic.enrollStudentsWithoutDocument(enrollLines, invalidCourseId);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            ignoreExpectedException();
        }
        
        
        ______TS("empty enroll line");
        
        try {
            studentsLogic.enrollStudentsWithoutDocument("", courseIdForEnrollTest);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            ignoreExpectedException();
        }
        
        
        ______TS("invalidity info in enroll line");
        
        enrollLines = headerLine + EOL + "invalidline0\ninvalidline1\n";
        try {
            studentsLogic.enrollStudentsWithoutDocument(enrollLines, courseIdForEnrollTest);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            ignoreExpectedException();
        }
        
    }
   
    public void testGetStudentForEmail() throws Exception {

        ______TS("null parameters");

        try {
            studentsLogic.getStudentForEmail(null, "valid@email.tmt");
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        ______TS("non-exist student");
        
        String nonExistStudentEmail = "nonExist@google.tmt";
        String course1Id = dataBundle.courses.get("typicalCourse1").id;
        assertEquals(null, studentsLogic.getStudentForEmail(course1Id, nonExistStudentEmail));
        
        
        ______TS("typical case");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertEquals(student1InCourse1.googleId, studentsLogic.getStudentForEmail(course1Id, student1InCourse1.email).googleId);
    }
    
    public void testGetStudentForRegistrationKey() {
        
        ______TS("null parameter");

        try {
            studentsLogic.getStudentForRegistrationKey(null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        
        ______TS("non-exist student");
        
        String nonExistStudentKey = "nonExistKey"; 
        assertEquals(null, studentsLogic.getStudentForRegistrationKey(nonExistStudentKey));
        
        
        ______TS("typical case");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String course1Id = dataBundle.courses.get("typicalCourse1").id;
        String studentKey = studentsLogic.getStudentForCourseIdAndGoogleId(course1Id, student1InCourse1.googleId).key;
        StudentAttributes actualStudent = studentsLogic.getStudentForRegistrationKey(studentKey);
        assertEquals(student1InCourse1.googleId, actualStudent.googleId);
    }

    public void testGetStudentsForGoogleId() throws Exception {
    
        ______TS("student in one course");
    
        StudentAttributes studentInCourse1 = dataBundle.students.get("student1InCourse1");
        assertEquals(1, studentsLogic.getStudentsForGoogleId(studentInCourse1.googleId).size());
        assertEquals(studentInCourse1.email, 
                studentsLogic.getStudentsForGoogleId(studentInCourse1.googleId).get(0).email);
        assertEquals(studentInCourse1.name,
                studentsLogic.getStudentsForGoogleId(studentInCourse1.googleId).get(0).name);
        assertEquals(studentInCourse1.course,
                studentsLogic.getStudentsForGoogleId(studentInCourse1.googleId).get(0).course);
    
        
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
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }

    public void testGetStudentForCourseIdAndGoogleId() throws Exception {
    
        ______TS("student in two courses");
        
        StudentAttributes studentInTwoCoursesInCourse1 = dataBundle.students
                .get("student2InCourse1");
    
        String googleIdOfstudentInTwoCourses = studentInTwoCoursesInCourse1.googleId;
        assertEquals(studentInTwoCoursesInCourse1.email,
                studentsLogic.getStudentForCourseIdAndGoogleId(
                        studentInTwoCoursesInCourse1.course,
                        googleIdOfstudentInTwoCourses).email);
    
        StudentAttributes studentInTwoCoursesInCourse2 = dataBundle.students
                .get("student2InCourse2");
        assertEquals(studentInTwoCoursesInCourse2.email,
                studentsLogic.getStudentForCourseIdAndGoogleId(
                        studentInTwoCoursesInCourse2.course,
                        googleIdOfstudentInTwoCourses).email);
    
        ______TS("student in zero courses");
    
        assertEquals(null, studentsLogic.getStudentForCourseIdAndGoogleId("non-existent",
                "random-google-id"));
    
        ______TS("null parameters");
    
        try {
            studentsLogic.getStudentForCourseIdAndGoogleId("valid.course", null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }

    public void testGetStudentsForCourse() throws Exception {
        
        ______TS("course with multiple students");
    
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
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    
        ______TS("non-existent course");
    
        studentList = studentsLogic.getStudentsForCourse("non-existent");
        assertEquals(0, studentList.size());
        
    }

    public void testGetKeyForStudent() throws Exception {
    
        ______TS("null parameters");
    
        try {
            studentsLogic.getKeyForStudent("valid.course.id", null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    
        
        ______TS("non-existent student");
        
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String nonExistStudentEmail = "non@existent";
        try {
            studentsLogic.getKeyForStudent(student1InCourse1.course, nonExistStudentEmail);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            String expectedErrorMsg = "Student does not exist: [" + student1InCourse1.course + "/" + nonExistStudentEmail + "]";
            assertEquals(expectedErrorMsg, e.getMessage());
        }
        
        
        // the typical case below seems unnecessary though--it is not useful for now
        // as the method itself is too simple
        ______TS("typical case");
        
        String course1Id = dataBundle.courses.get("typicalCourse1").id;
        String actualKey = studentsLogic.getKeyForStudent(course1Id, student1InCourse1.email);
        String expectedKey = studentsLogic.getStudentForCourseIdAndGoogleId(course1Id, student1InCourse1.googleId).key;
        assertEquals(expectedKey, actualKey);
    }
    
    public void testGetEncryptedKeyForStudent() throws Exception {
        
        ______TS("null parameters");
        
        try {
            studentsLogic.getEncryptedKeyForStudent("valid.course.id", null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    
        
        ______TS("non-existent student");
        
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String nonExistStudentEmail = "non@existent";
        try {
            studentsLogic.getEncryptedKeyForStudent(student1InCourse1.course, nonExistStudentEmail);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            String expectedErrorMsg = "Student does not exist: [" + student1InCourse1.course + "/" + nonExistStudentEmail + "]";
            assertEquals(expectedErrorMsg, e.getMessage());
        }
        
        
        // the typical case below seems unnecessary though--it is not useful for now
        // as the method itself is too simple
        ______TS("typical case");
        
        String course1Id = dataBundle.courses.get("typicalCourse1").id;
        String actualKey = studentsLogic.getEncryptedKeyForStudent(course1Id, student1InCourse1.email);
        String expectedKey = StringHelper.encrypt(studentsLogic.getStudentForCourseIdAndGoogleId(course1Id, student1InCourse1.googleId).key);
        assertEquals(expectedKey, actualKey);
    }
    
    public void testIsStudentInAnyCourse() {
        
        ______TS("non-existent student");
        
        String nonExistStudentGoogleId = "nonExistGoogleId";
        assertFalse(studentsLogic.isStudentInAnyCourse(nonExistStudentGoogleId));
        
        
        ______TS("typical case");
        
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertTrue(studentsLogic.isStudentInAnyCourse(student1InCourse1.googleId));
    }
    
    public void testIsStudentInCourse() {
        
        ______TS("non-existent student");
        
        String nonExistStudentEmail = "nonExist@google.tmt";
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        assertFalse(studentsLogic.isStudentInCourse(course1.id, nonExistStudentEmail));
        
        
        ______TS("typical case");
        
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertTrue(studentsLogic.isStudentInCourse(course1.id, student1InCourse1.email));
    }
    
    public void testIsStudentInTeam() {
        
        ______TS("non-existent student");
        
        String nonExistStudentEmail = "nonExist@google.tmt";
        String teamName = "Team 1";
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        assertFalse(studentsLogic.isStudentInTeam(course1.id, teamName, nonExistStudentEmail));
        
        
        ______TS("student not in given team");
        
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertFalse(studentsLogic.isStudentInTeam(course1.id, teamName, nonExistStudentEmail));
        
        ______TS("typical case");
        teamName = student1InCourse1.team;
        assertTrue(studentsLogic.isStudentInTeam(course1.id, teamName, student1InCourse1.email));
    }
    
    public void testIsStudentsInSameTeam() {
        
        ______TS("non-existent student1");
        
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        StudentAttributes student2InCourse1 = dataBundle.students.get("student2InCourse1");
        String nonExistStudentEmail = "nonExist@google.tmt";
        assertFalse(studentsLogic.isStudentsInSameTeam(course1.id, nonExistStudentEmail, student2InCourse1.email));
        
        
        ______TS("students of different teams");
        
        StudentAttributes student5InCourse1 = dataBundle.students.get("student5InCourse1");
        assertFalse(studentsLogic.isStudentsInSameTeam(course1.id, student2InCourse1.email, student5InCourse1.email));
        
        
        ______TS("students of different teams");     
        
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertTrue(studentsLogic.isStudentsInSameTeam(course1.id, student2InCourse1.email, student1InCourse1.email));
        
    }

    public void testSendRegistrationInviteForCourse() throws Exception {
        
        ______TS("all students already registered");
   
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
    
        // send registration key to a class in which all are registered
        List<MimeMessage> emailsSent = studentsLogic
                .sendRegistrationInviteForCourse(course1.id);
        assertEquals(0, emailsSent.size());
        
        ______TS("typical case: send invite to one student");
        StudentAttributes student2InCourse1 = dataBundle.students.get("student2InCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        
        String courseId = dataBundle.courses.get("typicalCourse1").id;
        StudentAttributes newsStudent0Info = new StudentAttributes("sect", "team", "n0", "e0@google.tmt", "", courseId);
        StudentAttributes newsStudent1Info = new StudentAttributes("sect", "team", "n1", "e1@google.tmt", "", courseId);
        StudentAttributes newsStudent2Info = new StudentAttributes("sect", "team", "n2", "e2@google.tmt", "", courseId);
        invokeEnrollStudent(newsStudent0Info);
        invokeEnrollStudent(newsStudent1Info);
        invokeEnrollStudent(newsStudent2Info);

        List<MimeMessage> msgsForCourse = studentsLogic.sendRegistrationInviteForCourse(courseId);
        assertEquals(3, msgsForCourse.size());
        Emails emailMgr = new Emails();
        @SuppressWarnings("static-access")
        String emailInfo0 = emailMgr.getEmailInfo(msgsForCourse.get(0));
        String expectedEmailInfoForEmail0 = "[Email sent]to=e0@google.tmt|from=TEAMMATES Admin " + 
                "<Admin@null.appspotmail.com>|subject=TEAMMATES: Invitation to join course " + 
                "[Typical Course 1 with 2 Evals][Course ID: idOfTypicalCourse1]";
        assertEquals(expectedEmailInfoForEmail0, emailInfo0);
        @SuppressWarnings("static-access")
        String emailInfo1 = emailMgr.getEmailInfo(msgsForCourse.get(1));
        String expectedEmailInfoForEmail1 = "[Email sent]to=e1@google.tmt|from=TEAMMATES Admin " + 
                "<Admin@null.appspotmail.com>|subject=TEAMMATES: Invitation to join course " + 
                "[Typical Course 1 with 2 Evals][Course ID: idOfTypicalCourse1]";
        assertEquals(expectedEmailInfoForEmail1, emailInfo1);
        @SuppressWarnings("static-access")
        String emailInfo2 = emailMgr.getEmailInfo(msgsForCourse.get(2));
        String expectedEmailInfoForEmail2 = "[Email sent]to=e2@google.tmt|from=" + 
                "TEAMMATES Admin <Admin@null.appspotmail.com>|subject=TEAMMATES:" + 
                " Invitation to join course [Typical Course 1 with 2 Evals][Course ID: idOfTypicalCourse1]";
        assertEquals(expectedEmailInfoForEmail2, emailInfo2);
        
        studentsLogic.updateStudentCascadeWithoutDocument(student1InCourse1.email, student1InCourse1);
        studentsLogic.updateStudentCascadeWithoutDocument(student2InCourse1.email, student2InCourse1);
        studentsLogic.deleteStudentCascadeWithoutDocument(newsStudent0Info.course, newsStudent0Info.email);
        studentsLogic.deleteStudentCascadeWithoutDocument(newsStudent1Info.course, newsStudent1Info.email);
        studentsLogic.deleteStudentCascadeWithoutDocument(newsStudent2Info.course, newsStudent2Info.email);
    
        ______TS("null parameters");
    
        try {
            studentsLogic.sendRegistrationInviteForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    public void testDeleteStudent() throws Exception {

        ______TS("typical delete");

        // this is the student to be deleted
        StudentAttributes student2InCourse1 = dataBundle.students.get("student2InCourse1");
        TestHelper.verifyPresentInDatastore(student2InCourse1);

        studentsLogic.deleteStudentCascadeWithoutDocument(student2InCourse1.course, student2InCourse1.email);
        TestHelper.verifyAbsentInDatastore(student2InCourse1);

        // verify that other students in the course are intact
        
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        TestHelper.verifyPresentInDatastore(student1InCourse1);

        // verify comments made to this student are gone
        TestHelper.verifyAbsentInDatastore(dataBundle.comments.get("comment1FromI3C1toS2C1"));

        ______TS("delete non-existent student");

        // should fail silently.
        studentsLogic.deleteStudentCascadeWithoutDocument(student2InCourse1.course, student2InCourse1.email);

        ______TS("null parameters");

        try {
            studentsLogic.deleteStudentCascadeWithoutDocument(null, "valid@email.tmt");
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }

    private static StudentEnrollDetails invokeEnrollStudent(StudentAttributes student)
            throws Exception {
        Method privateMethod = StudentsLogic.class.getDeclaredMethod("enrollStudent",
                new Class[] { StudentAttributes.class, Boolean.class });
        privateMethod.setAccessible(true);
        Object[] params = new Object[] { student, new Boolean(false) };
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
        AccountsLogic.inst().deleteAccountCascade(dataBundle.students.get("student4InCourse1").googleId);
        printTestClassFooter();
        
        turnLoggingDown(StudentsLogic.class);
    }

}
