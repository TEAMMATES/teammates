package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;

import java.util.List;
import java.util.UUID;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackQuestionsDb}.
 */
public class FeedbackQuestionsDbTest extends BaseTestCase {
    private FeedbackQuestionsDb feedbackQuestionsDb;
    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        feedbackQuestionsDb = spy(FeedbackQuestionsDb.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testCreateFeedbackQuestion_success() throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackQuestion feedbackQuestion = getFeedbackQuestion();

        feedbackQuestionsDb.createFeedbackQuestion(feedbackQuestion);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackQuestion), times(1));
    }

    @Test
    public void testCreateFeedbackQuestion_questionAlreadyExists_throwsEntityAlreadyExistsException() {
        FeedbackQuestion feedbackQuestion = getFeedbackQuestion();
        UUID fqid = feedbackQuestion.getId();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackQuestion.class, fqid)).thenReturn(feedbackQuestion);

        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> feedbackQuestionsDb.createFeedbackQuestion(feedbackQuestion));

        assertEquals(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, feedbackQuestion.toString()), eaee.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackQuestion), never());
    }

    @Test
    public void testCreateFeedbackQuestion_invalidQuestion_throwsInvalidParametersException() {
        FeedbackQuestion feedbackQuestion = getFeedbackQuestion();
        feedbackQuestion.setGiverType(FeedbackParticipantType.NONE);

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> feedbackQuestionsDb.createFeedbackQuestion(feedbackQuestion));

        assertEquals(feedbackQuestion.getInvalidityInfo(), List.of(ipe.getMessage()));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackQuestion), never());
    }

    @Test
    public void testGetFeedbackQuestion_success() {
        FeedbackQuestion feedbackQuestion = getFeedbackQuestion();
        UUID fqid = feedbackQuestion.getId();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackQuestion.class, fqid)).thenReturn(feedbackQuestion);

        FeedbackQuestion retrievedSession = feedbackQuestionsDb.getFeedbackQuestion(fqid);

        mockHibernateUtil.verify(() -> HibernateUtil.get(FeedbackQuestion.class, fqid), times(1));
        assertEquals(feedbackQuestion, retrievedSession);
    }

    @Test
    public void testGetFeedbackQuestion_questionDoesNotExist_returnNull() {
        UUID fqid = UUID.randomUUID();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackQuestion.class, fqid)).thenReturn(null);

        FeedbackQuestion retrievedSession = feedbackQuestionsDb.getFeedbackQuestion(fqid);

        mockHibernateUtil.verify(() -> HibernateUtil.get(FeedbackQuestion.class, fqid), times(1));
        assertNull(retrievedSession);
    }

    @Test
    public void testDeleteFeedbackQuestion_success() {
        FeedbackQuestion feedbackQuestion = getFeedbackQuestion();
        UUID fqid = feedbackQuestion.getId();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackQuestion.class, fqid)).thenReturn(feedbackQuestion);

        feedbackQuestionsDb.deleteFeedbackQuestion(fqid);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(feedbackQuestion), times(1));
    }

    @Test
    public void testDeleteFeedbackQuestion_questionDoesNotExist_nothingHappens() {
        UUID fqid = UUID.randomUUID();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackQuestion.class, fqid)).thenReturn(null);

        feedbackQuestionsDb.deleteFeedbackQuestion(fqid);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(any()), never());
    }

    private FeedbackQuestion getFeedbackQuestion() {
        return getTypicalFeedbackQuestionForSession(getTypicalFeedbackSessionForCourse(getTypicalCourse()));
    }
}
