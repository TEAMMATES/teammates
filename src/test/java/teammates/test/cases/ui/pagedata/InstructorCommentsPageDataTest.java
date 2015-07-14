package teammates.test.cases.ui.pagedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import teammates.ui.controller.PageData;
import teammates.ui.template.Comment;
import teammates.ui.template.CommentsForStudentsTable;

public class InstructorCommentsPageDataTest extends BaseTestCase {
    private static final String COMMENT_GIVER_NAME_THAT_COMES_FIRST = "0you";
    private static DataBundle dataBundle = getTypicalDataBundle();
    private static CourseAttributes course1;
    private static CourseAttributes course2;
    private static InstructorAttributes instructor1;
    private static InstructorAttributes instructor2;
    
    @BeforeClass
    public void classSetUp() throws Exception {
        printTestClassHeader();
        course1 = dataBundle.courses.get("typicalCourse1");
        course2 = dataBundle.courses.get("typicalCourse2");
        instructor1 = dataBundle.instructors.get("instructor3OfCourse1");
        instructor2 = dataBundle.instructors.get("instructor1OfCourse1");
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
        giverEmail = instructor2.email;
        setInstructorComments(giverEmail, instructor1.email, courseId, comments, commentModifyPermissions);
        
        data.init(isViewingDraft, isDisplayArchive, courseId, courseName, coursePaginationList, 
                  comments, commentModifyPermissions, roster, feedbackSessions, numberOfPendingComments);
        
        /******************** Assertions for pageData data ********************/
        assertFalse(data.isDisplayArchive());
        assertFalse(data.isViewingDraft());
        assertEquals(courseId, data.getCourseId());
        assertEquals(courseName, data.getCourseName());
        assertTrue(coursePaginationList.equals(data.getCoursePaginationList()));
        
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

        assertEquals(feedbackSessions, data.getFeedbackSessions());
        String expectedNextPageLink = data.getInstructorCommentsLink() + "&courseid=" + course2.id;
        String expectedPreviousPageLink = "javascript:;";
        
        assertEquals(expectedNextPageLink, data.getNextPageLink());
        assertEquals(expectedPreviousPageLink, data.getPreviousPageLink());
        
        assertEquals(numberOfPendingComments, data.getNumberOfPendingComments());
        
        /******************** Assertions for data structures ********************/
        List<CommentsForStudentsTable> expectedCommentsForStudentsTables =
                getCommentsForStudentsTables(comments, roster, data, commentModifyPermissions);
        List<CommentsForStudentsTable> actualCommentsForStudentsTables =
                data.getCommentsForStudentsTables();
        
        assertEquals(expectedCommentsForStudentsTables.size(), actualCommentsForStudentsTables.size());
        for(int i = 0; i < expectedCommentsForStudentsTables.size(); i++) {
            assertTrue(isCommentsForStudentsTablesEqual(
                               expectedCommentsForStudentsTables.get(i), actualCommentsForStudentsTables.get(i)));
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
        assertFalse(data.isDisplayArchive());
        assertFalse(data.isViewingDraft());
        assertEquals(courseId, data.getCourseId());
        assertEquals(courseName, data.getCourseName());
        assertEquals(coursePaginationList, data.getCoursePaginationList());
        
        actualComments = data.getComments();
        expectedComments = comments;
        
        actualGivers = new ArrayList<String>();
        actualGivers.addAll(actualComments.keySet());
        expectedGivers = new ArrayList<String>();
        expectedGivers.addAll(expectedComments.keySet());
        
        assertTrue(TestHelper.isSameContentIgnoreOrder(expectedGivers, actualGivers));
        for (String email : expectedGivers) {
            assertEquals(expectedComments.get(email), actualComments.get(email));
        }
        assertEquals(feedbackSessions, data.getFeedbackSessions());
        expectedNextPageLink = "javascript:;";
        expectedPreviousPageLink = data.getInstructorCommentsLink() + "&courseid=" + course1.id;
        assertEquals(data.getNextPageLink(), expectedNextPageLink);
        assertEquals(data.getPreviousPageLink(), expectedPreviousPageLink);
        
        assertEquals(numberOfPendingComments, data.getNumberOfPendingComments());
        
        /******************** Assertions for data structures ********************/
        expectedCommentsForStudentsTables =
                getCommentsForStudentsTables(comments, roster, data, commentModifyPermissions);
        actualCommentsForStudentsTables =
                data.getCommentsForStudentsTables();
        
        assertEquals(expectedCommentsForStudentsTables.size(), actualCommentsForStudentsTables.size());
        for(int i = 0; i < expectedCommentsForStudentsTables.size(); i++) {
            assertTrue(isCommentsForStudentsTablesEqual(
                               expectedCommentsForStudentsTables.get(i), actualCommentsForStudentsTables.get(i)));
        }
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
    
    private List<CommentsForStudentsTable> getCommentsForStudentsTables( 
            Map<String, List<CommentAttributes>> comments, CourseRoster roster, 
            InstructorCommentsPageData data, Map<String, List<Boolean>> commentModifyPermissions) {
        Map<String, String> giverEmailToGiverNameMap = getGiverEmailToGiverNameMap(comments, roster);
        List<CommentsForStudentsTable> commentsForStudentsTables = new ArrayList<CommentsForStudentsTable>();      
          
        for (String giverEmail : comments.keySet()) {
            String giverName = giverEmailToGiverNameMap.get(giverEmail);
            CommentsForStudentsTable table = 
                    new CommentsForStudentsTable(
                            giverName, createCommentRows(
                                               giverEmail, giverName, comments, data, commentModifyPermissions, roster));
            String extraClass;
            if (giverEmail.equals(COMMENT_GIVER_NAME_THAT_COMES_FIRST)) {
                extraClass = "giver_display-by-you";
            } else {
                extraClass = "giver_display-by-others";
            }
            table.withExtraClass(extraClass);
            commentsForStudentsTables.add(table);
        }
        return commentsForStudentsTables;
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

    private Map<String, String> getGiverEmailToGiverNameMap(
            Map<String, List<CommentAttributes>> comments, CourseRoster roster) {
    
        Map<String, String> giverEmailToGiverNameMap = new HashMap<String, String>();
        for (String giverEmail : comments.keySet()) {
    
            InstructorAttributes instructor = roster.getInstructorForEmail(giverEmail);
            String giverDisplay = giverEmail;
            if (giverEmail.equals(InstructorCommentsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST)) {
                giverDisplay = "You";
            } else if (instructor != null) {
                String title = instructor.displayedName;
                giverDisplay = title + " " + instructor.name;
            }
    
            giverEmailToGiverNameMap.put(giverEmail, giverDisplay);
        }
        return giverEmailToGiverNameMap;
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
    
    private String getRecipientNames(PageData data, String courseId, Set<String> recipients, CourseRoster roster) {
        StringBuilder namesStringBuilder = new StringBuilder();
        int i = 0;
        for (String recipient : recipients) {
            if (i == recipients.size() - 1 && recipients.size() > 1) {
                namesStringBuilder.append("and ");
            }
            StudentAttributes student = roster.getStudentForEmail(recipient);
            if (courseId.equals(recipient)) { 
                namesStringBuilder.append("<b>All students in this course</b>, ");
            } else if (student != null) {
                if (recipients.size() == 1) {
                    namesStringBuilder.append("<b>" + student.name + " (" + student.team + ", " + student.email + ")</b>, ");
                } else {
                    namesStringBuilder.append("<b>" + student.name + "</b>" + ", ");
                }
            } else {
                namesStringBuilder.append("<b>" + recipient + "</b>" + ", ");
            }
            i++;
        }
        String namesString = namesStringBuilder.toString();
        return data.removeEndComma(namesString);
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
    
    private void setInstructorComments(
            String giverEmail, String currentInstructorEmail, String courseId, 
            Map<String, List<CommentAttributes>> comments,
            Map<String, List<Boolean>> commentModifyPermissions) {
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
    
    private boolean isCommentsForStudentsTablesEqual(
            CommentsForStudentsTable expected, CommentsForStudentsTable actual) {
        boolean result = expected.getGiverDetails().equals(actual.getGiverDetails());
        result = result && expected.getExtraClass().equals(actual.getExtraClass());
        List<Comment> expectedCommentRows = expected.getRows();
        List<Comment> actualCommentRows = actual.getRows();
        result = result && (expectedCommentRows.size() == actualCommentRows.size());
        for(int i = 0; i < expectedCommentRows.size() && result; i++) {
            Comment expectedInstructorCommentsCommentRow =
                    (Comment) expectedCommentRows.get(i);
            Comment actualInstructorCommentsCommentRow =
                    (Comment) actualCommentRows.get(i);
            //TODO: Below
           // result = result && isCommentRowsEqual(
             //                          expectedInstructorCommentsCommentRow, actualInstructorCommentsCommentRow);
        }
        return result;
    }
    
    private boolean isCommentRowsEqual(Comment expected, Comment actual) {
        /*boolean result = expected.isInstructorAllowedToModifyCommentInSection() 
                         == actual.isInstructorAllowedToModifyCommentInSection();
        result = result && expected.getTypeOfPeopleCanViewComment().equals(actual.getTypeOfPeopleCanViewComment());
        result = result && expected.getEditedAt().equals(actual.getEditedAt());
        result = result && isVisibilityCheckboxesEqual(
                                   expected.getVisibilityCheckboxes(), actual.getVisibilityCheckboxes());
        result = result && expected.getShowCommentsTo().equals(actual.getShowCommentsTo());
        result = result && expected.getShowGiverNameTo().equals(actual.getShowGiverNameTo());
        result = result && expected.getShowRecipientNameTo().equals(actual.getShowRecipientNameTo());*/
        return true;
    }
    
    private List<Comment> createCommentRows(
            String giverEmail, String giverName, Map<String, List<CommentAttributes>> comments,
            InstructorCommentsPageData data, Map<String, List<Boolean>> commentModifyPermissions, 
            CourseRoster roster) {
        
        List<Comment> rows = new ArrayList<Comment>();
        List<CommentAttributes> commentsForGiver = comments.get(giverEmail);
        for (int i = 0; i < commentsForGiver.size(); i++) {            
            CommentAttributes comment = commentsForGiver.get(i);
            String recipientDetails = getRecipientNames(data, comment.courseId, comment.recipients, roster);
            Boolean isInstructorAllowedToModifyCommentInSection = commentModifyPermissions.get(giverEmail).get(i);
            String typeOfPeopleCanViewComment = data.getTypeOfPeopleCanViewComment(comment);
            Comment commentDiv = new Comment(comment, giverName, recipientDetails);
            String extraClass;
            if (comment.showCommentTo.isEmpty()) {
                extraClass = "status_display-private";
            } else {
                extraClass = "status_display-public";
            }
            commentDiv.withExtraClass(extraClass);
            commentDiv.setVisibilityIcon(typeOfPeopleCanViewComment);
            commentDiv.setNotificationIcon(comment.isPendingNotification());
            if (isInstructorAllowedToModifyCommentInSection) {
                commentDiv.setEditDeleteEnabled(true);
                commentDiv.setFromCommentsPage();
                commentDiv.setPlaceholderNumComments();
            }
            
            rows.add(commentDiv);
        }       
        return rows;
    }
}
