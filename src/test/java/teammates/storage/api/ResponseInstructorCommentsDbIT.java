package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.TestGroups;

/**
 * SUT: {@link ResponseInstructorCommentsDb}.
 */
public class ResponseInstructorCommentsDbIT extends BaseTestCaseWithDatabaseAccess {

    private final ResponseInstructorCommentsDb frcDb = ResponseInstructorCommentsDb.inst();

    private DataBundle testDataBundle;

    @BeforeClass(alwaysRun = true)
    public void setupClass() {
        testDataBundle = loadDataBundle("/FeedbackResponsesITBundle.json");
    }

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        persistDataBundle(testDataBundle);
    }

    @Test(groups = TestGroups.INTEGRATION)
    public void testGetResponseInstructorCommentsForResponses_matchFound_success() {
        FeedbackResponse response1ForQ1 = testDataBundle.feedbackResponses.get("response1ForQ1");
        FeedbackResponse response4ForQ1 = testDataBundle.feedbackResponses.get("response4ForQ1");

        ______TS("Multiple feedback responses match");
        List<ResponseInstructorComment> expected = List.of(
                testDataBundle.responseInstructorComments.get("comment1ToResponse1ForQ1"),
                testDataBundle.responseInstructorComments.get("comment1ToResponse4ForQ1")
        );
        List<ResponseInstructorComment> results = inTransaction(() -> frcDb.getResponseInstructorCommentsForResponses(
                List.of(response1ForQ1.getId(), response4ForQ1.getId())));
        assertListCommentsEqual(expected, results);
    }

    @Test(groups = TestGroups.INTEGRATION)
    public void testGetResponseInstructorCommentsForResponses_matchNotFound_shouldReturnEmptyList() {
        FeedbackResponse response1ForQ1 = testDataBundle.feedbackResponses.get("response1ForQ1");
        UUID nonexistentResponseId = UUID.fromString("11110000-0000-0000-0000-000000000000");

        ______TS("No matching response IDs");
        List<ResponseInstructorComment> results = inTransaction(() -> frcDb.getResponseInstructorCommentsForResponses(
                List.of(nonexistentResponseId)));
        assertEquals(0, results.size());

        ______TS("Empty list of response IDs");
        results = inTransaction(() -> frcDb.getResponseInstructorCommentsForResponses(List.of()));
        assertEquals(0, results.size());

        ______TS("Mixed response IDs returns matching comments only");
        List<ResponseInstructorComment> expected = List.of(
                testDataBundle.responseInstructorComments.get("comment1ToResponse1ForQ1")
        );
        results = inTransaction(() -> frcDb.getResponseInstructorCommentsForResponses(
                List.of(response1ForQ1.getId(), nonexistentResponseId)));
        assertListCommentsEqual(expected, results);
    }

    private void assertListCommentsEqual(List<ResponseInstructorComment> expected, List<ResponseInstructorComment> actual) {
        assertTrue(new HashSet<>(expected).equals(new HashSet<>(actual)),
                String.format("List contents are not equal.%nExpected: %s,%nActual: %s",
                        expected.toString(), actual.toString()));
        assertEquals(expected.size(), actual.size(), "List size not equal.");
    }

}
