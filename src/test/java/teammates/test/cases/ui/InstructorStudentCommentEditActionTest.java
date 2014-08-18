package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.ui.controller.InstructorStudentCommentEditAction;
import teammates.ui.controller.RedirectResult;

public class InstructorStudentCommentEditActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    BackDoorLogic backDoorLogic = new BackDoorLogic();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_EDIT;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        StudentAttributes student = dataBundle.students.get("student2InCourse1");
        String instructorId = instructor.googleId;
        
        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Invalid parameters");
        
        //no params
        verifyAssumptionFailure();
        
        //null courseId
        String[] invalidParams = new String[]{
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };
        
        verifyAssumptionFailure(invalidParams);
        
        //null studentemail
        invalidParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor.courseId
        };
        
        verifyAssumptionFailure(invalidParams);
        
        //null comment text
        invalidParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };
        
        verifyAssumptionFailure(invalidParams);
        

        ______TS("Typical case, edit comment successful");
        List<CommentAttributes> comments = backDoorLogic.getCommentsForReceiver(instructor.courseId, CommentRecipientType.PERSON, student.email);
        Iterator<CommentAttributes> iterator = comments.iterator();
        while(iterator.hasNext()){
            CommentAttributes commentAttributes = iterator.next();
            if(!commentAttributes.giverEmail.equals(instructor.email)){
                iterator.remove();
            }
        }
        Assumption.assertEquals(1, comments.size());

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
                + "studentemail=student2InCourse1%40gmail.com&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertEquals(false, r.isError);
        assertEquals("Comment edited", r.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||instructorStudentCommentEdit|||instructorStudentCommentEdit"+
                "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"+
                "|||instr3@course1n2.com|||" +
                "Edited Comment for Student:<span class=\"bold\">(null)</span> " +
                "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>" +
                "<span class=\"bold\">Comment:</span> "  + "<Text: An edited comment text>" +
                "|||/page/instructorStudentCommentEdit";
        
        assertEquals(expectedLogMessage, a.getLogMessage());
        
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
                + "studentemail=student2InCourse1%40gmail.com&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertEquals(false, r.isError);
        assertEquals("Comment deleted", r.getStatusMessage());
    }
    
    private InstructorStudentCommentEditAction getAction(String... params) throws Exception {
        return (InstructorStudentCommentEditAction) (gaeSimulation.getActionObject(uri, params));
    }
}
