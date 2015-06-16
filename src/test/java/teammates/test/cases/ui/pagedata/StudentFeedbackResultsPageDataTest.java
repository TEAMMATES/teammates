package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

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
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.logic.api.Logic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.ui.controller.StudentFeedbackResultsPageData;
import teammates.ui.template.StudentFeedbackResultsQuestionWithResponses;

public class StudentFeedbackResultsPageDataTest extends BaseComponentTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAll() throws UnauthorizedAccessException, EntityDoesNotExistException {
        ______TS("typical success case");
        
        AccountAttributes account = dataBundle.accounts.get("student1InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        Logic logic = new Logic();
        
        StudentFeedbackResultsPageData pageData = new StudentFeedbackResultsPageData(account, student);
        
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses = 
                                        new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
        
        FeedbackQuestionAttributes question1 = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestionAttributes question2 = dataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        
        List<FeedbackResponseAttributes> responsesForQ1 = new ArrayList<FeedbackResponseAttributes>();
        List<FeedbackResponseAttributes> responsesForQ2 = new ArrayList<FeedbackResponseAttributes>();
        
        /* Question 1 with responses */
        responsesForQ1.add(dataBundle.feedbackResponses.get("response1ForQ1S1C1"));
        responsesForQ1.add(dataBundle.feedbackResponses.get("response2ForQ1S1C1"));
        questionsWithResponses.put(question1, responsesForQ1);
        
        /* Question 2 with responses */
        responsesForQ2.add(dataBundle.feedbackResponses.get("response1ForQ2S1C1"));
        responsesForQ2.add(dataBundle.feedbackResponses.get("response2ForQ2S1C1"));
        questionsWithResponses.put(question2, responsesForQ1);
        
        
        pageData.bundle = logic.getFeedbackSessionResultsForStudent(question1.feedbackSessionName, question1.courseId, student.email);
        pageData.init(student.key, student.email, student.course, questionsWithResponses);
        
        assertNotNull(pageData.getFeedbackResultsQuestionsWithResponses());
        assertEquals(pageData.getFeedbackResultsQuestionsWithResponses().size(), 2);
        assertEquals(pageData.getRegisterMessage(), 
                        "You are viewing feedback results as <span class='text-danger text-bold text-large'>"
                        + "student1 In Course1</span>. You may submit feedback and view results without logging in. "
                        + "To access other features you need <a href='/page/studentCourseJoinAuthentication?studentemail="
                        + "student1InCourse1%40gmail.tmt&courseid=idOfTypicalCourse1' class='link'>to login using "
                        + "a google account</a> (recommended)."); 
        
        assertNotNull(getQ1(pageData).getQuestionDetails());
        assertNotNull(getQ2(pageData).getQuestionDetails()); 
        
        assertEquals(getQ1(pageData).getQuestionDetails().getQuestionIndex(), "1");
        assertEquals(getQ2(pageData).getQuestionDetails().getQuestionIndex(), "2"); 
        
        assertEquals(getQ1(pageData).getQuestionDetails().getAdditionalInfo(), "");
        assertEquals(getQ2(pageData).getQuestionDetails().getAdditionalInfo(), "");
        
        assertNotNull(getQ1(pageData).getResponseTables());
        assertNotNull(getQ2(pageData).getResponseTables());      
        
        assertEquals(getQ1(pageData).getResponseTables().get(0).getRecipientName(), "You");
        assertEquals(getQ1(pageData).getResponseTables().get(1).getRecipientName(), "student2 In Course1");
        
        assertNotNull(getQ1(pageData).getResponseTables().get(0).getResponses());
        assertNotNull(getQ2(pageData).getResponseTables().get(1).getResponses());
        
        assertEquals(getQ1(pageData).getResponseTables().get(0).getResponses()
                                        .get(0).getGiverName(), "You");
        assertEquals(getQ1(pageData).getResponseTables().get(1).getResponses()
                                        .get(0).getGiverName(), "student2 In Course1");
        
        assertEquals(getQ1(pageData).getResponseTables().get(0).getResponses()
                                        .get(0).getAnswer(), "Student 1 self feedback.");
        assertEquals(getQ1(pageData).getResponseTables().get(1).getResponses()
                                        .get(0).getAnswer(), "I&#39;m cool&#39;");
        
        ______TS("student in unregistered course");
        
        student = dataBundle.students.get("student1InUnregisteredCourse");
        
        pageData = new StudentFeedbackResultsPageData(account, student);
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponsesUnregistered = 
                                        new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
        
        pageData.init(student.key, student.email, student.course, questionsWithResponsesUnregistered);
        
        assertTrue(pageData.getFeedbackResultsQuestionsWithResponses().isEmpty());
        
        assertEquals(student.key, "regKeyForStuNotYetJoinCourse");
        assertEquals(student.course, "idOfUnregisteredCourse");
        assertEquals(student.email, "student1InUnregisteredCourse@gmail.tmt");
        
        assertEquals(pageData.getRegisterMessage(), 
                                        "You are viewing feedback results as "
                                        + "<span class='text-danger text-bold text-large'>student1 In "
                                        + "unregisteredCourse</span>. You may submit feedback and view "
                                        + "results without logging in. To access other features you need "
                                        + "<a href='/page/studentCourseJoinAuthentication?key="
                                        + "regKeyForStuNotYetJoinCourse&studentemail="
                                        + "student1InUnregisteredCourse%40gmail.tmt&courseid=idOfUnregisteredCourse' "
                                        + "class='link'>to login using a google account</a> (recommended).");       
    }
    
    public StudentFeedbackResultsQuestionWithResponses getQ1(StudentFeedbackResultsPageData pageData) {
        return pageData.getFeedbackResultsQuestionsWithResponses().get(0);
    }
    
    public StudentFeedbackResultsQuestionWithResponses getQ2(StudentFeedbackResultsPageData pageData) {
        return pageData.getFeedbackResultsQuestionsWithResponses().get(1);
    }
}