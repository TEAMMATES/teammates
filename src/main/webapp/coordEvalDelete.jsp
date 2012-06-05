<%@ page import="java.util.*"%>
<%@ page import="java.net.*" %>
<%@ page import="teammates.api.*"%>


<%	
	// See if user is logged in, if not we redirect them to the login page
	APIServlet server = new APIServlet();
	if (!APIServlet.isUserLoggedIn()) {
		response.sendRedirect( APIServlet.getLoginUrl("/coordHome.jsp") );
		return ;
	}
	
	
	String coordID = server.getLoggedInUser().id.toLowerCase();
%>
<%
	String courseID = URLDecoder.decode(request.getParameter(Common.PARAM_COURSE_ID),Common.ENCODING);
	String evalName = URLDecoder.decode(request.getParameter(Common.PARAM_EVALUATION_NAME),Common.ENCODING);
	String nextURL = URLDecoder.decode(request.getParameter(Common.PARAM_NEXT_URL),Common.ENCODING);
	
	server.deleteEvaluation(courseID, evalName);
	
	response.sendRedirect(nextURL);
%>