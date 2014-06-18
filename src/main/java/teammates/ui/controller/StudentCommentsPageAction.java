package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;

public class StudentCommentsPageAction extends Action {
    
    private StudentCommentsPageData data;
    private String courseId;
    private String previousPageLink = "javascript:;";
    private String nextPageLink = "javascript:;";
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        //check accessibility without courseId
        verifyBasicAccessibility();
        
        //COURSE_ID can be null, if viewed by default
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        if(courseId == null){
            courseId = "";
        }
        
        List<String> coursePaginationList = new ArrayList<String>(); 
        String courseName = getCoursePaginationList(coursePaginationList);
        
        //check accessibility with courseId
        if (!isJoinedCourse(courseId, account.googleId)) {
            return createPleaseJoinCourseResponse(courseId);
        }
        verifyAccessible();
        
        CourseRoster roster = null;
        Map<String, List<CommentAttributes>> recipientToCommentsMap = new HashMap<String, List<CommentAttributes>>();
        if(coursePaginationList.size() > 0){
            roster = new CourseRoster(
                    new StudentsDb().getStudentsForCourse(courseId),
                    new InstructorsDb().getInstructorsForCourse(courseId));

            recipientToCommentsMap = getRecipientToCommentsMap();
        }
        
        data = new StudentCommentsPageData(account);
        data.courseId = courseId;
        data.courseName = courseName;
        data.coursePaginationList = coursePaginationList;
        data.comments = recipientToCommentsMap;
        data.roster = roster;
        data.previousPageLink = previousPageLink;
        data.nextPageLink = nextPageLink;
        
        statusToAdmin = "studentComments Page Load<br>" + 
                "Viewing <span class=\"bold\">" + account.googleId + "'s</span> comment records " +
                "for Course <span class=\"bold\">[" + courseId + "]</span>";

        return createShowPageResult(Const.ViewURIs.STUDENT_COMMENTS, data);
    }
    
    private void verifyBasicAccessibility() {
        new GateKeeper().verifyLoggedInUserPrivileges();
        if(isUnregistered) { 
            // unregistered users cannot view the page
            throw new UnauthorizedAccessException("User is not registered");
        }
    }
    
    private void verifyAccessible() {
        new GateKeeper().verifyAccessible(
                logic.getStudentForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));
    }

    private String getCoursePaginationList(List<String> coursePaginationList) 
            throws EntityDoesNotExistException {
        String courseName = "";
        List<CourseAttributes> courses = logic.getCoursesForStudentAccount(account.googleId);
        java.util.Collections.sort(courses);
        for(int i = 0; i < courses.size(); i++){
            CourseAttributes course = courses.get(i);
            coursePaginationList.add(course.id);
            if(courseId == ""){
                //if courseId not provided, select the newest course
                courseId = course.id;
            }
            if(course.id.equals(courseId)){
                courseName = course.id + " : " + course.name;
                setPreviousPageLink(courses, i);
                setNextPageLink(courses, i);
            }
        }
        if(courseName.equals("")){
            throw new EntityDoesNotExistException(
                    "Trying to access a course that does not exist.");
        }
        return courseName;
    }
    
    private void setPreviousPageLink(List<CourseAttributes> courses, int currentIndex){
        if(currentIndex - 1 >= 0){
            CourseAttributes course = courses.get(currentIndex - 1);
            previousPageLink = new PageData(account).getInstructorCommentsLink() + "&courseid=" + course.id;
        }
    }
    
    private void setNextPageLink(List<CourseAttributes> courses, int currentIndex){
        if(currentIndex + 1 < courses.size()){
            CourseAttributes course = courses.get(currentIndex + 1);
            nextPageLink = new PageData(account).getInstructorCommentsLink() + "&courseid=" + course.id;
        }
    }

    private Map<String, List<CommentAttributes>> getRecipientToCommentsMap()
            throws EntityDoesNotExistException {
        StudentAttributes student = logic.getStudentForEmail(courseId, account.email);
        List<StudentAttributes> teammates = logic.getStudentsForTeam(student.team, courseId);
        List<String> teammatesEmails = getTeammatesEmails(teammates);
        
        List<CommentAttributes> comments = new ArrayList<CommentAttributes>();
        
        List<CommentAttributes> commentsForStudent = logic.getCommentsForReceiver(courseId, CommentRecipientType.PERSON, student.email);
        removeNonVisibleCommentsForStudent(commentsForStudent);
        appendCommentsFrom(commentsForStudent, comments);
        
        List<CommentAttributes> commentsForTeam = logic.getCommentsForCommentViewer(courseId, CommentRecipientType.TEAM);
        removeNonVisibleCommentsForTeam(commentsForTeam, student, teammatesEmails);
        appendCommentsFrom(commentsForTeam, comments);
        
        //TODO: handle comments for section
        List<CommentAttributes> commentsForCourse = logic.getCommentsForCommentViewer(courseId, CommentRecipientType.COURSE);
        removeNonVisibleCommentsForCourse(commentsForCourse, student);
        appendCommentsFrom(commentsForTeam, comments);
        
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
    
    private List<String> getTeammatesEmails(List<StudentAttributes> teammates) {
        List<String> teammatesEmails = new ArrayList<String>();
        for(StudentAttributes teammate : teammates){
            teammatesEmails.add(teammate.email);
        }
        return teammatesEmails;
    }

    private void removeNonVisibleCommentsForCourse(
            List<CommentAttributes> comments, StudentAttributes student) {
        Iterator<CommentAttributes> iter = comments.iterator();
        while(iter.hasNext()){
            CommentAttributes c = iter.next();
            if(!c.courseId.equals(student.course)){
                iter.remove();
            }
        }
    }
    
    private void removeNonVisibleCommentsForTeam(List<CommentAttributes> comments,
            StudentAttributes student, List<String> teammates) {
        Iterator<CommentAttributes> iter = comments.iterator();
        while(iter.hasNext()){
            CommentAttributes c = iter.next();
            if(c.recipientType == CommentRecipientType.PERSON){
                boolean isToRemove = true;
                for(String recipient : c.recipients){
                    if(teammates.contains(recipient)){
                        isToRemove = false;
                        break;
                    }
                }
                if(isToRemove){
                    iter.remove();
                }
            } else if(c.recipientType == CommentRecipientType.TEAM && !c.recipients.contains(student.team)){
                iter.remove();
            }
        }
    }

    private void removeNonVisibleCommentsForStudent(List<CommentAttributes> comments){
        Iterator<CommentAttributes> iter = comments.iterator();
        while(iter.hasNext()){
            CommentAttributes c = iter.next();
            if(!c.showCommentTo.contains(CommentRecipientType.PERSON)){
                iter.remove();
            }
        }
    }
    
    private void appendCommentsFrom(List<CommentAttributes> thisCommentList, List<CommentAttributes> thatCommentList){
        for(CommentAttributes c : thisCommentList){
            thatCommentList.add(c);
        }
    }
}
