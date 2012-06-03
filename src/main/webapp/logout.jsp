<%@ page import="teammates.api.*"%>
<%
	// See if user is logged in, if not we redirect them to main page
	APIServlet server = new APIServlet();
	if (!server.isUserLoggedIn()) {
		response.sendRedirect("/index.jsp");
		return;
	}

	String userID = server.getUserId().toLowerCase();
	response.sendRedirect(server.getLogoutUrl("/index.jsp"));
%>