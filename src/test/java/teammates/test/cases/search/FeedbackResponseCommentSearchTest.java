package teammates.test.cases.search;

//import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.storage.api.FeedbackResponseCommentsDb;
//import teammates.test.driver.AssertHelper;


/**
 * SUT: {@link FeedbackResponseCommentsDb},
 *      {@link FeedbackResponseCommentSearchResult}.
 */
public class FeedbackResponseCommentSearchTest extends BaseSearchTest {

    @Test
    public void allTests(){
        
       FeedbackResponseCommentsDb  feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();
       
    // FeedbackResponseCommentAttributes fdbrI1S1=dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
    // FeedbackResponseCommentAttributes fdbrI1S1Q2 = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
       FeedbackResponseCommentAttributes fdbrI1S1Q3 = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q3S1C1");
       List<InstructorAttributes> ins1OfCourse1 = Arrays.asList(
               new InstructorAttributes[] { dataBundle.instructors.get("instructor1OfCourse1") });
       
       ______TS("success: search for feedbackresponsecomments; query string does not match any feedbackresponsecomments; results restricted "
               + "based on instructor's privilege");
        
       FeedbackResponseCommentSearchResultBundle bundle = feedbackResponseCommentsDb.search("non-existent", ins1OfCourse1);
       assertEquals(0,bundle.numberOfResults);
       assertTrue(bundle.comments.isEmpty());
       
       ______TS("success: search for feedbackresponsecomments; query string matches some feedbackresponsecomments; results restricted "
               + "based on instructor's privilege");
       
        bundle =feedbackResponseCommentsDb.search("self feedback Question 2", ins1OfCourse1);
        assertEquals(2,bundle.numberOfResults);
        //AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(fdbrI1S1,fdbrI1S1Q2), new ArrayList<>(bundle.comments.values()));
       
        ______TS("success: search for feedbackresponsecomments; query string should be case-insensitive;results restricted "
               + "based on instructor's privilege");
        
        bundle = feedbackResponseCommentsDb.search("Instructor 1 COMMENT to student 1 self feedback Question 2", ins1OfCourse1);
        assertEquals(2,bundle.numberOfResults);
        //AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(fdbrI1S1Q2), new ArrayList<>(bundle.comments.values()));
        
        ______TS("success: search for students; deleted student no longer searchable");
        
       feedbackResponseCommentsDb.deleteFeedbackResponseCommentsForCourse(fdbrI1S1Q3.courseId);
       bundle = feedbackResponseCommentsDb.search("feedback Question 3",ins1OfCourse1);
       assertEquals(0, bundle.numberOfResults);
       assertTrue(bundle.comments.isEmpty());
       
    }
}
