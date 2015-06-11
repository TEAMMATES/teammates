package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.ui.template.InstructorStudentsListFilterBox;
import teammates.ui.template.InstructorStudentsListFilterCourses;
import teammates.ui.template.InstructorStudentsListSearchBox;

public class InstructorStudentListPageData extends PageData {

    private InstructorStudentsListSearchBox searchBox;
    private InstructorStudentsListFilterBox filterBox;
    private int numOfCourses;
    public List<CourseAttributes> courses;
    public Boolean displayArchive;
    public String searchKey;
    
    public InstructorStudentListPageData(AccountAttributes account, String searchKey,
                                         boolean displayArchive,
                                         Map<String, InstructorAttributes> instructors,
                                         Map<String, String> numStudents,
                                         List<CourseAttributes> courses) {
        super(account);
        this.courses = courses;
        this.displayArchive = displayArchive;
        this.searchKey = searchKey;
        init(account, searchKey, displayArchive, instructors, numStudents, courses);
    }
    
    private void init(AccountAttributes account, String searchKey, boolean displayArchive,
                      Map<String, InstructorAttributes> instructors, Map<String, String> numStudents,
                      List<CourseAttributes> courses) {
        this.searchBox = new InstructorStudentsListSearchBox(getInstructorSearchLink(), searchKey, account.googleId);
        List<InstructorStudentsListFilterCourses> coursesList = new ArrayList<InstructorStudentsListFilterCourses>();
        for (CourseAttributes course : courses) {
            InstructorAttributes instructor = instructors.get(course.id);
            boolean isCourseNotArchived = displayArchive || !isCourseArchived(course.id, instructor.googleId);
            if (isCourseNotArchived) {
                coursesList.add(new InstructorStudentsListFilterCourses(course.id, course.name));
            }
        }
        this.filterBox = new InstructorStudentsListFilterBox(coursesList, displayArchive);
        this.numOfCourses = coursesList.size();
    }
    
    public InstructorStudentsListSearchBox getSearchBox() {
        return searchBox;
    }

    public InstructorStudentsListFilterBox getFilterBox() {
        return filterBox;
    }
    
    public int getNumOfCourses() {
        return numOfCourses;
    }

}
