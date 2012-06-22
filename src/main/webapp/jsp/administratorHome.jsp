<%@ page import="teammates.api.Common" %>
<%@ page import="teammates.jsp.AdminHomeHelper"%>
<% AdminHomeHelper helper = (AdminHomeHelper)request.getAttribute("helper"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Administrator</title>
	<link rel=stylesheet href="/stylesheets/main.css" type="text/css" />
	<link rel=stylesheet href="/stylesheets/evaluation.css" type="text/css" />

	<script language="JavaScript" src="/js/jquery-1.6.2.min.js"></script>
	<script language="JavaScript" src="/js/tooltip.js"></script>
</head>

<body>
	<div id="dhtmltooltip"></div>

	<div id="frameTop">
		<div id="frameTopWrapper">
			<div id="logo">
				<img alt="Teammates" height="47px"
					src="/images/teammateslogo.jpg"
					width="150px" />
			</div>
			<div id="contentLinks">
				<ul id="navbar">
					<li><a class='t_logout' href="<%= Common.JSP_LOGOUT %>">Logout</a></li>
				</ul>
			</div>
		</div>
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<form action="">
					<b>Add Coordinator</b><br></br><br></br>
					Google ID: <input type="text" name="<%= Common.PARAM_COORD_ID %>"></input><br></br> 
					Name: <input type="text" name="<%= Common.PARAM_COORD_NAME %>"></input><br></br>
					Email: <input type="text" name="<%= Common.PARAM_COORD_EMAIL %>"></input><br></br> 
					<input type="submit" value="Add"></input>
				</form>
			</div>
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>