<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="teammates.ui.controller.InstructorCourseEnrollPageData"%>
<%
	InstructorCourseEnrollPageData data = (InstructorCourseEnrollPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>TEAMMATES - Instructor</title>
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
					<h1>Enroll Students for <%=sanitizeForHtml(data.courseId)%></h1>
				</div>
				<form action="<%=data.getInstructorCourseEnrollSaveLink(data.courseId)%>" method="post">
					<p class ="bold rightalign spreadsheetLink">		
						[ <a id ="spreadsheet_download" 
							class="color_black t_course_enroll"
							href="/files/Course%20Enroll%20Sample%20Spreadsheet.csv"
							onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_ENROLL_SAMPLE_SPREADSHEET%>')"
							onmouseout="hideddrivetip()">Sample spreadsheet</a> ] 
					</p>
					<img src="/images/enrollInstructions.png" border="0" > 
					<p class="info centeralign bold">Recommended maximum class size : 250 students</p>
					<br>
				 	<table class="inputTable enrollStudentTable" > 
						<tr>
							<td class="label bold middlealign" id="studentDetails"> Student details: </td>
							<td>
							<%-- The placeholder message must be written in a single line to avoid display problem in some browsers --%>
								<textarea rows="6" cols="120" class ="textvalue" name="enrollstudents" id="enrollstudents" 
									placeholder="This box can be used for enrolling new students and editing details (except email address) of students already enrolled. To EDIT, simply enroll students using the updated data and existing data will be updated accordingly. To DELETE students or to UPDATE EMAIL address of a student,please use the 'view' page of the course."><%=data.enrollStudents%></textarea>
							</td>
						</tr>
					</table>
					<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
					<br>
					<div id="instructorCourseEnrollmentButtons" class="centeralign">
						<input type="submit" class="button" name="button_enroll" id="button_enroll" value="Enroll students">
					</div>
				</form>
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