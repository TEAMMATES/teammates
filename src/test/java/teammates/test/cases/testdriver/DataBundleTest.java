package teammates.test.cases.testdriver;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.util.TimeHelper;
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

        // EVALUATIONS
        EvaluationAttributes evaluation1 = data.evaluations
                .get("evaluation1InCourse1");
        assertEquals("evaluation1 In Course1", evaluation1.name);
        assertEquals("idOfTypicalCourse1", evaluation1.courseId);
        assertEquals("instructions for evaluation1InCourse1",
                evaluation1.instructions.getValue());
        assertEquals(10, evaluation1.gracePeriod);
        assertEquals(true, evaluation1.p2pEnabled);
        assertEquals(TimeHelper.convertToDate("2012-04-01 11:59 PM UTC"),
                evaluation1.startTime);
        assertEquals(TimeHelper.convertToDate("2017-04-30 11:59 PM UTC"),
                evaluation1.endTime);
        assertEquals(true, evaluation1.activated);
        assertEquals(false, evaluation1.published);
        assertEquals(2.0, evaluation1.timeZone, 0.01);

        EvaluationAttributes evaluation2 = data.evaluations
                .get("evaluation2InCourse1");
        assertEquals("evaluation2 In Course1", evaluation2.name);
        assertEquals("idOfTypicalCourse1", evaluation2.courseId);

        // SUBMISSIONS
        SubmissionAttributes submissionFromS1C1ToS2C1 = data.submissions
                .get("submissionFromS1C1ToS2C1");
        assertEquals("student1InCourse1@gmail.com",
                submissionFromS1C1ToS2C1.reviewer);
        assertEquals("student2InCourse1@gmail.com",
                submissionFromS1C1ToS2C1.reviewee);
        assertEquals("idOfTypicalCourse1", submissionFromS1C1ToS2C1.course);
        assertEquals("evaluation1 In Course1",
                submissionFromS1C1ToS2C1.evaluation);
        assertEquals(10, submissionFromS1C1ToS2C1.points);
        assertEquals("Team 1.1", submissionFromS1C1ToS2C1.team);
        // since justification filed is of Text type, we have to use it's
        // .getValue() method to access the string contained inside it
        assertEquals(
                "justification of student1InCourse1 rating to student2InCourse1",
                submissionFromS1C1ToS2C1.justification.getValue());
        assertEquals("comments from student1InCourse1 to student2InCourse1",
                submissionFromS1C1ToS2C1.p2pFeedback.getValue());

        SubmissionAttributes submissionFromS2C1ToS1C1 = data.submissions
                .get("submissionFromS2C1ToS1C1");
        assertEquals("student2InCourse1@gmail.com",
                submissionFromS2C1ToS1C1.reviewer);
        assertEquals("student1InCourse1@gmail.com",
                submissionFromS2C1ToS1C1.reviewee);
    }

}
