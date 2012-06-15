<%@ page import="teammates.api.Common"%>
<%@ page import="teammates.datatransfer.CourseData"%>
<%@ page import="teammates.datatransfer.StudentData"%>
<%@ page import="teammates.datatransfer.TeamData"%>
<%@ page import="teammates.jsp.CoordCourseDetailsHelper"%>
<%	CoordCourseDetailsHelper helper = (CoordCourseDetailsHelper)request.getAttribute("helper"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Coordinator</title>
	<link rel=stylesheet href="/stylesheets/main.css" type="text/css" />
	<link rel=stylesheet href="/stylesheets/evaluation.css" type="text/css" />
	
	<script language="JavaScript" src="/js/jquery-1.6.2.min.js"></script>
	<script language="JavaScript" src="/js/tooltip.js"></script>
	<script language="JavaScript" src="/js/date.js"></script>
	<script language="JavaScript" src="/js/CalendarPopup.js"></script>
	<script language="JavaScript" src="/js/AnchorPosition.js"></script>
	<script language="JavaScript" src="/js/helperNew.js"></script>
	<script language="JavaScript" src="/js/commonNew.js"></script>
	
	<script language="JavaScript" src="/js/coordinatorNew.js"></script>
	<script language="JavaScript" src="/js/coordCourse.js"></script>

</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%= Common.JSP_COORD_HEADER %>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Course Details</h1>
			</div>
			<div id="coordinatorCourseInformation">
				<table class="headerform">
					<tr>
		 				<td class="fieldname">Course ID:</td>
		 				<td><%= helper.course.id %></td>
		 			</tr>
		 			<tr>
		 				<td class="fieldname">Course name:</td>
		 				<td><%= CoordCourseDetailsHelper.escapeHTML(helper.course.name) %></td>
					</tr>
					<tr>
		 				<td class="fieldname">Teams:</td>
		 				<td><%= helper.course.teamsTotal %></td>
		 			</tr>
		 			<tr>
		 				<td class="fieldname">Total students:</td>
		 				<td><%= helper.course.studentsTotal %></td>
		 			</tr>
		 			<%	if(helper.course.studentsTotal>1){ %>
		 			<tr>
		 				<td class="centeralign" colspan="2">
		 					<input type="button" class="button t_remind_students"
		 							id="button_remind"
		 							onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_COURSE_REMIND %>')" 
		 							onmouseout="hideddrivetip();" 
		 							onclick="hideddrivetip(); toggleSendRegistrationKeysConfirmation('<%= helper.course.id %>');"
		 							value="Remind Students to Join" tabindex=1 />
		 				</td>
		 			</tr>
		 			<%	} %>
				</table>
			</div>
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
			<div id="coordinatorStudentTable">
				<table id="dataform">
					<tr>
						<th><input class="buttonSortAscending" type="button" id="button_sortstudentname" 
								onclick="toggleSort(this,1)"/>Student Name</th>
						<th><input class="buttonSortNone" type="button" id="button_sortstudentteam"
								onclick="toggleSort(this,2)"/>Team</th>
						<th class="centeralign"><input class="buttonSortNone" type="button" id="button_sortstudentstatus"
								onclick="toggleSort(this,3)"/>Status</th>
						<th class="centeralign">Action(s)</th>
					</tr>
					<%	for(StudentData student: helper.students){ %>
							<tr>
								<td><%= student.name %></td>
	 							<td><%= CoordCourseDetailsHelper.escapeHTML(student.team) %></td>
	 							<td class="centeralign"><%= helper.status(student) %></td>
	 							<td class="centeralign">
									<a class="t_student_details" href="<%= helper.getCourseStudentDetailsLink(student) %>"
											onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_COURSE_STUDENT_DETAILS %>')"
											onmouseout="hideddrivetip()">
											View</a>
									<a class="t_student_edit" href="<%= helper.getCourseStudentEditLink(student) %>"
											onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_COURSE_STUDENT_EDIT %>')"
											onmouseout="hideddrivetip()">
											Edit</a>
									<%	if(helper.status(student).equals(Common.STUDENT_STATUS_YET_TO_JOIN)){ %>
										<a class="t_student_resend" href="<%= helper.getCourseStudentRemindLink(student) %>"
												onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_COURSE_STUDENT_REMIND %>')"
												onmouseout="hideddrivetip()">
												Resend Invite</a>
									<%	} %>
									<a class="t_student_delete" href="<%= helper.getCourseStudentDeleteLink(student) %>"
											onclick="return toggleDeleteStudentConfirmation('<%= CoordCourseDetailsHelper.escape(student.name) %>')"
											onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_COURSE_STUDENT_DELETE %>')"
											onmouseout="hideddrivetip()">
											Delete</a>
								</td>
	 						</tr>
					<%	} %>
				</table>
				<br /><br />
				<input type="button" class="button" onclick="window.location.href='<%= Common.PAGE_COORD_COURSE %>'" value="Back" />
				<br /><br />
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>