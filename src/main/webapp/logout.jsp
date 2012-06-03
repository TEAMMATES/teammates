<%@ page import="teammates.manager.Accounts"%>
<%@ page import="teammates.api.*"%>
<%
	// See if user is logged in, if not we redirect them to main page
	Accounts accounts = Accounts.inst();
	if (accounts.getUser() == null) {
		response.sendRedirect("/index.jsp");
		return;
	}

	APIServlet server = new APIServlet();

	String userID = accounts.getUser().getNickname().toLowerCase();
	response.sendRedirect(server.coordGetLogoutUrl("/index.jsp"));
%>