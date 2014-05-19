package teammates.logic.automated;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.EvaluationsLogic;
/**
 * Receives and executes tasks added to the "evaluation-remind-email-queue" Task Queue
 * Only accessible from within the application and not externally by users  
 */
@SuppressWarnings("serial")
public class EvaluationRemindEmailWorkerServlet extends WorkerServlet {
    private static Logger log = Utils.getLogger();
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        
        String evaluationName = HttpRequestHelper
                .getValueFromRequestParameterMap(req, ParamsNames.SUBMISSION_EVAL);
        Assumption.assertNotNull(evaluationName);
        
        String courseId = HttpRequestHelper
                .getValueFromRequestParameterMap(req, ParamsNames.SUBMISSION_COURSE);
        Assumption.assertNotNull(courseId);
        
        try {
            EvaluationsLogic.inst().sendReminderForEvaluation(courseId, evaluationName);
        } catch (EntityDoesNotExistException e) {
            log.severe("Unexpected error while sending emails " + e.getMessage());
        }
    }
}
