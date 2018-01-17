package teammates.test.cases.testdriver;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCaseWithBackDoorApiAccess;
import teammates.test.driver.BackDoor;
import teammates.test.driver.Priority;

/**
 * SUT: {@link BackDoor}.
 */
@Priority(2)
public class BackDoorTest extends BaseTestCaseWithBackDoorApiAccess {

    private DataBundle dataBundle;

    @BeforeClass
    public void classSetup() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);

        // verifies that typical bundle is restored by the above operation
        DataBundle expected = getTypicalDataBundle();
        expected.sanitizeForSaving();
        verifyPresentInDatastore(expected);
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
        fq = dataBundle.feedbackQuestions.get("qn5InSession1InCourse1");
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

    }

    @Test
    public void testCreateAccount() {
        AccountAttributes newAccount = dataBundle.accounts.get("instructor1OfCourse1");

        // Make sure not already inside
        BackDoor.deleteAccount(newAccount.googleId);
        verifyAbsentInDatastore(newAccount);

        // Perform creation
        BackDoor.createAccount(newAccount);
        verifyPresentInDatastore(newAccount);

        // Clean up
        BackDoor.deleteAccount(newAccount.googleId);
        verifyAbsentInDatastore(newAccount);
    }

    @Test
    public void testCreateInstructor() {
        // only minimal testing because this is a wrapper method for
        // another well-tested method.

        String instructorId = "tmapitt.tcc.instructor";
        String courseId = "tmapitt.tcc.course";
        String name = "Tmapitt testInstr Name";
        String email = "tmapitt@tci.tmt";
        InstructorAttributes instructor = InstructorAttributes.builder(instructorId, courseId, name, email)
                .build();

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

    @Test
    public void testCreateCourse() {
        // only minimal testing because this is a wrapper method for
        // another well-tested method.

        String courseId = "tmapitt.tcc.course";
        CourseAttributes course = CourseAttributes
                .builder(courseId, "Name of tmapitt.tcc.instructor", "UTC")
                .build();

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

    @Test
    public void testCreateStudent() {
        // only minimal testing because this is a wrapper method for
        // another well-tested method.

        StudentAttributes student = StudentAttributes
                .builder("tmapit.tcs.course", "name of tcs student", "tcsStudent@gmail.tmt")
                .withSection("section name")
                .withTeam("team name")
                .withComments("")
                .build();
        BackDoor.deleteStudent(student.course, student.email);
        verifyAbsentInDatastore(student);
        BackDoor.createStudent(student);
        verifyPresentInDatastore(student);
        BackDoor.deleteStudent(student.course, student.email);
        verifyAbsentInDatastore(student);
    }

    @Test
    public void testGetEncryptedKeyForStudent() {

        StudentAttributes student = StudentAttributes
                .builder("course1", "name of tgsr student", "tgsr@gmail.tmt")
                .withSection("sect1")
                .withTeam("t1")
                .withComments("")
                .build();

        BackDoor.createStudent(student);
        String key = Const.StatusCodes.BACKDOOR_STATUS_FAILURE;
        int retryLimit = 5;
        while (key.startsWith(Const.StatusCodes.BACKDOOR_STATUS_FAILURE) && retryLimit > 0) {
            key = BackDoor.getEncryptedKeyForStudent(student.course, student.email);
            retryLimit--;
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

}
