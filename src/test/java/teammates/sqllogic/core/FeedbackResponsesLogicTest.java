package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.FeedbackResponsesDb;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.responses.FeedbackRankRecipientsResponse;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackResponsesLogic}.
 */
public class FeedbackResponsesLogicTest extends BaseTestCase {
    
    private FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();

    private SqlDataBundle typicalDataBundle;

    @BeforeClass
    public void setUpClass() {
        typicalDataBundle = getTypicalSqlDataBundle();
    }

    @BeforeMethod
    public void setUpMethod() {
        FeedbackResponsesDb frDb = mock(FeedbackResponsesDb.class);
        UsersLogic usersLogic = mock(UsersLogic.class);
        FeedbackQuestionsLogic fqLogic = mock(FeedbackQuestionsLogic.class);
        frLogic.initLogicDependencies(frDb, usersLogic, fqLogic);
    }

    @Test(enabled = false)
    public void testUpdateFeedbackResponse_feedbackResponseDoesNotExist_throwEntityDoesNotExistException() {
        FeedbackResponse oldFr = typicalDataBundle.feedbackResponses.get("response1ForQ6");

        when(frLogic.getFeedbackResponse(generateDifferentUuid(oldFr.getId()))).thenReturn(null);

        assertThrows(EntityDoesNotExistException.class, () -> frLogic.updateFeedbackResponse(oldFr));
    }

    @Test(enabled = false)
    public void testUpdateFeedbackResponse_feedbackResponseSameGiverAndRecipient_updateAttributes()
            throws EntityAlreadyExistsException, EntityDoesNotExistException, InvalidParametersException {
        FeedbackResponse oldFr = typicalDataBundle.feedbackResponses.get("response1ForQ6");
        FeedbackRankRecipientsResponse oldFrWithUpdatedAnswer =
                (FeedbackRankRecipientsResponse) typicalDataBundle.feedbackResponses.get("response1ForQ6");

        FeedbackRankRecipientsResponseDetails oldFrDetails =
                (FeedbackRankRecipientsResponseDetails) oldFr.getFeedbackResponseDetailsCopy();

        oldFrDetails.setAnswer(80);
        oldFrWithUpdatedAnswer.setAnswer(oldFrDetails);

        when(frLogic.getFeedbackResponse(oldFr.getId())).thenReturn(oldFr);

        frLogic.updateFeedbackResponse(oldFrWithUpdatedAnswer);

        FeedbackRankRecipientsResponse updatedFrFromDb =
                (FeedbackRankRecipientsResponse) frLogic.getFeedbackResponse(oldFr.getId());

        assertEquals(oldFrWithUpdatedAnswer, updatedFrFromDb);
        assertEquals(oldFrWithUpdatedAnswer.getGiver(), updatedFrFromDb.getGiver());
        assertEquals(oldFrWithUpdatedAnswer.getRecipient(), updatedFrFromDb.getRecipient());
        assertEquals(oldFrWithUpdatedAnswer.getAnswer(), updatedFrFromDb.getAnswer());
        assertEquals(oldFrWithUpdatedAnswer.getAnswer().getAnswer(), updatedFrFromDb.getAnswer().getAnswer());
    }

    private UUID generateDifferentUuid(UUID uuid) {
        UUID ret = UUID.randomUUID();
        while (ret.equals(uuid)) {
            ret = UUID.randomUUID();
        }
        return ret;
    }

}
