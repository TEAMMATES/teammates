<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.ui.controller.AdminHomeHelper"%>
<% AdminHomeHelper helper = (AdminHomeHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Administrator</title>
	<link rel="stylesheet" href="/stylesheets/main.css" type="text/css">
	<link rel="stylesheet" href="/stylesheets/evaluation.css" type="text/css">

	<script type="text/javascript" src="/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
</head>

<body>
	<div id="dhtmltooltip"></div>

	<div id="frameTop">
		<div id="frameTopWrapper">
			<div id="logo">
				<img alt="Teammates" height="47px"
					src="/images/teammateslogo.jpg"
					width="150px">
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
					<b>Add Coordinator</b><br><br><br><br>
					Google ID: <input type="text" name="<%= Common.PARAM_COORD_ID %>"><br><br> 
					Name: <input type="text" name="<%= Common.PARAM_COORD_NAME %>"><br><br>
					Email: <input type="text" name="<%= Common.PARAM_COORD_EMAIL %>"><br><br> 
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