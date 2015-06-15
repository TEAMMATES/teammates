package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.controller.StudentHomePageData;
import teammates.ui.template.CourseTable;
import teammates.ui.template.ElementTag;

public class StudentHomePageDataTest {
    @Test
    public void testInit() throws Exception {
        // Data creation
        
        // Courses
        CourseAttributes course1 = new CourseAttributes("course-id-1", "course-name-1");
        CourseAttributes course2 = new CourseAttributes("course-id-2", "course-name-2");
        
        // Feedback sessions
        FeedbackSessionAttributes submittedSession = createFeedbackSession("submitted session", -1, 1, 1);
        FeedbackSessionAttributes pendingSession = createFeedbackSession("pending session", -1, 1, 1);
        FeedbackSessionAttributes awaitingSession = createFeedbackSession("awaiting session", 1, 2, 1);
        FeedbackSessionAttributes publishedSession = createFeedbackSession("published sesssion", -1, -1, -1);
        FeedbackSessionAttributes closedSession = createFeedbackSession("closed session", -2, -1, 1);
        FeedbackSessionAttributes submittedClosedSession = createFeedbackSession("submitted closed session", -1, 0, 1);
        
        // Submission status
        Map<String, Boolean> sessionSubmissionStatusMap = new HashMap<String, Boolean>();
        sessionSubmissionStatusMap.put(course1.id + "%" + submittedSession.feedbackSessionName, true);
        sessionSubmissionStatusMap.put(course1.id + "%" + pendingSession.feedbackSessionName, false);
        sessionSubmissionStatusMap.put(course1.id + "%" + awaitingSession.feedbackSessionName, false);
        sessionSubmissionStatusMap.put(course2.id + "%" + publishedSession.feedbackSessionName, false);
        sessionSubmissionStatusMap.put(course2.id + "%" + closedSession.feedbackSessionName, false);
        sessionSubmissionStatusMap.put(course2.id + "%" + submittedClosedSession.feedbackSessionName, true);
        
        // Packing into bundles
        CourseDetailsBundle courseDetails1 = new CourseDetailsBundle(course1);
        courseDetails1.feedbackSessions.add(new FeedbackSessionDetailsBundle(submittedSession));
        courseDetails1.feedbackSessions.add(new FeedbackSessionDetailsBundle(pendingSession));
        courseDetails1.feedbackSessions.add(new FeedbackSessionDetailsBundle(awaitingSession));
        
        CourseDetailsBundle courseDetails2 = new CourseDetailsBundle(course2);
        courseDetails2.feedbackSessions.add(new FeedbackSessionDetailsBundle(publishedSession));
        courseDetails2.feedbackSessions.add(new FeedbackSessionDetailsBundle(closedSession));
        courseDetails2.feedbackSessions.add(new FeedbackSessionDetailsBundle(submittedClosedSession));
        
        List<CourseDetailsBundle> courses = new ArrayList<CourseDetailsBundle>();
        courses.add(courseDetails1);
        courses.add(courseDetails2);
        
        
        StudentHomePageData data = new StudentHomePageData(new AccountAttributes());
        data.init(courses, sessionSubmissionStatusMap);
        
        
        // Assertions
        
        assertEquals(courses.size(), data.getCourseTables().size());
        
        CourseTable courseTable = data.getCourseTables().get(0);
        verifyCourseTable(courseTable, course1);
        
        assertEquals(courseDetails1.feedbackSessions.size(), courseTable.getRows().size());
        List<Map<String, String>> sessions = courseTable.getRows();
        Map<String, String> submittedRow = sessions.get(0);
        Map<String, String> pendingRow = sessions.get(1);
        Map<String, String> awaitingRow = sessions.get(2);
        
        verifyFeedbackSession(submittedRow, submittedSession,
                              Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_SUBMITTED, "Submitted");
        verifyFeedbackSession(pendingRow, pendingSession,
                              Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_PENDING, "Pending");
        verifyFeedbackSession(awaitingRow, awaitingSession,
                              Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_AWAITING, "Awaiting");
        
        courseTable = data.getCourseTables().get(1);
        verifyCourseTable(courseTable, course2);
        
        assertEquals(courseDetails2.feedbackSessions.size(), courseTable.getRows().size());
        sessions = courseTable.getRows();
        Map<String, String> publishedRow = sessions.get(0);
        Map<String, String> closedRow = sessions.get(1);
        Map<String, String> submittedClosedRow = sessions.get(2);
        
        verifyFeedbackSession(publishedRow, publishedSession,
                              Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_PENDING
                                  + Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_CLOSED
                                  + Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_PUBLISHED,
                              "Published");
        verifyFeedbackSession(closedRow, closedSession,
                              Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_PENDING
                                  + Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_CLOSED,
                              "Closed");
        verifyFeedbackSession(submittedClosedRow, submittedClosedSession,
                              Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_SUBMITTED
                                  + Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_CLOSED,
                              "Closed");
    }
    
    private FeedbackSessionAttributes createFeedbackSession(String name,
            int offsetStart, int offsetEnd, int offsetPublish) {
        FeedbackSessionAttributes session = new FeedbackSessionAttributes();
        session.feedbackSessionName = name;
        session.startTime = TimeHelper.getHoursOffsetToCurrentTime(offsetStart);
        session.endTime = TimeHelper.getHoursOffsetToCurrentTime(offsetEnd);
        session.resultsVisibleFromTime = TimeHelper.getHoursOffsetToCurrentTime(offsetPublish);
        session.sessionVisibleFromTime = TimeHelper.getHoursOffsetToCurrentTime(-1);
        return session;
    }
    
    private void verifyCourseTable(CourseTable table, CourseAttributes course) {
        assertEquals(course.id, table.getCourseId());
        assertEquals(course.name, table.getCourseName());
        assertEquals(1, table.getButtons().size());
        verifyViewTeamButton(table.getButtons().get(0), course.id);
    }
    
    private void verifyViewTeamButton(ElementTag tag, String courseId) {
        assertEquals("View Team", tag.getContent());
        assertEquals(2, tag.getAttributes().size());
        assertTrue(tag.getAttributes().get("href").startsWith(Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE));
        assertTrue(tag.getAttributes().get("href").endsWith(Const.ParamsNames.COURSE_ID + "=" + courseId));
        assertEquals(Const.Tooltips.STUDENT_COURSE_DETAILS, tag.getAttributes().get("title"));
    }
    
    private void verifyFeedbackSession(Map<String, String> row, FeedbackSessionAttributes session,
            String expectedTooltip, String expectedStatus) {
        assertEquals(session.feedbackSessionName, row.get("name"));
        assertEquals(TimeHelper.formatTime(session.endTime), row.get("endTime"));
        assertEquals(expectedTooltip, row.get("tooltip"));
        assertEquals(expectedStatus, row.get("status"));
        //TODO: verify actions are correct after they are not merely a block of HTML
    }
}
