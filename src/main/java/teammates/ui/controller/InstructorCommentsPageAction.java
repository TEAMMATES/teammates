package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
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

    public static final String COMMENT_PAGE_DISPLAY_ARCHIVE_SESSION = "comments_page_displayarchive";
    private static final Boolean IS_INCLUDE_RESPONSE_STATUS = true;
    
    private InstructorCommentsPageData data;
    private String courseId;
    private String isDisplayArchivedCourseString;
    private Boolean isDisplayArchivedCourse;
    private Boolean isViewingDraft;
    private String previousPageLink = "javascript:;";
    private String nextPageLink = "javascript:;";
    private InstructorAttributes instructor;
    private int numberOfPendingComments = 0;
    
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
        
        CourseRoster roster = null;
        Map<String, List<CommentAttributes>> giverEmailToCommentsMap = new HashMap<String, List<CommentAttributes>>();
        Map<String, FeedbackSessionResultsBundle> feedbackResultBundles = new HashMap<String, FeedbackSessionResultsBundle>();
        if(coursePaginationList.size() > 0){
        //Load details of students and instructors once and pass it to callee methods
        //  (rather than loading them many times).
            roster = new CourseRoster(
                    new StudentsDb().getStudentsForCourse(courseId),
                    new InstructorsDb().getInstructorsForCourse(courseId));

            giverEmailToCommentsMap = getGiverEmailToCommentsMap();
            feedbackResultBundles = getFeedbackResultBundles(roster);
        }
        
        data = new InstructorCommentsPageData(account);
        data.isViewingDraft = isViewingDraft;
        data.isDisplayArchive = isDisplayArchivedCourse;
        data.courseId = courseId;
        data.courseName = courseName;
        data.coursePaginationList = coursePaginationList;
        data.comments = giverEmailToCommentsMap;
        data.roster = roster;
        data.feedbackResultBundles = feedbackResultBundles;
        data.instructorEmail = instructor != null? instructor.email : "no-email";
        data.previousPageLink = previousPageLink;
        data.nextPageLink = nextPageLink;
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
            if(isDisplayArchivedCourse || !course.isArchived || course.id.equals(courseId)){
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
            if(isDisplayArchivedCourse || !course.isArchived){
                previousPageLink = new PageData(account).getInstructorCommentsLink() + "&courseid=" + course.id;
                break;
            }
        }
    }
    
    private void setNextPageLink(List<CourseAttributes> courses, int currentIndex){
        for(int i = currentIndex + 1; i < courses.size(); i++){
            CourseAttributes course = courses.get(i);
            if(isDisplayArchivedCourse || !course.isArchived){
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
            String key = comment.giverEmail.equals(instructor.email)? 
                    InstructorCommentsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST: comment.giverEmail;
            if(comment.isPending){
                numberOfPendingComments++;
            }

            List<CommentAttributes> commentList = giverEmailToCommentsMap.get(key);
            if(commentList == null){
                commentList = new ArrayList<CommentAttributes>();
                giverEmailToCommentsMap.put(key, commentList);
            }
            commentList.add(comment);
        }
        
        //sort comments by created date
        for(List<CommentAttributes> commentList : giverEmailToCommentsMap.values()){
            java.util.Collections.sort(commentList);
        }
        return giverEmailToCommentsMap;
    }

    private Map<String, FeedbackSessionResultsBundle> getFeedbackResultBundles(CourseRoster roster)
            throws EntityDoesNotExistException {
        Map<String, FeedbackSessionResultsBundle> feedbackResultBundles = new HashMap<String, FeedbackSessionResultsBundle>();
        if(!isViewingDraft){
            List<FeedbackSessionAttributes> fsList = logic.getFeedbackSessionsForCourse(courseId);
            for(FeedbackSessionAttributes fs : fsList){
                FeedbackSessionResultsBundle bundle = 
                        logic.getFeedbackSessionResultsForInstructor(
                                fs.feedbackSessionName, courseId, instructor.email, roster, !IS_INCLUDE_RESPONSE_STATUS);
                if(bundle != null){
                    removeQuestionsAndResponsesWithoutFeedbackResponseComment(bundle);
                    if(bundle.questions.size() != 0){
                        feedbackResultBundles.put(fs.feedbackSessionName, bundle);
                    }
                }
            }
        }
        return feedbackResultBundles;
    }

    private void removeQuestionsAndResponsesWithoutFeedbackResponseComment(FeedbackSessionResultsBundle bundle) {
        List<FeedbackResponseAttributes> responsesWithFeedbackResponseComment = new ArrayList<FeedbackResponseAttributes>();
        for(FeedbackResponseAttributes fr: bundle.responses){
            List<FeedbackResponseCommentAttributes> frComment = bundle.responseComments.get(fr.getId());
            if(frComment != null && frComment.size() != 0){
                responsesWithFeedbackResponseComment.add(fr);
                for(FeedbackResponseCommentAttributes frc: frComment){
                    if(frc.isPending && bundle.feedbackSession.isPublished()){
                        numberOfPendingComments++;
                    }
                }
            }
        }
        Map<String, FeedbackQuestionAttributes> questionsWithFeedbackResponseComment = new HashMap<String, FeedbackQuestionAttributes>();
        for(FeedbackResponseAttributes fr: responsesWithFeedbackResponseComment){
            FeedbackQuestionAttributes qn = bundle.questions.get(fr.feedbackQuestionId);
            if(questionsWithFeedbackResponseComment.get(qn.getId()) == null){
                questionsWithFeedbackResponseComment.put(qn.getId(), qn);
            }
        }
        bundle.questions = questionsWithFeedbackResponseComment;
        bundle.responses = responsesWithFeedbackResponseComment;
    }
}
