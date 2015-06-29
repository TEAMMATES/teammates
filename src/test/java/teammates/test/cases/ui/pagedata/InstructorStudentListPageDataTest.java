package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.util.Sanitizer;
import teammates.ui.controller.InstructorStudentListPageData;
import teammates.ui.datatransfer.InstructorStudentListPageCourseData;
import teammates.ui.template.InstructorStudentListFilterBox;
import teammates.ui.template.InstructorStudentListFilterCourse;
import teammates.ui.template.InstructorStudentListSearchBox;
import teammates.ui.template.InstructorStudentListStudentsTableCourse;

public class InstructorStudentListPageDataTest {

    private InstructorStudentListPageData islpd;

    private AccountAttributes acct;
    private String searchKey;
    private boolean displayArchive;
    private Map<String, String> numStudents;
    private List<InstructorStudentListPageCourseData> coursesToDisplay;

    private CourseAttributes sampleCourse;
    private boolean isCourseArchived;
    private boolean isInstructorAllowedToModify;

    @Test
    public void allTests() {
        islpd = initializeDataWithSearchKey();
        testSearchBox(islpd.getSearchBox());
        testFilterBox(islpd.getFilterBox());
        testStudentsTable(islpd.getStudentsTable());
        testNumOfCourses(islpd.getNumOfCourses());
        
        islpd = initializeDataWithNoSearchKey();
        testNullSearchKeyHandler(islpd);
    }

    private InstructorStudentListPageData initializeDataWithSearchKey() {
        acct = new AccountAttributes();
        acct.googleId = "valid.id"; // only googleId is used

        searchKey = "<script>alert(\"A search key\");</script>";
        displayArchive = false;

        // only course ID and name are used
        sampleCourse = new CourseAttributes();
        sampleCourse.id = "validCourseId";
        sampleCourse.name = "Sample course name";

        numStudents = new HashMap<String, String>();
        numStudents.put(sampleCourse.id, "5");

        isCourseArchived = false;
        isInstructorAllowedToModify = true;

        coursesToDisplay = new ArrayList<InstructorStudentListPageCourseData>();
        coursesToDisplay.add(new InstructorStudentListPageCourseData(sampleCourse, isCourseArchived,
                                                                     isInstructorAllowedToModify));
        return new InstructorStudentListPageData(acct, searchKey, displayArchive, numStudents, coursesToDisplay);
    }
    
    private InstructorStudentListPageData initializeDataWithNoSearchKey() {
        searchKey = null;
        return new InstructorStudentListPageData(acct, searchKey, displayArchive, numStudents, coursesToDisplay);
    }
    
    private void testSearchBox(InstructorStudentListSearchBox searchBox) {
        assertEquals(acct.googleId, searchBox.getGoogleId());
        assertEquals(Sanitizer.sanitizeForHtml(searchKey), searchBox.getSearchKey());
        assertEquals(islpd.getInstructorSearchLink(), searchBox.getInstructorSearchLink());
    }

    private void testFilterBox(InstructorStudentListFilterBox filterBox) {
        assertEquals(displayArchive, filterBox.isDisplayArchive());

        // sample data has only one course
        InstructorStudentListFilterCourse course = filterBox.getCourses().get(0);
        assertEquals(sampleCourse.id, course.getCourseId());
        assertEquals(sampleCourse.name, course.getCourseName());
    }

    private void testStudentsTable(List<InstructorStudentListStudentsTableCourse> studentsTable) {
        // sample data has only one course
        InstructorStudentListStudentsTableCourse course = studentsTable.get(0);
        assertEquals(sampleCourse.id, course.getCourseId());
        assertEquals(sampleCourse.name, course.getCourseName());
        assertEquals(acct.googleId, course.getGoogleId());
        assertEquals(islpd.getInstructorCourseEnrollLink(sampleCourse.id), course.getInstructorCourseEnrollLink());
        assertEquals(numStudents.get(sampleCourse.id), course.getNumStudents());
        assertEquals(isCourseArchived, course.isCourseArchived());
        assertEquals(isInstructorAllowedToModify, course.isInstructorAllowedToModify());
    }

    private void testNumOfCourses(int numOfCourses) {
        assertEquals(coursesToDisplay.size(), numOfCourses);
    }

    private void testNullSearchKeyHandler(InstructorStudentListPageData islpd) {
        assertEquals("", islpd.getSearchBox().getSearchKey());
    }

}
