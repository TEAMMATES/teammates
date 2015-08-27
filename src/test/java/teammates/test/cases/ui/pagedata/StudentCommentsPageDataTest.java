package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class StudentCommentsPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    private static StudentCommentsPageData data;
    private static CourseAttributes sampleCourse;
    private static StudentAttributes sampleStudent;
    private static InstructorAttributes sampleInstructor;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        sampleCourse = dataBundle.courses.get("typicalCourse1");
        sampleStudent = dataBundle.students.get("student1InCourse1");
        sampleInstructor = dataBundle.instructors.get("instructor1OfCourse1");
        
    }
    
    @Test
    public static void testAll() {
        
        ______TS("typical success case");
        
        AccountAttributes account = dataBundle.accounts.get("student1InCourse1");
        data = new StudentCommentsPageData(account);
        
        String courseId = sampleCourse.id;
        String courseName = sampleCourse.name;
        List<String> coursePaginationList = Arrays.asList(courseId);
        List<CommentAttributes> comments = new ArrayList<CommentAttributes>();
        comments.add(dataBundle.comments.get("comment1FromI1C1toS1C1"));
        comments.add(dataBundle.comments.get("comment2FromI1C1toS1C1"));
        List<StudentAttributes> students = Arrays.asList(sampleStudent);
        List<InstructorAttributes> instructors = Arrays.asList(sampleInstructor);
        CourseRoster roster = new CourseRoster(students, instructors);
        String studentEmail = sampleStudent.email;
        Map<String, FeedbackSessionResultsBundle> feedbackResultBundles = 
                new HashMap<String, FeedbackSessionResultsBundle>();
        FeedbackSessionResultsBundle bundle = getSingleFeedbackSessionResultsBundle(roster);
        feedbackResultBundles.put(bundle.feedbackSession.feedbackSessionName, bundle);
        
        data.init(courseId, courseName, coursePaginationList, comments, roster, studentEmail, feedbackResultBundles);
        
        /**************************** JUnit Comparisons ****************************/
        // Regular pageData data comparison
        checkRegularDataCorrect(account, courseId, courseName, coursePaginationList);
        
        // JSTL data structure comparison: Comments for students tables
        List<CommentsForStudentsTable> actualCommentsForStudentsTables = data.getCommentsForStudentsTables();
        assertEquals(1, data.getCommentsForStudentsTables().size());
        String expectedGiverDetails = sampleInstructor.displayedName + " " + sampleInstructor.name;
        CommentsForStudentsTable expectedCommentsForStudentsTable = 
                getCommentsForStudentsTable(expectedGiverDetails, studentEmail, comments, roster);
        CommentsForStudentsTable actualCommentsForStudentsTable = actualCommentsForStudentsTables.get(0);
        checkCommentsForStudentsTablesEqual(expectedCommentsForStudentsTable, actualCommentsForStudentsTable);
        
        // JSTL data structure comparison: Feedback session rows
        List<FeedbackSessionRow> actualFeedbackSessionRows = data.getFeedbackSessionRows();
        assertEquals(1, actualFeedbackSessionRows.size());
        FeedbackSessionRow expectedFeedbackSessionRow = getFeedbackSessionRow(bundle);
        FeedbackSessionRow actualFeedbackSessionRow = actualFeedbackSessionRows.get(0);
        checkFeedbackSessionRowsEqual(expectedFeedbackSessionRow, actualFeedbackSessionRow);
    }

    private static CommentsForStudentsTable getCommentsForStudentsTable(
            String giverDetails, String studentEmail, List<CommentAttributes> comments, CourseRoster roster) {
        List<Comment> commentRows = new ArrayList<Comment>();
        for (CommentAttributes comment : comments) {
            String recipientDetails = data.getRecipientNames(comment.recipients, sampleCourse.id, studentEmail, roster);
            commentRows.add(new Comment(comment, giverDetails, recipientDetails));
        }
        CommentsForStudentsTable commentsForStudentsTable = 
                new CommentsForStudentsTable(giverDetails, commentRows);
        return commentsForStudentsTable;
    }
    
    private static FeedbackSessionRow getFeedbackSessionRow(FeedbackSessionResultsBundle bundle) {
        List<QuestionTable> questionTables = new ArrayList<QuestionTable>();
        FeedbackSessionAttributes session = bundle.feedbackSession;
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionToResponsesMap =
                bundle.getQuestionResponseMap();
        for (FeedbackQuestionAttributes question : questionToResponsesMap.keySet()) {
            List<ResponseRow> responseRows = new ArrayList<ResponseRow>();
            List<FeedbackResponseAttributes> responses = questionToResponsesMap.get(question);
            for (FeedbackResponseAttributes response : responses) {
                List<FeedbackResponseComment> feedbackResponseCommentRows = new ArrayList<FeedbackResponseComment>();
                List<FeedbackResponseCommentAttributes> responseComments = 
                        bundle.responseComments.get(response.getId());
                for (FeedbackResponseCommentAttributes responseComment : responseComments) {
                    FeedbackResponseComment feedbackResponseCommentRow = 
                            new FeedbackResponseComment(responseComment, responseComment.giverEmail);
                    feedbackResponseCommentRows.add(feedbackResponseCommentRow);
                }
                String giverName = bundle.getGiverNameForResponse(response);
                String giverTeamName = bundle.getTeamNameForEmail(response.giverEmail);
                giverName = bundle.appendTeamNameToName(giverName, giverTeamName);
    
                String recipientName = bundle.getRecipientNameForResponse(response);
                String recipientTeamName = bundle.getTeamNameForEmail(response.recipientEmail);
                recipientName = bundle.appendTeamNameToName(recipientName, recipientTeamName);
                
                String responseText = response.getResponseDetails().getAnswerHtml(question.getQuestionDetails());
                
                ResponseRow responseRow = 
                        new ResponseRow(giverName, recipientName, responseText, feedbackResponseCommentRows);
                responseRows.add(responseRow);
            }
            int questionNumber = question.questionNumber;
            String questionText = bundle.getQuestionText(question.getId());
            String additionalInfo = 
                    question.getQuestionDetails().getQuestionAdditionalInfoHtml(question.questionNumber, "");
            QuestionTable questionTable = 
                    new QuestionTable(questionNumber, questionText, additionalInfo, responseRows);
            questionTables.add(questionTable);
        }
        
        FeedbackSessionRow sessionRow = 
                new FeedbackSessionRow(session.feedbackSessionName, session.courseId, questionTables);
    
        return sessionRow;
    }
    
    /** Creates a single FeedbackSessionResultsBundle object which comprises 
      * a single feedback session, a single question, a single response and a single response comment */
    private static FeedbackSessionResultsBundle getSingleFeedbackSessionResultsBundle(CourseRoster roster) {
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
        Map<String, Set<String>> sectionTeamNameTable = new HashMap<String, Set<String>>();
        Map<String, boolean[]> visibilityTable = new HashMap<String, boolean[]>();
        Map<String, List<FeedbackResponseCommentAttributes>> responseComments =
                new HashMap<String, List<FeedbackResponseCommentAttributes>>();
        emailNameTable.put(sampleStudent.email, sampleStudent.name);
        emailLastNameTable.put(sampleStudent.email, sampleStudent.lastName);
        emailTeamNameTable.put(sampleStudent.email, sampleStudent.team);
        boolean[] visibility = {true, true};
        visibilityTable.put(response.getId(), visibility);
        FeedbackSessionResponseStatus responseStatus = new FeedbackSessionResponseStatus();
        responseStatus.emailNameTable.put(sampleStudent.email, sampleStudent.name);
        responseStatus.emailTeamNameTable.put(sampleStudent.email, sampleStudent.team);
        FeedbackResponseCommentAttributes responseComment = 
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        List<FeedbackResponseCommentAttributes> responseCommentsList = Arrays.asList(responseComment);
        responseComments.put(response.getId(), responseCommentsList);
        boolean isComplete = true;
        return new FeedbackSessionResultsBundle(
                session, responses, relevantQuestions, emailNameTable, 
                emailLastNameTable, emailTeamNameTable, sectionTeamNameTable,
                visibilityTable, responseStatus, roster, responseComments, isComplete);
    }

    private static void checkRegularDataCorrect(
            AccountAttributes account, String courseId, String courseName, List<String> coursePaginationList) {
        String expectedCourseId = courseId;
        String actualCourseId = data.getCourseId();
        assertEquals(expectedCourseId, actualCourseId);
        
        String expectedCourseName = courseName;
        String actualCourseName = data.getCourseName();
        assertEquals(expectedCourseName, actualCourseName);
        
        int index = coursePaginationList.indexOf(courseId);
        String expectedPreviousPageLink = index > 0 ? coursePaginationList.get(index - 1) : "javascript:;";
        String expectedNextPageLink = 
                index < coursePaginationList.size() - 1 ? coursePaginationList.get(index + 1) : "javascript:;";
        List<String> expectedCoursePaginationList = coursePaginationList;
        String expectedActiveCourseClass = "active";
        String expectedActiveCourse = courseId;
        String expectedUserCommentsLink = Const.ActionURIs.STUDENT_COMMENTS_PAGE;
        expectedUserCommentsLink = Url.addParamToUrl(expectedUserCommentsLink, Const.ParamsNames.USER_ID, account.googleId);
        CoursePagination actualCoursePagination = data.getCoursePagination();
        assertEquals(expectedPreviousPageLink, actualCoursePagination.getPreviousPageLink());
        assertEquals(expectedNextPageLink, actualCoursePagination.getNextPageLink());
        assertEquals(expectedCoursePaginationList, actualCoursePagination.getCoursePaginationList());
        assertEquals(expectedActiveCourseClass, actualCoursePagination.getActiveCourseClass());
        assertEquals(expectedActiveCourse, actualCoursePagination.getActiveCourse());
        assertEquals(expectedUserCommentsLink, actualCoursePagination.getUserCommentsLink());
    }

    private static void checkCommentsForStudentsTablesEqual(CommentsForStudentsTable expectedCommentsForStudentsTable,
                                                            CommentsForStudentsTable actualCommentsForStudentsTable) {
        assertEquals(expectedCommentsForStudentsTable.getGiverDetails(),
                     actualCommentsForStudentsTable.getGiverDetails());
        assertEquals(expectedCommentsForStudentsTable.getExtraClass(), actualCommentsForStudentsTable.getExtraClass());
        List<Comment> actualCommentRows = actualCommentsForStudentsTable.getRows();
        List<Comment> expectedCommentRows = expectedCommentsForStudentsTable.getRows();
        assertEquals(expectedCommentRows.size(), actualCommentRows.size());
        
        for(int i = 0; i < expectedCommentRows.size(); i++) {
            checkCommentRowsEqual(expectedCommentRows.get(i), actualCommentRows.get(i));
        }
    }

    private static void checkFeedbackSessionRowsEqual(
            FeedbackSessionRow expectedFeedbackSessionRow, FeedbackSessionRow actualFeedbackSessionRow) {
        assertEquals(expectedFeedbackSessionRow.getFeedbackSessionName(), 
                     actualFeedbackSessionRow.getFeedbackSessionName());
        assertEquals(expectedFeedbackSessionRow.getCourseId(), actualFeedbackSessionRow.getCourseId());
        List<QuestionTable> actualQuestionTables = actualFeedbackSessionRow.getQuestionTables();
        List<QuestionTable> expectedQuestionTables = expectedFeedbackSessionRow.getQuestionTables();
        assertEquals(expectedQuestionTables.size(), actualQuestionTables.size());
        
        for (int i = 0; i < expectedQuestionTables.size(); i++) {
            checkQuestionTablesEqual(expectedQuestionTables.get(i), actualQuestionTables.get(i));
        }
    }

    private static void checkQuestionTablesEqual(
            QuestionTable expectedQuestionTable, QuestionTable actualQuestionTable) {
        assertEquals(expectedQuestionTable.getQuestionNumber(), actualQuestionTable.getQuestionNumber());
        assertEquals(expectedQuestionTable.getQuestionText(), actualQuestionTable.getQuestionText());
        assertEquals(expectedQuestionTable.getAdditionalInfo(), actualQuestionTable.getAdditionalInfo());        
        List<ResponseRow> actualResponseRows = actualQuestionTable.getResponseRows();
        List<ResponseRow> expectedResponseRows = expectedQuestionTable.getResponseRows();
        assertEquals(expectedResponseRows.size(), actualResponseRows.size());
        
        for (int i = 0; i < expectedResponseRows.size(); i++) {
            checkResponseRowsEqual(expectedResponseRows.get(i), actualResponseRows.get(i));
        }
    }

    private static void checkResponseRowsEqual(ResponseRow expectedResponseRow, ResponseRow actualResponseRow) {
        assertEquals(expectedResponseRow.getGiverName(), actualResponseRow.getGiverName());
        assertEquals(expectedResponseRow.getRecipientName(), actualResponseRow.getRecipientName());
        assertEquals(expectedResponseRow.getResponse(), actualResponseRow.getResponse());
        List<FeedbackResponseComment> actualFeedbackResponseCommentRows = 
                actualResponseRow.getFeedbackResponseComments();
        List<FeedbackResponseComment> expectedFeedbackResponseCommentRows = 
                expectedResponseRow.getFeedbackResponseComments();
        assertEquals(expectedFeedbackResponseCommentRows.size(), 
                     actualFeedbackResponseCommentRows.size());
        
        for (int i = 0; i < expectedFeedbackResponseCommentRows.size(); i++) {
            checkFeedbackResponseCommentRowsEqual(expectedFeedbackResponseCommentRows.get(i), 
                                                  actualFeedbackResponseCommentRows.get(i));
        }
    }
    
    /** The methods below check if the data structures are equal 
     *  Only asserts the attributes that are used in the respective comment tags 
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
