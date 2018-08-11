package teammates.test.cases.search;

import java.util.ArrayList;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.storage.api.FeedbackResponseCommentsDb;

/**
 * SUT: {@link FeedbackResponseCommentsDb},
 *      {@link teammates.storage.search.FeedbackResponseCommentSearchDocument},
 *      {@link teammates.storage.search.FeedbackResponseCommentSearchQuery}.
 */
public class FeedbackResponseCommentSearchTest extends BaseSearchTest {

    @Test
    public void allTests() {
        FeedbackResponseCommentsDb commentsDb = new FeedbackResponseCommentsDb();

        FeedbackResponseCommentAttributes frc1I1Q1S1C1 = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q1S1C1");
        FeedbackResponseCommentAttributes frc1I1Q2S1C1 = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q2S1C1");
        FeedbackResponseCommentAttributes frc1I3Q1S2C2 = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q1S2C2");

        ArrayList<InstructorAttributes> instructors = new ArrayList<InstructorAttributes>();

        ______TS("success: search for comments; no results found as instructor doesn't have privileges");

        instructors.add(dataBundle.instructors.get("helperOfCourse1"));
        FeedbackResponseCommentSearchResultBundle bundle = commentsDb.search("\"self-feedback\"", instructors);
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.comments.isEmpty());

        ______TS("success: search for comments; query string does not match any comment");

        instructors.clear();
        instructors.add(dataBundle.instructors.get("instructor3OfCourse1"));
        instructors.add(dataBundle.instructors.get("instructor3OfCourse2"));
        bundle = commentsDb.search("non-existent", instructors);
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.comments.isEmpty());

        ______TS("success: search for comments; query string matches single comment");

        bundle = commentsDb.search("\"Instructor 3 comment to instr1C2 response to student1C2\"", instructors);
        verifySearchResults(bundle, frc1I3Q1S2C2);

        ______TS("success: search for comments in instructor's course; query string matches some comments");

        bundle = commentsDb.search("\"self feedback\"", instructors);
        verifySearchResults(bundle, frc1I1Q1S1C1, frc1I1Q2S1C1);

        ______TS("success: search for comments in instructor's course; confirms query string is case insensitive");

        bundle = commentsDb.search("\"Instructor 1 COMMENT to student 1 self feedback Question 2\"", instructors);
        verifySearchResults(bundle, frc1I1Q2S1C1);

        ______TS("success: search for comments using feedbackSessionName");

        bundle = commentsDb.search("\"First feedback session\"", instructors);
        verifySearchResults(bundle, frc1I1Q2S1C1, frc1I1Q1S1C1);

        ______TS("success: search for comments using Instructor's email");

        bundle = commentsDb.search("instructor1@course1.tmt", instructors);
        verifySearchResults(bundle, frc1I1Q2S1C1, frc1I1Q1S1C1);

        ______TS("success: search for comments using Student name");

        bundle = commentsDb.search("\"student2 In Course1\"", instructors);
        verifySearchResults(bundle, frc1I1Q2S1C1);

        ______TS("success: search for comments; confirms deleted comments are not included in results");

        commentsDb.deleteDocument(commentsDb.getFeedbackResponseComment(frc1I3Q1S2C2.courseId, frc1I3Q1S2C2.createdAt,
                frc1I3Q1S2C2.commentGiver));
        bundle = commentsDb.search("\"Instructor 3 comment to instr1C2 response to student1C2\"", instructors);
        verifySearchResults(bundle);
    }
}
