<%@ page import="java.util.List" %>
<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="teammates.ui.controller.InstructorCourseEnrollResultPageData"%>
<%
InstructorCourseEnrollResultPageData data = (InstructorCourseEnrollResultPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Instructor</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/instructorCourseEnroll.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/instructorCourseEnroll-print.css" type="text/css" media="print">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/instructor.js"></script>
	<script type="text/javascript" src="/js/instructorCourseEnroll.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Common.JSP_INSTRUCTOR_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
				<div id="headerOperation">
					<h1>Enrollment Results for <%=data.courseId%></h1>
				</div>
				<div style="display: block;" id="statusMessage">Enrollment Successful. Summary given below. Click <a href="javascript:history.go(-1)" id="edit_enroll">here</a> to modify values and re-do the enrollment.</div>
				<%
					for(int i=0; i<5; i++){
						List<StudentAttributes> students = data.students[i];
				%>
					<%
						if(students.size()>0){
					%>
						<p class="bold centeralign"><%=data.getMessageForEnrollmentStatus(i)%></p>
						<br>
						<table class="dataTable" class="enroll_result<%=i%>">
						<tr>
							<th class="bold color_white">Student Name</th>
							<th class="bold color_white centeralign">E-mail address</th>
							<th class="bold color_white centeralign">Team</th>
							<th class="bold color_white centeralign" width="40%">Comments</th>
						</tr>
						<%
							for(StudentAttributes student: students){
						%>
							<tr>
								<td><%= student.name %></td>
								<td><%= student.email %></td>
								<td><%= student.team %></td>
								<td><%= student.comments %></td>
							</tr>
						<% 	} %>
						</table>
						<br>
						<br>
						<br>
					<%	} %>
				<%	} %>
				
				<div id="instructorCourseEnrollmentButtons">
				</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Common.JSP_FOOTER%>" />
	</div>
</body>
</html>