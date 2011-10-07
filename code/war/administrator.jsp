<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.*"%>
<%@ page import="teammates.Accounts"%>

<%
	// See if user is logged in, if not we redirect them to the login page
	Accounts accounts = Accounts.inst();
	if (accounts.getUser() == null) {
		response.sendRedirect( accounts.getLoginPage("/administrator.jsp") );
		return ;
	}
%>


<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" /> 
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Automated Test Driver</title>
	<link rel=stylesheet href="/stylesheets/main.css" type="text/css">
	<script language="JavaScript" src="js/jquery-1.6.2.min.js"></script>
	<script language="JavaScript" src="js/administrator.js"></script>
</head>

<body>
<% if (!accounts.isAdministrator()) { %>

<p>You are not authorised to view this page.<br></br><br></br> <a href="javascript:logout()">Logout and return to main page.</a></p>
<%
	} 
	else 
	{
%>
<form>
<b>Add Coordinator</b><br></br><br></br>
Google ID: <input type="text"></input><br></br> 
Name: <input type="text"></input><br></br>
Email: <input type="text"></input><br></br> 
<input type="submit" value="Add" onclick="doAddCoordinator(this.form);"></input>
</form>
<%
	}
%>
</body>
</html>