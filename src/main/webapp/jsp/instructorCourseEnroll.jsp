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
					<div class="instructionImg">
						<img src="/images/enrollInstructions.gif" border="0" > 
					</div>
					<p class="info centeralign bold">Recommended maximum class size : 250 students</p>
					<br>
				 	<table class="inputTable enrollStudentTable" > 
						<tr>
							<td class="label bold middlealign" id="studentDetails"> Student details: </td>
							<td>
							<%-- The placeholder message must be written in a single line to avoid display problem in some browsers --%>
								<textarea rows="6" cols="120" class ="textvalue" name="enrollstudents" id="enrollstudents" 
									placeholder="Paste student data here ..."><%=data.enrollStudents%></textarea>
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
				
				<div class="moreInfo">
				<br>More info:
				<br>
				<ul>
					<li><span class="bold">Sample spreadsheet</span>: If you are not sure about the format of the spreadsheet, here is a sample file.</li>
					<li><span class="bold">Column headings</span>: The column order is not important. Column headings are not case sensitive. e.g. Team, TEAM, team are all acceptable.</li>
					<li><span class="bold">Columns</span>:
						<ul>
							<li>Team [Compulsory]: team name/ID</li>
							<li>Name [Compulsory]: Student name</li>
							<li>Email [Compulsory]: The email address used to contact the student.<br>
							This need not be a Gmail address.<br>
							It should be unique for each student. If two students are given the same email, they will be considered the same student.</li>
							<li>Comments [Optional]: Any other information you want to record about a student.</li>
						</ul>
					<li><span class="bold">Mass editing enrolled students</span>: The text box above can be used for mass-editing details (except email address) of students already enrolled.
					To edit, simply enroll students using the updated data and existing data will be updated accordingly.
						<ul>
							<li>To DELETE students or to UPDATE EMAIL address of a student, please go to the ‘courses’ page and click the 'view' link of the course.</li>
						</ul>
					</li>
					<li><span class="bold">Enrolling without spreadsheets</span>:  The alternative is to type student data in the text box, using the pipe symbol (also called the vertical bar, 
					not to be confused with upper case i or lower case L).
						<br>Here is an example.
						<br><span class="enrollLines">Team   |   Name   |   Email   |   Comments</span>
						<br><span class="enrollLines">Team 1   |   Tom Jacobs  |  tom@email.com</span>
						<br><span class="enrollLines">Team 1  |   Jean Wong   |   jean@email.com   |   Exchange Student</span>
					</li>
				</ul>
				<br>
				</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>