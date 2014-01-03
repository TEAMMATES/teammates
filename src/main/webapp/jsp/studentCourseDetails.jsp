<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.StudentAttributes" %>
<%@ page import="teammates.common.datatransfer.InstructorAttributes" %>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="teammates.ui.controller.StudentCourseDetailsPageData"%>
<%
	StudentCourseDetailsPageData data = (StudentCourseDetailsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>TEAMMATES - Student</title>
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
		<jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Team Details for <%=data.courseDetails.course.id%></h1>
			</div>
			<br>
			<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
			<br>
				
			<table class="inputTable" id="studentCourseInformation">
				<tr>
	 				<td class="label rightalign bold" width="30%">Course ID:</td>
	 				<td id="<%=Const.ParamsNames.COURSE_ID%>"><%=data.courseDetails.course.id%></td>
	 			</tr>
				<tr>
	 				<td class="label rightalign bold" width="30%">Course name:</td>
	 				<td id="<%=Const.ParamsNames.COURSE_NAME%>"><%=PageData.sanitizeForHtml(data.courseDetails.course.name)%></td>
	 			</tr>
	 			<tr>
	 				<td class="label rightalign bold" width="30%">Instructors:</td>
	 				<td id="<%=Const.ParamsNames.INSTRUCTOR_NAME%>">
	 				<%
	 					for (int i = 0; i < data.instructors.size(); i++){
	 					 					 					 					 					 	InstructorAttributes instructor = data.instructors.get(i);
	 					 					 					 					 					 	String instructorInfo = instructor.name + " (" + instructor.email + ")";
	 				%>
		 				<a href = "mailto:<%=instructor.email%>"><%=instructorInfo%></a><br><br>
		 			<%
		 				}
		 			%>
	 				</td>
	 			</tr>
	 			<tr>
	 				<td class="label rightalign bold" width="30%">Your team:</td>
	 				<td id="<%=Const.ParamsNames.TEAM_NAME%>"><%=PageData.sanitizeForHtml(data.student.team)%></td>
	 			</tr>
	 			<tr>
	 				<td class="label rightalign bold" width="30%">Your name:</td>
	 				<td id="<%=Const.ParamsNames.STUDENT_NAME%>"><%=PageData.sanitizeForHtml(data.student.name)%></td>
	 			</tr>
	 			<tr>
	 				<td class="label rightalign bold" width="30%">Your e-mail:</td>
	 				<td id="<%=Const.ParamsNames.STUDENT_EMAIL%>"><%=data.student.email%></td>
	 			</tr>
	 			<tr>
	 				<td class="label rightalign bold" width="30%">Your teammates:</td>
	 				<td id="<%=Const.ParamsNames.TEAMMATES%>">
	 					<%
	 						if(data.team==null || data.team.students.size()==1){
	 					%>
	 						<span style="font-style: italic;">You have no team members or you are not registered in any team</span>
	 					<%
	 						} else {
	 					%>
	 						<ul>
									<%
										for(StudentAttributes student: data.team.students){
									%>
										<%
											if(!student.email.equals(data.student.email)) {
										%>
											<li><a href = "mailto:<%=student.email%>"><%=PageData.sanitizeForHtml(student.name)%></a></li>
										<%
											}
										%>
									<%
										}
									%>
		 					</ul>
		 				<%
		 					}
		 				%>
	 				</td>
	 			</tr>
	 		</table>	
	 		<br>
	 		<br>
	 		<br> 	   
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>