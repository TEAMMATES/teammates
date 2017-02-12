package teammates.test.cases.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.storage.api.CommentsDb;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link CommentsDb},
 *      {@link CommentSearchDocument},
 *      {@link CommentSearchQuery}.
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
        
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.giverCommentTable.isEmpty());
        assertTrue(bundle.giverTable.isEmpty());
        assertTrue(bundle.recipientTable.isEmpty());
        
        bundle = commentsDb.search("", Arrays.asList(ins1InCourse1));
        
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.giverCommentTable.isEmpty());
        assertTrue(bundle.giverTable.isEmpty());
        assertTrue(bundle.recipientTable.isEmpty());
        
        ______TS("success: search for comments; query string matches some comments");
        
        // comment content
        bundle = commentsDb.search("\"Comment 1\"", Arrays.asList(ins1InCourse1));
        
        assertEquals(1, bundle.numberOfResults);
        assertSameCommentContentIgnoreOrder(Arrays.asList(comment1FromI1C1toS1C1),
                     bundle.giverCommentTable.get("instructor1@course1.tmtidOfTypicalCourse1"));
        
        // giver email
        bundle = commentsDb.search("instructor1@course1.tmt", Arrays.asList(ins1InCourse1, ins1InCourse2));
        
        assertEquals(5, bundle.numberOfResults);
        assertSameCommentContentIgnoreOrder(
                Arrays.asList(comment1FromI1C1toS1C1, comment2FromI1C1toS1C1, comment1FromI1C1toT11C1,
                        comment1FromI1C1toSE1C1, comment1FromI1C1toC1),
                bundle.giverCommentTable.get("instructor1@course1.tmtidOfTypicalCourse1"));
        
        // recipients email
        bundle = commentsDb.search("student1InCourse1@gmail.tmt", Arrays.asList(ins1InCourse1, ins1InCourse2));
        
        assertEquals(4, bundle.numberOfResults);
        assertEquals(1, bundle.giverCommentTable.size());
        assertSameCommentContentIgnoreOrder(
                 Arrays.asList(comment1FromI1C1toS1C1, comment2FromI1C1toS1C1, comment1FromI1C1toT11C1,
                         comment1FromI1C1toSE1C1), // recipients also in the section and team
                 bundle.giverCommentTable.get("instructor1@course1.tmtidOfTypicalCourse1"));
        
        // recipients name
        bundle = commentsDb.search("student1", Arrays.asList(ins3InCourse1, ins1InCourse2));
        
        assertEquals(1, bundle.numberOfResults);
        assertEquals(1, bundle.giverCommentTable.size());
        assertSameCommentContentIgnoreOrder(Arrays.asList(comment1FromI1C2toS1C2),
                 bundle.giverCommentTable.get("instructor1@course2.tmtidOfTypicalCourse2"));
        
        // recipients team
        bundle = commentsDb.search("\"Team 1.1\"", Arrays.asList(ins1InCourse1, ins1InCourse2));
        
        assertEquals(1, bundle.numberOfResults);
        assertEquals(1, bundle.giverCommentTable.size());
        assertSameCommentContentIgnoreOrder(Arrays.asList(comment1FromI1C1toT11C1),
                 bundle.giverCommentTable.get("instructor1@course1.tmtidOfTypicalCourse1"));
        
        // recipients section
        bundle = commentsDb.search("\"Section 1\"", Arrays.asList(ins1InCourse1, ins1InCourse2));
        
        assertEquals(4, bundle.numberOfResults);
        assertEquals(1, bundle.giverCommentTable.size());
        assertSameCommentContentIgnoreOrder(
                 Arrays.asList(comment1FromI1C1toS1C1, comment2FromI1C1toS1C1, comment1FromI1C1toT11C1,
                         comment1FromI1C1toSE1C1),
                 bundle.giverCommentTable.get("instructor1@course1.tmtidOfTypicalCourse1"));
        
        
        // course id
        bundle = commentsDb.search("idOfTypicalCourse2", Arrays.asList(ins1InCourse1, ins1InCourse2));
        
        assertEquals(1, bundle.numberOfResults);
        assertEquals(1, bundle.giverCommentTable.size());
        assertSameCommentContentIgnoreOrder(Arrays.asList(comment1FromI1C2toS1C2),
                 bundle.giverCommentTable.get("instructor1@course2.tmtidOfTypicalCourse2"));
        
        // course name
        bundle = commentsDb.search("Course", Arrays.asList(ins1InCourse2, ins3InCourse1));
        
        assertEquals(3, bundle.numberOfResults);
        assertEquals(2, bundle.giverCommentTable.size());
        assertSameCommentContentIgnoreOrder(Arrays.asList(comment1FromI1C2toS1C2),
                 bundle.giverCommentTable.get("instructor1@course2.tmtidOfTypicalCourse2"));
        assertSameCommentContentIgnoreOrder(Arrays.asList(comment1FromI3C1toS2C1, comment1FromI3C1toC1),
                 bundle.giverCommentTable.get("instructor3@course1.tmtidOfTypicalCourse1"));
        
        ______TS("success: search for comments; query string should be case-insensitive");

        bundle = commentsDb.search("sTuDeNt1INCourSe1@gMaIl.Tmt", Arrays.asList(ins1InCourse1, ins1InCourse2));
        
        assertEquals(4, bundle.numberOfResults);
        assertEquals(1, bundle.giverCommentTable.size());
        assertSameCommentContentIgnoreOrder(
                 Arrays.asList(comment1FromI1C1toS1C1, comment2FromI1C1toS1C1, comment1FromI1C1toT11C1,
                         comment1FromI1C1toSE1C1),
                 bundle.giverCommentTable.get("instructor1@course1.tmtidOfTypicalCourse1"));

        ______TS("success: search for comments; query string matches some comments based on comment visibility");
        
        // Test case : not in the same course
        
        // Test case : comment not visible to instructorS

        // The above cases are tested in the previous test case
        
        // instructors beside the giver can view the comment due the visibility setting
        bundle = commentsDb.search("idOfTypicalCourse2", Arrays.asList(ins2InCourse2));
        
        assertEquals(1, bundle.numberOfResults);
        assertEquals(1, bundle.giverCommentTable.size());
        assertSameCommentContentIgnoreOrder(Arrays.asList(comment1FromI1C2toS1C2),
                 bundle.giverCommentTable.get("AnonymousidOfTypicalCourse2"));
        assertEquals(1, bundle.giverTable.size());
        assertEquals("Anonymous (idOfTypicalCourse2)", bundle.giverTable.get("AnonymousidOfTypicalCourse2"));

        ______TS("success: search for comments; deleted comment no longer searchable");
        
        commentsDb.deleteCommentsByInstructorEmail(ins1InCourse1.courseId, ins1InCourse1.email);
        
        bundle = commentsDb.search("comment", Arrays.asList(ins1InCourse1));
        
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.giverCommentTable.isEmpty());
        assertTrue(bundle.giverTable.isEmpty());
        assertTrue(bundle.recipientTable.isEmpty());
        
        ______TS("success: search for students; deleted comment without deleted comment: the document "
                 + "will be deleted during the search");
        
        commentsDb.deleteEntity(comment1FromI3C1toS2C1);
        
        bundle = commentsDb.search("instructor3@course1.tmt", Arrays.asList(ins3InCourse1));
        
        assertEquals(1, bundle.numberOfResults);
        assertSameCommentContentIgnoreOrder(Arrays.asList(comment1FromI3C1toC1),
                bundle.giverCommentTable.get("instructor3@course1.tmtidOfTypicalCourse1"));
    }
    
    private List<String> getCommentContentAsList(List<CommentAttributes> commentsList) {
        ArrayList<String> comments = new ArrayList<String>();
        for (CommentAttributes comment : commentsList) {
            comments.add(comment.commentText.getValue());
        }
        return comments;
    }
    
    private void assertSameCommentContentIgnoreOrder(List<CommentAttributes> commentsListA,
            List<CommentAttributes> commentListB) {
        AssertHelper.assertSameContentIgnoreOrder(getCommentContentAsList(commentsListA),
                getCommentContentAsList(commentListB));
    }
    
}
