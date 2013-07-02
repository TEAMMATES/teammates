<%@ page import="teammates.common.util.Config"%>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.util.FieldValidator"%>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="static teammates.ui.controller.PageData.escapeForHTML"%>
<%@ page import="teammates.ui.controller.InstructorCoursePageData"%>
<%
	InstructorCoursePageData data = (InstructorCoursePageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Instructor</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/instructorCourse.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
	<link rel="stylesheet" href="/stylesheets/instructorCourse-print.css" type="text/css" media="print">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/instructor.js"></script>
	<script type="text/javascript" src="/js/instructorCourse.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Config.JSP_INSTRUCTOR_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Add New Course</h1>
			</div>

			<form method="get" action="<%=Config.PAGE_INSTRUCTOR_COURSE_ADD%>" name="form_addcourse">
				<input type="hidden" id="<%=Config.PARAM_INSTRUCTOR_ID%>" name="<%=Config.PARAM_INSTRUCTOR_ID%>" value="<%=data.account.googleId%>">
				<table id="addform" class="inputTable">
					<tr>
						<td class="label bold" width="20%">Course ID:</td>
						<td><input class="addinput" type="text"
							name="<%=Config.PARAM_COURSE_ID%>" id="<%=Config.PARAM_COURSE_ID%>"
							value="<%=(escapeForHTML(data.courseIdToShow))%>"
							onmouseover="ddrivetip('Enter the identifier of the course, e.g.CS3215-2013Semester1.')"
							onmouseout="hideddrivetip()"
							maxlength=<%=FieldValidator.COURSE_ID_MAX_LENGTH%> tabindex="1"
							placeholder="e.g. CS3215-2013Semester1" /></td>
					</tr>
					<tr>
						<td class="label bold">Course Name:</td>
						<td><input class="addinput" type="text"
							name="<%=Config.PARAM_COURSE_NAME%>" id="<%=Config.PARAM_COURSE_NAME%>"
							value="<%=(escapeForHTML(data.courseNameToShow))%>"
							onmouseover="ddrivetip('Enter the name of the course, e.g. Software Engineering.')"
							onmouseout="hideddrivetip()"
							maxlength=<%=FieldValidator.COURSE_NAME_MAX_LENGTH%> tabindex=2
							placeholder="e.g. Software Engineering" /></td>
					</tr>
					<tr>
						<td colspan=2 class="label bold">Instructors:</td>
					</tr>
					<tr>
						<td colspan=2>
							<span id="instructorformat" class="bold">Format: Google ID | Instructor Name | Instructor Email</span>
							<textarea rows="6" cols="110" class ="textvalue" name="<%=Config.PARAM_COURSE_INSTRUCTOR_LIST%>" 
							id="<%=Config.PARAM_COURSE_INSTRUCTOR_LIST%>"><%=escapeForHTML(data.instructorListToShow)%></textarea>
						</td>
					</tr>
					<tr>
						<td colspan=2 class="centeralign"><input id="btnAddCourse" type="submit" class="button"
							onclick="return verifyCourseData();" value="Add Course" tabindex="3"></td>
					</tr>
				</table>
				
				<input type="hidden" name="<%=Config.PARAM_USER_ID%>" value="<%=data.account.googleId%>">
				
			</form>
			<br>
			<jsp:include page="<%=Config.JSP_STATUS_MESSAGE%>" />
			<br>
			<table class="dataTable">
				<tr>
					<th class="color_white bold"><input class="buttonSortAscending" type="button"
						id="button_sortcourseid"
						onclick="toggleSort(this,1);">
						Course ID</th>
					<th class="color_white bold"><input class="buttonSortNone" type="button"
						id="button_sortcoursename"
						onclick="toggleSort(this,2);">
						Course Name</th>
					<th class="centeralign color_white bold">Teams</th>
					<th class="centeralign color_white bold">Total Students</th>
					<th class="centeralign color_white bold">Total Unregistered</th>
					<th class="centeralign color_white bold no-print">Action(s)</th>
				</tr>
				<%
					int idx = -1;
							for(CourseDetailsBundle courseDetails: data.currentCourses){ 
								idx++;
				%>
					<tr class="courses_row">
						<td id="courseid<%=idx%>"><%=escapeForHTML(courseDetails.course.id)%></td>
						<td id="coursename<%=idx%>"><%=escapeForHTML(courseDetails.course.name)%></td>
						<td class="t_course_teams centeralign"><%=courseDetails.stats.teamsTotal%></td>
						<td class="centeralign"><%=courseDetails.stats.studentsTotal%></td>
						<td class="centeralign"><%=courseDetails.stats.unregisteredTotal%></td>
						<td class="centeralign no-print">
							<a class="color_black t_course_enroll<%=idx%>"
								href="<%=data.getInstructorCourseEnrollLink(courseDetails.course.id)%>"
								onmouseover="ddrivetip('<%=Config.HOVER_MESSAGE_COURSE_ENROLL%>')"
								onmouseout="hideddrivetip()">Enroll</a>
							<a class="color_black t_course_view<%=idx%>"
								href="<%=data.getInstructorCourseDetailsLink(courseDetails.course.id)%>"
								onmouseover="ddrivetip('<%=Config.HOVER_MESSAGE_COURSE_DETAILS%>')"
								onmouseout="hideddrivetip()">View</a>
							<a class="color_black t_course_edit<%=idx%>"
								href="<%=data.getInstructorCourseEditLink(courseDetails.course.id)%>"
								onmouseover="ddrivetip('<%=Config.HOVER_MESSAGE_COURSE_EDIT%>')"
								onmouseout="hideddrivetip()">Edit</a>
							<a class="color_black t_course_delete<%=idx%>"
								href="<%=data.getInstructorCourseDeleteLink(courseDetails.course.id,false)%>"
								onclick="hideddrivetip(); return toggleDeleteCourseConfirmation('<%=courseDetails.course.id%>');"
								onmouseover="ddrivetip('<%=Config.HOVER_MESSAGE_COURSE_DELETE%>')"
								onmouseout="hideddrivetip()">Delete</a>
						</td>
					</tr>
				<%
					}
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
				<%
					}
				%>
			</table>
			<br>
			<br>
			<br>
			<%
				if(idx==-1){
			%>
				No records found. <br>
				<br>
				<br>
				<br>
			<%
				}
			%>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Config.JSP_FOOTER%>" />
	</div>
</body>
</html>