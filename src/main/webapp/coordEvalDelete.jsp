<%@ page import="java.util.*"%>
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
	String courseID = request.getParameter(Common.PARAM_COURSE_ID);
	String evalName = request.getParameter(Common.PARAM_EVALUATION_NAME);
	String nextURL = request.getParameter(Common.PARAM_NEXT_URL);
	
	server.deleteEvaluation(courseID, evalName);
	
	response.sendRedirect(nextURL);
%>