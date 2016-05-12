package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class FeedbackSubmissionAdjustmentWorkerServlet extends WorkerServlet {
    
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
        
        FeedbackSubmissionAdjustmentAction adjustmentAction = new FeedbackSubmissionAdjustmentAction(req);
        boolean isExecuteSuccessful = adjustmentAction.execute();
        if (!isExecuteSuccessful) {
            //Retry task if failed
            resp.setStatus(100);
        }
    }
}
