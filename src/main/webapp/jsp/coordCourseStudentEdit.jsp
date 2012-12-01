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
	<link rel="stylesheet" href="/stylesheets/instructorCourseStudentEdit.css" type="text/css">
	
	<script type="text/javascript" src="/js/jquery-1.6.2.min.js"></script>
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
		<jsp:include page="<%= Common.JSP_INSTRUCTOR_HEADER %>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Edit Student Details</h1>
				<form action="<%= Common.PAGE_INSTRUCTOR_COURSE_STUDENT_EDIT %>" method="post" id="studentEditForm">
					<input type="hidden" name="<%= Common.PARAM_COURSE_ID %>" value="<%= helper.student.course %>">
					<table class="inputTable">
						<tr>
				 			<td class="label">Student Name:</td>
				 			<td>
				 				<input class="fieldvalue" name="<%= Common.PARAM_STUDENT_NAME %>" id="<%= Common.PARAM_STUDENT_NAME %>"
				 						value="<%= helper.student.name %>">
				 			</td>
				 		</tr>
					 	<tr>
					 		<td class="label">Team Name:</td>
					 		<td>
					 			<input class="fieldvalue" name="<%= Common.PARAM_TEAM_NAME %>" id="<%= Common.PARAM_TEAM_NAME %>"
					 					value="<%=InstructorCourseStudentDetailsHelper.escapeForHTML(helper.student.team)%>">
					 		</td>
					 	</tr>
					 	<tr>
					 		<td class="label">E-mail Address:
					 			<input type="hidden" name="<%=Common.PARAM_STUDENT_EMAIL%>" id="<%=Common.PARAM_STUDENT_EMAIL%>"
					 					value="<%=InstructorCourseStudentDetailsHelper.escapeForHTML(helper.student.email)%>">
					 		</td>
					 		<td>
					 			<input class="fieldvalue" name="<%=Common.PARAM_NEW_STUDENT_EMAIL%>" id="<%=Common.PARAM_NEW_STUDENT_EMAIL%>"
					 					value="<%=InstructorCourseStudentDetailsHelper.escapeForHTML(helper.student.email)%>">
					 		</td>
					 	</tr>
					 	<tr>
							<td class="label">Google ID:</td>
							<td id="<%=Common.PARAM_USER_ID%>"><%=(helper.student.id!= null ? InstructorCourseStudentDetailsHelper.escapeForHTML(helper.student.id) : "")%></td>
						</tr>
						<tr>
							<td class="label">Registration Key:</td>
							<td id="<%=Common.PARAM_REGKEY%>"><%=InstructorCourseStudentDetailsHelper.escapeForHTML(helper.regKey)%></td>
						</tr>
					 	<tr>
					 		<td class="label">Comments:</td>
					 		<td>
					 			<textarea class="textvalue" rows="6" cols="70" name="<%=Common.PARAM_COMMENTS%>" id="<%=Common.PARAM_COMMENTS%>"><%=InstructorCourseStudentDetailsHelper.escapeForHTML(helper.student.comments)%></textarea>
					 		</td>
					 	</tr>
					</table>
					<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
					<br><br>
					<input type="button" class="button" id="button_back" value="Cancel"
							onclick="window.location.href='<%= helper.getInstructorCourseDetailsLink(helper.student.course) %>'">
					<input type="submit" class="button" id="button_submit" name="submit" value="Save Changes"
							onclick="return isStudentInputValid(this.form.<%= Common.PARAM_STUDENT_NAME %>.value,this.form.<%= Common.PARAM_TEAM_NAME %>.value,this.form.<%= Common.PARAM_NEW_STUDENT_EMAIL %>.value)">
					<br><br>
					<% if(helper.isMasqueradeMode()){ %>
						<input type="hidden" name="<%= Common.PARAM_USER_ID %>" value="<%= helper.requestedUser %>">
					<% } %>
				</form>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>