<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="teammates.api.*"%>

<%	
	// See if user is logged in, if not we redirect them to the login page
	APIServlet server = new APIServlet();
	
	if (!server.isUserLoggedIn()) {
		response.sendRedirect( server.getLoginUrl("/coordHome.jsp") );
		return ;
	}
	
	String coordID = server.getUserId().toLowerCase();
%>
<%
	String courseID = URLDecoder.decode(request.getParameter("courseid"),Common.ENCODING);
	String nextURL = URLDecoder.decode(request.getParameter("next"),Common.ENCODING);
	
	server.deleteCourse(courseID);
	
	response.sendRedirect(nextURL);
%>