package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

/**
 * Action: Showing the InstructorCommentsPage for an instructor
 */
public class InstructorCommentsPageAction extends Action {

    public static final String COMMENT_PAGE_DISPLAY_ARCHIVE_SESSION = "comments_page_displayarchive";
    
    private InstructorCommentsPageData data;
    private String courseId;
    private String isDisplayArchivedCourseString;
    private Boolean isDisplayArchivedCourse;
    private Boolean isViewingDraft;
    private String previousPageLink = "javascript:;";
    private String nextPageLink = "javascript:;";
    private InstructorAttributes instructor;
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        //COURSE_ID can be null, if viewed by Draft
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        //DISPLAY_ARCHIVE can be null. Its value can be retrieved from session
        isDisplayArchivedCourseString = getRequestParamValue(Const.ParamsNames.DISPLAY_ARCHIVE); 
        //TODO: a param for draft page

        verifyAccessible();
        
        if(isDisplayArchivedCourseString != null){
            putDisplayArchivedOptionToSession();
        } else {
            getDisplayArchivedOptionFromSession();
        }
        
        List<String> coursePaginationList = new ArrayList<String>(); 
        String courseName = getCoursePaginationList(coursePaginationList);
        
        data = new InstructorCommentsPageData(account);
        data.isViewingDraft = isViewingDraft;
        data.currentInstructor = instructor;
        data.isDisplayArchive = isDisplayArchivedCourse;
        data.courseId = courseId;
        data.courseName = courseName;
        
        CourseRoster roster = null;
        Map<String, List<CommentAttributes>> giverEmailToCommentsMap = new HashMap<String, List<CommentAttributes>>();
        List<FeedbackSessionAttributes> feedbackSessions = new ArrayList<FeedbackSessionAttributes>();
        if(coursePaginationList.size() > 0){
        //Load details of students and instructors once and pass it to callee methods
        //  (rather than loading them many times).
            roster = new CourseRoster(
                    logic.getStudentsForCourse(courseId),
                    logic.getInstructorsForCourse(courseId));

            //Prepare comments data
            giverEmailToCommentsMap = getGiverEmailToCommentsMap();
            feedbackSessions = getFeedbackSessions();
        }
        
        data.coursePaginationList = coursePaginationList;
        data.comments = giverEmailToCommentsMap;
        data.roster = roster;
        data.feedbackSessions = feedbackSessions;
        data.instructorEmail = instructor != null? instructor.email : "no-email";
        data.previousPageLink = previousPageLink;
        data.nextPageLink = nextPageLink;
        int numberOfPendingComments = 0;
        if(!courseId.isEmpty()){
            numberOfPendingComments = logic.getCommentsForSendingState(courseId, CommentSendingState.PENDING).size() 
                    + logic.getFeedbackResponseCommentsForSendingState(courseId, CommentSendingState.PENDING).size();
        }
        data.numberOfPendingComments = numberOfPendingComments;
        
        statusToAdmin = "instructorComments Page Load<br>" + 
                "Viewing <span class=\"bold\">" + account.googleId + "'s</span> comment records " +
                "for Course <span class=\"bold\">[" + courseId + "]</span>";
            
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COMMENTS, data);
    }

    private void verifyAccessible() {
        isViewingDraft = courseId == null;
        if(!isViewingDraft){//view by Course
            instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
            new GateKeeper().verifyAccessible(instructor, logic.getCourse(courseId));
        } else {//view by Draft
            courseId = "";
            new GateKeeper().verifyInstructorPrivileges(account);
        }
        isViewingDraft = false;//TODO: handle the draft page
    }

    private void getDisplayArchivedOptionFromSession() {
        Boolean isDisplayBooleanInSession = (Boolean) session.getAttribute(COMMENT_PAGE_DISPLAY_ARCHIVE_SESSION);
        isDisplayArchivedCourse = isDisplayBooleanInSession != null? isDisplayBooleanInSession: false;
    }

    private void putDisplayArchivedOptionToSession() {
        isDisplayArchivedCourse = Boolean.parseBoolean(isDisplayArchivedCourseString);
        session.setAttribute(COMMENT_PAGE_DISPLAY_ARCHIVE_SESSION, isDisplayArchivedCourse);
    }

    private String getCoursePaginationList(List<String> coursePaginationList) 
            throws EntityDoesNotExistException {
        String courseName = "";
        List<CourseAttributes> courses = logic.getCoursesForInstructor(account.googleId);
        java.util.Collections.sort(courses);
        for(int i = 0; i < courses.size(); i++){
            CourseAttributes course = courses.get(i);
            if(isDisplayArchivedCourse || !isCourseArchived(course, account.googleId) || course.id.equals(courseId)){
                if(courseId == ""){
                    courseId = course.id;
                    instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
                }
                coursePaginationList.add(course.id);
            }
            if(course.id.equals(courseId)){
                courseName = course.id + " : " + course.name;
                setPreviousPageLink(courses, i);
                setNextPageLink(courses, i);
            }
        }
        return courseName;
    }
    
    private void setPreviousPageLink(List<CourseAttributes> courses, int currentIndex){
        for(int i = currentIndex - 1; i >= 0; i--){
            CourseAttributes course = courses.get(i);
            
            if(isDisplayArchivedCourse || !isCourseArchived(course, account.googleId)){
                previousPageLink = new PageData(account).getInstructorCommentsLink() + "&courseid=" + course.id;
                break;
            }
        }
    }
    
    private void setNextPageLink(List<CourseAttributes> courses, int currentIndex){
        for(int i = currentIndex + 1; i < courses.size(); i++){
            CourseAttributes course = courses.get(i);
            
            if(isDisplayArchivedCourse || !isCourseArchived(course, account.googleId)){
                nextPageLink = new PageData(account).getInstructorCommentsLink() + "&courseid=" + course.id;
                break;
            }
        }
    }

    private Map<String, List<CommentAttributes>> getGiverEmailToCommentsMap()
            throws EntityDoesNotExistException {
        List<CommentAttributes> comments;
        if(isViewingDraft){//for comment drafts
            comments = logic.getCommentDrafts(account.email);
        } else {//for normal comments
            comments = logic.getCommentsForInstructor(instructor);
        }
        //group data by recipients
        Map<String, List<CommentAttributes>> giverEmailToCommentsMap = new TreeMap<String, List<CommentAttributes>>();
        for(CommentAttributes comment : comments){
            boolean isCurrentInstructorGiver = comment.giverEmail.equals(instructor.email);
            String key = isCurrentInstructorGiver? 
                    InstructorCommentsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST: comment.giverEmail;

            List<CommentAttributes> commentList = giverEmailToCommentsMap.get(key);
            if (commentList == null) {
                commentList = new ArrayList<CommentAttributes>();
                giverEmailToCommentsMap.put(key, commentList);
            }
            updateCommentList(comment, isCurrentInstructorGiver, commentList);
        }
        
        //sort comments by created date
        for(List<CommentAttributes> commentList : giverEmailToCommentsMap.values()){
            java.util.Collections.sort(commentList);
        }
        return giverEmailToCommentsMap;
    }

    private void updateCommentList(CommentAttributes comment, boolean isCurrentInstructorGiver, List<CommentAttributes> commentList) {
        if (!isViewingDraft && !isCurrentInstructorGiver) { 
            if (data.isInstructorAllowedForPrivilegeOnComment(comment, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS)) {
                commentList.add(comment);
            }
        } else {
            commentList.add(comment);
        }
    }

    private List<FeedbackSessionAttributes> getFeedbackSessions() {
            List<FeedbackSessionAttributes> fsList = logic.getFeedbackSessionsForCourse(courseId);
        return fsList;
    }
    
    private boolean isCourseArchived(CourseAttributes course, String googleId) {
        return logic.isCourseArchived(course.id, googleId);
    }
}
