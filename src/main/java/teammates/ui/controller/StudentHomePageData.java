package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;

public class StudentHomePageData extends PageData {
    
    public StudentHomePageData(AccountAttributes account) {
        super(account);
    }
    
    public List<CourseDetailsBundle> courses = new ArrayList<CourseDetailsBundle>();
    public Map<String, Boolean> sessionSubmissionStatusMap = new HashMap<String, Boolean>();
    public String eventualConsistencyCourse;
    
    /**
     * Returns the submission status of the student for a given feedback session.
     */
    public String getStudentStatusForSession(FeedbackSessionAttributes session){
        if(session.isOpened()) {
            Boolean hasSubmitted = sessionSubmissionStatusMap.get(session.courseId+"%"+session.feedbackSessionName);
            return hasSubmitted ? "Submitted" : "Pending";
        }
        
        if(session.isWaitingToOpen()) {
            return "Awaiting";
        }
        
        if (session.isPublished()) {
            return "Published";
        }
        
        return "Closed";
    }
    
    /**
     * @param submissionStatus Submission status of a student for a particular feedback session. 
     * 
     * @return The hover message to explain evaluation submission status.
     */
    public String getStudentHoverMessageForSession(FeedbackSessionAttributes session){
        String msg = "";
        
        Boolean isAwaiting = session.isWaitingToOpen();
        Boolean hasSubmitted = sessionSubmissionStatusMap.get(session.courseId+"%"+session.feedbackSessionName);
        
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
    
    
    public String getStudentCourseDetailsLink(String courseId){
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
     * @return The list of available actions for a specific feedback session.
     */
    public String getStudentFeedbackSessionActions(FeedbackSessionAttributes fs, int idx) {
        String keyOfMap = fs.courseId+"%"+fs.feedbackSessionName;
        boolean hasSubmitted = sessionSubmissionStatusMap.get(keyOfMap).booleanValue();
        
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
    
    /**
     * Obtain the course ID of the course that the student had just recently joined
     * so that it can be used to check for eventual consistency.
     */
    public void setEventualConsistencyCourse(String courseId) {
        eventualConsistencyCourse = courseId;
    }
}
