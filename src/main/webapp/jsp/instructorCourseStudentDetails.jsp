<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.common.datatransfer.CourseData"%>
<%@ page import="teammates.common.datatransfer.EvaluationData"%>
<%@ page import="teammates.ui.controller.InstructorCourseStudentDetailsHelper"%>
<%	InstructorCourseStudentDetailsHelper helper = (InstructorCourseStudentDetailsHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Instructor</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css">
	<link rel="stylesheet" href="/stylesheets/instructorCourseStudentDetails.css" type="text/css">
	
	<script type="text/javascript" src="/js/jquery-1.8.3.min.js"></script>
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
		<jsp:include page="<%= Common.JSP_INSTRUCTOR_HEADER %>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Student Details</h1>
			</div>
			
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
			
			<table class="inputTable" id="studentInfomationTable">
				<tr>
		 			<td class="label rightalign bold" width="30%">Student Name:</td>
		 			<td id="<%= Common.PARAM_STUDENT_NAME %>"><%= helper.student.name %></td>
					</tr>
			 	<tr>
					<td class="label rightalign bold" width="30%">Team Name:</td>
			 		<td id="<%= Common.PARAM_TEAM_NAME %>"><%=InstructorCourseStudentDetailsHelper.escapeForHTML(helper.student.team)%></td>
			 	</tr>
			 	<tr>
			 		<td class="label rightalign bold" width="30%">E-mail Address:</td>
			 		<td id="<%=Common.PARAM_STUDENT_EMAIL%>"><%=InstructorCourseStudentDetailsHelper.escapeForHTML(helper.student.email)%></td>
			 	</tr>
			 	<tr>
					<td class="label rightalign bold" width="30%">Google ID:</td>
					<td id="<%=Common.PARAM_USER_ID%>"><%=(helper.student.id!= null ? InstructorCourseStudentDetailsHelper.escapeForHTML(helper.student.id) : "")%></td>
				</tr>
				<tr>
					<td class="label rightalign bold" width="30%">Registration Key:</td>
					<td id="<%=Common.PARAM_REGKEY%>"><%=InstructorCourseStudentDetailsHelper.escapeForHTML(helper.regKey)%></td>
				</tr>
			 	<tr>
			 		<td class="label rightalign bold" width="30%">Comments:</td>
			 		<td id="<%=Common.PARAM_COMMENTS%>"><%=InstructorCourseStudentDetailsHelper.escapeForHTML(helper.student.comments)%></td>
			 	</tr>
			 </table>
			 <br>
			 <br>
		
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>