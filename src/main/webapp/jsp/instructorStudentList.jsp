<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.TeamDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForJs"%>
<%@ page import="teammates.ui.controller.InstructorStudentListPageData"%>
<%
	InstructorStudentListPageData data = (InstructorStudentListPageData) request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
	<head>
		<link rel="shortcut icon" href="/favicon.png" />
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>TEAMMATES - Instructor</title>
		<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="/stylesheets/instructorStudentList.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print" />

		<script type="text/javascript" src="/js/googleAnalytics.js"></script>
		<script type="text/javascript" src="/js/jquery-minified.js"></script>
		<script type="text/javascript" src="/js/tooltip.js"></script>
		<script type="text/javascript" src="/js/date.js"></script>
		<script type="text/javascript" src="/js/CalendarPopup.js"></script>
		<script type="text/javascript" src="/js/AnchorPosition.js"></script>
		<script type="text/javascript" src="/js/common.js"></script>
		
		<script type="text/javascript" src="/js/instructor.js"></script>
		<script type="text/javascript" src="/js/instructorStudentList.js"></script>
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
					<h1>Student List</h1>
				</div>
	
				<table class="inputTable" id="searchTable">
					<tr>
						<td><input type="text" id="searchbox"
							onmouseover="ddrivetip('Search for student\'s name or course name')"
							onmouseout="hideddrivetip()" tabindex="1"></td>
						<td><input id="button_search" type="submit" class="button"
							onclick="return searchName()" value="Search" tabindex="2"></td>
					</tr>
				</table>
	
				<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
				<% 	
					int courseIdx = -1;
					int studentIdx = 0;
					if(data.courses.size() > 0){
				%>
						<br><br>
						<a class="color_black" id="show_email" href=""
							onmouseover="ddrivetip('<%=Const.Tooltips.SHOW_EMAILS%>')"
							onmouseout="hideddrivetip()"
							onclick="toggleEmailView(); return false;">Show student e-mails</a>
						<div class="emails" style="display: none;">
							<div class="student_emails">
								<h4 class="bold">Emails of all currently displayed student:</h4>
								<ul>
					<%
						for(CourseDetailsBundle courseDetails: data.courses){
							courseIdx++;
							int totalCourseStudents = courseDetails.stats.studentsTotal;
					%>
						
						<%
							if(totalCourseStudents >= 1){
						%>
								<li class="student_email" id="student_email-<%=studentIdx%>" style="display: list-item;"><%=data.students.get(studentIdx).email %></li>
						<%
							}
							for(int i = studentIdx+1; i < studentIdx + totalCourseStudents; i++) {
						%>
								<li class="student_email" id="student_email-<%=i%>" style="display: list-item;"><%=data.students.get(i).email %></li>
					<%
							}
							studentIdx += totalCourseStudents;
						}
					%>
								</ul>
							</div>
						</div>
				<%
					}
					courseIdx = -1;
					studentIdx = 0;
					for (CourseDetailsBundle courseDetails : data.courses) {
						courseIdx++;
						int totalCourseStudents = courseDetails.stats.studentsTotal;
				%>
	
				<div class="backgroundBlock" id="course-<%=courseIdx%>">
					<div class="courseTitle">
						<h2 class="color_white">
							[<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(courseDetails.course.name)%>
						</h2>
					</div>
					<div class="enrollLink blockLink rightalign">
						<a class="t_course_enroll-<%=courseIdx%> color_white bold"
							href="<%=data.getInstructorCourseEnrollLink(courseDetails.course.id)%>"
							onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_ENROLL%>')"
							onmouseout="hideddrivetip()"> Enroll Students</a>
					</div>
					<div style="clear: both;"></div>
					<br>
					<%
						if (totalCourseStudents > 0) {
					%>
					<table class="dataTable">
						<tr>
							<th class="leftalign color_white bold"><input
								class="buttonSortAscending" type="button"
								id="button_sortstudentname" onclick="toggleSort(this,1)">Student Name</th>
							<th></th>
						</tr>
						<%
							for (int i = studentIdx; i < studentIdx + totalCourseStudents; i++) {
						%>
						<tr class="student_row" id="student-<%=i%>" style="display: table-row;">
							<td id="studentname"><%=PageData.sanitizeForHtml(data.students.get(i).name)%></td>
							<td class="centeralign no-print">
								<a class="color_black t_student_details-<%=i%>" 
								href="<%=data.getCourseStudentDetailsLink(courseDetails.course.id, data.students.get(i))%>"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_DETAILS%>')"
								onmouseout="hideddrivetip()"> View</a> 
								
								<a class="color_black t_student_edit-<%=i%>"
								href="<%=data.getCourseStudentEditLink(courseDetails.course.id, data.students.get(i))%>"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_EDIT%>')"
								onmouseout="hideddrivetip()"> Edit</a> 
								
								<a class="color_black t_student_delete-<%=i%>"
								href="<%=data.getCourseStudentDeleteLink(courseDetails.course.id, data.students.get(i))%>"
								onclick="return toggleDeleteStudentConfirmation('<%=sanitizeForJs(courseDetails.course.id)%>','<%=sanitizeForJs(data.students.get(i).name)%>')"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_DELETE%>')"
								onmouseout="hideddrivetip()"> Delete</a>
								
								<a class="color_black t_student_records-<%=i%>"
								href="<%=data.getStudentRecordsLink(courseDetails.course.id, data.students.get(i))%>"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_RECORDS%>')"
								onmouseout="hideddrivetip()"> All Records</a>
							</td>
						</tr>
						<%
							}
							studentIdx += totalCourseStudents;
						%>
					</table>
					<%
						} else {
					%>
					<table class="dataTable">
						<tr>
							<th class="centeralign color_white bold"><%=Const.StatusMessages.INSTRUCTOR_COURSE_EMPTY %></th>
						</tr>
					</table>
					<%
						}
					%>
				</div>
				<%
					out.flush();
					}
				%>
			</div>
			<br> <br> <br>
		</div>
	
	
	
	
		<div id="frameBottom">
			<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
		</div>
	</body>
</html>