package teammates.test.cases.logic;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionBundle;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.cases.BaseComponentTestCase;

public class FeedbackQuestionsLogicTest extends BaseComponentTestCase {
    
    private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private DataBundle typicalBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(FeedbackSessionsLogic.class);
    }
    
    @BeforeMethod
    public void caseSetUp() throws Exception {
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testGetRecipientsForQuestion() throws Exception {
        FeedbackQuestionAttributes question;
        String email;
        Map<String, String> recipients;
        
        ______TS("response to students, total 5");
        
        question = getQuestionFromDatastore("qn2InSession1InCourse1");
        email = typicalBundle.students.get("student1InCourse1").email;        
        recipients = fqLogic.getRecipientsForQuestion(question, email);        
        assertEquals(recipients.size(), 4); // 5 students minus giver himself
        
        email = typicalBundle.instructors.get("instructor1OfCourse1").email;
        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.size(), 5); // instructor is not student so he can respond to all 5.
        
        ______TS("response to instructors, total 3");
        
        question = getQuestionFromDatastore("qn2InSession2InCourse2");
        email = typicalBundle.instructors.get("instructor1OfCourse2").email;
        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.size(), 2); // 3 - giver = 2
        
        ______TS("empty case: response to team members, but alone");

        question = getQuestionFromDatastore("team.members.feedback");
        email = typicalBundle.students.get("student5InCourse1").email;
        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.size(), 0);
                        
        ______TS("special case: response to other team, instructor is also student");
        question = getQuestionFromDatastore("team.feedback");
        email = typicalBundle.students.get("student1InCourse1").email;
        AccountsLogic.inst().makeAccountInstructor(typicalBundle.students.get("student1InCourse1").googleId);
        
        recipients = fqLogic.getRecipientsForQuestion(question, email);    

        assertEquals(recipients.size(), 1);
        
        ______TS("to nobody (general feedback)");
        question = getQuestionFromDatastore("qn3InSession1InCourse1");
        email = typicalBundle.students.get("student1InCourse1").email;
        AccountsLogic.inst().makeAccountInstructor(typicalBundle.students.get("student1InCourse1").googleId);
        
        recipients = fqLogic.getRecipientsForQuestion(question, email);    
        assertEquals(recipients.get(Const.GENERAL_QUESTION), Const.GENERAL_QUESTION);
        assertEquals(recipients.size(), 1);
        
        ______TS("to self");
        question = getQuestionFromDatastore("qn1InSession1InCourse1");
        email = typicalBundle.students.get("student1InCourse1").email;
        AccountsLogic.inst().makeAccountInstructor(typicalBundle.students.get("student1InCourse1").googleId);
        
        recipients = fqLogic.getRecipientsForQuestion(question, email);    
        assertEquals(recipients.get(email), Const.USER_NAME_FOR_SELF);
        assertEquals(recipients.size(), 1);

    }
    
    @Test
    public void testUpdateQuestionNumber() throws Exception{
        ______TS("shift question up");
        List<FeedbackQuestionAttributes> expectedList = new ArrayList<FeedbackQuestionAttributes>();
        FeedbackQuestionAttributes q1 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q1.questionNumber = 2;
        FeedbackQuestionAttributes q2 = getQuestionFromDatastore("qn2InSession1InCourse1");
        q2.questionNumber = 3;
        FeedbackQuestionAttributes q3 = getQuestionFromDatastore("qn3InSession1InCourse1");
        q3.questionNumber = 1;
        FeedbackQuestionAttributes q4 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q4.questionNumber = 4;
        
        expectedList.add(q3);
        expectedList.add(q1);
        expectedList.add(q2);
        expectedList.add(q4);
        
        FeedbackQuestionAttributes questionToUpdate = getQuestionFromDatastore("qn3InSession1InCourse1");
        questionToUpdate.questionNumber = 1;
        fqLogic.updateFeedbackQuestionNumber(questionToUpdate);
        
        List<FeedbackQuestionAttributes> actualList = fqLogic.getFeedbackQuestionsForSession(questionToUpdate.feedbackSessionName, questionToUpdate.courseId);
        
        assertEquals(actualList.size(), expectedList.size());
        for(int i = 0; i < actualList.size(); i++){
            assertEquals(actualList.get(i), expectedList.get(i));
        }
        
        ______TS("shift question down");
        expectedList = new ArrayList<FeedbackQuestionAttributes>();
        q1 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q1.questionNumber = 2;
        q2 = getQuestionFromDatastore("qn2InSession1InCourse1");
        q2.questionNumber = 1;
        q3 = getQuestionFromDatastore("qn3InSession1InCourse1");
        q3.questionNumber = 3;
        q4 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q4.questionNumber = 4;
        
        expectedList.add(q2);
        expectedList.add(q1);
        expectedList.add(q3);
        expectedList.add(q4);
        
        questionToUpdate = getQuestionFromDatastore("qn1InSession1InCourse1");
        questionToUpdate.questionNumber = 2;
        fqLogic.updateFeedbackQuestionNumber(questionToUpdate);
        
        actualList = fqLogic.getFeedbackQuestionsForSession(questionToUpdate.feedbackSessionName, questionToUpdate.courseId);
        
        assertEquals(actualList.size(), expectedList.size());
        for(int i = 0; i < actualList.size(); i++){
            assertEquals(expectedList.get(i), actualList.get(i));
        }
    }
    
    @Test
    public void testAddQuestion() throws Exception{
        
        ______TS("Add question for feedback session that does not exist");
        FeedbackQuestionAttributes question = getQuestionFromDatastore("qn1InSession1InCourse1");
        question.feedbackSessionName = "non-existent Feedback Session";
        question.setId(null);
        try {
            fqLogic.createFeedbackQuestion(question);
            signalFailureToDetectException();
        } catch(AssertionError e){
            assertEquals(e.getMessage(), "Session disappeared.");
        }
        
        ______TS("Add question for course that does not exist");
        question = getQuestionFromDatastore("qn1InSession1InCourse1");
        question.courseId = "non-existent course id";
        question.setId(null);
        try {
            fqLogic.createFeedbackQuestion(question);
            signalFailureToDetectException();
        } catch(AssertionError e){
            assertEquals(e.getMessage(), "Session disappeared.");
        }
        
        ______TS("Add questions sequentially");
        List<FeedbackQuestionAttributes> expectedList = new ArrayList<FeedbackQuestionAttributes>();
        FeedbackQuestionAttributes q1 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q1.questionNumber = 1;
        FeedbackQuestionAttributes q2 = getQuestionFromDatastore("qn2InSession1InCourse1");
        q2.questionNumber = 2;
        FeedbackQuestionAttributes q3 = getQuestionFromDatastore("qn3InSession1InCourse1");
        q3.questionNumber = 3;
        FeedbackQuestionAttributes q4 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q4.questionNumber = 4;
        FeedbackQuestionAttributes q5 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q5.questionNumber = 5;
        
        expectedList.add(q1);
        expectedList.add(q2);
        expectedList.add(q3);
        expectedList.add(q4);
        expectedList.add(q5);

        //Appends a question to the back of the current question list
        FeedbackQuestionAttributes newQuestion = getQuestionFromDatastore("qn1InSession1InCourse1");
        newQuestion.questionNumber = 5;
        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestion(newQuestion);
        
        List<FeedbackQuestionAttributes> actualList = fqLogic.getFeedbackQuestionsForSession(q1.feedbackSessionName, q1.courseId);
        
        assertEquals(actualList.size(), expectedList.size());
        for(int i = 0; i < actualList.size(); i++){
            assertEquals(actualList.get(i), expectedList.get(i));
        }
                
        
        ______TS("add new question to the front of the list");
        restoreTypicalDataInDatastore();
        
        expectedList = new ArrayList<FeedbackQuestionAttributes>();
        q1 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q1.questionNumber = 1;
        q2 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q2.questionNumber = 2;
        q3 = getQuestionFromDatastore("qn2InSession1InCourse1");
        q3.questionNumber = 3;
        q4 = getQuestionFromDatastore("qn3InSession1InCourse1");
        q4.questionNumber = 4;
        q5 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q5.questionNumber = 5;
        
        expectedList.add(q1);
        expectedList.add(q2);
        expectedList.add(q3);
        expectedList.add(q4);
        expectedList.add(q5);
        
        //Add a question to session1course1 and sets its number to 1
        newQuestion = getQuestionFromDatastore("qn4InSession1InCourse1");
        newQuestion.questionNumber = 1;
        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestion(newQuestion);
        
        actualList = fqLogic.getFeedbackQuestionsForSession(q1.feedbackSessionName, q1.courseId);
        
        assertEquals(actualList.size(), expectedList.size());
        for(int i = 0; i < actualList.size(); i++){
            assertEquals(actualList.get(i), expectedList.get(i));
        }
        
        
        ______TS("add new question inbetween 2 existing questions");
        restoreTypicalDataInDatastore();
        
        expectedList = new ArrayList<FeedbackQuestionAttributes>();
        q1 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q1.questionNumber = 1;
        q2 = getQuestionFromDatastore("qn2InSession1InCourse1");
        q2.questionNumber = 2;
        q3 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q3.questionNumber = 3;
        q4 = getQuestionFromDatastore("qn3InSession1InCourse1");
        q4.questionNumber = 4;
        q5 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q5.questionNumber = 5;
        
        expectedList.add(q1);
        expectedList.add(q2);
        expectedList.add(q3);
        expectedList.add(q4);
        expectedList.add(q5);
        
        //Add a question to session1course1 and place it between existing question 2 and 3
        newQuestion = getQuestionFromDatastore("qn4InSession1InCourse1");
        newQuestion.questionNumber = 3;
        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestion(newQuestion);
        
        actualList = fqLogic.getFeedbackQuestionsForSession(q1.feedbackSessionName, q1.courseId);
        
        assertEquals(actualList.size(), expectedList.size());
        for(int i = 0; i < actualList.size(); i++){
            assertEquals(actualList.get(i), expectedList.get(i));
        }
    }
    
    @Test
    public void testUpdateQuestion() throws Exception {
        ______TS("standard update, no existing responses, with 'keep existing' policy");
        FeedbackQuestionAttributes questionToUpdate = getQuestionFromDatastore("qn2InSession2InCourse2");
        questionToUpdate.questionMetaData = new Text("new question text");
        questionToUpdate.questionNumber = 3;
        List<FeedbackParticipantType> newVisibility = 
                new LinkedList<FeedbackParticipantType>();
        newVisibility.add(FeedbackParticipantType.INSTRUCTORS);
        questionToUpdate.showResponsesTo = newVisibility;
        // Check keep existing policy.
        String originalCourseId = questionToUpdate.courseId;
        questionToUpdate.courseId = null;
        
        fqLogic.updateFeedbackQuestion(questionToUpdate);
        
        questionToUpdate.courseId = originalCourseId;

        FeedbackQuestionAttributes updatedQuestion =
                fqLogic.getFeedbackQuestion(questionToUpdate.getId());        
        assertEquals(updatedQuestion.toString(), questionToUpdate.toString());
        
        ______TS("cascading update, non-destructive changes, existing responses are preserved");
        questionToUpdate = getQuestionFromDatastore("qn2InSession1InCourse1");
        questionToUpdate.questionMetaData = new Text("new question text 2");
        questionToUpdate.numberOfEntitiesToGiveFeedbackTo = 2;
        
        int numberOfResponses =
                frLogic.getFeedbackResponsesForQuestion(
                        questionToUpdate.getId()).size();
        
        fqLogic.updateFeedbackQuestion(questionToUpdate);
        updatedQuestion = fqLogic.getFeedbackQuestion(questionToUpdate.getId());
        
        assertEquals(updatedQuestion.toString(), questionToUpdate.toString());
        assertEquals(
                frLogic.getFeedbackResponsesForQuestion(
                        questionToUpdate.getId()).size(), numberOfResponses);
        
        ______TS("cascading update, destructive changes, delete all existing responses");
        questionToUpdate = getQuestionFromDatastore("qn2InSession1InCourse1");
        questionToUpdate.questionMetaData = new Text("new question text 3");
        questionToUpdate.recipientType = FeedbackParticipantType.INSTRUCTORS;
        
        assertTrue(frLogic.getFeedbackResponsesForQuestion(
                        questionToUpdate.getId()).isEmpty() == false);
        
        fqLogic.updateFeedbackQuestion(questionToUpdate);
        updatedQuestion = fqLogic.getFeedbackQuestion(questionToUpdate.getId());
        
        assertEquals(updatedQuestion.toString(), questionToUpdate.toString());
        assertEquals(frLogic.getFeedbackResponsesForQuestion(
                questionToUpdate.getId()).size(), 0);

        ______TS("failure: question does not exist");
        
        questionToUpdate = getQuestionFromDatastore("qn3InSession1InCourse1");
        fqLogic.deleteFeedbackQuestionCascade(questionToUpdate.getId());
        
        try {
            fqLogic.updateFeedbackQuestion(questionToUpdate);
            signalFailureToDetectException("Expected EntityDoesNotExistException not caught.");
        } catch (EntityDoesNotExistException e){
            assertEquals(e.getMessage(), "Trying to update a feedback question that does not exist.");
        }
        
        ______TS("failure: invalid parameters");
        
        questionToUpdate = getQuestionFromDatastore("qn3InSession1InCourse1");
        questionToUpdate.giverType = FeedbackParticipantType.TEAMS;
        questionToUpdate.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;
        try {
            fqLogic.updateFeedbackQuestion(questionToUpdate);
            signalFailureToDetectException("Expected InvalidParametersException not caught.");
        } catch (InvalidParametersException e){
            assertEquals(e.getMessage(), String.format(FieldValidator.PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                                                       questionToUpdate.recipientType.toDisplayRecipientName(),
                                                       questionToUpdate.giverType.toDisplayGiverName()));
        }
    }
    
    @Test
    public void testDeleteQuestion() throws Exception {
        //Success case already tested in update
        ______TS("question already does not exist, silently fail");
        
        fqLogic.deleteFeedbackQuestionCascade("non-existent-question-id");
        //No error should be thrown.
        
    }

    @Test
    public void testGetFeedbackQuestionsForInstructor() throws Exception{
        List<FeedbackQuestionAttributes> expectedQuestions, actualQuestions, allQuestions;
        
        ______TS("Get questions created for instructors and self");
        
        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn4InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("qn3InSession1InCourse1"));
        actualQuestions = 
                    fqLogic.getFeedbackQuestionsForInstructor("First feedback session", "idOfTypicalCourse1", "instructor1@course1.com");
        
        assertEquals(actualQuestions, expectedQuestions);
                
        ______TS("Get questions created for instructors and self by another instructor");
        
        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn4InSession1InCourse1"));
        actualQuestions = 
                    fqLogic.getFeedbackQuestionsForInstructor("First feedback session", "idOfTypicalCourse1", "instructor2@course1.com");

        assertEquals(actualQuestions, expectedQuestions);
        
        ______TS("Get questions created for instructors by the creating instructor");
        
        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn1InSession2InCourse2"));
        expectedQuestions.add(getQuestionFromDatastore("qn2InSession2InCourse2"));        
        
        actualQuestions = 
                    fqLogic.getFeedbackQuestionsForInstructor("Instructor feedback session", "idOfTypicalCourse2", "instructor1@course2.com");
        
        assertEquals(actualQuestions, expectedQuestions);
        
        ______TS("Get questions created for instructors not by the creating instructor");
        
        actualQuestions = 
                fqLogic.getFeedbackQuestionsForInstructor("Instructor feedback session", "idOfTypicalCourse2", "instructor2@course2.com");
    
        assertEquals(actualQuestions, expectedQuestions);
        
        ______TS("Get questions created for instructors by non-instructor of the course");
        
        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        actualQuestions = 
                fqLogic.getFeedbackQuestionsForInstructor("Instructor feedback session", "idOfTypicalCourse2", "iwc@yahoo.com");
        
        assertEquals(actualQuestions, expectedQuestions);
        
        ______TS("Failure: Getting questions for a non-existent session");
        
        try {
            fqLogic.getFeedbackQuestionsForInstructor("Instructor feedback session", "idOfTypicalCourse1", "instructor1@course1.com");
            fail("Allowed to get questions for a feedback session that does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals(e.getMessage(), "Trying to get questions for a feedback session that does not exist.");
        }
        
        ______TS("Get questions created for self  from list of all questions");
        
        
        allQuestions = new ArrayList<FeedbackQuestionAttributes>();
        allQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn3InSession1InCourse1"));
        
        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn3InSession1InCourse1"));
        
        actualQuestions = fqLogic.getFeedbackQuestionsForInstructor(allQuestions, true);
        
        assertEquals(actualQuestions, expectedQuestions);
    }
    
    @Test
    public void testGetFeedbackQuestionsForStudents() throws Exception{
        List<FeedbackQuestionAttributes> expectedQuestions, actualQuestions, allQuestions;
        
        ______TS("Get questions created for students");
        
        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));
        actualQuestions = 
                    fqLogic.getFeedbackQuestionsForStudents("First feedback session", "idOfTypicalCourse1");
        
        assertEquals(actualQuestions, expectedQuestions);
        
        ______TS("Get questions created for students and teams");
        
        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("team.members.feedback"));
        expectedQuestions.add(getQuestionFromDatastore("team.feedback"));
        actualQuestions = 
                    fqLogic.getFeedbackQuestionsForStudents("Second feedback session", "idOfTypicalCourse1");
        
        assertEquals(actualQuestions, expectedQuestions);
        
        ______TS("Get questions created for students from list of all questions");
        
        allQuestions = new ArrayList<FeedbackQuestionAttributes>();
        allQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn3InSession1InCourse1"));
                
        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));
        
        actualQuestions = fqLogic.getFeedbackQuestionsForStudents(allQuestions);
        
        assertEquals(actualQuestions, expectedQuestions);
        
        ______TS("Get questions created for students and teams from list of all questions");
        
        allQuestions = new ArrayList<FeedbackQuestionAttributes>();
        allQuestions.add(getQuestionFromDatastore("team.feedback"));
        allQuestions.add(getQuestionFromDatastore("team.members.feedback"));
                
        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("team.feedback"));
        expectedQuestions.add(getQuestionFromDatastore("team.members.feedback"));
        
        actualQuestions = fqLogic.getFeedbackQuestionsForStudents(allQuestions);
        
        assertEquals(actualQuestions, expectedQuestions);
    }
    
    @Test
    public void testGetFeedbackQuestionsForTeam() throws Exception{
        List<FeedbackQuestionAttributes> expectedQuestions, actualQuestions;
        
        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("team.feedback"));
        actualQuestions = 
                fqLogic.getFeedbackQuestionsForTeam("Second feedback session", "idOfTypicalCourse1", "");
        
        assertEquals(actualQuestions, expectedQuestions);
        
    }
    
    @Test
    public void testIsQuestionHasResponses() {
        FeedbackQuestionAttributes questionWithResponse, questionWithoutResponse;
        
        ______TS("Check that a question has some responses");
        
        questionWithResponse = getQuestionFromDatastore("qn1InSession2InCourse2");
        assertTrue(fqLogic.isQuestionHasResponses(questionWithResponse.getId()));
        
        ______TS("Check that a question has no responses");
        
        questionWithoutResponse = getQuestionFromDatastore("qn2InSession2InCourse2");
        assertFalse(fqLogic.isQuestionHasResponses(questionWithoutResponse.getId()));
    }
    
    @Test
    public void testIsQuestionAnswered() throws Exception {
        FeedbackQuestionAttributes question;
        ______TS("test question is answered by user");
        
        question = getQuestionFromDatastore("qn1InSession1InCourse1");
        assertTrue(fqLogic.isQuestionAnsweredByUser(question, "student1InCourse1@gmail.com"));
        assertFalse(fqLogic.isQuestionAnsweredByUser(question, "studentWithNoResponses@gmail.com"));
        
        
        List<FeedbackResponseAttributes> responses = new ArrayList<FeedbackResponseAttributes>();
        assertFalse(fqLogic.isQuestionAnsweredByUser(question, "student1InCourse1@gmail.com", responses));
        
        responses = frLogic.getFeedbackResponsesForQuestion(question.getId());
        assertTrue(fqLogic.isQuestionAnsweredByUser(question, "student2InCourse1@gmail.com", responses));
        
        ______TS("test question is fully answered by user");
        
        assertTrue(fqLogic.isQuestionFullyAnsweredByUser(question, "student1InCourse1@gmail.com"));
        
        assertFalse(fqLogic.isQuestionFullyAnsweredByUser(question, "studentWithNoResponses@gmail.com"));
       
        ______TS("test question is fully answered by team");
        
        assertFalse(fqLogic.isQuestionFullyAnsweredByTeam(question, "Team 1.1"));
        
    }  
    
    @Test
    public void testGetFeedbackQuestionBundle() throws Exception {
        testGetFeedbackQuestionBundleForInstructor();
        testGetFeedbackQuestionBundleForStudent();
    }
    
    private void testGetFeedbackQuestionBundleForInstructor() throws Exception{
        ______TS("typical success case");
        
        FeedbackQuestionBundle fqBundle = null;
        FeedbackQuestionAttributes fqa = getQuestionFromDatastore("qn3InSession1InCourse1");
        
        fqBundle = fqLogic.getFeedbackQuestionBundleForInstructor(
                        "First feedback session", "idOfTypicalCourse1",
                        fqa.getId(), "instructor1@course1.com");
        
        assertEquals(fqBundle.feedbackSession.courseId,"idOfTypicalCourse1");
        assertEquals(fqBundle.feedbackSession.feedbackSessionName,"First feedback session");
        assertEquals(fqBundle.question.questionNumber, 3);
        assertEquals(fqBundle.recipientList.size(), 1);
        assertEquals(fqBundle.responseList.size(), 1);
        
        ______TS("non-existent feedback session");
        
        try {
            fqBundle = fqLogic.getFeedbackQuestionBundleForInstructor(
                        "non-existent feedback session", "idOfTypicalCourse1",
                        fqa.getId(), "instructor1@course1.com");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals(e.getMessage(),"Trying to get a feedback session that does not exist.");
        }
        
        ______TS("non-existent feedback question");
        
        try {
            fqBundle = fqLogic.getFeedbackQuestionBundleForInstructor(
                        "First feedback session", "idOfTypicalCourse1",
                        "non-existent fq id", "instructor1@course1.com");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals(e.getMessage(),"Trying to get a feedback question that does not exist.");
        }
        
        ______TS("question not meant for user");
        
        fqa = getQuestionFromDatastore("qn1InSession1InCourse1");
        
        try {
            fqBundle = fqLogic.getFeedbackQuestionBundleForInstructor(
                        "First feedback session", "idOfTypicalCourse1",
                        fqa.getId(), "instructor1@course1.com");
            signalFailureToDetectException();
       } catch (UnauthorizedAccessException e) {
            assertEquals(e.getMessage(),"Trying to access a question not meant for the user.");
        }
    }
    
    private void testGetFeedbackQuestionBundleForStudent() throws Exception{
        ______TS("typical success case");
        
        FeedbackQuestionAttributes fqa = getQuestionFromDatastore("qn1InSession1InCourse1");
        
        FeedbackQuestionBundle fqBundle = fqLogic.getFeedbackQuestionBundleForStudent(
                        "First feedback session", "idOfTypicalCourse1",
                        fqa.getId(), "student1InCourse1@gmail.com");
        
        assertEquals(fqBundle.feedbackSession.courseId,"idOfTypicalCourse1");
        assertEquals(fqBundle.feedbackSession.feedbackSessionName,"First feedback session");
        assertEquals(fqBundle.question.questionNumber, 1);
        assertEquals(fqBundle.recipientList.size(), 1);
        assertEquals(fqBundle.responseList.size(), 1);
        
        ______TS("non-existent feedback session");
        
        try {
            fqBundle = fqLogic.getFeedbackQuestionBundleForStudent(
                        "non-existent feedback session", "idOfTypicalCourse1",
                        fqa.getId(), "student1InCourse1@gmail.com");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals(e.getMessage(),"Trying to get a feedback session that does not exist.");
        }
        
        ______TS("non-existent feedback question");
        
        try {
            fqBundle = fqLogic.getFeedbackQuestionBundleForStudent(
                        "First feedback session", "idOfTypicalCourse1",
                        "non-existent fq id", "student1InCourse1@gmail.com");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals(e.getMessage(),"Trying to get a feedback question that does not exist.");
        }
        
        ______TS("question not meant for user");
        
        fqa = getQuestionFromDatastore("qn3InSession1InCourse1");
        
        try {
            fqBundle = fqLogic.getFeedbackQuestionBundleForStudent(
                        "First feedback session", "idOfTypicalCourse1",
                        fqa.getId(), "student1InCourse1@gmail.com");
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException e) {
            assertEquals(e.getMessage(),"Trying to access a question not meant for the user.");
        }
    }
        
    private FeedbackQuestionAttributes getQuestionFromDatastore(String questionKey) {
        FeedbackQuestionAttributes question;
        question = typicalBundle.feedbackQuestions.get(questionKey);
        question = fqLogic.getFeedbackQuestion(
                question.feedbackSessionName, question.courseId, question.questionNumber);
        return question;
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(FeedbackSessionsLogic.class);
    }
    
    
}
