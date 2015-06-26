package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.InstructorCourseDetailsPageData;

public class InstructorCourseDetailsPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void testAll() {
        ______TS("test typical case");
        AccountAttributes instructorAccount = dataBundle.accounts.get("instructor1OfCourse1");
        InstructorCourseDetailsPageData pageData = new InstructorCourseDetailsPageData(instructorAccount);
        
        InstructorAttributes curInstructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        List<InstructorAttributes> instructors = new ArrayList<InstructorAttributes>();
        for(InstructorAttributes instructor : dataBundle.instructors.values()) {
            if (instructor.courseId.equals("idOfTypicalCourse1")) {
                instructors.add(instructor);
            }
        }
        
        List<StudentAttributes> students = new ArrayList<StudentAttributes>();
        for(StudentAttributes student : dataBundle.students.values()) {
            if (student.course.equals("idOfTypicalCourse1")) {
                students.add(student);
            }
        }
        
        CourseDetailsBundle courseDetails = new CourseDetailsBundle(dataBundle.courses.get("typicalCourse1"));
        
        pageData.init(curInstructor, courseDetails, instructors, students);
        
        assertEquals(4, pageData.getInstructors().size());
        assertEquals(5, pageData.getStudentsTable().getRows().size());
        assertEquals(4, pageData.getStudentsTable().getRows().get(0).getActions().size());
        assertEquals(2, pageData.getStudentsTable().getRows().get(0).getCommentActions().size());
        assertNotNull(pageData.getCourseRemindButton());
        assertFalse(pageData.getCourseRemindButton().getAttributes().isEmpty());
        assertNull(pageData.getCourseRemindButton().getContent());
        assertNotNull(pageData.getGiveCommentButton());
        assertFalse(pageData.getGiveCommentButton().getAttributes().isEmpty());
        assertNotNull(pageData.getGiveCommentButton().getContent());
        
    }
}