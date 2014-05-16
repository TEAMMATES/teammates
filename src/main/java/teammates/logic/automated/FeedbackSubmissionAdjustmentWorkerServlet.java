package teammates.logic.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.logic.automated.WorkerServlet;

@SuppressWarnings("serial")
public class FeedbackSubmissionAdjustmentWorkerServlet extends WorkerServlet {
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        
        FeedbackSubmissionAdjustmentAction adjustmentAction = new FeedbackSubmissionAdjustmentAction(req);
        boolean isExecuteSuccessful = adjustmentAction.execute();
        if (!isExecuteSuccessful) {
            //Retry task if failed
            resp.setStatus(100);
        }
    }
}
