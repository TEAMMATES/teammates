package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;

public class InstructorCommentsPageAction extends Action {

    private static final String COMMENT_PAGE_DISPLAY_ARCHIVE_SESSION = "comments_page_displayarchive";
    
    private InstructorCommentsPageData data;
    private String courseId;
    private String isDisplayArchivedCourseString;
    private Boolean isDisplayArchivedCourse;
    private Boolean isViewingDraft;
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        //COURSE_ID can be null, if viewed by Draft
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        //DISPLAY_ARCHIVE can be null. Its value can be retrieved from session
        isDisplayArchivedCourseString = getRequestParamValue(Const.ParamsNames.DISPLAY_ARCHIVE); 

        verifyAccessible();
        
        if(isDisplayArchivedCourseString != null){
            putDisplayArchivedOptionToSession();
        } else {
            getDisplayArchivedOptionFromSession();
        }
        
        //Load details of students and instructors once and pass it to callee methods
        //  (rather than loading them many times).
        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        
        List<String> coursePaginationList = new ArrayList<String>(); 
        String courseName = getCoursePaginationList(coursePaginationList);

        Map<String, List<CommentAttributes>> recipientToCommentsMap = getRecipientToCommentsMap();
        Map<String, FeedbackSessionResultsBundle> feedbackResultBundles = getFeedbackResultBundles(roster);
        
        data = new InstructorCommentsPageData(account);
        data.isViewingDraft = isViewingDraft;
        data.isDisplayArchive = isDisplayArchivedCourse;
        data.courseId = courseId;
        data.courseName = courseName;
        data.coursePaginationList = coursePaginationList;
        data.comments = recipientToCommentsMap;
        data.roster = roster;
        data.feedbackResultBundles = feedbackResultBundles;
        
        statusToAdmin = "instructorComments Page Load<br>" + 
                "Viewing <span class=\"bold\">" + account.googleId + "'s</span> comment records " +
                "for Course <span class=\"bold\">[" + courseId + "]</span>";
            
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COMMENTS, data);
    }

    private void verifyAccessible() {
        isViewingDraft = courseId == null;
        if(!isViewingDraft){//view by Course
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
            new GateKeeper().verifyAccessible(instructor, logic.getCourse(courseId));
        } else {//view by Draft
            courseId = "";
            new GateKeeper().verifyInstructorPrivileges(account);
        }
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
        for(CourseAttributes course : courses){
            if(course.id.equals(courseId)){
                courseName = course.id + " : " + course.name;
            }
            if(isDisplayArchivedCourse || !course.isArchived){
                coursePaginationList.add(course.id);
            }
        }
        return courseName;
    }

    private Map<String, List<CommentAttributes>> getRecipientToCommentsMap()
            throws EntityDoesNotExistException {
        List<CommentAttributes> comments;
        if(isViewingDraft){//for comment drafts
            comments = logic.getCommentDrafts(account.email);
        } else {//for normal comments
            comments = logic.getCommentsForGiverAndStatus(courseId, account.email, CommentStatus.FINAL);
        }
        //group data by recipients
        Map<String, List<CommentAttributes>> recipientToCommentsMap = new TreeMap<String, List<CommentAttributes>>();
        for(CommentAttributes comment : comments){
            for(String recipient : comment.recipients){
                List<CommentAttributes> commentList = recipientToCommentsMap.get(recipient);
                if(commentList == null){
                    commentList = new ArrayList<CommentAttributes>();
                    commentList.add(comment);
                    recipientToCommentsMap.put(recipient, commentList);
                } else {
                    commentList.add(comment);
                }
            }
        }
        //sort comments by created date
        for(List<CommentAttributes> commentList : recipientToCommentsMap.values()){
            java.util.Collections.sort(commentList);
        }
        return recipientToCommentsMap;
    }

    private Map<String, FeedbackSessionResultsBundle> getFeedbackResultBundles(CourseRoster roster)
            throws EntityDoesNotExistException {
        Map<String, FeedbackSessionResultsBundle> feedbackResultBundles = new HashMap<String, FeedbackSessionResultsBundle>();
        if(!isViewingDraft){
            List<FeedbackSessionAttributes> fsList = logic.getFeedbackSessionsForCourse(courseId);
            for(FeedbackSessionAttributes fs : fsList){
                FeedbackSessionResultsBundle bundle = 
                        logic.getFeedbackSessionResultsForInstructor(fs.feedbackSessionName, courseId, account.email, roster);
                if(bundle != null){
                    removeResponsesWithoutFeedbackResponseComment(bundle);
                    feedbackResultBundles.put(fs.feedbackSessionName, bundle);
                }
            }
        }
        return feedbackResultBundles;
    }

    private void removeResponsesWithoutFeedbackResponseComment(FeedbackSessionResultsBundle bundle) {
        List<FeedbackResponseAttributes> responsesWithFeedbackResponseComment = new ArrayList<FeedbackResponseAttributes>();
        for(FeedbackResponseAttributes fr: bundle.responses){
            List<FeedbackResponseCommentAttributes> frComment = bundle.responseComments.get(fr.getId());
            if(frComment != null && frComment.size() != 0){
                responsesWithFeedbackResponseComment.add(fr);
            }
        }
        bundle.responses = responsesWithFeedbackResponseComment;
    }
}
