package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.FeedbackSubmissionEditPageData;

public class FeedbackSubmissionEditPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void testAll() {
        ______TS("test typical case");
        AccountAttributes studentAccount = dataBundle.accounts.get("student1InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        FeedbackSubmissionEditPageData pageData = new FeedbackSubmissionEditPageData(studentAccount, student);
        
        pageData.init(student.key, student.email, student.course);
        
        assertEquals("You are submitting feedback as <span class='text-danger text-bold text-large'>"
                      + "student1 In Course1</span>. You may submit feedback and view results without logging "
                      + "in. To access other features you need <a href='/page/studentCourseJoinAuthentication?"
                      + "studentemail=student1InCourse1%40gmail.tmt&courseid=idOfTypicalCourse1' class='link'>"
                      + "to login using a google account</a> (recommended).", pageData.getRegisterMessage());
        
        assertEquals("/page/studentFeedbackSubmissionEditSave", pageData.getSubmitAction());
        assertEquals("/page/studentFeedbackQuestionSubmissionEditSave", pageData.getSubmitActionQuestion());
        assertFalse(pageData.isModeration);
        assertFalse(pageData.isSessionOpenForSubmission);
        assertFalse(pageData.isSubmittable());
        
        ______TS("student in unregistered course");
        student = dataBundle.students.get("student1InUnregisteredCourse");
        pageData = new FeedbackSubmissionEditPageData(studentAccount, student);
        
        pageData.init(student.key, student.email, student.course);
        
        assertEquals("You are submitting feedback as <span class='text-danger text-bold text-large'>student1 "
                      + "In unregisteredCourse</span>. You may submit feedback and view results without logging "
                      + "in. To access other features you need <a href='/page/studentCourseJoinAuthentication?"
                      + "key=regKeyForStuNotYetJoinCourse&studentemail=student1InUnregisteredCourse%40gmail.tmt&"
                      + "courseid=idOfUnregisteredCourse' class='link'>to login using a google account</a> "
                      + "(recommended).", pageData.getRegisterMessage());
        
        assertEquals("/page/studentFeedbackSubmissionEditSave", pageData.getSubmitAction());
        assertEquals("/page/studentFeedbackQuestionSubmissionEditSave", pageData.getSubmitActionQuestion());
        assertFalse(pageData.isModeration);
        assertFalse(pageData.isSessionOpenForSubmission);
        assertFalse(pageData.isSubmittable());
        
        ______TS("student in archived course");
        student = dataBundle.students.get("student1InArchivedCourse");
        pageData = new FeedbackSubmissionEditPageData(studentAccount, student);
        
        pageData.init(student.key, student.email, student.course);
        
        assertEquals("You are submitting feedback as <span class='text-danger text-bold text-large'>student1 In Course1"
                      + "</span>. You may submit feedback and view results without logging in. To access other features "
                      + "you need <a href='/page/studentCourseJoinAuthentication?studentemail=student1InArchivedCourse%40"
                      + "gmail.tmt&courseid=idOfArchivedCourse' class='link'>to login using a google account</a> "
                      + "(recommended).", pageData.getRegisterMessage());
        
        assertEquals("/page/studentFeedbackSubmissionEditSave", pageData.getSubmitAction());
        assertEquals("/page/studentFeedbackQuestionSubmissionEditSave", pageData.getSubmitActionQuestion());
        assertFalse(pageData.isModeration);
        assertFalse(pageData.isSessionOpenForSubmission);
        assertFalse(pageData.isSubmittable());
    }
}