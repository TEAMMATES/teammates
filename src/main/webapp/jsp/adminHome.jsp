<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.ui.controller.AdminHomeHelper"%>
<% AdminHomeHelper helper = (AdminHomeHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Administrator</title>
	<link rel="stylesheet" href="/stylesheets/adminHome.css" type="text/css">
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css">

	<script type="text/javascript" src="/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
	<jsp:include page="<%= Common.JSP_ADMIN_HEADER %>" />
	</div>
	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
			<h1>Add New Coordinator</h1>
			</div>
			<div id="adminManagement">
				<form action="">
					<table id="addform" class="inputTable">
					<tr>
						<td class="label">Google ID:</td>
					</tr>
					<tr>
					   <td><input class="addinput" type="text" name="<%= Common.PARAM_COORD_ID %>"></td>
					</tr>
					<tr>
						<td class="label">Name:</td>
					</tr>
					<tr>
						<td><input class="addinput" type="text" name="<%= Common.PARAM_COORD_NAME %>"></td>
				    </tr>
				    <tr>
					    <td class="label">Email: </td>
					</tr>
					<tr>
						<td><input class="addinput" type="text" name="<%= Common.PARAM_COORD_EMAIL %>"></td>
				    </tr>
				    <tr>
						<td><input type="checkbox" name="<%= Common.PARAM_COORD_IMPORT_SAMPLE %>" value="importsample">Import sample data</input></td>
				    </tr>
				    <tr>
						<td><input id="btnAddCoord" class="button" type="submit" value="Add Coordinator"></td>
				    </tr>
				    </table>
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