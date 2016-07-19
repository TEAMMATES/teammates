package teammates.test.cases.common;

import java.util.ArrayList;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackTextQuestionDetails;
import teammates.storage.entity.Question;
import teammates.storage.entity.QuestionsDbPersistenceAttributes;
import teammates.test.cases.BaseTestCase;

public class QuestionsDbPersistenceAttributesTest extends BaseTestCase {

    @BeforeClass
    public static void classSetUp() {
        printTestClassHeader();
    }

    @Test
    public void testToEntity() throws Exception {
        
        FeedbackQuestionAttributes fqa = new FeedbackQuestionAttributes();

        fqa.courseId = "validCourse";
        fqa.creatorEmail = "instructor@email.com";
        fqa.feedbackSessionName = "validFeedbackSession";
        fqa.giverType = FeedbackParticipantType.INSTRUCTORS;
        fqa.recipientType = FeedbackParticipantType.SELF;
        fqa.numberOfEntitiesToGiveFeedbackTo = 1;
        fqa.questionNumber = 1;

        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails("Valid Question text.");
        fqa.questionType = FeedbackQuestionType.TEXT;
        fqa.setQuestionDetails(questionDetails);

        fqa.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        fqa.showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
        fqa.showResponsesTo = new ArrayList<FeedbackParticipantType>();
        
        try {
            new QuestionsDbPersistenceAttributes(fqa);
            signalFailureToDetectException("Id cannot be unset when creating object for saving new question type");
        } catch (AssertionError expectedAssertion) {
            // expected
        }
        
        fqa.setId("valid");
        QuestionsDbPersistenceAttributes question = new QuestionsDbPersistenceAttributes(fqa);
        Question entity = question.toEntity();
        assertEquals("valid", entity.getId());
    }

    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
    }
}
