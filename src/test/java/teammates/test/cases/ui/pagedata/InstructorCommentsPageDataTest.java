package teammates.test.cases.ui.pagedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.InstructorCommentsPageData;
import teammates.ui.template.CommentRow;
import teammates.ui.template.CommentsForStudentsTable;
import teammates.ui.template.CoursePagination;

public class InstructorCommentsPageDataTest extends BaseTestCase {
    private static final String COMMENT_GIVER_NAME_THAT_COMES_FIRST = "0you";
    private static DataBundle dataBundle = getTypicalDataBundle();
    private static CourseAttributes course1;
    private static CourseAttributes course2;
    private static InstructorAttributes instructor1;
    
    @BeforeClass
    public void classSetup() {
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
        String courseId = course1.getId();
        String courseName = course1.getName();
        List<String> coursePaginationList = Arrays.asList(course1.getId(), course2.getId());
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
        assertEquals(courseId, data.getCourseId());
        assertEquals(courseName, data.getCourseName());
        CoursePagination actualCoursePagination = data.getCoursePagination();
        assertEquals("javascript:;", actualCoursePagination.getPreviousPageLink());
        assertEquals(data.getInstructorCommentsLink() + "&courseid=" + course2.getId(),
                     actualCoursePagination.getNextPageLink());
        assertEquals(coursePaginationList, actualCoursePagination.getCoursePaginationList());
        assertEquals(courseId, actualCoursePagination.getActiveCourse());
        assertEquals("active", actualCoursePagination.getActiveCourseClass());
        assertEquals(data.getInstructorCommentsLink(), actualCoursePagination.getUserCommentsLink());
        assertEquals(feedbackSessions, data.getFeedbackSessions());
        assertEquals(numberOfPendingComments, data.getNumberOfPendingComments());
        assertFalse(data.isDisplayArchive());
        assertFalse(data.isViewingDraft());
        
        /******************** Assertions for data structures ********************/
        
        String giverDetails = "You";
        List<CommentRow> commentRows = new ArrayList<CommentRow>();
        
        // Create first expected comment row
        String recipientDisplay = "student2 In Course1 (Team 1.1</td></div>'\", student2InCourse1@gmail.tmt)";
        CommentAttributes comment = dataBundle.comments.get("comment1FromI3C1toS2C1");
        CommentRow commentRow = new CommentRow(comment, giverDetails, recipientDisplay);
        commentRow.withExtraClass("status_display-private");
        commentRow.setVisibilityIcon("");
        commentRow.setNotificationIcon(comment.isPendingNotification());
        commentRow.setEditDeleteEnabled(true);
        commentRow.setFromCommentsPage();
        commentRow.setPlaceholderNumComments();
        commentRows.add(commentRow);
        
        // Create second expected comment row
        recipientDisplay = "all students in this course";
        comment = dataBundle.comments.get("comment1FromI3C1toC1");
        commentRow = new CommentRow(comment, giverDetails, recipientDisplay);
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
        for (int i = 0; i < expectedCommentsForStudentsTables.size(); i++) {
            checkCommentsForStudentsTablesEqual(
                     expectedCommentsForStudentsTables.get(i), actualCommentsForStudentsTables.get(i));
        }
        
        ______TS("instructor is in second course page");
        
        courseId = course2.getId();
        courseName = course2.getName();
        
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
        assertEquals(courseId, data.getCourseId());
        assertEquals(courseName, data.getCourseName());
        actualCoursePagination = data.getCoursePagination();
        assertEquals(data.getInstructorCommentsLink() + "&courseid=" + course1.getId(),
                     actualCoursePagination.getPreviousPageLink());
        assertEquals("javascript:;", actualCoursePagination.getNextPageLink());
        assertEquals(coursePaginationList, actualCoursePagination.getCoursePaginationList());
        assertEquals(courseId, actualCoursePagination.getActiveCourse());
        assertEquals("active", actualCoursePagination.getActiveCourseClass());
        assertEquals(data.getInstructorCommentsLink(), actualCoursePagination.getUserCommentsLink());
        assertEquals(coursePaginationList, data.getCoursePagination().getCoursePaginationList());
        assertEquals(feedbackSessions, data.getFeedbackSessions());
        assertEquals(numberOfPendingComments, data.getNumberOfPendingComments());
        assertFalse(data.isDisplayArchive());
        assertFalse(data.isViewingDraft());
        
        /******************** Assertions for data structures ********************/
        
        actualCommentsForStudentsTables = data.getCommentsForStudentsTables();
        assertEquals(1, actualCommentsForStudentsTables.size());
        assertEquals(0, actualCommentsForStudentsTables.get(0).getRows().size());
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
            if (feedbackSession.getCourseId().equals(courseId)) {
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
    
    private void checkCommentsForStudentsTablesEqual(CommentsForStudentsTable expected, CommentsForStudentsTable actual) {
        assertEquals(expected.getGiverDetails(), actual.getGiverDetails());
        assertEquals(expected.getExtraClass(), actual.getExtraClass());
        List<CommentRow> expectedCommentRows = expected.getRows();
        List<CommentRow> actualCommentRows = actual.getRows();
        assertEquals(expectedCommentRows.size(), actualCommentRows.size());
        for (int i = 0; i < expectedCommentRows.size(); i++) {
            CommentRow expectedCommentRow = expectedCommentRows.get(i);
            CommentRow actualCommentRow = actualCommentRows.get(i);
            checkCommentRowsEqual(expectedCommentRow, actualCommentRow);
        }
    }

    private void checkCommentRowsEqual(CommentRow expected, CommentRow actual) {
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getEditedAt(), actual.getEditedAt());
        assertEquals(expected.getCommentText(), actual.getCommentText());
        assertEquals(expected.getRecipientDisplay(), actual.getRecipientDisplay());
        assertEquals(expected.getExtraClass(), actual.getExtraClass());
        assertEquals(expected.isWithVisibilityIcon(), actual.isWithVisibilityIcon());
        assertEquals(expected.getWhoCanSeeComment(), actual.getWhoCanSeeComment());
        assertEquals(expected.isWithNotificationIcon(), actual.isWithNotificationIcon());
        assertEquals(expected.isWithLinkToCommentsPage(), actual.isWithLinkToCommentsPage());
        assertEquals(expected.getLinkToCommentsPage(), actual.getLinkToCommentsPage());
        assertEquals(expected.isEditDeleteEnabled(), actual.isEditDeleteEnabled());
        assertEquals(expected.isEditDeleteEnabledOnlyOnHover(), actual.isEditDeleteEnabledOnlyOnHover());
        assertEquals(expected.isFromCommentsPage(), actual.isFromCommentsPage());
        assertEquals(expected.getNumComments(), actual.getNumComments());
        assertEquals(expected.getCommentId(), actual.getCommentId());
        assertEquals(expected.getCourseId(), actual.getCourseId());
        assertEquals(expected.isCommentForPerson(), actual.isCommentForPerson());
        assertEquals(expected.isCommentForTeam(), actual.isCommentForTeam());
        assertEquals(expected.isCommentForSection(), actual.isCommentForSection());
        assertEquals(expected.isCommentForCourse(), actual.isCommentForCourse());
        assertEquals(expected.getShowCommentToString(), actual.getShowCommentToString());
        assertEquals(expected.getShowGiverNameToString(), actual.getShowGiverNameToString());
        assertEquals(expected.getShowRecipientNameToString(), actual.getShowRecipientNameToString());
        assertEquals(expected.isShowCommentToRecipient(), actual.isShowCommentToRecipient());
        assertEquals(expected.isShowGiverNameToRecipient(), actual.isShowGiverNameToRecipient());
        assertEquals(expected.isShowCommentToRecipientTeam(), actual.isShowCommentToRecipientTeam());
        assertEquals(expected.isShowGiverNameToRecipientTeam(), actual.isShowGiverNameToRecipientTeam());
        assertEquals(expected.isShowRecipientNameToRecipientTeam(), actual.isShowRecipientNameToRecipientTeam());
        assertEquals(expected.isShowCommentToRecipientSection(), actual.isShowCommentToRecipientSection());
        assertEquals(expected.isShowGiverNameToRecipientSection(), actual.isShowGiverNameToRecipientSection());
        assertEquals(expected.isShowRecipientNameToRecipientSection(), actual.isShowRecipientNameToRecipientSection());
        assertEquals(expected.isShowCommentToCourse(), actual.isShowCommentToCourse());
        assertEquals(expected.isShowGiverNameToCourse(), actual.isShowGiverNameToCourse());
        assertEquals(expected.isShowRecipientNameToCourse(), actual.isShowRecipientNameToCourse());
        assertEquals(expected.isShowCommentToInstructors(), actual.isShowCommentToInstructors());
        assertEquals(expected.isShowGiverNameToInstructors(), actual.isShowGiverNameToInstructors());
        assertEquals(expected.isShowRecipientNameToInstructors(), actual.isShowRecipientNameToInstructors());
    }

    private void setInstructorComments(
            String giverEmail, String currentInstructorEmail, String courseId,
            Map<String, List<CommentAttributes>> comments, Map<String, List<Boolean>> commentModifyPermissions) {
        List<CommentAttributes> commentsForGiverList;
        List<Boolean> canModifyCommentList = new ArrayList<Boolean>();
        commentsForGiverList = getCommentsForGiverInCourse(giverEmail, courseId);
        for (int i = 0; i < commentsForGiverList.size(); i++) {
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
