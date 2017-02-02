package teammates.ui.automated;

import java.util.List;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

/**
 * Task queue worker action: Puts the necessary items into documents
 * to reduce calls to database made by AdminAccountInstructorAddAction
 */
public class SearchableDocumentsProductionWorkerAction extends AutomatedAction {
    
    @Override
    protected String getActionDescription() {
        return null;
    }
    
    @Override
    protected String getActionMessage() {
        return null;
    }
    
    @Override
    public void execute() {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        Assumption.assertNotNull(instructorEmail);
        
        //produce searchable documents
        List<FeedbackResponseCommentAttributes> frComments =
                logic.getFeedbackResponseCommentForGiver(courseId, instructorEmail);
        List<StudentAttributes> students = logic.getStudentsForCourse(courseId);
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        
        for (FeedbackResponseCommentAttributes comment : frComments) {
            logic.putDocument(comment);
        }
        for (StudentAttributes student : students) {
            logic.putDocument(student);
        }
        for (InstructorAttributes instructor : instructors) {
            logic.putDocument(instructor);
        }
    }
    
}
