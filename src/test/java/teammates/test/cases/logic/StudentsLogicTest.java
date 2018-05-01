package teammates.test.cases.logic;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.CourseEnrollmentResult;
import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.StudentUpdateStatus;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.StudentsDb;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.StringHelperExtension;
import teammates.test.driver.TimeHelperExtension;

/**
 * SUT: {@link StudentsLogic}.
 */
public class StudentsLogicTest extends BaseLogicTest {

    private static StudentsLogic studentsLogic = StudentsLogic.inst();
    private static AccountsLogic accountsLogic = AccountsLogic.inst();
    private static CoursesLogic coursesLogic = CoursesLogic.inst();

    @Test
    public void testAll() throws Exception {

        testGetStudentProfile();
        testGetStudentForEmail();
        testGetStudentForRegistrationKey();
        testGetStudentsForGoogleId();
        testGetStudentForCourseIdAndGoogleId();
        testGetStudentsForCourse();
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
        testEnrollLinesChecking();
        testEnrollStudents();

        testDeleteStudent();

    }

    /*
     * NOTE: enrollStudents() tested in SubmissionsAdjustmentTest.
     * This is because it uses Task Queues for scheduling and therefore has to be
     * tested in a separate class.
     */

    private void testGetTeamForStudent() {
        ______TS("Typical case: get team of existing student");

        String courseId = "idOfTypicalCourse1";
        String googleId = "student1InCourse1";
        StudentAttributes student = StudentsLogic.inst().getStudentForCourseIdAndGoogleId(courseId, googleId);
        TeamDetailsBundle team = StudentsLogic.inst().getTeamDetailsForStudent(student);

        assertEquals("Team 1.1</td></div>'\"", team.name);
        assertNotNull(team.students);
        assertEquals(4, team.students.size());

        ______TS("Typical case: get team of non-existing student");
        courseId = "idOfTypicalCourse1";
        googleId = "idOfNonExistingStudent";
        student = StudentsLogic.inst().getStudentForCourseIdAndGoogleId(courseId, googleId);
        team = StudentsLogic.inst().getTeamDetailsForStudent(student);

        assertNull(team);

    }

    private void testEnrollStudent() throws Exception {

        String instructorId = "instructorForEnrollTesting";
        String instructorCourse = "courseForEnrollTesting";

        //delete leftover data, if any
        accountsLogic.deleteAccountCascade(instructorId);
        coursesLogic.deleteCourseCascade(instructorCourse);

        //create fresh test data
        accountsLogic.createAccount(
                AccountAttributes.builder()
                        .withGoogleId(instructorId)
                        .withName("ICET Instr Name")
                        .withEmail("instructor@icet.tmt")
                        .withInstitute("TEAMMATES Test Institute 1")
                        .withIsInstructor(true)
                        .withStudentProfileAttributes(StudentProfileAttributes.builder(instructorId)
                                .withShortName("ICET")
                                .build())
                        .build());
        coursesLogic.createCourseAndInstructor(instructorId, instructorCourse, "Course for Enroll Testing", "UTC");

        ______TS("add student into empty course");

        StudentAttributes student1 = StudentAttributes
                .builder(instructorCourse, "n", "e@g")
                .withSection("sect 1")
                .withTeam("t1")
                .withComments("c")
                .build();

        // check if the course is empty
        assertEquals(0, studentsLogic.getStudentsForCourse(instructorCourse).size());

        // add a new student and verify it is added and treated as a new student
        StudentEnrollDetails enrollmentResult = enrollStudent(student1);
        assertEquals(1, studentsLogic.getStudentsForCourse(instructorCourse).size());
        verifyEnrollmentDetailsForStudent(student1, null, enrollmentResult,
                StudentUpdateStatus.NEW);
        verifyPresentInDatastore(student1);

        ______TS("add existing student");

        // Verify it was not added
        enrollmentResult = enrollStudent(student1);
        verifyEnrollmentDetailsForStudent(student1, null, enrollmentResult,
                StudentUpdateStatus.UNMODIFIED);
        assertEquals(1, studentsLogic.getStudentsForCourse(instructorCourse).size());

        ______TS("add student into non-empty course");
        StudentAttributes student2 = StudentAttributes
                .builder(instructorCourse, "n2", "e2@g")
                .withSection("sect 1")
                .withTeam("t1")
                .withComments("c")
                .build();
        enrollmentResult = enrollStudent(student2);
        verifyEnrollmentDetailsForStudent(student2, null, enrollmentResult,
                StudentUpdateStatus.NEW);

        //add some more students to the same course (we add more than one
        //  because we can use them for testing cascade logic later in this test case)
        enrollStudent(StudentAttributes
                .builder(instructorCourse, "n3", "e3@g")
                .withSection("sect 2")
                .withTeam("t2")
                .withComments("c")
                .build());
        enrollStudent(StudentAttributes
                .builder(instructorCourse, "n4", "e4@g")
                .withSection("sect 2")
                .withTeam("t2")
                .withComments("")
                .build());
        assertEquals(4, studentsLogic.getStudentsForCourse(instructorCourse).size());

        ______TS("modify info of existing student");
        //add some more details to the student
        student1.googleId = "googleId";
        studentsLogic.updateStudentCascadeWithoutDocument(student1.email, student1);

    }

    private void testGetStudentProfile() throws Exception {

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

        StudentProfileAttributes expectedStudentProfile = StudentProfileAttributes.builder(student1.googleId).build();

        expectedStudentProfile.shortName = "short";
        expectedStudentProfile.email = "personal@email.tmt";
        expectedStudentProfile.institute = "institute";
        expectedStudentProfile.nationality = "Angolan";
        expectedStudentProfile.gender = "female";
        expectedStudentProfile.moreInfo = "This sentence may sound sound but it cannot make actual sound... :P";

        student1.studentProfile = expectedStudentProfile;
        accountsLogic.updateAccount(student1, true);

        StudentProfileAttributes actualStudentProfile = studentsLogic.getStudentProfile(student1InCourse1.googleId);
        expectedStudentProfile.modifiedDate = actualStudentProfile.modifiedDate;
        assertEquals(expectedStudentProfile.toString(), actualStudentProfile.toString());
    }

    private void testValidateSections() throws Exception {

        CourseAttributes typicalCourse1 = dataBundle.courses.get("typicalCourse1");
        String courseId = typicalCourse1.getId();

        ______TS("Typical case");

        List<StudentAttributes> studentList = new ArrayList<>();
        studentList.add(StudentAttributes
                .builder(courseId, "New Student", "emailNew@com")
                .withSection("Section 3")
                .withTeam("Team 1.3")
                .withComments("")
                .build());
        studentList.add(StudentAttributes
                .builder(courseId, "student2 In Course1", "student2InCourse1@gmail.tmt")
                .withSection("Section 2")
                .withTeam("Team 1.4")
                .withComments("")
                .build());

        studentsLogic.validateSectionsAndTeams(studentList, courseId);

        ______TS("Failure case: invalid section");

        studentList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            StudentAttributes addedStudent = StudentAttributes
                    .builder(courseId, "Name " + i, "email@com" + i)
                    .withSection("Section 1")
                    .withTeam("Team " + i)
                    .withComments("cmt" + i)
                    .build();
            studentList.add(addedStudent);
        }
        try {
            studentsLogic.validateSectionsAndTeams(studentList, courseId);
        } catch (EnrollException e) {
            assertEquals(String.format(Const.StatusMessages.SECTION_QUOTA_EXCEED, "Section 1"), e.getMessage());
        }

        ______TS("Failure case: invalid team");

        studentList = new ArrayList<>();
        studentList.add(StudentAttributes
                .builder(courseId, "New Student", "newemail@com")
                .withSection("Section 2")
                .withTeam("Team 1.1")
                .withComments("")
                .build());
        try {
            studentsLogic.validateSectionsAndTeams(studentList, courseId);
        } catch (EnrollException e) {
            assertEquals(String.format(Const.StatusMessages.TEAM_INVALID_SECTION_EDIT, "Team 1.1</td></div>'\"")
                             + "Please use the enroll page to edit multiple students",
                         e.getMessage());
        }
    }

    private void testupdateStudentCascadeWithoutDocument() throws Exception {

        ______TS("typical edit");

        StudentAttributes student4InCourse1 = dataBundle.students.get("student4InCourse1");
        verifyPresentInDatastore(student4InCourse1);
        String originalEmail = student4InCourse1.email;
        student4InCourse1 = studentsLogic.getStudentForEmail(student4InCourse1.course, student4InCourse1.email);
        student4InCourse1.name = student4InCourse1.name + "y";
        student4InCourse1.googleId = student4InCourse1.googleId + "y";
        student4InCourse1.comments = student4InCourse1.comments + "y";
        student4InCourse1.email = student4InCourse1.email + "y";
        student4InCourse1.section = "Section 2";
        student4InCourse1.team = "Team 1.2"; // move to a different team

        studentsLogic.updateStudentCascadeWithoutDocument(originalEmail, student4InCourse1);
        StudentAttributes updatedStudent4InCourse1 =
                studentsLogic.getStudentForEmail(student4InCourse1.course, student4InCourse1.email);
        assertFalse(student4InCourse1.getUpdatedAt().equals(updatedStudent4InCourse1.getUpdatedAt()));

        ______TS("check for KeepExistingPolicy : change email only");

        originalEmail = student4InCourse1.email;
        String newEmail = student4InCourse1.email + "y";
        student4InCourse1.email = newEmail;

        // create an empty student and then copy course and email attributes
        StudentAttributes copyOfStudent1 = StudentAttributes
                .builder(student4InCourse1.course, student4InCourse1.name, newEmail)
                .build();
        student4InCourse1.googleId = "";
        student4InCourse1.section = "None";

        studentsLogic.updateStudentCascadeWithoutDocument(originalEmail, copyOfStudent1);
        verifyPresentInDatastore(student4InCourse1);

        ______TS("check for KeepExistingPolicy : change nothing");

        originalEmail = student4InCourse1.email;
        copyOfStudent1.email = null;
        studentsLogic.updateStudentCascadeWithoutDocument(originalEmail, copyOfStudent1);
        verifyPresentInDatastore(copyOfStudent1);

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

    private void testAdjustFeedbackResponseForEnrollments() throws Exception {

        // the case below will not cause the response to be deleted
        // because the studentEnrollDetails'email is not the same as giver or recipient
        ______TS("adjust feedback response: no change of team");

        String course1Id = dataBundle.courses.get("typicalCourse1").getId();
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        StudentAttributes student2InCourse1 = dataBundle.students.get("student2InCourse1");
        ArrayList<StudentEnrollDetails> enrollmentList = new ArrayList<>();
        StudentEnrollDetails studentDetails1 =
                new StudentEnrollDetails(StudentUpdateStatus.MODIFIED,
                                         course1Id, student1InCourse1.email, student1InCourse1.team,
                                         student1InCourse1.team + "tmp", student1InCourse1.section,
                                         student1InCourse1.section + "tmp");
        enrollmentList.add(studentDetails1);

        FeedbackResponseAttributes feedbackResponse1InBundle = dataBundle.feedbackResponses.get("response1ForQ2S2C1");
        FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
        FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
        FeedbackQuestionAttributes feedbackQuestionInDb =
                fqLogic.getFeedbackQuestion(feedbackResponse1InBundle.feedbackSessionName,
                                            feedbackResponse1InBundle.courseId,
                                            Integer.parseInt(feedbackResponse1InBundle.feedbackQuestionId));
        FeedbackResponseAttributes responseBefore =
                frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(),
                                            feedbackResponse1InBundle.giver,
                                            feedbackResponse1InBundle.recipient);

        studentsLogic.adjustFeedbackResponseForEnrollments(enrollmentList, responseBefore);

        FeedbackResponseAttributes responseAfter = frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(),
                feedbackResponse1InBundle.giver, feedbackResponse1InBundle.recipient);
        assertEquals(responseBefore.getId(), responseAfter.getId());

        // the case below will not cause the response to be deleted
        // because the studentEnrollDetails'email is not the same as giver or recipient
        ______TS("adjust feedback response: unmodified status");

        enrollmentList = new ArrayList<>();
        studentDetails1 =
                new StudentEnrollDetails(StudentUpdateStatus.UNMODIFIED, course1Id,
                                         student1InCourse1.email, student1InCourse1.team,
                                         student1InCourse1.team + "tmp", student1InCourse1.section,
                                         student1InCourse1.section + "tmp");
        enrollmentList.add(studentDetails1);

        feedbackQuestionInDb = fqLogic.getFeedbackQuestion(feedbackResponse1InBundle.feedbackSessionName,
                feedbackResponse1InBundle.courseId, Integer.parseInt(feedbackResponse1InBundle.feedbackQuestionId));
        responseBefore = frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(),
                feedbackResponse1InBundle.giver, feedbackResponse1InBundle.recipient);

        studentsLogic.adjustFeedbackResponseForEnrollments(enrollmentList, responseBefore);

        responseAfter = frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(),
                feedbackResponse1InBundle.giver, feedbackResponse1InBundle.recipient);
        assertEquals(responseBefore.getId(), responseAfter.getId());

        // the code below will cause the feedback to be deleted because
        // recipient's e-mail is the same as the one in studentEnrollDetails
        // and the question's recipient's type is own team members
        ______TS("adjust feedback response: delete after adjustment");

        studentDetails1 =
                new StudentEnrollDetails(StudentUpdateStatus.MODIFIED, course1Id,
                                         student2InCourse1.email, student1InCourse1.team,
                                         student1InCourse1.team + "tmp", student1InCourse1.section,
                                         student1InCourse1.section + "tmp");
        enrollmentList = new ArrayList<>();
        enrollmentList.add(studentDetails1);

        feedbackQuestionInDb = fqLogic.getFeedbackQuestion(feedbackResponse1InBundle.feedbackSessionName,
                feedbackResponse1InBundle.courseId, Integer.parseInt(feedbackResponse1InBundle.feedbackQuestionId));
        responseBefore = frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(),
                feedbackResponse1InBundle.giver, feedbackResponse1InBundle.recipient);

        studentsLogic.adjustFeedbackResponseForEnrollments(enrollmentList, responseBefore);

        responseAfter = frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(),
                feedbackResponse1InBundle.giver, feedbackResponse1InBundle.recipient);
        assertNull(responseAfter);

    }

    private void testEnrollLinesChecking() throws Exception {
        String info;
        String enrollLines;
        String courseId = "CourseID";
        coursesLogic.createCourse(courseId, "CourseName", "UTC");
        String invalidInfoString = null;
        String expectedInvalidInfoString;
        List<String> expectedInvalidInfoList = new ArrayList<>();

        ______TS("enrollLines with invalid parameters");
        String invalidTeamName = StringHelperExtension.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1);
        String invalidStudentName = StringHelperExtension.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);

        String headerLine = "Team | Name | Email";
        String lineWithInvalidTeamName = invalidTeamName + "| John | john@email.tmt";
        String lineWithInvalidStudentName = "Team 1 |" + invalidStudentName + "| student@email.tmt";
        String lineWithInvalidEmail = "Team 1 | James |" + "James_invalid_email.tmt";
        String lineWithInvalidStudentNameAndEmail =
                "Team 2 |" + invalidStudentName + "|" + "student_invalid_email.tmt";
        String lineWithInvalidTeamNameAndEmail = invalidTeamName + "| Paul |" + "Paul_invalid_email.tmt";
        String lineWithInvalidTeamNameAndStudentNameAndEmail =
                invalidTeamName + "|" + invalidStudentName + "|" + "invalid_email.tmt";

        enrollLines = headerLine + System.lineSeparator()
                + lineWithInvalidTeamName + System.lineSeparator()
                + lineWithInvalidStudentName + System.lineSeparator()
                + lineWithInvalidEmail + System.lineSeparator()
                + lineWithInvalidStudentNameAndEmail + System.lineSeparator()
                + lineWithInvalidTeamNameAndEmail + System.lineSeparator()
                + lineWithInvalidTeamNameAndStudentNameAndEmail;

        invalidInfoString = getExceptionMessageOnCreatingStudentsList(enrollLines, courseId);

        StudentAttributesFactory saf = new StudentAttributesFactory(headerLine);
        expectedInvalidInfoList.clear();
        info = StringHelper.toString(
                    SanitizationHelper.sanitizeForHtml(
                        saf.makeStudent(lineWithInvalidTeamName, courseId).getInvalidityInfo()),
                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfoList.add(
                String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamName, info));

        info = StringHelper.toString(
                    SanitizationHelper.sanitizeForHtml(
                        saf.makeStudent(lineWithInvalidStudentName, courseId).getInvalidityInfo()),
                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfoList.add(
                String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidStudentName, info));

        info = StringHelper.toString(
                    SanitizationHelper.sanitizeForHtml(
                        saf.makeStudent(lineWithInvalidEmail, courseId).getInvalidityInfo()),
                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfoList.add(
                String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidEmail, info));

        info = StringHelper.toString(
                    SanitizationHelper.sanitizeForHtml(
                        saf.makeStudent(lineWithInvalidStudentNameAndEmail, courseId).getInvalidityInfo()),
                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfoList.add(
                String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidStudentNameAndEmail, info));

        info = StringHelper.toString(
                    SanitizationHelper.sanitizeForHtml(
                        saf.makeStudent(lineWithInvalidTeamNameAndEmail, courseId).getInvalidityInfo()),
                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfoList.add(
                String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamNameAndEmail, info));

        info = StringHelper.toString(
                    SanitizationHelper.sanitizeForHtml(
                        saf.makeStudent(lineWithInvalidTeamNameAndStudentNameAndEmail, courseId).getInvalidityInfo()),
                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfoList.add(
                String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM,
                              lineWithInvalidTeamNameAndStudentNameAndEmail, info));

        expectedInvalidInfoString = StringHelper.toString(expectedInvalidInfoList, "<br>");
        assertEquals(expectedInvalidInfoString, invalidInfoString);

        ______TS("enrollLines with too few");
        String lineWithNoEmailInput = "Team 4 | StudentWithNoEmailInput";
        String lineWithExtraParameters = "Team 4 | StudentWithExtraParameters | "
                + " studentWithExtraParameters@email.tmt | comment | extra_parameter";

        enrollLines = headerLine + System.lineSeparator()
                + lineWithNoEmailInput + System.lineSeparator()
                + lineWithExtraParameters;

        invalidInfoString = getExceptionMessageOnCreatingStudentsList(enrollLines, courseId);

        expectedInvalidInfoList.clear();
        expectedInvalidInfoList.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithNoEmailInput,
                StudentAttributesFactory.ERROR_ENROLL_LINE_TOOFEWPARTS));

        expectedInvalidInfoString = StringHelper.toString(expectedInvalidInfoList, "<br>");
        assertEquals(expectedInvalidInfoString, invalidInfoString);

        ______TS("enrollLines with some empty fields");

        String lineWithTeamNameEmpty = " | StudentWithTeamFieldEmpty | student@email.tmt";
        String lineWithStudentNameEmpty = "Team 5 |  | no_name@email.tmt";
        String lineWithEmailEmpty = "Team 5 | StudentWithEmailFieldEmpty | |";

        enrollLines = headerLine + System.lineSeparator()
                + lineWithTeamNameEmpty + System.lineSeparator()
                + lineWithStudentNameEmpty + System.lineSeparator()
                + lineWithEmailEmpty;

        invalidInfoString = getExceptionMessageOnCreatingStudentsList(enrollLines, courseId);

        expectedInvalidInfoList.clear();
        info = StringHelper.toString(
                SanitizationHelper.sanitizeForHtml(saf.makeStudent(lineWithTeamNameEmpty, courseId).getInvalidityInfo()),
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfoList.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithTeamNameEmpty, info));
        info = StringHelper.toString(
                SanitizationHelper.sanitizeForHtml(saf.makeStudent(lineWithStudentNameEmpty, courseId).getInvalidityInfo()),
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfoList.add(
                String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithStudentNameEmpty, info));
        info = StringHelper.toString(
                SanitizationHelper.sanitizeForHtml(saf.makeStudent(lineWithEmailEmpty, courseId).getInvalidityInfo()),
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfoList.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithEmailEmpty, info));

        expectedInvalidInfoString = StringHelper.toString(expectedInvalidInfoList, "<br>");
        assertEquals(expectedInvalidInfoString, invalidInfoString);

        ______TS("enrollLines with correct input");
        headerLine = "Team | Name | Email | Comment";
        String lineWithCorrectInput = "Team 3 | Mary | mary@email.tmt";
        String lineWithCorrectInputWithComment = "Team 4 | Benjamin | benjamin@email.tmt | Foreign student";

        enrollLines = headerLine + System.lineSeparator()
                + lineWithCorrectInput + System.lineSeparator()
                + lineWithCorrectInputWithComment;
        // No exception is supposed be thrown here. Test will fail if Enrollment Exception is thrown
        studentsLogic.createStudents(enrollLines, courseId);

        ______TS("enrollLines with only whitespaces");
        // not tested as enroll lines must be trimmed before passing to the method

        ______TS("enrollLines with duplicate emails");

        enrollLines = headerLine + System.lineSeparator()
                + lineWithCorrectInput + System.lineSeparator()
                + lineWithCorrectInput;

        invalidInfoString = getExceptionMessageOnCreatingStudentsList(enrollLines, courseId);

        expectedInvalidInfoString = "Same email address as the student in line \"" + lineWithCorrectInput + "\"";
        AssertHelper.assertContains(expectedInvalidInfoString, invalidInfoString);

        ______TS("enrollLines with a mix of all above cases");
        enrollLines = headerLine + System.lineSeparator()
                + lineWithInvalidTeamName + System.lineSeparator()
                + lineWithInvalidTeamNameAndStudentNameAndEmail + System.lineSeparator()
                + lineWithExtraParameters + System.lineSeparator()
                + lineWithTeamNameEmpty + System.lineSeparator()
                + lineWithCorrectInput + System.lineSeparator() + "\t";

        invalidInfoString = getExceptionMessageOnCreatingStudentsList(enrollLines, courseId);

        expectedInvalidInfoList.clear();

        info = StringHelper.toString(
                    SanitizationHelper.sanitizeForHtml(
                        saf.makeStudent(lineWithInvalidTeamName, courseId).getInvalidityInfo()),
                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfoList.add(
                String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamName, info));

        info = StringHelper.toString(
                    SanitizationHelper.sanitizeForHtml(
                        saf.makeStudent(lineWithInvalidTeamNameAndStudentNameAndEmail, courseId).getInvalidityInfo()),
                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfoList.add(
                String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM,
                              lineWithInvalidTeamNameAndStudentNameAndEmail, info));

        info = StringHelper.toString(
                    SanitizationHelper.sanitizeForHtml(
                        saf.makeStudent(lineWithTeamNameEmpty, courseId).getInvalidityInfo()),
                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        expectedInvalidInfoList.add(
                String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithTeamNameEmpty, info));

        expectedInvalidInfoString = StringHelper.toString(expectedInvalidInfoList, "<br>");
        assertEquals(expectedInvalidInfoString, invalidInfoString);
    }

    /**
     * Returns the error message of EnrollException thrown when trying to call
     * {@link StudentsLogic#createStudents(String, String)} method with
     * {@code invalidEnrollLines}. This method assumes that an EnrollException is thrown, else this method fails with
     * {@link #signalFailureToDetectException()}.
     *
     * @param invalidEnrollLines is assumed to be invalid
     */
    private String getExceptionMessageOnCreatingStudentsList(String invalidEnrollLines, String courseId) {
        String invalidInfoString = null;
        try {
            studentsLogic.createStudents(invalidEnrollLines, courseId);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            invalidInfoString = e.getMessage();
        }
        return invalidInfoString;
    }

    private void testEnrollStudents() throws Exception {

        String instructorId = "instructorForEnrollTesting";
        String courseIdForEnrollTest = "courseForEnrollTest";
        String instructorEmail = "instructor@email.tmt";
        StudentProfileAttributes profileAttributes = StudentProfileAttributes.builder(instructorId)
                .withShortName("Ins1").withGender("male")
                .build();
        AccountAttributes accountToAdd = AccountAttributes.builder()
                .withGoogleId(instructorId)
                .withName("Instructor 1")
                .withEmail(instructorEmail)
                .withInstitute("TEAMMATES Test Institute 1")
                .withIsInstructor(true)
                .withStudentProfileAttributes(profileAttributes)
                .build();

        accountsLogic.createAccount(accountToAdd);
        coursesLogic.createCourseAndInstructor(instructorId, courseIdForEnrollTest, "Course for Enroll Testing", "UTC");
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

        FeedbackSessionAttributes fsAttr = FeedbackSessionAttributes
                .builder("newFeedbackSessionName", courseIdForEnrollTest, instructorEmail)
                .withInstructions(new Text("default instructions"))
                .withCreatedTime(Instant.now())
                .withStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(2))
                .withEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(5))
                .withSessionVisibleFromTime(TimeHelperExtension.getInstantHoursOffsetFromNow(1))
                .withResultsVisibleFromTime(TimeHelperExtension.getInstantHoursOffsetFromNow(6))
                .withTimeZone(ZoneId.of("Asia/Singapore"))
                .withGracePeriodMinutes(0)
                .withOpeningEmailEnabled(false)
                .withClosingEmailEnabled(false)
                .withPublishedEmailEnabled(false)
                .build();
        fsLogic.createFeedbackSession(fsAttr);

        ______TS("all valid students, but contains blank lines and trailing spaces");

        String headerLine = "team | name | email | comment";
        String line0 = "t1   |  n1   |   e1@g  |   c1";
        String line1 = " t2|  n2|  e2@g|  c2";
        String line2 = "\u00A0t3  |n3|  e3@g|c3  ";
        String line3 = "t4|n4|  e4@g|c4";
        String line4 = "t5|  n5|e5@g  |c5";
        String lines = headerLine + System.lineSeparator()
                + line0 + System.lineSeparator()
                + line1 + System.lineSeparator()
                + line2 + System.lineSeparator()
                + "  \t \t \t \t           " + System.lineSeparator()
                + line3 + System.lineSeparator() + System.lineSeparator()
                + line4 + System.lineSeparator()
                + "    " + System.lineSeparator() + System.lineSeparator();
        CourseEnrollmentResult enrollResults = studentsLogic.enrollStudentsWithoutDocument(lines, courseIdForEnrollTest);

        StudentAttributesFactory saf = new StudentAttributesFactory(headerLine);
        assertEquals(5, enrollResults.studentList.size());
        assertEquals(5, studentsLogic.getStudentsForCourse(courseIdForEnrollTest).size());
        // Test enroll result
        line0 = "t1|n1|e1@g|c1";
        verifyEnrollmentResultForStudent(saf.makeStudent(line0, courseIdForEnrollTest),
                                         enrollResults.studentList.get(0), StudentUpdateStatus.NEW);
        verifyEnrollmentResultForStudent(saf.makeStudent(line1, courseIdForEnrollTest),
                                         enrollResults.studentList.get(1), StudentUpdateStatus.NEW);
        verifyEnrollmentResultForStudent(saf.makeStudent(line4, courseIdForEnrollTest),
                                         enrollResults.studentList.get(4), StudentUpdateStatus.NEW);

        CourseDetailsBundle courseDetails = coursesLogic.getCourseSummary(courseIdForEnrollTest);
        assertEquals(5, courseDetails.stats.unregisteredTotal);

        ______TS("includes a mix of unmodified, modified, and new");

        String modifiedLine2 = "t3|modified name|e3@g|c3";
        String line5 = "t6|n6|e6@g|c6";
        lines = headerLine + System.lineSeparator()
                + line0 + System.lineSeparator()
                + modifiedLine2 + System.lineSeparator()
                + line1 + System.lineSeparator()
                + line5;
        enrollResults = studentsLogic.enrollStudentsWithoutDocument(lines, courseIdForEnrollTest);
        assertEquals(6, enrollResults.studentList.size());
        assertEquals(6, studentsLogic.getStudentsForCourse(courseIdForEnrollTest).size());
        verifyEnrollmentResultForStudent(saf.makeStudent(line0, courseIdForEnrollTest),
                                         enrollResults.studentList.get(0), StudentUpdateStatus.UNMODIFIED);
        verifyEnrollmentResultForStudent(saf.makeStudent(modifiedLine2, courseIdForEnrollTest),
                                         enrollResults.studentList.get(1), StudentUpdateStatus.MODIFIED);
        verifyEnrollmentResultForStudent(saf.makeStudent(line1, courseIdForEnrollTest),
                                         enrollResults.studentList.get(2), StudentUpdateStatus.UNMODIFIED);
        verifyEnrollmentResultForStudent(saf.makeStudent(line5, courseIdForEnrollTest),
                                         enrollResults.studentList.get(3), StudentUpdateStatus.NEW);
        assertEquals(StudentUpdateStatus.NOT_IN_ENROLL_LIST,
                     enrollResults.studentList.get(4).updateStatus);
        assertEquals(StudentUpdateStatus.NOT_IN_ENROLL_LIST,
                     enrollResults.studentList.get(5).updateStatus);

        ______TS("includes an incorrect line");

        // no changes should be done to the database
        String incorrectLine = "incorrectly formatted line";
        lines = headerLine + System.lineSeparator()
                + "t7|n7|e7@g|c7" + System.lineSeparator()
                + incorrectLine + System.lineSeparator()
                + line2 + System.lineSeparator()
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
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        ______TS("same student added, modified and unmodified");

        StudentProfileAttributes studentAttributes = StudentProfileAttributes.builder("tes.instructor")
                .withShortName("Ins 1").withGender("male")
                .build();
        accountToAdd = AccountAttributes.builder()
                .withGoogleId("tes.instructor")
                .withName("Instructor 1")
                .withEmail("instructor@email.tmt")
                .withInstitute("TEAMMATES Test Institute 1")
                .withIsInstructor(true)
                .withStudentProfileAttributes(studentAttributes)
                .build();

        accountsLogic.createAccount(accountToAdd);
        coursesLogic.createCourseAndInstructor("tes.instructor", "tes.course", "TES Course", "UTC");

        String line = headerLine + System.lineSeparator() + "t8|n8|e8@g|c1";
        enrollResults = studentsLogic.enrollStudentsWithoutDocument(line, "tes.course");
        assertEquals(1, enrollResults.studentList.size());
        assertEquals(StudentUpdateStatus.NEW, enrollResults.studentList.get(0).updateStatus);

        line = headerLine + System.lineSeparator() + "t8|n8a|e8@g|c1";
        enrollResults = studentsLogic.enrollStudentsWithoutDocument(line, "tes.course");
        assertEquals(1, enrollResults.studentList.size());
        assertEquals(StudentUpdateStatus.MODIFIED, enrollResults.studentList.get(0).updateStatus);

        line = headerLine + System.lineSeparator() + "t8|n8a|e8@g|c1";
        enrollResults = studentsLogic.enrollStudentsWithoutDocument(line, "tes.course");
        assertEquals(1, enrollResults.studentList.size());
        assertEquals(StudentUpdateStatus.UNMODIFIED, enrollResults.studentList.get(0).updateStatus);

        ______TS("duplicated emails");

        String lineT9 = "t9|n9|e9@g|c9";
        String lineT10 = "t10|n10|e9@g|c10";
        lines = headerLine + System.lineSeparator() + lineT9 + System.lineSeparator() + lineT10;
        try {
            studentsLogic.enrollStudentsWithoutDocument(lines, "tes.course");
        } catch (EnrollException e) {
            assertTrue(e.getMessage().contains(lineT10));
            AssertHelper.assertContains("Same email address as the student in line \"" + lineT9 + "\"", e.getMessage());
        }

        ______TS("invalid course id");

        String enrollLines = headerLine + System.lineSeparator();
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

        enrollLines = headerLine + System.lineSeparator() + "invalidline0\ninvalidline1\n";
        try {
            studentsLogic.enrollStudentsWithoutDocument(enrollLines, courseIdForEnrollTest);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            ignoreExpectedException();
        }

    }

    private void testGetStudentForEmail() {

        ______TS("null parameters");

        try {
            studentsLogic.getStudentForEmail(null, "valid@email.tmt");
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        ______TS("non-exist student");

        String nonExistStudentEmail = "nonExist@google.tmt";
        String course1Id = dataBundle.courses.get("typicalCourse1").getId();
        assertNull(studentsLogic.getStudentForEmail(course1Id, nonExistStudentEmail));

        ______TS("typical case");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertEquals(student1InCourse1.googleId,
                     studentsLogic.getStudentForEmail(course1Id, student1InCourse1.email).googleId);
    }

    private void testGetStudentForRegistrationKey() {

        ______TS("null parameter");

        try {
            studentsLogic.getStudentForRegistrationKey(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        ______TS("non-exist student");

        String nonExistStudentKey = StringHelper.encrypt("nonExistKey");
        assertNull(studentsLogic.getStudentForRegistrationKey(nonExistStudentKey));

        ______TS("typical case");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String course1Id = dataBundle.courses.get("typicalCourse1").getId();
        String studentKey = studentsLogic.getStudentForCourseIdAndGoogleId(course1Id, student1InCourse1.googleId).key;
        StudentAttributes actualStudent = studentsLogic.getStudentForRegistrationKey(StringHelper.encrypt(studentKey));
        assertEquals(student1InCourse1.googleId, actualStudent.googleId);
    }

    private void testGetStudentsForGoogleId() {

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

        listReceivedUsingStudentInCourse1.sort(Comparator.comparing(student -> student.course));

        StudentAttributes firstStudentReceived = listReceivedUsingStudentInCourse1.get(1);
        // First student received turned out to be the one from course 2
        assertEquals(studentInTwoCoursesInCourse2.email,
                firstStudentReceived.email);
        assertEquals(studentInTwoCoursesInCourse2.name,
                firstStudentReceived.name);
        assertEquals(studentInTwoCoursesInCourse2.course,
                firstStudentReceived.course);

        // then the second student received must be from course 1
        StudentAttributes secondStudentReceived = listReceivedUsingStudentInCourse1
                .get(0);
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
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }

    private void testGetStudentForCourseIdAndGoogleId() {

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

        assertNull(studentsLogic.getStudentForCourseIdAndGoogleId("non-existent",
                "random-google-id"));

        ______TS("null parameters");

        try {
            studentsLogic.getStudentForCourseIdAndGoogleId("valid.course", null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }

    private void testGetStudentsForCourse() {

        ______TS("course with multiple students");

        CourseAttributes course1OfInstructor1 = dataBundle.courses.get("typicalCourse1");
        List<StudentAttributes> studentList = studentsLogic
                .getStudentsForCourse(course1OfInstructor1.getId());
        assertEquals(5, studentList.size());
        for (StudentAttributes s : studentList) {
            assertEquals(course1OfInstructor1.getId(), s.course);
        }

        ______TS("course with 0 students");

        CourseAttributes course2OfInstructor1 = dataBundle.courses.get("courseNoEvals");
        studentList = studentsLogic.getStudentsForCourse(course2OfInstructor1.getId());
        assertEquals(0, studentList.size());

        ______TS("null parameter");

        try {
            studentsLogic.getStudentsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        ______TS("non-existent course");

        studentList = studentsLogic.getStudentsForCourse("non-existent");
        assertEquals(0, studentList.size());

    }

    private void testGetEncryptedKeyForStudent() throws Exception {

        ______TS("null parameters");

        try {
            studentsLogic.getEncryptedKeyForStudent("valid.course.id", null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        ______TS("non-existent student");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String nonExistStudentEmail = "non@existent";
        try {
            studentsLogic.getEncryptedKeyForStudent(student1InCourse1.course, nonExistStudentEmail);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            String expectedErrorMsg = "Student does not exist: "
                                      + "[" + student1InCourse1.course + "/" + nonExistStudentEmail + "]";
            assertEquals(expectedErrorMsg, e.getMessage());
        }

        // the typical case below seems unnecessary though--it is not useful for now
        // as the method itself is too simple
        ______TS("typical case");

        String course1Id = dataBundle.courses.get("typicalCourse1").getId();
        String actualKey = studentsLogic.getEncryptedKeyForStudent(course1Id, student1InCourse1.email);
        String expectedKey = StringHelper.encrypt(
                studentsLogic.getStudentForCourseIdAndGoogleId(course1Id, student1InCourse1.googleId).key);
        assertEquals(expectedKey, actualKey);
    }

    private void testIsStudentInAnyCourse() {

        ______TS("non-existent student");

        String nonExistStudentGoogleId = "nonExistGoogleId";
        assertFalse(studentsLogic.isStudentInAnyCourse(nonExistStudentGoogleId));

        ______TS("typical case");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertTrue(studentsLogic.isStudentInAnyCourse(student1InCourse1.googleId));
    }

    private void testIsStudentInCourse() {

        ______TS("non-existent student");

        String nonExistStudentEmail = "nonExist@google.tmt";
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        assertFalse(studentsLogic.isStudentInCourse(course1.getId(), nonExistStudentEmail));

        ______TS("typical case");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertTrue(studentsLogic.isStudentInCourse(course1.getId(), student1InCourse1.email));
    }

    private void testIsStudentInTeam() {

        ______TS("non-existent student");

        String nonExistStudentEmail = "nonExist@google.tmt";
        String teamName = "Team 1";
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        assertFalse(studentsLogic.isStudentInTeam(course1.getId(), teamName, nonExistStudentEmail));

        ______TS("student not in given team");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertFalse(studentsLogic.isStudentInTeam(course1.getId(), teamName, nonExistStudentEmail));

        ______TS("typical case");
        teamName = student1InCourse1.team;
        assertTrue(studentsLogic.isStudentInTeam(course1.getId(), teamName, student1InCourse1.email));
    }

    private void testIsStudentsInSameTeam() {

        ______TS("non-existent student1");

        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        StudentAttributes student2InCourse1 = dataBundle.students.get("student2InCourse1");
        String nonExistStudentEmail = "nonExist@google.tmt";
        assertFalse(studentsLogic.isStudentsInSameTeam(course1.getId(), nonExistStudentEmail,
                                                       student2InCourse1.email));

        ______TS("students of different teams");

        StudentAttributes student5InCourse1 = dataBundle.students.get("student5InCourse1");
        assertFalse(studentsLogic.isStudentsInSameTeam(course1.getId(), student2InCourse1.email,
                                                       student5InCourse1.email));

        ______TS("students of different teams");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertTrue(studentsLogic.isStudentsInSameTeam(course1.getId(), student2InCourse1.email,
                                                      student1InCourse1.email));

    }

    private void testDeleteStudent() {

        ______TS("typical delete");

        // this is the student to be deleted
        StudentAttributes student2InCourse1 = dataBundle.students.get("student2InCourse1");
        verifyPresentInDatastore(student2InCourse1);

        studentsLogic.deleteStudentCascadeWithoutDocument(student2InCourse1.course, student2InCourse1.email);
        verifyAbsentInDatastore(student2InCourse1);

        // verify that other students in the course are intact

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        verifyPresentInDatastore(student1InCourse1);

        ______TS("delete non-existent student");

        // should fail silently.
        studentsLogic.deleteStudentCascadeWithoutDocument(student2InCourse1.course, student2InCourse1.email);

        ______TS("null parameters");

        try {
            studentsLogic.deleteStudentCascadeWithoutDocument(null, "valid@email.tmt");
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }

    private static StudentEnrollDetails enrollStudent(StudentAttributes student) throws Exception {
        return (StudentEnrollDetails) invokeMethod(StudentsLogic.class, "enrollStudent",
                                                   new Class<?>[] { StudentAttributes.class, Boolean.class },
                                                   StudentsLogic.inst(), new Object[] { student, false });
    }

    @AfterClass
    public void classTearDown() {
        AccountsLogic.inst().deleteAccountCascade(dataBundle.students.get("student4InCourse1").googleId);
    }

    private void verifyEnrollmentDetailsForStudent(StudentAttributes expectedStudent, String oldTeam,
                                                   StudentEnrollDetails enrollmentResult, StudentUpdateStatus status) {
        assertEquals(expectedStudent.email, enrollmentResult.email);
        assertEquals(expectedStudent.team, enrollmentResult.newTeam);
        assertEquals(expectedStudent.course, enrollmentResult.course);
        assertEquals(oldTeam, enrollmentResult.oldTeam);
        assertEquals(status, enrollmentResult.updateStatus);
    }

    private void verifyEnrollmentResultForStudent(StudentAttributes expectedStudent,
                                                  StudentAttributes enrollmentResult, StudentUpdateStatus status) {
        String errorMessage = "mismatch! \n expected:\n"
                            + JsonUtils.toJson(expectedStudent)
                            + "\n actual \n"
                            + JsonUtils.toJson(enrollmentResult);
        assertTrue(errorMessage, enrollmentResult.isEnrollInfoSameAs(expectedStudent)
                                 && enrollmentResult.updateStatus == status);
    }
}
