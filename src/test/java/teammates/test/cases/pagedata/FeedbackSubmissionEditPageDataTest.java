package teammates.test.cases.pagedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.ui.pagedata.FeedbackSubmissionEditPageData;
import teammates.ui.template.StudentFeedbackSubmissionEditQuestionsWithResponses;

/**
 * SUT: {@link FeedbackSubmissionEditPageData}.
 */
public class FeedbackSubmissionEditPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    private FeedbackSubmissionEditPageData pageData;

    private FeedbackQuestionAttributes question;
    private List<FeedbackResponseAttributes> responses = new ArrayList<>();

    private Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionResponseBundle = new HashMap<>();

    private Map<String, Map<String, String>> recipientList = new HashMap<>();
    private Map<String, String> recipients = new HashMap<>();

    private void createData(StudentAttributes student) {
        FeedbackSessionAttributes feedbackSession = dataBundle.feedbackSessions.get("session1InCourse1");
        question = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");

        responses.add(dataBundle.feedbackResponses.get("response1ForQ1S1C1"));
        responses.add(dataBundle.feedbackResponses.get("response2ForQ1S1C1"));

        // create a dummy questionId for question,
        // otherwise it would be uninitialised as this is normally done by the database
        setDummyQuestionId(question, responses);

        questionResponseBundle.put(question, responses);

        recipients.put(student.email, Const.USER_NAME_FOR_SELF);
        recipientList.put(question.getId(), recipients);
        Map<String, List<FeedbackResponseCommentAttributes>> commentsForResponses = new HashMap<>();
        CourseRoster roster = new CourseRoster(new ArrayList<>(dataBundle.students.values()),
                new ArrayList<>(dataBundle.instructors.values()));
        pageData.bundle = new FeedbackSessionQuestionsBundle(feedbackSession, questionResponseBundle, recipientList,
                commentsForResponses, roster);
        pageData.bundle.questionResponseBundle.put(question, responses);
    }

    private void setDummyQuestionId(
            FeedbackQuestionAttributes question, List<FeedbackResponseAttributes> responses) {
        String dummyQuestionId = "dummy";
        question.setId(dummyQuestionId);
        for (FeedbackResponseAttributes response : responses) {
            response.feedbackQuestionId = dummyQuestionId;
        }
    }

    @Test
    public void testAll() {
        ______TS("test typical case");
        AccountAttributes studentAccount = dataBundle.accounts.get("student1InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");

        pageData = new FeedbackSubmissionEditPageData(studentAccount, student, dummySessionToken);
        createData(student);

        pageData.init(student.key, student.email, student.course);

        assertEquals("You are submitting feedback as <span class='text-danger text-bold text-large'>"
                             + "student1 In Course1</td></div>'\"</span>. "
                             + "You may submit feedback for sessions that are currently open "
                             + "and view results without logging in. "
                             + "To access other features you need <a href='/page/studentCourseJoinAuthentication?"
                             + "studentemail=student1InCourse1%40gmail.tmt&courseid=idOfTypicalCourse1' class='link'>"
                             + "to login using a Google account</a> (recommended).",
                     pageData.getRegisterMessage());

        assertNull(pageData.getSubmitAction());

        assertFalse(pageData.isModeration());
        assertFalse(pageData.isSessionOpenForSubmission());
        assertFalse(pageData.isSubmittable());

        testQuestionAttributes();

        ______TS("student in unregistered course");
        student = dataBundle.students.get("student1InUnregisteredCourse");

        pageData = new FeedbackSubmissionEditPageData(studentAccount, student, dummySessionToken);
        createData(student);

        pageData.init(student.key, student.email, student.course);

        assertEquals("You are submitting feedback as <span class='text-danger text-bold text-large'>student1 "
                      + "In unregisteredCourse</span>. You may submit feedback for sessions that are currently open "
                      + "and view results without logging in. "
                      + "To access other features you need <a href='/page/studentCourseJoinAuthentication?"
                      + "key=regKeyForStuNotYetJoinCourse&studentemail=student1InUnregisteredCourse%40gmail.tmt&"
                      + "courseid=idOfUnregisteredCourse' class='link'>to login using a Google account</a> "
                      + "(recommended).", pageData.getRegisterMessage());

        assertNull(pageData.getSubmitAction());

        assertFalse(pageData.isModeration());
        assertFalse(pageData.isSessionOpenForSubmission());
        assertFalse(pageData.isSubmittable());

        testQuestionAttributes();

        ______TS("student in archived course");
        student = dataBundle.students.get("student1InArchivedCourse");

        pageData = new FeedbackSubmissionEditPageData(studentAccount, student, dummySessionToken);
        createData(student);

        pageData.init(student.key, student.email, student.course);

        assertEquals("You are submitting feedback as <span class='text-danger text-bold text-large'>student1 In Course1"
                      + "</span>. You may submit feedback for sessions that are currently open "
                      + "and view results without logging in. To access other features "
                      + "you need <a href='/page/studentCourseJoinAuthentication?studentemail=student1InArchivedCourse%40"
                      + "gmail.tmt&courseid=idOfArchivedCourse' class='link'>to login using a Google account</a> "
                      + "(recommended).", pageData.getRegisterMessage());

        assertNull(pageData.getSubmitAction());

        assertFalse(pageData.isModeration());
        assertFalse(pageData.isSessionOpenForSubmission());
        assertFalse(pageData.isSubmittable());

        testQuestionAttributes();

        ______TS("student submission open");
        student = dataBundle.students.get("student1InCourse1");

        pageData = new FeedbackSubmissionEditPageData(studentAccount, student, dummySessionToken);
        createData(student);

        pageData.setSessionOpenForSubmission(true);
        pageData.init(student.key, student.email, student.course);

        assertEquals("You are submitting feedback as <span class='text-danger text-bold text-large'>"
                             + "student1 In Course1</td></div>'\"</span>. "
                             + "You may submit feedback for sessions that are currently open "
                             + "and view results without logging in. "
                             + "To access other features you need <a href='/page/studentCourseJoinAuthentication?"
                             + "studentemail=student1InCourse1%40gmail.tmt&courseid=idOfTypicalCourse1' class='link'>"
                             + "to login using a Google account</a> (recommended).",
                     pageData.getRegisterMessage());

        assertNull(pageData.getSubmitAction());

        assertFalse(pageData.isModeration());
        assertTrue(pageData.isSessionOpenForSubmission());
        assertTrue(pageData.isSubmittable());

        ______TS("instructor moderating a response - closed for submission");
        AccountAttributes instructorAccount = dataBundle.accounts.get("instructor1OfCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        student = dataBundle.students.get("student1InCourse1");

        pageData = new FeedbackSubmissionEditPageData(instructorAccount, student, dummySessionToken);
        createData(student);

        pageData.setModeration(true);
        pageData.init("", student.email, student.course);

        assertNull(pageData.getSubmitAction());

        assertTrue(pageData.isModeration());
        assertFalse(pageData.isSessionOpenForSubmission());
        assertTrue(pageData.isSubmittable());

        testQuestionAttributes();

        ______TS("instructor moderating a response - open for submission");
        student = dataBundle.students.get("student1InCourse1");

        pageData = new FeedbackSubmissionEditPageData(instructorAccount, student, dummySessionToken);
        createData(student);

        pageData.setModeration(true);
        pageData.setSessionOpenForSubmission(true);
        pageData.init("", student.email, student.course);

        assertNull(pageData.getSubmitAction());

        assertTrue(pageData.isModeration());
        assertTrue(pageData.isSessionOpenForSubmission());
        assertTrue(pageData.isSubmittable());

        testQuestionAttributes();

        ______TS("instructor previewing a response");
        pageData = new FeedbackSubmissionEditPageData(instructorAccount, student, dummySessionToken);
        createData(student);

        pageData.setPreview(true);
        pageData.setPreviewInstructor(instructor);
        pageData.init("", student.email, student.course);

        assertNull(pageData.getSubmitAction());

        assertFalse(pageData.isModeration());
        assertFalse(pageData.isSessionOpenForSubmission());
        assertFalse(pageData.isSubmittable());

        testQuestionAttributes();
    }

    private void testQuestionAttributes() {
        StudentFeedbackSubmissionEditQuestionsWithResponses questionWithResponses =
                pageData.getQuestionsWithResponses().get(0);

        assertEquals(question.questionType, questionWithResponses.getQuestion().getQuestionType());
        assertEquals(question.courseId, questionWithResponses.getQuestion().getCourseId());
        assertEquals(question.questionNumber, questionWithResponses.getQuestion().getQuestionNumber());
        assertEquals(question.getQuestionDetails().getQuestionText(), questionWithResponses.getQuestion().getQuestionText());
        assertEquals(question.numberOfEntitiesToGiveFeedbackTo,
                     questionWithResponses.getQuestion().getNumberOfEntitiesToGiveFeedbackTo());
        assertEquals(question.getId(), questionWithResponses.getQuestion().getQuestionId());
    }
}
