<%@ page import="teammates.api.Common"%>
<%@ page import="teammates.datatransfer.CourseData"%>
<%@ page import="teammates.jsp.CoordCourseHelper"%>
<%
	CoordCourseHelper helper = (CoordCourseHelper)request.getAttribute("helper");
%>
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
				<h1>Add New Course</h1>
			</div>
			<div id="coordinatorCourseManagement">
				<form method="get" action="<%= Common.PAGE_COORD_COURSE %>" name="form_addcourse">
					<table class="addform round">
						<tr>
							<td><b>Course ID:</b></td>
						</tr>
						<tr>
							<td><input class="addinput" type="text"
								name="<%= Common.PARAM_COURSE_ID %>" id="<%= Common.PARAM_COURSE_ID %>"
								value="<%= (helper.courseID==null?"":helper.courseID) %>"
								onmouseover="ddrivetip('Enter the identifier of the course, e.g.CS3215-Sem1.')"
								onmouseout="hideddrivetip()"
								maxlength=<%= Common.COURSE_ID_MAX_LENGTH %> tabindex=1 /></td>
						</tr>
						<tr>
							<td><b>Course Name:</b></td>
						</tr>
						<tr>
							<td><input class="addinput" type="text"
								name="<%= Common.PARAM_COURSE_NAME %>" id="<%= Common.PARAM_COURSE_NAME %>"
								value="<%= (helper.courseName==null?"":CoordCourseHelper.escapeHTML(helper.courseName)) %>"
								onmouseover="ddrivetip('Enter the name of the course, e.g. Software Engineering.')"
								onmouseout="hideddrivetip()"
								maxlength=<%= Common.COURSE_NAME_MAX_LENGTH %> tabindex=2 /></td>
						</tr>
						<tr>
							<td><input id="btnAddCourse" type="submit" class="button"
								onclick="return verifyAddCourse();" value="Add Course" tabindex="3" /></td>
						</tr>
					</table>
					<% if(helper.isMasqueradeMode()){ %>
						<input type="hidden" name="<%= Common.PARAM_USER_ID %>" value="<%= helper.requestedUser %>" />
					<% } %>
				</form>
			</div>
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
			<div id="coordinatorCourseTable">
				<table id="dataform">
					<tr>
						<th><input class="buttonSortAscending" type="button"
							id="button_sortcourseid"
							onclick="toggleSort(this,1);" />
							Course ID</th>
						<th><input class="buttonSortNone" type="button"
							id="button_sortcoursename"
							onclick="toggleSort(this,2);" />
							Course Name</th>
						<th class="centeralign">Teams</th>
						<th class="centeralign">Total Students</th>
						<th class="centeralign">Total Unregistered</th>
						<th class="centeralign">Action(s)</th>
					</tr>
					<%	
						int idx = -1;
						for(CourseData course: helper.courses){ idx++;
					%>
						<tr class="courses_row">
							<td id="courseid<%= idx %>"><%= course.id %></td>
							<td id="coursename<%= idx %>"><%= CoordCourseHelper.escapeHTML(course.name) %></td>
							<td class="t_course_teams centeralign"><%= course.teamsTotal %></td>
							<td class="centeralign"><%= course.studentsTotal %></td>
							<td class="centeralign"><%= course.unregisteredTotal %></td>
							<td class="centeralign">
								<a class="t_course_enroll<%= idx %>"
									href="<%= helper.getCoordCourseEnrollLink(course.id) %>"
									onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_COURSE_ENROLL %>')"
									onmouseout="hideddrivetip()">Enroll</a>
								<a class="t_course_view<%= idx %>"
									href="<%=helper.getCoordCourseDetailsLink(course.id)%>"
									onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_COURSE_DETAILS %>')"
									onmouseout="hideddrivetip()">View</a>
								<a class="t_course_delete<%= idx %>"
									href="<%=helper.getCoordCourseDeleteLink(course.id,false)%>"
									onclick="hideddrivetip(); return toggleDeleteCourseConfirmation('<%= course.id %>');"
									onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_COURSE_DELETE %>')"
									onmouseout="hideddrivetip()">Delete</a>
							</td>
						</tr>
					<%	}
						if(idx==-1){ // Print empty row
					%>
						<tr>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
					<%	} %>
				</table>
				<br />
				<br />
				<br />
				<% if(idx==-1){ %>
					No records found. <br />
					<br />
					<br />
					<br />
				<% } %>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>