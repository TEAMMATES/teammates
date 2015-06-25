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
    private List<CourseDetailsBundle> courses;
    
    private FeedbackSessionAttributes submittedSession;
    private FeedbackSessionAttributes pendingSession;
    private FeedbackSessionAttributes awaitingSession;
    
    private FeedbackSessionAttributes publishedSession;
    private FeedbackSessionAttributes closedSession;
    private FeedbackSessionAttributes submittedClosedSession;
    
    @Test
    public void allTests() {
        StudentHomePageData data = createData();
        testCourseTables(data.getCourseTables());
    }

    public void testCourseTables(List<CourseTable> courseTables) {
        assertEquals(courses.size(), courseTables.size());
        
        CourseDetailsBundle newCourse = courses.get(0);
        CourseTable newCourseTable = courseTables.get(0);
        
        testCourseTableMeta(newCourse.course, newCourseTable);
        testNewCourseTable(newCourse, newCourseTable);
        
        CourseDetailsBundle oldCourse = courses.get(1);
        CourseTable oldCourseTable = courseTables.get(1);
        
        testCourseTableMeta(oldCourse.course, oldCourseTable);
        testOldCourseTable(oldCourse, oldCourseTable);
    }
    
    private void testCourseTableMeta(CourseAttributes course, CourseTable table) {
        assertEquals(course.id, table.getCourseId());
        assertEquals(course.name, table.getCourseName());
        assertEquals(1, table.getButtons().size());
        testViewTeamButton(table.getButtons().get(0), course.id);
    }
    
    private void testViewTeamButton(ElementTag tag, String courseId) {
        assertEquals("View Team", tag.getContent());
        assertEquals(2, tag.getAttributes().size());
        assertTrue(tag.getAttributes().get("href").startsWith(Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE));
        assertTrue(tag.getAttributes().get("href").endsWith(Const.ParamsNames.COURSE_ID + "=" + courseId));
        assertEquals(Const.Tooltips.STUDENT_COURSE_DETAILS, tag.getAttributes().get("title"));
    }

    private void testNewCourseTable(CourseDetailsBundle newCourse, CourseTable courseTable) {
        assertEquals(newCourse.feedbackSessions.size(), courseTable.getRows().size());
        List<Map<String, Object>> sessions = courseTable.getRows();
        Map<String, Object> submittedRow = sessions.get(0);
        Map<String, Object> pendingRow = sessions.get(1);
        Map<String, Object> awaitingRow = sessions.get(2);
        
        testFeedbackSession(submittedRow, submittedSession,
                            Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_SUBMITTED, "Submitted");
        testFeedbackSession(pendingRow, pendingSession,
                            Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_PENDING, "Pending");
        testFeedbackSession(awaitingRow, awaitingSession,
                            Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_AWAITING, "Awaiting");
    }
    
    private void testOldCourseTable(CourseDetailsBundle oldCourse, CourseTable courseTable) {
        // Sessions in old course have multiple messages in tooltip as their end dates have passed.
        assertEquals(oldCourse.feedbackSessions.size(), courseTable.getRows().size());
        List<Map<String, Object>> sessions = courseTable.getRows();
        Map<String, Object> publishedRow = sessions.get(0);
        Map<String, Object> closedRow = sessions.get(1);
        Map<String, Object> submittedClosedRow = sessions.get(2);
        
        testFeedbackSession(publishedRow, publishedSession,
                            Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_PENDING
                                + Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_CLOSED
                                + Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_PUBLISHED,
                            "Published");
        testFeedbackSession(closedRow, closedSession,
                            Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_PENDING
                                + Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_CLOSED,
                            "Closed");
        testFeedbackSession(submittedClosedRow, submittedClosedSession,
                            Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_SUBMITTED
                                + Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_CLOSED,
                            "Closed");
    }
    
    private void testFeedbackSession(Map<String, Object> row, FeedbackSessionAttributes session,
            String expectedTooltip, String expectedStatus) {
        assertEquals(session.feedbackSessionName, row.get("name"));
        assertEquals(TimeHelper.formatTime(session.endTime), row.get("endTime"));
        assertEquals(expectedTooltip, row.get("tooltip"));
        assertEquals(expectedStatus, row.get("status"));
        //TODO: verify actions are correct after they are not merely a block of HTML
    }
    
    private StudentHomePageData createData() {
        // Courses
        CourseAttributes course1 = new CourseAttributes("course-id-1", "old-course");
        CourseAttributes course2 = new CourseAttributes("course-id-2", "new-course");
        
        // Feedback sessions
        submittedSession = createFeedbackSession("submitted session", -1, 1, 1);
        pendingSession = createFeedbackSession("pending session", -1, 1, 1);
        awaitingSession = createFeedbackSession("awaiting session", 1, 2, 1);
        publishedSession = createFeedbackSession("published sesssion", -1, -1, -1);
        closedSession = createFeedbackSession("closed session", -2, -1, 1);
        submittedClosedSession = createFeedbackSession("submitted closed session", -1, 0, 1);
        
        // Submission status
        Map<String, Boolean> sessionSubmissionStatusMap = new HashMap<String, Boolean>();
        sessionSubmissionStatusMap.put(course1.id + "%" + submittedSession.feedbackSessionName, true);
        sessionSubmissionStatusMap.put(course1.id + "%" + pendingSession.feedbackSessionName, false);
        sessionSubmissionStatusMap.put(course1.id + "%" + awaitingSession.feedbackSessionName, false);
        sessionSubmissionStatusMap.put(course2.id + "%" + publishedSession.feedbackSessionName, false);
        sessionSubmissionStatusMap.put(course2.id + "%" + closedSession.feedbackSessionName, false);
        sessionSubmissionStatusMap.put(course2.id + "%" + submittedClosedSession.feedbackSessionName, true);
        
        // Packing into bundles
        CourseDetailsBundle newCourseBundle = new CourseDetailsBundle(course1);
        newCourseBundle.feedbackSessions.add(new FeedbackSessionDetailsBundle(submittedSession));
        newCourseBundle.feedbackSessions.add(new FeedbackSessionDetailsBundle(pendingSession));
        newCourseBundle.feedbackSessions.add(new FeedbackSessionDetailsBundle(awaitingSession));
        
        CourseDetailsBundle oldCourseBundle = new CourseDetailsBundle(course2);
        oldCourseBundle.feedbackSessions.add(new FeedbackSessionDetailsBundle(publishedSession));
        oldCourseBundle.feedbackSessions.add(new FeedbackSessionDetailsBundle(closedSession));
        oldCourseBundle.feedbackSessions.add(new FeedbackSessionDetailsBundle(submittedClosedSession));
        
        courses = new ArrayList<CourseDetailsBundle>();
        courses.add(newCourseBundle);
        courses.add(oldCourseBundle);

        return new StudentHomePageData(new AccountAttributes(), courses, sessionSubmissionStatusMap);
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
}
