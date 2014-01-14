<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.CourseSummaryBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="teammates.ui.controller.InstructorHomePageData"%>
<%
	InstructorHomePageData data = (InstructorHomePageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>TEAMMATES - Instructor</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen"/>
	<link rel="stylesheet" href="/stylesheets/instructorHome.css" type="text/css" media="screen"/>
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print"/>
    <link rel="stylesheet" href="/stylesheets/instructorHome-print.css" type="text/css" media="print"/>
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/instructor.js"></script>
	<script type="text/javascript" src="/js/instructorHome.js"></script>
	<script type="text/javascript" src="/js/ajaxResponseRate.js"></script>
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
				<h1>Instructor Home</h1>
			</div>
			
			<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
			
			<form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE%>" name="search_form">
				<table class="inputTable" id="searchTable">
					<tr>
						<td><input type="text" id="searchbox" name=<%=Const.ParamsNames.SEARCH_KEY %>
							onmouseover="ddrivetip('<%=Const.Tooltips.SEARCH_STUDENT%>')"
							onmouseout="hideddrivetip()" tabindex="1"></td>
						<td><input id="button_search" type="submit" class="button"
							value="Search" tabindex="2"></td>
					</tr>
				</table>
				<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
			</form>
			<br>
			<br>
			
			<div class="backgroundBlock">
				<div class="blockLink rightalign">
					<a href="<%=data.getInstructorCourseLink()%>" name="addNewCourse" id="addNewCourse" class="color_white bold">
						Add New Course </a>
				</div>
			</div>

			<%
				int courseIdx = -1;
				int sessionIdx = -1;
				for (CourseSummaryBundle courseDetails : data.courses) {
					courseIdx++;
			%>
			<br>
			<br>
			<br>
			<div class="backgroundBlock home_courses_div" id="course<%=courseIdx%>">
				<div class="result_homeTitle">
					<h2 class="color_white">
						[<%=courseDetails.course.id%>] :
						<%=PageData.sanitizeForHtml(courseDetails.course.name)%>
					</h2>
				</div>
				<div class="result_homeLinks blockLink rightalign">
					<a class="t_course_enroll<%=courseIdx%> color_white bold"
						href="<%=data.getInstructorCourseEnrollLink(courseDetails.course.id)%>"
						onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_ENROLL%>')"
						onmouseout="hideddrivetip()"> Enroll</a> <a
						class="t_course_view<%=courseIdx%> color_white bold"
						href="<%=data.getInstructorCourseDetailsLink(courseDetails.course.id)%>"
						onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_DETAILS%>')"
						onmouseout="hideddrivetip()"> View</a> <a
						class="t_course_edit<%=courseIdx%> color_white bold"
						href="<%=data.getInstructorCourseEditLink(courseDetails.course.id)%>"
						onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_EDIT%>')"
						onmouseout="hideddrivetip()"> Edit</a> <a
						class="t_course_add_eval<%=courseIdx%> color_white bold"
						href="<%=data.getInstructorEvaluationLinkForCourse(courseDetails.course.id)%>"
						onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_ADD_EVALUATION%>')"
						onmouseout="hideddrivetip()"> Add Session</a> <a
						class="t_course_delete<%=courseIdx%> color_white bold"
						href="<%=data.getInstructorCourseDeleteLink(courseDetails.course.id,true)%>"
						onclick="hideddrivetip(); return toggleDeleteCourseConfirmation('<%=courseDetails.course.id%>')"
						onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_DELETE%>')"
						onmouseout="hideddrivetip()"> Delete</a>
				</div>
				<div style="clear: both;"></div>
				<br>
				<%
					if (courseDetails.evaluations.size() > 0||
							courseDetails.feedbackSessions.size() > 0) {
				%>
				<table class="dataTable">
					<tr>
						<th class="leftalign color_white bold">Session Name</th>
						<th class="centeralign color_white bold">Status</th>
						<th class="centeralign color_white bold"><span
							onmouseover="ddrivetip('<%=Const.Tooltips.EVALUATION_RESPONSE_RATE%>')"
							onmouseout="hideddrivetip()">Response Rate</span></th>
						<th class="centeralign color_white bold no-print">Action(s)</th>
					</tr>
					<%
							for (EvaluationAttributes edd: courseDetails.evaluations){
								sessionIdx++;
					%>
					<tr class="home_sessions_row" id="session<%=sessionIdx%>">
						<td class="t_session_name<%=courseIdx%>"><%=PageData.sanitizeForHtml(edd.name)%></td>
						<td class="t_session_status<%=courseIdx%> centeralign"><span
							onmouseover="ddrivetip('<%=PageData.getInstructorHoverMessageForEval(edd)%>')"
							onmouseout="hideddrivetip()"><%=PageData.getInstructorStatusForEval(edd)%></span></td>
						<td class="t_session_response<%=courseIdx%> centeralign<% if(!TimeHelper.isOlderThanAYear(edd.endTime)) { out.print(" recent");} %>">
							<a oncontextmenu="return false;" href="<%=data.getEvaluationStatsLink(edd.courseId, edd.name)%>">Show</a>
						</td>
						<td class="centeralign no-print"><%=data.getInstructorEvaluationActions(edd,sessionIdx, true)%>
						</td>
					</tr>
					<%
						}
						for(FeedbackSessionAttributes fdb: courseDetails.feedbackSessions) {
									sessionIdx++;
					%>
					<tr class="home_sessions_row" id="session<%=sessionIdx%>">
						<td class="t_session_name"><%=PageData
								.sanitizeForHtml(fdb.feedbackSessionName)%></td>
						<td class="t_session_status centeralign"><span
							onmouseover="ddrivetip(' <%=PageData
								.getInstructorHoverMessageForFeedbackSession(fdb)%>')"
							onmouseout="hideddrivetip()"><%=PageData
								.getInstructorStatusForFeedbackSession(fdb)%></span></td>
						<td class="t_session_response centeralign<% if(!TimeHelper.isOlderThanAYear(fdb.createdTime)) { out.print(" recent");} %>">
							<a oncontextmenu="return false;" href="<%=data.getFeedbackSessionStatsLink(fdb.courseId, fdb.feedbackSessionName)%>">Show</a>
						</td>
						<td class="centeralign no-print"><%=data.getInstructorFeedbackSessionActions(
								fdb, sessionIdx, false)%></td>
					</tr>
					<%
						}
					%>
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
		<br>
		<br>
		<br>
	</div>

	
	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>