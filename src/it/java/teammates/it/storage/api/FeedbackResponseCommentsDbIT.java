package teammates.it.storage.api;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;

/**
 * SUT: {@link FeedbackResponseCommentsDb}.
 */
public class FeedbackResponseCommentsDbIT extends BaseTestCaseWithDatabaseAccess {

    private final FeedbackResponseCommentsDb frcDb = FeedbackResponseCommentsDb.inst();

    private DataBundle testDataBundle;

    @BeforeClass
    public void setupClass() {
        testDataBundle = loadDataBundle("/FeedbackResponsesITBundle.json");
    }

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(testDataBundle);
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
    }

    @Test
    public void testGetFeedbackResponseCommentForResponseFromParticipant() {
        ______TS("success: typical case");
        FeedbackResponse fr = testDataBundle.feedbackResponses.get("response1ForQ1");

        FeedbackResponseComment expectedComment = testDataBundle.feedbackResponseComments.get("comment2ToResponse1ForQ1");
        FeedbackResponseComment actualComment = frcDb.getFeedbackResponseCommentForResponseFromParticipant(fr.getId());

        assertEquals(expectedComment, actualComment);
    }

    @Test
    public void testGetFeedbackResponseCommentsForResponses_matchFound_success() {
        FeedbackResponse response1ForQ1 = testDataBundle.feedbackResponses.get("response1ForQ1");
        FeedbackResponse response4ForQ1 = testDataBundle.feedbackResponses.get("response4ForQ1");

        ______TS("Multiple feedback responses match");
        List<FeedbackResponseComment> expected = List.of(
                testDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1"),
                testDataBundle.feedbackResponseComments.get("comment2ToResponse1ForQ1"),
                testDataBundle.feedbackResponseComments.get("comment1ToResponse4ForQ1")
        );
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForResponses(
                List.of(response1ForQ1.getId(), response4ForQ1.getId()));
        assertListCommentsEqual(expected, results);
    }

    @Test
    public void testGetFeedbackResponseCommentsForResponses_matchNotFound_shouldReturnEmptyList() {
        FeedbackResponse response1ForQ1 = testDataBundle.feedbackResponses.get("response1ForQ1");
        UUID nonexistentResponseId = UUID.fromString("11110000-0000-0000-0000-000000000000");

        ______TS("No matching response IDs");
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForResponses(
                List.of(nonexistentResponseId));
        assertEquals(0, results.size());

        ______TS("Empty list of response IDs");
        results = frcDb.getFeedbackResponseCommentsForResponses(List.of());
        assertEquals(0, results.size());

        ______TS("Mixed response IDs returns matching comments only");
        List<FeedbackResponseComment> expected = List.of(
                testDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1"),
                testDataBundle.feedbackResponseComments.get("comment2ToResponse1ForQ1")
        );
        results = frcDb.getFeedbackResponseCommentsForResponses(
                List.of(response1ForQ1.getId(), nonexistentResponseId));
        assertListCommentsEqual(expected, results);
    }

    private void assertListCommentsEqual(List<FeedbackResponseComment> expected, List<FeedbackResponseComment> actual) {
        assertTrue(
                String.format("List contents are not equal.%nExpected: %s,%nActual: %s",
                        expected.toString(), actual.toString()),
                new HashSet<>(expected).equals(new HashSet<>(actual)));
        assertEquals("List size not equal.", expected.size(), actual.size());
    }

}
