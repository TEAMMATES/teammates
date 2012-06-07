<%@ page import="java.net.URLDecoder" %>
<%@ page import="teammates.api.Common"%>
<%@ page import="teammates.api.APIServlet"%>
<%
	APIServlet server = new APIServlet();
	String courseID = URLDecoder.decode(request.getParameter(Common.PARAM_COURSE_ID),Common.ENCODING);
	String evalName = URLDecoder.decode(request.getParameter(Common.PARAM_EVALUATION_NAME),Common.ENCODING);
	String nextURL = URLDecoder.decode(request.getParameter(Common.PARAM_NEXT_URL),Common.ENCODING);
	
	server.deleteEvaluation(courseID, evalName);
	
	response.sendRedirect(nextURL);
%>