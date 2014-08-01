package teammates.test.cases.testdriver;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Arrays;
import java.util.HashMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Utils;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.BackDoor;
import teammates.test.util.Priority;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;

@Priority(2)
public class BackDoorTest extends BaseTestCase {

    private static Gson gson = Utils.getTeammatesGson();
    private static DataBundle dataBundle = getTypicalDataBundle();
    private static String jsonString = gson.toJson(dataBundle);

    @BeforeClass
    public static void setUp() throws Exception {
        printTestClassHeader();
        dataBundle = getTypicalDataBundle();
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, BackDoor.removeAndRestoreDataBundleFromDb(dataBundle));
    }

    @SuppressWarnings("unused")
    private void ____SYSTEM_level_methods_________________________________() {
    }
    
    @Priority(-2)
    @Test
    public void testPersistence() {
        // typical bundle should be restored in the @BeforeClass method above
        verifyPresentInDatastore(jsonString);
    }

    @Test
    public void testDeletion() {
        
        // ----------deleting Instructor entities-------------------------
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor2OfCourse2");
        verifyPresentInDatastore(instructor1OfCourse1);
        String status = BackDoor.deleteInstructor(instructor1OfCourse1.courseId, instructor1OfCourse1.email);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(instructor1OfCourse1);
        
        //try to delete again: should indicate as success because delete fails silently.
        status = BackDoor.deleteInstructor(instructor1OfCourse1.email, instructor1OfCourse1.courseId);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        
        // ----------deleting Evaluation entities-------------------------

        // check the existence of a submission that will be deleted along with
        // the evaluation
        SubmissionAttributes subInDeletedEvaluation = dataBundle.submissions
                .get("submissionFromS1C1ToS2C1");
        verifyPresentInDatastore(subInDeletedEvaluation);

        // delete the evaluation and verify it is deleted
        EvaluationAttributes evaluation1InCourse1 = dataBundle.evaluations
                .get("evaluation1InCourse1");
        verifyPresentInDatastore(evaluation1InCourse1);
        status = BackDoor.deleteEvaluation(evaluation1InCourse1.courseId,
                evaluation1InCourse1.name);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(evaluation1InCourse1);

        // verify that the submission is deleted too
        verifyAbsentInDatastore(subInDeletedEvaluation);

        // try to delete the evaluation again, should succeed
        status = BackDoor.deleteEvaluation(evaluation1InCourse1.courseId,
                evaluation1InCourse1.name);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        // verify that the other evaluation in the same course is intact
        EvaluationAttributes evaluation2InCourse1 = dataBundle.evaluations
                .get("evaluation2InCourse1");
        verifyPresentInDatastore(evaluation2InCourse1);

        // ----------deleting Course entities-------------------------

        // #COURSE 2
        CourseAttributes course2 = dataBundle.courses.get("typicalCourse2");
        verifyPresentInDatastore(course2);
        status = BackDoor.deleteCourse(course2.id);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(course2);

        // check if related student entities are also deleted
        StudentAttributes student2InCourse2 = dataBundle.students
                .get("student2InCourse2");
        verifyAbsentInDatastore(student2InCourse2);

        // check if related evaluation entities are also deleted
        EvaluationAttributes evaluation1InCourse2 = dataBundle.evaluations
                .get("evaluation1InCourse2");
        verifyAbsentInDatastore(evaluation1InCourse2);
        
        // #COURSE 1
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        verifyPresentInDatastore(course1);
        status = BackDoor.deleteCourse(course1.id);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(course1);
        
        // check if related student entities are also deleted
        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        verifyAbsentInDatastore(student1InCourse1);
        
        // previously not deleted evaluation should be deleted now since the course has been deleted
        verifyAbsentInDatastore(evaluation2InCourse1);
        
        // #COURSE NO EVALS
        CourseAttributes courseNoEvals = dataBundle.courses.get("courseNoEvals");
        verifyPresentInDatastore(courseNoEvals);
        status = BackDoor.deleteCourse(courseNoEvals.id);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(courseNoEvals);
        
        // ----------deleting Feedback Session entities-------------------------
        // TODO: do proper deletion test
        BackDoor.deleteFeedbackSessions(dataBundle);

    }
    
    @SuppressWarnings("unused")
    private void ____ACCOUNT_level_methods_________________________________() {
    }
    
    @Test
    public void testAccounts() throws Exception{
        
        testCreateAccount();
        testGetAccountAsJson();
        testEditAccount();
        testDeleteAccount();
    }
    
    public void testCreateAccount() {
        AccountAttributes newAccount = dataBundle.accounts.get("instructor1OfCourse1");
        BackDoor.deleteAccount(newAccount.googleId);
        verifyAbsentInDatastore(newAccount);
        BackDoor.createAccount(newAccount);
        verifyPresentInDatastore(newAccount);
    }
    
    public void testGetAccountAsJson() {
        AccountAttributes testAccount = dataBundle.accounts.get("instructor1OfCourse1");
        verifyPresentInDatastore(testAccount);
        String actualString = BackDoor.getAccountAsJson(testAccount.googleId);
        AccountAttributes actualAccount = gson.fromJson(actualString, AccountAttributes.class);
        actualAccount.createdAt = testAccount.createdAt;
        assertEquals(gson.toJson(testAccount), gson.toJson(actualAccount));
    }
    
    public void testEditAccount() {
        AccountAttributes testAccount = dataBundle.accounts.get("instructor1OfCourse1");
        verifyPresentInDatastore(testAccount);
        testAccount.name = "New name";
        testAccount.institute = "NTU";
        BackDoor.editAccount(testAccount);
        verifyPresentInDatastore(testAccount);
    }
    
    public void testDeleteAccount() {
        AccountAttributes testAccount = dataBundle.accounts.get("instructor2OfCourse1");
        BackDoor.createAccount(testAccount);
        verifyPresentInDatastore(testAccount);
        BackDoor.deleteAccount(testAccount.googleId);
        verifyAbsentInDatastore(testAccount);
    }

    @SuppressWarnings("unused")
    private void ____INSTRUCTOR_level_methods_________________________________() {
    }

    public void testDeleteInstructors() {
        // already tested by testPersistenceAndDeletion
    }

    @Test
    public void testCreateInstructor() {
        // only minimal testing because this is a wrapper method for
        // another well-tested method.

        String instructorId = "tmapitt.tcc.instructor";
        String courseId = "tmapitt.tcc.course";
        String name = "Tmapitt testInstr Name";
        String email = "tmapitt@tci.com";
        InstructorAttributes instructor = new InstructorAttributes(instructorId, courseId, name, email);
        
        // Make sure not already inside
        BackDoor.deleteInstructor(courseId, email);
        verifyAbsentInDatastore(instructor);
        
        // Perform creation
        BackDoor.createInstructor(instructor);
        verifyPresentInDatastore(instructor);
        
        // Clean up
        BackDoor.deleteInstructor(courseId, email);
        verifyAbsentInDatastore(instructor);
    }

    public void testGetInstructorAsJson() {
        // already tested by testPersistenceAndDeletion
    }

    public void testDeleteInstructor() {
        // already tested by testPersistenceAndDeletion
    }

    public void testEditInstructor() {
        // method not implemented
    }

    

    @Test
    public void testGetCoursesByInstructorId() throws InvalidParametersException {

        // testing for non-existent instructor
        String[] courses = BackDoor.getCoursesByInstructorId("nonExistentInstructor");
        assertEquals("[]", Arrays.toString(courses));
        
        // Create 2 courses for a new instructor
        String course1 = "AST.TGCBCI.course1";
        String course2 = "AST.TGCBCI.course2";
        BackDoor.deleteCourse(course1);
        BackDoor.deleteCourse(course2);
        String status = BackDoor.createCourse(new CourseAttributes(course1, "tmapit tgcbci c1OfInstructor1"));
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        status = BackDoor.createCourse(new CourseAttributes(course2, "tmapit tgcbci c2OfInstructor1"));
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        
        // create a fresh instructor with relations for the 2 courses
        String instructor1Id = "AST.TGCBCI.instructor1";
        String instructor1name = "AST TGCBCI Instructor";
        String instructor1email = "instructor1@ast.tgcbi";
        BackDoor.deleteAccount(instructor1Id);
        status = BackDoor.createInstructor(new InstructorAttributes(instructor1Id, course1, instructor1name, instructor1email));
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        status = BackDoor.createInstructor(new InstructorAttributes(instructor1Id, course2, instructor1name, instructor1email));
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        //============================================================================
        // Don't be confused by the following: it has no relation with the above instructor/course(s)
        
        // add a course that belongs to a different instructor
        String course3 = "AST.TGCBCI.course3";
        BackDoor.deleteCourse(course3);
        status = BackDoor.createCourse(new CourseAttributes(course3, "tmapit tgcbci c1OfInstructor2"));
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        courses = BackDoor.getCoursesByInstructorId(instructor1Id);
        assertEquals("[" + course1 + ", " + course2 + "]", Arrays.toString(courses));

        BackDoor.deleteInstructor(instructor1email, course1);
        BackDoor.deleteInstructor(instructor1email, course2);
    }

    @SuppressWarnings("unused")
    private void ____COURSE_level_methods_________________________________() {
    }

    @Test
    public void testCreateCourse() throws InvalidParametersException {
        // only minimal testing because this is a wrapper method for
        // another well-tested method.

        String courseId = "tmapitt.tcc.course";
        CourseAttributes course = new CourseAttributes(courseId,
                "Name of tmapitt.tcc.instructor");
        
        // Make sure not already inside
        BackDoor.deleteCourse(courseId);
        verifyAbsentInDatastore(course);
        
        // Perform creation
        BackDoor.createCourse(course);
        verifyPresentInDatastore(course);
        
        // Clean up
        BackDoor.deleteCourse(courseId);
        verifyAbsentInDatastore(course);
    }

    
    public void testGetCourseAsJson() {
        // already tested by testPersistenceAndDeletion
    }
    
    public void testEditCourse() {
        // not implemented
    }

    
    public void testDeleteCourse() {
        // already tested by testPersistenceAndDeletion
    }

    @SuppressWarnings("unused")
    private void ____STUDENT_level_methods_________________________________() {
    }

    @Test
    public void testCreateStudent() throws EnrollException {
        // only minimal testing because this is a wrapper method for
        // another well-tested method.

        StudentAttributes student = new StudentAttributes(
                "section name", "team name", "name of tcs student", "tcsStudent@gmail.com", "",
                "tmapit.tcs.course");
        BackDoor.deleteStudent(student.course, student.email);
        verifyAbsentInDatastore(student);
        BackDoor.createStudent(student);
        verifyPresentInDatastore(student);
        BackDoor.deleteStudent(student.course, student.email);
        verifyAbsentInDatastore(student);
    }

    @Test
    public void testGetKeyForStudent() throws EnrollException {

        StudentAttributes student = new StudentAttributes("sect1", "t1", "name of tgsr student", "tgsr@gmail.com", "", "course1");
        BackDoor.createStudent(student);
        String key = BackDoor.getKeyForStudent(student.course, student.email); 

        // The following is the google app engine description about generating
        // keys.
        //
        // A key can be converted to a string by passing the Key object to
        // str(). The string is "urlsafe"—it uses only characters valid for use in URLs. 
        //
        // RFC3986 definition of a safe url pattern
        // Characters that are allowed in a URI but do not have a reserved
        // purpose are called unreserved. 
        // unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
        String pattern = "(\\w|-|~|.)*";

        String errorMessage = key + "[length=" + key.length() + "][reg="
                + StringHelper.isMatching(key, pattern) + "] is not as expected";
        assertTrue(errorMessage, key.length() > 30 && StringHelper.isMatching(key, pattern));

        // clean up student as this is an orphan entity
        BackDoor.deleteStudent(student.course, student.email);

    }

    public void testGetStudentAsJson() {
        // already tested by testPersistenceAndDeletion
    }

    @Test
    public void testEditStudent() {

        // check for successful edit        
        StudentAttributes student = dataBundle.students.get("student4InCourse1");
        // try to create the entity in case it does not exist
        BackDoor.createStudent(student);
        
        String originalEmail = student.email;
        student.name = "New name";
        student.email = "new@gmail.com";
        student.comments = "new comments";
        student.team = "new team";
        String status = BackDoor.editStudent(originalEmail, student);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyPresentInDatastore(student);

        // test for unsuccessful edit
        student.course = "non-existent";
        status = BackDoor.editStudent(originalEmail, student);
        assertTrue(status.startsWith(Const.StatusCodes.BACKDOOR_STATUS_FAILURE));
        verifyAbsentInDatastore(student);
    }

    public void testDeleteStudent() {
        // already tested by testPersistenceAndDeletion
    }

    @SuppressWarnings("unused")
    private void ____EVALUATION_level_methods______________________________() {
    }

    @Test
    public void testCreateEvaluation() throws InvalidParametersException {
        // only minimal testing because this is a wrapper method for
        // another well-tested method.

        EvaluationAttributes e = new EvaluationAttributes();
        e.courseId = "tmapit.tce.course";
        e.name = "Eval for tmapit.tce.course";
        e.instructions = new Text("inst.");
        e.p2pEnabled = true;
        e.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
        e.timeZone = 8.0;
        e.gracePeriod = 5;
        BackDoor.deleteEvaluation(e.courseId, e.name);
        verifyAbsentInDatastore(e);
        BackDoor.createEvaluation(e);
        verifyPresentInDatastore(e);
        BackDoor.deleteEvaluation(e.courseId, e.name);
        verifyAbsentInDatastore(e);
    }

    public void testGetEvaluationAsJson() {
        // already tested by testPersistenceAndDeletion
    }

    @Test
    public void testEditEvaluation() {

        // check for successful edit
        EvaluationAttributes eval = dataBundle.evaluations
                .get("evaluation1InCourse2");
        
        // try creating the entity to make sure it exists
        BackDoor.createEvaluation(eval);

        eval.gracePeriod = eval.gracePeriod + 1;
        eval.instructions = new Text(eval.instructions + "x");
        eval.p2pEnabled = (!eval.p2pEnabled);
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        eval.activated = (!eval.activated);
        eval.published = (!eval.published);
        eval.timeZone = eval.timeZone + 1.0;

        String status = BackDoor.editEvaluation(eval);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyPresentInDatastore(eval);

        // not testing for unsuccesful edit because this does 
        //  not go through the Logic API (i.e., no error checking done)

    }

    public void testDeleteEvaluation() {
        // already tested by testPersistenceAndDeletion
    }

    @SuppressWarnings("unused")
    private void ____SUBMISSION_level_methods______________________________() {
    }

    public void testCreateSubmission() {
        // not implemented
    }

    public void testGetSubmission() {
        // already tested by testPersistenceAndDeletion
    }

    @Priority(-1)
    @Test
    public void testEditSubmission() {

        // check for successful edit
        SubmissionAttributes submission = dataBundle.submissions
                .get("submissionFromS1C2ToS2C2");
        
        submission.justification = new Text(submission.justification.getValue()    + "x");
        submission.points = submission.points + 10;
        String status = BackDoor.editSubmission(submission);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyPresentInDatastore(submission);

        // test for unsuccessful edit
        String initialReviewer = submission.reviewer;
        submission.reviewer = "non-existent@gmail.com";
        status = BackDoor.editSubmission(submission);
        assertTrue(status.startsWith(Const.StatusCodes.BACKDOOR_STATUS_FAILURE));
        verifyAbsentInDatastore(submission);
        
        submission.reviewer = initialReviewer;
    }

    public void testDeleteSubmission() {
        // not implemented
    }

    @SuppressWarnings("unused")
    private void ____helper_methods_________________________________() {
    }

    

    private void verifyAbsentInDatastore(AccountAttributes account) {
        assertEquals("null", BackDoor.getAccountAsJson(account.googleId));
    }
    
    private void verifyAbsentInDatastore(CourseAttributes course) {
        assertEquals("null", BackDoor.getCourseAsJson(course.id));
    }
    
    private void verifyAbsentInDatastore(InstructorAttributes expectedInstructor) {
        assertEquals("null", BackDoor.getInstructorAsJsonByEmail(expectedInstructor.email, expectedInstructor.courseId));
    }

    private void verifyAbsentInDatastore(StudentAttributes student) {
        assertEquals("null",
                BackDoor.getStudentAsJson(student.course, student.email));
    }

    private void verifyAbsentInDatastore(EvaluationAttributes evaluation1InCourse1) {
        assertEquals("null", BackDoor.getEvaluationAsJson(
                evaluation1InCourse1.courseId, evaluation1InCourse1.name));
    }

    private void verifyAbsentInDatastore(SubmissionAttributes subInDeletedEvaluation) {
        String submissionAsJson = BackDoor.getSubmissionAsJson(
                subInDeletedEvaluation.course,
                subInDeletedEvaluation.evaluation,
                subInDeletedEvaluation.reviewer,
                subInDeletedEvaluation.reviewee);
        assertEquals("null", submissionAsJson);
    }

    
    private void verifyPresentInDatastore(String dataBundleJsonString) {
        Gson gson = Utils.getTeammatesGson();

        DataBundle data = gson.fromJson(dataBundleJsonString, DataBundle.class);
        HashMap<String, AccountAttributes> accounts = data.accounts;
        for (AccountAttributes expectedAccount : accounts.values()) {
            verifyPresentInDatastore(expectedAccount);
        }

        HashMap<String, CourseAttributes> courses = data.courses;
        for (CourseAttributes expectedCourse : courses.values()) {
            verifyPresentInDatastore(expectedCourse);
        }
        
        HashMap<String, InstructorAttributes> instructors = data.instructors;
        for (InstructorAttributes expectedInstructor : instructors.values()) {
            verifyPresentInDatastore(expectedInstructor);
        }

        HashMap<String, StudentAttributes> students = data.students;
        for (StudentAttributes expectedStudent : students.values()) {
            verifyPresentInDatastore(expectedStudent);
        }

        HashMap<String, EvaluationAttributes> evaluations = data.evaluations;
        for (EvaluationAttributes expectedEvaluation : evaluations.values()) {
            verifyPresentInDatastore(expectedEvaluation);
        }

        HashMap<String, SubmissionAttributes> submissions = data.submissions;
        for (SubmissionAttributes expectedSubmission : submissions.values()) {
            verifyPresentInDatastore(expectedSubmission);
        }
    }

    private void verifyPresentInDatastore(SubmissionAttributes expectedSubmission) {
        int tries = 0;
        SubmissionAttributes actualSubmission = null;
        while (tries < 2){
            try {
                String submissionsJsonString = BackDoor.getSubmissionAsJson(
                        expectedSubmission.course, expectedSubmission.evaluation,
                        expectedSubmission.reviewer, expectedSubmission.reviewee);
                actualSubmission = gson.fromJson(submissionsJsonString,
                        SubmissionAttributes.class);
                assertEquals(gson.toJson(expectedSubmission),
                        gson.toJson(actualSubmission));
                break;
            } catch (AssertionError ae) {
                tries += 1;
            }
        }
        assertEquals(gson.toJson(expectedSubmission),
                gson.toJson(actualSubmission));
    }

    private void verifyPresentInDatastore(EvaluationAttributes expectedEvaluation) {
        String evaluationJsonString = BackDoor.getEvaluationAsJson(
                expectedEvaluation.courseId, expectedEvaluation.name);
        EvaluationAttributes actualEvaluation = gson.fromJson(evaluationJsonString,
                EvaluationAttributes.class);
        // equalize id field before comparing (because id field is
        // autogenerated by GAE)
        assertEquals(gson.toJson(expectedEvaluation),
                gson.toJson(actualEvaluation));
    }

    private void verifyPresentInDatastore(StudentAttributes expectedStudent) {
        String studentJsonString = BackDoor.getStudentAsJson(
                expectedStudent.course, expectedStudent.email);
        StudentAttributes actualStudent = gson.fromJson(studentJsonString,
                StudentAttributes.class);
        equalizeIrrelevantData(expectedStudent, actualStudent);
        assertEquals(gson.toJson(expectedStudent), gson.toJson(actualStudent));
    }

    private void verifyPresentInDatastore(CourseAttributes expectedCourse) {
        String courseJsonString = BackDoor.getCourseAsJson(expectedCourse.id);
        CourseAttributes actualCourse = gson.fromJson(courseJsonString,
                CourseAttributes.class);
        // Ignore time field as it is stamped at the time of creation in testing
        actualCourse.createdAt = expectedCourse.createdAt;
        assertEquals(gson.toJson(expectedCourse), gson.toJson(actualCourse));
    }

    private void verifyPresentInDatastore(InstructorAttributes expectedInstructor) {
        String instructorJsonString = BackDoor.getInstructorAsJsonByEmail(expectedInstructor.email, expectedInstructor.courseId);
        InstructorAttributes actualInstructor = gson.fromJson(instructorJsonString, InstructorAttributes.class);
        
        equalizeIrrelevantData(expectedInstructor, actualInstructor);
        assertEquals(gson.toJson(expectedInstructor), gson.toJson(actualInstructor));
    }
    
    private void verifyPresentInDatastore(AccountAttributes expectedAccount) {
        String accountJsonString = BackDoor.getAccountAsJson(expectedAccount.googleId);
        AccountAttributes actualAccount = gson.fromJson(accountJsonString, AccountAttributes.class);
        // Ignore time field as it is stamped at the time of creation in testing
        actualAccount.createdAt = expectedAccount.createdAt;
        
        if (expectedAccount.studentProfile == null) {
            expectedAccount.studentProfile = new StudentProfileAttributes();
            expectedAccount.studentProfile.googleId = expectedAccount.googleId;
        }
        expectedAccount.studentProfile.modifiedDate = actualAccount.studentProfile.modifiedDate;
        assertEquals(gson.toJson(expectedAccount), gson.toJson(actualAccount));
    }
    
    private void equalizeIrrelevantData(
            StudentAttributes expectedStudent,
            StudentAttributes actualStudent) {
        
        // For these fields, we consider null and "" equivalent.
        if ((expectedStudent.googleId == null) && (actualStudent.googleId.equals(""))) {
            actualStudent.googleId = null;
        }
        if ((expectedStudent.team == null) && (actualStudent.team.equals(""))) {
            actualStudent.team = null;
        }
        if ((expectedStudent.comments == null)
                && (actualStudent.comments.equals(""))) {
            actualStudent.comments = null;
        }

        // pretend keys match because the key is generated on the server side
        // and cannot be anticipated
        if ((actualStudent.key != null)) {
            expectedStudent.key = actualStudent.key;
        }
    }
    
    private void equalizeIrrelevantData(
            InstructorAttributes expectedInstructor,
            InstructorAttributes actualInstructor) {
        
        // pretend keys match because the key is generated only before storing into database
        if ((actualInstructor.key != null)) {
            expectedInstructor.key = actualInstructor.key;
        }
    }

    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
}
