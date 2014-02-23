package teammates.logic.core;

import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class FeedbackSubmissionAdjustmentWorkerServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		@SuppressWarnings("unchecked")
		HashMap<String, String> paramMap = (HashMap<String, String>) req.getParameterMap();
		
		FeedbackSubmissionAdjustmentAction adjustmentAction = new FeedbackSubmissionAdjustmentAction(paramMap);
		boolean isExecuteSuccessful = adjustmentAction.execute();
		if (!isExecuteSuccessful) {
			//Retry task if failed
			resp.setStatus(100);
		}
	}
}
