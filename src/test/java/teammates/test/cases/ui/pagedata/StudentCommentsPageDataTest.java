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
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.StudentCommentsPageData;
import teammates.ui.template.CoursePagination;

public class StudentCommentsPageDataTest extends BaseTestCase{
    private static DataBundle dataBundle = getTypicalDataBundle();
    private static StudentCommentsPageData data;
    private static CourseAttributes course1;
    private static StudentAttributes student1;
    private static InstructorAttributes instructor1;
    private static final int EMAIL_NAME_PAIR = 0;
    private static final int EMAIL_LASTNAME_PAIR = 1;
    private static final int EMAIL_TEAMNAME_PAIR = 2;
    
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
        
        // create feedbackResultBundles for init (creates a single feedbackResultBundle)
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
    }
}
