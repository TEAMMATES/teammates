package teammates.test.cases.testdriver;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.test.cases.BaseTestCase;

public class DataBundleTest extends BaseTestCase {
    
    @Test
    public void testDataBundle() throws Exception {

        
        DataBundle data = getTypicalDataBundle();

        // INSTRUCTORS
        InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
        assertEquals("idOfInstructor1OfCourse1", instructor1OfCourse1.googleId);
        assertEquals("idOfTypicalCourse1", instructor1OfCourse1.courseId);

        InstructorAttributes instructor2OfCourse1 = data.instructors.get("instructor2OfCourse1");
        assertEquals("idOfInstructor2OfCourse1", instructor2OfCourse1.googleId);
        assertEquals("idOfTypicalCourse1", instructor2OfCourse1.courseId);
        
        InstructorAttributes instructor1OfCourse2 = data.instructors.get("instructor1OfCourse2");
        assertEquals("idOfInstructor1OfCourse2", instructor1OfCourse2.googleId);
        assertEquals("idOfTypicalCourse2", instructor1OfCourse2.courseId);
        
        InstructorAttributes instructor2OfCourse2 = data.instructors.get("instructor2OfCourse2");
        assertEquals("idOfInstructor2OfCourse2", instructor2OfCourse2.googleId);
        assertEquals("idOfTypicalCourse2", instructor2OfCourse2.courseId);
        
        InstructorAttributes instructor3OfCourse1 = data.instructors.get("instructor3OfCourse1");
        assertEquals("idOfInstructor3", instructor3OfCourse1.googleId);
        assertEquals("idOfTypicalCourse1", instructor3OfCourse1.courseId);
        
        InstructorAttributes instructor3OfCourse2 = data.instructors.get("instructor3OfCourse2");
        assertEquals("idOfInstructor3", instructor3OfCourse2.googleId);
        assertEquals("idOfTypicalCourse2", instructor3OfCourse2.courseId);
        
        InstructorAttributes instructor4 = data.instructors.get("instructor4");
        assertEquals("idOfInstructor4", instructor4.googleId);
        assertEquals("idOfCourseNoEvals", instructor4.courseId);
        
        // COURSES
        CourseAttributes course1 = data.courses.get("typicalCourse1");
        assertEquals("idOfTypicalCourse1", course1.id);
        assertEquals("Typical Course 1 with 2 Evals", course1.name);
        
        CourseAttributes course2 = data.courses.get("typicalCourse2");
        assertEquals("idOfTypicalCourse2", course2.id);
        assertEquals("Typical Course 2 with 1 Evals", course2.name);

        // STUDENTS
        StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
        assertEquals("student1InCourse1", student1InCourse1.googleId);
        assertEquals("student1 In Course1", student1InCourse1.name);
        assertEquals("Team 1.1", student1InCourse1.team);
        assertEquals("comment for student1InCourse1",
                student1InCourse1.comments);
        assertEquals("idOfTypicalCourse1", student1InCourse1.course);
        
        StudentAttributes student2InCourse2 = data.students.get("student2InCourse2");
        assertEquals("student2InCourse1", student2InCourse2.googleId);
        assertEquals("student2 In Course2", student2InCourse2.name);
        assertEquals("Team 2.1", student2InCourse2.team);

    }

}
