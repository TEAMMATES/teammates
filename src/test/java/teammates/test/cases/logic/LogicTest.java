package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.EMAIL_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.END_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.EVALUATION_NAME;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;
import static teammates.common.util.FieldValidator.START_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.TIME_FRAME_ERROR_MESSAGE;
import static teammates.logic.core.TeamEvalResult.NA;
import static teammates.logic.core.TeamEvalResult.NSB;
import static teammates.logic.core.TeamEvalResult.NSU;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.EvaluationResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.TeamResultBundle;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.api.Logic;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.EvaluationsLogic;
import teammates.logic.core.SubmissionsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

import com.google.appengine.api.datastore.Text;

public class LogicTest extends BaseComponentTestCase {

    private static final Logic logic = new Logic();
    protected static SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();

    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(Logic.class);
    }

    @BeforeMethod
    public void caseSetUp() throws ServletException {
        dataBundle = getTypicalDataBundle();
    }

    @SuppressWarnings("unused")
    private void ____USER_level_methods___________________________________() {
    }

    @Test
    public void testGetLoginUrl() {
        gaeSimulation.logoutUser();
        assertEquals("/_ah/login?continue=www.abc.com",
                Logic.getLoginUrl("www.abc.com"));
    }

    @Test
    public void testGetLogoutUrl() {
        gaeSimulation.loginUser("any.user");
        assertEquals("/_ah/logout?continue=www.def.com",
                Logic.getLogoutUrl("www.def.com"));
    }
    
    //TODO: test isUserLoggedIn method

    @Test
    public void testGetCurrentUser() throws Exception {

        restoreTypicalDataInDatastore();

        ______TS("admin+instructor+student");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsAdmin(instructor.googleId);
        // also make this user a student
        StudentAttributes instructorAsStudent = new StudentAttributes(
                "Team 1", "Instructor As Student", "instructorasstudent@yahoo.com", "", "some-course");
        instructorAsStudent.googleId = instructor.googleId;
        logic.createStudent(instructorAsStudent);

        UserType user = logic.getCurrentUser();
        assertEquals(instructor.googleId, user.id);
        assertEquals(true, user.isAdmin);
        assertEquals(true, user.isInstructor);
        assertEquals(true, user.isStudent);

        ______TS("admin+instructor only");

        // this user is no longer a student
        logic.deleteStudent(instructorAsStudent.course, instructorAsStudent.email);

        user = logic.getCurrentUser();
        assertEquals(instructor.googleId, user.id);
        assertEquals(true, user.isAdmin);
        assertEquals(true, user.isInstructor);
        assertEquals(false, user.isStudent);

        ______TS("instructor only");
        
        // this user is no longer an admin
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        user = logic.getCurrentUser();
        assertEquals(instructor.googleId, user.id);
        assertEquals(false, user.isAdmin);
        assertEquals(true, user.isInstructor);
        assertEquals(false, user.isStudent);

        ______TS("unregistered");

        gaeSimulation.loginUser("unknown");

        user = logic.getCurrentUser();
        assertEquals("unknown", user.id);
        assertEquals(false, user.isAdmin);
        assertEquals(false, user.isInstructor);
        assertEquals(false, user.isStudent);

        ______TS("student only");

        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        gaeSimulation.loginAsStudent(student.googleId);

        user = logic.getCurrentUser();
        assertEquals(student.googleId, user.id);
        assertEquals(false, user.isAdmin);
        assertEquals(false, user.isInstructor);
        assertEquals(true, user.isStudent);

        ______TS("admin only");

        gaeSimulation.loginAsAdmin("any.user");

        user = logic.getCurrentUser();
        assertEquals("any.user", user.id);
        assertEquals(true, user.isAdmin);
        assertEquals(false, user.isInstructor);
        assertEquals(false, user.isStudent);

        ______TS("not logged in");

        // check for user not logged in
        gaeSimulation.logoutUser();
        assertEquals(null, logic.getCurrentUser());
    }

    @SuppressWarnings("unused")
    private void ____STUDENT_level_methods__________________________________() {
    }

    @Test
    public void testGetTeamsForCourse() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        String methodName = "getTeamsForCourse";
        Class<?>[] paramTypes = new Class<?>[] { String.class };
    
        ______TS("typical case");
    
        restoreTypicalDataInDatastore();
    
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        logic.createStudent(new StudentAttributes("t1", "s1", "s1@e", "", course.id));
        List<TeamDetailsBundle> courseAsTeams = logic.getTeamsForCourse(course.id);
        assertEquals(3, courseAsTeams.size());
    
        String team1Id = "Team 1.1";
        assertEquals(team1Id, courseAsTeams.get(0).name);
        assertEquals(4, courseAsTeams.get(0).students.size());
        assertEquals(team1Id, courseAsTeams.get(0).students.get(0).team);
        assertEquals(team1Id, courseAsTeams.get(0).students.get(1).team);
    
        String team2Id = "Team 1.2";
        assertEquals(team2Id, courseAsTeams.get(1).name);
        assertEquals(1, courseAsTeams.get(1).students.size());
        assertEquals(team2Id, courseAsTeams.get(1).students.get(0).team);
    
        ______TS("null parameters");
    
        try {
            logic.getTeamsForCourse(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("course without teams");
    
        logic.deleteCourse("course1");
        logic.createAccount("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
        logic.createCourseAndInstructor("instructor1", "course1", "Course 1");
        assertEquals(0, logic.getTeamsForCourse("course1").size());

        ______TS("non-existent course");
        
        TestHelper.verifyEntityDoesNotExistException(methodName, paramTypes,
                new Object[] { "non-existent" });
    }
    
    @Test
    public void testJoinCourseForStudent() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        // make a student 'unregistered'
        
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        String googleId = "student1InCourse1";
        String key = logic.getKeyForStudent(student.course, student.email);
        student.googleId = "";
        logic.updateStudent(student.email, student);
        assertEquals("", logic.getStudentForEmail(student.course, student.email).googleId);
    
    
        ______TS("register an unregistered student");
    
        restoreTypicalDataInDatastore();
    
        
    
        // make a student 'unregistered'
        student = dataBundle.students.get("student1InCourse1");
        googleId = "student1InCourse1";
        key = logic.getKeyForStudent(student.course, student.email);
        student.googleId = "";
        logic.updateStudent(student.email, student);
        assertEquals("", logic.getStudentForEmail(student.course, student.email).googleId);
    
        // TODO: remove encrpytion - should fail test
        //Test if unencrypted key used
        logic.joinCourseForStudent(key, googleId);
        assertEquals(googleId,
                logic.getStudentForEmail(student.course, student.email).googleId);
        
        
        
        // make a student 'unregistered'
        student = dataBundle.students.get("student1InCourse1");
        googleId = "student1InCourse1";
        key = logic.getKeyForStudent(student.course, student.email);
        student.googleId = "";
        logic.updateStudent(student.email, student);
        assertEquals("", logic.getStudentForEmail(student.course, student.email).googleId);
        logic.deleteAccount(googleId);    // for testing account creation
        AccountAttributes studentAccount = logic.getAccount(googleId); // this is because student accounts are not in typical data bundle
        assertNull(studentAccount);
    
        //Test for encrypted key used
        key = StringHelper.encrypt(key);
        logic.joinCourseForStudent(key, googleId);
        assertEquals(googleId,
                logic.getStudentForEmail(student.course, student.email).googleId);
        
        // Check that an account with the student's google ID was created
        studentAccount = logic.getAccount(googleId);
        TestHelper.verifyPresentInDatastore(studentAccount); 
        AccountAttributes accountOfInstructorOfCourse = dataBundle.accounts.get("instructor1OfCourse1");
        assertEquals(accountOfInstructorOfCourse.institute, studentAccount.institute);// Test that student account was appended with the correct Institute
                
        ______TS("null parameters");
    
        try {
            logic.joinCourseForStudent(null, "valid.user");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        
        try {
            logic.joinCourseForStudent(key, null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }
    
    @Test
    public void testEnrollStudents() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        ______TS("all valid students, but contains blank lines");
    
        restoreTypicalDataInDatastore();
    
        String instructorId = "instructorForEnrollTesting";
        String courseId = "courseForEnrollTest";
        
        logic.createAccount(instructorId, "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
        logic.createCourseAndInstructor(instructorId, courseId, "Course for Enroll Testing");
        String EOL = Const.EOL;
    
        String line0 = "t1|n1|e1@g|c1";
        String line1 = " t2|  n2|  e2@g|  c2";
        String line2 = "t3|n3|e3@g|c3  ";
        String line3 = "t4|n4|  e4@g|c4";
        String line4 = "t5|n5|e5@g  |c5";
        String lines = line0 + EOL + line1 + EOL + line2 + EOL
                + "  \t \t \t \t           " + EOL + line3 + EOL + EOL + line4
                + EOL + "    " + EOL + EOL;
        List<StudentAttributes> enrollResults = logic.enrollStudents(lines, courseId);
    
        StudentAttributesFactory saf = new StudentAttributesFactory();
        assertEquals(5, enrollResults.size());
        assertEquals(5, logic.getStudentsForCourse(courseId).size());
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line0, courseId),
                enrollResults.get(0), StudentAttributes.UpdateStatus.NEW);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line1, courseId),
                enrollResults.get(1), StudentAttributes.UpdateStatus.NEW);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line4, courseId),
                enrollResults.get(4), StudentAttributes.UpdateStatus.NEW);
        
        CourseDetailsBundle cd = logic.getCourseDetails(courseId);
        assertEquals(5, cd.stats.unregisteredTotal);
    
        ______TS("includes a mix of unmodified, modified, and new");
    
        String line0_1 = "t3|modified name|e3@g|c3";
        String line5 = "t6|n6|e6@g|c6";
        lines = line0 + EOL + line0_1 + EOL + line1 + EOL + line5;
        enrollResults = logic.enrollStudents(lines, courseId);
        assertEquals(6, enrollResults.size());
        assertEquals(6, logic.getStudentsForCourse(courseId).size());
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line0, courseId),
                enrollResults.get(0), StudentAttributes.UpdateStatus.UNMODIFIED);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line0_1, courseId),
                enrollResults.get(1), StudentAttributes.UpdateStatus.MODIFIED);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line1, courseId),
                enrollResults.get(2), StudentAttributes.UpdateStatus.UNMODIFIED);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line5, courseId),
                enrollResults.get(3), StudentAttributes.UpdateStatus.NEW);
        assertEquals(StudentAttributes.UpdateStatus.NOT_IN_ENROLL_LIST,
                enrollResults.get(4).updateStatus);
        assertEquals(StudentAttributes.UpdateStatus.NOT_IN_ENROLL_LIST,
                enrollResults.get(5).updateStatus);
    
        ______TS("includes an incorrect line");
    
        // no changes should be done to the database
        String incorrectLine = "incorrectly formatted line";
        lines = "t7|n7|e7@g|c7" + EOL + incorrectLine + EOL + line2 + EOL
                + line3;
        try {
            enrollResults = logic.enrollStudents(lines, courseId);
            Assert.fail("Did not throw exception for incorrectly formatted line");
        } catch (EnrollException e) {
            assertTrue(e.getMessage().contains(incorrectLine));
        }
        assertEquals(6, logic.getStudentsForCourse(courseId).size());
    
        ______TS("null parameters");
    
        try {
            logic.enrollStudents("a|b|c|d", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("same student added, modified and unmodified");
    
        logic.createAccount("tes.instructor", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
        logic.createCourseAndInstructor("tes.instructor", "tes.course", "TES Course");
        
        String line = "t8|n8|e8@g|c1" ;
        enrollResults = logic.enrollStudents(line, "tes.course");
        assertEquals(1, enrollResults.size());
        assertEquals(StudentAttributes.UpdateStatus.NEW,
                enrollResults.get(0).updateStatus);
        
        line = "t8|n8a|e8@g|c1";
        enrollResults = logic.enrollStudents(line, "tes.course");
        assertEquals(1, enrollResults.size());
        assertEquals(StudentAttributes.UpdateStatus.MODIFIED,
                enrollResults.get(0).updateStatus);
        
        line = "t8|n8a|e8@g|c1";
        enrollResults = logic.enrollStudents(line, "tes.course");
        assertEquals(1, enrollResults.size());
        assertEquals(StudentAttributes.UpdateStatus.UNMODIFIED,
                enrollResults.get(0).updateStatus);

        ______TS("duplicated emails");
        
        String line_t9 = "t9|n9|e9@g|c9";
        String line_t10 = "t10|n10|e9@g|c10";
        try {
            logic.enrollStudents(line_t9 + EOL + line_t10, "tes.course");
        } catch (EnrollException e) {
            assertTrue(e.getMessage().contains(line_t10));
            assertTrue(e.getMessage().contains("Same email address as the student in line \""+line_t9+"\""));    
        }
    }
    
    @Test
    public void testUpdateStudent() throws Exception {

        restoreTypicalDataInDatastore();

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String originalEmail = student1InCourse1.email;
                 
        ______TS("typical success case");
        student1InCourse1.name = student1InCourse1.name + "x";
        student1InCourse1.googleId = student1InCourse1.googleId + "x";
        student1InCourse1.comments = student1InCourse1.comments + "x";
        student1InCourse1.email = student1InCourse1.email + "x";
        student1InCourse1.team = "Team 1.2";
        logic.updateStudent(originalEmail, student1InCourse1);        
        TestHelper.verifyPresentInDatastore(student1InCourse1);
        
        // check for cascade
        List<SubmissionAttributes> submissionsAfterEdit = submissionsLogic.getSubmissionsForCourse(student1InCourse1.course);        
        TestHelper.verifySubmissionsExistForCurrentTeamStructureInAllExistingEvaluations(submissionsAfterEdit,
                student1InCourse1.course);
        
        ______TS("null parameters");

        try {
            logic.updateStudent(null, student1InCourse1);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }        
        try {
            logic.updateStudent("test@email.com", null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        
    }

    @Test
    public void testSendReminderForEvaluation() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        ______TS("empty class");
    
        restoreTypicalDataInDatastore();
    
        AccountsLogic.inst().deleteAccountCascade("instructor1");
        CoursesLogic.inst().deleteCourseCascade("course1");
        logic.createAccount("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
        logic.createCourseAndInstructor("instructor1", "course1", "course 1");
        EvaluationAttributes newEval = new EvaluationAttributes();
        newEval.courseId = "course1";
        newEval.name = "new eval";
        newEval.instructions = new Text("instructions");
        newEval.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
        newEval.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
        logic.createEvaluation(newEval);
    
        List<MimeMessage> emailsSent = EvaluationsLogic.inst().sendReminderForEvaluation(
                "course1", "new eval");
        
        int numOfInstructor = logic.getInstructorsForCourse(newEval.courseId).size();
        assertEquals(0+numOfInstructor, emailsSent.size());
    
        ______TS("1 person submitted fully, 4 others have not");
    
        EvaluationAttributes eval = dataBundle.evaluations
                .get("evaluation1InCourse1");
        emailsSent = EvaluationsLogic.inst().sendReminderForEvaluation(eval.courseId, eval.name);
        
        numOfInstructor = logic.getInstructorsForCourse(eval.courseId).size();
        assertEquals(4+numOfInstructor, emailsSent.size());
        List<StudentAttributes> studentList = logic
                .getStudentsForCourse(eval.courseId);
    
        //student 1 would not recieve email 
        for (StudentAttributes s : studentList) {
            if(!s.name.equals("student1 In Course1")){
                String errorMessage = "No email sent to " + s.email;
                assertTrue(errorMessage, TestHelper.getEmailToStudent(s, emailsSent) != null);
            }
        }
    
        ______TS("some have submitted fully");
        // This student is the only member in Team 1.2. If he submits his
        // self-evaluation, he sill be considered 'fully submitted'. Only
        // student in Team 1.1 should receive emails.
        StudentAttributes singleStudnetInTeam1_2 = dataBundle.students
                .get("student5InCourse1");
        SubmissionAttributes sub = new SubmissionAttributes();
        sub.course = singleStudnetInTeam1_2.course;
        sub.evaluation = eval.name;
        sub.team = singleStudnetInTeam1_2.team;
        sub.reviewer = singleStudnetInTeam1_2.email;
        sub.reviewee = singleStudnetInTeam1_2.email;
        sub.points = 100;
        sub.justification = new Text("j");
        sub.p2pFeedback = new Text("y");
        ArrayList<SubmissionAttributes> submissions = new ArrayList<SubmissionAttributes>();
        submissions.add(sub);
        logic.updateSubmissions(submissions);
        emailsSent = EvaluationsLogic.inst().sendReminderForEvaluation(eval.courseId, eval.name);
    
        numOfInstructor = logic.getInstructorsForCourse(eval.courseId).size();
        assertEquals(3+numOfInstructor, emailsSent.size());
    
        studentList = logic.getStudentsForCourse(eval.courseId);
    
        // verify 3 students in Team 1.1 received emails.
        for (StudentAttributes s : studentList) {
            if (s.team.equals("Team 1.1") && !s.name.equals("student1 In Course1")) {
                String errorMessage = "No email sent to " + s.email;
                assertTrue(errorMessage,
                        TestHelper.getEmailToStudent(s, emailsSent) != null);
            }
        }
    
        ______TS("non-existent course/evaluation");
        try {
            EvaluationsLogic.inst().sendReminderForEvaluation("non-existent-course", "non-existent-eval");
        } catch (Exception e) {
            assertEquals("Trying to edit non-existent evaluation non-existent-course/non-existent-eval", 
                    e.getMessage());
        }
    
        ______TS("null parameter");
    
        try {
            EvaluationsLogic.inst().sendReminderForEvaluation("valid.course.id", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals("Supplied parameter was null\n", a.getMessage());
        }
    }

    @Test
    public void testDeleteStudent() throws Exception {

        restoreTypicalDataInDatastore();

        ______TS("typical delete");

        restoreTypicalDataInDatastore();

        

        // this is the student to be deleted
        StudentAttributes student2InCourse1 = dataBundle.students
                .get("student2InCourse1");
        TestHelper.verifyPresentInDatastore(student2InCourse1);

        // ensure student-to-be-deleted has some submissions
        SubmissionAttributes submissionFromS1C1ToS2C1 = dataBundle.submissions
                .get("submissionFromS1C1ToS2C1");
        TestHelper.verifyPresentInDatastore(submissionFromS1C1ToS2C1);

        SubmissionAttributes submissionFromS2C1ToS1C1 = dataBundle.submissions
                .get("submissionFromS2C1ToS1C1");
        TestHelper.verifyPresentInDatastore(submissionFromS2C1ToS1C1);

        SubmissionAttributes submissionFromS1C1ToS1C1 = dataBundle.submissions
                .get("submissionFromS1C1ToS1C1");
        TestHelper.verifyPresentInDatastore(submissionFromS1C1ToS1C1);

        logic.deleteStudent(student2InCourse1.course, student2InCourse1.email);
        TestHelper.verifyAbsentInDatastore(student2InCourse1);

        // verify that other students in the course are intact
        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        TestHelper.verifyPresentInDatastore(student1InCourse1);

        // verify that submissions are deleted
        TestHelper.verifyAbsentInDatastore(submissionFromS1C1ToS2C1);
        TestHelper.verifyAbsentInDatastore(submissionFromS2C1ToS1C1);

        // verify other student's submissions are intact
        TestHelper.verifyPresentInDatastore(submissionFromS1C1ToS1C1);

        ______TS("delete non-existent student");

        // should fail silently.
        logic.deleteStudent(student2InCourse1.course, student2InCourse1.email);

        ______TS("null parameters");

        try {
            logic.deleteStudent(null, "valid@email.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }



    @SuppressWarnings("unused")
    private void ____EVALUATION_level_methods_______________________________() {

    }

    @Test
    public void testCreateEvaluation() throws Exception {

        restoreTypicalDataInDatastore();

        EvaluationAttributes evaluation = new EvaluationAttributes();
        evaluation.courseId = "idOfTypicalCourse1";
        evaluation.name = "new evaluation";

        ______TS("typical case");

        

        restoreTypicalDataInDatastore();

        evaluation = dataBundle.evaluations.get("evaluation1InCourse1");
        TestHelper.verifyPresentInDatastore(evaluation);
        logic.deleteEvaluation(evaluation.courseId, evaluation.name);
        TestHelper.verifyAbsentInDatastore(evaluation);
        logic.createEvaluation(evaluation);
        TestHelper.verifyPresentInDatastore(evaluation);

        ______TS("Duplicate evaluation name");

        try {
            logic.createEvaluation(evaluation);
            Assert.fail();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(evaluation.name, e.getMessage());
        }

        ______TS("null parameters");

        try {
            logic.createEvaluation(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("invalid parameters");

        // Only checking that exception is thrown at logic level
        evaluation.courseId = "invalid course";
        try {
            logic.createEvaluation(evaluation);
            Assert.fail();
        } catch (InvalidParametersException e) {
            assertEquals(
                    String.format(COURSE_ID_ERROR_MESSAGE, evaluation.courseId, REASON_INCORRECT_FORMAT),
                    e.getMessage());
        }
        // invalid values to other parameters should be checked against
        // EvaluationData.validate();

    }

    @Test
    public void testGetEvaluation() throws Exception {

        restoreTypicalDataInDatastore();

        ______TS("typical case");

        restoreTypicalDataInDatastore();

        EvaluationAttributes expected = dataBundle.evaluations
                .get("evaluation1InCourse1");
        EvaluationAttributes actual = logic.getEvaluation(expected.courseId,
                expected.name);
        TestHelper.verifySameEvaluationData(expected, actual);

        ______TS("null parameters");

        try {
            logic.getEvaluation("valid.course.id", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }

        ______TS("non-existent");

        assertNull(logic.getEvaluation("non-existent", expected.name));
        assertNull(logic.getEvaluation(expected.courseId, "non-existent"));

    }

    /* TODO: implement tests for the following: 
     * 1. getEvaluationDetails()    
     * 2. getEvaluationsListForInstructor()
     * 3. getEvaluationDetailsForCourse()
     */
    @Test
    public void testGetEvaluationsDetailsForInstructor() throws Exception {
    
        restoreTypicalDataInDatastore();
        String methodName = "getEvaluationsDetailsForInstructor";
        Class<?>[] paramTypes = new Class<?>[] { String.class };
    
        ______TS("typical case, instructor has 3 evaluations");
    
        
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        ArrayList<EvaluationDetailsBundle> evalList = logic
                .getEvaluationsDetailsForInstructor(instructor.googleId);
        // 2 Evals from Course 1, 1 Eval from Course  2
        assertEquals(3, evalList.size());
        EvaluationAttributes evaluation = dataBundle.evaluations.get("evaluation1InCourse1");
        for (EvaluationDetailsBundle edd : evalList) {
            if(edd.evaluation.name.equals(evaluation.name)){
                //We have, 4 students in Team 1.1 and 1 student in Team 1.2
                //Only 3 have submitted.
                assertEquals(5,edd.stats.expectedTotal);
                assertEquals(3,edd.stats.submittedTotal);
            }
        }
        
        ______TS("check immunity from orphaned submissions");
        
        //move a student from Team 1.1 to Team 1.2
        StudentAttributes student = dataBundle.students.get("student4InCourse1");
        student.team = "Team 1.2";
        logic.updateStudent(student.email, student);
        
        evalList = logic.getEvaluationsDetailsForInstructor(instructor.googleId);
        assertEquals(3, evalList.size());
        
        for (EvaluationDetailsBundle edd : evalList) {
            if(edd.evaluation.name.equals(evaluation.name)){
                //Now we have, 3 students in Team 1.1 and 2 student in Team 1.2
                //Only 2 (1 less than before) have submitted 
                //   because we just moved a student to a new team and that
                //   student's previous submissions are now orphaned.
                assertEquals(5,edd.stats.expectedTotal);
                assertEquals(2,edd.stats.submittedTotal);
            }
        }
    
        ______TS("instructor has 1 evaluation");
    
        
    
        InstructorAttributes instructor2 = dataBundle.instructors.get("instructor2OfCourse2");
        evalList = logic.getEvaluationsDetailsForInstructor(instructor2.googleId);
        assertEquals(1, evalList.size());
        for (EvaluationDetailsBundle edd : evalList) {
            assertTrue(logic.isInstructorOfCourse(instructor2.googleId, edd.evaluation.courseId));
        }
    
        ______TS("instructor has 0 evaluations");
    
        
        
        InstructorAttributes instructor4 = dataBundle.instructors.get("instructor4");
        evalList = logic.getEvaluationsDetailsForInstructor(instructor4.googleId);
        assertEquals(0, evalList.size());
    
        ______TS("null parameters");
    
        try {
            logic.getEvaluationsDetailsForInstructor(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("non-existent instructor");
    
        
        
        TestHelper.verifyEntityDoesNotExistException(methodName, paramTypes,
                new Object[] { "non-existent" });
    }

    
    
    @Test
    public void testGetEvaluationResult() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        String methodName = "getEvaluationResult";
        Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
    
        ______TS("typical case");
    
        restoreTypicalDataInDatastore();
    
        
    
        // reconfigure points of an existing evaluation in the datastore
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        EvaluationAttributes evaluation = dataBundle.evaluations
                .get("evaluation1InCourse1");
    
        // @formatter:off
        TestHelper.setPointsForSubmissions(new int[][] { 
                { 100, 100, 100, 100 },
                { 110, 110, NSU, 110 }, 
                { NSB, NSB, NSB, NSB },
                { 70, 80, 110, 120 } });
        // @formatter:on
    
        EvaluationResultsBundle result = logic.getEvaluationResult(course.id,
                evaluation.name);
        print(result.toString());
    
        // no need to sort, the result should be sorted by default
    
        // check for evaluation details
        assertEquals(evaluation.courseId, result.evaluation.courseId);
        assertEquals(evaluation.name, result.evaluation.name);
        assertEquals(evaluation.startTime, result.evaluation.startTime);
        assertEquals(evaluation.endTime, result.evaluation.endTime);
        assertEquals(evaluation.gracePeriod, result.evaluation.gracePeriod);
        assertEquals(evaluation.instructions, result.evaluation.instructions);
        assertEquals(evaluation.timeZone, result.evaluation.timeZone, 0.1);
        assertEquals(evaluation.p2pEnabled, result.evaluation.p2pEnabled);
        assertEquals(evaluation.published, result.evaluation.published);
    
        // check number of teams
        assertEquals(2, result.teamResults.size());
    
        // check students in team 1.1
        TeamResultBundle team1_1 = result.teamResults.get("Team 1.1");
        assertEquals(4, team1_1.studentResults.size());
    
        int S1_POS = 0;
        int S2_POS = 1;
        int S3_POS = 2;
        int S4_POS = 3;
    
        
        StudentResultBundle srb1 = team1_1.studentResults.get(S1_POS);
        StudentResultBundle srb2 = team1_1.studentResults.get(S2_POS);
        StudentResultBundle srb3 = team1_1.studentResults.get(S3_POS);
        StudentResultBundle srb4 = team1_1.studentResults.get(S4_POS);
        
        StudentAttributes s1 = srb1.student;
        StudentAttributes s2 = srb2.student;
        StudentAttributes s3 = srb3.student;
        StudentAttributes s4 = srb4.student;
    
        assertEquals("student1InCourse1", s1.googleId);
        assertEquals("student2InCourse1", s2.googleId);
        assertEquals("student3InCourse1", s3.googleId);
        assertEquals("student4InCourse1", s4.googleId);
    
        // check self-evaluations of some students
        assertEquals(s1.name, srb1.getSelfEvaluation().details.revieweeName);
        assertEquals(s1.name, srb1.getSelfEvaluation().details.reviewerName);
        assertEquals(s3.name, srb3.getSelfEvaluation().details.revieweeName);
        assertEquals(s3.name, srb3.getSelfEvaluation().details.reviewerName);
    
        // check individual values for s1
        assertEquals(100, srb1.summary.claimedFromStudent);
        assertEquals(100, srb1.summary.claimedToInstructor);
        assertEquals(90, srb1.summary.perceivedToStudent);
        assertEquals(90, srb1.summary.perceivedToInstructor);
        // check some more individual values
        assertEquals(110, srb2.summary.claimedFromStudent);
        assertEquals(NSB, srb3.summary.claimedToInstructor);
        assertEquals(95, srb4.summary.perceivedToStudent);
        assertEquals(96, srb2.summary.perceivedToInstructor);
    
        // check outgoing submissions (s1 more intensely than others)
    
        assertEquals(4, srb1.outgoing.size());
    
        SubmissionAttributes s1_s1 = srb1.outgoing.get(S1_POS);
        assertEquals(100, s1_s1.details.normalizedToInstructor);
        String expected = "justification of student1InCourse1 rating to student1InCourse1";
        assertEquals(expected, s1_s1.justification.getValue());
        expected = "student1InCourse1 view of team dynamics";
        assertEquals(expected, s1_s1.p2pFeedback.getValue());
    
        SubmissionAttributes s1_s2 = srb1.outgoing.get(S2_POS);
        assertEquals(100, s1_s2.details.normalizedToInstructor);
        expected = "justification of student1InCourse1 rating to student2InCourse1";
        assertEquals(expected, s1_s2.justification.getValue());
        expected = "comments from student1InCourse1 to student2InCourse1";
        assertEquals(expected, s1_s2.p2pFeedback.getValue());
    
        assertEquals(100, srb1.outgoing.get(S3_POS).details.normalizedToInstructor);
        assertEquals(100, srb1.outgoing.get(S4_POS).details.normalizedToInstructor);
    
        assertEquals(NSU, srb2.outgoing.get(S3_POS).details.normalizedToInstructor);
        assertEquals(100, srb2.outgoing.get(S4_POS).details.normalizedToInstructor);
        assertEquals(NSB, srb3.outgoing.get(S2_POS).details.normalizedToInstructor);
        assertEquals(84, srb4.outgoing.get(S2_POS).details.normalizedToInstructor);
    
        // check incoming submissions (s2 more intensely than others)
    
        assertEquals(4, srb1.incoming.size());
        assertEquals(90, srb1.incoming.get(S1_POS).details.normalizedToStudent);
        assertEquals(100, srb1.incoming.get(S4_POS).details.normalizedToStudent);
    
        SubmissionAttributes s2_s1 = srb1.incoming.get(S2_POS);
        assertEquals(96, s2_s1.details.normalizedToStudent);
        expected = "justification of student2InCourse1 rating to student1InCourse1";
        assertEquals(expected, s2_s1.justification.getValue());
        expected = "comments from student2InCourse1 to student1InCourse1";
        assertEquals(expected, s2_s1.p2pFeedback.getValue());
        assertEquals(115, srb2.incoming.get(S4_POS).details.normalizedToStudent);
    
        SubmissionAttributes s3_s1 = srb1.incoming.get(S3_POS);
        assertEquals(113, s3_s1.details.normalizedToStudent);
        assertEquals("", s3_s1.justification.getValue());
        assertEquals("", s3_s1.p2pFeedback.getValue());
        assertEquals(113, srb3.incoming.get(S3_POS).details.normalizedToStudent);
    
        assertEquals(108, srb4.incoming.get(S3_POS).details.normalizedToStudent);
    
        // check team 1.2
        TeamResultBundle team1_2 = result.teamResults.get("Team 1.2");
        assertEquals(1, team1_2.studentResults.size());
        StudentResultBundle team1_2studentResult = team1_2.studentResults.get(0);
        assertEquals(NSB, team1_2studentResult.summary.claimedFromStudent);
        assertEquals(1, team1_2studentResult.outgoing.size());
        assertEquals(NSB, team1_2studentResult.summary.claimedToInstructor);
        assertEquals(NSB, team1_2studentResult.outgoing.get(0).points);
        assertEquals(NA, team1_2studentResult.incoming.get(0).details.normalizedToStudent);
    
        ______TS("null parameters");
    
        try {
            logic.getEvaluationResult("valid.course.id", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("non-existent course");
    
        TestHelper.verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
                course.id, "non existent evaluation" });
    
        TestHelper.verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
                "non-existent-course", "any name" });
    
        ______TS("data used in UI tests");
    
        // @formatter:off
    
        TestHelper.createNewEvaluationWithSubmissions("courseForTestingER", "Eval 1",
                new int[][] { 
                { 110, 100, 110 }, 
                {  90, 110, NSU },
                {  90, 100, 110 } });
        // @formatter:on
    
        result = logic.getEvaluationResult("courseForTestingER", "Eval 1");
        print(result.toString());
    
    }

    @Test
    public void testGetEvaluationResultSummaryAsCsv() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        String methodName = "getEvaluationResultSummaryAsCsv";
        Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
    
        ______TS("typical case");
    
        restoreTypicalDataInDatastore();
    
        
        
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        
        String export = logic.getEvaluationResultSummaryAsCsv(eval.courseId, eval.name);
        
        // This is what export should look like:
        // ==================================
        //Course,,idOfTypicalCourse1
        //Evaluation Name,,evaluation1 In Course1
        //
        //Team,,Student,,Claimed,,Perceived,,Received
        //Team 1.1,,student1 In Course1,,100,,100,,100,100,-9999
        //Team 1.1,,student2 In Course1,,-999,,100,,100,-9999,-9999
        //Team 1.1,,student3 In Course1,,-999,,100,,100,-9999,-9999
        //Team 1.1,,student4 In Course1,,-999,,100,,100,-9999,-9999
        //Team 1.2,,student5 In Course1,,-999,,-9999,,
        
        String[] exportLines = export.split(Const.EOL);
        assertEquals("Course,\"" + eval.courseId + "\"", exportLines[0]);
        assertEquals("Evaluation Name,\"" + eval.name + "\"", exportLines[1]);
        assertEquals("", exportLines[2]);
        assertEquals("Team,Student,Claimed,Perceived,Received", exportLines[3]);
        assertEquals("\"Team 1.1\",\"student1 In Course1\",\"100\",\"100\",\"100,100,N/A\"", exportLines[4]);
        assertEquals("\"Team 1.1\",\"student2 In Course1\",\"Not Submitted\",\"100\",\"100,N/A,N/A\"", exportLines[5]);
        assertEquals("\"Team 1.1\",\"student3 In Course1\",\"Not Submitted\",\"100\",\"100,N/A,N/A\"", exportLines[6]);
        assertEquals("\"Team 1.1\",\"student4 In Course1\",\"Not Submitted\",\"100\",\"100,N/A,N/A\"", exportLines[7]);
        assertEquals("\"Team 1.2\",\"student5 In Course1\",\"Not Submitted\",\"N/A\",\"\"", exportLines[8]);
        
        ______TS("Non-existent Course/Eval");
        
        TestHelper.verifyEntityDoesNotExistException(methodName, paramTypes,
                new Object[] { "non.existent", "Non Existent" });
        
        ______TS("Null parameters");
        
        try {
            logic.getEvaluationResultSummaryAsCsv(null, eval.name);
            Assert.fail();
        } catch (AssertionError ae) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, ae.getMessage());
        }
        
        try {
            logic.getEvaluationResultSummaryAsCsv(eval.courseId, null);
            Assert.fail();
        } catch (AssertionError ae) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, ae.getMessage());
        }
    }
    
    @Test
    public void testGetCourseStudentListAsCsv() throws Exception {
        ______TS("typical case");
    
        restoreTypicalDataInDatastore();    
        
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        String instructorGoogleId = dataBundle.instructors.get("instructor1OfCourse1").googleId;
        
        String export = logic.getCourseStudentListAsCsv(course.id, instructorGoogleId);
        
        // This is what export should look like:
        // ==================================
        //Course ID,"idOfTypicalCourse1"
        //Course Name,"Typical Course 1 with 2 Evals"
        //
        //
        //Team,Student Name,Status,Email
        //"Team 1.1","student1 In Course1","Joined","student1InCourse1@gmail.com"
        //"Team 1.1","student2 In Course1","Joined","student2InCourse1@gmail.com"
        //"Team 1.1","student3 In Course1","Joined","student3InCourse1@gmail.com"
        //"Team 1.1","student4 In Course1","Joined","student4InCourse1@gmail.com"
        //"Team 1.2","student5 In Course1","Joined","student5InCourse1@gmail.com"
        
        String[] exportLines = export.split(Const.EOL);
        assertEquals("Course ID," + "\"" + course.id + "\"", exportLines[0]);
        assertEquals("Course Name," + "\"" + course.name + "\"", exportLines[1]);
        assertEquals("", exportLines[2]);
        assertEquals("", exportLines[3]);
        assertEquals("Team,Student Name,Status,Email", exportLines[4]);
        assertEquals("\"Team 1.1\",\"student1 In Course1\",\"Joined\",\"student1InCourse1@gmail.com\"", exportLines[5]);
        assertEquals("\"Team 1.1\",\"student2 In Course1\",\"Joined\",\"student2InCourse1@gmail.com\"", exportLines[6]);
        assertEquals("\"Team 1.1\",\"student3 In Course1\",\"Joined\",\"student3InCourse1@gmail.com\"", exportLines[7]);
        assertEquals("\"Team 1.1\",\"student4 In Course1\",\"Joined\",\"student4InCourse1@gmail.com\"", exportLines[8]);
        assertEquals("\"Team 1.2\",\"student5 In Course1\",\"Joined\",\"student5InCourse1@gmail.com\"", exportLines[9]);

        ______TS("Null parameters");
        
        try {
            logic.getCourseStudentListAsCsv(null, instructorGoogleId);
            Assert.fail();
        } catch (AssertionError ae) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, ae.getMessage());
        }
        
        try {
            logic.getCourseStudentListAsCsv(course.id, null);
            Assert.fail();
        } catch (AssertionError ae) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, ae.getMessage());
        }
    }

    @Test
    public void testGetEvauationResultForStudent() throws Exception {
    
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        EvaluationAttributes evaluation = dataBundle.evaluations
                .get("evaluation1InCourse1");
        String student1email = "student1InCourse1@gmail.com";
    
        restoreTypicalDataInDatastore();
        
        String methodName = "getEvaluationResultForStudent";
        Class<?>[] paramTypes = new Class<?>[] { String.class, String.class,
                String.class };
    
        ______TS("typical case");
    
        // reconfigure points of an existing evaluation in the datastore
        restoreTypicalDataInDatastore();
        course = dataBundle.courses.get("typicalCourse1");
        evaluation = dataBundle.evaluations.get("evaluation1InCourse1");
        student1email = "student1InCourse1@gmail.com";
    
        
    
        // @formatter:off
        TestHelper.setPointsForSubmissions(new int[][] 
                { { 100, 100, 100, 100 },
                  { 110, 110, NSU, 110 }, 
                  { NSB, NSB, NSB, NSB },
                  { 70, 80, 110, 120 } });
        // @formatter:on
    
        // "idOfCourse1OfInstructor1", "evaluation1 In Course1",
    
        StudentResultBundle result = logic.getEvaluationResultForStudent(course.id,
                evaluation.name, student1email);
    
        // expected result:
        // [100, 100, 100, 100]
        // [100, 100, NSU, 100]
        // [NSB, NSB, NSB, NSB]
        // [74, 84, 116, 126]
        // =======================
        // [91, 96, 114, 100]
        // =======================
        // [91, 96, 114, 100]
        // [105, 110, 131, 115]
        // [91, 96, 114, 100]
        // [86, 91, 108, 95]
    
        // check calculated values
        assertEquals(student1email, result.getOwnerEmail());
        assertEquals(100, result.summary.claimedFromStudent);
        assertEquals(100, result.summary.claimedToInstructor);
        assertEquals(90, result.summary.perceivedToInstructor);
        assertEquals(90, result.summary.perceivedToStudent);
        int teamSize = 4;
    
        // check size of submission lists
        assertEquals(teamSize, result.outgoing.size());
        assertEquals(teamSize, result.incoming.size());
        assertEquals(teamSize, result.selfEvaluations.size());
    
        // check reviewee of incoming
        assertEquals("student1InCourse1@gmail.com",
                result.outgoing.get(0).reviewee);
        assertEquals("student2InCourse1@gmail.com",
                result.outgoing.get(1).reviewee);
        assertEquals("student3InCourse1@gmail.com",
                result.outgoing.get(2).reviewee);
        assertEquals("student4InCourse1@gmail.com",
                result.outgoing.get(3).reviewee);
    
        // check sorting of 'incoming' (should be sorted feedback)
        String feedback1 = result.incoming.get(0).p2pFeedback.getValue();
        String feedback2 = result.incoming.get(1).p2pFeedback.getValue();
        String feedback3 = result.incoming.get(2).p2pFeedback.getValue();
        String feedback4 = result.incoming.get(3).p2pFeedback.getValue();
        assertTrue(0 > feedback1.compareTo(feedback2));
        assertTrue(0 > feedback2.compareTo(feedback3));
        assertTrue(0 > feedback3.compareTo(feedback4));
    
        // check reviewer of outgoing
        assertEquals("student3InCourse1@gmail.com",
                result.incoming.get(0).reviewer);
        assertEquals("student2InCourse1@gmail.com",
                result.incoming.get(1).reviewer);
        assertEquals("student4InCourse1@gmail.com",
                result.incoming.get(2).reviewer);
        assertEquals("student1InCourse1@gmail.com",
                result.incoming.get(3).reviewer);
    
        // check some random values from submission lists
        assertEquals(100, result.outgoing.get(1).points); // reviewee=student2
        assertEquals(NSB, result.incoming.get(0).points); // reviewer=student3
        assertEquals(113, result.incoming.get(0).details.normalizedToStudent); // reviewer=student3
        assertEquals(
                "justification of student1InCourse1 rating to student1InCourse1",
                result.selfEvaluations.get(0).justification.getValue()); // student2
    
        ______TS("null parameter");
    
        try {
            logic.getEvaluationResultForStudent("valid.course.id", "valid evaluation name", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("non-existent course");
    
        TestHelper.verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
                "non-existent-course", evaluation.name, student1email });
    
        ______TS("non-existent evaluation");
    
        TestHelper.verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
                course.id, "non existent eval", student1email });
    
        ______TS("non-existent student");
    
        try {
            logic.getEvaluationResultForStudent(course.id, evaluation.name,
                    "non-existent@email.com");
            Assert.fail();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("non-existent@email.com", e
                    .getMessage().toLowerCase());
        }
    
        ______TS("student added after evaluation");
    
        // testcreateStudentWithSubmissionAdjustment verifies adding student mid-evaluation creates
        //   additional submissions correctly. No need to check here.
    
    }

    @Test
    public void testUpdateEvaluation() throws Exception {

        restoreTypicalDataInDatastore();

        EvaluationAttributes eval = new EvaluationAttributes();
        eval.courseId = "idOfTypicalCourse1";
        eval.name = "new evaluation";
        eval.instructions = new Text("inst");
        Date dummyTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
        eval.startTime = dummyTime;
        eval.endTime = dummyTime;

        ______TS("typical case");

        restoreTypicalDataInDatastore();

        

        eval = dataBundle.evaluations.get("evaluation1InCourse1");
        eval.gracePeriod = eval.gracePeriod + 1;
        eval.instructions = new Text(eval.instructions.getValue() + "x");
        eval.p2pEnabled = (!eval.p2pEnabled);
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
        //we don't modify derived attributes here because they cannot be updated this way.
        TestHelper.invokeEditEvaluation(eval);

        TestHelper.verifyPresentInDatastore(eval);

        ______TS("null parameters");

        try {
            logic.updateEvaluation(null,
                                    "valid evaluation name",
                                    "valid instructions",
                                    new Date(),
                                    new Date(),
                                    1.00,
                                    1,
                                    true);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }

        ______TS("invalid parameters");

        // make the evaluation invalid (end time is before start time)
        eval.timeZone = 0;
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
        try {
            TestHelper.invokeEditEvaluation(eval);
            Assert.fail();
        } catch (InvalidParametersException e) {
            String errorMessage = String.format(TIME_FRAME_ERROR_MESSAGE,
                    END_TIME_FIELD_NAME, EVALUATION_NAME, START_TIME_FIELD_NAME) ;
            assertEquals(errorMessage, e.getMessage());
        }

        // Checking for other type of invalid parameter situations
        // is done in EvaluationDataTest

    }

    @Test
    public void testPublishAndUnpublishEvaluation() throws Exception {

        restoreTypicalDataInDatastore();

        String[] methodNames = new String[] { "publishEvaluation",
                "unpublishEvaluation" };
        Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
        Object[] params = new Object[] { "idOfTypicalCourse1",
                "new evaluation" };


        ______TS("typical cases");

        restoreTypicalDataInDatastore();

        

        EvaluationAttributes eval1 = dataBundle.evaluations
                .get("evaluation1InCourse1");
        // ensure not published yet
        assertEquals(false,
                logic.getEvaluation(eval1.courseId, eval1.name).published);
        // ensure CLOSED
        eval1.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        assertEquals(EvalStatus.CLOSED, eval1.getStatus());
        BackDoorLogic backDoorLogic = new BackDoorLogic();
        backDoorLogic.updateEvaluation(eval1);

        logic.publishEvaluation(eval1.courseId, eval1.name);
        assertEquals(true,
                logic.getEvaluation(eval1.courseId, eval1.name).published);

        logic.unpublishEvaluation(eval1.courseId, eval1.name);
        assertEquals(false,
                logic.getEvaluation(eval1.courseId, eval1.name).published);

        
        ______TS("Trying to publish an already published evaluation");
        
        //Publish evaluation once
        logic.publishEvaluation(eval1.courseId, eval1.name);
        assertEquals(true,logic.getEvaluation(eval1.courseId, eval1.name).published);
        
        //Publish the same evaluation again
        logic.publishEvaluation(eval1.courseId, eval1.name);
        assertEquals(true,logic.getEvaluation(eval1.courseId, eval1.name).published);

        
        ______TS("not ready for publishing");

        // make the evaluation OPEN
        eval1.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        assertEquals(EvalStatus.OPEN, eval1.getStatus());
        backDoorLogic.updateEvaluation(eval1);

        try {
            logic.publishEvaluation(eval1.courseId, eval1.name);
            Assert.fail();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(Const.StatusCodes.PUBLISHED_BEFORE_CLOSING,
                    e.errorCode);
        }

        // ensure evaluation stays in the same state
        assertEquals(EvalStatus.OPEN,
                logic.getEvaluation(eval1.courseId, eval1.name).getStatus());

        

        ______TS("Try to unpublish an already unpublished evaluation");

        //Close and publish the evaluation first
        eval1.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        assertEquals(EvalStatus.CLOSED, eval1.getStatus());
        backDoorLogic.updateEvaluation(eval1);
        
        logic.publishEvaluation(eval1.courseId, eval1.name);
        assertEquals(true,logic.getEvaluation(eval1.courseId, eval1.name).published);
        
        //Unpublish the evaluation
        logic.unpublishEvaluation(eval1.courseId, eval1.name);
        assertEquals(false,logic.getEvaluation(eval1.courseId, eval1.name).published);
        
        //Try to unpublish it again
        logic.unpublishEvaluation(eval1.courseId, eval1.name);
        assertEquals(false,logic.getEvaluation(eval1.courseId, eval1.name).published);
        
        
        
        ______TS("non-existent");

        for (int i = 0; i < params.length; i++) {
            TestHelper.verifyEntityDoesNotExistException(methodNames[i], paramTypes,
                    new Object[] { "non-existent", "non-existent" });
        }
        ______TS("null parameters");
        
        // Same as entity does not exist
        try {
            logic.publishEvaluation(null, eval1.name);
            Assert.fail();
        } catch (AssertionError a) {
        }
        
        try {
            logic.unpublishEvaluation(eval1.courseId, null);
            Assert.fail();
        } catch (AssertionError a) {
        }

    }


    
    
    @Test
    public void testDeleteEvaluation() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        ______TS("typical delete");
    
        restoreTypicalDataInDatastore();
        
    
        EvaluationAttributes eval = dataBundle.evaluations
                .get("evaluation1InCourse1");
        TestHelper.verifyPresentInDatastore(eval);
        // verify there are submissions under this evaluation
        SubmissionAttributes submission = dataBundle.submissions
                .get("submissionFromS1C1ToS1C1");
        TestHelper.verifyPresentInDatastore(submission);
    
        logic.deleteEvaluation(eval.courseId, eval.name);
        TestHelper.verifyAbsentInDatastore(eval);
        // verify submissions are deleted too
        ArrayList<SubmissionAttributes> submissionsOfEvaluation = new ArrayList<SubmissionAttributes>(dataBundle.submissions.values());
        for (SubmissionAttributes s : submissionsOfEvaluation) {
            if (s.evaluation.equals(eval.name)) {
                TestHelper.verifyAbsentInDatastore(s);
            }
        }
    
        ______TS("null parameters");
    
        try {
            logic.deleteEvaluation("valid.course.id", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("non-existent");
    
        // should fail silently
        logic.deleteEvaluation("non-existent", eval.name);
        logic.deleteEvaluation(eval.courseId, "non-existent");
    
    }

    
    @SuppressWarnings("unused")
    private void ____SUBMISSION_level_methods_______________________________() {
    }

    @Test
    public void testCreateSubmission() {
        // method not implemented
    }


    @Test
    public void testGetSubmissionsForEvaluationFromStudent() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        ______TS("typical case");
    
        restoreTypicalDataInDatastore();
    
        
    
        EvaluationAttributes evaluation = dataBundle.evaluations
                .get("evaluation1InCourse1");
        // reuse this evaluation data to create a new one
        evaluation.name = "new evaluation";
        logic.createEvaluationWithoutSubmissionQueue(evaluation);
        // this is the student we are going to check
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
    
        List<SubmissionAttributes> submissions = logic.getSubmissionsForEvaluationFromStudent(
                evaluation.courseId, evaluation.name, student.email);
        // there should be 4 submissions as this student is in a 4-person team
        assertEquals(4, submissions.size());
        // verify they all belong to this student
        for (SubmissionAttributes s : submissions) {
            assertEquals(evaluation.courseId, s.course);
            assertEquals(evaluation.name, s.evaluation);
            assertEquals(student.email, s.reviewer);
            assertEquals(student.name, s.details.reviewerName);
            assertEquals(logic.getStudentForEmail(evaluation.courseId, s.reviewee).name,
                    s.details.revieweeName);
        }
    
        ______TS("orphan submissions");
    
        //Move student to a new team
        student.team = "Team 1.3";
        logic.updateStudent(student.email, student);
        
        submissions = logic.getSubmissionsForEvaluationFromStudent(
                evaluation.courseId, evaluation.name, student.email);
        //There should be 1 submission as he is now in a 1-person team.
        //   Orphaned submissions from previous team should not be returned.
                assertEquals(1, submissions.size());
                
        // Move the student out and move in again
        student.team = "Team 1.4";
        logic.updateStudent(student.email, student);
        student.team = "Team 1.3";
        logic.updateStudent(student.email, student);
        submissions = logic.getSubmissionsForEvaluationFromStudent(evaluation.courseId,
                evaluation.name, student.email);
        assertEquals(1, submissions.size());
    
        ______TS("null parameters");
        
        try {
            logic.getSubmissionsForEvaluationFromStudent("valid.course.id", "valid evaluation name", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("course/evaluation/student does not exist");
    
        
        assertEquals(0, logic.getSubmissionsForEvaluationFromStudent(
                "non-existent", evaluation.name, student.email ).size());
    
        assertEquals(0, logic.getSubmissionsForEvaluationFromStudent(
                evaluation.courseId, "non-existent", student.email ).size());
    
        assertEquals(0, logic.getSubmissionsForEvaluationFromStudent(
                evaluation.courseId, evaluation.name, "non-existent" ).size());
    }

    @Test
    public void testHasStudentSubmittedEvaluation() throws Exception {
    
        EvaluationAttributes evaluation = dataBundle.evaluations
                .get("evaluation1InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
    
        restoreTypicalDataInDatastore();
    
        ______TS("student has submitted");
    
        
    
        assertEquals(true, logic.hasStudentSubmittedEvaluation(
                evaluation.courseId, evaluation.name, student.email));
    
        ______TS("student has not submitted");
    
        // create a new evaluation reusing data from previous one
        evaluation.name = "New evaluation";
        logic.createEvaluation(evaluation);
        assertEquals(false, logic.hasStudentSubmittedEvaluation(
                evaluation.courseId, evaluation.name, student.email));
    
        ______TS("null parameters");
    
        try {
            logic.hasStudentSubmittedEvaluation("valid.course.id", "valid evaluation name", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("non-existent course/evaluation/student");
    
        assertEquals(false, logic.hasStudentSubmittedEvaluation(
                "non-existent-course", evaluation.name, student.email));
        assertEquals(false, logic.hasStudentSubmittedEvaluation(
                evaluation.courseId, "non-existent-eval", student.email));
        assertEquals(false, logic.hasStudentSubmittedEvaluation(
                evaluation.courseId, evaluation.name, "non-existent@student"));
    
    }

    @Test
    public void testUpdateSubmissions() throws Exception {

        ______TS("typical cases");

        restoreTypicalDataInDatastore();
        

        ArrayList<SubmissionAttributes> submissionContainer = new ArrayList<SubmissionAttributes>();

        // try without empty list. Nothing should happen
        logic.updateSubmissions(submissionContainer);

        SubmissionAttributes sub1 = dataBundle.submissions
                .get("submissionFromS1C1ToS2C1");

        SubmissionAttributes sub2 = dataBundle.submissions
                .get("submissionFromS2C1ToS1C1");

        // checking editing of one of the submissions
        TestHelper.alterSubmission(sub1);

        submissionContainer.add(sub1);
        logic.updateSubmissions(submissionContainer);

        TestHelper.verifyPresentInDatastore(sub1);
        TestHelper.verifyPresentInDatastore(sub2);

        // check editing both submissions
        TestHelper.alterSubmission(sub1);
        TestHelper.alterSubmission(sub2);

        submissionContainer = new ArrayList<SubmissionAttributes>();
        submissionContainer.add(sub1);
        submissionContainer.add(sub2);
        logic.updateSubmissions(submissionContainer);

        TestHelper.verifyPresentInDatastore(sub1);
        TestHelper.verifyPresentInDatastore(sub2);

        ______TS("non-existent evaluation");

        // already tested under testUpdateSubmission()

        ______TS("null parameter");

        try {
            logic.updateSubmissions(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }



    @Test
    public void testDeleteSubmission() {
        // method not implemented
    }
    
    /* TODO: implement tests for the following :
     * 1. getFeedbackSessionDetails()
     * 2. getFeedbackSessionsListForInstructor()
     */
    
    @SuppressWarnings("unused")
    private void ____COMMENT_level_methods_____________________________() {
        //The tests here are only for null params check,
        //the rest are done in CommentsLogicTest
    }
    
    @Test
    public void testCreateComment() throws Exception{
        ______TS("null parameters");
        
        try {
            logic.createComment(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        
    }
    
    @Test
    public void testUpdateComment() throws Exception{
        ______TS("null parameters");
        
        try {
            logic.updateComment(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }
    
    @Test
    public void testDeleteComment() throws Exception{
        ______TS("null parameters");
        
        try {
            logic.deleteComment(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }
    
    @Test
    public void testGetCommentsForGiver() throws Exception{
        ______TS("null parameters");
        
        try {
            logic.getCommentsForGiver(null, "giver@mail.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        try {
            logic.getCommentsForGiver("course-id", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }
    
    @Test
    public void testGetCommentsForReceiver() throws Exception{
        ______TS("null parameters");
        
        try {
            logic.getCommentsForReceiver(null, "receiver@mail.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        try {
            logic.getCommentsForReceiver("course-id", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }
    
    @Test
    public void testGetCommentsForGiverAndReceiver() throws Exception{
        ______TS("null parameters");
        
        try {
            logic.getCommentsForGiverAndReceiver(null, "giver@mail.com", "receiver@mail.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        try {
            logic.getCommentsForGiverAndReceiver("course-id", null, "receiver@mail.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        try {
            logic.getCommentsForGiverAndReceiver("course-id", "giver@mail.com", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(Logic.class);
    }

}
