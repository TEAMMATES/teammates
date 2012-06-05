<%@ page import="teammates.api.*"%>
<%
	// See if user is logged in, if not we redirect them to main page
	if (!APIServlet.isUserLoggedIn()) {
		response.sendRedirect("/index.jsp");
		return;
	}

	response.sendRedirect(APIServlet.getLogoutUrl("/index.jsp"));
%>