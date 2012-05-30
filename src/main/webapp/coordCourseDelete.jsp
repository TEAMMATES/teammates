<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="teammates.*"%>
<%@ page import="teammates.jdo.*"%>

<%	
	// See if user is logged in, if not we redirect them to the login page
	Accounts accounts = Accounts.inst();
	if (accounts.getUser() == null) {
		response.sendRedirect( accounts.getLoginPage("/coordHome.jsp") );
		return ;
	}
	
	APIServlet server = new APIServlet();
	String coordID = accounts.getUser().getNickname().toLowerCase();
%>
<%
	String courseID = URLDecoder.decode(request.getParameter("courseid"),Common.ENCODING);
	String nextURL = URLDecoder.decode(request.getParameter("next"),Common.ENCODING);
	
	server.deleteCourse(courseID);
	
	response.sendRedirect(nextURL);
%>