package teammates.ui.controller;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.TeamResultBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

public class InstructorEvalResultsPageAction extends Action {
    Logger log = Utils.getLogger();

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
        Assumption.assertNotNull(evalName);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(
                instructor, logic.getEvaluation(courseId, evalName));
        
        InstructorEvalResultsPageData data = new InstructorEvalResultsPageData(account);
        
        data.evaluationResults = logic.getEvaluationResult(courseId, evalName);
        data.instructor = instructor;
        Iterator<Entry<String, TeamResultBundle>> iter = data.evaluationResults.teamResults.entrySet().iterator();
        while (iter.hasNext()) {
            boolean shouldDisplayTeam = true;
            for (StudentResultBundle studentBundle : iter.next().getValue().studentResults) {
                if (!instructor.isAllowedForPrivilege(studentBundle.student.section, Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES+evalName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)) {
                    shouldDisplayTeam = false;
                    break;
                }
            }
            if (!shouldDisplayTeam) {
                iter.remove();
            }
        }
        data.evaluationResults.sortForReportToInstructor();
                
        statusToUser.add(Const.StatusMessages.LOADING);
        statusToAdmin = "instructorEvalResults Page Load<br>" + "Viewing Results " +
                "for Evaluation <span class=\"bold\">" + evalName + "</span> " +
                "in Course <span class=\"bold\">[" + courseId + "]</span>";
            
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_EVAL_RESULTS, data);

    }

}