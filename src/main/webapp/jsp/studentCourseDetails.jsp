<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.common.datatransfer.StudentData" %>
<%@ page import="teammates.common.datatransfer.InstructorData" %>
<%@ page import="teammates.ui.controller.StudentCourseDetailsHelper"%>
<% StudentCourseDetailsHelper helper = (StudentCourseDetailsHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Student</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen"/>
	<link rel="stylesheet" href="/stylesheets/studentCourseDetails.css" type="text/css" media="screen"/>
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print"/>
    <link rel="stylesheet" href="/stylesheets/studentCourseDetails-print.css" type="text/css" media="print"/>

	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/student.js"></script>
	<jsp:include page="../enableJS.jsp"></jsp:include>	
</head>

<body>
	<div id="dhtmltooltip"></div>

	<div id="frameTop">
		<jsp:include page="<%= Common.JSP_STUDENT_HEADER %>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Team Details for <%= helper.course.id %></h1>
			</div>
			<br>
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
			<br>
				
			<table class="inputTable" id="studentCourseInformation">
				<tr>
	 				<td class="label rightalign bold" width="30%">Course ID:</td>
	 				<td id="<%= Common.PARAM_COURSE_ID %>"><%= helper.course.id %></td>
	 			</tr>
				<tr>
	 				<td class="label rightalign bold" width="30%">Course name:</td>
	 				<td id="<%= Common.PARAM_COURSE_NAME %>"><%=StudentCourseDetailsHelper.escapeForHTML(helper.course.name)%></td>
	 			</tr>
	 			<tr>
	 				<td class="label rightalign bold" width="30%">Instructors:</td>
	 				<td id="<%=Common.PARAM_INSTRUCTOR_NAME%>">
	 				<%
		 				for (int i = 0; i < helper.instructors.size(); i++){
		 					InstructorData instructor = helper.instructors.get(i);
		 					String instructorInfo = instructor.name + " (" + instructor.email + ")";
		 			%>
		 				<a href = "mailto:<%=instructor.email%>"><%=instructorInfo %></a><br><br>
		 			<%
		 				}
		 			%>
	 				</td>
	 			</tr>
	 			<tr>
	 				<td class="label rightalign bold" width="30%">Your team:</td>
	 				<td id="<%=Common.PARAM_TEAM_NAME%>"><%=StudentCourseDetailsHelper.escapeForHTML(helper.student.team)%></td>
	 			</tr>
	 			<tr>
	 				<td class="label rightalign bold" width="30%">Your name:</td>
	 				<td id="<%=Common.PARAM_STUDENT_NAME%>"><%=StudentCourseDetailsHelper.escapeForHTML(helper.student.name)%></td>
	 			</tr>
	 			<tr>
	 				<td class="label rightalign bold" width="30%">Your e-mail:</td>
	 				<td id="<%=Common.PARAM_STUDENT_EMAIL%>"><%=helper.student.email%></td>
	 			</tr>
	 			<tr>
	 				<td class="label rightalign bold" width="30%">Your teammates:</td>
	 				<td id="<%=Common.PARAM_TEAMMATES%>">
	 					<%
	 						if(helper.team==null || helper.team.students.size()==1){
	 					%>
	 						<span style="font-style: italic;">You have no team members or you are not registered in any team</span>
	 					<%
	 						} else {
	 					%>
	 						<ul>
									<%
										for(StudentData student: helper.team.students){
									%>
										<%
											if(!student.email.equals(helper.student.email)) {
										%>
											<li><a href = "mailto:<%=student.email%>"><%=StudentCourseDetailsHelper.escapeForHTML(student.name)%></a></li>
										<%	} %>
									<%	} %>
		 					</ul>
		 				<%	} %>
	 				</td>
	 			</tr>
	 		</table>	
	 		<br>
	 		<br>
	 		<br> 	   
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>