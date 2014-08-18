package teammates.ui.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorStudentListPageAction extends Action {
    
    private InstructorStudentListPageData data;
    

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyInstructorPrivileges(account);
        
        String searchKey = getRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        Boolean displayArchive = getRequestParamAsBoolean(Const.ParamsNames.DISPLAY_ARCHIVE);
        
        data = new InstructorStudentListPageData(account);
        data.instructors = new HashMap<String, InstructorAttributes>();
        data.numStudents = new HashMap<String, String>();
        data.courses = logic.getCoursesForInstructor(account.googleId);
        for (CourseAttributes course : data.courses) {     
            InstructorAttributes instructor = logic.getInstructorForGoogleId(course.id, account.googleId);
            data.instructors.put(course.id, instructor);
            int numStudentsInCourse = logic.getStudentsForCourse(course.id).size();
            data.numStudents.put(course.id, String.valueOf(numStudentsInCourse));
        }
        
        Collections.sort(data.courses, new Comparator<CourseAttributes>(){
            @Override
            public int compare(CourseAttributes c1, CourseAttributes c2) {
                return c1.createdAt.compareTo(c2.createdAt);
            }
        });
        data.searchKey = searchKey;
        data.displayArchive = displayArchive;
        
        if(data.courses.size() == 0){
            statusToUser.add(Const.StatusMessages.INSTRUCTOR_NO_COURSE_AND_STUDENTS);
        }
           
        statusToAdmin = "instructorStudentList Page Load<br>" + "Total Courses: " + data.courses.size();
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST, data);
        return response;

    }

}
