package teammates.test;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.logic.core.StudentsLogic;

/*
* Test cases to check the changes in a studentAttribute's course and team
* */

public class ChangeStudentStatusTest extends BaseTestCase {

    DataBundle dataBundle = new DataBundle();
    private final StudentsLogic studentsLogic = StudentsLogic.inst();

    @Test
    private void testChangeStudentCourse() {
        ______TS("check current course");
        StudentAttributes student1 = dataBundle.students.get("student1InCourse1");
        String oldCourseName = "idOfTypicalCourse1";
        assertEquals(oldCourseName, student1.getCourse());

        ______TS("changed course name and check new name is not equals to old name");
        String testNewCourseName = "testNewCourseForStudent1";
        student1.setCourse(testNewCourseName);
        assertNotEquals(student1.getCourse(), oldCourseName);

        ______TS("check new course name is as expected");
        assertEquals(student1.getCourse(), testNewCourseName);
    }

    @Test
    private void testChangeStudentTeam() {
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        ______TS("get original team");
        String oldTeamName = student1InCourse1.getTeam();

        ______TS("change student team to 'Team 1.2'");
        student1InCourse1.setTeam("Team 1.2");
        assertEquals("Team 1.2", student1InCourse1.getTeam());

        ______TS("'Team 1.2' should not equal old teamName");
        assertNotEquals(oldTeamName, student1InCourse1.getTeam());
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
    }
}
