package teammates.test.cases.ui;

import java.util.Iterator;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorStudentCommentEditAction;
import teammates.ui.controller.RedirectResult;

public class InstructorStudentCommentEditActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_EDIT;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        InstructorAttributes anotherInstructor = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student = dataBundle.students.get("student2InCourse1");
        StudentAttributes anotherStudent = dataBundle.students.get("student1InCourse1");
        String instructorId = instructor.googleId;
        
        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Invalid parameters");
        
        //no params
        verifyAssumptionFailure();
        
        //null courseId
        String[] invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };
        
        verifyAssumptionFailure(invalidParams);
        
        //null studentemail
        invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId
        };
        
        verifyAssumptionFailure(invalidParams);
        
        //null comment text
        invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };
        
        verifyAssumptionFailure(invalidParams);
        

        ______TS("Typical case, edit comment successful from student records page");
        
        List<CommentAttributes> comments =
                backDoorLogic.getCommentsForReceiver(
                                      instructor.courseId, CommentParticipantType.PERSON, student.email);
        Iterator<CommentAttributes> iterator = comments.iterator();
        while (iterator.hasNext()) {
            CommentAttributes commentAttributes = iterator.next();
            if (!commentAttributes.giverEmail.equals(instructor.email)) {
                iterator.remove();
            }
        }
        assertEquals(1, comments.size());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COMMENT_ID, comments.get(0).getCommentId().toString(),
                Const.ParamsNames.COMMENT_EDITTYPE, "edit",
                Const.ParamsNames.COMMENT_TEXT, "An edited comment text",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };

        InstructorStudentCommentEditAction a = getAction(submissionParams);
        RedirectResult r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student2InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("Comment edited", r.getStatusMessage());

        String expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentEdit|||instructorStudentCommentEdit"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Edited Comment for Student:<span class=\"bold\">(null)</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> <Text: An edited comment text>"
                + "|||/page/instructorStudentCommentEdit";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Typical case, edit comment successful from comments page");
        
        comments = backDoorLogic.getCommentsForReceiver(
                                         instructor.courseId, CommentParticipantType.PERSON, student.email);
        iterator = comments.iterator();
        while (iterator.hasNext()) {
            CommentAttributes commentAttributes = iterator.next();
            if (!commentAttributes.giverEmail.equals(instructor.email)) {
                iterator.remove();
            }
        }
        assertEquals(1, comments.size());

        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_ID, comments.get(0).getCommentId().toString(),
                Const.ParamsNames.COMMENT_EDITTYPE, "edit",
                Const.ParamsNames.COMMENT_TEXT, "Another edited comment text",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
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
        assertEquals("Comment edited", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentEdit|||instructorStudentCommentEdit"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Edited Comment for Student:<span class=\"bold\">(null)</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> <Text: Another edited comment text>"
                + "|||/page/instructorStudentCommentEdit";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Typical case, edit comment successful recipient type not null");
        
        comments = backDoorLogic.getCommentsForReceiver(
                                         instructor.courseId, CommentParticipantType.PERSON, student.email);
        iterator = comments.iterator();
        while (iterator.hasNext()) {
            CommentAttributes commentAttributes = iterator.next();
            if (!commentAttributes.giverEmail.equals(instructor.email)) {
                iterator.remove();
            }
        }
        assertEquals(1, comments.size());

        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_ID, comments.get(0).getCommentId().toString(),
                Const.ParamsNames.COMMENT_EDITTYPE, "edit",
                Const.ParamsNames.COMMENT_TEXT, "some text",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENT_TYPE, "PERSON"
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student2InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("Comment edited", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentEdit|||instructorStudentCommentEdit"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Edited Comment for Student:<span class=\"bold\">(null)</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> <Text: some text>"
                + "|||/page/instructorStudentCommentEdit";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Edit comment successful with empty recipients");
        
        comments = backDoorLogic.getCommentsForReceiver(
                                         instructor.courseId, CommentParticipantType.PERSON, student.email);
        iterator = comments.iterator();
        while (iterator.hasNext()) {
            CommentAttributes commentAttributes = iterator.next();
            if (!commentAttributes.giverEmail.equals(instructor.email)) {
                iterator.remove();
            }
        }
        assertEquals(1, comments.size());

        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_ID, comments.get(0).getCommentId().toString(),
                Const.ParamsNames.COMMENT_EDITTYPE, "edit",
                Const.ParamsNames.COMMENT_TEXT, "some text",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENT_TYPE, "PERSON",
                Const.ParamsNames.RECIPIENTS, ""
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student2InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("Comment edited", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentEdit|||instructorStudentCommentEdit"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Edited Comment for Student:<span class=\"bold\">([student2InCourse1@gmail.tmt])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> <Text: some text>"
                + "|||/page/instructorStudentCommentEdit";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Edit comment successful, giver not current instructor");
        
        comments =
                backDoorLogic.getCommentsForReceiver(
                                      anotherInstructor.courseId, CommentParticipantType.PERSON, anotherStudent.email);
        iterator = comments.iterator();
        while (iterator.hasNext()) {
            CommentAttributes commentAttributes = iterator.next();
            if (!commentAttributes.giverEmail.equals(anotherInstructor.email)) {
                iterator.remove();
            }
        }
        assertEquals(2, comments.size());

        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_ID, comments.get(0).getCommentId().toString(),
                Const.ParamsNames.COMMENT_EDITTYPE, "edit",
                Const.ParamsNames.COMMENT_TEXT, "An edited comment text",
                Const.ParamsNames.COURSE_ID, anotherInstructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, anotherStudent.email,
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student1InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("Comment edited", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentEdit|||instructorStudentCommentEdit"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Edited Comment for Student:<span class=\"bold\">(null)</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> <Text: An edited comment text>"
                + "|||/page/instructorStudentCommentEdit";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Edit comment successful, giver not current instructor with course as recipient");
        
        comments = backDoorLogic.getCommentsForReceiver(
                                         instructor.courseId,
                                         CommentParticipantType.COURSE,
                                         instructor.courseId);
        iterator = comments.iterator();
        while (iterator.hasNext()) {
            CommentAttributes commentAttributes = iterator.next();
            if (!commentAttributes.giverEmail.equals(anotherInstructor.email)
                    || !commentAttributes.recipientType.equals(CommentParticipantType.COURSE)) {
                iterator.remove();
            }
        }
        assertEquals(1, comments.size());

        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_ID, comments.get(0).getCommentId().toString(),
                Const.ParamsNames.COMMENT_EDITTYPE, "edit",
                Const.ParamsNames.COMMENT_TEXT, "some text",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.RECIPIENT_TYPE, "COURSE",
                Const.ParamsNames.RECIPIENTS, instructor.courseId
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("Comment edited", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentEdit|||instructorStudentCommentEdit"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Edited Comment for Student:<span class=\"bold\">([" + instructor.courseId + "])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> <Text: some text>"
                + "|||/page/instructorStudentCommentEdit";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Edit comment successful, giver not current instructor with section as recipient");
        
        comments = backDoorLogic.getCommentsForReceiver(
                                         instructor.courseId,
                                         CommentParticipantType.SECTION,
                                         student.section);
        iterator = comments.iterator();
        while (iterator.hasNext()) {
            CommentAttributes commentAttributes = iterator.next();
            if (!commentAttributes.giverEmail.equals(anotherInstructor.email)
                    || !commentAttributes.recipientType.equals(CommentParticipantType.SECTION)) {
                iterator.remove();
            }
        }
        assertEquals(1, comments.size());

        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_ID, comments.get(0).getCommentId().toString(),
                Const.ParamsNames.COMMENT_EDITTYPE, "edit",
                Const.ParamsNames.COMMENT_TEXT, "some text",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.RECIPIENT_TYPE, "SECTION",
                Const.ParamsNames.RECIPIENTS, student.section
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("Comment edited", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentEdit|||instructorStudentCommentEdit"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Edited Comment for Student:<span class=\"bold\">([" + student.section + "])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> <Text: some text>"
                + "|||/page/instructorStudentCommentEdit";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Edit comment successful, giver not current instructor with team as recipient");
        
        comments = backDoorLogic.getCommentsForReceiver(
                                         instructor.courseId,
                                         CommentParticipantType.TEAM,
                                         Sanitizer.sanitizeForHtml(student.team));
        iterator = comments.iterator();
        while (iterator.hasNext()) {
            CommentAttributes commentAttributes = iterator.next();
            if (!commentAttributes.giverEmail.equals(anotherInstructor.email)
                    || !commentAttributes.recipientType.equals(CommentParticipantType.TEAM)) {
                iterator.remove();
            }
        }
        assertEquals(1, comments.size());

        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_ID, comments.get(0).getCommentId().toString(),
                Const.ParamsNames.COMMENT_EDITTYPE, "edit",
                Const.ParamsNames.COMMENT_TEXT, "some text",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.RECIPIENT_TYPE, "TEAM",
                Const.ParamsNames.RECIPIENTS, student.team
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("Comment edited", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentEdit|||instructorStudentCommentEdit"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Edited Comment for Student:"
                + "<span class=\"bold\">([" + Sanitizer.sanitizeForHtml(student.team) + "])</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> <Text: some text>"
                + "|||/page/instructorStudentCommentEdit";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Typical case, delete comment successful");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_ID, comments.get(0).getCommentId().toString(),
                Const.ParamsNames.COMMENT_EDITTYPE, "delete",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.COMMENT_TEXT, "some text",
        };
        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student2InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("Comment deleted", r.getStatusMessage());
        
        ______TS("Edit comment visibility successful show all comment details to recipient's team");
        
        comments = backDoorLogic.getCommentsForReceiver(
                                         instructor.courseId, CommentParticipantType.PERSON, student.email);
        iterator = comments.iterator();
        while (iterator.hasNext()) {
            CommentAttributes commentAttributes = iterator.next();
            if (!commentAttributes.giverEmail.equals(instructor.email)) {
                iterator.remove();
            }
        }
        assertEquals(1, comments.size());

        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_ID, comments.get(0).getCommentId().toString(),
                Const.ParamsNames.COMMENT_EDITTYPE, "edit",
                Const.ParamsNames.COMMENT_TEXT, "some text",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO, "TEAM",
                Const.ParamsNames.COMMENTS_SHOWGIVERTO, "TEAM",
                Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO, "TEAM"
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student2InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("Comment edited", r.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorStudentCommentEdit|||instructorStudentCommentEdit"
                + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                + "|||instr3@course1n2.tmt|||"
                + "Edited Comment for Student:<span class=\"bold\">(null)</span> "
                + "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>"
                + "<span class=\"bold\">Comment:</span> <Text: some text>"
                + "|||/page/instructorStudentCommentEdit";
        
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Unsuccessful edit, non-existent comment");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_ID, "123123123123", // non-existent comment id
                Const.ParamsNames.COMMENT_EDITTYPE, "edit",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.COMMENT_TEXT, "some text",
        };
        
        try {
            a = getAction(submissionParams);
            r = getRedirectResult(a);
        } catch (AssertionError e) {
            assertEquals("Comment or instructor cannot be null for editing comment", e.getMessage());
        }

    }
    
    private InstructorStudentCommentEditAction getAction(String... params) {
        return (InstructorStudentCommentEditAction) gaeSimulation.getActionObject(uri, params);
    }
}
