package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.StudentCommentsPageData;
import teammates.ui.template.Comment;
import teammates.ui.template.CommentsForStudentsTable;
import teammates.ui.template.CoursePagination;
import teammates.ui.template.FeedbackSessionRow;
import teammates.ui.template.QuestionTable;
import teammates.ui.template.FeedbackResponseComment;
import teammates.ui.template.ResponseRow;

public class StudentCommentsPageDataTest extends BaseTestCase{
    private static DataBundle dataBundle = getTypicalDataBundle();
    private static StudentCommentsPageData data;
    private static CourseAttributes course1;
    private static StudentAttributes student1;
    private static InstructorAttributes instructor1;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        course1 = dataBundle.courses.get("typicalCourse1");
        student1 = dataBundle.students.get("student1InCourse1");
        instructor1 = dataBundle.instructors.get("instructor1OfCourse1");
        
    }
    
    @Test
    public static void testAll() {
        
        ______TS("typical success case");
        
        AccountAttributes account = dataBundle.accounts.get("student1InCourse1");
        data = new StudentCommentsPageData(account);
        
        String courseId = course1.id;
        String courseName = course1.name;
        List<String> coursePaginationList = Arrays.asList(course1.id);
        List<CommentAttributes> comments = new ArrayList<CommentAttributes>();
        comments.add(dataBundle.comments.get("comment1FromI1C1toS1C1"));
        comments.add(dataBundle.comments.get("comment2FromI1C1toS1C1"));
        List<StudentAttributes> students = Arrays.asList(student1);
        List<InstructorAttributes> instructors = Arrays.asList(instructor1);
        CourseRoster roster = new CourseRoster(students, instructors);
        String studentEmail = student1.email;
        
        // create a single feedbackResultBundles for init
        Map<String, FeedbackSessionResultsBundle> feedbackResultBundles = 
                new HashMap<String, FeedbackSessionResultsBundle>();
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get("response1ForQ1S1C1"); 
        response.setId("1");
        List<FeedbackResponseAttributes> responses = Arrays.asList(response);
        Map<String, FeedbackQuestionAttributes> relevantQuestions = new HashMap<String, FeedbackQuestionAttributes>();
        FeedbackQuestionAttributes question = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        question.setId("1");
        relevantQuestions.put(question.getId(), question);
        Map<String, String> emailNameTable = new HashMap<String, String>();
        Map<String, String> emailLastNameTable = new HashMap<String, String>();
        Map<String, String> emailTeamNameTable = new HashMap<String, String>();
        Map<String, boolean[]> visibilityTable = new HashMap<String, boolean[]>();
        Map<String, List<FeedbackResponseCommentAttributes>> responseComments =
                new HashMap<String, List<FeedbackResponseCommentAttributes>>();
        emailNameTable.put(student1.email, student1.name);
        emailLastNameTable.put(student1.email, student1.lastName);
        emailTeamNameTable.put(student1.email, student1.team);
        boolean[] visibility = {true, true};
        visibilityTable.put(response.getId(), visibility);
        FeedbackSessionResponseStatus responseStatus = new FeedbackSessionResponseStatus();
        responseStatus.emailNameTable.put(student1.email, student1.name);
        responseStatus.emailTeamNameTable.put(student1.email, student1.team);
        FeedbackResponseCommentAttributes responseComment = 
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        List<FeedbackResponseCommentAttributes> responseCommentsList = Arrays.asList(responseComment);
        responseComments.put(response.getId(), responseCommentsList);
        boolean isComplete = true;
        FeedbackSessionResultsBundle results =
                new FeedbackSessionResultsBundle(
                        session, responses, relevantQuestions,
                        emailNameTable, emailLastNameTable, emailTeamNameTable,
                        visibilityTable, responseStatus, roster, responseComments, isComplete);
        feedbackResultBundles.put(session.feedbackSessionName, results);
        
        data.init(courseId, courseName, coursePaginationList, comments, roster, studentEmail, feedbackResultBundles);
        
        /**************************** Assertions ****************************/
        // Regular pageData data assertions
        String expectedCourseId = courseId;
        String actualCourseId = data.getCourseId();
        assertEquals(expectedCourseId, actualCourseId);
        String expectedCourseName = courseName;
        String actualCourseName = data.getCourseName();
        assertEquals(expectedCourseName, actualCourseName);
        String expectedPreviousPageLink;
        String expectedNextPageLink;
        expectedPreviousPageLink = expectedNextPageLink = "javascript:;";
        List<String> expectedCoursePaginationList = coursePaginationList;
        String expectedActiveCourse = course1.id;
        String expectedLink = Const.ActionURIs.STUDENT_COMMENTS_PAGE;
        expectedLink = Url.addParamToUrl(expectedLink, Const.ParamsNames.USER_ID, account.googleId);
        CoursePagination actualCoursePagination = data.getCoursePagination();
        assertEquals(expectedPreviousPageLink, actualCoursePagination.getPreviousPageLink());
        assertEquals(expectedNextPageLink, actualCoursePagination.getNextPageLink());
        assertEquals(expectedCoursePaginationList, actualCoursePagination.getCoursePaginationList());
        assertEquals(expectedActiveCourse, actualCoursePagination.getActiveCourse());
        assertEquals(expectedLink, actualCoursePagination.getUserCommentsLink());
        
        // JSTL data structure assertion: Comments for students tables
        assertEquals(1, data.getCommentsForStudentsTables().size());
        CommentsForStudentsTable actualCommentsForStudentsTable = data.getCommentsForStudentsTables().get(0);
        String expectedGiverDetails = "Instructor " + instructor1.name;
        String expectedExtraClass = "";
        assertEquals(expectedGiverDetails, actualCommentsForStudentsTable.getGiverDetails());
        assertEquals(expectedExtraClass, actualCommentsForStudentsTable.getExtraClass());
        
        List<Comment> actualCommentRows = actualCommentsForStudentsTable.getRows();
        List<Comment> expectedCommentRows = new ArrayList<Comment>();
        CommentAttributes expectedComment = comments.get(0);
        expectedCommentRows.add(new Comment(expectedComment, expectedGiverDetails, "you"));
        expectedComment = comments.get(1);
        expectedCommentRows.add(new Comment(expectedComment, expectedGiverDetails, "you"));
        assertEquals(expectedCommentRows.size(), actualCommentRows.size());
        
        for(int i = 0; i < expectedCommentRows.size(); i++) {
            checkCommentRowsEqual(expectedCommentRows.get(i), actualCommentRows.get(i));
        }
        
        // JSTL data structure assertions: Feedback session rows
        List<FeedbackSessionRow> actualFeedbackSessionRows = data.getFeedbackSessionRows();
        expectedGiverDetails = instructor1.email;
        FeedbackResponseComment expectedFeedbackResponseCommentRow = 
                new FeedbackResponseComment(responseComment, expectedGiverDetails);
        
        String giverName = student1.name + " (" + student1.team + ")";
        String recipientName = giverName;
        String feedbackResponse = response.getResponseDetails().getAnswerHtml(question.getQuestionDetails());
        
        ResponseRow expectedResponseRow = 
                new ResponseRow(giverName, recipientName, feedbackResponse, 
                                Arrays.asList(expectedFeedbackResponseCommentRow));
        
        QuestionTable expectedQuestionTable = 
                new QuestionTable(question.questionNumber, 
                                  results.getQuestionText(question.getId()), 
                                  question.getQuestionDetails()
                                                   .getQuestionAdditionalInfoHtml(question.questionNumber, ""), 
                                  Arrays.asList(expectedResponseRow));
        FeedbackSessionRow expectedFeedbackSessionRow = 
                new FeedbackSessionRow(
                        session.feedbackSessionName, session.courseId, Arrays.asList(expectedQuestionTable));
        assertEquals(1, actualFeedbackSessionRows.size());

        FeedbackSessionRow actualFeedbackSessionRow = actualFeedbackSessionRows.get(0);
        assertEquals(expectedFeedbackSessionRow.getFeedbackSessionName(), 
                     actualFeedbackSessionRow.getFeedbackSessionName());
        assertEquals(expectedFeedbackSessionRow.getCourseId(), actualFeedbackSessionRow.getCourseId());
        assertEquals(1, actualFeedbackSessionRow.getQuestionTables().size());
        
        QuestionTable actualQuestionTable = actualFeedbackSessionRow.getQuestionTables().get(0);
        assertEquals(expectedQuestionTable.getQuestionNumber(), actualQuestionTable.getQuestionNumber());
        assertEquals(expectedQuestionTable.getQuestionText(), actualQuestionTable.getQuestionText());
        assertEquals(expectedQuestionTable.getAdditionalInfo(), actualQuestionTable.getAdditionalInfo());
        assertEquals(1, actualQuestionTable.getResponseRows().size());
        
        ResponseRow actualResponseRow = actualQuestionTable.getResponseRows().get(0);
        assertEquals(expectedResponseRow.getGiverName(), actualResponseRow.getGiverName());
        assertEquals(expectedResponseRow.getRecipientName(), actualResponseRow.getRecipientName());
        assertEquals(expectedResponseRow.getResponse(), actualResponseRow.getResponse());
        assertEquals(1, actualResponseRow.getFeedbackResponseComments().size());
        
        FeedbackResponseComment actualFeedbackResponseCommentRow = 
                actualResponseRow.getFeedbackResponseComments().get(0);
        checkFeedbackResponseCommentRowsEqual(expectedFeedbackResponseCommentRow, actualFeedbackResponseCommentRow);
    }
    
    /** The two methods below check if the data structures are equal 
     *  Only asserts the attributes that are used in the comment tag 
     *  when accessing from StudentComments page*/
    private static void checkCommentRowsEqual(Comment expected, Comment actual) {
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getEditedAt(), actual.getEditedAt());
        assertEquals(expected.getCommentText(), actual.getCommentText());
        assertEquals(expected.getRecipientDisplay(), actual.getRecipientDisplay());
        assertEquals(expected.getExtraClass(), actual.getExtraClass());
        assertEquals(expected.isWithVisibilityIcon(), actual.isWithVisibilityIcon());
        assertEquals(expected.isWithNotificationIcon(), actual.isWithNotificationIcon());
        assertEquals(expected.isWithLinkToCommentsPage(), actual.isWithLinkToCommentsPage());
        assertEquals(expected.isEditDeleteEnabled(), actual.isEditDeleteEnabled());
    }
    
    private static void checkFeedbackResponseCommentRowsEqual(
            FeedbackResponseComment expected, FeedbackResponseComment actual) {
        assertEquals(expected.getExtraClass(), actual.getExtraClass());
        assertEquals(expected.getCommentId(), actual.getCommentId());
        assertEquals(expected.getGiverDisplay(), actual.getGiverDisplay());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getEditedAt(), actual.getEditedAt());
        assertEquals(expected.getCommentText(), actual.getCommentText());
        assertEquals(expected.isWithVisibilityIcon(), actual.isWithVisibilityIcon());
        assertEquals(expected.isWithNotificationIcon(), actual.isWithNotificationIcon());
        assertEquals(expected.isWithLinkToCommentsPage(), actual.isWithLinkToCommentsPage());
        assertEquals(expected.isEditDeleteEnabled(), actual.isEditDeleteEnabled());
    }
}
