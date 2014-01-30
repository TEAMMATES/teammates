<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="teammates.ui.controller.StudentCourseJoinConfirmationPageData"%>
<%
	StudentCourseJoinConfirmationPageData data = (StudentCourseJoinConfirmationPageData) request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>TEAMMATES - Student</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/studentHome.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/studentHome-print.css" type="text/css" media="print">

	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<jsp:include page="../enableJS.jsp"></jsp:include>	
</head>

<body>
	<div id="dhtmltooltip"></div>

	<div id="frameTop">
		<jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Student Course Join Confirmation</h1>
			</div>
						
			<br>
			<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
			<br>
			
			<div class="centeralign">
				<h2>
					You are currently logged in as <span class="bold"><%=data.account.googleId%></span>. 
					<br>If this is not you please <a href="/logout.jsp">log out</a> and re-login using your own Google account.
					<br>If this is you, please confirm below to complete your registration.
					<br><br><br>
					<a href="<%=Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED + "?regkey=" + data.regkey%>" 
						style="padding-right:30px;"
						id="button_confirm">Yes, this is my account</a>
					<a href="/logout.jsp" 
						style="padding-left:30px;"
						id="button_cancel">No, this is not my account</a>
				</h2>
			</div>
		</div>
	</div>
	
	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>