package teammates.test.cases.ui.pagedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.SessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.StudentCommentsPageData;

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
        Map<String, FeedbackSessionResultsBundle> feedbackResultBundles = 
                new HashMap<String, FeedbackSessionResultsBundle>();
        FeedbackSessionAttributes feedbackSession = dataBundle.feedbackSessions.get("session1InCourse1");
        String fsName = feedbackSession.feedbackSessionName;
        FeedbackSessionResultsBundle feedbackSessionResultsBundle = 
                new FeedbackSessionResultsBundle(
                        feedbackSession, Arrays.asList(dataBundle.feedbackResponses.get("response1ForQ1S1C1")), 
                        null, null, null, null, null, null, roster, null);
    }
    
}
