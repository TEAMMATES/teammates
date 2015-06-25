package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.ui.template.CourseTable;
import teammates.ui.template.ElementTag;
import teammates.ui.template.StudentFeedbackSessionActions;

public class StudentHomePageData extends PageData {
    
    private List<CourseTable> courseTables;
    
    public StudentHomePageData(AccountAttributes account,
                               List<CourseDetailsBundle> courses,
                               Map<String, Boolean> sessionSubmissionStatusMap) {
        super(account);
        setCourseTables(courses, sessionSubmissionStatusMap);
    }
    
    public List<CourseTable> getCourseTables() {
        return courseTables;
    }
    
    private void setCourseTables(List<CourseDetailsBundle> courses, Map<String, Boolean> sessionSubmissionStatusMap) {
        courseTables = new ArrayList<CourseTable>();
        int startingSessionIdx = 0; // incremented for each session row without resetting between courses
        for (CourseDetailsBundle courseDetails : courses) {
            CourseTable courseTable = new CourseTable(courseDetails.course,
                                                      createCourseTableLinks(courseDetails.course.id),
                                                      createSessionRows(courseDetails.feedbackSessions,
                                                                        courseDetails.course.id,
                                                                        sessionSubmissionStatusMap,
                                                                        startingSessionIdx));
            startingSessionIdx += courseDetails.feedbackSessions.size();
            courseTables.add(courseTable);
        }
    }
    
    private List<ElementTag> createCourseTableLinks(String courseId) {
        List<ElementTag> links = new ArrayList<ElementTag>();
        links.add(new ElementTag(
            "View Team",
            "href", getStudentCourseDetailsLink(courseId),
            "title", Const.Tooltips.STUDENT_COURSE_DETAILS
        ));
        return links;
    }
    
    private List<Map<String, Object>> createSessionRows(List<FeedbackSessionDetailsBundle> feedbackSessions,
            String courseId, Map<String, Boolean> sessionSubmissionStatusMap, int sessionIdx) {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        
        for (FeedbackSessionDetailsBundle session : feedbackSessions) {
            FeedbackSessionAttributes feedbackSession = session.feedbackSession;
            String sessionName = feedbackSession.feedbackSessionName;
            boolean hasSubmitted = sessionSubmissionStatusMap.get(courseId + "%" + sessionName);
            
            Map<String, Object> columns = new HashMap<String, Object>();
            columns.put("name", PageData.sanitizeForHtml(sessionName));
            columns.put("endTime", TimeHelper.formatTime(feedbackSession.endTime));
            columns.put("tooltip", getStudentHoverMessageForSession(feedbackSession, hasSubmitted));
            columns.put("status", getStudentStatusForSession(feedbackSession, hasSubmitted));
            columns.put("actions", getStudentFeedbackSessionActions(feedbackSession, sessionIdx, hasSubmitted));
            columns.put("index", Integer.toString(sessionIdx));
            rows.add(columns);
            
            ++sessionIdx;
        }
        
        return rows;
    }
    
    /**
     * @param session The feedback session in question.
     * @param hasSubmitted Whether the student had submitted the session or not.
     * @return The submission status of the student for a given feedback session as a String.
     */
    private String getStudentStatusForSession(FeedbackSessionAttributes session, boolean hasSubmitted){
        if (session.isOpened()) {
            return hasSubmitted ? "Submitted" : "Pending";
        }
        
        if (session.isWaitingToOpen()) {
            return "Awaiting";
        }
        
        if (session.isPublished()) {
            return "Published";
        }
        
        return "Closed";
    }
    
    /**
     * @param session The feedback session in question.
     * @param hasSubmitted Whether the student had submitted the session or not.
     * @return The hover message to explain feedback session submission status.
     */
    private String getStudentHoverMessageForSession(FeedbackSessionAttributes session, boolean hasSubmitted){
        String msg = "";
        
        Boolean isAwaiting = session.isWaitingToOpen();
        
        if (isAwaiting) {
            msg += Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_AWAITING;
        } else if (hasSubmitted){
            msg += Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_SUBMITTED;
        } else {
            msg += Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_PENDING;
        }        
        if (session.isClosed()){
            msg += Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_CLOSED;
        }
        if (session.isPublished()) {
            msg += Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_PUBLISHED;
        }
        return msg;
    }
    
    
    private String getStudentCourseDetailsLink(String courseId){
        String link = Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE;
        link = addUserIdToUrl(link);
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        return link;
    }
    
    public String getStudentFeedbackResponseEditLink(String courseId, String feedbackSessionName){
        String link = Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.FEEDBACK_SESSION_NAME,feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getStudentFeedbackResultsLink(String courseId, String feedbackSessionName){
        String link = Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.FEEDBACK_SESSION_NAME,feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    

    /**
     * @param fs The feedback session in question.
     * @param idx The index of the session in the table.
     * @param hasSubmitted Whether the student had submitted the session or not.
     * @return The list of available actions for a specific feedback session.
     */
    private StudentFeedbackSessionActions getStudentFeedbackSessionActions(FeedbackSessionAttributes fs, int idx,
                                                                           boolean hasSubmitted) {
        return new StudentFeedbackSessionActions(this, fs, idx, hasSubmitted);
    }
}
