package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.FeedbackSubmissionEditPageData;
import teammates.ui.template.StudentFeedbackSubmissionEditQuestionsWithResponses;

public class FeedbackSubmissionEditPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    private FeedbackSubmissionEditPageData pageData;
    
    private FeedbackSessionAttributes feedbackSession;
    private FeedbackQuestionAttributes question;
    private List<FeedbackResponseAttributes> responses = new ArrayList<FeedbackResponseAttributes>();
    
    private Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionResponseBundle = 
                                    new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
    
    private Map<String, Map<String, String>> recipientList = new HashMap<String, Map<String, String>>();
    private Map<String,String> recipients = new HashMap<String,String>();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    public void createData(StudentAttributes student) {        
        feedbackSession = dataBundle.feedbackSessions.get("session1InCourse1");
        question = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");      
        
        responses.add(dataBundle.feedbackResponses.get("response1ForQ1S1C1"));
        responses.add(dataBundle.feedbackResponses.get("response2ForQ1S1C1"));      
        
        questionResponseBundle.put(question, responses);      
        
        recipients.put(student.email, Const.USER_NAME_FOR_SELF);
        recipientList.put(question.getId(), recipients);
        
        pageData.bundle = new FeedbackSessionQuestionsBundle(feedbackSession, questionResponseBundle, recipientList);
        pageData.bundle.questionResponseBundle.put(question, responses);
    }   
    
    @Test
    public void testAll() {
        ______TS("test typical case"); 
        AccountAttributes studentAccount = dataBundle.accounts.get("student1InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        
        pageData = new FeedbackSubmissionEditPageData(studentAccount, student);
        createData(student);         
        
        pageData.init(student.key, student.email, student.course);
        
        assertEquals("You are submitting feedback as <span class='text-danger text-bold text-large'>"
                      + "student1 In Course1</span>. You may submit feedback and view results without logging "
                      + "in. To access other features you need <a href='/page/studentCourseJoinAuthentication?"
                      + "studentemail=student1InCourse1%40gmail.tmt&courseid=idOfTypicalCourse1' class='link'>"
                      + "to login using a google account</a> (recommended).", pageData.getRegisterMessage());
        
        assertEquals("/page/studentFeedbackSubmissionEditSave", pageData.getSubmitAction());
        assertEquals("/page/studentFeedbackQuestionSubmissionEditSave", pageData.getSubmitActionQuestion());
        
        assertFalse(pageData.isModeration());
        assertFalse(pageData.isSessionOpenForSubmission());
        assertFalse(pageData.isSubmittable());
        
        testQuestionAttributes();
        
        ______TS("student in unregistered course");
        student = dataBundle.students.get("student1InUnregisteredCourse");
        
        pageData = new FeedbackSubmissionEditPageData(studentAccount, student);
        createData(student);
        
        pageData.init(student.key, student.email, student.course);
        
        assertEquals("You are submitting feedback as <span class='text-danger text-bold text-large'>student1 "
                      + "In unregisteredCourse</span>. You may submit feedback and view results without logging "
                      + "in. To access other features you need <a href='/page/studentCourseJoinAuthentication?"
                      + "key=regKeyForStuNotYetJoinCourse&studentemail=student1InUnregisteredCourse%40gmail.tmt&"
                      + "courseid=idOfUnregisteredCourse' class='link'>to login using a google account</a> "
                      + "(recommended).", pageData.getRegisterMessage());
        
        assertEquals("/page/studentFeedbackSubmissionEditSave", pageData.getSubmitAction());
        assertEquals("/page/studentFeedbackQuestionSubmissionEditSave", pageData.getSubmitActionQuestion());
        
        assertFalse(pageData.isModeration());
        assertFalse(pageData.isSessionOpenForSubmission());
        assertFalse(pageData.isSubmittable());
        
        testQuestionAttributes();
        
        ______TS("student in archived course");
        student = dataBundle.students.get("student1InArchivedCourse");
        
        pageData = new FeedbackSubmissionEditPageData(studentAccount, student);
        createData(student);
        
        pageData.init(student.key, student.email, student.course);
        
        assertEquals("You are submitting feedback as <span class='text-danger text-bold text-large'>student1 In Course1"
                      + "</span>. You may submit feedback and view results without logging in. To access other features "
                      + "you need <a href='/page/studentCourseJoinAuthentication?studentemail=student1InArchivedCourse%40"
                      + "gmail.tmt&courseid=idOfArchivedCourse' class='link'>to login using a google account</a> "
                      + "(recommended).", pageData.getRegisterMessage());
        
        assertEquals("/page/studentFeedbackSubmissionEditSave", pageData.getSubmitAction());
        assertEquals("/page/studentFeedbackQuestionSubmissionEditSave", pageData.getSubmitActionQuestion());
        
        assertFalse(pageData.isModeration());
        assertFalse(pageData.isSessionOpenForSubmission());
        assertFalse(pageData.isSubmittable());
        
        testQuestionAttributes();
        
        ______TS("student submission open"); 
        student = dataBundle.students.get("student1InCourse1");
        
        pageData = new FeedbackSubmissionEditPageData(studentAccount, student);
        createData(student);         
        
        pageData.setSessionOpenForSubmission(true);
        pageData.init(student.key, student.email, student.course);
        
        assertEquals("You are submitting feedback as <span class='text-danger text-bold text-large'>"
                      + "student1 In Course1</span>. You may submit feedback and view results without logging "
                      + "in. To access other features you need <a href='/page/studentCourseJoinAuthentication?"
                      + "studentemail=student1InCourse1%40gmail.tmt&courseid=idOfTypicalCourse1' class='link'>"
                      + "to login using a google account</a> (recommended).", pageData.getRegisterMessage());
        
        assertEquals("/page/studentFeedbackSubmissionEditSave", pageData.getSubmitAction());
        assertEquals("/page/studentFeedbackQuestionSubmissionEditSave", pageData.getSubmitActionQuestion());
        
        assertFalse(pageData.isModeration());
        assertTrue(pageData.isSessionOpenForSubmission());
        assertTrue(pageData.isSubmittable());
        
        ______TS("instructor moderating a response - closed for submission");
        AccountAttributes instructorAccount = dataBundle.accounts.get("instructor1OfCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        student = dataBundle.students.get("student1InCourse1");
        
        pageData = new FeedbackSubmissionEditPageData(instructorAccount, student);
        createData(student); 
        
        pageData.setModeration(true);
        pageData.init("", student.email, student.course);
        
        assertEquals("/page/instructorEditStudentFeedbackSave", pageData.getSubmitAction());
        assertEquals("/page/instructorEditStudentFeedbackSave", pageData.getSubmitActionQuestion());
        
        assertTrue(pageData.isModeration());
        assertFalse(pageData.isSessionOpenForSubmission());
        assertTrue(pageData.isSubmittable());
        
        testQuestionAttributes();
        
        ______TS("instructor moderating a response - open for submission");
        student = dataBundle.students.get("student1InCourse1");
        
        pageData = new FeedbackSubmissionEditPageData(instructorAccount, student);
        createData(student); 
        
        pageData.setModeration(true);
        pageData.setSessionOpenForSubmission(true);
        pageData.init("", student.email, student.course);
        
        assertEquals("/page/instructorEditStudentFeedbackSave", pageData.getSubmitAction());
        assertEquals("/page/instructorEditStudentFeedbackSave", pageData.getSubmitActionQuestion());
        
        assertTrue(pageData.isModeration());
        assertTrue(pageData.isSessionOpenForSubmission());
        assertTrue(pageData.isSubmittable());
        
        testQuestionAttributes();
        
        ______TS("instructor previewing a response");       
        pageData = new FeedbackSubmissionEditPageData(instructorAccount, student);
        createData(student); 
        
        pageData.setPreview(true);
        pageData.setPreviewInstructor(instructor);
        pageData.init("", student.email, student.course);
        
        assertEquals("/page/studentFeedbackSubmissionEditSave", pageData.getSubmitAction());
        assertEquals("/page/studentFeedbackQuestionSubmissionEditSave", pageData.getSubmitActionQuestion());
        
        assertFalse(pageData.isModeration());
        assertFalse(pageData.isSessionOpenForSubmission());
        assertFalse(pageData.isSubmittable());
        
        testQuestionAttributes();
    }
    
    public void testQuestionAttributes() {
        StudentFeedbackSubmissionEditQuestionsWithResponses questionWithResponses = pageData.getQuestionsWithResponses().get(0);
        
        assertEquals(question.questionType, questionWithResponses.getQuestion().getQuestionType());
        assertEquals(question.courseId, questionWithResponses.getQuestion().getCourseId());
        assertEquals(question.questionNumber, questionWithResponses.getQuestion().getQuestionNumber());
        assertEquals(question.getQuestionDetails().questionText, questionWithResponses.getQuestion().getQuestionText());
        assertEquals(question.numberOfEntitiesToGiveFeedbackTo, questionWithResponses.getQuestion().getNumberOfEntitiesToGiveFeedbackTo());
        assertEquals(question.getId(), questionWithResponses.getQuestion().getQuestionId());
    }
}