package teammates.logic.automated;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.EvaluationsLogic;
/**
 * Receives and executes tasks added to the "submission-queue" Task Queue
 * Only accessible from within the application and not externally by users  
 */
@SuppressWarnings("serial")
public class SubmissionWorkerServlet extends WorkerServlet {

    private static Logger log = Utils.getLogger();
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        int responseCodeForRetry = 100;
        
        EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
        
        String evaluationName = HttpRequestHelper
                .getValueFromRequestParameterMap(req, ParamsNames.SUBMISSION_EVAL);
        Assumption.assertNotNull(evaluationName);
        
        String courseId = HttpRequestHelper
                .getValueFromRequestParameterMap(req, ParamsNames.SUBMISSION_COURSE);
        Assumption.assertNotNull(courseId);
        
        EvaluationAttributes evaluation = evaluationsLogic
                .getEvaluation(courseId, evaluationName);
        log.info("Creating submissions for evaluation :" + evaluationName);
        if (evaluation != null) {
            try {
                evaluationsLogic.createSubmissionsForEvaluation(evaluation);
                log.info("Submissions for evaluation : " + evaluationName + " successfully created");
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                log.severe(e.getMessage());
            }
        } else {
            log.severe("Evaluation : " + evaluationName + " does not exist in the system anymore");
            resp.setStatus(responseCodeForRetry);
        }
        
    }
}
