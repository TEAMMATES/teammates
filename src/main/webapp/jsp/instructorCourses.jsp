<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.util.FieldValidator"%>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="teammates.ui.controller.InstructorCoursesPageData"%>
<%
	InstructorCoursesPageData data = (InstructorCoursesPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>TEAMMATES - Instructor</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/instructorCourses.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
	<link rel="stylesheet" href="/stylesheets/instructorCourses-print.css" type="text/css" media="print">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/instructor.js"></script>
	<script type="text/javascript" src="/js/instructorCourses.js"></script>
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
				<h1>Add New Course</h1>
			</div>
				<form method="get" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_ADD%>" name="form_addcourse">
					<input type="hidden" id="<%=Const.ParamsNames.INSTRUCTOR_ID%>" name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" value="<%=data.account.googleId%>">
					<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
					
					<table id="courseDetailTable" class="inputTable">
						<tr>
							<td class="label bold" width="20%">Course ID:</td>
							<td><input class="addinput" type="text"
								name="<%=Const.ParamsNames.COURSE_ID%>" id="<%=Const.ParamsNames.COURSE_ID%>"
								value="<%=(sanitizeForHtml(data.courseIdToShow))%>"
								onmouseover="ddrivetip('Enter the identifier of the course, e.g.CS3215-2013Semester1.')"
								onmouseout="hideddrivetip()"
								maxlength=<%=FieldValidator.COURSE_ID_MAX_LENGTH%> tabindex="1"
								placeholder="e.g. CS3215-2013Semester1" /></td>
						</tr>
						<tr>
							<td class="label bold">Course Name:</td>
							<td><input class="addinput" type="text"
								name="<%=Const.ParamsNames.COURSE_NAME%>" id="<%=Const.ParamsNames.COURSE_NAME%>"
								value="<%=(sanitizeForHtml(data.courseNameToShow))%>"
								onmouseover="ddrivetip('Enter the name of the course, e.g. Software Engineering.')"
								onmouseout="hideddrivetip()"
								maxlength=<%=FieldValidator.COURSE_NAME_MAX_LENGTH%> tabindex=2
								placeholder="e.g. Software Engineering" /></td>
						</tr>
						<tr>
							<td colspan=2 class="centeralign"><input id="btnAddCourse" type="submit" class="button"
									onclick="return verifyCourseData();" value="Add Course" tabindex="3"></td>
						</tr>
					</table>
					<br>
				</form>

			<br>
			<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
			<br>
			
			<h2 class="centeralign">Active courses</h2>
			
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
					for(CourseDetailsBundle courseDetails: data.allCourses){ 
						if (!courseDetails.course.isArchived) {
							idx++;
				%>
					<tr class="courses_row">
						<td id="courseid<%=idx%>"><%=sanitizeForHtml(courseDetails.course.id)%></td>
						<td id="coursename<%=idx%>"><%=sanitizeForHtml(courseDetails.course.name)%></td>
						<td class="t_course_teams centeralign"><%=courseDetails.stats.teamsTotal%></td>
						<td class="centeralign"><%=courseDetails.stats.studentsTotal%></td>
						<td class="centeralign"><%=courseDetails.stats.unregisteredTotal%></td>
						<td class="centeralign no-print">
							<a class="color_black t_course_enroll<%=idx%>"
								href="<%=data.getInstructorCourseEnrollLink(courseDetails.course.id)%>"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_ENROLL%>')"
								onmouseout="hideddrivetip()">Enroll</a>
							<a class="color_black t_course_view<%=idx%>"
								href="<%=data.getInstructorCourseDetailsLink(courseDetails.course.id)%>"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_DETAILS%>')"
								onmouseout="hideddrivetip()">View</a>
							<a class="color_black t_course_edit<%=idx%>"
								href="<%=data.getInstructorCourseEditLink(courseDetails.course.id)%>"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_EDIT%>')"
								onmouseout="hideddrivetip()">Edit</a>
							<a class="color_black t_course_delete<%=idx%>"
								href="<%=data.getInstructorCourseDeleteLink(courseDetails.course.id,false)%>"
								onclick="hideddrivetip(); return toggleDeleteCourseConfirmation('<%=courseDetails.course.id%>');"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_DELETE%>')"
								onmouseout="hideddrivetip()">Delete</a>
							<a class="color_black t_course_archive<%=idx%>"
								href="<%=data.getInstructorCourseArchiveLink(courseDetails.course.id, true, false)%>"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_ARCHIVE%>')"
								onmouseout="hideddrivetip()">Archive</a>
						</td>
					</tr>
				<%
						}
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
			<%
				if(idx==-1){
			%>
				No records found. <br>
				<br>
			<%
				}
			%>
			<br>
			<br>
			
			<%
				if (!data.archivedCourses.isEmpty()) {
			%>
			
			<h2 class="centeralign">Archived courses</h2>
			
			<table class="dataTable" style="width:600px">
				<tr>
					<th class="color_white bold"><input class="buttonSortAscending" type="button"
						id="button_sortcourseid"
						onclick="toggleSort(this,1);">
						Course ID</th>
					<th class="color_white bold"><input class="buttonSortNone" type="button"
						id="button_sortcoursename"
						onclick="toggleSort(this,2);">
						Course Name</th>
					<th class="centeralign color_white bold no-print">Action(s)</th>
				</tr>
				<%
					for (CourseAttributes course: data.archivedCourses) { 
						idx++;
				%>
					<tr class="courses_row">
						<td id="courseid<%=idx%>"><%=sanitizeForHtml(course.id)%></td>
						<td id="coursename<%=idx%>"><%=sanitizeForHtml(course.name)%></td>
						<td class="centeralign no-print">
							<a class="color_black t_course_delete<%=idx%>"
								href="<%=data.getInstructorCourseDeleteLink(course.id,false)%>"
								onclick="hideddrivetip(); return toggleDeleteCourseConfirmation('<%=course.id%>');"
								onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_DELETE%>')"
								onmouseout="hideddrivetip()">Delete</a>
							<a class="color_black t_course_unarchive<%=idx%>"
								href="<%=data.getInstructorCourseArchiveLink(course.id, false, false)%>">Unarchive</a>
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
					</tr>
				<%
					}
				%>
			</table>
			<br>
			<br>
			<br>
			<br>
			<%
				}
			%>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>