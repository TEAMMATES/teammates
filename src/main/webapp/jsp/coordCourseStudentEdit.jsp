<%@ page import="teammates.api.Common" %>
<%@ page import="teammates.datatransfer.CourseData"%>
<%@ page import="teammates.datatransfer.EvaluationData"%>
<%@ page import="teammates.jsp.CoordCourseStudentDetailsHelper"%>
<%	CoordCourseStudentDetailsHelper helper = (CoordCourseStudentDetailsHelper)request.getAttribute("helper"); %>
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
	<script language="JavaScript" src="/js/constants.js"></script>
	<script language="JavaScript" src="/js/commonNew.js"></script>
	
	<script language="JavaScript" src="/js/coordinatorNew.js"></script>

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
				<h1>Edit Student Details</h1>
				<form action="<%= Common.PAGE_COORD_COURSE_STUDENT_EDIT %>" method="post">
					<input type="hidden" name="<%= Common.PARAM_COURSE_ID %>" value="<%= helper.student.course %>" />
					<table class="detailform">
						<tr>
				 			<td class="fieldname">Student Name:</td>
				 			<td>
				 				<input class="fieldvalue" name="<%= Common.PARAM_STUDENT_NAME %>" id="<%= Common.PARAM_STUDENT_NAME %>"
				 						value="<%= helper.student.name %>" />
				 			</td>
				 		</tr>
					 	<tr>
					 		<td class="fieldname">Team Name:</td>
					 		<td>
					 			<input class="fieldvalue" name="<%= Common.PARAM_TEAM_NAME %>" id="<%= Common.PARAM_TEAM_NAME %>"
					 					value="<%= CoordCourseStudentDetailsHelper.escapeHTML(helper.student.team) %>" />
					 		</td>
					 	</tr>
					 	<tr>
					 		<td class="fieldname">E-mail Address:
					 			<input type="hidden" name="<%= Common.PARAM_STUDENT_EMAIL %>" id="<%= Common.PARAM_STUDENT_EMAIL %>"
					 					value="<%= CoordCourseStudentDetailsHelper.escapeHTML(helper.student.email) %>" />
					 		</td>
					 		<td><%= CoordCourseStudentDetailsHelper.escapeHTML(helper.student.email) %></td>
					 	</tr>
					 	<tr>
							<td class="fieldname">Google ID:</td>
							<td id="<%= Common.PARAM_USER_ID %>"><%= (helper.student.id!= null ? CoordCourseStudentDetailsHelper.escapeHTML(helper.student.id) : "") %></td>
						</tr>
						<tr>
							<td class="fieldname">Registration Key:</td>
							<td id="<%= Common.PARAM_REGKEY %>"><%= CoordCourseStudentDetailsHelper.escapeHTML(helper.regKey) %></td>
						</tr>
					 	<tr>
					 		<td class="fieldname">Comments:</td>
					 		<td>
					 			<textarea class="textvalue" rows="6" cols="80" name="<%= Common.PARAM_COMMENTS %>" id="<%= Common.PARAM_COMMENTS %>"><%= CoordCourseStudentDetailsHelper.escapeHTML(helper.student.comments) %></textarea>
					 		</td>
					 	</tr>
					</table>
					<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
					<br /><br />
					<input type="button" class="button" id="button_back" value="Cancel"
							onclick="window.location.href='<%= helper.getCoordCourseDetailsLink(helper.student.course) %>'" />
					<input type="submit" class="button" id="button_save" name="submit" value="Save Changes"
							onclick="return isStudentInputValid(this.form.<%= Common.PARAM_STUDENT_NAME %>.value,this.form.<%= Common.PARAM_TEAM_NAME %>.value)" />
					<br /><br />
					<% if(helper.isMasqueradeMode()){ %>
						<input type="hidden" name="<%= Common.PARAM_USER_ID %>" value="<%= helper.requestedUser %>" />
					<% } %>
				</form>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>