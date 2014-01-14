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
							onmouseover="ddrivetip('<%=Const.Tooltips.SEARCH_STUDENT%>')"
							onmouseout="hideddrivetip()" tabindex="1" value="<%=data.searchKey == null ? "" : PageData.sanitizeForHtml(data.searchKey) %>"></td>
						<td><input id="button_search" type="submit" class="button"
							onclick="return applyFilters();" value="Search" tabindex="2"></td>
					</tr>
					<tr>
						<td colspan="2">
							<input id="option_check" type="checkbox">
								<label for="option_check">
								Show More Options
								</label>
						</td>
					</tr>
				</table>
				<br><br>
				<table class="inputTable" id="optionsTable" style="display: none;">	
					<tr>
						<td width="250px">
							<h4 class="bold">Courses</h4>
							<div class="leftalign" id="course_checkboxes">
							<br>
								<ul>
								<li>
									<input id="course_all" type="checkbox" checked="checked">
									<label for="course_all" class="bold">Select All</label>
								</li>
								<%
									int courseIdx = -1;
									for(CourseDetailsBundle courseDetails: data.courses){
										courseIdx++;
								%>
									<li><input class="course_check" id="course_check-<%=courseIdx %>" type="checkbox" checked="checked">
										<label for="course_check-<%=courseIdx %>">
										[<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(courseDetails.course.name)%>
										</label>
									</li>
								<%
									}
								%>
								</ul>
							<br>
							</div>
						</td>
						<td width="250px">
							<h4 class="bold">Teams</h4>
							<div class="leftalign" id="team_checkboxes">
							<br>
							<ul>
								<li>
									<input id="team_all" type="checkbox" checked="checked">
									<label for="team_all" class="bold">Select All</label>
								</li>
								<%
									courseIdx = -1;
									for(CourseDetailsBundle courseDetails: data.courses){
										courseIdx++;
										int teamIdx = -1;
										for(TeamDetailsBundle teamDetails: courseDetails.teams){
											teamIdx++;
								%>
									<li><input class="team_check" id="team_check-<%=courseIdx %>-<%=teamIdx %>" type="checkbox" checked="checked">
										<label for="team_check-<%=courseIdx %>-<%=teamIdx%>">
										[<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(teamDetails.name)%>
										</label>
									</li>
								<%
										}
									}
								%>
							</ul>
							<br>
							</div>
						</td>
						<td width="250px">
							<h4 class="bold">
								<input id="show_email" type="checkbox">
									<label for="show_email">
									Show Emails
									</label>
							</h4>
							<div class="leftalign" id="emails" style="display: none;">
							<br>
							<ul>
							<%
								
								courseIdx = -1;
								for(CourseDetailsBundle courseDetails: data.courses){
									courseIdx++;
									int totalCourseStudents = courseDetails.stats.studentsTotal;
									if(totalCourseStudents >= 1){
										int studentIdx = -1;
										for(TeamDetailsBundle teamDetails: courseDetails.teams){
											for(StudentAttributes student: teamDetails.students){
												studentIdx++;
							%>
									<li class="student_email" id="student_email-c<%=courseIdx %>.<%=studentIdx%>" style="display: list-item;"><%=student.email %></li>
							<%
											}
										}
									}
								}
							%>
							</ul>
							<br>
							</div>
						</td>
					</tr>
				</table>
	
				<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
				<%
					courseIdx = -1;
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
							<th class="leftalign color_white bold">
								<input class="buttonSortAscending" type="button"
								id="button_sortteam" onclick="toggleSort(this,1)">Team
							</th>
							<th class="leftalign color_white bold"><input
								class="buttonSortNone" type="button"
								id="button_sortstudentname" onclick="toggleSort(this,2)">Student Name</th>
							<th class="centeralign color_white bold no-print">Action(s)</th>
						</tr>
						<%
							int teamIdx = -1;
							int studentIdx = -1;
							for(TeamDetailsBundle teamDetails: courseDetails.teams){
								teamIdx++;
								for(StudentAttributes student: teamDetails.students){
									studentIdx++;
						%>
						<tr class="student_row" id="student-c<%=courseIdx %>.<%=studentIdx%>" style="display: table-row;">
							<td id="studentteam-c<%=courseIdx %>.<%=teamIdx%>"><%=PageData.sanitizeForHtml(teamDetails.name)%></td>
							<td id="studentname-c<%=courseIdx %>.<%=studentIdx%>"><%=PageData.sanitizeForHtml(student.name)%></td>
							<td class="centeralign no-print">
								<a class="color_black t_student_details-c<%=courseIdx %>.<%=studentIdx%>" 
								href="<%=data.getCourseStudentDetailsLink(courseDetails.course.id, student)%>"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_DETAILS%>')"
								onmouseout="hideddrivetip()"> View</a> 
								
								<a class="color_black t_student_edit-c<%=courseIdx %>.<%=studentIdx%>"
								href="<%=data.getCourseStudentEditLink(courseDetails.course.id, student)%>"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_EDIT%>')"
								onmouseout="hideddrivetip()"> Edit</a> 
								
								<a class="color_black t_student_delete-c<%=courseIdx %>.<%=studentIdx%>"
								href="<%=data.getCourseStudentDeleteLink(courseDetails.course.id, student)%>"
								onclick="return toggleDeleteStudentConfirmation('<%=sanitizeForJs(courseDetails.course.id)%>','<%=sanitizeForJs(student.name)%>')"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_DELETE%>')"
								onmouseout="hideddrivetip()"> Delete</a>
								
								<a class="color_black t_student_records-c<%=courseIdx %>.<%=studentIdx%>"
								href="<%=data.getStudentRecordsLink(courseDetails.course.id, student)%>"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_RECORDS%>')"
								onmouseout="hideddrivetip()"> All Records</a>
							</td>
						</tr>
						<%
								}
							}
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