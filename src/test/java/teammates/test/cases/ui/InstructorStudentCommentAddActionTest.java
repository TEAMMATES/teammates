package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorStudentCommentAddAction;
import teammates.ui.controller.RedirectResult;

public class InstructorStudentCommentAddActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_ADD;
    }

    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        StudentAttributes student = dataBundle.students.get("student3InCourse1");
        String instructorId = instructor.googleId;
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        ______TS("Unsuccessful case: test empty course id parameter");
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added"
        };
        
        InstructorStudentCommentAddAction a;
        RedirectResult r;
        
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                    Const.ParamsNames.COURSE_ID), e.getMessage());
        }
       
        ______TS("Unsuccessful case: test empty student email parameter");
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added"
        };

        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                    Const.ParamsNames.STUDENT_EMAIL), e.getMessage());
        }
        
        ______TS("Unsuccessful case: test empty comment text parameter");
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };
  
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                    Const.ParamsNames.COMMENT_TEXT), e.getMessage());
        }
        
        ______TS("Unsuccessful case: non-existent team");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENT_TYPE, "TEAM",
                Const.ParamsNames.RECIPIENTS, "non-existent team"
        };
  
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that team does not exist.");
        } catch (AssertionError e) {
            assertEquals("null", e.getMessage());
        }
        
        ______TS("Unsuccessful case: non-existent student");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, "non-existent student",
                Const.ParamsNames.RECIPIENT_TYPE, "TEAM",
                Const.ParamsNames.RECIPIENTS, "non-existent student"
        };
  
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that student does not exist.");
        } catch (AssertionError e) {
            assertEquals("null", e.getMessage());
        }
       
        ______TS("Typical success case from student records page");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENT_TYPE, "PERSON",
                Const.ParamsNames.RECIPIENTS, student.email
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student3InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("New comment has been added", r.getStatusMessage());

        String expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentAdd|||instructorStudentCommentAdd"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Created Comment for Student:<span class=\"bold\">([" + student.email + "])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> " + "<Text: A typical comment to be added>"
                + "|||/page/instructorStudentCommentAdd";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Typical success case from comments page");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENT_TYPE, "PERSON",
                Const.ParamsNames.RECIPIENTS, student.email,
                Const.ParamsNames.FROM_COMMENTS_PAGE, "true"
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE
                + "?user=idOfInstructor3&"
                + "courseid=idOfTypicalCourse1&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("New comment has been added", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentAdd|||instructorStudentCommentAdd"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Created Comment for Student:<span class=\"bold\">([" + student.email + "])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> " + "<Text: A typical comment to be added>"
                + "|||/page/instructorStudentCommentAdd";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Typical success case from student details page");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENT_TYPE, "PERSON",
                Const.ParamsNames.RECIPIENTS, student.email,
                Const.ParamsNames.FROM_STUDENT_DETAILS_PAGE, "true"
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student3InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("New comment has been added", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentAdd|||instructorStudentCommentAdd"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Created Comment for Student:<span class=\"bold\">([" + student.email + "])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> " + "<Text: A typical comment to be added>"
                + "|||/page/instructorStudentCommentAdd";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Typical success case from course details page");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENT_TYPE, "PERSON",
                Const.ParamsNames.RECIPIENTS, student.email,
                Const.ParamsNames.FROM_COURSE_DETAILS_PAGE, "true"
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("New comment has been added", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentAdd|||instructorStudentCommentAdd"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Created Comment for Student:<span class=\"bold\">([" + student.email + "])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> " + "<Text: A typical comment to be added>"
                + "|||/page/instructorStudentCommentAdd";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Typical success case for course as recipient");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENT_TYPE, "COURSE",
                Const.ParamsNames.RECIPIENTS, student.course
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student3InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("New comment has been added", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentAdd|||instructorStudentCommentAdd"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Created Comment for Student:<span class=\"bold\">([" + student.course + "])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> " + "<Text: A typical comment to be added>"
                + "|||/page/instructorStudentCommentAdd";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Typical success case for section as recipient");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENT_TYPE, "SECTION",
                Const.ParamsNames.RECIPIENTS, student.section
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student3InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("New comment has been added", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentAdd|||instructorStudentCommentAdd"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Created Comment for Student:<span class=\"bold\">([" + student.section + "])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> " + "<Text: A typical comment to be added>"
                + "|||/page/instructorStudentCommentAdd";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Typical success case for team as recipient");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENT_TYPE, "TEAM",
                Const.ParamsNames.RECIPIENTS, student.team
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student3InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("New comment has been added", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentAdd|||instructorStudentCommentAdd"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Created Comment for Student:"
                + "<span class=\"bold\">([" + Sanitizer.sanitizeForHtml(student.team) + "])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> " + "<Text: A typical comment to be added>"
                + "|||/page/instructorStudentCommentAdd";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Success case for null recipient type");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENTS, student.email
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student3InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("New comment has been added", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentAdd|||instructorStudentCommentAdd"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Created Comment for Student:<span class=\"bold\">([" + student.email + "])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> " + "<Text: A typical comment to be added>"
                + "|||/page/instructorStudentCommentAdd";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Success case for show comment to recipient");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENT_TYPE, "PERSON",
                Const.ParamsNames.RECIPIENTS, student.email,
                Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO, "PERSON"
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student3InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("New comment has been added", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentAdd|||instructorStudentCommentAdd"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Created Comment for Student:<span class=\"bold\">([" + student.email + "])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> " + "<Text: A typical comment to be added>"
                + "|||/page/instructorStudentCommentAdd";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Success case for show giver to recipient");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENT_TYPE, "PERSON",
                Const.ParamsNames.RECIPIENTS, student.email,
                Const.ParamsNames.COMMENTS_SHOWGIVERTO, "PERSON"
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student3InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("New comment has been added", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentAdd|||instructorStudentCommentAdd"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Created Comment for Student:<span class=\"bold\">([" + student.email + "])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> " + "<Text: A typical comment to be added>"
                + "|||/page/instructorStudentCommentAdd";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
    }
    
    private InstructorStudentCommentAddAction getAction(String... params) {
        return (InstructorStudentCommentAddAction) gaeSimulation.getActionObject(uri, params);
    }
}
