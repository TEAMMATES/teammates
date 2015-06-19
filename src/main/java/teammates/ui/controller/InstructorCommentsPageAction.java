package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;

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
    private InstructorAttributes instructor;
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        //COURSE_ID can be null, if viewed by Draft
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        //DISPLAY_ARCHIVE can be null. Its value can be retrieved from session
        isDisplayArchivedCourseString = getRequestParamValue(Const.ParamsNames.DISPLAY_ARCHIVE); 
        //TODO: a param for draft page

        verifyAccessible();
        
        if (isDisplayArchivedCourseString != null) {
            putDisplayArchivedOptionToSession();
        } else {
            getDisplayArchivedOptionFromSession();
        }
        
        List<String> coursePaginationList = new ArrayList<String>(); 
        String courseName = getCoursePaginationList(coursePaginationList);
        data = new InstructorCommentsPageData(account);
        
        CourseRoster roster = null;
        Map<String, List<CommentAttributes>> giverEmailToCommentsMap = new HashMap<String, List<CommentAttributes>>();
        Map<String, List<Boolean>> giverEmailToCanModifyCommentListMap = new HashMap<String, List<Boolean>>();
        List<FeedbackSessionAttributes> feedbackSessions = new ArrayList<FeedbackSessionAttributes>();
        if (coursePaginationList.size() > 0) {
        //Load details of students and instructors once and pass it to callee methods
        //  (rather than loading them many times).
            roster = new CourseRoster(logic.getStudentsForCourse(courseId), logic.getInstructorsForCourse(courseId));

            //Prepare comments data
            giverEmailToCommentsMap = getGiverEmailToCommentsMap();
            giverEmailToCanModifyCommentListMap = getGiverEmailToCanModifyCommentListMap(giverEmailToCommentsMap);
            feedbackSessions = getFeedbackSessions();
        }
        
        int numberOfPendingComments = 0;
        if (!courseId.isEmpty()) {
            numberOfPendingComments = logic.getCommentsForSendingState(courseId, CommentSendingState.PENDING).size() 
                    + logic.getFeedbackResponseCommentsForSendingState(courseId, CommentSendingState.PENDING).size();
        }
        
        statusToAdmin = "instructorComments Page Load<br>" + 
                "Viewing <span class=\"bold\">" + account.googleId + "'s</span> comment records " +
                "for Course <span class=\"bold\">[" + courseId + "]</span>";

        data.init(isViewingDraft, isDisplayArchivedCourse, courseId, courseName, coursePaginationList,
                  giverEmailToCommentsMap, giverEmailToCanModifyCommentListMap, roster, 
                  feedbackSessions, numberOfPendingComments);
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COMMENTS, data);
    }

    private void verifyAccessible() {
        isViewingDraft = courseId == null;
        if (!isViewingDraft) {//view by Course
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
        isDisplayArchivedCourse = isDisplayBooleanInSession != null ? isDisplayBooleanInSession : false;
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
        for (int i = 0; i < courses.size(); i++) {
            CourseAttributes course = courses.get(i);
            if (isDisplayArchivedCourse 
             || !isCourseArchived(course, account.googleId) 
             || course.id.equals(courseId)) {
                if (courseId == "") {
                    courseId = course.id;
                    instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
                }
                coursePaginationList.add(course.id);
            }
            if (course.id.equals(courseId)) {
                courseName = course.id + " : " + course.name;
            }
        }
        return courseName;
    }

    private Map<String, List<CommentAttributes>> getGiverEmailToCommentsMap() throws EntityDoesNotExistException {
        List<CommentAttributes> comments;
        if (isViewingDraft) {//for comment drafts
            comments = logic.getCommentDrafts(account.email);
        } else {//for normal comments
            comments = logic.getCommentsForInstructor(instructor);
        }

        //group data by recipients
        Map<String, List<CommentAttributes>> giverEmailToCommentsMap = new TreeMap<String, List<CommentAttributes>>();
        for (CommentAttributes comment : comments) {
            boolean isCurrentInstructorGiver = comment.giverEmail.equals(instructor.email);
            String key = isCurrentInstructorGiver ? 
                         InstructorCommentsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST : comment.giverEmail;

            List<CommentAttributes> commentList = giverEmailToCommentsMap.get(key);
            if (commentList == null) {
                commentList = new ArrayList<CommentAttributes>();
                giverEmailToCommentsMap.put(key, commentList);
            }
            updateCommentList(comment, isCurrentInstructorGiver, commentList);
        }
        
        //sort comments by created date
        for (List<CommentAttributes> commentList : giverEmailToCommentsMap.values()) {
            java.util.Collections.sort(commentList);
        }
        return giverEmailToCommentsMap;
    }

    private void updateCommentList(CommentAttributes comment,
                                   boolean isCurrentInstructorGiver,
                                   List<CommentAttributes> commentList) {
        if (!isViewingDraft && !isCurrentInstructorGiver) { 
            if (isInstructorAllowedForPrivilegeOnComment(comment, instructor, courseId, 
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS)) {
                commentList.add(comment);
            }
        } else {
            commentList.add(comment);
        }
    }
    
    private Map<String, List<Boolean>> getGiverEmailToCanModifyCommentListMap(
                                               Map<String, List<CommentAttributes>> comments) {
        Map<String, List<Boolean>> giverEmailToCanModifyCommentListMap = new TreeMap<String, List<Boolean>>();
        for (String giverEmail : comments.keySet()) {
            List<Boolean> canModifyCommentList = new ArrayList<Boolean>();
            for (CommentAttributes comment : comments.get(giverEmail)) {
                Boolean canModifyComment = isInstructorAllowedToModifyCommentInSection(comment); 
                canModifyCommentList.add(canModifyComment);
            }
            giverEmailToCanModifyCommentListMap.put(giverEmail, canModifyCommentList);
        }
        return giverEmailToCanModifyCommentListMap;
    }

    private List<FeedbackSessionAttributes> getFeedbackSessions() {
        List<FeedbackSessionAttributes> fsList = logic.getFeedbackSessionsForCourse(courseId);
        return fsList;
    }
    
    private boolean isCourseArchived(CourseAttributes course, String googleId) {
        return Logic.isCourseArchived(course.id, googleId);
    }
    
    private boolean isInstructorAllowedToModifyCommentInSection(CommentAttributes comment) {
        return instructor != null 
                       && (comment.giverEmail.equals(instructor.email)
                                   || isInstructorAllowedForPrivilegeOnComment(
                                              comment, instructor, courseId, 
                                              Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
    }
    
    private boolean isInstructorAllowedForPrivilegeOnComment(CommentAttributes comment, InstructorAttributes instructor, 
                                                             String courseId, String privilegeName) {
        // TODO: remember to come back and change this if later
        // CommentAttributes.recipients can have multiple later!!!

        if (instructor == null) {
            return false;
        }
        if (comment.recipientType == CommentParticipantType.COURSE) {
            return instructor.isAllowedForPrivilege(privilegeName);
        } else if (comment.recipientType == CommentParticipantType.SECTION) {
            String section = "";
            if (!comment.recipients.isEmpty()) {
                Iterator<String> iterator = comment.recipients.iterator();
                section = iterator.next();
            }
            return instructor.isAllowedForPrivilege(section, privilegeName);
        } else if (comment.recipientType == CommentParticipantType.TEAM) {
            String team = "";
            String section = "";
            if (!comment.recipients.isEmpty()) {
                Iterator<String> iterator = comment.recipients.iterator();
                team = iterator.next();
            }
            List<StudentAttributes> students = logic.getStudentsForTeam(team, courseId);
            if (!students.isEmpty()) {
                section = students.get(0).section;
            }
            return instructor.isAllowedForPrivilege(section, privilegeName);
        } else if (comment.recipientType == CommentParticipantType.PERSON) {
            String studentEmail = "";
            String section = "";
            if (!comment.recipients.isEmpty()) {
                Iterator<String> iterator = comment.recipients.iterator();
                studentEmail = iterator.next();
            }
            StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
            if (student != null) {
                section = student.section;
            }
            return instructor.isAllowedForPrivilege(section, privilegeName);
        } else {
            // TODO: implement this if instructor is later allowed to be added
            // to recipients
            return false;
        }
    }
}
