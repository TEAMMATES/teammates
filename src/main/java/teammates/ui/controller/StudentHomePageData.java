package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.ui.template.CourseTable;
import teammates.ui.template.ElementTag;

public class StudentHomePageData extends PageData {
    
    public StudentHomePageData(AccountAttributes account) {
        super(account);
    }
    
    private List<CourseTable> courseTables;
    
    public void init(List<CourseDetailsBundle> courses, Map<String, Boolean> sessionSubmissionStatusMap) {
        setCourseTables(courses, sessionSubmissionStatusMap);
    }
    
    public List<CourseTable> getCourseTables() {
        return courseTables;
    }
    
    private void setCourseTables(List<CourseDetailsBundle> courses, Map<String, Boolean> sessionSubmissionStatusMap) {
        courseTables = new ArrayList<CourseTable>();
        for (CourseDetailsBundle courseDetails : courses) {
            courseTables.add(createCourseTable(courseDetails.course, courseDetails.feedbackSessions, sessionSubmissionStatusMap));
        }
    }
    
    private CourseTable createCourseTable(CourseAttributes course,
                                          List<FeedbackSessionDetailsBundle> feedbackSessions,
                                          Map<String, Boolean> sessionSubmissionStatusMap) {
        String courseId = course.id;
        return new CourseTable(course,
                               createCourseTableLinks(courseId),
                               createSessionRows(feedbackSessions, courseId, sessionSubmissionStatusMap));
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
    
    private List<Map<String, String>> createSessionRows(List<FeedbackSessionDetailsBundle> feedbackSessions,
            String courseId, Map<String, Boolean> sessionSubmissionStatusMap) {
        List<Map<String, String>> rows = new ArrayList<Map<String,String>>();
        
        int sessionIndex = 0;
        for (FeedbackSessionDetailsBundle session : feedbackSessions) {
            FeedbackSessionAttributes feedbackSession = session.feedbackSession;
            String sessionName = feedbackSession.feedbackSessionName;
            boolean hasSubmitted = sessionSubmissionStatusMap.get(courseId + "%" + sessionName);
            
            Map<String, String> columns = new HashMap<String, String>();
            columns.put("name", PageData.sanitizeForHtml(sessionName));
            columns.put("endTime", TimeHelper.formatTime(feedbackSession.endTime));
            columns.put("tooltip", getStudentHoverMessageForSession(feedbackSession, hasSubmitted));
            columns.put("status", getStudentStatusForSession(feedbackSession, hasSubmitted));
            columns.put("actions", getStudentFeedbackSessionActions(feedbackSession, sessionIndex, hasSubmitted));
            rows.add(columns);
        }
        
        return rows;
    }
    
    /**
     * Returns the submission status of the student for a given feedback session.
     * @param sessionSubmissionStatusMap 
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
     * @param sessionSubmissionStatusMap 
     * @param submissionStatus Submission status of a student for a particular feedback session. 
     * 
     * @return The hover message to explain evaluation submission status.
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
    
    private String getStudentFeedbackResponseEditLink(String courseId, String feedbackSessionName){
        String link = Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.FEEDBACK_SESSION_NAME,feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    
    private String getStudentFeedbackResultsLink(String courseId, String feedbackSessionName){
        String link = Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.FEEDBACK_SESSION_NAME,feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    

    /**
     * @param sessionSubmissionStatusMap 
     * @return The list of available actions for a specific feedback session.
     */
    private String getStudentFeedbackSessionActions(FeedbackSessionAttributes fs, int idx, boolean hasSubmitted) {
        
        String result = "<a class=\"btn btn-default btn-xs btn-tm-actions" + (fs.isPublished() ? "\"" : DISABLED) 
                + "href=\"" + getStudentFeedbackResultsLink(fs.courseId, fs.feedbackSessionName)
                + "\" " + "name=\"viewFeedbackResults"
                + idx + "\" " + " id=\"viewFeedbackResults" + idx + "\" "
                + "data-toggle=\"tooltip\" data-placement=\"top\""
                + "title=\"" + Const.Tooltips.FEEDBACK_SESSION_RESULTS + "\""
                + "role=\"button\">" + "View Responses</a>";
                
        if (hasSubmitted) {
            result += "<a class=\"btn btn-default btn-xs btn-tm-actions\" href=\""
                    + getStudentFeedbackResponseEditLink(fs.courseId, fs.feedbackSessionName)
                    + "\" " + "name=\"editFeedbackResponses" + idx
                    + "\" id=\"editFeedbackResponses" + idx + "\" "
                    + "data-toggle=\"tooltip\" data-placement=\"top\""
                    + "title=\"" + (fs.isOpened() ? 
                                Const.Tooltips.FEEDBACK_SESSION_EDIT_SUBMITTED_RESPONSE :
                                Const.Tooltips.FEEDBACK_SESSION_VIEW_SUBMITTED_RESPONSE) + "\""
                    + "role=\"button\">"
                    + (fs.isOpened() ? "Edit" : "View") + " Submission</a>";
        } else {
            String title = "";
            String linkText = "";
            if (!fs.isClosed()) {
                title = fs.isWaitingToOpen() ? Const.Tooltips.FEEDBACK_SESSION_AWAITING : Const.Tooltips.FEEDBACK_SESSION_SUBMIT;
                linkText = "Start Submission";
            } else {
                title = Const.Tooltips.FEEDBACK_SESSION_VIEW_SUBMITTED_RESPONSE;
                linkText = (fs.isOpened() ? "Edit" : "View") + " Submission";
            }
            result += "<a class=\"btn btn-default btn-xs btn-tm-actions" + (fs.isVisible() ? "\"" : DISABLED)
                    + "id=\"submitFeedback" + idx + "\" " + "href=\"" 
                    + getStudentFeedbackResponseEditLink(fs.courseId,
                        fs.feedbackSessionName) + "\" "
                    + "data-toggle=\"tooltip\" data-placement=\"top\""
                    + "title=\"" + title + "\""
                    + "role=\"button\">" + linkText + "</a>";    
        }
        
        return result;
    }
}
