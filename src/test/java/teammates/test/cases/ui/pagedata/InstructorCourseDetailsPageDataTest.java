package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertTrue;
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
import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
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
        
        StudentAttributes unregisteredStudent = new StudentAttributes("None", "Team 1.1", "Unregistered Student", 
                                                                      "unregisteredStudentInCourse1@gmail.tmt", "No comment", "idOfTypicalCourse1");
        students.add(unregisteredStudent);
        
        CourseDetailsBundle courseDetails = new CourseDetailsBundle(dataBundle.courses.get("typicalCourse1"));
        courseDetails.sections = new ArrayList<SectionDetailsBundle>();
        SectionDetailsBundle sampleSection = new SectionDetailsBundle();
        sampleSection.name = "Sample section name";
        courseDetails.sections.add(sampleSection);
        
        pageData.init(curInstructor, courseDetails, instructors, students);
        
        assertEquals(instructors.size(), pageData.getInstructors().size());
        assertNotNull(pageData.getCourseRemindButton());
        assertFalse(pageData.getCourseRemindButton().getAttributes().isEmpty());
        assertNull(pageData.getCourseRemindButton().getContent());
        assertNotNull(pageData.getGiveCommentButton());
        assertFalse(pageData.getGiveCommentButton().getAttributes().isEmpty());
        assertNotNull(pageData.getGiveCommentButton().getContent());
        assertNotNull(pageData.getCourseDetails());
        assertNotNull(pageData.getCurrentInstructor());
        assertTrue(pageData.isHasSection());
        assertEquals(1, pageData.getSections().size());
        
        ______TS("test data bundle with no section");
        
        courseDetails.sections = new ArrayList<SectionDetailsBundle>();
        sampleSection = new SectionDetailsBundle();
        sampleSection.name = "None";
        courseDetails.sections.add(sampleSection);
        pageData.init(curInstructor, courseDetails, instructors, students);
        assertFalse(pageData.isHasSection());
        assertEquals(1, pageData.getSections().size());
        
        ______TS("test current instructor doesn't have any permission for the course");
        String[] allPrivileges = {
                Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
        };
        
        for (String privilege : allPrivileges) {
            curInstructor.privileges.updatePrivilege(privilege, false);
        }
        
        pageData.init(curInstructor, courseDetails, instructors, students);

        assertEquals(instructors.size(), pageData.getInstructors().size());
        assertNotNull(pageData.getCourseRemindButton());
        assertFalse(pageData.getCourseRemindButton().getAttributes().isEmpty());
        assertNull(pageData.getCourseRemindButton().getContent());
        assertNotNull(pageData.getGiveCommentButton());
        assertFalse(pageData.getGiveCommentButton().getAttributes().isEmpty());
        assertNotNull(pageData.getGiveCommentButton().getContent());
        assertNotNull(pageData.getCourseDetails());
        assertNotNull(pageData.getCurrentInstructor());
    }
}