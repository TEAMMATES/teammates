package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CommentAttributes;
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
    private List<CommentAttributes> comments = new ArrayList<CommentAttributes>();
    
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
        if(coursePaginationList.size() > 0){
            roster = new CourseRoster(
                    new StudentsDb().getStudentsForCourse(courseId),
                    new InstructorsDb().getInstructorsForCourse(courseId));

            StudentAttributes student = roster.getStudentForEmail(account.email);
            comments = logic.getCommentsForStudent(student);
        }
        
        data = new StudentCommentsPageData(account);
        data.courseId = courseId;
        data.courseName = courseName;
        data.coursePaginationList = coursePaginationList;
        data.comments = comments;
        data.roster = roster;
        data.previousPageLink = previousPageLink;
        data.nextPageLink = nextPageLink;
        data.studentEmail = account.email;
        
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
            previousPageLink = new PageData(account).getStudentCommentsLink() + "&courseid=" + course.id;
        }
    }
    
    private void setNextPageLink(List<CourseAttributes> courses, int currentIndex){
        if(currentIndex + 1 < courses.size()){
            CourseAttributes course = courses.get(currentIndex + 1);
            nextPageLink = new PageData(account).getStudentCommentsLink() + "&courseid=" + course.id;
        }
    }
}
