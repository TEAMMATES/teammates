<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.util.FieldValidator"%>
<%@ page import="teammates.common.datatransfer.InstructorAttributes"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="teammates.ui.controller.InstructorCourseEditPageData"%>
<%
	InstructorCourseEditPageData data = (InstructorCourseEditPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>TEAMMATES - Instructor</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/instructorCourseEdit.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/instructorCourseEdit-print.css" type="text/css" media="print">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/instructor.js"></script>
	<script type="text/javascript" src="/js/instructorCourseEdit.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body onload="readyCourseEditPage(); initializetooltip();">
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Edit Course Details</h1>
			</div>
				
			<form action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_SAVE%>" method="post" id="formEditcourse">
				<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.course.id%>">
				<input type="hidden" name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" value="<%=data.account.googleId%>">
				
				<table id="courseDetailTable" class="inputTable">
					<tr>
						<td class="label bold">Course ID:</td>
						<td><input class="addinput" type="text"
							name="<%=Const.ParamsNames.COURSE_ID%>" id="<%=Const.ParamsNames.COURSE_ID%>"
							value="<%=(data.course.id==null ? "" : sanitizeForHtml(data.course.id))%>"
							onmouseover="ddrivetip('Identifier of the course, e.g.CS3215-Sem1.')"
							onmouseout="hideddrivetip()"
							maxlength=<%=FieldValidator.COURSE_ID_MAX_LENGTH%> tabindex="1" disabled="disabled"></td>
					</tr>
					<tr>
						<td class="label bold">Course Name:</td>
						<td><input class="addinput" type="text"
							name="<%=Const.ParamsNames.COURSE_NAME%>" id="<%=Const.ParamsNames.COURSE_NAME%>"
							value="<%=(data.course.name==null ? "" : sanitizeForHtml(data.course.name))%>"
							onmouseover="ddrivetip('Enter the name of the course, e.g. Software Engineering.')"
							onmouseout="hideddrivetip()"
							maxlength=<%=FieldValidator.COURSE_NAME_MAX_LENGTH%> tabindex=2 disabled="disabled"/></td>
					</tr>
					<tr>
						<td colspan=2 class="rightalign">
							<a href="<%=data.getInstructorCourseDeleteLink(data.course.id, false)%>" class="color_red" id="courseDeleteLink"
							onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_DELETE%>')" onmouseout="hideddrivetip()"
							onclick="hideddrivetip(); return toggleDeleteCourseConfirmation('<%=data.course.id%>');">Delete Course</a>
						</td>
					</tr>
					<tr>
						<td colspan=2 class="centeralign">
							<input type="submit" class="button" id="btnSaveCourse" name="btnSaveCourse"
							style="display:none;" value="Save Changes" onclick="return verifyCourseData();">
						</td>
					</tr>
				</table>

				<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
				<br>
				
				<br>
				<br>
				<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
			</form>
			
			<%
				for (int i = 0; i < data.instructorList.size(); i++) {
					InstructorAttributes instructor = data.instructorList.get(i);
					int index = i+1;
			%>
			<form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_EDIT_SAVE%>"
			id="formEditInstructor<%=index%>>" name="formEditInstructors" class="formInstructor" >
				<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=instructor.courseId%>">
				<input type="hidden" name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" value="<%=instructor.googleId%>">
				<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
				
				<table id="instructorTable<%=index%>" class="inputTable instructorTable">
					<tr>
						<td class="label bold">Instructor <%=index%>:</td>
						<td class="rightalign">
							<a href="#" class="color_blue pad_right" id="instrEditLink<%=index%>" 
							onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_INSTRUCTOR_EDIT%>')" onmouseout="hideddrivetip()" 
							onclick="enableEditInstructor(<%=index%>, <%=data.instructorList.size()%>)">Edit</a>
							<a href="<%=data.getInstructorCourseInstructorDeleteLink(instructor.courseId, instructor.googleId)%>" class="color_red" id="instrDeleteLink<%=index%>"
							onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_INSTRUCTOR_DELETE%>')" onmouseout="hideddrivetip()"
							onclick="hideddrivetip(); return toggleDeleteInstructorConfirmation('<%=instructor.courseId%>','<%=instructor.googleId%>', '<%=data.account.googleId%>');">Delete</a>
						</td>
					</tr>
					<tr>
						<td class="label bold">Google ID:</td>
						<td><input class="addinput immutable" type="text"
							name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" id="<%=Const.ParamsNames.INSTRUCTOR_ID+index%>"
							value="<%=instructor.googleId%>"
							onmouseover="ddrivetip('Enter the google id of the instructor.')"
							onmouseout="hideddrivetip()"
							maxlength=<%=FieldValidator.GOOGLE_ID_MAX_LENGTH%> tabindex=3
							disabled="disabled"></td>
					</tr>
					<tr>
						<td class="label bold">Name:</td>
						<td><input class="addinput" type="text"
							name="<%=Const.ParamsNames.INSTRUCTOR_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_NAME+index%>"
							value="<%=instructor.name%>"
							onmouseover="ddrivetip('Enter the name of the instructor.')"
							onmouseout="hideddrivetip()"
							maxlength=<%=FieldValidator.PERSON_NAME_MAX_LENGTH%> tabindex=4
							disabled="disabled"></td>
					</tr>
					<tr>
						<td class="label bold">Email:</td>
						<td><input class="addinput" type="text"
							name="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>" id="<%=Const.ParamsNames.INSTRUCTOR_EMAIL+index%>"
							value="<%=instructor.email%>"
							onmouseover="ddrivetip('Enter the name of the instructor.')"
							onmouseout="hideddrivetip()"
							maxlength=<%=FieldValidator.EMAIL_MAX_LENGTH%> tabindex=5
							disabled="disabled"></td>
					</tr>
					<tr>
						<td colspan=2 class="centeralign"><input id="btnSaveInstructor<%=index%>" type="submit" class="button"
							style="display:none;" value="Save changes" tabindex="6"></td>
					</tr>
				</table>
			</form>
			
			<br>
			<br>
			<%
				}
			%>
			
			<div class="centeralign">
				<input id="btnShowNewInstructorForm" class="button centeralign" value="Add New Instructor" 
					onclick="showNewInstructorForm()">
			</div>
			
			<form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_ADD%>"
			id="formAddInstructor" name="formAddInstructor" class="formInstructor" >
				<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.course.id%>">
				<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
				
				<table id="instructorAddTable" class="inputTable instructorTable">
					<tr>
						<td colspan=2 class="label bold">Instructors <%=data.instructorList.size()+1%>:</td>
					</tr>
					<tr>
						<td class="label bold">Google ID:</td>
						<td><input class="addinput" type="text"
							name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" id="<%=Const.ParamsNames.INSTRUCTOR_ID%>"
							onmouseover="ddrivetip('Enter the google id of the instructor.')"
							onmouseout="hideddrivetip()"
							maxlength=<%=FieldValidator.GOOGLE_ID_MAX_LENGTH%> tabindex=7/></td>
					</tr>
					<tr>
						<td class="label bold">Name:</td>
						<td><input class="addinput" type="text"
							name="<%=Const.ParamsNames.INSTRUCTOR_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_NAME%>"
							onmouseover="ddrivetip('Enter the name of the instructor.')"
							onmouseout="hideddrivetip()"
							maxlength=<%=FieldValidator.PERSON_NAME_MAX_LENGTH%> tabindex=8/></td>
					</tr>
					<tr>
						<td class="label bold">Email:</td>
						<td><input class="addinput" type="text"
							name="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>" id="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>"
							onmouseover="ddrivetip('Enter the name of the instructor.')"
							onmouseout="hideddrivetip()"
							maxlength=<%=FieldValidator.EMAIL_MAX_LENGTH%> tabindex=9/></td>
					</tr>
					<tr>
						<td colspan=2 class="centeralign"><input id="btnAddInstructor" type="submit" class="button"
							value="Add Instructor" tabindex="10"></td>
					</tr>
				</table>
			</form>
			<br><br>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>