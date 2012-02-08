<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.*"%>
<%@ page import="teammates.Accounts"%>
<%@ page import="teammates.jdo.Evaluation"%>

<%
	// See if user is logged in, if not we redirect them to the login page
	Accounts accounts = Accounts.inst();
	if (accounts.getUser() == null) {
		response.sendRedirect( accounts.getLoginPage("/developer.jsp") );
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
	<script src="js/developer.js"></script>
	
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
<b>Developer Console (This page is for testing purpose only.)</b><br></br><br></br>
Course ID: <input id="courseid" type="text"></input><br></br>
Evaluation: <input id="evaluation" type="text"></input><br></br> 

Set status to:
<button type="button" id="awaitevaluation" onclick="doChangeEvaluationState(this.form, 'await');">Awaiting</button>
<button type="button" id="openevaluation" onclick="doChangeEvaluationState(this.form, 'active');">Open</button>
<button type="button" id="closeevaluation" onclick="doChangeEvaluationState(this.form, 'expire');">Closed</button>
</form>
<div id="message"></div>
<%
	}
%>
</body>
</html>