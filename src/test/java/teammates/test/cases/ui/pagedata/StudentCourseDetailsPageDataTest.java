package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.StudentCourseDetailsPageData;

public class StudentCourseDetailsPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void test() throws EntityDoesNotExistException {
        ______TS("typical success case");
        
        AccountAttributes account = dataBundle.accounts.get("student1InCourse1");      
        StudentCourseDetailsPageData pageData = new StudentCourseDetailsPageData(account);
              
        StudentAttributes student = dataBundle.students.get("student1InCourse1");        
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        
        CourseDetailsBundle courseDetails = new CourseDetailsBundle(course);
        
        List<InstructorAttributes> instructors = new ArrayList<InstructorAttributes>();
        instructors.add(dataBundle.instructors.get("instructor1OfCourse1"));
        instructors.add(dataBundle.instructors.get("instructor2OfCourse1"));
        instructors.add(dataBundle.instructors.get("helperOfCourse1"));
        
        TeamDetailsBundle team = new TeamDetailsBundle();
        team.name = student.team;
        
        // Get team members of student
        for (Entry<String, StudentAttributes> entry : dataBundle.students.entrySet()) {
            StudentAttributes currStudent = entry.getValue();
            if (currStudent.team.equals(team.name)) {
                team.students.add(currStudent);
            }
        }
        
        pageData.init(courseDetails, instructors, student, team);
        
        assertEquals(courseDetails.course.name, pageData.getStudentCourseDetailsPanel().getCourseName());
        assertEquals(courseDetails.course.id, pageData.getStudentCourseDetailsPanel().getCourseId());
        
        assertFalse(getInstructors(pageData).isEmpty());
        assertFalse(getTeammates(pageData).isEmpty());
        
        assertEquals(student.email, pageData.getStudentCourseDetailsPanel().getStudentEmail());
        assertEquals(student.name, pageData.getStudentCourseDetailsPanel().getStudentName());
        assertEquals(team.name, pageData.getStudentCourseDetailsPanel().getStudentTeam());
        
        assertEquals(3, getInstructors(pageData).size());
        assertEquals("Instructor1 Course1", getInstructors(pageData).get(0).name);
        assertEquals("Instructor2 Course1", getInstructors(pageData).get(1).name);
        assertEquals("Helper Course1", getInstructors(pageData).get(2).name);
        assertEquals("Instructor", getInstructors(pageData).get(0).displayedName);
        assertEquals("Manager", getInstructors(pageData).get(1).displayedName);
        assertEquals("Helper", getInstructors(pageData).get(2).displayedName);
        
        assertEquals(4, getTeammates(pageData).size());
        
        ______TS("student in unregistered course");
        
        student = dataBundle.students.get("student1InUnregisteredCourse");        
        course = dataBundle.courses.get("unregisteredCourse");
        
        courseDetails = new CourseDetailsBundle(course);
        
        instructors = new ArrayList<InstructorAttributes>();
        instructors.add(dataBundle.instructors.get("instructor5"));
        
        team = new TeamDetailsBundle();
        team.name = student.team;
        
        // Get team members of student
        for (Entry<String, StudentAttributes> entry : dataBundle.students.entrySet()) {
            StudentAttributes currStudent = entry.getValue();
            if (currStudent.team.equals(team.name)) {
                team.students.add(currStudent);
            }
        }
        
        pageData.init(courseDetails, instructors, student, team);
        
        assertEquals(courseDetails.course.name, pageData.getStudentCourseDetailsPanel().getCourseName());
        assertEquals(courseDetails.course.id, pageData.getStudentCourseDetailsPanel().getCourseId());
        
        assertFalse(getInstructors(pageData).isEmpty());        
        assertFalse(getTeammates(pageData).isEmpty());
        
        assertEquals(student.email, pageData.getStudentCourseDetailsPanel().getStudentEmail());
        assertEquals(student.name, pageData.getStudentCourseDetailsPanel().getStudentName());
        assertEquals(team.name, pageData.getStudentCourseDetailsPanel().getStudentTeam());
        
        assertEquals(1, getInstructors(pageData).size());
        assertEquals("Instructor 5 of CourseNoRegister", getInstructors(pageData).get(0).name);
        assertEquals("Instructor", getInstructors(pageData).get(0).displayedName);
        
        assertEquals(1, getTeammates(pageData).size());
        
        ______TS("student in archived course");
              
        student = dataBundle.students.get("student1InArchivedCourse");        
        course = dataBundle.courses.get("archivedCourse");
        
        courseDetails = new CourseDetailsBundle(course);
        
        instructors = new ArrayList<InstructorAttributes>();
        instructors.add(dataBundle.instructors.get("instructorOfArchivedCourse"));
        
        team = new TeamDetailsBundle();
        team.name = student.team;
        
        // Get team members of student
        for (Entry<String, StudentAttributes> entry : dataBundle.students.entrySet()) {
            StudentAttributes currStudent = entry.getValue();
            if (currStudent.team.equals(team.name)) {
                team.students.add(currStudent);
            }
        }
        
        pageData.init(courseDetails, instructors, student, team);
        
        assertEquals(courseDetails.course.name, pageData.getStudentCourseDetailsPanel().getCourseName());
        assertEquals(courseDetails.course.id, pageData.getStudentCourseDetailsPanel().getCourseId());
        
        assertFalse(getInstructors(pageData).isEmpty());        
        assertFalse(getTeammates(pageData).isEmpty());
        
        assertEquals(student.email, pageData.getStudentCourseDetailsPanel().getStudentEmail());
        assertEquals(student.name, pageData.getStudentCourseDetailsPanel().getStudentName());
        assertEquals(team.name, pageData.getStudentCourseDetailsPanel().getStudentTeam());
        
        assertEquals(1, getInstructors(pageData).size());
        assertEquals("InstructorOfArchiveCourse name", getInstructors(pageData).get(0).name);
        assertEquals("Instructor", getInstructors(pageData).get(0).displayedName);
        
        assertEquals(3, getTeammates(pageData).size());
    }
    
    public List<InstructorAttributes> getInstructors(StudentCourseDetailsPageData pageData) {
        return pageData.getStudentCourseDetailsPanel().getInstructors();
    }
    
    public List<StudentAttributes> getTeammates(StudentCourseDetailsPageData pageData) {
        return pageData.getStudentCourseDetailsPanel().getTeammates();
    }
}