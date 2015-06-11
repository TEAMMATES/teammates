package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.template.InstructorStudentsListFilterBox;
import teammates.ui.template.InstructorStudentsListFilterCourses;
import teammates.ui.template.InstructorStudentsListSearchBox;
import teammates.ui.template.InstructorStudentsListStudentsTableCourses;

public class InstructorStudentListPageData extends PageData {

    private InstructorStudentsListSearchBox searchBox;
    private InstructorStudentsListFilterBox filterBox;
    private List<InstructorStudentsListStudentsTableCourses> studentsTable;
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
        List<InstructorStudentsListFilterCourses> coursesForFilter =
                                        new ArrayList<InstructorStudentsListFilterCourses>();
        List<InstructorStudentsListStudentsTableCourses> coursesForStudentsTable =
                                        new ArrayList<InstructorStudentsListStudentsTableCourses>();
        for (CourseAttributes course : courses) {
            InstructorAttributes instructor = instructors.get(course.id);
            boolean isCourseArchived = isCourseArchived(course.id, instructor.googleId);
            boolean isCourseDisplayed = displayArchive || !isCourseArchived;
            if (isCourseDisplayed) {
                coursesForFilter.add(new InstructorStudentsListFilterCourses(course.id, course.name));
                coursesForStudentsTable.add(
                               new InstructorStudentsListStudentsTableCourses(isCourseArchived, course.id,
                                                                             course.name, account.googleId,
                                                                             numStudents.get(course.id),
                                                                             getInstructorCourseEnrollLink(course.id),
                               instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)));
            }
        }
        Assumption.assertEquals(coursesForFilter.size(), coursesForStudentsTable.size());
        this.filterBox = new InstructorStudentsListFilterBox(coursesForFilter, displayArchive);
        this.studentsTable = coursesForStudentsTable;
        this.numOfCourses = coursesForFilter.size();
    }
    
    public InstructorStudentsListSearchBox getSearchBox() {
        return searchBox;
    }

    public InstructorStudentsListFilterBox getFilterBox() {
        return filterBox;
    }
    
    public List<InstructorStudentsListStudentsTableCourses> getStudentsTable() {
        return studentsTable;
    }

    public int getNumOfCourses() {
        return numOfCourses;
    }

}
