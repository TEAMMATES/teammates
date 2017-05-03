package teammates.test.cases.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.storage.api.CommentsDb;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link CommentsDb},
 *      {@link teammates.storage.search.CommentSearchDocument},
 *      {@link teammates.storage.search.CommentSearchQuery}.
 */
public class CommentSearchTest extends BaseSearchTest {

    @Test
    public void allTests() {

        CommentsDb commentsDb = new CommentsDb();

        InstructorAttributes ins1InCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes ins3InCourse1 = dataBundle.instructors.get("instructor3OfCourse1");
        InstructorAttributes ins1InCourse2 = dataBundle.instructors.get("instructor1OfCourse2");
        InstructorAttributes ins2InCourse2 = dataBundle.instructors.get("instructor2OfCourse2");
        CommentAttributes comment1FromI1C1toS1C1 = dataBundle.comments.get("comment1FromI1C1toS1C1"); // PERSON
        CommentAttributes comment2FromI1C1toS1C1 = dataBundle.comments.get("comment2FromI1C1toS1C1"); // PERSON
        CommentAttributes comment1FromI3C1toS2C1 = dataBundle.comments.get("comment1FromI3C1toS2C1"); // PERSON
        CommentAttributes comment1FromI1C2toS1C2 = dataBundle.comments.get("comment1FromI1C2toS1C2"); // PERSON
        CommentAttributes comment1FromI1C1toT11C1 = dataBundle.comments.get("comment1FromI1C1toT1.1C1"); // TEAM
        CommentAttributes comment1FromI1C1toSE1C1 = dataBundle.comments.get("comment1FromI1C1toSE1C1"); // SECTION
        CommentAttributes comment1FromI1C1toC1 = dataBundle.comments.get("comment1FromI1C1toC1"); // COURSE
        CommentAttributes comment1FromI3C1toC1 = dataBundle.comments.get("comment1FromI3C1toC1"); // COURSE

        ______TS("success: search for comments; query string does not match any comment");

        CommentSearchResultBundle bundle = commentsDb.search("non-existent", Arrays.asList(ins1InCourse1));

        assertEmptyBundle(bundle);

        bundle = commentsDb.search("", Arrays.asList(ins1InCourse1));

        assertEmptyBundle(bundle);

        ______TS("success: search for comments; query string matches some comments");

        // comment content
        bundle = commentsDb.search("\"Comment 1\"", Arrays.asList(ins1InCourse1));

        List<CommentAttributes> expectedComments = Arrays.asList(comment1FromI1C1toS1C1);
        assertHasOwnComments(bundle, Arrays.asList(expectedComments), Arrays.asList(ins1InCourse1));

        // giver's email
        bundle = commentsDb.search("instructor1@course1.tmt", Arrays.asList(ins1InCourse1, ins1InCourse2));

        expectedComments = Arrays.asList(comment1FromI1C1toS1C1, comment2FromI1C1toS1C1, comment1FromI1C1toT11C1,
                                         comment1FromI1C1toSE1C1, comment1FromI1C1toC1);
        assertHasOwnComments(bundle, Arrays.asList(expectedComments), Arrays.asList(ins1InCourse1));

        // recipient's email
        bundle = commentsDb.search("student1InCourse1@gmail.tmt", Arrays.asList(ins1InCourse1, ins1InCourse2));

        expectedComments = Arrays.asList(comment1FromI1C1toS1C1, comment2FromI1C1toS1C1, comment1FromI1C1toT11C1,
                                         comment1FromI1C1toSE1C1); // recipients also in the section and team
        assertHasOwnComments(bundle, Arrays.asList(expectedComments), Arrays.asList(ins1InCourse1));

        // recipient's name
        bundle = commentsDb.search("student1", Arrays.asList(ins3InCourse1, ins1InCourse2));

        expectedComments = Arrays.asList(comment1FromI1C2toS1C2);
        assertHasOwnComments(bundle, Arrays.asList(expectedComments), Arrays.asList(ins1InCourse2));

        // recipient's team
        bundle = commentsDb.search("\"Team 1.1\"", Arrays.asList(ins1InCourse1, ins1InCourse2));

        expectedComments = Arrays.asList(comment1FromI1C1toT11C1);
        assertHasOwnComments(bundle, Arrays.asList(expectedComments), Arrays.asList(ins1InCourse1));

        // recipient's section
        bundle = commentsDb.search("\"Section 1\"", Arrays.asList(ins1InCourse1, ins1InCourse2));

        expectedComments = Arrays.asList(comment1FromI1C1toS1C1, comment2FromI1C1toS1C1,
                                         comment1FromI1C1toT11C1, comment1FromI1C1toSE1C1);
        assertHasOwnComments(bundle, Arrays.asList(expectedComments), Arrays.asList(ins1InCourse1));

        // course id
        bundle = commentsDb.search("idOfTypicalCourse2", Arrays.asList(ins1InCourse1, ins1InCourse2));

        expectedComments = Arrays.asList(comment1FromI1C2toS1C2);
        assertHasOwnComments(bundle, Arrays.asList(expectedComments), Arrays.asList(ins1InCourse2));

        // course name
        bundle = commentsDb.search("Course", Arrays.asList(ins1InCourse2, ins3InCourse1));

        expectedComments = Arrays.asList(comment1FromI1C2toS1C2);
        List<CommentAttributes> expectedCommentsForI3 =
                Arrays.asList(comment1FromI3C1toS2C1, comment1FromI3C1toC1);
        assertHasOwnComments(bundle, Arrays.asList(expectedComments, expectedCommentsForI3),
                             Arrays.asList(ins1InCourse2, ins3InCourse1));

        ______TS("success: search for comments; query string should be case-insensitive");

        bundle = commentsDb.search("sTuDeNt1INCourSe1@gMaIl.Tmt", Arrays.asList(ins1InCourse1, ins1InCourse2));

        expectedComments = Arrays.asList(comment1FromI1C1toS1C1, comment2FromI1C1toS1C1,
                                         comment1FromI1C1toT11C1, comment1FromI1C1toSE1C1);
        assertHasOwnComments(bundle, Arrays.asList(expectedComments), Arrays.asList(ins1InCourse1));

        ______TS("success: search for comments; query string matches some comments based on comment visibility");

        // Test case: instructor not in the same course cannot view comment

        // Test case: instructor cannot view comment that is not visible to instructors

        // The above cases are tested in the previous test case

        // instructors besides the giver can view the comment due to the visibility setting
        bundle = commentsDb.search("idOfTypicalCourse2", Arrays.asList(ins2InCourse2));

        String giverAsKey = "Anonymous" + ins2InCourse2.courseId;
        assertEquals(1, bundle.numberOfResults);
        assertEquals(1, bundle.giverCommentTable.size());
        assertSameCommentContentIgnoreOrder(
                Arrays.asList(comment1FromI1C2toS1C2), bundle.giverCommentTable.get(giverAsKey));
        assertEquals("Anonymous" + " (" + ins2InCourse2.courseId + ")", bundle.giverTable.get(giverAsKey));

        ______TS("success: search for comments; deleted comment no longer searchable");

        commentsDb.deleteCommentsByInstructorEmail(ins1InCourse1.courseId, ins1InCourse1.email);

        bundle = commentsDb.search("comment", Arrays.asList(ins1InCourse1));

        assertEmptyBundle(bundle);

        ______TS("success: search for comments; deleted comment without deleting document: the document "
                 + "will be deleted during the search");

        commentsDb.deleteEntity(comment1FromI3C1toS2C1);

        bundle = commentsDb.search("instructor3@course1.tmt", Arrays.asList(ins3InCourse1));

        expectedComments = Arrays.asList(comment1FromI3C1toC1);
        assertHasOwnComments(bundle, Arrays.asList(expectedComments), Arrays.asList(ins3InCourse1));
    }

    private void assertEmptyBundle(CommentSearchResultBundle bundle) {
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.giverCommentTable.isEmpty());
        assertTrue(bundle.giverTable.isEmpty());
        assertTrue(bundle.recipientTable.isEmpty());
    }

    private void assertHasOwnComments(CommentSearchResultBundle bundle,
            List<List<CommentAttributes>> commentsList, List<InstructorAttributes> instructorsList) {
        assertEquals(commentsList.size(), instructorsList.size()); // every instructor has one comments list

        int totalComments = 0;
        for (int i = 0; i < commentsList.size(); i++) {
            List<CommentAttributes> comments = commentsList.get(i);
            InstructorAttributes instructor = instructorsList.get(i);
            String giverAsKey = instructor.email + instructor.courseId;

            assertSameCommentContentIgnoreOrder(comments, bundle.giverCommentTable.get(giverAsKey));
            assertEquals(bundle.giverTable.get(giverAsKey), "You (" + instructor.courseId + ")");

            // Not testable for bundle.recipientTable as the key is comment id

            totalComments += comments.size();
        }

        assertEquals(totalComments, bundle.numberOfResults);
    }

    private void assertSameCommentContentIgnoreOrder(
            List<CommentAttributes> expectedCommentsList, List<CommentAttributes> actualCommentsList) {
        // as comment content is unique for every comment in test data,
        // we can assume same contents means same comments.
        AssertHelper.assertSameContentIgnoreOrder(
                getCommentContentAsList(expectedCommentsList), getCommentContentAsList(actualCommentsList));
    }

    private List<String> getCommentContentAsList(List<CommentAttributes> commentsList) {
        List<String> comments = new ArrayList<String>();
        for (CommentAttributes comment : commentsList) {
            comments.add(comment.commentText.getValue());
        }
        return comments;
    }

}
