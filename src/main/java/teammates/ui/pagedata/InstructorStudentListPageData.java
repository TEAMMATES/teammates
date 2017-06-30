package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.util.SanitizationHelper;
import teammates.ui.datatransfer.InstructorStudentListPageCourseData;
import teammates.ui.template.InstructorStudentListFilterBox;
import teammates.ui.template.InstructorStudentListFilterCourse;
import teammates.ui.template.InstructorStudentListSearchBox;
import teammates.ui.template.InstructorStudentListStudentsTableCourse;

public class InstructorStudentListPageData extends PageData {

    private InstructorStudentListSearchBox searchBox;
    private InstructorStudentListFilterBox filterBox;
    private List<InstructorStudentListStudentsTableCourse> studentsTable;
    private int numOfCourses;

    public InstructorStudentListPageData(AccountAttributes account, String sessionToken, String searchKey,
                                         boolean displayArchive,
                                         List<InstructorStudentListPageCourseData> coursesToDisplay) {
        super(account, sessionToken);
        this.searchBox = new InstructorStudentListSearchBox(getInstructorSearchLink(), searchKey, account.googleId);
        List<InstructorStudentListFilterCourse> coursesForFilter = new ArrayList<>();
        List<InstructorStudentListStudentsTableCourse> coursesForStudentsTable = new ArrayList<>();
        for (InstructorStudentListPageCourseData islpcData : coursesToDisplay) {
            CourseAttributes course = islpcData.course;
            //TODO: [CourseAttribute] remove desanitization after data migration
            String courseName = SanitizationHelper.desanitizeIfHtmlSanitized(course.getName());
            coursesForFilter.add(new InstructorStudentListFilterCourse(course.getId(), courseName));

            InstructorStudentListStudentsTableCourse courseForStudentsTable =
                    new InstructorStudentListStudentsTableCourse(islpcData.isCourseArchived, course.getId(),
                                                                 courseName,
                                                                 account.googleId,
                                                                 getInstructorCourseEnrollLink(course.getId()),
                                                                 islpcData.isInstructorAllowedToModify);
            coursesForStudentsTable.add(courseForStudentsTable);
        }
        this.filterBox = new InstructorStudentListFilterBox(coursesForFilter, displayArchive);
        this.studentsTable = coursesForStudentsTable;
        this.numOfCourses = coursesForFilter.size();
    }

    public InstructorStudentListSearchBox getSearchBox() {
        return searchBox;
    }

    public InstructorStudentListFilterBox getFilterBox() {
        return filterBox;
    }

    public List<InstructorStudentListStudentsTableCourse> getStudentsTable() {
        return studentsTable;
    }

    public int getNumOfCourses() {
        return numOfCourses;
    }

}
