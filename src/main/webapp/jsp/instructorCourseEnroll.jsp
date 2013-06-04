<%@ page import="java.util.List" %>
<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="teammates.ui.controller.InstructorCourseEnrollPageData"%>
<%
InstructorCourseEnrollPageData data = (InstructorCourseEnrollPageData)request.getAttribute("data");
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
		<jsp:include page="<%=Common.JSP_INSTRUCTOR_HEADER_NEW%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			
				<div id="headerOperation">
					<h1>Enroll Students for <%= data.courseId %></h1>
				</div>
				<form action="<%= data.getInstructorCourseEnrollSaveLink(data.courseId) %>" method="post">
					<p class ="bold rightalign spreadsheetLink">		
						[ <a id ="spreadsheet_download" 
							class="color_black t_course_enroll"
							href="/files/Course%20Enroll%20Sample%20Spreadsheet.csv"
							onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_COURSE_ENROLL_SAMPLE_SPREADSHEET %>')"
							onmouseout="hideddrivetip()">Sample spreadsheet</a> ] 
					</p>
					<img src="/images/enrollInstructions.png" border="0" > 
					<p class="info centeralign bold">Recommended maximum class size : 250 students</p>
					<br>
				 	<table class="inputTable enrollStudentTable" > 
						<tr>
							<td class="label bold middlealign" id="studentDetails"> Student details: </td>
							<td><textarea rows="6" cols="120" class ="textvalue" name="enrollstudents" id="enrollstudents" placeholder="This box can be used for enrolling new students and editing details (except email address) of students already enrolled. To EDIT, simply enroll students using the updated data and existing data will be updated accordingly. To DELETE students or to UPDATE EMAIL address of a student,please use the 'view' page of the course."></textarea></td>
						</tr>
					</table>
					<jsp:include page="<%= Common.JSP_STATUS_MESSAGE_NEW %>" />
					<br>
					<div id="instructorCourseEnrollmentButtons" class="centeralign">
						<input type="submit" class="button" name="button_enroll" id="button_enroll" value="Enroll students"
							onclick="return checkEnrollmentInput(document.getElementById('enrollstudents').value)">
					</div>
				</form>
				<br>
				<br>
				<br>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER_NEW %>" />
	</div>
</body>
</html>