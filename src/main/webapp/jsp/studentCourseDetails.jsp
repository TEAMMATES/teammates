<%@ page import="teammates.api.Common" %>
<%@ page import="teammates.datatransfer.StudentData" %>
<%@ page import="teammates.ui.StudentCourseDetailsHelper"%>
<% StudentCourseDetailsHelper helper = (StudentCourseDetailsHelper)request.getAttribute("helper"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Student</title>
	<link rel=stylesheet href="/stylesheets/main.css" type="text/css" />
	<link rel=stylesheet href="/stylesheets/evaluation.css" type="text/css" />

	<script language="JavaScript" src="/js/jquery-1.6.2.min.js"></script>
	<script language="JavaScript" src="/js/tooltip.js"></script>
	
	<script language="JavaScript" src="/js/studentNew.js"></script>	
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
				<table width="600" class="detailform">
					<tr>
	 					<td>Course ID:</td>
	 					<td id="<%= Common.PARAM_COURSE_ID %>"><%= helper.course.id %></td>
	 				</tr>
					<tr>
	 					<td>Course name:</td>
	 					<td id="<%= Common.PARAM_COURSE_NAME %>"><%=StudentCourseDetailsHelper.escapeForHTML(helper.course.name)%></td>
	 				</tr>
	 				<tr>
	 					<td>Coordinator name:</td>
	 					<td id="<%=Common.PARAM_COORD_NAME%>"><%=StudentCourseDetailsHelper.escapeForHTML(helper.coordName)%></td>
	 				</tr>
	 				<tr>
	 					<td>Your team:</td>
	 					<td id="<%=Common.PARAM_TEAM_NAME%>"><%=StudentCourseDetailsHelper.escapeForHTML(helper.student.team)%></td>
	 				</tr>
	 				<tr>
	 					<td>Your name:</td>
	 					<td id="<%=Common.PARAM_STUDENT_NAME%>"><%=StudentCourseDetailsHelper.escapeForHTML(helper.student.name)%></td>
	 				</tr>
	 				<tr>
	 					<td>Your e-mail:</td>
	 					<td id="<%=Common.PARAM_STUDENT_EMAIL%>"><%=helper.student.email%></td>
	 				</tr>
	 				<tr>
	 					<td>Your teammates:</td>
	 					<td id="<%=Common.PARAM_TEAMMATES%>">
	 						<%
	 							if(helper.team==null || helper.team.students.size()==1){
	 						%>
	 							<span style="font-style:italic">You have no team members or you are not registered in any team</span>
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
	 			<br /><br />
	 			<input type="button" class="button" id="button_back" value="Back"
	 					onclick="window.location.href='<%= helper.getStudentHomeLink() %>'" />
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>