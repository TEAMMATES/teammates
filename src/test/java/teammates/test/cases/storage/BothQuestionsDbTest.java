package teammates.test.cases.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.FeedbackTextQuestionDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.storage.api.BothQuestionsDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.QuestionsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

public class BothQuestionsDbTest extends BaseComponentTestCase {
    private static final BothQuestionsDb fqDb = new BothQuestionsDb();
    private static final QuestionsDb newDb = new QuestionsDb();
    private static final FeedbackQuestionsDb oldDb = new FeedbackQuestionsDb();

    @BeforeClass
    public static void classSetUp() throws InvalidParametersException, EntityAlreadyExistsException {
        printTestClassHeader();
        new FeedbackSessionsLogic().createFeedbackSession(getFeedbackSessionAttributes());
    }
    
    @Test
    public void testTimestamp() throws InvalidParametersException, EntityAlreadyExistsException,
                                       EntityDoesNotExistException {
        
        ______TS("success : created");

        FeedbackQuestionAttributes fq = getNewFeedbackQuestionAttributes();
        
        // remove possibly conflicting entity from the database
        fqDb.deleteEntity(fq);
        
        fqDb.createEntity(fq);
        verifyPresentInDatastore(fq, true);
        
        String feedbackSessionName = fq.feedbackSessionName;
        String courseId = fq.courseId;
        int questionNumber = fq.questionNumber;
        
        FeedbackQuestionAttributes feedbackQuestion =
                fqDb.getFeedbackQuestion(feedbackSessionName, courseId, questionNumber);
     
        // Assert dates are now.
        AssertHelper.assertDateIsNow(feedbackQuestion.getCreatedAt());
        AssertHelper.assertDateIsNow(feedbackQuestion.getUpdatedAt());
        
        FeedbackQuestionAttributes questionInOldDb = oldDb.getFeedbackQuestion(
                                        feedbackSessionName, courseId, questionNumber);
        AssertHelper.assertDateIsNow(questionInOldDb.getCreatedAt());
        AssertHelper.assertDateIsNow(questionInOldDb.getUpdatedAt());
        FeedbackQuestionAttributes questionInNewDb = newDb.getFeedbackQuestion(
                                        feedbackSessionName, courseId, questionNumber);
        AssertHelper.assertDateIsNow(questionInNewDb.getCreatedAt());
        AssertHelper.assertDateIsNow(questionInNewDb.getUpdatedAt());
        
        ______TS("success : update lastUpdated");
        
        feedbackQuestion.questionNumber++;
        fqDb.updateFeedbackQuestion(feedbackQuestion);
        
        FeedbackQuestionAttributes updatedFq =
                fqDb.getFeedbackQuestion(feedbackSessionName, courseId, feedbackQuestion.questionNumber);
        
        // Assert lastUpdate has changed, and is now.
        assertFalse(feedbackQuestion.getUpdatedAt().equals(updatedFq.getUpdatedAt()));
        AssertHelper.assertDateIsNow(updatedFq.getUpdatedAt());
        
        questionInOldDb = oldDb.getFeedbackQuestion(feedbackSessionName, courseId, feedbackQuestion.questionNumber);
        assertFalse("lastUpdate should be changed",
                    feedbackQuestion.getUpdatedAt().equals(questionInOldDb.getUpdatedAt()));
        AssertHelper.assertDateIsNow(questionInOldDb.getUpdatedAt());
        questionInNewDb = newDb.getFeedbackQuestion(feedbackSessionName, courseId, feedbackQuestion.questionNumber);
        assertFalse("lastUpdate should be changed",
                    feedbackQuestion.getUpdatedAt().equals(questionInNewDb.getUpdatedAt()));
        AssertHelper.assertDateIsNow(questionInNewDb.getUpdatedAt());
        
        ______TS("success : keep lastUpdated");

        feedbackQuestion.questionNumber++;
        fqDb.updateFeedbackQuestion(feedbackQuestion, true);

        FeedbackQuestionAttributes updatedFqTwo =
                fqDb.getFeedbackQuestion(feedbackSessionName, courseId, feedbackQuestion.questionNumber);
        
        // Assert lastUpdate has NOT changed.
        assertEquals(updatedFq.getUpdatedAt(), updatedFqTwo.getUpdatedAt());
    }
      
    @Test
    public void testCreateDeleteFeedbackQuestion() throws InvalidParametersException, EntityAlreadyExistsException {

        ______TS("standard success case");

        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();
        
        // remove possibly conflicting entity from the database
        fqDb.deleteEntity(fqa);
        
        fqDb.createEntity(fqa);
        verifyPresentInDatastore(fqa, true);

        ______TS("duplicate - with same id.");

        try {
            fqDb.createEntity(fqa);
            
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(String.format(QuestionsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS,
                                                      fqa.getEntityTypeAsString()) + fqa.getIdentificationString(),
                                                      e.getMessage());
        }

        ______TS("delete - with id specified");

        fqDb.deleteEntity(fqa);
        verifyAbsentInDatastore(fqa);

        ______TS("null params");

        try {
            fqDb.createEntity(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("invalid params");

        try {
            fqa.creatorEmail = "haha";
            fqDb.createEntity(fqa);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("Invalid creator's email", e.getLocalizedMessage());
        }
    }

    @Test
    public void testGetFeedbackQuestions() throws Exception {
        FeedbackQuestionAttributes expected = getNewFeedbackQuestionAttributes();
        
        // remove possibly conflicting entity from the database
        fqDb.deleteEntity(expected);
        
        fqDb.createEntity(expected);

        ______TS("standard success case");

        FeedbackQuestionAttributes actual = fqDb.getFeedbackQuestion(expected.feedbackSessionName,
                                                                     expected.courseId,
                                                                     expected.questionNumber);

        assertEquals(expected.toString(), actual.toString());

        ______TS("non-existent question");

        assertNull(fqDb.getFeedbackQuestion("Non-existent feedback session", "non-existent-course", 1));

        ______TS("null fsName");

        try {
            fqDb.getFeedbackQuestion(null, expected.courseId, 1);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("null courseId");

        try {
            fqDb.getFeedbackQuestion(expected.feedbackSessionName, null, 1);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("get by id");

        actual = fqDb.getFeedbackQuestion(actual.getId());
        assertEquals(expected.toString(), actual.toString());

        ______TS("get non-existent question by id");

        actual = fqDb.getFeedbackQuestion("non-existent id");

        assertNull(actual);
  
        ______TS("standard success case for multiple questions");

        int numToCreate = 3;

        List<FeedbackQuestionAttributes> expectedQuestions = createFeedbackQuestions(numToCreate);

        List<FeedbackQuestionAttributes> questions =
                fqDb.getFeedbackQuestionsForSession(expectedQuestions.get(0).feedbackSessionName,
                                                    expectedQuestions.get(0).courseId);

        for (int i = 0; i < numToCreate; i++) {
            expectedQuestions.get(i).setId(questions.get(i).getId());
        }

        assertEquals(numToCreate, questions.size());
        AssertHelper.assertSameContentIgnoreOrder(expectedQuestions, questions);
        assertEquals("Both old question and new question type's db should have the same questions",
                     oldDb.getFeedbackQuestionsForSession(expectedQuestions.get(0).feedbackSessionName,
                                                          expectedQuestions.get(0).courseId),
                     newDb.getFeedbackQuestionsForSession(expectedQuestions.get(0).feedbackSessionName,
                                                          expectedQuestions.get(0).courseId));

        ______TS("null params");

        try {
            fqDb.getFeedbackQuestionsForSession(null, expectedQuestions.get(0).courseId);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            fqDb.getFeedbackQuestionsForSession(expectedQuestions.get(0).feedbackSessionName, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent session");

        assertTrue(fqDb.getFeedbackQuestionsForSession("non-existent session", expectedQuestions.get(0).courseId).isEmpty());

        ______TS("no questions in session");

        assertTrue(fqDb.getFeedbackQuestionsForSession("Empty session", expectedQuestions.get(0).courseId).isEmpty());

        deleteFeedbackQuestions(numToCreate);

        
        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();
        
        // remove all entities from the database to prevent conflicts
        fqDb.deleteFeedbackQuestionsForCourse(fqa.courseId);
        
        int[] numOfQuestions = createNewQuestionsForDifferentRecipientTypes();

        ______TS("standard success case");

        questions = fqDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, fqa.courseId,
                                                      FeedbackParticipantType.INSTRUCTORS);
        
        assertEquals(numOfQuestions[0], questions.size());
        assertEquals("Both old question and new question type's db should have the same questions",
                     oldDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, fqa.courseId,
                                                            FeedbackParticipantType.INSTRUCTORS),
                     newDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, fqa.courseId,
                                                            FeedbackParticipantType.INSTRUCTORS));

        questions = fqDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName,
                                                          fqa.courseId, FeedbackParticipantType.STUDENTS);
        assertEquals(numOfQuestions[1], questions.size());
        assertEquals("Both old question and new question type's db should have the same questions",
                     oldDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, fqa.courseId,
                                    FeedbackParticipantType.STUDENTS),
                     newDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, fqa.courseId,
                                    FeedbackParticipantType.STUDENTS));

        questions = fqDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName,
                                                          fqa.courseId, FeedbackParticipantType.SELF);
        assertEquals(numOfQuestions[2], questions.size());
        assertEquals("Both old question and new question type's db should have the same questions",
                     oldDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, fqa.courseId,
                                                            FeedbackParticipantType.SELF),
                     newDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, fqa.courseId,
                                                            FeedbackParticipantType.SELF));

        questions = fqDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName,
                                                          fqa.courseId, FeedbackParticipantType.TEAMS);
        assertEquals(numOfQuestions[3], questions.size());
        assertEquals("Both old question and new question type's db should have the same questions",
                     oldDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, fqa.courseId,
                                    FeedbackParticipantType.TEAMS),
                     newDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, fqa.courseId,
                                    FeedbackParticipantType.TEAMS));

        ______TS("null params");

        try {
            fqDb.getFeedbackQuestionsForGiverType(null, fqa.courseId, FeedbackParticipantType.STUDENTS);
            signalFailureToDetectException();
        } catch (AssertionError expectedAssertion) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT,
                                        expectedAssertion.getLocalizedMessage());
        }

        try {
            fqDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, null, FeedbackParticipantType.STUDENTS);
            signalFailureToDetectException();
        } catch (AssertionError expectedAssertion) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT,
                                        expectedAssertion.getLocalizedMessage());
        }

        try {
            fqDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, fqa.courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError expectedAssertion) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT,
                                        expectedAssertion.getLocalizedMessage());
        }

        ______TS("non-existent session");

        assertTrue(fqDb.getFeedbackQuestionsForGiverType("non-existent session", fqa.courseId,
                                                         FeedbackParticipantType.STUDENTS).isEmpty());

        ______TS("no questions in session");

        assertTrue(fqDb.getFeedbackQuestionsForGiverType("Empty session", fqa.courseId,
                                                         FeedbackParticipantType.STUDENTS).isEmpty());

        deleteFeedbackQuestions(numOfQuestions[0] + numOfQuestions[1] + numOfQuestions[2] + numOfQuestions[3]);
    }

    @Test
    public void testUpdateFeedbackQuestion() throws Exception {

        ______TS("null params");

        try {
            fqDb.updateFeedbackQuestion(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("invalid feedback question attributes");

        FeedbackQuestionAttributes invalidFqa = getNewFeedbackQuestionAttributes();
        fqDb.deleteEntity(invalidFqa);
        fqDb.createEntity(invalidFqa);
        invalidFqa.setId(fqDb.getFeedbackQuestion(invalidFqa.feedbackSessionName, invalidFqa.courseId,
                                                  invalidFqa.questionNumber).getId());
        invalidFqa.creatorEmail = "haha";

        try {
            fqDb.updateFeedbackQuestion(invalidFqa);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("Invalid creator's email", e.getLocalizedMessage());
        }

        ______TS("feedback session does not exist");

        FeedbackQuestionAttributes nonExistentFq = getNewFeedbackQuestionAttributes();
        nonExistentFq.setId("non-existent fq id");

        try {
            fqDb.updateFeedbackQuestion(nonExistentFq);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains(FeedbackQuestionsDb.ERROR_UPDATE_NON_EXISTENT, e.getLocalizedMessage());
        }

        ______TS("standard success case");

        FeedbackQuestionAttributes modifiedQuestion = getNewFeedbackQuestionAttributes();
        fqDb.deleteEntity(modifiedQuestion);
        fqDb.createEntity(modifiedQuestion);
        verifyPresentInDatastore(modifiedQuestion, true);

        modifiedQuestion = fqDb.getFeedbackQuestion(modifiedQuestion.feedbackSessionName,
                                                    modifiedQuestion.courseId,
                                                    modifiedQuestion.questionNumber);
        FeedbackQuestionDetails fqd = modifiedQuestion.getQuestionDetails();
        fqd.setQuestionText("New question text!");
        modifiedQuestion.setQuestionDetails(fqd);
        fqDb.updateFeedbackQuestion(modifiedQuestion);

        verifyPresentInDatastore(modifiedQuestion);
        modifiedQuestion = fqDb.getFeedbackQuestion(modifiedQuestion.feedbackSessionName,
                                                    modifiedQuestion.courseId,
                                                    modifiedQuestion.questionNumber);
        assertEquals("New question text!", modifiedQuestion.getQuestionDetails().getQuestionText());
        assertEquals(newDb.getFeedbackQuestion(modifiedQuestion.feedbackSessionName,
                                               modifiedQuestion.courseId, modifiedQuestion.questionNumber),
                     oldDb.getFeedbackQuestion(modifiedQuestion.feedbackSessionName,
                                               modifiedQuestion.courseId, modifiedQuestion.questionNumber));

        fqDb.deleteEntity(modifiedQuestion);
    }
    
    private static FeedbackSessionAttributes getFeedbackSessionAttributes() {
        FeedbackSessionAttributes fsa = new FeedbackSessionAttributes();
        
        fsa.setCourseId("testCourse");
        fsa.setCreatorEmail("instructor@email.com");
        fsa.setFeedbackSessionName("testFeedbackSession");
        
        fsa.setFeedbackSessionType(FeedbackSessionType.STANDARD);
        fsa.setCreatedTime(new Date());
        fsa.setStartTime(new Date());
        fsa.setEndTime(new Date());
        fsa.setSessionVisibleFromTime(new Date());
        fsa.setResultsVisibleFromTime(new Date());
        fsa.setGracePeriod(5);
        fsa.setSentOpenEmail(true);
        fsa.setSentPublishedEmail(true);
        fsa.setInstructions(new Text("Give feedback."));
        
        return fsa;
    }

    private FeedbackQuestionAttributes getNewFeedbackQuestionAttributes() {
        FeedbackQuestionAttributes fqa = new FeedbackQuestionAttributes();

        fqa.courseId = "testCourse";
        fqa.creatorEmail = "instructor@email.com";
        fqa.feedbackSessionName = "testFeedbackSession";
        fqa.giverType = FeedbackParticipantType.INSTRUCTORS;
        fqa.recipientType = FeedbackParticipantType.SELF;
        fqa.numberOfEntitiesToGiveFeedbackTo = 1;
        fqa.questionNumber = 1;

        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails("Question text.");
        fqa.questionType = FeedbackQuestionType.TEXT;
        fqa.setQuestionDetails(questionDetails);

        fqa.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        fqa.showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
        fqa.showResponsesTo = new ArrayList<FeedbackParticipantType>();
        return fqa;
    }

    private List<FeedbackQuestionAttributes> createFeedbackQuestions(int numToCreate) throws Exception {
        FeedbackQuestionAttributes fqa;
        List<FeedbackQuestionAttributes> returnVal = new ArrayList<FeedbackQuestionAttributes>();

        for (int i = 1; i <= numToCreate; i++) {
            fqa = getNewFeedbackQuestionAttributes();
            fqa.questionNumber = i;
            
            // remove possibly conflicting entity from the database
            FeedbackQuestionAttributes existingQuestion =
                    fqDb.getFeedbackQuestion(fqa.feedbackSessionName, fqa.courseId, fqa.questionNumber);
            if (existingQuestion != null) {
                // use the retrieved entity to delete as that has the actual questionId
                fqDb.deleteEntity(existingQuestion);
            }
            
            fqDb.createEntity(fqa);
            returnVal.add(fqa);
        }

        return returnVal;
    }

    private int[] createNewQuestionsForDifferentRecipientTypes() throws Exception {

        int[] numberOfQuestionsToCreate = new int[] {
                2,
                3,
                1,
                2
        };

        FeedbackQuestionAttributes fqa;

        for (int i = 1; i <= numberOfQuestionsToCreate[0]; i++) {
            fqa = getNewFeedbackQuestionAttributes();
            fqa.questionNumber = i;
            fqa.giverType = FeedbackParticipantType.INSTRUCTORS;
            // delete possibly conflicting question
            FeedbackQuestionAttributes existingQuestion =
                    fqDb.getFeedbackQuestion(fqa.feedbackSessionName, fqa.courseId, fqa.questionNumber);
            if (existingQuestion != null) {
                // use the retrieved entity to delete as that has the actual questionId
                fqDb.deleteEntity(existingQuestion);
            }
            fqDb.createEntity(fqa);
        }

        for (int i = 1; i <= numberOfQuestionsToCreate[1]; i++) {
            fqa = getNewFeedbackQuestionAttributes();
            fqa.questionNumber = numberOfQuestionsToCreate[0] + i;
            fqa.giverType = FeedbackParticipantType.STUDENTS;
            fqDb.createEntity(fqa);
        }

        for (int i = 1; i <= numberOfQuestionsToCreate[2]; i++) {
            fqa = getNewFeedbackQuestionAttributes();
            fqa.giverType = FeedbackParticipantType.SELF;
            fqa.questionNumber = numberOfQuestionsToCreate[0] + numberOfQuestionsToCreate[1] + i;
            fqDb.createEntity(fqa);
        }

        for (int i = 1; i <= numberOfQuestionsToCreate[3]; i++) {
            fqa = getNewFeedbackQuestionAttributes();
            fqa.giverType = FeedbackParticipantType.TEAMS;
            fqa.questionNumber = numberOfQuestionsToCreate[0] + numberOfQuestionsToCreate[1]
                                 + numberOfQuestionsToCreate[2] + i;
            fqDb.createEntity(fqa);
        }

        return numberOfQuestionsToCreate;
    }

    private void deleteFeedbackQuestions(int numToDelete) {
        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();
        for (int i = 1; i <= numToDelete; i++) {
            fqa.questionNumber = i;
            fqDb.deleteEntity(fqa);
        }
    }
    
    protected static void verifyAbsentInDatastore(FeedbackQuestionAttributes fq) {
        assertNull(fqDb.getFeedbackQuestion(fq.feedbackSessionName, fq.courseId, fq.questionNumber));
        assertNull(newDb.getFeedbackQuestion(fq.feedbackSessionName, fq.courseId, fq.questionNumber));
        assertNull(oldDb.getFeedbackQuestion(fq.feedbackSessionName, fq.courseId, fq.questionNumber));
    }
    
    protected static void verifyPresentInDatastore(FeedbackQuestionAttributes expected) {
        verifyPresentInDatastore(expected, false);
    }
    
    protected static void verifyPresentInDatastore(FeedbackQuestionAttributes expected, boolean wildcardId) {
        BaseComponentTestCase.verifyPresentInDatastore(expected, wildcardId);
        
        FeedbackQuestionAttributes expectedCopy = expected.getCopy();
        FeedbackQuestionAttributes actual = oldDb.getFeedbackQuestion(
                expectedCopy.feedbackSessionName, expectedCopy.courseId, expectedCopy.questionNumber);
        if (wildcardId) {
            expectedCopy.setId(actual.getId());
        }
        assertEquals(gson.toJson(expectedCopy), gson.toJson(actual));
        
        FeedbackQuestionAttributes expectedCopy1 = expected.getCopy();
        FeedbackQuestionAttributes actual1 = newDb.getFeedbackQuestion(
                expectedCopy1.feedbackSessionName, expectedCopy1.courseId, expectedCopy1.questionNumber);
        if (wildcardId) {
            expectedCopy1.setId(actual1.getId());
        }
        assertEquals(gson.toJson(expectedCopy1), gson.toJson(actual1));
    }
    
    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
    }
}
