<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="teammates.ui.controller.InstructorCourseStudentDetailsPageData"%>
<%
	InstructorCourseStudentDetailsPageData data = (InstructorCourseStudentDetailsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>TEAMMATES - Instructor</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/instructorCourseStudentDetails.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/instructorCourseStudentDetails-print.css" type="text/css" media="print">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script language="JavaScript" src="/js/instructor.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>


<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />
	</div>


	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Student Details</h1>
			</div>
			
			<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
			
			<table class="inputTable" id="studentInfomationTable">
				<tr>
		 			<td class="label rightalign bold" width="30%">Student Name:</td>
		 			<td id="<%=Const.ParamsNames.STUDENT_NAME%>"><%=data.student.name%></td>
					</tr>
			 	<tr>
					<td class="label rightalign bold" width="30%">Team Name:</td>
			 		<td id="<%=Const.ParamsNames.TEAM_NAME%>"><%=sanitizeForHtml(data.student.team)%></td>
			 	</tr>
			 	<tr>
			 		<td class="label rightalign bold" width="30%">E-mail Address:</td>
			 		<td id="<%=Const.ParamsNames.STUDENT_EMAIL%>"><%=sanitizeForHtml(data.student.email)%></td>
			 	</tr>
			 	<tr>
					<td class="label rightalign bold" width="30%">Google ID:</td>
					<td id="<%=Const.ParamsNames.USER_ID%>"><%=(data.student.googleId!= null ? sanitizeForHtml(data.student.googleId) : "")%></td>
				</tr>
				<tr>
					<td class="label rightalign bold" width="30%">Registration Key:</td>
					<td id="<%=Const.ParamsNames.REGKEY%>"><%=sanitizeForHtml(data.regKey)%></td>
				</tr>
			 	<tr>
			 		<td class="label rightalign bold" width="30%">Comments:</td>
			 		<td id="<%=Const.ParamsNames.COMMENTS%>"><%=sanitizeForHtml(data.student.comments)%></td>
			 	</tr>
			 </table>
			 <br>
			 <br>
		
		</div>
	</div>


	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>