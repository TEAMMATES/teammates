package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.FeedbackSessionStats;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.api.Logic;
import teammates.logic.automated.EmailAction;
import teammates.logic.automated.FeedbackSessionPublishedMailAction;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.logic.core.Emails;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.Emails.EmailType;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

import com.google.appengine.api.datastore.Text;

public class FeedbackSessionsLogicTest extends BaseComponentTestCase {
    private static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private DataBundle dataBundle = getTypicalDataBundle();
    
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(FeedbackSessionsLogic.class);
        gaeSimulation.resetDatastore();
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAll() throws Exception{
        
        testGetFeedbackSessionsForCourse();
        testGetFeedbackSessionsListForInstructor();
        testGetFeedbackSessionsClosingWithinTimeLimit();
        testGetFeedbackSessionsWhichNeedOpenMailsToBeSent();
        testGetFeedbackSessionWhichNeedPublishedEmailsToBeSent();
        testGetFeedbackSessionDetailsForInstructor();
        testGetFeedbackSessionQuestionsForStudent();
        testGetFeedbackSessionQuestionsForInstructor();
        testGetFeedbackSessionResultsForUser();
        testGetFeedbackSessionResultsSummaryAsCsv();
        testIsFeedbackSessionViewableToStudents();
        
        testCreateAndDeleteFeedbackSession();       
        testCopyFeedbackSession();
        
        testUpdateFeedbackSession();
        testPublishUnpublishFeedbackSession();
        
        testIsFeedbackSessionHasQuestionForStudents();
        testIsFeedbackSessionCompletedByStudent();
        testIsFeedbackSessionCompletedByInstructor();
        testIsFeedbackSessionFullyCompletedByStudent();
                
        testSendReminderForFeedbackSession();
        testSendReminderForFeedbackSessionParticularUsers();
        testDeleteFeedbackSessionsForCourse();
    }
    
    public void testGetFeedbackSessionsListForInstructor () throws Exception{        
        List<FeedbackSessionAttributes> finalFsa = new ArrayList<FeedbackSessionAttributes>();
        Collection<FeedbackSessionAttributes> allFsa = dataBundle.feedbackSessions.values();
        
        String courseId = dataBundle.courses.get("typicalCourse1").id;
        String instructorGoogleId = dataBundle.instructors.get("instructor1OfCourse1").googleId;
        
        for (FeedbackSessionAttributes fsa : allFsa) {
            if (fsa.courseId == courseId) {
                finalFsa.add(fsa);
            }
        }
        
        TestHelper.isSameContentIgnoreOrder(finalFsa, fsLogic.getFeedbackSessionsListForInstructor(instructorGoogleId));
        
    }
    
    public void testIsFeedbackSessionHasQuestionForStudents () throws Exception{
        // no need to removeAndRestoreTypicalDataInDatastore() as the previous test does not change the db
        
        FeedbackSessionAttributes sessionWithStudents = dataBundle.feedbackSessions.get("gracePeriodSession");
        FeedbackSessionAttributes sessionWithoutStudents = dataBundle.feedbackSessions.get("closedSession");
        
        ______TS("non-existent session/courseId");
        
        try {
            fsLogic.isFeedbackSessionHasQuestionForStudents("nOnEXistEnT session", "someCourse");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals ("Trying to check a feedback session that does not exist.", 
                    edne.getMessage());
        }
        
        ______TS("session contains students");
        
        assertTrue (fsLogic.isFeedbackSessionHasQuestionForStudents(sessionWithStudents.feedbackSessionName, sessionWithStudents.courseId));
        
        ______TS("session does not contain students");
        
        assertFalse (fsLogic.isFeedbackSessionHasQuestionForStudents(sessionWithoutStudents.feedbackSessionName, sessionWithoutStudents.courseId));
    }
    
    public void testGetFeedbackSessionsClosingWithinTimeLimit() throws Exception {
        
        
        ______TS("init : 0 non private sessions closing within time-limit");
        List<FeedbackSessionAttributes> sessionList = fsLogic
                .getFeedbackSessionsClosingWithinTimeLimit();
        
        assertEquals(0, sessionList.size());
        
        ______TS("typical case : 1 non private session closing within time limit");
        FeedbackSessionAttributes session = getNewFeedbackSession();
        session.timeZone = 0;
        session.feedbackSessionType = FeedbackSessionType.STANDARD;
        session.sessionVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        ThreadHelper.waitBriefly(); // this one is correctly used
        fsLogic.createFeedbackSession(session);
        
        sessionList = fsLogic
                .getFeedbackSessionsClosingWithinTimeLimit();
        
        assertEquals(1, sessionList.size());
        assertEquals(session.feedbackSessionName, 
                sessionList.get(0).feedbackSessionName);
        
        ______TS("case : 1 private session closing within time limit");
        session.feedbackSessionType = FeedbackSessionType.PRIVATE;
        fsLogic.updateFeedbackSession(session);
        
        sessionList = fsLogic
                .getFeedbackSessionsClosingWithinTimeLimit();
        assertEquals(0, sessionList.size());
        
        //delete the newly added session as removeAndRestoreTypicalDataInDatastore()
                //wont do it
        fsLogic.deleteFeedbackSessionCascade(session.feedbackSessionName,
                session.courseId);
    }
    
    public void testGetFeedbackSessionsWhichNeedOpenMailsToBeSent() throws Exception {
        
        ______TS("init : 0 open sessions");
        List<FeedbackSessionAttributes> sessionList = fsLogic
                .getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
        
        assertEquals(0, sessionList.size());
        
        ______TS("case : 1 open session with mail unsent");
        FeedbackSessionAttributes session = getNewFeedbackSession();
        session.timeZone = 0;
        session.feedbackSessionType = FeedbackSessionType.STANDARD;
        session.sessionVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        session.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        session.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        session.sentOpenEmail = false;
        fsLogic.createFeedbackSession(session);        
        
        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
        assertEquals(1, sessionList.size());
        assertEquals(sessionList.get(0).feedbackSessionName,
                session.feedbackSessionName);
        
        ______TS("typical case : 1 open session with mail sent");
        session.sentOpenEmail = true;
        fsLogic.updateFeedbackSession(session);
        
        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
        
        assertEquals(0, sessionList.size());
        
        ______TS("case : 1 closed session with mail unsent");
        session.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        fsLogic.updateFeedbackSession(session);
        
        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
        assertEquals(0, sessionList.size());
        
        //delete the newly added session as removeAndRestoreTypicalDataInDatastore()
        //wont do it
        fsLogic.deleteFeedbackSessionCascade(session.feedbackSessionName,
                session.courseId);
    }
    
    public void testGetFeedbackSessionWhichNeedPublishedEmailsToBeSent() throws Exception {
        
        
        ______TS("init : no published sessions");
        unpublishAllSessions();
        List<FeedbackSessionAttributes> sessionList = fsLogic
                .getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
        
        assertEquals(0, sessionList.size());
        
        ______TS("case : 1 published session with mail unsent");
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        session.timeZone = 0;
        session.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        session.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session.resultsVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        
        session.sentPublishedEmail = false;
        fsLogic.updateFeedbackSession(session);
        
        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
        assertEquals(1, sessionList.size());
        assertEquals(sessionList.get(0).feedbackSessionName,
                session.feedbackSessionName);
        
        ______TS("case : 1 published session with mail sent");
        session.sentPublishedEmail = true;
        fsLogic.updateFeedbackSession(session);        
        
        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
        assertEquals(0, sessionList.size());
    }
    
    public void testCreateAndDeleteFeedbackSession() throws InvalidParametersException, EntityAlreadyExistsException {        
        ______TS("test create");
        
        FeedbackSessionAttributes fs = getNewFeedbackSession();
        fsLogic.createFeedbackSession(fs);
        TestHelper.verifyPresentInDatastore(fs);
        
        ______TS("test create with invalid session name");
        fs.feedbackSessionName = "test & test";
        try {
            fsLogic.createFeedbackSession(fs);
            signalFailureToDetectException();
        } catch (Exception a) {
            assertEquals("The provided feedback session name is not acceptable to TEAMMATES as it cannot contain the following special html characters in brackets: (&lt; &gt; \\ &#x2f; &#39; &amp;)", a.getMessage());
        }

        fs.feedbackSessionName = "test %| test";
        try {
            fsLogic.createFeedbackSession(fs);
            signalFailureToDetectException();
        } catch (Exception a) {
            assertEquals("\"test %| test\" is not acceptable to TEAMMATES as feedback session name because it contains invalid characters. All feedback session name must start with an alphanumeric character, and cannot contain any vertical bar (|) or percent sign (%).", a.getMessage());
        }
        
        ______TS("test delete");
        fs = getNewFeedbackSession();
        // Create a question under the session to test for cascading during delete.
        FeedbackQuestionAttributes fq = new FeedbackQuestionAttributes();
        fq.feedbackSessionName = fs.feedbackSessionName;
        fq.courseId = fs.courseId;
        fq.questionNumber = 1;
        fq.creatorEmail = fs.creatorEmail;
        fq.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        fq.giverType = FeedbackParticipantType.STUDENTS;
        fq.recipientType = FeedbackParticipantType.TEAMS;
        fq.questionMetaData = new Text("question to be deleted through cascade");
        fq.questionType = FeedbackQuestionType.TEXT;
        fq.showResponsesTo = new ArrayList<FeedbackParticipantType>();
        fq.showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
        fq.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        
        fqLogic.createFeedbackQuestion(fq);
        
        fsLogic.deleteFeedbackSessionCascade(fs.feedbackSessionName, fs.courseId);
        TestHelper.verifyAbsentInDatastore(fs);
        TestHelper.verifyAbsentInDatastore(fq);
    }
    
    public void testCopyFeedbackSession() throws Exception {
        
        ______TS("Test copy");
        
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor2OfCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
        CourseAttributes typicalCourse2 = dataBundle.courses.get("typicalCourse2");
        FeedbackSessionAttributes copiedSession = fsLogic.copyFeedbackSession(
                "Copied Session", typicalCourse2.id,
                session1InCourse1.feedbackSessionName,
                session1InCourse1.courseId, instructor2OfCourse1.email);
        TestHelper.verifyPresentInDatastore(copiedSession);
        
        assertEquals("Copied Session", copiedSession.feedbackSessionName);
        assertEquals(typicalCourse2.id, copiedSession.courseId);
        List<FeedbackQuestionAttributes> questions1 = fqLogic.getFeedbackQuestionsForSession(session1InCourse1.feedbackSessionName, session1InCourse1.courseId);
        List<FeedbackQuestionAttributes> questions2 = fqLogic.getFeedbackQuestionsForSession(copiedSession.feedbackSessionName, copiedSession.courseId);
        
        assertEquals(questions1.size(), questions2.size());
        for(int i = 0; i < questions1.size(); i++){
            FeedbackQuestionAttributes question1 = questions1.get(i);
            FeedbackQuestionDetails questionDetails1 = question1.getQuestionDetails();
            FeedbackQuestionAttributes question2 = questions2.get(i);
            FeedbackQuestionDetails questionDetails2 = question2.getQuestionDetails();
            
            assertEquals(questionDetails1.questionText, questionDetails2.questionText);
            assertEquals(question1.giverType, question2.giverType);
            assertEquals(question1.recipientType, question2.recipientType);
            assertEquals(question1.questionType, question2.questionType);
            assertEquals(question1.numberOfEntitiesToGiveFeedbackTo, question2.numberOfEntitiesToGiveFeedbackTo);
        }
        assertEquals(0, copiedSession.respondingInstructorList.size());
        assertEquals(0, copiedSession.respondingStudentList.size());
        
        ______TS("Failure case: duplicate session");
        
        try {
            fsLogic.copyFeedbackSession(
                    session1InCourse1.feedbackSessionName, session1InCourse1.courseId,
                    session1InCourse1.feedbackSessionName,
                    session1InCourse1.courseId, instructor2OfCourse1.email);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e){
            ignoreExpectedException();
        }
        
        fsLogic.deleteFeedbackSessionCascade(copiedSession.feedbackSessionName, copiedSession.courseId);
    }
    
    
    public void testGetFeedbackSessionDetailsForInstructor() throws Exception {
        
        // This file contains a session with a private session + a standard
        // session + a special session with all questions without recipients.
        DataBundle newDataBundle = loadDataBundle("/FeedbackSessionDetailsTest.json");
        new BackDoorLogic().persistDataBundle(newDataBundle);
        
        Map<String,FeedbackSessionDetailsBundle> detailsMap =
                new HashMap<String,FeedbackSessionDetailsBundle>();
        
        List<FeedbackSessionDetailsBundle> detailsList = 
                fsLogic.getFeedbackSessionDetailsForInstructor(newDataBundle.instructors.get("instructor1OfCourse1").googleId);
        
        List<String> expectedSessions = new ArrayList<String>();
        expectedSessions.add(newDataBundle.feedbackSessions.get("standard.session").toString());
        expectedSessions.add(newDataBundle.feedbackSessions.get("no.responses.session").toString());
        expectedSessions.add(newDataBundle.feedbackSessions.get("no.recipients.session").toString());
        expectedSessions.add(newDataBundle.feedbackSessions.get("private.session").toString());
        
        String actualSessions = "";
        for (FeedbackSessionDetailsBundle details : detailsList) {
            actualSessions += details.feedbackSession.toString();
            detailsMap.put(
                    details.feedbackSession.feedbackSessionName + "%" +
                    details.feedbackSession.courseId,
                    details);
        }
        
        ______TS("standard session");
        
        assertEquals(4, detailsList.size());
        AssertHelper.assertContains(expectedSessions, actualSessions);
        
        FeedbackSessionStats stats =
                detailsMap.get(newDataBundle.feedbackSessions.get("standard.session").feedbackSessionName + "%" +
                                newDataBundle.feedbackSessions.get("standard.session").courseId).stats;
        
        // 2 instructors, 6 students = 8
        assertEquals(8, stats.expectedTotal);
        // 1 instructor, 1 student, did not respond => 8-2=6
        assertEquals(6, stats.submittedTotal);
        
        
        ______TS("No recipients session");
        stats = detailsMap.get(newDataBundle.feedbackSessions.get("no.recipients.session").feedbackSessionName + "%" +
                                newDataBundle.feedbackSessions.get("no.recipients.session").courseId).stats;
        
        // 2 instructors, 6 students = 8
        assertEquals(8, stats.expectedTotal);
        // only 1 student responded
        assertEquals(1, stats.submittedTotal);
        
        ______TS("No responses session");
        stats = detailsMap.get(newDataBundle.feedbackSessions.get("no.responses.session").feedbackSessionName + "%" +
                                newDataBundle.feedbackSessions.get("no.responses.session").courseId).stats;
        
        // 1 instructors, 1 students = 2
        assertEquals(2, stats.expectedTotal);
        // no responses
        assertEquals(0, stats.submittedTotal);
        
        ______TS("private session with questions");
        stats = detailsMap.get(newDataBundle.feedbackSessions.get("private.session").feedbackSessionName + "%" +
                newDataBundle.feedbackSessions.get("private.session").courseId).stats;
        assertEquals(1, stats.expectedTotal);
        // For private sessions, we mark as completed only when creator has finished all questions.
        assertEquals(0, stats.submittedTotal);
        
        ______TS("change private session to non-private");
        FeedbackSessionAttributes privateSession = 
                newDataBundle.feedbackSessions.get("private.session");
        privateSession.sessionVisibleFromTime = privateSession.startTime;
        privateSession.endTime = TimeHelper.convertToDate("2015-04-01 10:00 PM UTC");
        privateSession.feedbackSessionType = FeedbackSessionType.STANDARD;
        fsLogic.updateFeedbackSession(privateSession);
        
        // Re-read details
        detailsList = fsLogic.getFeedbackSessionDetailsForInstructor(
                newDataBundle.instructors.get("instructor1OfCourse1").googleId);
        for (FeedbackSessionDetailsBundle details : detailsList) {
            if(details.feedbackSession.feedbackSessionName.equals(
                    newDataBundle.feedbackSessions.get("private.session").feedbackSessionName)){
                stats = details.stats;
                break;
            }
        }
        // 1 instructor (creator only), 6 students = 8
        assertEquals(7, stats.expectedTotal);
        // 1 instructor, 1 student responded
        assertEquals(2, stats.submittedTotal);
        
        ______TS("private session without questions");
        
        expectedSessions.clear();
        expectedSessions.add(newDataBundle.feedbackSessions.get("private.session.noquestions").toString());
        expectedSessions.add(newDataBundle.feedbackSessions.get("private.session.done").toString());
        
        detailsList = fsLogic.getFeedbackSessionDetailsForInstructor(
                newDataBundle.instructors.get("instructor2OfCourse1").googleId);
        
        detailsMap.clear();
        actualSessions = "";
        for (FeedbackSessionDetailsBundle details : detailsList) {
            actualSessions += details.feedbackSession.toString();
            detailsMap.put(
                    details.feedbackSession.feedbackSessionName + "%" +
                    details.feedbackSession.courseId,
                    details);
        }
        
        AssertHelper.assertContains(expectedSessions, actualSessions);   
        
        stats = detailsMap.get(newDataBundle.feedbackSessions.get("private.session.noquestions").feedbackSessionName + "%" 
                + newDataBundle.feedbackSessions.get("private.session.noquestions").courseId).stats;
        
        assertEquals(0, stats.expectedTotal);
        assertEquals(0, stats.submittedTotal);
        
        ______TS("completed private session");
        
        stats = detailsMap.get(newDataBundle.feedbackSessions.get("private.session.done").feedbackSessionName + "%" 
                + newDataBundle.feedbackSessions.get("private.session.done").courseId).stats;
        
        assertEquals(1, stats.expectedTotal);
        assertEquals(1, stats.submittedTotal);
        
        ______TS("private session with questions with no recipients");
        
        expectedSessions.clear();
        expectedSessions.add(newDataBundle.feedbackSessions.get("private.session.norecipients").toString());
        
        detailsList = fsLogic.getFeedbackSessionDetailsForInstructor(
                newDataBundle.instructors.get("instructor1OfCourse3").googleId);
        
        detailsMap.clear();
        actualSessions = "";
        for (FeedbackSessionDetailsBundle details : detailsList) {
            actualSessions += details.feedbackSession.toString();
            detailsMap.put(
                    details.feedbackSession.feedbackSessionName + "%" +
                    details.feedbackSession.courseId,
                    details);
        }
        
        AssertHelper.assertContains(expectedSessions, actualSessions);  
        stats = detailsMap.get(newDataBundle.feedbackSessions.get("private.session.norecipients").feedbackSessionName + "%" 
                + newDataBundle.feedbackSessions.get("private.session.norecipients").courseId).stats;
        
        assertEquals(0, stats.expectedTotal);
        assertEquals(0, stats.submittedTotal);
        
        ______TS("instructor does not exist");
            
        assertTrue(fsLogic.getFeedbackSessionDetailsForInstructor("non-existent.google.id").isEmpty());
                    
    }
    
    public void testGetFeedbackSessionsForCourse() throws Exception {
        
        List<FeedbackSessionAttributes> actualSessions = null;
        
        ______TS("non-existent course");
        
        try {
            fsLogic.getFeedbackSessionsForUserInCourse("NonExistentCourseId", "randomUserId");
            signalFailureToDetectException("Did not detect that course does not exist.");
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to get feedback sessions for a course that does not exist.", edne.getMessage());
        }
        
       ______TS("Student viewing: 2 visible, 1 awaiting, 1 no questions");
        
        // 2 valid sessions in course 1, 0 in course 2.
        
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse1", "student1InCourse1@gmail.tmt");
        
        // Student can see sessions 1 and 2. Session 3 has no questions. Session 4 is not yet visible for students.
        String expected =
                dataBundle.feedbackSessions.get("session1InCourse1").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("session2InCourse1").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("gracePeriodSession").toString() + Const.EOL;
        
        for (FeedbackSessionAttributes session : actualSessions) {
            AssertHelper.assertContains(session.toString(), expected);
        }
        assertTrue(actualSessions.size() == 3);
        
        // Course 2 only has an instructor session and a private session.
        // The private session is not viewable to students,
        // but the instructor session has questions where responses are visible
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "student1InCourse2@gmail.tmt");        
        assertEquals(1, actualSessions.size());
                
        ______TS("Instructor viewing");
        
        // 3 valid sessions in course 1, 1 in course 2.
        
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse1", "instructor1@course1.tmt");
        
        // Instructors should be able to see all sessions for the course
        expected =
                dataBundle.feedbackSessions.get("session1InCourse1").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("session2InCourse1").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("empty.session").toString() + Const.EOL + 
                dataBundle.feedbackSessions.get("awaiting.session").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("closedSession").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("gracePeriodSession").toString() + Const.EOL;
        
        for (FeedbackSessionAttributes session : actualSessions) {
            AssertHelper.assertContains(session.toString(), expected);
        }
        assertTrue(actualSessions.size() == 6);
        
        // We should only have one session here as session 2 is private and this instructor is not the creator.
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "instructor2@course2.tmt");
        
        assertEquals(actualSessions.get(0).toString(),
                dataBundle.feedbackSessions.get("session2InCourse2").toString());
        assertTrue(actualSessions.size() == 1);

        
        ______TS("Private session viewing");
        
        // This is the creator for the private session.
        // We have already tested above that other instructors cannot see it.
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "instructor1@course2.tmt");
        AssertHelper.assertContains(dataBundle.feedbackSessions.get("session1InCourse2").toString(),
                actualSessions.toString());

        
        ______TS("Feedback session without questions for students but with visible responses are visible");
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfArchivedCourse", "student1InCourse1@gmail.tmt");
        AssertHelper.assertContains(dataBundle.feedbackSessions.get("archiveCourse.session1").toString(),
                actualSessions.toString());
    }
    
    public void testGetFeedbackSessionQuestionsForStudent() throws Exception {
        
        ______TS("standard test");

        
        
        FeedbackSessionQuestionsBundle actual =
                fsLogic.getFeedbackSessionQuestionsForStudent(
                        "First feedback session", "idOfTypicalCourse1", "student1InCourse1@gmail.tmt");
        
        // We just test this once.
        assertEquals(actual.feedbackSession.toString(), 
                dataBundle.feedbackSessions.get("session1InCourse1").toString());
        
        // There should be 2 question for students to do in session 1.
        // The final question is set for SELF (creator) only.
        assertEquals(2, actual.questionResponseBundle.size());
        
        // Question 1
        FeedbackQuestionAttributes expectedQuestion = 
                getQuestionFromDatastore("qn1InSession1InCourse1");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        
        String expectedResponsesString = getResponseFromDatastore("response1ForQ1S1C1", dataBundle).toString();
        List<String> actualResponses = new ArrayList<String>();        
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);
        
        // Question 2
        expectedQuestion = getQuestionFromDatastore("qn2InSession1InCourse1");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        
        expectedResponsesString = getResponseFromDatastore("response2ForQ2S1C1",dataBundle).toString();    
        actualResponses.clear();        
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);
        
        ______TS("team feedback test");

        // Check that student3 get team member's (student4) feedback response as well (for team question).
        actual = fsLogic.getFeedbackSessionQuestionsForStudent(
                        "Second feedback session", "idOfTypicalCourse1", "student3InCourse1@gmail.tmt");

        assertEquals(2, actual.questionResponseBundle.size());
        
        // Question 1
        expectedQuestion = getQuestionFromDatastore("team.feedback");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

        expectedResponsesString = getResponseFromDatastore(
                "response1ForQ1S2C1", dataBundle).toString();
        actualResponses.clear();
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle
                .get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);
        
        // Question 2, no responses from this student yet
        expectedQuestion = getQuestionFromDatastore("team.members.feedback");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        assertTrue(actual.questionResponseBundle.get(expectedQuestion).isEmpty());
        
        ______TS("failure: invalid session");
        
        try {
            fsLogic.getFeedbackSessionQuestionsForStudent(
                    "invalid session", "idOfTypicalCourse1", "student3InCourse1@gmail.tmt");
            signalFailureToDetectException("Did not detect that session does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to get a feedback session that does not exist.", e.getMessage());
        }
        
        ______TS("failure: non-existent student");
        
        try {
            fsLogic.getFeedbackSessionQuestionsForStudent(
                    "Second feedback session", "idOfTypicalCourse1", "randomUserId");
            signalFailureToDetectException("Did not detect that student does not exist.");
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to get a feedback session for student that does not exist.", edne.getMessage());
        }
        
        
        
    }
    
    public void testGetFeedbackSessionQuestionsForInstructor() throws Exception {
        ______TS("standard test");

        FeedbackSessionQuestionsBundle actual =
                fsLogic.getFeedbackSessionQuestionsForInstructor(
                        "Instructor feedback session", "idOfTypicalCourse2", "instructor1@course2.tmt");
        
        // We just test this once.
        assertEquals(dataBundle.feedbackSessions.get("session2InCourse2").toString(), 
                actual.feedbackSession.toString());
        
        // There should be 2 question for students to do in session 1.
        // The final question is set for SELF (creator) only.
        assertEquals(2, actual.questionResponseBundle.size());
        
        // Question 1
        FeedbackQuestionAttributes expectedQuestion = 
                getQuestionFromDatastore("qn1InSession2InCourse2");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        
        String expectedResponsesString = getResponseFromDatastore("response1ForQ1S2C2", dataBundle).toString();
        List<String> actualResponses = new ArrayList<String>();        
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);
        
        // Question 2
        expectedQuestion = getQuestionFromDatastore("qn2InSession2InCourse2");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        assertTrue(actual.questionResponseBundle.get(expectedQuestion).isEmpty());
        
        ______TS("private test: not creator");
        actual = fsLogic.getFeedbackSessionQuestionsForInstructor(
                        "Private feedback session", "idOfTypicalCourse2", "instructor2@course2.tmt");
        assertEquals(0, actual.questionResponseBundle.size());
        
        ______TS("private test: is creator");
        actual = fsLogic.getFeedbackSessionQuestionsForInstructor(
                        "Private feedback session", "idOfTypicalCourse2", "instructor1@course2.tmt");
        assertEquals(1, actual.questionResponseBundle.size());
        expectedQuestion = getQuestionFromDatastore("qn1InSession1InCourse2");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        
        ______TS("failure: invalid session");
        
        try {
            fsLogic.getFeedbackSessionQuestionsForInstructor(
                    "invalid session", "idOfTypicalCourse1", "instructor1@course1.tmt");
            signalFailureToDetectException("Did not detect that session does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to get a feedback session that does not exist.", e.getMessage());
        }
    }
    
    public void testGetFeedbackSessionResultsForUser() throws Exception {
        
        // This file contains a session with a private session + a standard
        // session which needs to have enough qn/response combinations to cover as much
        // of the SUT as possible
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
        new BackDoorLogic().persistDataBundle(responseBundle);
        
        ______TS("standard session with varied visibilities");        
        
        FeedbackSessionAttributes session =
                responseBundle.feedbackSessions.get("standard.session");
        
        /*** Test result bundle for student1 ***/
        StudentAttributes student = 
                responseBundle.students.get("student1InCourse1");        
        FeedbackSessionResultsBundle results =
                fsLogic.getFeedbackSessionResultsForStudent(session.feedbackSessionName, 
                        session.courseId, student.email);
    
        // We just check for correct session once
        assertEquals(session.toString(), results.feedbackSession.toString());    
        
        // Student can see responses: q1r1, q2r1,3, q3r1, qr4r2-3, q5r1, q7r1-2, q8r1-2
        // We don't check the actual IDs as this is also implicitly tested
        // later when checking the visibility table.
        assertEquals(11, results.responses.size());
        assertEquals(7, results.questions.size());
        
        // Test the user email-name maps used for display purposes
        String mapString = results.emailNameTable.toString();
        List<String> expectedStrings = new ArrayList<String>();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.tmt=student1 In Course1",
                "FSRTest.student2InCourse1@gmail.tmt=student2 In Course1",
                "FSRTest.student4InCourse1@gmail.tmt=student4 In Course1",
                "Team 1.1=Team 1.1",
                "Team 1.2=Team 1.2",
                "Team 1.3=Team 1.3",
                "Team 1.4=Team 1.4",
                "FSRTest.instr1@course1.tmt=Instructor1 Course1",
                "FSRTest.student1InCourse1@gmail.tmt" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.1",
                "FSRTest.student2InCourse1@gmail.tmt" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.1",
                "FSRTest.student4InCourse1@gmail.tmt" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.2",
                "Anonymous student 670710946@@Anonymous student 670710946.com=Anonymous student 670710946",
                "Anonymous student 412545508@@Anonymous student 412545508.com=Anonymous student 412545508");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(13, results.emailNameTable.size());

        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student4InCourse1@gmail.tmt=Team 1.2",
                "FSRTest.student1InCourse1@gmail.tmt=Team 1.1",
                "FSRTest.student1InCourse1@gmail.tmt's Team=",
                "FSRTest.student2InCourse1@gmail.tmt's Team=",
                "FSRTest.student4InCourse1@gmail.tmt's Team=",
                "FSRTest.student2InCourse1@gmail.tmt=Team 1.1",
                "Team 1.1=",
                "Team 1.3=",
                "Team 1.2=",
                "Team 1.4=",
                "FSRTest.instr1@course1.tmt=Instructors",
                "Anonymous student 670710946@@Anonymous student 670710946.com=Anonymous student 670710946"+ Const.TEAM_OF_EMAIL_OWNER,
                "Anonymous student 412545508@@Anonymous student 412545508.com=Anonymous student 412545508"+ Const.TEAM_OF_EMAIL_OWNER);
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(13, results.emailTeamNameTable.size());
        
        // Test 'Append TeamName to Name' for display purposes with Typical Cases
        expectedStrings.clear();
        List<String> actualStrings = new ArrayList<String>();
        for(FeedbackResponseAttributes response: results.responses) {
            String giverName = results.getNameForEmail(response.giverEmail);
            String giverTeamName = results.getTeamNameForEmail(response.giverEmail);
            giverName = results.appendTeamNameToName(giverName, giverTeamName);
            String recipientName = results.getNameForEmail(response.recipientEmail);
            String recipientTeamName = results.getTeamNameForEmail(response.recipientEmail);
            recipientName = results.appendTeamNameToName(recipientName, recipientTeamName);
            actualStrings.add(giverName);
            actualStrings.add(recipientName);
        }
        Collections.addAll(expectedStrings,
                "Anonymous student 670710946",
                "student1 In Course1 (Team 1.1)",
                "student2 In Course1 (Team 1.1)",
                "student4 In Course1 (Team 1.2)",
                "Instructor1 Course1 (Instructors)",
                "Anonymous student 412545508",
                "Team 1.1",
                "Team 1.2",
                "Team 1.3",
                "Team 1.4");
        AssertHelper.assertContains(expectedStrings, actualStrings.toString());
        
        // Test 'Append TeamName to Name' for display purposes with Special Cases
        expectedStrings.clear();
        actualStrings.clear();
        
        // case: Unknown User
        String UnknownUserName = Const.USER_UNKNOWN_TEXT;
        String someTeamName = "Some Team Name";
        UnknownUserName = results.appendTeamNameToName(UnknownUserName, someTeamName);
        actualStrings.add(UnknownUserName);
        
        // case: Nobody
        String NobodyUserName = Const.USER_NOBODY_TEXT;
        NobodyUserName = results.appendTeamNameToName(NobodyUserName, someTeamName);
        actualStrings.add(NobodyUserName);
        
        // case: Anonymous User
        String AnonymousUserName = "Anonymous " + System.currentTimeMillis();
        AnonymousUserName = results.appendTeamNameToName(AnonymousUserName, someTeamName);
        actualStrings.add(AnonymousUserName);
        Collections.addAll(expectedStrings,
                Const.USER_UNKNOWN_TEXT,
                Const.USER_NOBODY_TEXT,
                AnonymousUserName);
        assertEquals(expectedStrings.toString(), actualStrings.toString());
        
        // Test the generated response visibilityTable for userNames.        
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("qn1.resp1",responseBundle)+"={true,true}",
                getResponseId("qn2.resp1",responseBundle)+"={true,true}",
                getResponseId("qn2.resp3",responseBundle)+"={true,true}",
                getResponseId("qn3.resp1",responseBundle)+"={true,true}",
                getResponseId("qn4.resp2",responseBundle)+"={true,true}",
                getResponseId("qn4.resp3",responseBundle)+"={false,true}",
                getResponseId("qn5.resp1",responseBundle)+"={true,false}",
                getResponseId("qn7.resp1",responseBundle)+"={true,true}",
                getResponseId("qn7.resp2",responseBundle)+"={true,true}",
                getResponseId("qn8.resp1",responseBundle)+"={true,true}",
                getResponseId("qn8.resp2",responseBundle)+"={true,true}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(11, results.visibilityTable.size());
        
        
        /*** Test result bundle for instructor1 within a course ***/
        InstructorAttributes instructor =
                responseBundle.instructors.get("instructor1OfCourse1");        
        results = fsLogic.getFeedbackSessionResultsForInstructor(
                session.feedbackSessionName, 
                session.courseId, instructor.email);
        
        // Instructor can see responses: q2r1-3, q3r1-2, q4r1-3, q5r1, q6r1
        assertEquals(10, results.responses.size());
        //Instructor should still see all questions
        assertEquals(8, results.questions.size());
        
        // Test the user email-name maps used for display purposes
        mapString = results.emailNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "%GENERAL%=%NOBODY%",
                "FSRTest.student1InCourse1@gmail.tmt=student1 In Course1",
                "FSRTest.student2InCourse1@gmail.tmt=student2 In Course1",
                "FSRTest.student3InCourse1@gmail.tmt=student3 In Course1",
                "FSRTest.student4InCourse1@gmail.tmt=student4 In Course1",
                "FSRTest.student5InCourse1@gmail.tmt=student5 In Course1",
                "FSRTest.student6InCourse1@gmail.tmt=student6 In Course1",
                "FSRTest.instr1@course1.tmt=Instructor1 Course1",
                "FSRTest.instr2@course1.tmt=Instructor2 Course1",
                "Anonymous student 283462789@@Anonymous student 283462789.com=Anonymous student 283462789",
                "Anonymous student 928876384@@Anonymous student 928876384.com=Anonymous student 928876384",
                "Anonymous student 412545508@@Anonymous student 412545508.com=Anonymous student 412545508",
                "Anonymous student 541628227@@Anonymous student 541628227.com=Anonymous student 541628227",
                "Anonymous instructor 1805393227@@Anonymous instructor 1805393227.com=Anonymous instructor 1805393227",
                "Anonymous instructor 682119606@@Anonymous instructor 682119606.com=Anonymous instructor 682119606",
                "Team 1.2=Team 1.2",
                "Team 1.3=Team 1.3",
                "Team 1.4=Team 1.4");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(18, results.emailNameTable.size());
        
        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "%GENERAL%=",
                "FSRTest.student1InCourse1@gmail.tmt=Team 1.1",
                "FSRTest.student2InCourse1@gmail.tmt=Team 1.1",
                "FSRTest.student3InCourse1@gmail.tmt=Team 1.2",
                "FSRTest.student4InCourse1@gmail.tmt=Team 1.2",
                "FSRTest.student5InCourse1@gmail.tmt=Team 1.3",
                "FSRTest.student6InCourse1@gmail.tmt=Team 1.4",
                "FSRTest.instr2@course1.tmt=Instructors",
                "FSRTest.instr1@course1.tmt=Instructors",
                "Anonymous student 283462789@@Anonymous student 283462789.com=Anonymous student 283462789's Team",
                "Anonymous student 928876384@@Anonymous student 928876384.com=Anonymous student 928876384's Team",
                "Anonymous student 541628227@@Anonymous student 541628227.com=Anonymous student 541628227's Team",
                "Anonymous student 412545508@@Anonymous student 412545508.com=Anonymous student 412545508's Team",
                "Anonymous instructor 1805393227@@Anonymous instructor 1805393227.com=Anonymous instructor 1805393227's Team",
                "Anonymous instructor 682119606@@Anonymous instructor 682119606.com=Anonymous instructor 682119606's Team",
                "Team 1.3=",
                "Team 1.2=",
                "Team 1.4=");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(18, results.emailTeamNameTable.size());

        // Test the generated response visibilityTable for userNames.        
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("qn2.resp1",responseBundle)+"={false,false}",
                getResponseId("qn2.resp2",responseBundle)+"={false,false}",
                getResponseId("qn2.resp3",responseBundle)+"={false,false}",
                getResponseId("qn3.resp1",responseBundle)+"={true,false}",
                getResponseId("qn3.resp2",responseBundle)+"={false,false}",
                getResponseId("qn4.resp1",responseBundle)+"={true,true}",
                getResponseId("qn4.resp2",responseBundle)+"={true,true}",
                getResponseId("qn4.resp3",responseBundle)+"={true,true}",
                getResponseId("qn5.resp1",responseBundle)+"={false,true}",
                getResponseId("qn6.resp1",responseBundle)+"={true,true}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(10, results.visibilityTable.size());
        
        /*** Test result bundle for instructor1 within a section ***/
        
        results = fsLogic.getFeedbackSessionResultsForInstructorInSection(
                session.feedbackSessionName, 
                session.courseId, instructor.email, "Section A");
        
        // Instructor can see responses: q2r1-3, q3r1-2, q4r1-3, q5r1, q6r1
        assertEquals(7, results.responses.size());
        //Instructor should still see all questions
        assertEquals(8, results.questions.size());
        
        // Test the user email-name maps used for display purposes
        mapString = results.emailNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.tmt=student1 In Course1",
                "Anonymous student 283462789@@Anonymous student 283462789.com=Anonymous student 283462789",
                "Anonymous instructor 682119606@@Anonymous instructor 682119606.com=Anonymous instructor 682119606",
                "Anonymous student 412545508@@Anonymous student 412545508.com=Anonymous student 412545508",
                "FSRTest.student2InCourse1@gmail.tmt=student2 In Course1",
                "Team 1.4=Team 1.4",
                "FSRTest.instr1@course1.tmt=Instructor1 Course1");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(13, results.emailNameTable.size());
        
        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.tmt=Team 1.1",
                "Anonymous student 283462789@@Anonymous student 283462789.com=Anonymous student 283462789's Team",
                "Anonymous student 412545508@@Anonymous student 412545508.com=Anonymous student 412545508's Team",
                "Anonymous instructor 682119606@@Anonymous instructor 682119606.com=Anonymous instructor 682119606's Team",
                "FSRTest.student2InCourse1@gmail.tmt=Team 1.1",
                "Team 1.4=",
                "FSRTest.instr1@course1.tmt=Instructors");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(13, results.emailTeamNameTable.size());

        // Test the generated response visibilityTable for userNames.        
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("qn3.resp1",responseBundle)+"={true,false}",
                getResponseId("qn4.resp3",responseBundle)+"={true,true}",
                getResponseId("qn2.resp3",responseBundle)+"={false,false}",
                getResponseId("qn2.resp1",responseBundle)+"={false,false}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(7, results.visibilityTable.size());
        // TODO: test student2 too.
        
        ______TS("private session");

        session = responseBundle.feedbackSessions.get("private.session");
        
        /*** Test result bundle for student1 ***/
        student =  responseBundle.students.get("student1InCourse1");        
        results = fsLogic.getFeedbackSessionResultsForStudent(session.feedbackSessionName, 
                        session.courseId, student.email);
        
        assertEquals(0, results.questions.size());
        assertEquals(0, results.responses.size());
        assertEquals(0, results.emailNameTable.size());
        assertEquals(0, results.emailTeamNameTable.size());
        assertEquals(0, results.visibilityTable.size());
        
        /*** Test result bundle for instructor1 ***/
        
        instructor =
                responseBundle.instructors.get("instructor1OfCourse1");        
        results = fsLogic.getFeedbackSessionResultsForInstructor(
                session.feedbackSessionName, 
                session.courseId, instructor.email);
        
        // Can see all responses regardless of visibility settings.
        assertEquals(2, results.questions.size());
        assertEquals(2, results.responses.size());
        
        // Test the user email-name maps used for display purposes
        mapString = results.emailNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.tmt=student1 In Course1",
                "Team 1.2=Team 1.2",
                "Anonymous team 1605535342@@Anonymous team 1605535342.com=Anonymous team 1605535342",
                "FSRTest.instr1@course1.tmt=Instructor1 Course1");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(4, results.emailNameTable.size());
        
        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.tmt=Team 1.1",
                "Team 1.2=",
                "Anonymous team 1605535342@@Anonymous team 1605535342.com=Anonymous team 1605535342's Team",
                "FSRTest.instr1@course1.tmt=Instructors");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(4, results.emailTeamNameTable.size());

        // Test that name visibility is adhered to even when
        // it is a private session. (to protect anonymity during session type conversion)"
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("p.qn1.resp1",responseBundle)+"={true,true}",
                getResponseId("p.qn2.resp1",responseBundle)+"={true,false}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(2, results.visibilityTable.size());
        
        ______TS("failure: no session");
                
        try {
            fsLogic.getFeedbackSessionResultsForInstructor("invalid session", 
                session.courseId, instructor.email);
            signalFailureToDetectException("Did not detect that session does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to view non-existent feedback session.", e.getMessage());
        }
        //TODO: check for cases where a person is both a student and an instructor
    }
    
    
    public void testGetFeedbackSessionResultsSummaryAsCsv() throws Exception {

        ______TS("typical case");
    
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        String export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);
        
        /* This is what export should look like:
        ==================================
        Course,idOfTypicalCourse1
        Session Name,First feedback session
        
        
        Question 1,"What is the best selling point of your product?"
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Student 1 self feedback."
        "Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","I'm cool'"
        "Team 1.1","student3 In Course1","Course1","Team 1.1","student3 In Course1","Course1","No Response"
        "Team 1.1","student4 In Course1","Course1","Team 1.1","student4 In Course1","Course1","No Response"
        "Team 1.2","student5 In Course1","Course1","Team 1.2","student5 In Course1","Course1","No Response"
        
        Question 2,"Rate 5 other students' products",
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student1 In" Course1,"Course1","student1InCourse1@gmail.tmt","Response from student 1 to student 2."
        "Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","Team 1.1","student1 In" Course1,"Course1","student1InCourse1@gmail.tmt","Response from student 2 to student 1."
        "Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","Team 1.1","student2 In" Course1,"Course1","student2InCourse1@gmail.tmt","Response from student 3 ""to"" student 2.
        Multiline test."
        
        
        Question 3,"My comments on the class",
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Instructors","Instructor1 Course1","Instructor1 Course1","instructor1@course1.tmt","-","-","-","-","Good work, keep it up!"
        */
        String[] exportLines = export.split(Const.EOL);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        assertEquals(exportLines[2], "");
        assertEquals(exportLines[3], "");
        assertEquals(exportLines[4], "Question 1,\"What is the best selling point of your product?\"");
        assertEquals(exportLines[5], "");
        assertEquals(exportLines[6], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback");
        assertEquals(exportLines[7], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Student 1 self feedback.\"");
        // checking single quotes inside cell
        assertEquals(exportLines[8], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"I'm cool'\"");
        assertEquals(exportLines[9], "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[10], "\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[11],"\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[12], "");
        assertEquals(exportLines[13], "");
        assertEquals(exportLines[14], "Question 2,\"Rate 1 other student's product\"");
        assertEquals(exportLines[15], "");
        assertEquals(exportLines[16], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback");
        assertEquals(exportLines[17], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Response from student 1 to student 2.\"");
        assertEquals(exportLines[18], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Response from student 2 to student 1.\"");
        // checking double quotes inside cell + multiline cell
        assertEquals(exportLines[19].trim(), "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Response from student 3 \"\"to\"\" student 2.");
        assertEquals(exportLines[20], "Multiline test.\"");
        assertEquals(exportLines[21], "");
        assertEquals(exportLines[22], "");
        assertEquals(exportLines[23], "Question 3,\"My comments on the class\"");
        assertEquals(exportLines[24], "");
        assertEquals(exportLines[25], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback");
        // checking comma inside cell
        assertEquals(exportLines[26], "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"-\",\"-\",\"-\",\"-\",\"Good work, keep it up!\"");
        
        ______TS("MCQ results");
        
        removeAndRestoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");
        DataBundle newDataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        session = newDataBundle.feedbackSessions.get("mcqSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");
        
        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);
        

        System.out.println(export);
        
        /*This is how the export should look like
        =======================================
        Course,"FSQTT.idOfTypicalCourse1"
        Session Name,"MCQ Session"
        
        
        Question 1,"What do you like best about our product?"
        
        Summary Statistics,
        Choice, Response Count, Percentage
        "It's good",1,50
        "It's perfect",1,50
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Team 1.1","student1 In" Course1,"Course1","student1InCourse1@gmail.tmt","Team 1.1","student1 In" Course1,"Course1","student1InCourse1@gmail.tmt","It's good"
        "Team 1.1","student2 In" Course1,"Course1","student2InCourse1@gmail.tmt","Team 1.1","student2 In" Course1,"Course1","student2InCourse1@gmail.tmt","It's perfect"
        "Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt""Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","No Response"
        "Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt""Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","No Response"
        "Team 1.2","student5 In Course1","Course1","student5InCourse1@gmail.tmt""Team 1.2","student5 In Course1","Course1","student5InCourse1@gmail.tmt","No Response"
        
        
        Question 2,"What do you like best about the class' product?"
        
        Summary Statistics,
        Choice, Response Count, Percentage
        "It's good",1,50
        "It's perfect",1,50
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Instructors","Instructor1 Course1","Instructor1 Course1","instructor1@course1.tmt","Instructors","Instructor1 Course1","Instructor1 Course1","instructor1@course1.tmt","It's good"
        "Instructors","Instructor2 Course1","Instructor2 Course1","instructor2@course1.tmt","Instructors","Instructor2 Course1","Instructor2 Course1","instructor2@course1.tmt","It's perfect"
        "Instructors","Instructor3 Course1","Instructor3 Course1","instructor3@course1.tmt""Instructors","Instructor3 Course1","Instructor3 Course1","instructor3@course1.tmt""No Response"
        */
        
        exportLines = export.split(Const.EOL);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        assertEquals(exportLines[2], "");
        assertEquals(exportLines[3], "");
        assertEquals(exportLines[4], "Question 1,\"What do you like best about our product?\"");
        assertEquals(exportLines[5], "");
        assertEquals(exportLines[6], "Summary Statistics,");
        assertEquals(exportLines[7], "Choice, Response Count, Percentage");
        assertEquals(exportLines[8], "\"It's good\",1,50");
        assertEquals(exportLines[9], "\"It's perfect\",1,50");
        assertEquals(exportLines[10], "");
        assertEquals(exportLines[11], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback");
        assertEquals(exportLines[12], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"It's good\"");
        assertEquals(exportLines[13], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"It's perfect\"");
        assertEquals(exportLines[14], "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[15], "\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[16], "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[17], "");
        assertEquals(exportLines[18], "");
        assertEquals(exportLines[19], "Question 2,\"What do you like best about the class' product?\"");
        assertEquals(exportLines[20], "");
        assertEquals(exportLines[21], "Summary Statistics,");
        assertEquals(exportLines[22], "Choice, Response Count, Percentage");
        assertEquals(exportLines[23], "\"It's good\",1,50");
        assertEquals(exportLines[24], "\"It's perfect\",1,50");
        assertEquals(exportLines[25], "");
        assertEquals(exportLines[26], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback");
        assertEquals(exportLines[27], "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"It's good\"");
        assertEquals(exportLines[28], "\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"It's perfect\"");
        assertEquals(exportLines[29], "\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"No Response\"");
        
        ______TS("MSQ results");
        
        session = newDataBundle.feedbackSessions.get("msqSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");
        
        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);

        System.out.println(export);
        
        /*This is how the export should look like
        =======================================
        Course,"FSQTT.idOfTypicalCourse1"
        Session Name,"MSQ Session"
        
        
        Question 1,"What do you like best about our product?"
        
        Summary Statistics,
        Choice, Response Count, Percentage
        "It's good",2,66.67
        "It's perfect",1,33.33
        
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedbacks:,"It's good","It's perfect"
        "Team 1.1","student1 In" Course1,"Course1","student1InCourse1@gmail.tmt","Team 1.1","student1 In" Course1,"Course1","student1InCourse1@gmail.tmt",,"It's good","It's perfect"
        "Team 1.1","student2 In" Course1,"Course1","student2InCourse1@gmail.tmt","Team 1.1","student2 In" Course1,"Course1","student2InCourse1@gmail.tmt",,"It's good",
        "Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","No Response"
        "Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","No Response"
        "Team 1.2","student5 In Course1","Course1","student5InCourse1@gmail.tmt","Team 1.2","student5 In Course1","Course1","student5InCourse1@gmail.tmt","No Response"
        
        Question 2,"What do you like best about the class' product?"
        
        Summary Statistics,
        Choice, Response Count, Percentage
        "It's good",1,33.33
        "It's perfect",2,66.67
        
        
        Team,Giver's Full Name,Giver's Last Name,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Feedbacks:,"It's good","It's perfect"
        "Instructors","Instructor1 Course1","Instructor1 Course1","instructor1@course1.tmt","Instructors","Instructor1 Course1","Instructor1 Course1","instructor1@course1.tmt",,"It's good","It's perfect"
        "Instructors","Instructor2 Course1","Instructor2 Course1","instructor2@course1.tmt","Instructors","Instructor2 Course1","Instructor2 Course1","instructor2@course1.tmt",,,"It's perfect"
        "Instructors","Instructor3 Course1","Instructor3 Course1","instructor3@course1.tmt","Instructors","Instructor3 Course1","Instructor3 Course1","instructor3@course1.tmt",,,"No Response"
        */
        
        exportLines = export.split(Const.EOL);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        assertEquals(exportLines[2], "");
        assertEquals(exportLines[3], "");
        assertEquals(exportLines[4], "Question 1,\"What do you like best about our product?\"");
        assertEquals(exportLines[5], "");
        assertEquals(exportLines[6], "Summary Statistics,");
        assertEquals(exportLines[7], "Choice, Response Count, Percentage");
        assertEquals(exportLines[8], "\"It's good\",2,66.67");
        assertEquals(exportLines[9], "\"It's perfect\",1,33.33");
        assertEquals(exportLines[10], "");
        assertEquals(exportLines[11], "");
        assertEquals(exportLines[12], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedbacks:,\"It's good\",\"It's perfect\"");
        assertEquals(exportLines[13], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",,\"It's good\",\"It's perfect\"");
        assertEquals(exportLines[14], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",,\"It's good\",");
        assertEquals(exportLines[15], "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",");
        assertEquals(exportLines[16], "\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[17], "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[18], "");
        assertEquals(exportLines[19], "");
        assertEquals(exportLines[20], "Question 2,\"What do you like best about the class' product?\"");
        assertEquals(exportLines[21], "");
        assertEquals(exportLines[22], "Summary Statistics,");
        assertEquals(exportLines[23], "Choice, Response Count, Percentage");
        assertEquals(exportLines[24], "\"It's good\",1,33.33");
        assertEquals(exportLines[25], "\"It's perfect\",2,66.67");
        assertEquals(exportLines[26], "");
        assertEquals(exportLines[27], "");
        assertEquals(exportLines[28], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedbacks:,\"It's good\",\"It's perfect\"");
        assertEquals(exportLines[29], "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",,\"It's good\",\"It's perfect\"");
        assertEquals(exportLines[30], "\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",,,\"It's perfect\"");
        assertEquals(exportLines[31], "\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",");
       
        ______TS("NUMSCALE results");
        
        session = newDataBundle.feedbackSessions.get("numscaleSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");
        
        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);
        
        System.out.println(export);
        
        /*This is how the export should look like
        =======================================
        Course,"FSQTT.idOfTypicalCourse1"
        Session Name,"NUMSCALE Session"
        
        
        Question 1,"Rate our product."
        
        Summary Statistics,
        Team, Recipient, Average, Minimum, Maximum, Average excluding self response
        "Team 1.1","student2 In Course1",2,2,2,-
        "Team 1.1","student1 In Course1",3.5,3.5,3.5,-
        
        
       Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt",3.5
        "Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt",2
        "Team 1.1","student3 In Course1","Course1","student2InCourse1@gmail.tmt","Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt",No Response
        "Team 1.1","student4 In Course1","Course1","student2InCourse1@gmail.tmt","Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt",No Response
        "Team 1.2","student5 In Course1","Course1","student2InCourse1@gmail.tmt","Team 1.2","student5 In Course1","Course1","student5InCourse1@gmail.tmt",No Response
        
        
        Question 2,"Rate our product."
        
        Summary Statistics,
        Team, Recipient, Average, Minimum, Maximum
        "Instructors","Instructor1 Course1",4.5,4.5,4.5
        "Instructors","Instructor2 Course1",1,1,1
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Instructors","Instructor1 Course1","Instructor1 Course1","instructor1@course1.tmt","Instructors","Instructor1 Course1","Instructor1 Course1","instructor1@course1.tmt",4.5
        "Instructors","Instructor2 Course1","Instructor2 Course1","instructor2@course1.tmt","Instructors","Instructor2 Course1","Instructor2 Course1","instructor2@course1.tmt",1
        "Instructors","Instructor3 Course1","Instructor3 Course1","instructor3@course1.tmt","Instructors","Instructor3 Course1","Instructor3 Course1","instructor3@course1.tmt","No Response"
        */
        
        exportLines = export.split(Const.EOL);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        assertEquals(exportLines[2], "");
        assertEquals(exportLines[3], "");
        assertEquals(exportLines[4], "Question 1,\"Rate our product.\"");
        assertEquals(exportLines[5], "");
        assertEquals(exportLines[6], "Summary Statistics,");
        assertEquals(exportLines[7], "Team, Recipient, Average, Minimum, Maximum");
        assertEquals(exportLines[8], "\"Team 1.1\",\"student2 In Course1\",2,2,2");
        assertEquals(exportLines[9], "\"Team 1.1\",\"student1 In Course1\",3.5,3.5,3.5");
        assertEquals(exportLines[10], "");
        assertEquals(exportLines[11], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback");
        assertEquals(exportLines[12], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",3.5");
        assertEquals(exportLines[13], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",2");
        assertEquals(exportLines[14], "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[15], "\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[16], "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[17], "");
        assertEquals(exportLines[18], "");
        assertEquals(exportLines[19], "Question 2,\"Rate our product.\"");
        assertEquals(exportLines[20], "");
        assertEquals(exportLines[21], "Summary Statistics,");
        assertEquals(exportLines[22], "Team, Recipient, Average, Minimum, Maximum");
        assertEquals(exportLines[23], "\"Instructors\",\"Instructor1 Course1\",4.5,4.5,4.5");
        assertEquals(exportLines[24], "\"Instructors\",\"Instructor2 Course1\",1,1,1");
        assertEquals(exportLines[25], "");
        assertEquals(exportLines[26], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback");
        assertEquals(exportLines[27], "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",4.5");
        assertEquals(exportLines[28], "\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",1");
        assertEquals(exportLines[29], "\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"No Response\"");
       
        
        ______TS("CONSTSUM results");
        
        session = newDataBundle.feedbackSessions.get("constSumSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");
        
        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);
        
        System.out.println(export);
        
        /*This is how the export should look like
        =======================================
        Course,"FSQTT.idOfTypicalCourse1"
        Session Name,"CONSTSUM Session"
        
        
        Question 1,"How important are the following factors to you? Give points accordingly."
        
        Summary Statistics,
        Option, Average Points
        "Fun",50.5
        "Grades",49.5
        
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Team 1.1","student1 In Course1,"Course1","student1InCourse1@gmail.tmt","Team 1.1","student1 In Course1,"Course1","student1InCourse1@gmail.tmt",,20,80
        "Team 1.1","student2 In Course1,"Course1","student2InCourse1@gmail.tmt","Team 1.1","student2 In Course1,"Course1","student2InCourse1@gmail.tmt",,80,20
        "Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","No Response"
        "Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","No Response"
        "Team 1.2","student5 In Course1","Course1","student5InCourse1@gmail.tmt","Team 1.2","student5 In Course1","Course1","student5InCourse1@gmail.tmt","No Response"
        
        
        Question 2,"Split points among the teams"
        
        Summary Statistics,
        Recipient, Average Points
        "Team 1.1",80
        "Team 1.2",20
        
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Instructors","Instructor1 Course1","Instructor1 Course1","instructor1@course1.tmt","","Team 1.1","Team 1.1","instructor1@course1.tmt",80
        "Instructors","Instructor2 Course1","Instructor2 Course1","instructor2@course1.tmt","","Team 1.2","Team 1.2","instructor2@course1.tmt",20
        
        Question 3,"How much has each student worked?"

        Summary Statistics,
        Team, Recipient, Average Points
        Team 1.1,student1 In Course1,30
        Team 1.1,student2 In Course1,20
        Team 1.1,student3 In Course1,30
        Team 1.1,student4 In Course1,10
        Team 1.2,student5 In Course1,10


        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Instructors","Instructor1 Course1","Instructor1 Course1","instructor1@course1.tmt","Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt",30
        "Instructors","Instructor1 Course1","Instructor1 Course1","instructor1@course1.tmt","Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt",20
        "Instructors","Instructor1 Course1","Instructor1 Course1","instructor1@course1.tmt","Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt",30
        "Instructors","Instructor1 Course1","Instructor1 Course1","instructor1@course1.tmt","Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt",10
        "Instructors","Instructor1 Course1","Instructor1 Course1","instructor1@course1.tmt","Team 1.2","student5 In Course1","Course1","student5InCourse1@gmail.tmt",10
        */
        
        exportLines = export.split(Const.EOL);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        assertEquals(exportLines[2], "");
        assertEquals(exportLines[3], "");
        assertEquals(exportLines[4], "Question 1,\"How important are the following factors to you? Give points accordingly.\"");
        assertEquals(exportLines[5], "");
        assertEquals(exportLines[6], "Summary Statistics,");
        assertEquals(exportLines[7], "Option, Average Points");
        assertEquals(exportLines[8], "\"Fun\",50.5");
        assertEquals(exportLines[9], "\"Grades\",49.5");
        assertEquals(exportLines[10], "");
        assertEquals(exportLines[11], "");
        assertEquals(exportLines[12], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedbacks:,\"Grades\",\"Fun\"");
        assertEquals(exportLines[13], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",,19,81");
        assertEquals(exportLines[14], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",,80,20");
        assertEquals(exportLines[15], "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[16], "\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[17], "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[18], "");
        assertEquals(exportLines[19], "");
        assertEquals(exportLines[20], "Question 2,\"Split points among the teams\"");
        assertEquals(exportLines[21], "");
        assertEquals(exportLines[22], "Summary Statistics,");
        assertEquals(exportLines[23], "Team, Recipient, Average Points");
        assertEquals(exportLines[24], "\"\",\"Team 1.1\",80");
        assertEquals(exportLines[25], "\"\",\"Team 1.2\",20");
        assertEquals(exportLines[26], "");
        assertEquals(exportLines[27], "");
        assertEquals(exportLines[28], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback");
        assertEquals(exportLines[29], "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"\",\"Team 1.1\",\"Team 1.1\",\"-\",80");
        assertEquals(exportLines[30], "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"\",\"Team 1.2\",\"Team 1.2\",\"-\",20");
        assertEquals(exportLines[31], "");
        assertEquals(exportLines[32], "");
        assertEquals(exportLines[33], "Question 3,\"How much has each student worked?\"");
        assertEquals(exportLines[34], "");
        assertEquals(exportLines[35], "Summary Statistics,");
        assertEquals(exportLines[36], "Team, Recipient, Average Points");
        assertEquals(exportLines[37], "\"Team 1.2\",\"student5 In Course1\",10");
        assertEquals(exportLines[38], "\"Team 1.1\",\"student4 In Course1\",10");
        assertEquals(exportLines[39], "\"Team 1.1\",\"student2 In Course1\",20");
        assertEquals(exportLines[40], "\"Team 1.1\",\"student1 In Course1\",30");
        assertEquals(exportLines[41], "\"Team 1.1\",\"student3 In Course1\",30");
        assertEquals(exportLines[42], "");
        assertEquals(exportLines[43], "");
        assertEquals(exportLines[44], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback");
        assertEquals(exportLines[45], "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",30");
        assertEquals(exportLines[46], "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",20");
        assertEquals(exportLines[47], "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",30");
        assertEquals(exportLines[48], "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",10");
        assertEquals(exportLines[49], "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",10");
        
        ______TS("Instructor without privilege to view responses");
        
        instructor = newDataBundle.instructors.get("instructor2OfCourse1");
        
        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);
        
        exportLines = export.split(Const.EOL);
        System.out.println(export);
        assertEquals(22, exportLines.length);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        
        ______TS("CONTRIB results");
        
        session = newDataBundle.feedbackSessions.get("contribSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");
        
        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);
        
        System.out.println(export);
        
        /*This is how the export should look like
        =======================================
        Course,"FSQTT.idOfTypicalCourse1"
        Session Name,"CONTRIB Session"
        
        
        Question 1,"How much has each team member including yourself, contributed to the project?"
        
        Summary Statistics,
        Team, Name, Email, CC, PC, Ratings Received
        "Team 1.1","student1 In Course1","student1InCourse1@gmail.tmt","95","N/A","N/A, N/A, N/A"
        "Team 1.1","student2 In Course1","student2InCourse1@gmail.tmt","Not Submitted","75","75, N/A, N/A"
        "Team 1.1","student3 In Course1","student3InCourse1@gmail.tmt","Not Submitted","103","103, N/A, N/A"
        "Team 1.1","student4 In Course1","student4InCourse1@gmail.tmt","Not Submitted","122","122, N/A, N/A"
        
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Team 1.1","student1 In Course1,"Course1","student1InCourse1@gmail.tmt","Team 1.1","student1 In Course1,"Course1","student1InCourse1@gmail.tmt","Equal share"
        "Team 1.1","student1 In Course1,"Course1","student1InCourse1@gmail.tmt","Team 1.1","student2 In Course1,"Course1","student1InCourse2@gmail.tmt","Equal share - 20%"
        "Team 1.1","student1 In Course1,"Course1","student1InCourse1@gmail.tmt","Team 1.1","student3 In Course1,"Course1","student1InCourse3@gmail.tmt","Equal share + 10%"
        "Team 1.1","student1 In Course1,"Course1","student1InCourse1@gmail.tmt","Team 1.1","student4 In Course1,"Course1","student1InCourse4@gmail.tmt","Equal share + 30%"
        */
        
        exportLines = export.split(Const.EOL);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        assertEquals(exportLines[2], "");
        assertEquals(exportLines[3], "");
        assertEquals(exportLines[4], "Question 1,\"How much has each team member including yourself, contributed to the project?\"");
        assertEquals(exportLines[5], "");
        assertEquals(exportLines[6], "Summary Statistics,");
        assertEquals(exportLines[7], "In the points given below, an equal share is equal to 100 points. e.g. 80 means \"Equal share - 20%\" and 110 means \"Equal share + 10%\".");
        assertEquals(exportLines[8], "Claimed Contribution (CC) = the contribution claimed by the student.");
        assertEquals(exportLines[9], "Perceived Contribution (PC) = the average value of student's contribution as perceived by the team members.");
        assertEquals(exportLines[10], "Team, Name, Email, CC, PC, Ratings Recieved");
        assertEquals(exportLines[11], "\"Team 1.1\",\"student1 In Course1\",\"student1InCourse1@gmail.tmt\",\"95\",\"N/A\",\"N/A, N/A, N/A\"");
        assertEquals(exportLines[12], "\"Team 1.1\",\"student2 In Course1\",\"student2InCourse1@gmail.tmt\",\"Not Submitted\",\"75\",\"75, N/A, N/A\"");
        assertEquals(exportLines[13], "\"Team 1.1\",\"student3 In Course1\",\"student3InCourse1@gmail.tmt\",\"Not Submitted\",\"103\",\"103, N/A, N/A\"");
        assertEquals(exportLines[14], "\"Team 1.1\",\"student4 In Course1\",\"student4InCourse1@gmail.tmt\",\"Not Submitted\",\"122\",\"122, N/A, N/A\"");
        assertEquals(exportLines[15], "\"Team 1.2\",\"student5 In Course1\",\"student5InCourse1@gmail.tmt\",\"Not Submitted\",\"N/A\",\"N/A\"");
        assertEquals(exportLines[16], "");
        assertEquals(exportLines[17], "");
        assertEquals(exportLines[18], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback");
        assertEquals(exportLines[19], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Equal share - 5%\"");
        assertEquals(exportLines[20], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Equal share - 25%\"");
        assertEquals(exportLines[21], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Equal share + 3%\"");
        assertEquals(exportLines[22], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Equal share + 22%\"");
        assertEquals(exportLines[23], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[24], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[25], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[26], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[27], "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[28], "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[29], "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[30], "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[31], "\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[32], "\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[33], "\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[34], "\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[35], "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"");
        
        ______TS("CONTRIB summary visibility variations");
        
        // instructor not allowed to see student
        session = newDataBundle.feedbackSessions.get("contribSessionStudentAnonymised");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");
        
        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);
        
        System.out.println(export);
        
        /*This is how the export should look like
        =======================================
        Course,"FSQTT.idOfTypicalCourse1"
        Session Name,"CONTRIB Session Student Anonymised"
        
        
        Question 1,"How much has each team member including yourself, contributed to the project?"
        
        Summary Statistics,
        In the points given below, an equal share is equal to 100 points. e.g. 80 means "Equal share - 20%" and 110 means "Equal share + 10%".
        Claimed Contribution (CC) = the contribution claimed by the student.
        Perceived Contribution (PC) = the average value of student's contribution as perceived by the team members.
        Team, Name, Email, CC, PC, Ratings Recieved
        "Anonymous student 283462789's Team","Anonymous student 283462789","-","100","N/A","N/A, N/A, N/A"
        "Anonymous student 412545508's Team","Anonymous student 412545508","-","Not Submitted","N/A","N/A, N/A, N/A"
        "Anonymous student 541628227's Team","Anonymous student 541628227","-","Not Submitted","N/A","N/A, N/A, N/A"
        "Anonymous student 670710946's Team","Anonymous student 670710946","-","Not Submitted","N/A","N/A, N/A, N/A"
        "Anonymous student 799793665's Team","Anonymous student 799793665","-","Not Submitted","N/A","N/A"
        
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Anonymous student 283462789&#39;s Team","Anonymous student 283462789","Unknown user","-",""
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","No Response"
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","No Response"
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","No Response"
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","No Response"
        
        */
        
        exportLines = export.split(Const.EOL);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        assertEquals(exportLines[2], "");
        assertEquals(exportLines[3], "");
        assertEquals(exportLines[4], "Question 1,\"How much has each team member including yourself, contributed to the project?\"");
        assertEquals(exportLines[5], "");
        assertEquals(exportLines[6], "Summary Statistics,");
        assertEquals(exportLines[7], "In the points given below, an equal share is equal to 100 points. e.g. 80 means \"Equal share - 20%\" and 110 means \"Equal share + 10%\".");
        assertEquals(exportLines[8], "Claimed Contribution (CC) = the contribution claimed by the student.");
        assertEquals(exportLines[9], "Perceived Contribution (PC) = the average value of student's contribution as perceived by the team members.");
        assertEquals(exportLines[10], "Team, Name, Email, CC, PC, Ratings Recieved");
        assertEquals(exportLines[11], "\"Anonymous student 283462789's Team\",\"Anonymous student 283462789\",\"-\",\"100\",\"N/A\",\"N/A, N/A, N/A\"");
        assertEquals(exportLines[12], "\"Anonymous student 412545508's Team\",\"Anonymous student 412545508\",\"-\",\"Not Submitted\",\"N/A\",\"N/A, N/A, N/A\"");
        assertEquals(exportLines[13], "\"Anonymous student 541628227's Team\",\"Anonymous student 541628227\",\"-\",\"Not Submitted\",\"N/A\",\"N/A, N/A, N/A\"");
        assertEquals(exportLines[14], "\"Anonymous student 670710946's Team\",\"Anonymous student 670710946\",\"-\",\"Not Submitted\",\"N/A\",\"N/A, N/A, N/A\"");
        assertEquals(exportLines[15], "\"Anonymous student 799793665's Team\",\"Anonymous student 799793665\",\"-\",\"Not Submitted\",\"N/A\",\"N/A\"");
        assertEquals(exportLines[16], "");
        assertEquals(exportLines[17], "");
        assertEquals(exportLines[18], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback");
        assertEquals(exportLines[19], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Anonymous student 283462789&#39;s Team\",\"Anonymous student 283462789\",\"Unknown user\",\"-\",\"\"");
        assertEquals(exportLines[20], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[21], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[22], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[23], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"");

        // instructor not allowed to view student responses in section
        session = newDataBundle.feedbackSessions.get("contribSessionInstructorSectionRestricted");
        instructor = newDataBundle.instructors.get("instructor1OfCourseWithSections");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);
        
        System.out.println(export);
        
        /*This is how the export should look like
        =======================================
        Course,"FSQTT.idOfCourseWithSections"
        Session Name,"CONTRIB Session Section Restricted"
        
        
        Question 1,"How much has each team member including yourself, contributed to the project?"
        
        Summary Statistics,
        In the points given below, an equal share is equal to 100 points. e.g. 80 means "Equal share - 20%" and 110 means "Equal share + 10%".
        Claimed Contribution (CC) = the contribution claimed by the student.
        Perceived Contribution (PC) = the average value of student's contribution as perceived by the team members.
        Team, Name, Email, CC, PC, Ratings Recieved
        "Team 2","student3 In Course With Sections","student3InCourseWithSections@gmail.tmt","100","N/A","N/A"
        "Team 3","student4 In Course With Sections","student4InCourseWithSections@gmail.tmt","Not Submitted","N/A","N/A"
        
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Team 2","student3 In Course With Sections","Sections","student3InCourseWithSections@gmail.tmt","Team 2","student3 In Course With Sections","Sections","student3InCourseWithSections@gmail.tmt","Equal share"
        "Team 1","student1 In Course With Sections","Sections","student1InCourseWithSections@gmail.tmt","Team 1","student1 In Course With Sections","Sections","student1InCourseWithSections@gmail.tmt","No Response"
        "Team 1","student1 In Course With Sections","Sections","student1InCourseWithSections@gmail.tmt","Team 1","student2 In Course With Sections","Sections","student2InCourseWithSections@gmail.tmt","No Response"
        "Team 1","student2 In Course With Sections","Sections","student2InCourseWithSections@gmail.tmt","Team 1","student1 In Course With Sections","Sections","student1InCourseWithSections@gmail.tmt","No Response"
        "Team 1","student2 In Course With Sections","Sections","student2InCourseWithSections@gmail.tmt","Team 1","student2 In Course With Sections","Sections","student2InCourseWithSections@gmail.tmt","No Response"
        "Team 3","student4 In Course With Sections","Sections","student4InCourseWithSections@gmail.tmt","Team 3","student4 In Course With Sections","Sections","student4InCourseWithSections@gmail.tmt","No Response"

        */
        
        exportLines = export.split(Const.EOL);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        assertEquals(exportLines[2], "");
        assertEquals(exportLines[3], "");
        assertEquals(exportLines[4], "Question 1,\"How much has each team member including yourself, contributed to the project?\"");
        assertEquals(exportLines[5], "");
        assertEquals(exportLines[6], "Summary Statistics,");
        assertEquals(exportLines[7], "In the points given below, an equal share is equal to 100 points. e.g. 80 means \"Equal share - 20%\" and 110 means \"Equal share + 10%\".");
        assertEquals(exportLines[8], "Claimed Contribution (CC) = the contribution claimed by the student.");
        assertEquals(exportLines[9], "Perceived Contribution (PC) = the average value of student's contribution as perceived by the team members.");
        assertEquals(exportLines[10], "Team, Name, Email, CC, PC, Ratings Recieved");
        assertEquals(exportLines[11], "\"Team 2\",\"student3 In Course With Sections\",\"student3InCourseWithSections@gmail.tmt\",\"100\",\"N/A\",\"N/A\"");
        assertEquals(exportLines[12], "\"Team 3\",\"student4 In Course With Sections\",\"student4InCourseWithSections@gmail.tmt\",\"Not Submitted\",\"N/A\",\"N/A\"");
        assertEquals(exportLines[13], "");
        assertEquals(exportLines[14], "");
        assertEquals(exportLines[15], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback");
        assertEquals(exportLines[16], "\"Team 2\",\"student3 In Course With Sections\",\"Sections\",\"student3InCourseWithSections@gmail.tmt\",\"Team 2\",\"student3 In Course With Sections\",\"Sections\",\"student3InCourseWithSections@gmail.tmt\",\"Equal share\"");
        assertEquals(exportLines[17], "\"Team 1\",\"student1 In Course With Sections\",\"Sections\",\"student1InCourseWithSections@gmail.tmt\",\"Team 1\",\"student1 In Course With Sections\",\"Sections\",\"student1InCourseWithSections@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[18], "\"Team 1\",\"student1 In Course With Sections\",\"Sections\",\"student1InCourseWithSections@gmail.tmt\",\"Team 1\",\"student2 In Course With Sections\",\"Sections\",\"student2InCourseWithSections@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[19], "\"Team 1\",\"student2 In Course With Sections\",\"Sections\",\"student2InCourseWithSections@gmail.tmt\",\"Team 1\",\"student1 In Course With Sections\",\"Sections\",\"student1InCourseWithSections@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[20], "\"Team 1\",\"student2 In Course With Sections\",\"Sections\",\"student2InCourseWithSections@gmail.tmt\",\"Team 1\",\"student2 In Course With Sections\",\"Sections\",\"student2InCourseWithSections@gmail.tmt\",\"No Response\"");
        assertEquals(exportLines[21], "\"Team 3\",\"student4 In Course With Sections\",\"Sections\",\"student4InCourseWithSections@gmail.tmt\",\"Team 3\",\"student4 In Course With Sections\",\"Sections\",\"student4InCourseWithSections@gmail.tmt\",\"No Response\"");
        
        ______TS("RUBRIC results");
        
        session = newDataBundle.feedbackSessions.get("rubricSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");
        
        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);
        
        System.out.println(export);
        
        /*This is how the export should look like
        =======================================
        Course,"FSQTT.idOfTypicalCourse1"
        Session Name,"RUBRIC Session"
        
        
        Question 1,"Please choose the best choice for the following sub-questions."
        
        Summary Statistics,
        ,"Yes","No"
        "a) This student has done a good job.",67% (2),33% (1)
        "b) This student has tried his/her best.",75% (3),25% (1)
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Sub Question,Choice
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","a","Yes (Choice 1)"
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","b","No (Choice 2)"
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","a","No (Choice 2)"
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","b","Yes (Choice 1)"
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","a","Yes (Choice 1)"
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","b","Yes (Choice 1)"
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","a","No Response"
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","b","Yes (Choice 1)"
        "Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","All Sub-Questions","No Response"
        "Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","All Sub-Questions","No Response"
        "Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","All Sub-Questions","No Response"
        "Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","All Sub-Questions","No Response"
        "Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","All Sub-Questions","No Response"
        "Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","All Sub-Questions","No Response"
        "Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","All Sub-Questions","No Response"
        "Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","All Sub-Questions","No Response"
        "Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","All Sub-Questions","No Response"
        "Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","All Sub-Questions","No Response"
        "Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","Team 1.1","student3 In Course1","Course1","student3InCourse1@gmail.tmt","All Sub-Questions","No Response"
        "Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","Team 1.1","student4 In Course1","Course1","student4InCourse1@gmail.tmt","All Sub-Questions","No Response"
        "Team 1.2","student5 In Course1","Course1","student5InCourse1@gmail.tmt","Team 1.2","student5 In Course1","Course1","student5InCourse1@gmail.tmt","All Sub-Questions","No Response"

        */
        
        exportLines = export.split(Const.EOL);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        assertEquals(exportLines[2], "");
        assertEquals(exportLines[3], "");
        assertEquals(exportLines[4], "Question 1,\"Please choose the best choice for the following sub-questions.\"");
        assertEquals(exportLines[5], "");
        assertEquals(exportLines[6], "Summary Statistics,");
        assertEquals(exportLines[7], ",\"Yes\",\"No\"");
        assertEquals(exportLines[8], "\"a) This student has done a good job.\",67% (2),33% (1)");
        assertEquals(exportLines[9], "\"b) This student has tried his/her best.\",75% (3),25% (1)");
        assertEquals(exportLines[10], "");
        assertEquals(exportLines[11], "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Sub Question,Choice Value,Choice Number");
        assertEquals(exportLines[12], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"a\",\"Yes\",\"1\"");
        assertEquals(exportLines[13], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"b\",\"No\",\"2\"");
        assertEquals(exportLines[14], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"a\",\"No\",\"2\"");
        assertEquals(exportLines[15], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"b\",\"Yes\",\"1\"");
        assertEquals(exportLines[16], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"a\",\"Yes\",\"1\"");
        assertEquals(exportLines[17], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"b\",\"Yes\",\"1\"");
        assertEquals(exportLines[18], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"a\",\"No Response\",\"\"");
        assertEquals(exportLines[19], "\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"b\",\"Yes\",\"1\"");
        assertEquals(exportLines[20], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"");
        assertEquals(exportLines[21], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"");
        assertEquals(exportLines[22], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"");
        assertEquals(exportLines[23], "\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"");
        assertEquals(exportLines[24], "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"");
        assertEquals(exportLines[25], "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"");
        assertEquals(exportLines[26], "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"");
        assertEquals(exportLines[27], "\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"");
        assertEquals(exportLines[28], "\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"");
        assertEquals(exportLines[29], "\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"");
        assertEquals(exportLines[30], "\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"");
        assertEquals(exportLines[31], "\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"");
        assertEquals(exportLines[32], "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"");
        
        
        ______TS("Non-existent Course/Session");
        
        try {
            fsLogic.getFeedbackSessionResultsSummaryAsCsv("non.existent", "no course", instructor.email);
            signalFailureToDetectException("Failed to detect non-existent feedback session.");
        } catch (EntityDoesNotExistException e) {
            assertEquals(e.getMessage(), "Trying to view non-existent feedback session.");
        }
    }

    public void testIsFeedbackSessionViewableToStudents() throws EntityDoesNotExistException {
        ______TS("Session with questions for students to answer");
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        assertTrue(fsLogic.isFeedbackSessionViewableToStudents(session));
        
        ______TS("Session without questions for students, but with visible responses");
        session = dataBundle.feedbackSessions.get("archiveCourse.session1");
        assertTrue(fsLogic.isFeedbackSessionViewableToStudents(session));
       
        session = dataBundle.feedbackSessions.get("session2InCourse2");
        assertTrue(fsLogic.isFeedbackSessionViewableToStudents(session));
       
        ______TS("private session");
        session = dataBundle.feedbackSessions.get("session1InCourse2");
        assertFalse(fsLogic.isFeedbackSessionViewableToStudents(session));
        
        ______TS("empty session");
        session = dataBundle.feedbackSessions.get("empty.session");
        assertFalse(fsLogic.isFeedbackSessionViewableToStudents(session));
    }
    
    public void testUpdateFeedbackSession() throws Exception {
        
        
        FeedbackSessionAttributes fsa = null;
        
        ______TS("failure 1: null object");
        try {
            fsLogic.updateFeedbackSession(fsa);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            AssertHelper.assertContains(Const.StatusCodes.NULL_PARAMETER, ae.getMessage());
        }
        
        ______TS("failure 2: non-existent session name");
        fsa = new FeedbackSessionAttributes();
        fsa.feedbackSessionName = "asdf_randomName1423";
        fsa.courseId = "idOfTypicalCourse1";
        
        try {
            fsLogic.updateFeedbackSession(fsa);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to update a feedback session that does not exist.", edne.getMessage());
        }
        
        ______TS("success 1: all changeable values sent are null");
        fsa = dataBundle.feedbackSessions.get("session1InCourse1");
        fsa.instructions = null;
        fsa.startTime = null;
        fsa.endTime = null;
        fsa.feedbackSessionType = null;
        fsa.sessionVisibleFromTime = null;
        fsa.resultsVisibleFromTime = null;
        
        fsLogic.updateFeedbackSession(fsa);
        
        assertEquals(fsa.toString(), fsLogic.getFeedbackSession(fsa.feedbackSessionName, fsa.courseId).toString());
    }
    
    public void testPublishUnpublishFeedbackSession() throws Exception {

        ______TS("success: publish");
        FeedbackSessionAttributes
            sessionUnderTest = dataBundle.feedbackSessions.get("session1InCourse1");
        
        // set as manual publish
        
        sessionUnderTest.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
        fsLogic.updateFeedbackSession(sessionUnderTest);
        
        fsLogic.publishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);

        HashMap<String, String> paramMap = createParamMapForAction(sessionUnderTest);
        EmailAction fsPublishedAction = new FeedbackSessionPublishedMailAction(paramMap);
        fsPublishedAction.getPreparedEmailsAndPerformSuccessOperations();
        
        sessionUnderTest.sentPublishedEmail = true;

        // Set real time of publishing
        FeedbackSessionAttributes sessionPublished = fsLogic.getFeedbackSession(sessionUnderTest.feedbackSessionName,sessionUnderTest.courseId);
        sessionUnderTest.resultsVisibleFromTime = sessionPublished.resultsVisibleFromTime;
        
        assertEquals(sessionUnderTest.toString(), sessionPublished.toString());

        ______TS("failure: already published");
        
        try{
            fsLogic.publishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
            signalFailureToDetectException(
                    "Did not catch exception signalling that session is already published.");
        } catch (InvalidParametersException e) {
            assertEquals("Session is already published.", e.getMessage());
        }
        
        ______TS("success: unpublish");
        
        fsLogic.unpublishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
        
        sessionUnderTest.sentPublishedEmail = false;
        sessionUnderTest.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
        
        assertEquals(
                sessionUnderTest.toString(),
                fsLogic.getFeedbackSession(
                        sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId).toString());
        
        ______TS("failure: not published");
        
        try{
            fsLogic.unpublishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
            signalFailureToDetectException(
                    "Did not catch exception signalling that session is not published.");
        } catch (InvalidParametersException e) {
            assertEquals("Session is already unpublished.", e.getMessage());
        }
        
        ______TS("failure: private session");
        
        sessionUnderTest = dataBundle.feedbackSessions.get("session1InCourse2");

        try{
            fsLogic.publishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
            signalFailureToDetectException(
                    "Did not catch exception signalling that private session can't " +
                    "be published.");
        } catch (InvalidParametersException e) {
            assertEquals("Private session can't be published.", e.getMessage());
        }
        
        try{
            fsLogic.unpublishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
            signalFailureToDetectException(
                    "Did not catch exception signalling that private session should " +
                    "not be published");
        } catch (InvalidParametersException e) {
            assertEquals("Private session can't be unpublished.", e.getMessage());
        }
                
        ______TS("failure: session does not exist");

        sessionUnderTest.feedbackSessionName = "non-existant session";
        
        try{
            fsLogic.publishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
            signalFailureToDetectException(
                    "Did not catch exception signalling that session does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to publish a non-existant session.", e.getMessage());
        }
        
        try{
            fsLogic.unpublishFeedbackSession(
                    sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
            signalFailureToDetectException(
                    "Did not catch exception signalling that session does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to unpublish a non-existant session.", e.getMessage());
        }
    }
    
    public void testIsFeedbackSessionCompletedByInstructor() throws Exception {
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor2OfCourse1");
        
        ______TS("failure: non-existent feedback session for instructor");
        
        try {
            fsLogic.isFeedbackSessionCompletedByInstructor(fs.courseId, "nonExistentFSName","random.instructor@email");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to check a feedback session that does not exist.",
                         edne.getMessage());
        }
        
        ______TS("success: empty session");
        
        fs = dataBundle.feedbackSessions.get("empty.session");
        
        assertTrue(fsLogic.isFeedbackSessionCompletedByInstructor(fs.feedbackSessionName, fs.courseId, instructor.email));
        
    }
    
    public void testIsFeedbackSessionCompletedByStudent() throws Exception {
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes student = dataBundle.students.get("student2InCourse1");
        
        ______TS("success: empty session");
        
        fs = dataBundle.feedbackSessions.get("empty.session");
        
        assertTrue(fsLogic.isFeedbackSessionCompletedByStudent(fs, student.email));
    }
    
    public void testIsFeedbackSessionFullyCompletedByStudent() throws Exception {
        
        
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes student1OfCourse1 = dataBundle.students.get("student1InCourse1");
        StudentAttributes student3OfCourse1 = dataBundle.students.get("student3InCourse1");
        
        ______TS("failure: non-existent feedback session for student");
        
        try {
            fsLogic.isFeedbackSessionFullyCompletedByStudent(fs.courseId, "nonExistentFSName","random.student@email");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to check a feedback session that does not exist.",
                         edne.getMessage());
        }
        
        ______TS("success case: fully done by student 1");
        assertTrue(fsLogic.isFeedbackSessionFullyCompletedByStudent(fs.feedbackSessionName, fs.courseId, student1OfCourse1.email));
        
        ______TS("success case: partially done by student 3");
        assertFalse(fsLogic.isFeedbackSessionFullyCompletedByStudent(fs.feedbackSessionName, fs.courseId, student3OfCourse1.email));
    }
    
    public void testScheduleFeedbackSessionOpeningEmails() {
        // this method is tested in FeedbackSessionEmailTaskQueueTest.java
    }
    
    public void testScheduleFeedbackSessionClosingEmails() {
        // this method is tested in FeedbackSessionEmailTaskQueueTest.java
    }
    
    public void testScheduleFeedbackSessionPublishedEmails() {
        // this method is tested in FeedbackSessionEmailTaskQueueTest.java
    }
    
    public void testSendReminderForFeedbackSession() throws Exception {
        // private method. no need to check for authentication.
        Logic logic = new Logic();
        
        ______TS("typical success case");
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");

        List<MimeMessage> emailsSent = 
                fsLogic.sendReminderForFeedbackSession(fs.courseId, fs.feedbackSessionName);
        assertEquals(11, emailsSent.size());

        fs = fsLogic.getFeedbackSession(fs.feedbackSessionName, fs.courseId);

        List<StudentAttributes> studentList = logic.getStudentsForCourse(fs.courseId);
        for (StudentAttributes s : studentList) {
            MimeMessage emailToStudent = TestHelper.getEmailToStudent(s, emailsSent);
            if (fsLogic.isFeedbackSessionCompletedByStudent(fs, s.email)) {
                String errorMessage = "Email sent to " + s.email + " when he already completed the session.";
                assertNull(errorMessage, emailToStudent);
            } else {
                String errorMessage = "No email sent to " + s.email + " when he hasn't completed the session.";
                assertNotNull(errorMessage, emailToStudent);
                AssertHelper.assertContains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER,
                        emailToStudent.getSubject());
                AssertHelper.assertContains(fs.feedbackSessionName, emailToStudent.getSubject());
            }
        }
        
        List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(fs.courseId);
        String notificationHeader = "The email below has been sent to students of course: " + fs.courseId;
        for (InstructorAttributes i : instructorList) {
            List<MimeMessage> emailsToInstructor = TestHelper.getEmailsToInstructor(i, emailsSent);
            
            if(fsLogic.isFeedbackSessionCompletedByInstructor(fs.feedbackSessionName, fs.courseId, i.email)) {
                // Only send notification (no reminder) if instructor already completed the session
                assertEquals(1, emailsToInstructor.size());
                AssertHelper.assertContains(notificationHeader, emailsToInstructor.get(0).getContent().toString());
                AssertHelper.assertContains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER,
                        emailsToInstructor.get(0).getSubject());
                AssertHelper.assertContains(fs.feedbackSessionName, emailsToInstructor.get(0).getSubject());
            } else {
                // Send both notification and reminder if the instructor hasn't completed the session
                assertEquals(2, emailsToInstructor.size());
                
                assertTrue(emailsToInstructor.get(0).getContent().toString().contains(notificationHeader) 
                            || emailsToInstructor.get(1).getContent().toString().contains(notificationHeader));
                assertTrue(!emailsToInstructor.get(0).getContent().toString().contains(notificationHeader) 
                            || !emailsToInstructor.get(1).getContent().toString().contains(notificationHeader));
                AssertHelper.assertContains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER,
                        emailsToInstructor.get(0).getSubject());
                AssertHelper.assertContains(fs.feedbackSessionName, emailsToInstructor.get(0).getSubject());
                AssertHelper.assertContains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER,
                        emailsToInstructor.get(1).getSubject());
                AssertHelper.assertContains(fs.feedbackSessionName, emailsToInstructor.get(1).getSubject());
            }
            
            
        }
        
        ______TS("failure: non-existent Feedback session");
        
        String nonExistentFSName = "non-ExIsTENT FsnaMe123";
        
        try {
            fsLogic.sendReminderForFeedbackSession(fs.courseId, nonExistentFSName);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to remind non-existent feedback session " 
                            + fs.courseId + "/" + nonExistentFSName,
                         edne.getMessage());
        }
        
    }
    
    public void testSendReminderForFeedbackSessionParticularUsers() throws Exception {
        // private method. no need to check for authentication.
        Logic logic = new Logic();
        
        ______TS("typical success case");
        
        StudentAttributes studentToRemind = dataBundle.students.get("student5InCourse1");
        InstructorAttributes instrToRemind = dataBundle.instructors.get("helperOfCourse1");
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] usersToRemind = new String[] {studentToRemind.email, instrToRemind.email};

        List<MimeMessage> emailsSent =
                fsLogic.sendReminderForFeedbackSessionParticularUsers(
                        fs.courseId, fs.feedbackSessionName, usersToRemind);
        assertEquals(7, emailsSent.size());

        MimeMessage emailToStudent = TestHelper.getEmailToStudent(studentToRemind, emailsSent);
        String errorMessage = "No email sent to selected student " + studentToRemind.email;
        assertNotNull(errorMessage, emailToStudent);
        AssertHelper.assertContains(
                Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER,
                emailToStudent.getSubject());
        AssertHelper.assertContains(fs.feedbackSessionName, emailToStudent.getSubject());

        List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(fs.courseId);
        String notificationHeader = "The email below has been sent to students of course: " + fs.courseId;
        for (InstructorAttributes i : instructorList) {
            List<MimeMessage> emailsToInstructor = TestHelper.getEmailsToInstructor(i, emailsSent);
            
            if(!i.email.equals(instrToRemind.email)) {
                // Only send notification (no reminder) if instructor is not selected
                assertEquals(1, emailsToInstructor.size());
                AssertHelper.assertContains(notificationHeader, emailsToInstructor.get(0).getContent().toString());
                AssertHelper.assertContains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER,
                        emailsToInstructor.get(0).getSubject());
                AssertHelper.assertContains(fs.feedbackSessionName, emailsToInstructor.get(0).getSubject());
            } else {
                // Send both notification and reminder if the instructor is selected
                assertEquals(2, emailsToInstructor.size());
                
                assertTrue(emailsToInstructor.get(0).getContent().toString().contains(notificationHeader) 
                            || emailsToInstructor.get(1).getContent().toString().contains(notificationHeader));
                assertTrue(!emailsToInstructor.get(0).getContent().toString().contains(notificationHeader) 
                            || !emailsToInstructor.get(1).getContent().toString().contains(notificationHeader));
                AssertHelper.assertContains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER,
                        emailsToInstructor.get(0).getSubject());
                AssertHelper.assertContains(fs.feedbackSessionName, emailsToInstructor.get(0).getSubject());
                AssertHelper.assertContains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER,
                        emailsToInstructor.get(1).getSubject());
                AssertHelper.assertContains(fs.feedbackSessionName, emailsToInstructor.get(1).getSubject());
            }
        }
        
        ______TS("failure: non-existent Feedback session");
        
        String nonExistentFSName = "non-ExIsTENT FsnaMe123";
        
        try {
            fsLogic.sendReminderForFeedbackSessionParticularUsers(
                    fs.courseId, nonExistentFSName, usersToRemind);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to remind non-existent feedback session " 
                            + fs.courseId + "/" + nonExistentFSName,
                         edne.getMessage());
        }
        
    }
    
    private FeedbackSessionAttributes getNewFeedbackSession() {
        FeedbackSessionAttributes fsa = new FeedbackSessionAttributes();
        fsa.feedbackSessionType = FeedbackSessionType.STANDARD;
        fsa.feedbackSessionName = "fsTest1";
        fsa.courseId = "testCourse";
        fsa.creatorEmail = "valid@email.tmt";
        fsa.createdTime = new Date();
        fsa.startTime = new Date();
        fsa.endTime = new Date();
        fsa.sessionVisibleFromTime = new Date();
        fsa.resultsVisibleFromTime = new Date();
        fsa.gracePeriod = 5;
        fsa.sentOpenEmail = true;
        fsa.instructions = new Text("Give feedback.");
        return fsa;
    }
    
    private FeedbackQuestionAttributes getQuestionFromDatastore(String jsonId) {
        FeedbackQuestionAttributes questionToGet = dataBundle.feedbackQuestions.get(jsonId);
        questionToGet = fqLogic.getFeedbackQuestion(
                questionToGet.feedbackSessionName, 
                questionToGet.courseId,
                questionToGet.questionNumber);
        
        return questionToGet;
    }

    // Extract response id from datastore based on json key.
    private String getResponseId(String jsonId, DataBundle bundle) {
        return getResponseFromDatastore(jsonId, bundle).getId();
    }
    
    private FeedbackResponseAttributes getResponseFromDatastore(String jsonId, DataBundle bundle) {
        FeedbackResponseAttributes response = bundle.feedbackResponses.get(jsonId);
        
        String questionId = null;        
        try {
            int qnNumber = Integer.parseInt(response.feedbackQuestionId);        
            questionId = fqLogic.getFeedbackQuestion(
                        response.feedbackSessionName, response.courseId,
                        qnNumber).getId();
        } catch (NumberFormatException e) {
            questionId = response.feedbackQuestionId;
        }
        
        return frLogic.getFeedbackResponse(questionId, 
                response.giverEmail, response.recipientEmail);
    }
    
    private void unpublishAllSessions() throws InvalidParametersException, EntityDoesNotExistException {
        for (FeedbackSessionAttributes fs : dataBundle.feedbackSessions.values()) {
            if(fs.isPublished()) {
                fsLogic.unpublishFeedbackSession(fs.feedbackSessionName, fs.courseId);                
            }
        }
    }
    
    // Stringifies the visibility table for easy testing/comparison.
    private String tableToString(Map<String, boolean[]> table){
        String tableString = "";
        for(Map.Entry<String, boolean[]> entry : table.entrySet()) {
            tableString += "{";
            tableString += entry.getKey().toString();
            tableString += "={";
            tableString += String.valueOf(entry.getValue()[0]);
            tableString += ",";
            tableString += String.valueOf(entry.getValue()[1]);
            tableString += "}},";
        }
        if(!tableString.isEmpty()) {
            tableString = tableString.substring(0, tableString.length()-1);
        }
        return tableString;
    }
    
    public void testDeleteFeedbackSessionsForCourse() throws Exception {
        
        assertFalse(fsLogic.getFeedbackSessionsForCourse("idOfTypicalCourse1").isEmpty());
        fsLogic.deleteFeedbackSessionsForCourseCascade("idOfTypicalCourse1");
        assertTrue(fsLogic.getFeedbackSessionsForCourse("idOfTypicalCourse1").isEmpty());
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(FeedbackSessionsLogic.class);
    }

    private HashMap<String, String> createParamMapForAction(FeedbackSessionAttributes fs) {
        // Prepare parameter map to be used with FeedbackSessionPublishedMailAction
        HashMap<String, String> paramMap = new HashMap<String, String>();

        paramMap.put(ParamsNames.EMAIL_TYPE, EmailType.FEEDBACK_PUBLISHED.toString());
        paramMap.put(ParamsNames.EMAIL_FEEDBACK, fs.feedbackSessionName);
        paramMap.put(ParamsNames.EMAIL_COURSE, fs.courseId);

        return paramMap;
    }

}
