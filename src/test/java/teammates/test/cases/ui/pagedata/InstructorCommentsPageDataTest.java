package teammates.test.cases.ui.pagedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.test.util.TestHelper;
import teammates.ui.controller.InstructorCommentsPageData;
import teammates.ui.template.Comment;
import teammates.ui.template.CommentsForStudentsTable;

public class InstructorCommentsPageDataTest extends BaseTestCase {
    private static final String COMMENT_GIVER_NAME_THAT_COMES_FIRST = "0you";
    private static DataBundle dataBundle = getTypicalDataBundle();
    private static CourseAttributes course1;
    private static CourseAttributes course2;
    private static InstructorAttributes instructor1;
    
    @BeforeClass
    public void classSetUp() throws Exception {
        printTestClassHeader();
        course1 = dataBundle.courses.get("typicalCourse1");
        course2 = dataBundle.courses.get("typicalCourse2");
        instructor1 = dataBundle.instructors.get("instructor3OfCourse1");
    }
    
    @Test
    public void testAll() {
        
        ______TS("typical success case");
        
        AccountAttributes account = dataBundle.accounts.get("instructor3");
        InstructorCommentsPageData data = new InstructorCommentsPageData(account);
        
        boolean isViewingDraft = false;
        boolean isDisplayArchive = false;
        String courseId = course1.id;
        String courseName = course1.name;
        List<String> coursePaginationList = Arrays.asList(course1.id, course2.id);
        Map<String, List<CommentAttributes>> comments = new TreeMap<String, List<CommentAttributes>>();
        Map<String, List<Boolean>> commentModifyPermissions = new TreeMap<String, List<Boolean>>();
        
        CourseRoster roster = new CourseRoster(getStudentsInCourse(courseId), getInstructorsInCourse(courseId));
        List<FeedbackSessionAttributes> feedbackSessions = getFeedbackSessionsForCourse(courseId);
        int numberOfPendingComments = 0;
        
        // Setup instructor comments
        String giverEmail = instructor1.email;
        setInstructorComments(giverEmail, instructor1.email, courseId, comments, commentModifyPermissions);
        
        data.init(isViewingDraft, isDisplayArchive, courseId, courseName, coursePaginationList, 
                  comments, commentModifyPermissions, roster, feedbackSessions, numberOfPendingComments);
        
        /******************** Assertions for pageData data ********************/
        
        Map<String, List<CommentAttributes>> actualComments = data.getComments();
        Map<String, List<CommentAttributes>> expectedComments = comments;
        
        List<String> actualGivers = new ArrayList<String>();
        actualGivers.addAll(actualComments.keySet());
        List<String> expectedGivers = new ArrayList<String>();
        expectedGivers.addAll(expectedComments.keySet());
        assertTrue(TestHelper.isSameContentIgnoreOrder(expectedGivers, actualGivers));
        
        for (String email : expectedGivers) {
            assertEquals(expectedComments.get(email), actualComments.get(email));
        }

        assertEquals(courseId, data.getCourseId());
        assertEquals(courseName, data.getCourseName());
        assertTrue(coursePaginationList.equals(data.getCoursePaginationList()));
        assertEquals(feedbackSessions, data.getFeedbackSessions());
        String expectedNextPageLink = data.getInstructorCommentsLink() + "&courseid=" + course2.id;
        String expectedPreviousPageLink = "javascript:;";
        assertEquals(expectedNextPageLink, data.getNextPageLink());
        assertEquals(expectedPreviousPageLink, data.getPreviousPageLink());
        assertEquals(numberOfPendingComments, data.getNumberOfPendingComments());
        assertFalse(data.isDisplayArchive());
        assertFalse(data.isViewingDraft());
        
        /******************** Assertions for data structures ********************/
        
        String giverDetails = "You";
        List<Comment> commentRows = new ArrayList<Comment>();
        
        // Create first expected comment row
        String recipientDisplay = "<b>student2 In Course1 (Team 1.1, student2InCourse1@gmail.tmt)</b>";
        CommentAttributes comment = dataBundle.comments.get("comment1FromI3C1toS2C1");
        Comment commentRow = new Comment(comment, giverDetails, recipientDisplay);
        commentRow.withExtraClass("status_display-private");
        commentRow.setVisibilityIcon("");
        commentRow.setNotificationIcon(comment.isPendingNotification());
        commentRow.setEditDeleteEnabled(true);
        commentRow.setFromCommentsPage();
        commentRow.setPlaceholderNumComments();
        commentRows.add(commentRow);
        
        // Create second expected comment row
        recipientDisplay = "<b>All students in this course</b>";
        comment = dataBundle.comments.get("comment1FromI3C1toC1");
        commentRow = new Comment(comment, giverDetails, recipientDisplay);
        commentRow.withExtraClass("status_display-private");
        commentRow.setVisibilityIcon("");
        commentRow.setNotificationIcon(comment.isPendingNotification());
        commentRow.setEditDeleteEnabled(true);
        commentRow.setFromCommentsPage();
        commentRow.setPlaceholderNumComments();
        commentRows.add(commentRow);
        
        // Create expected comments table
        List<CommentsForStudentsTable> expectedCommentsForStudentsTables = new ArrayList<CommentsForStudentsTable>();
        CommentsForStudentsTable commentsForStudentsTable = new CommentsForStudentsTable(giverDetails, commentRows);
        commentsForStudentsTable.withExtraClass("giver_display-by-you");
        expectedCommentsForStudentsTables.add(commentsForStudentsTable);
        List<CommentsForStudentsTable> actualCommentsForStudentsTables = data.getCommentsForStudentsTables();
        
        assertEquals(expectedCommentsForStudentsTables.size(), actualCommentsForStudentsTables.size());
        for(int i = 0; i < expectedCommentsForStudentsTables.size(); i++) {
            isCommentsForStudentsTablesEqual(
                     expectedCommentsForStudentsTables.get(i), actualCommentsForStudentsTables.get(i));
        }
        
        ______TS("instructor is in second course page");
        
        courseId = course2.id;
        courseName = course2.name;
        
        comments = new TreeMap<String, List<CommentAttributes>>();
        commentModifyPermissions = new TreeMap<String, List<Boolean>>();
        
        roster = new CourseRoster(getStudentsInCourse(courseId), getInstructorsInCourse(courseId));
        feedbackSessions = getFeedbackSessionsForCourse(courseId);
        
        // Setup instructor comments
        giverEmail = instructor1.email;
        setInstructorComments(giverEmail, instructor1.email, courseId, comments, commentModifyPermissions);
        
        data.init(isViewingDraft, isDisplayArchive, courseId, courseName, coursePaginationList, 
                  comments, commentModifyPermissions, roster, feedbackSessions, numberOfPendingComments);
        
        /******************** Assertions for pageData data ********************/
        
        actualComments = data.getComments();
        assertEquals(1, actualComments.size());
        assertEquals(0, actualComments.get(COMMENT_GIVER_NAME_THAT_COMES_FIRST).size());
        assertEquals(courseId, data.getCourseId());
        assertEquals(courseName, data.getCourseName());
        assertEquals(coursePaginationList, data.getCoursePaginationList());
        assertEquals(feedbackSessions, data.getFeedbackSessions());
        expectedNextPageLink = "javascript:;";
        expectedPreviousPageLink = data.getInstructorCommentsLink() + "&courseid=" + course1.id;
        assertEquals(data.getNextPageLink(), expectedNextPageLink);
        assertEquals(data.getPreviousPageLink(), expectedPreviousPageLink);        
        assertEquals(numberOfPendingComments, data.getNumberOfPendingComments());
        assertFalse(data.isDisplayArchive());
        assertFalse(data.isViewingDraft());
        
        /******************** Assertions for data structures ********************/
        
        actualCommentsForStudentsTables = data.getCommentsForStudentsTables();
        assertEquals(1, actualCommentsForStudentsTables.size());
        assertEquals(0, actualCommentsForStudentsTables.get(0).getRows().size());
    }

    private boolean checkEqual(Object a, Object b) {
        if (a == null || b == null){
            return a == b;
        }
        return a.equals(b);
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

    private List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(String courseId) {
        List<FeedbackSessionAttributes> feedbackSessionsInCourse = new ArrayList<FeedbackSessionAttributes>();
        for (FeedbackSessionAttributes feedbackSession : dataBundle.feedbackSessions.values()) {
            if (feedbackSession.courseId.equals(courseId)) {
                feedbackSessionsInCourse.add(feedbackSession);
            }
        }
        return feedbackSessionsInCourse;
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
    
    private void isCommentsForStudentsTablesEqual(CommentsForStudentsTable expected, CommentsForStudentsTable actual) {
        assertTrue(checkEqual(expected.getGiverDetails(), actual.getGiverDetails()));
        assertTrue(checkEqual(expected.getExtraClass(), actual.getExtraClass()));
        List<Comment> expectedCommentRows = expected.getRows();
        List<Comment> actualCommentRows = actual.getRows();
        assertEquals(expectedCommentRows.size(), actualCommentRows.size());
        for(int i = 0; i < expectedCommentRows.size(); i++) {
            Comment expectedCommentRow = expectedCommentRows.get(i);
            Comment actualCommentRow = actualCommentRows.get(i);
            isCommentRowsEqual(expectedCommentRow, actualCommentRow);
        }
    }

    private void isCommentRowsEqual(Comment expected, Comment actual) {
        assertTrue(checkEqual(expected.getCreatedAt(), actual.getCreatedAt()));
        assertTrue(checkEqual(expected.getEditedAt(), actual.getEditedAt()));
        assertTrue(checkEqual(expected.getCommentText(), actual.getCommentText()));
        assertTrue(checkEqual(expected.getRecipientDisplay(), actual.getRecipientDisplay()));
        assertTrue(checkEqual(expected.getExtraClass(), actual.getExtraClass()));
        assertTrue(checkEqual(expected.isWithVisibilityIcon(), actual.isWithVisibilityIcon()));
        assertTrue(checkEqual(expected.getWhoCanSeeComment(), actual.getWhoCanSeeComment()));
        assertTrue(checkEqual(expected.isWithNotificationIcon(), actual.isWithNotificationIcon()));
        assertTrue(checkEqual(expected.isWithLinkToCommentsPage(), actual.isWithLinkToCommentsPage()));
        assertTrue(checkEqual(expected.getLinkToCommentsPage(), actual.getLinkToCommentsPage()));
        assertTrue(checkEqual(expected.isEditDeleteEnabled(), actual.isEditDeleteEnabled()));
        assertTrue(checkEqual(expected.isEditDeleteEnabledOnlyOnHover(), actual.isEditDeleteEnabledOnlyOnHover()));
        assertTrue(checkEqual(expected.isFromCommentsPage(), actual.isFromCommentsPage()));
        assertTrue(checkEqual(expected.getNumComments(), actual.getNumComments()));
        assertTrue(checkEqual(expected.getCommentId(), actual.getCommentId()));
        assertTrue(checkEqual(expected.getCourseId(), actual.getCourseId()));
        assertTrue(checkEqual(expected.isCommentForPerson(), actual.isCommentForPerson()));
        assertTrue(checkEqual(expected.isCommentForTeam(), actual.isCommentForTeam()));
        assertTrue(checkEqual(expected.isCommentForSection(), actual.isCommentForSection()));
        assertTrue(checkEqual(expected.isCommentForCourse(), actual.isCommentForCourse()));
        assertTrue(checkEqual(expected.getShowCommentToString(), actual.getShowCommentToString()));
        assertTrue(checkEqual(expected.getShowGiverNameToString(), actual.getShowGiverNameToString()));
        assertTrue(checkEqual(expected.getShowRecipientNameToString(), actual.getShowRecipientNameToString()));
        assertTrue(checkEqual(expected.isShowCommentToRecipient(), actual.isShowCommentToRecipient()));
        assertTrue(checkEqual(expected.isShowGiverNameToRecipient(), actual.isShowGiverNameToRecipient()));
        assertTrue(checkEqual(expected.isShowCommentToRecipientTeam(), actual.isShowCommentToRecipientTeam()));
        assertTrue(checkEqual(expected.isShowGiverNameToRecipientTeam(), actual.isShowGiverNameToRecipientTeam()));
        assertTrue(checkEqual(expected.isShowRecipientNameToRecipientTeam(), actual.isShowRecipientNameToRecipientTeam()));
        assertTrue(checkEqual(expected.isShowCommentToRecipientSection(), actual.isShowCommentToRecipientSection()));
        assertTrue(checkEqual(expected.isShowGiverNameToRecipientSection(), actual.isShowGiverNameToRecipientSection()));
        assertTrue(checkEqual(expected.isShowRecipientNameToRecipientSection(), actual.isShowRecipientNameToRecipientSection()));
        assertTrue(checkEqual(expected.isShowCommentToCourse(), actual.isShowCommentToCourse()));
        assertTrue(checkEqual(expected.isShowGiverNameToCourse(), actual.isShowGiverNameToCourse()));
        assertTrue(checkEqual(expected.isShowRecipientNameToCourse(), actual.isShowRecipientNameToCourse()));
        assertTrue(checkEqual(expected.isShowCommentToInstructors(), actual.isShowCommentToInstructors()));
        assertTrue(checkEqual(expected.isShowGiverNameToInstructors(), actual.isShowGiverNameToInstructors()));
        assertTrue(checkEqual(expected.isShowRecipientNameToInstructors(), actual.isShowRecipientNameToInstructors()));
    }

    private void setInstructorComments(
            String giverEmail, String currentInstructorEmail, String courseId, 
            Map<String, List<CommentAttributes>> comments, Map<String, List<Boolean>> commentModifyPermissions) {
        List<CommentAttributes> commentsForGiverList;
        List<Boolean> canModifyCommentList = new ArrayList<Boolean>();
        commentsForGiverList = getCommentsForGiverInCourse(giverEmail, courseId);
        for(int i = 0; i < commentsForGiverList.size(); i++) {
            canModifyCommentList.add(true);
        }
        String key = giverEmail;
        if (giverEmail.equals(currentInstructorEmail)) {
            key = COMMENT_GIVER_NAME_THAT_COMES_FIRST;
        }
        commentModifyPermissions.put(key, canModifyCommentList);
        comments.put(key, commentsForGiverList);   
    }
}
