package teammates.test.cases.ui.pagedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertEquals;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.test.util.TestHelper;
import teammates.ui.controller.InstructorCommentsPageData;

public class InstructorCommentsPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void testAll() {
        testInit();
    }
    
    private void testInit() {
        
        ______TS("typical success case");
        
        AccountAttributes account = dataBundle.accounts.get("instructor3");      
        InstructorCommentsPageData data = new InstructorCommentsPageData(account);
        
        boolean isViewingDraft = false;
        boolean isDisplayArchive = false;
        String courseId = "idOfTypicalCourse1";
        String courseName = "idOfTypicalCourse1 : Typical Course 1 with 2 Evals";
        List<String> coursePaginationList = Arrays.asList("idOfTypicalCourse1", "idOfTypicalCourse2");
        Map<String, List<CommentAttributes>> comments = new TreeMap<String, List<CommentAttributes>>();
        
        List<CommentAttributes> commentsForGiverList = new ArrayList<CommentAttributes>();
        commentsForGiverList.add(dataBundle.comments.get("comment1FromI3C1toS2C1"));
        comments.put(InstructorCommentsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST, commentsForGiverList);
        commentsForGiverList = new ArrayList<CommentAttributes>();
        commentsForGiverList.add(dataBundle.comments.get("comment1FromI1C1toS1C1"));
        commentsForGiverList.add(dataBundle.comments.get("comment2FromI1C1toS1C1"));
        comments.put("instructor1@course1.tmt", commentsForGiverList);
        
        Map<String, List<Boolean>> commentModifyPermissions = new TreeMap<String, List<Boolean>>();
        
        List<Boolean> canModifyCommentList = new ArrayList<Boolean>();
        canModifyCommentList.add(true);
        commentModifyPermissions.put(InstructorCommentsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST, 
                                     canModifyCommentList);
        canModifyCommentList.add(true);
        canModifyCommentList.add(true);
        commentModifyPermissions.put("instructor1@course1.tmt", canModifyCommentList);
        
        CourseRoster roster = new CourseRoster(getStudentsInCourse(courseId), getInstructorsInCourse(courseId));
        List<FeedbackSessionAttributes> feedbackSessions = getFeedbackSessionsForCourse(courseId);
        int numberOfPendingComments = 0;
        
        data.init(isViewingDraft, isDisplayArchive, courseId, courseName, coursePaginationList, 
                  comments, commentModifyPermissions, roster, feedbackSessions, numberOfPendingComments);
        
        Map<String, List<CommentAttributes>> actualComments = data.getComments();
        Map<String, List<CommentAttributes>> expectedComments = comments;
        
        List<String> actualGivers = new ArrayList<String>();
        actualGivers.addAll(actualComments.keySet());
        List<String> expectedGivers = new ArrayList<String>();
        expectedGivers.addAll(expectedComments.keySet());
        
        TestHelper.isSameContentIgnoreOrder(expectedGivers, actualGivers);
        for (String giverEmail : expectedGivers) {
            TestHelper.isSameContentIgnoreOrder(expectedComments.get(giverEmail), actualComments.get(giverEmail));
        }
        
        assertEquals(courseId, data.getCourseId());
        assertEquals(courseName, data.getCourseName());
        TestHelper.isSameContentIgnoreOrder(coursePaginationList, data.getCoursePaginationList());
        TestHelper.isSameContentIgnoreOrder(feedbackSessions, data.getFeedbackSessions());
        
        String expectedNextPageLink = data.getInstructorCommentsLink() + "&courseid=" + "idOfTypicalCourse2";
        String expectedPreviousPageLink = "javascript:;";
        
        assertEquals(data.getNextPageLink(), expectedNextPageLink);
        assertEquals(data.getPreviousPageLink(), expectedPreviousPageLink);
        
        assertEquals(data.getNumberOfPendingComments(), numberOfPendingComments);
        
    }
    
    private List<CommentAttributes> getCommentsForGiverInCourse(String giverEmail, String courseId) {
        List<CommentAttributes> commentsForGiverInCourseList = new ArrayList<CommentAttributes>();
        for (CommentAttributes comment : dataBundle.comments.values()) {
            if (comment.giverEmail.equals(giverEmail) && comment.courseId.equals(courseId)) {
                commentsForGiverInCourseList.add(comment);
            }
        }
        return commentsForGiverInCourseList;
    }
    
    private List<InstructorAttributes> getInstructorsInCourse(String courseId) {
        List<InstructorAttributes> instructorsInCourse = new ArrayList<InstructorAttributes>();
        for (InstructorAttributes instructor : dataBundle.instructors.values()) {
            if (instructor.courseId.equals(courseId)) {
                instructorsInCourse.add(instructor);
            }
        }
        return instructorsInCourse;
    }
    
    private List<StudentAttributes> getStudentsInCourse(String courseId) {
        List<StudentAttributes> studentsInCourse = new ArrayList<StudentAttributes>();
        for (StudentAttributes student : dataBundle.students.values()) {
            if (student.course.equals(courseId)) {
                studentsInCourse.add(student);
            }
        }
        return studentsInCourse;
    }
    
    private List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(String courseId) {
        List<FeedbackSessionAttributes> feedbackSessionsInCourse = new ArrayList<FeedbackSessionAttributes>();
        for (FeedbackSessionAttributes feedbackSession : dataBundle.feedbackSessions.values()) {
            if (feedbackSession.courseId.equals(courseId)) {
                feedbackSessionsInCourse.add(feedbackSession);
            }
        }
        return feedbackSessionsInCourse;
    }
}
