package teammates.ui.controller;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorEvalSubmissionPageAction extends Action {
    

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
        Assumption.assertNotNull(evalName);
        
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL); 
        //Note: in InstructorEvalSubmissionEditPageData we use Common.Params.FROM_EMAIL instead
        Assumption.assertNotNull(studentEmail);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        EvaluationAttributes evaluation = logic.getEvaluation(courseId, evalName);
        new GateKeeper().verifyAccessible(
                instructor, evaluation, student.section, evaluation.name, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);
        
        InstructorEvalSubmissionPageData data = new InstructorEvalSubmissionPageData(account);
        
        try {
            
            data.student = student;
            data.evaluation = evaluation;
            data.studentResult = logic.getEvaluationResultForStudent(courseId, evalName, studentEmail);
            
            statusToAdmin = "instructorEvalSubmissionView Page Load<br>" + 
                    "Viewing <span class=\"bold\">" + studentEmail + "'s</span> submission " +
                    "for Evaluation <span class=\"bold\">" + evalName + "</span> " +
                    "for Course <span class=\"bold\">[" + courseId + "]</span>";
            
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_EVAL_SUBMISSION, data);
            
        } catch (InvalidParametersException e) {
            setStatusForException(e); 
            return createShowPageResult(Const.ViewURIs.STATUS_MESSAGE, data);
        }
        
    }
    
    
    
}
