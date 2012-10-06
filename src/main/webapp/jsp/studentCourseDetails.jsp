<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.common.datatransfer.StudentData" %>
<%@ page import="teammates.ui.controller.StudentCourseDetailsHelper"%>
<% StudentCourseDetailsHelper helper = (StudentCourseDetailsHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Student</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" />
	<link rel="stylesheet" href="/stylesheets/studentCourseDetails.css" type="text/css" />

	<script type="text/javascript" src="/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/student.js"></script>	
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
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
			<div id="studentCourseInformation">
				<table width="600px" class="inputTable">
					<tr>
	 					<td class="label rightalign" width="30%">Course ID:</td>
	 					<td id="<%= Common.PARAM_COURSE_ID %>"><%= helper.course.id %></td>
	 				</tr>
					<tr>
	 					<td class="label rightalign" width="30%">Course name:</td>
	 					<td id="<%= Common.PARAM_COURSE_NAME %>"><%=StudentCourseDetailsHelper.escapeForHTML(helper.course.name)%></td>
	 				</tr>
	 				<tr>
	 					<td class="label rightalign" width="30%">Coordinator name:</td>
	 					<td id="<%=Common.PARAM_COORD_NAME%>"><%=StudentCourseDetailsHelper.escapeForHTML(helper.coordName)%></td>
	 				</tr>
	 				<tr>
	 					<td class="label rightalign" width="30%">Your team:</td>
	 					<td id="<%=Common.PARAM_TEAM_NAME%>"><%=StudentCourseDetailsHelper.escapeForHTML(helper.student.team)%></td>
	 				</tr>
	 				<tr>
	 					<td class="label rightalign" width="30%">Your name:</td>
	 					<td id="<%=Common.PARAM_STUDENT_NAME%>"><%=StudentCourseDetailsHelper.escapeForHTML(helper.student.name)%></td>
	 				</tr>
	 				<tr>
	 					<td class="label rightalign" width="30%">Your e-mail:</td>
	 					<td id="<%=Common.PARAM_STUDENT_EMAIL%>"><%=helper.student.email%></td>
	 				</tr>
	 				<tr>
	 					<td class="label rightalign" width="30%">Your teammates:</td>
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
			 								<li><%=StudentCourseDetailsHelper.escapeForHTML(student.name)%></li>
			 							<%	} %>
			 						<%	} %>
		 						</ul>
		 					<%	} %>
	 					</td>
	 				</tr>
	 			</table>
	 			<br><br>
	 	   </div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>