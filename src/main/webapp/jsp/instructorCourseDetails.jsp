<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.datatransfer.CourseData"%>
<%@ page import="teammates.common.datatransfer.StudentData"%>
<%@ page import="teammates.common.datatransfer.InstructorData"%>
<%@ page import="teammates.common.datatransfer.TeamData"%>
<%@ page import="teammates.ui.controller.InstructorCourseDetailsHelper"%>
<%	InstructorCourseDetailsHelper helper = (InstructorCourseDetailsHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Instructor</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css">
	<link rel="stylesheet" href="/stylesheets/instructorCourseDetails.css" type="text/css">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/instructor.js"></script>
	<script type="text/javascript" src="/js/instructorCourseDetails.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%= Common.JSP_INSTRUCTOR_HEADER %>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Course Details</h1>
			</div>

			<table class="inputTable" id="courseInformationHeader">
				<tr>
	 				<td class="label rightalign bold" width="30%">Course ID:</td>
	 				<td id="courseid"><%= helper.course.id %></td>
	 			</tr>
	 			<tr>
		 			<td class="label rightalign bold" width="30%">Course name:</td>
		 			<td id="coursename"><%=InstructorCourseDetailsHelper.escapeForHTML(helper.course.name)%></td>
				</tr>
				<tr>
		 			<td class="label rightalign bold" width="30%">Teams:</td>
		 			<td id="total_teams"><%=helper.course.teamsTotal%></td>
		 		</tr>
		 		<tr>
		 			<td class="label rightalign bold" width="30%">Total students:</td>
		 			<td id="total_students"><%=helper.course.studentsTotal%></td>
		 		</tr>
		 		<tr>
		 			<td class="label rightalign bold" width="30%">Instructors:</td>
		 			<td id="instructors">
		 			<%
		 				for (int i = 0; i < helper.instructors.size(); i++){
		 					InstructorData instructor = helper.instructors.get(i);
		 					String instructorInfo = instructor.name + " (" + instructor.email + ")";
		 			%>
		 				<%=instructorInfo %><br><br>
		 			<%
		 				}
		 			%>
					</td>
		 		</tr>
		 		<%
		 			if(helper.course.studentsTotal>1){
		 		%>
		 		<tr>
		 			<td class="centeralign" colspan="2">
		 				<input type="button" class="button t_remind_students"
		 						id="button_remind"
		 						onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_COURSE_REMIND%>')" 
		 						onmouseout="hideddrivetip();"
		 						onclick="hideddrivetip(); if(toggleSendRegistrationKeysConfirmation('<%=helper.course.id%>')) window.location.href='<%=helper.getInstructorCourseRemindLink()%>';"
		 						value="Remind Students to Join" tabindex="1">
		 			</td>
		 		</tr>
		 		<%
		 			}
		 		%>
			</table>
			
			<br>
			<jsp:include page="<%=Common.JSP_STATUS_MESSAGE%>" />
			<br>

			<table class="dataTable">
				<tr>
					<th class="color_white bold"><input class="buttonSortAscending" type="button" id="button_sortstudentname" 
							onclick="toggleSort(this,1)">Student Name</th>
					<th class="centeralign color_white bold"><input class="buttonSortNone" type="button" id="button_sortstudentteam"
							onclick="toggleSort(this,2)">Team</th>
					<th class="centeralign color_white bold"><input class="buttonSortNone" type="button" id="button_sortstudentstatus"
							onclick="toggleSort(this,3)">Status</th>
					<th class="centeralign color_white bold">Action(s)</th>
				</tr>
				<%
					int idx = -1;
								for(StudentData student: helper.students){ idx++;
				%>
						<tr class="student_row" id="student<%=idx%>">
							<td id="<%=Common.PARAM_STUDENT_NAME%>"><%=student.name%></td>
	 						<td id="<%=Common.PARAM_TEAM_NAME%>"><%=InstructorCourseDetailsHelper.escapeForHTML(student.team)%></td>
	 						<td class="centeralign"><%= helper.status(student) %></td>
	 						<td class="centeralign">
								<a class="color_black t_student_details<%= idx %>"
										href="<%= helper.getCourseStudentDetailsLink(student) %>"
										onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_COURSE_STUDENT_DETAILS %>')"
										onmouseout="hideddrivetip()">
										View</a>
								<a class="color_black t_student_edit<%= idx %>" href="<%= helper.getCourseStudentEditLink(student) %>"
										onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_COURSE_STUDENT_EDIT %>')"
										onmouseout="hideddrivetip()">
										Edit</a>
								<%	if(helper.status(student).equals(Common.STUDENT_STATUS_YET_TO_JOIN)){ %>
									<a class="color_black t_student_resend<%= idx %>" href="<%= helper.getCourseStudentRemindLink(student) %>"
											onclick="return toggleSendRegistrationKey()"
											onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_COURSE_STUDENT_REMIND %>')"
											onmouseout="hideddrivetip()">
											Send Invite</a>
								<%	} %>
								<a class="color_black t_student_delete<%= idx %>" href="<%= helper.getCourseStudentDeleteLink(student) %>"
										onclick="return toggleDeleteStudentConfirmation('<%=InstructorCourseDetailsHelper.escapeForJavaScript(student.name)%>')"
										onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_COURSE_STUDENT_DELETE %>')"
										onmouseout="hideddrivetip()">
										Delete</a>
							</td>
	 					</tr>
	 				<% if(idx%10==0) out.flush(); %>
				<%	} %>
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