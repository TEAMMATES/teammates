package teammates;



import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.users.UserService;


@SuppressWarnings("serial")
public class StudentJoinCourseServlet extends HttpServlet  {
	private final String REG_KEY = "regkey";
	private final String SERVLET_URL = "joinCourse";
	public void doGet(HttpServletRequest req, HttpServletResponse resp)  throws IOException{
		UserService userService = UserServiceFactory.getUserService();
		String regkey = req.getParameter(REG_KEY);
		
		if(!userService.isUserLoggedIn())
		{
			resp.sendRedirect(userService.createLoginURL("/"+SERVLET_URL+"?"+REG_KEY+"=" + regkey));
		} else {
			Accounts accounts = Accounts.inst();
			String googleID = accounts.getUser().getNickname();
			Courses courses = Courses.inst();

			try {
				courses.joinCourse(regkey, googleID);
			}

			catch (Exception e) {
				//TODO: handle exception
			} finally {
				resp.sendRedirect("/student.jsp");
			}

		}
		
		
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		doGet(req, resp);
	}
}
