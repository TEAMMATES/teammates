<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="teammates.common.datatransfer.InstructorAttributes"%>
<%@ page import="teammates.common.datatransfer.TeamResultBundle"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForJs"%>
<%@ page import="teammates.ui.controller.InstructorCourseDetailsPageData"%>
<%
	InstructorCourseDetailsPageData data = (InstructorCourseDetailsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>TEAMMATES - Instructor</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/instructorCourseDetails.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/instructorCourseDetails-print.css" type="text/css" media="print">
	
	
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
		<jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />
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
	 				<td id="courseid"><%=sanitizeForHtml(data.courseDetails.course.id)%></td>
	 			</tr>
	 			<tr>
		 			<td class="label rightalign bold" width="30%">Course name:</td>
		 			<td id="coursename"><%=sanitizeForHtml(data.courseDetails.course.name)%></td>
				</tr>
				<tr>
		 			<td class="label rightalign bold" width="30%">Teams:</td>
		 			<td id="total_teams"><%=data.courseDetails.stats.teamsTotal%></td>
		 		</tr>
		 		<tr>
		 			<td class="label rightalign bold" width="30%">Total students:</td>
		 			<td id="total_students"><%=data.courseDetails.stats.studentsTotal%></td>
		 		</tr>
		 		<tr>
		 			<td class="label rightalign bold" width="30%">Instructors:</td>
		 			<td id="instructors">
		 			<%
		 				for (int i = 0; i < data.instructors.size(); i++){
		 					 					 				 					 	InstructorAttributes instructor = data.instructors.get(i);
		 					 					 				 					 	String instructorInfo = instructor.name + " (" + instructor.email + ")";
		 			%>
				 				<%=sanitizeForHtml(instructorInfo)%><br><br>
				 			<%
				 				}
				 			%>
					</td>
		 		</tr>
		 		<%
		 			if(data.courseDetails.stats.studentsTotal>1){
		 		%>
		 		<tr>
		 			<td class="centeralign" colspan="2">
		 				<input type="button" class="button t_remind_students"
		 						id="button_remind"
		 						onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_REMIND%>')" 
		 						onmouseout="hideddrivetip();"
		 						onclick="hideddrivetip(); if(toggleSendRegistrationKeysConfirmation('<%=data.courseDetails.course.id%>')) window.location.href='<%=data.getInstructorCourseRemindLink()%>';"
		 						value="Remind Students to Join" tabindex="1">
		 			</td>
		 		</tr>
		 		<%
		 			}
		 		%>
			</table>
			
			<br>
			<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
			<br>

			<table class="dataTable">
				<tr>
					<th class="centeralign color_white bold"><input class="buttonSortNone" type="button" id="button_sortstudentteam"
							onclick="toggleSort(this,1)">Team</th>
					<th class="centeralign color_white bold"><input class="buttonSortAscending" type="button" id="button_sortstudentname" 
							onclick="toggleSort(this,2)">Student Name</th>
					<th class="centeralign color_white bold"><input class="buttonSortNone" type="button" id="button_sortstudentstatus"
							onclick="toggleSort(this,3)">Status</th>
					<th class="centeralign color_white bold no-print">Action(s)</th>
				</tr>
				<%
					int idx = -1;
																for(StudentAttributes student: data.students){ idx++;
				%>
						<tr class="student_row" id="student<%=idx%>">
							<td id="<%=Const.ParamsNames.TEAM_NAME%>"><%=sanitizeForHtml(student.team)%></td>
							<td id="<%=Const.ParamsNames.STUDENT_NAME%>"><%=sanitizeForHtml(student.name)%></td>
	 						<td class="centeralign"><%=data.getStudentStatus(student)%></td>
	 						<td class="centeralign no-print">
								<a class="color_black t_student_details<%=idx%>"
										href="<%=data.getCourseStudentDetailsLink(student)%>"
										onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_DETAILS%>')"
										onmouseout="hideddrivetip()">
										View</a>
								<a class="color_black t_student_edit<%=idx%>" href="<%=data.getCourseStudentEditLink(student)%>"
										onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_EDIT%>')"
										onmouseout="hideddrivetip()">
										Edit</a>
								<%
									if(data.getStudentStatus(student).equals(Const.STUDENT_COURSE_STATUS_YET_TO_JOIN)){
								%>
									<a class="color_black t_student_resend<%=idx%>" href="<%=data.getCourseStudentRemindLink(student)%>"
											onclick="return toggleSendRegistrationKey()"
											onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_REMIND%>')"
											onmouseout="hideddrivetip()">
											Send Invite</a>
								<%
									}
								%>
								<a class="color_black t_student_delete<%=idx%>" href="<%=data.getCourseStudentDeleteLink(student)%>"
										onclick="return toggleDeleteStudentConfirmation('<%=sanitizeForJs(student.name)%>')"
										onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_DELETE%>')"
										onmouseout="hideddrivetip()">
										Delete</a>
							</td>
	 					</tr>
	 				<%
	 					if(idx%10==0) out.flush();
	 				%>
				<%
					}
				%>
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