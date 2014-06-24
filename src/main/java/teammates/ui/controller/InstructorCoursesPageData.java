package teammates.ui.controller;

import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;

/**
 * This is the PageData object for the 'Courses' page 
 */
public class InstructorCoursesPageData extends PageData {
    
    public InstructorCoursesPageData(AccountAttributes account) {
        super(account);
    }
    
    /** Used when adding a course. Null if not adding a course. */
    public CourseAttributes newCourse;
    
    public HashMap<String, InstructorAttributes> instructors;
    
    /* List of details for all courses created by an instructor */
    public List<CourseDetailsBundle> allCourses;
    /* List of all archived courses from an instructor */
    public List<CourseAttributes> archivedCourses;
    
    /* Values to show in the form fields (in case reloading the page after a 
     *   failed attempt to create a course)*/
    public String courseIdToShow;
    public String courseNameToShow;

}
