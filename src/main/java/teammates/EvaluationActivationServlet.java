package teammates;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;

@SuppressWarnings("serial")
public class EvaluationActivationServlet extends HttpServlet {
	
	private static Logger log = Common.getLogger();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			new BackDoorLogic().activateReadyEvaluations();
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception during evaluation activation",e);
		} 
	}

}
