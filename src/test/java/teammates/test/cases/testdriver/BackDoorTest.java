package teammates.test.cases.testdriver;

import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.BackDoor;
import teammates.test.util.Priority;

import com.google.appengine.api.datastore.Text;

@Priority(2)
public class BackDoorTest extends BaseTestCase {

    private static DataBundle dataBundle = getTypicalDataBundle();
    private static String jsonString = JsonUtils.toJson(dataBundle);

    @BeforeClass
    public static void setUp() {
        printTestClassHeader();
        dataBundle = getTypicalDataBundle();
        int retryLimit = 5;
        String status = Const.StatusCodes.BACKDOOR_STATUS_FAILURE;
        while (status.startsWith(Const.StatusCodes.BACKDOOR_STATUS_FAILURE) && retryLimit > 0) {
            status = BackDoor.removeAndRestoreDataBundleFromDb(dataBundle);
            retryLimit--;
        }
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
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

        // ----------deleting Feedback Response entities-------------------------
        FeedbackQuestionAttributes fq = dataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ2S1C1");
        fq = BackDoor.getFeedbackQuestion(fq.courseId, fq.feedbackSessionName, fq.questionNumber);
        fr = BackDoor.getFeedbackResponse(fq.getId(), fr.giver, fr.recipient);
        
        verifyPresentInDatastore(fr);
        status = BackDoor.deleteFeedbackResponse(fq.getId(), fr.giver, fr.recipient);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(fr);
        
        // ----------deleting Feedback Question entities-------------------------
        fq = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        verifyPresentInDatastore(fq);
        status = BackDoor.deleteFeedbackQuestion(fq.getId());
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(fq);

        // ----------deleting Course entities-------------------------
        // #COURSE 2
        CourseAttributes course2 = dataBundle.courses.get("typicalCourse2");
        verifyPresentInDatastore(course2);
        status = BackDoor.deleteCourse(course2.getId());
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(course2);

        // check if related student entities are also deleted
        StudentAttributes student2InCourse2 = dataBundle.students
                .get("student2InCourse2");
        verifyAbsentInDatastore(student2InCourse2);
        
        // #COURSE 1
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        verifyPresentInDatastore(course1);
        status = BackDoor.deleteCourse(course1.getId());
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(course1);
        
        // check if related student entities are also deleted
        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        verifyAbsentInDatastore(student1InCourse1);

        // #COURSE NO EVALS
        CourseAttributes courseNoEvals = dataBundle.courses.get("courseNoEvals");
        verifyPresentInDatastore(courseNoEvals);
        status = BackDoor.deleteCourse(courseNoEvals.getId());
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(courseNoEvals);
        
        // ----------deleting Feedback Session entities-------------------------
        // TODO: do proper deletion test
        BackDoor.deleteFeedbackSessions(dataBundle);

    }
    
    @Test
    public void testAccounts() {
        
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
        AccountAttributes actualAccount = JsonUtils.fromJson(actualString, AccountAttributes.class);
        actualAccount.createdAt = testAccount.createdAt;
        assertEquals(JsonUtils.toJson(testAccount), JsonUtils.toJson(actualAccount));
    }
    
    public void testEditAccount() {
        AccountAttributes testAccount = dataBundle.accounts.get("instructor1OfCourse1");
        verifyPresentInDatastore(testAccount);
        testAccount.name = "New name";
        testAccount.institute = "TEAMMATES Test Institute 7";
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
        String email = "tmapitt@tci.tmt";
        @SuppressWarnings("deprecation")
        InstructorAttributes instructor = new InstructorAttributes(instructorId, courseId, name, email);
        
        // Make sure not already inside
        BackDoor.deleteInstructor(courseId, email);
        verifyAbsentInDatastore(instructor);
        
        // Perform creation
        BackDoor.createInstructor(instructor);
        verifyPresentInDatastore(instructor);
        instructor = BackDoor.getInstructorByEmail(email, courseId);
        // Clean up
        BackDoor.deleteInstructor(courseId, email);
        BackDoor.deleteAccount(instructor.googleId);
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
    public void testCreateCourse() {
        // only minimal testing because this is a wrapper method for
        // another well-tested method.

        String courseId = "tmapitt.tcc.course";
        CourseAttributes course = new CourseAttributes(courseId,
                "Name of tmapitt.tcc.instructor", "UTC");
        
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

    @Test
    public void testCreateStudent() {
        // only minimal testing because this is a wrapper method for
        // another well-tested method.

        StudentAttributes student = new StudentAttributes(
                "section name", "team name", "name of tcs student", "tcsStudent@gmail.tmt", "",
                "tmapit.tcs.course");
        BackDoor.deleteStudent(student.course, student.email);
        verifyAbsentInDatastore(student);
        BackDoor.createStudent(student);
        verifyPresentInDatastore(student);
        BackDoor.deleteStudent(student.course, student.email);
        verifyAbsentInDatastore(student);
    }

    @Test
    public void testGetEncryptedKeyForStudent() {

        StudentAttributes student = new StudentAttributes("sect1", "t1", "name of tgsr student",
                                                          "tgsr@gmail.tmt", "", "course1");
        BackDoor.createStudent(student);
        String key = "[BACKDOOR_STATUS_FAILURE]";
        while (key.startsWith("[BACKDOOR_STATUS_FAILURE]")) {
            key = BackDoor.getEncryptedKeyForStudent(student.course, student.email);
        }

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
        String pattern = "(\\w|-|~|\\.)*";

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
        verifyPresentInDatastore(student);
        
        String originalEmail = student.email;
        student.name = "New name";
        student.lastName = "name";
        student.email = "new@gmail.tmt";
        student.comments = "new comments";
        student.team = "new team";
        String status = Const.StatusCodes.BACKDOOR_STATUS_FAILURE;
        int retryLimit = 5;
        while (status.startsWith(Const.StatusCodes.BACKDOOR_STATUS_FAILURE) && retryLimit > 0) {
            status = BackDoor.editStudent(originalEmail, student);
            retryLimit--;
        }
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
    
    @Test
    public void testCreateFeedbackResponse() {

        FeedbackResponseAttributes fr = new FeedbackResponseAttributes();
        FeedbackQuestionAttributes fq = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        StudentAttributes student = dataBundle.students.get("student3InCourse1");

        fq = BackDoor.getFeedbackQuestion(fq.courseId, fq.feedbackSessionName, fq.questionNumber);

        fr.feedbackSessionName = fq.feedbackSessionName;
        fr.courseId = fq.courseId;
        fr.feedbackQuestionId = fq.getId();
        fr.feedbackQuestionType = fq.questionType;
        fr.giver = student.email;
        fr.giverSection = student.section;
        fr.recipient = student.email;
        fr.recipientSection = student.section;
        fr.responseMetaData = new Text("Student 3 self feedback");
        fr.setId(fq.getId() + "%" + fr.giver + "%" + fr.recipient);

        // Make sure not already inside
        BackDoor.deleteFeedbackResponse(fr.feedbackQuestionId, fr.giver, fr.recipient);
        verifyAbsentInDatastore(fr);

        // Perform creation
        BackDoor.createFeedbackResponse(fr);
        verifyPresentInDatastore(fr);

        // Clean up
        BackDoor.deleteFeedbackResponse(fr.feedbackQuestionId, fr.giver, fr.recipient);
        verifyAbsentInDatastore(fr);
    }
    
    private void verifyAbsentInDatastore(AccountAttributes account) {
        assertEquals("null", BackDoor.getAccountAsJson(account.googleId));
    }
    
    private void verifyAbsentInDatastore(CourseAttributes course) {
        assertEquals("null", BackDoor.getCourseAsJson(course.getId()));
    }
    
    private void verifyAbsentInDatastore(InstructorAttributes expectedInstructor) {
        assertEquals("null", BackDoor.getInstructorAsJsonByEmail(expectedInstructor.email, expectedInstructor.courseId));
    }

    private void verifyAbsentInDatastore(StudentAttributes student) {
        assertEquals("null",
                BackDoor.getStudentAsJson(student.course, student.email));
    }

    private void verifyAbsentInDatastore(FeedbackQuestionAttributes fq) {
        assertEquals("null", BackDoor.getFeedbackQuestionForIdAsJson(fq.getId()));
    }
    
    private void verifyAbsentInDatastore(FeedbackResponseAttributes fr) {
        assertEquals("null", BackDoor.getFeedbackResponseAsJson(fr.feedbackQuestionId, fr.giver, fr.recipient));
    }

    private void verifyPresentInDatastore(String dataBundleJsonString) {
        DataBundle data = JsonUtils.fromJson(dataBundleJsonString, DataBundle.class);
        Map<String, AccountAttributes> accounts = data.accounts;
        for (AccountAttributes expectedAccount : accounts.values()) {
            verifyPresentInDatastore(expectedAccount);
        }

        Map<String, CourseAttributes> courses = data.courses;
        for (CourseAttributes expectedCourse : courses.values()) {
            verifyPresentInDatastore(expectedCourse);
        }
        
        Map<String, InstructorAttributes> instructors = data.instructors;
        for (InstructorAttributes expectedInstructor : instructors.values()) {
            verifyPresentInDatastore(expectedInstructor);
        }

        Map<String, StudentAttributes> students = data.students;
        for (StudentAttributes expectedStudent : students.values()) {
            verifyPresentInDatastore(expectedStudent);
        }

    }

    private void verifyPresentInDatastore(StudentAttributes expectedStudent) {
        String studentJsonString = "null";
        while ("null".equals(studentJsonString)) {
            studentJsonString = BackDoor.getStudentAsJson(expectedStudent.course, expectedStudent.email);
        }
        StudentAttributes actualStudent = JsonUtils.fromJson(studentJsonString,
                StudentAttributes.class);
        equalizeIrrelevantData(expectedStudent, actualStudent);
        expectedStudent.lastName = StringHelper.splitName(expectedStudent.name)[1];
        assertEquals(JsonUtils.toJson(expectedStudent), JsonUtils.toJson(actualStudent));
    }

    private void verifyPresentInDatastore(CourseAttributes expectedCourse) {
        String courseJsonString = "null";
        while ("null".equals(courseJsonString)) {
            courseJsonString = BackDoor.getCourseAsJson(expectedCourse.getId());
        }
        CourseAttributes actualCourse = JsonUtils.fromJson(courseJsonString,
                CourseAttributes.class);
        // Ignore time field as it is stamped at the time of creation in testing
        actualCourse.createdAt = expectedCourse.createdAt;
        assertEquals(JsonUtils.toJson(expectedCourse), JsonUtils.toJson(actualCourse));
    }

    private void verifyPresentInDatastore(InstructorAttributes expectedInstructor) {
        String instructorJsonString = "null";
        while ("null".equals(instructorJsonString)) {
            instructorJsonString = BackDoor.getInstructorAsJsonByEmail(expectedInstructor.email,
                                                                       expectedInstructor.courseId);
        }
        InstructorAttributes actualInstructor = JsonUtils.fromJson(instructorJsonString, InstructorAttributes.class);
        
        equalizeIrrelevantData(expectedInstructor, actualInstructor);
        assertTrue(expectedInstructor.isEqualToAnotherInstructor(actualInstructor));
    }
    
    private void verifyPresentInDatastore(AccountAttributes expectedAccount) {
        String accountJsonString = BackDoor.getAccountAsJson(expectedAccount.googleId);
        AccountAttributes actualAccount = JsonUtils.fromJson(accountJsonString, AccountAttributes.class);
        // Ignore time field as it is stamped at the time of creation in testing
        actualAccount.createdAt = expectedAccount.createdAt;
        
        if (expectedAccount.studentProfile == null) {
            expectedAccount.studentProfile = new StudentProfileAttributes();
            expectedAccount.studentProfile.googleId = expectedAccount.googleId;
        }
        expectedAccount.studentProfile.modifiedDate = actualAccount.studentProfile.modifiedDate;
        assertEquals(JsonUtils.toJson(expectedAccount), JsonUtils.toJson(actualAccount));
    }

    private void verifyPresentInDatastore(FeedbackQuestionAttributes expectedQuestion) {
        String questionJsonString = BackDoor.getFeedbackQuestionAsJson(expectedQuestion.feedbackSessionName,
                                                                       expectedQuestion.courseId,
                                                                       expectedQuestion.questionNumber);
        FeedbackQuestionAttributes actualQuestion =
                JsonUtils.fromJson(questionJsonString, FeedbackQuestionAttributes.class);
        
        // Match the id of the expected Feedback Question because it is not known in advance
        equalizeId(expectedQuestion, actualQuestion);
        assertEquals(JsonUtils.toJson(expectedQuestion), JsonUtils.toJson(actualQuestion));
    }

    private void verifyPresentInDatastore(FeedbackResponseAttributes expectedResponse) {
        String responseJsonString = BackDoor.getFeedbackResponseAsJson(expectedResponse.feedbackQuestionId,
                                                                       expectedResponse.giver,
                                                                       expectedResponse.recipient);
        FeedbackResponseAttributes actualResponse =
                JsonUtils.fromJson(responseJsonString, FeedbackResponseAttributes.class);

        assertEquals(JsonUtils.toJson(expectedResponse), JsonUtils.toJson(actualResponse));
    }

    private void equalizeIrrelevantData(
            StudentAttributes expectedStudent,
            StudentAttributes actualStudent) {
        
        // For these fields, we consider null and "" equivalent.
        if (expectedStudent.googleId == null && actualStudent.googleId.isEmpty()) {
            actualStudent.googleId = null;
        }
        if (expectedStudent.team == null && actualStudent.team.isEmpty()) {
            actualStudent.team = null;
        }
        if (expectedStudent.comments == null
                && actualStudent.comments.isEmpty()) {
            actualStudent.comments = null;
        }

        // pretend keys match because the key is generated on the server side
        // and cannot be anticipated
        if (actualStudent.key != null) {
            expectedStudent.key = actualStudent.key;
        }
    }
    
    private void equalizeIrrelevantData(
            InstructorAttributes expectedInstructor,
            InstructorAttributes actualInstructor) {
        
        // pretend keys match because the key is generated only before storing into database
        if (actualInstructor.key != null) {
            expectedInstructor.key = actualInstructor.key;
        }
    }

    private void equalizeId(
            FeedbackQuestionAttributes expectedFeedbackQuestion,
            FeedbackQuestionAttributes actualFeedbackQuestion) {

        expectedFeedbackQuestion.setId(actualFeedbackQuestion.getId());
    }

    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
}
