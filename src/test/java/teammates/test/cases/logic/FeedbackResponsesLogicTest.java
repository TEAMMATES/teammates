package teammates.test.cases.logic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

import com.google.appengine.api.datastore.Text;

public class FeedbackResponsesLogicTest extends BaseComponentTestCase {
    
    private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private DataBundle typicalBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(FeedbackResponsesLogic.class);
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void allTests() throws Exception{
        testIsNameVisibleTo();
        testGetViewableResponsesForQuestionInSection();
        testUpdateFeedbackResponse();
        testUpdateFeedbackResponsesForChangingTeam();
        testUpdateFeedbackResponsesForChangingEmail();
        testDeleteFeedbackResponsesForStudent();
    }

    public void testUpdateFeedbackResponse() throws Exception {
        
        ______TS("success: standard update with carried params ");
        
        FeedbackResponseAttributes responseToUpdate = getResponseFromDatastore("response1ForQ2S1C1");
        
        responseToUpdate.responseMetaData = new Text("Updated Response");
        responseToUpdate.feedbackSessionName = "copy over";
        responseToUpdate.recipientEmail = null;
        
        frLogic.updateFeedbackResponse(responseToUpdate);
        
        responseToUpdate = getResponseFromDatastore("response1ForQ2S1C1");
        responseToUpdate.responseMetaData = new Text("Updated Response");
        
        assertEquals(frLogic.getFeedbackResponse(
                responseToUpdate.feedbackQuestionId, responseToUpdate.giverEmail, responseToUpdate.recipientEmail).toString(),
                responseToUpdate.toString());
        
        ______TS("failure: recipient one that is already exists");
        
        responseToUpdate = getResponseFromDatastore("response1ForQ2S1C1");
  
        FeedbackResponseAttributes existingResponse = 
                new FeedbackResponseAttributes(
                        responseToUpdate.feedbackSessionName, 
                        responseToUpdate.courseId, 
                        responseToUpdate.feedbackQuestionId, 
                        responseToUpdate.feedbackQuestionType, 
                        responseToUpdate.giverEmail,
                        responseToUpdate.giverSection,
                        "student3InCourse1@gmail.tmt",
                        responseToUpdate.recipientSection,
                        responseToUpdate.responseMetaData);
        
        frLogic.createFeedbackResponse(existingResponse);
        
        responseToUpdate.recipientEmail = "student3InCourse1@gmail.tmt";
        
        try {
            frLogic.updateFeedbackResponse(responseToUpdate);
            signalFailureToDetectException("Should have detected that same giver->recipient response alr exists");
        } catch (EntityAlreadyExistsException e){
            AssertHelper.assertContains(
                        "Trying to update recipient for response to one that already exists for this giver.", 
                        e.getMessage());
        }
        
        ______TS("success: standard update with carried params - using createFeedbackResponse");
        
        responseToUpdate = getResponseFromDatastore("response1ForQ2S1C1");
        
        responseToUpdate.responseMetaData = new Text("Updated Response 2");
        responseToUpdate.feedbackSessionName = "copy over";
        
        frLogic.createFeedbackResponse(responseToUpdate);
        
        responseToUpdate = getResponseFromDatastore("response1ForQ2S1C1");
        responseToUpdate.responseMetaData = new Text("Updated Response 2");
        
        assertEquals(frLogic.getFeedbackResponse(
                responseToUpdate.feedbackQuestionId, responseToUpdate.giverEmail, responseToUpdate.recipientEmail).toString(),
                responseToUpdate.toString());
        
        ______TS("success: recipient changed to something else");
        
        responseToUpdate.recipientEmail = "student5InCourse1@gmail.tmt";
        
        frLogic.updateFeedbackResponse(responseToUpdate);
        
        assertEquals(frLogic.getFeedbackResponse(
                responseToUpdate.feedbackQuestionId, responseToUpdate.giverEmail, responseToUpdate.recipientEmail).toString(),
                responseToUpdate.toString());
        assertNull(frLogic.getFeedbackResponse(
                responseToUpdate.feedbackQuestionId, responseToUpdate.giverEmail, "student2InCourse1@gmail.tmt"));
        
        ______TS("success: both giver and recipient changed (teammate changed response)");
        
        responseToUpdate = getResponseFromDatastore("response1GracePeriodFeedback");
        responseToUpdate.giverEmail = "student5InCourse1@gmail.tmt";
        responseToUpdate.recipientEmail = "Team 1.1";
        
        assertNotNull(frLogic.getFeedbackResponse(
                responseToUpdate.feedbackQuestionId, "student4InCourse1@gmail.tmt","Team 1.2"));
        
        frLogic.updateFeedbackResponse(responseToUpdate);
        
        assertEquals(frLogic.getFeedbackResponse(
                responseToUpdate.feedbackQuestionId, responseToUpdate.giverEmail, responseToUpdate.recipientEmail).toString(),
                responseToUpdate.toString());
        assertNull(frLogic.getFeedbackResponse(
                responseToUpdate.feedbackQuestionId, "student4InCourse1@gmail.tmt","Team 1.2"));
        
        
        ______TS("failure: invalid params");
        
        // Cannot have invalid params as all possible invalid params
        // are copied over from an existing response.
        
        ______TS("failure: no such response");
        
        responseToUpdate.setId("invalidId");
        
        try {
            frLogic.updateFeedbackResponse(responseToUpdate);
            signalFailureToDetectException("Should have detected that this response does not exist");
        } catch (EntityDoesNotExistException e){
            AssertHelper.assertContains(
                        "Trying to update a feedback response that does not exist.", 
                        e.getMessage());
        }
    }
    
    public void testUpdateFeedbackResponsesForChangingTeam() throws Exception {
        
        ______TS("standard update team case");
        
        StudentAttributes studentToUpdate = typicalBundle.students.get("student4InCourse1");
        
        // Student 4 has 1 responses to him from team members,
        // 1 response from him a team member, and
        // 1 team response from him to another team.
        FeedbackQuestionAttributes teamQuestion = getQuestionFromDatastore("team.members.feedback");
        assertEquals(frLogic.getFeedbackResponsesForReceiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size(),1);
        assertEquals(frLogic.getFeedbackResponsesFromGiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size(),1);
        
        teamQuestion = getQuestionFromDatastore("team.feedback");
        assertEquals(frLogic.getFeedbackResponsesFromGiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size(),1);
        
        // Add one more non-team response 
        FeedbackResponseAttributes responseToAdd = new FeedbackResponseAttributes("First feedback session",
                                                        "idOfTypicalCourse1", getQuestionFromDatastore("qn1InSession1InCourse1").getId(),
                                                        FeedbackQuestionType.TEXT, studentToUpdate.email, "Section 1",
                                                        studentToUpdate.email, "Section 1", new Text("New Response to self"));
        frLogic.createFeedbackResponse(responseToAdd);
        
        // All these responses should be gone after he changes teams
        
        frLogic.updateFeedbackResponsesForChangingTeam(
                studentToUpdate.course, studentToUpdate.email, studentToUpdate.team, "Team 1.2");
        
        teamQuestion = getQuestionFromDatastore("team.members.feedback");
        assertEquals(frLogic.getFeedbackResponsesForReceiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size(),0);
        assertEquals(frLogic.getFeedbackResponsesFromGiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size(),0);
        
        teamQuestion = getQuestionFromDatastore("team.feedback");
        assertEquals(frLogic.getFeedbackResponsesForReceiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size(),0);
        
        // Non-team response should remain
        
        assertEquals(frLogic.getFeedbackResponsesFromGiverForQuestion(
                            getQuestionFromDatastore("qn1InSession1InCourse1").getId(),
                            studentToUpdate.email).size()
                    , 1);
        
    }
    
    public void testUpdateFeedbackResponsesForChangingEmail() throws Exception {
        ______TS("standard update email case");
        
        // Student 1 currently has 3 responses to him and 3 from himself.
        InstructorAttributes studentToUpdate = typicalBundle.instructors.get("instructor1OfCourse1");
        assertEquals(frLogic.getFeedbackResponsesForReceiverForCourse(
                studentToUpdate.courseId, studentToUpdate.email).size(), 2);
        assertEquals(frLogic.getFeedbackResponsesFromGiverForCourse(
                studentToUpdate.courseId, studentToUpdate.email).size(), 3);
        
        frLogic.updateFeedbackResponsesForChangingEmail(
                studentToUpdate.courseId, studentToUpdate.email, "new@email.tmt");
        
        assertEquals(frLogic.getFeedbackResponsesForReceiverForCourse(
                studentToUpdate.courseId, studentToUpdate.email).size(), 0);
        assertEquals(frLogic.getFeedbackResponsesFromGiverForCourse(
                studentToUpdate.courseId, studentToUpdate.email).size(), 0);
        assertEquals(frLogic.getFeedbackResponsesForReceiverForCourse(
                studentToUpdate.courseId, "new@email.tmt").size(), 2);
        assertEquals(frLogic.getFeedbackResponsesFromGiverForCourse(
                studentToUpdate.courseId, "new@email.tmt").size(), 3);
        
        frLogic.updateFeedbackResponsesForChangingEmail(
                studentToUpdate.courseId, "new@email.tmt", studentToUpdate.email);
    }
    
    public void testGetViewableResponsesForQuestionInSection() throws Exception {
        
        ______TS("success: GetViewableResponsesForQuestion - instructor");
        
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackQuestionAttributes fq = getQuestionFromDatastore("qn3InSession1InCourse1"); 
        List<FeedbackResponseAttributes> responses = frLogic.getViewableFeedbackResponsesForQuestionInSection(fq, instructor.email, UserType.Role.INSTRUCTOR, null);
        
        assertEquals(responses.size(), 1);
        
        ______TS("success: GetViewableResponsesForQuestionInSection - instructor");
        
        fq = getQuestionFromDatastore("qn2InSession1InCourse1");
        responses = frLogic.getViewableFeedbackResponsesForQuestionInSection(fq, instructor.email, UserType.Role.INSTRUCTOR, "Section 1");
        
        assertEquals(responses.size(), 3);
        
        responses = frLogic.getViewableFeedbackResponsesForQuestionInSection(fq, instructor.email, UserType.Role.INSTRUCTOR, "Section 2");
        
        assertEquals(responses.size(), 0);

        ______TS("success: GetViewableResponsesForQuestion - student");
        
        StudentAttributes student = typicalBundle.students.get("student1InCourse1");        
        fq = getQuestionFromDatastore("qn2InSession1InCourse1"); 
        responses = frLogic.getViewableFeedbackResponsesForQuestionInSection(fq, student.email, UserType.Role.STUDENT, null);
        
        assertEquals(responses.size(), 2);
        
        fq = getQuestionFromDatastore("qn3InSession1InCourse1"); 
        responses = frLogic.getViewableFeedbackResponsesForQuestionInSection(fq, student.email, UserType.Role.STUDENT, null);
        
        assertEquals(responses.size(), 1);
        
        fq.recipientType = FeedbackParticipantType.TEAMS;
        fq.showResponsesTo.add(FeedbackParticipantType.RECEIVER);
        fq.showResponsesTo.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        fq.showResponsesTo.remove(FeedbackParticipantType.STUDENTS);
        FeedbackResponseAttributes fr = getResponseFromDatastore("response1ForQ3S1C1");
        fr.recipientEmail = student.email;
        frLogic.updateFeedbackResponse(fr);
        
        responses = frLogic.getViewableFeedbackResponsesForQuestionInSection(fq, student.email, UserType.Role.STUDENT, null);
        
        assertEquals(responses.size(), 1);
        
        
        ______TS("success: Null student in response, should skip over null student");
        fq = getQuestionFromDatastore("qn2InSession1InCourse1"); 
        fq.showResponsesTo.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
       
        FeedbackResponseAttributes existingResponse = getResponseFromDatastore("response1ForQ2S1C1");
        
        //Create a "null" response to simulate trying to get a null student's response
        FeedbackResponseAttributes newResponse = 
                new FeedbackResponseAttributes(
                        existingResponse.feedbackSessionName, 
                        "nullCourse", 
                        existingResponse.feedbackQuestionId, 
                        existingResponse.feedbackQuestionType, 
                        existingResponse.giverEmail,
                        "Section 1",
                        "nullRecipient@gmail.tmt", 
                        "Section 1",
                        existingResponse.responseMetaData);
      
        frLogic.createFeedbackResponse(newResponse);
        student = typicalBundle.students.get("student2InCourse1");           
        responses = frLogic.getViewableFeedbackResponsesForQuestionInSection(fq, student.email, UserType.Role.STUDENT, null);
        assertEquals(responses.size(), 4);
        
        
        ______TS("failure: GetViewableResponsesForQuestion invalid role");
        
        try {
            frLogic.getViewableFeedbackResponsesForQuestionInSection(fq, instructor.email, UserType.Role.ADMIN, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(e.getMessage(), "The role of the requesting use has to be Student or Instructor");
        }   
    }
    
    public void testIsNameVisibleTo() throws Exception {
        
        
        ______TS("testIsNameVisibleTo");
        
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student2 = typicalBundle.students.get("student2InCourse1");
        StudentAttributes student3 = typicalBundle.students.get("student5InCourse1");
        
        FeedbackQuestionAttributes fq = getQuestionFromDatastore("qn3InSession1InCourse1"); 
        FeedbackResponseAttributes fr = getResponseFromDatastore("response1ForQ3S1C1"); 
        
        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(fq.courseId),
                new InstructorsDb().getInstructorsForCourse(fq.courseId));
        
        assertTrue(frLogic.isNameVisibleTo(fq, fr, instructor.email, UserType.Role.INSTRUCTOR, true, roster));
        assertTrue(frLogic.isNameVisibleTo(fq, fr, instructor.email, UserType.Role.INSTRUCTOR, false, roster));
        assertTrue(frLogic.isNameVisibleTo(fq, fr, student.email, UserType.Role.STUDENT, false, roster));
        
        ______TS("test if visible to own team members");
        
        fr.giverEmail = student.email;
        assertTrue(frLogic.isNameVisibleTo(fq, fr, student.email, UserType.Role.STUDENT, false, roster));
        
        ______TS("test if visible to receiver/reciever team members");
        
        fq.recipientType = FeedbackParticipantType.TEAMS;
        fq.showRecipientNameTo.clear();
        fq.showRecipientNameTo.add(FeedbackParticipantType.RECEIVER);
        fr.recipientEmail = student.team;
        assertTrue(frLogic.isNameVisibleTo(fq, fr, student.email, UserType.Role.STUDENT, false, roster));
        assertTrue(frLogic.isNameVisibleTo(fq, fr, student3.email, UserType.Role.STUDENT, false, roster));
        
        fq.recipientType = FeedbackParticipantType.STUDENTS;
        fr.recipientEmail = student.email;
        assertTrue(frLogic.isNameVisibleTo(fq, fr, student.email, UserType.Role.STUDENT, false, roster));
        assertFalse(frLogic.isNameVisibleTo(fq, fr, student2.email, UserType.Role.STUDENT, false, roster));
        
        fq.recipientType = FeedbackParticipantType.TEAMS;
        fq.showRecipientNameTo.clear();
        fq.showRecipientNameTo.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        fr.recipientEmail = student.team;
        assertTrue(frLogic.isNameVisibleTo(fq, fr, student.email, UserType.Role.STUDENT, false, roster));
        assertTrue(frLogic.isNameVisibleTo(fq, fr, student3.email, UserType.Role.STUDENT, false, roster));
        
        fq.recipientType = FeedbackParticipantType.STUDENTS;
        fr.recipientEmail = student.email;
        assertTrue(frLogic.isNameVisibleTo(fq, fr, student.email, UserType.Role.STUDENT, false, roster));
        assertTrue(frLogic.isNameVisibleTo(fq, fr, student2.email, UserType.Role.STUDENT, false, roster));
        assertFalse(frLogic.isNameVisibleTo(fq, fr, student3.email, UserType.Role.STUDENT, false, roster));
        
        ______TS("null question");
        
        assertFalse(frLogic.isNameVisibleTo(null, fr, student.email, UserType.Role.STUDENT, false, roster));
        
    }
    
    public void testDeleteFeedbackResponsesForStudent() throws Exception {    
        
        ______TS("standard delete");
        
        StudentAttributes studentToDelete = typicalBundle.students.get("student1InCourse1");;
        List<FeedbackResponseAttributes> responsesForStudent1 =
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email);
        responsesForStudent1
                .addAll(
                        frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));
        frLogic.deleteFeedbackResponsesForStudentAndCascade(studentToDelete.course, studentToDelete.email);
        
        List<FeedbackResponseAttributes> remainingResponses = new ArrayList<FeedbackResponseAttributes>();                
        remainingResponses.addAll(frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email));
        remainingResponses.addAll(frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));        
        assertEquals(remainingResponses.size(), 0);
        
        List<FeedbackResponseCommentAttributes> remainingComments = new ArrayList<FeedbackResponseCommentAttributes>();
        for (FeedbackResponseAttributes response : responsesForStudent1) {
            remainingComments.addAll(frcLogic.getFeedbackResponseCommentForResponse(response.getId()));
        }
        assertEquals(remainingComments.size(), 0);
        
        ______TS("shift team then delete");
        
        remainingResponses.clear();
        
        studentToDelete = typicalBundle.students.get("student2InCourse1");;
        
        studentToDelete.team = "Team 1.3";
        StudentsLogic.inst().updateStudentCascadeWithoutDocument(studentToDelete.email, studentToDelete);

        frLogic.deleteFeedbackResponsesForStudentAndCascade(studentToDelete.course, studentToDelete.email);
        
        remainingResponses.addAll(frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email));
        remainingResponses.addAll(frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));        
        assertEquals(remainingResponses.size(), 0);                
        
        ______TS("delete last person in team");

        remainingResponses.clear();
                
        studentToDelete = typicalBundle.students.get("student5InCourse1");
        
        frLogic.deleteFeedbackResponsesForStudentAndCascade(studentToDelete.course, studentToDelete.email);
        remainingResponses.addAll(frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email));
        remainingResponses.addAll(frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));
        
        // check that team responses are gone too. already checked giver as it is stored by giver email not team id.
        remainingResponses.addAll(frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, "Team 1.2"));

        assertEquals(remainingResponses.size(),0);    
    }
    
    private FeedbackQuestionAttributes getQuestionFromDatastore(String jsonId) {
        FeedbackQuestionAttributes questionToGet = typicalBundle.feedbackQuestions.get(jsonId);
        questionToGet = fqLogic.getFeedbackQuestion(questionToGet.feedbackSessionName, 
                                                    questionToGet.courseId,
                                                    questionToGet.questionNumber);
        
        return questionToGet;
    }
    
    private FeedbackResponseAttributes getResponseFromDatastore(String jsonId){
        FeedbackResponseAttributes response =
                typicalBundle.feedbackResponses.get(jsonId);
        
        int qnNumber = Integer.parseInt(response.feedbackQuestionId);
        
        String qnId = fqLogic.getFeedbackQuestion(
                response.feedbackSessionName, response.courseId, qnNumber).getId();
        
        return frLogic.getFeedbackResponse(
                qnId, response.giverEmail, response.recipientEmail);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(FeedbackResponsesLogic.class);
    }
}
