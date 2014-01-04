<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="teammates.ui.controller.InstructorCourseStudentDetailsEditPageData"%>
<%
	InstructorCourseStudentDetailsEditPageData data = (InstructorCourseStudentDetailsEditPageData)request.getAttribute("data");
%>

<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>TEAMMATES - Instructor</title>
    <link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
    <link rel="stylesheet" href="/stylesheets/instructorCourseStudentEdit.css" type="text/css" media="screen">
    <link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/instructorCourseStudentEdit-print.css" type="text/css" media="print">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/instructor.js"></script>
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
				<h1>Edit Student Details</h1>
			</div>
				
			<form action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_SAVE%>" method="post">
				<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.student.course%>">
				<table class="inputTable" id="studentEditForm">
					<tr>
			 			<td class="label bold">Student Name:</td>
			 			<td>
			 				<input class="fieldvalue" name="<%=Const.ParamsNames.STUDENT_NAME%>" 
			 						id="<%=Const.ParamsNames.STUDENT_NAME%>"
			 						value="<%=sanitizeForHtml(data.student.name)%>">
			 			</td>
			 		</tr>
				 	<tr>
				 		<td class="label bold">Team Name:</td>
				 		<td>
				 			<input class="fieldvalue" name="<%=Const.ParamsNames.TEAM_NAME%>" 
				 					id="<%=Const.ParamsNames.TEAM_NAME%>"
				 					value="<%=sanitizeForHtml(data.student.team)%>">
				 		</td>
				 	</tr>
				 	<tr>
				 		<td class="label bold">E-mail Address:
				 			<input type="hidden" name="<%=Const.ParamsNames.STUDENT_EMAIL%>" 
				 					id="<%=Const.ParamsNames.STUDENT_EMAIL%>"
				 					value="<%=sanitizeForHtml(data.student.email)%>">
				 		</td>
				 		<td>
				 			<input class="fieldvalue" name="<%=Const.ParamsNames.NEW_STUDENT_EMAIL%>" 
				 					id="<%=Const.ParamsNames.NEW_STUDENT_EMAIL%>"
				 					value="<%=sanitizeForHtml(data.student.email)%>">
				 		</td>
				 	</tr>
				 	<tr>
						<td class="label bold">Google ID:</td>
						<td id="<%=Const.ParamsNames.USER_ID%>"><%=(data.student.googleId!= null ? sanitizeForHtml(data.student.googleId) : "")%></td>
					</tr>
					<tr>
						<td class="label bold">Registration Key:</td>
						<td id="<%=Const.ParamsNames.REGKEY%>"><%=sanitizeForHtml(data.regKey)%></td>
					</tr>
				 	<tr>
				 		<td class="label bold middlealign">Comments:</td>
				 		<td>
				 			<textarea class="textvalue" rows="6" cols="80" 
				 				name="<%=Const.ParamsNames.COMMENTS%>" 
				 				id="<%=Const.ParamsNames.COMMENTS%>"><%=sanitizeForHtml(data.student.comments)%></textarea>
				 		</td>
				 	</tr>
				</table>
				
				<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
				<br>
				<div class="centeralign">
					<input type="submit" class="button centeralign" id="button_submit" name="submit" value="Save Changes"
						onclick="return isStudentInputValid(this.form.<%=Const.ParamsNames.STUDENT_NAME%>.value,this.form.<%=Const.ParamsNames.TEAM_NAME%>.value,this.form.<%=Const.ParamsNames.NEW_STUDENT_EMAIL%>.value)">
				</div>
				<br>
				<br>
				<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
			</form>
			
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>