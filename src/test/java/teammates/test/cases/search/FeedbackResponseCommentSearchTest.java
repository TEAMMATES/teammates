package teammates.test.cases.search;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link FeedbackResponseCommentsDb},{@link FeedbackResponseCommentSearchResult}.
 */

public class FeedbackResponseCommentSearchTest extends BaseSearchTest {

    @Test
    public void allTests() throws InvalidParameterException {

        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();
        FeedbackResponseCommentAttributes fdbrI1S1 =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        FeedbackResponseCommentAttributes fdbrI1S1Q2 =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        FeedbackResponseCommentAttributes fdbrI1S1Q3 =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q3S1C1");
        List<InstructorAttributes> ins1OfCourse1 =
                Arrays.asList(new InstructorAttributes[] { dataBundle.instructors.get("instructor1OfCourse1") });

        ______TS("success: search for feedbackResponseComments; query string does not match any "
                + "feedbackResponseComments; results restricted "
                + "based on instructor's privilege");

        FeedbackResponseCommentSearchResultBundle bundle =
                feedbackResponseCommentsDb.search("non-existent", ins1OfCourse1);
        verifySearchResults(bundle);

        ______TS("success: search for feedbackResponseComments; query string matches"
                + " some feedbackResponseComments; results restricted based on instructor's privilege");

        bundle =
                feedbackResponseCommentsDb.search("\"self feedback Question 2\"", ins1OfCourse1);
        verifySearchResults(bundle, fdbrI1S1Q2);

        ______TS("success: search for feedbackResponseComments; query string should be case-insensitive;results restricted "
                + "based on instructor's privilege");

        bundle = feedbackResponseCommentsDb
                .search("\"Instructor 1 COMMENT to student 1 self feedback Question 2\"", ins1OfCourse1);
        verifySearchResults(bundle, fdbrI1S1Q2);

        ______TS("success: search for feedbackResponseComments searchable by courseId; results restricted "
                + "based on instructor's privilege");

        bundle = feedbackResponseCommentsDb.search("idOfTypicalCourse1", ins1OfCourse1);
        verifySearchResults(bundle, fdbrI1S1, fdbrI1S1Q2);

        ______TS("success: search for feedbackResponseComments searchable by feedbackSessionName; results restricted "
                + "based on instructor's privilege");

        bundle = feedbackResponseCommentsDb.search("\"First feedback session\"", ins1OfCourse1);
        verifySearchResults(bundle, fdbrI1S1, fdbrI1S1Q2);

        ______TS("success: search for feedbackResponseComments searchable by feedbackResponseId; results restricted "
                + "based on instructor's privilege");

        bundle = feedbackResponseCommentsDb
                .search("1%student1InCourse1@gmail.tmt%student1InCourse1@gmail.tmt", ins1OfCourse1);
        verifySearchResults(bundle, fdbrI1S1, fdbrI1S1Q2);

        ______TS("success: search for feedbackResponseComments; deleted feedbackResponseComments no longer searchable");

        feedbackResponseCommentsDb.deleteFeedbackResponseCommentsForCourse(fdbrI1S1Q3.courseId);
        bundle = feedbackResponseCommentsDb.search("\"feedback Question 3\"", ins1OfCourse1);
        verifySearchResults(bundle);

    }

    /*
     * Verifies that search results match with expected output. Parameters are
     * modified to standardize {@link FeedbackResponseCommentAttributes} for
     * comparison.
     *
     * @param actual the results from the search query.
     *
     * @param expected the expected results for the search query.
     */
    private static void verifySearchResults(FeedbackResponseCommentSearchResultBundle actual,
            FeedbackResponseCommentAttributes... expected) {
        assertEquals(expected.length, actual.numberOfResults);
        standardizedFeedbackResponseCommentsForComparison(expected);
        List<FeedbackResponseCommentAttributes> feedbackCommentListNormalised =
                new ArrayList<FeedbackResponseCommentAttributes>();
        for (List<FeedbackResponseCommentAttributes> feedbackResponseCommentAttributesList
                : actual.comments.values()) {
            standardizedFeedbackResponseCommentsForComparison(feedbackResponseCommentAttributesList
                    .toArray(new FeedbackResponseCommentAttributes[feedbackResponseCommentAttributesList.size()]));
            feedbackCommentListNormalised.add(feedbackResponseCommentAttributesList.get(0));
        }

        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(expected), feedbackCommentListNormalised);
    }

    /*
     * Standardizes feedbackResponsesComments for comparison by setting fields
     * to null
     *
     * @param feedbackResponsesComments the FeedbackResponseCommentAttributes to
     * standardize.
     */
    private static void standardizedFeedbackResponseCommentsForComparison(
            FeedbackResponseCommentAttributes[] feedbackResponsesComments) {

        for (FeedbackResponseCommentAttributes feedbackResponseComment : feedbackResponsesComments) {
            feedbackResponseComment.feedbackQuestionId = null;
            feedbackResponseComment.feedbackResponseId = null;
            feedbackResponseComment.feedbackResponseCommentId = null;
            feedbackResponseComment.lastEditorEmail = null;
            feedbackResponseComment.lastEditedAt = null;
        }

    }

}
