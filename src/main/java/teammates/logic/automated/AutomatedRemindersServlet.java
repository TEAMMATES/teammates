package teammates.logic.automated;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public abstract class AutomatedRemindersServlet extends HttpServlet{
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
	public abstract void doGet(HttpServletRequest req, HttpServletResponse resp);
}
