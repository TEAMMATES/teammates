package teammates;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class AutomatedRemindersServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			new BackDoorLogic().sendRemindersForClosingEvaluations();
		} catch (Exception e) {
			throw new RuntimeException(
					"Unexpected exception while sending reminders for closing evaluations"
							+ e);
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
}
