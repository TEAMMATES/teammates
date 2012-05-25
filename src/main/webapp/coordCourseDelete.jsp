<%@ page import="java.util.*"%>
<%@ page import="teammates.*"%>
<%@ page import="teammates.jdo.*"%>

<%	
	// See if user is logged in, if not we redirect them to the login page
	Accounts accounts = Accounts.inst();
	if (accounts.getUser() == null) {
		response.sendRedirect( accounts.getLoginPage("/coordinator.jsp") );
		return ;
	}
	
	APIServlet server = new APIServlet();
	String coordID = accounts.getUser().getNickname().toLowerCase();
%>
<%
	String courseID = request.getParameter("courseid");
	String nextURL = request.getParameter("next");
	
	server.deleteCourse(courseID);
	
	response.sendRedirect(nextURL);
%>