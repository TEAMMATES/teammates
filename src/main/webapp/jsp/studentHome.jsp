<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.TimeHelper" %>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle" %>
<%@ page import="teammates.common.datatransfer.EvaluationDetailsBundle" %>
<%@ page import="teammates.common.datatransfer.FeedbackSessionDetailsBundle"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="teammates.ui.controller.StudentHomePageData"%>
<%
	StudentHomePageData data = (StudentHomePageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>TEAMMATES - Student</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/studentHome.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/studentHome-print.css" type="text/css" media="print">

	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/student.js"></script>
	<script type="text/javascript" src="/js/studentHome.js"></script>
	<jsp:include page="../enableJS.jsp"></jsp:include>	
</head>

<body>
	<div id="dhtmltooltip"></div>

	<div id="frameTop">
		<jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Student Home</h1>
			</div>
		
			<form method="post" action="<%=Const.ActionURIs.STUDENT_COURSE_JOIN%>" name="form_joincourse">
				<table class="inputTable" id="result_addOrJoinCourse">
					<tr>
						<td class="label bold">Registration Key:</td>
						<td>
							<input class="keyvalue" type="text"
									name="<%=Const.ParamsNames.REGKEY%>"
									id="<%=Const.ParamsNames.REGKEY%>"
									onmouseover="ddrivetip('<%=Const.Tooltips.STUDENT_JOIN_COURSE%>')"
									onmouseout="hideddrivetip()" tabindex="1">
						</td>
						<td>
							<input id="button_join_course" type="submit" class="button"
									onclick="return this.form.<%=Const.ParamsNames.REGKEY%>.value!=''"
									value="Join Course" tabindex="2"
									onmouseover="ddrivetip('<%=Const.Tooltips.STUDENT_JOIN_COURSE_BUTTON%>')"
									onmouseout="hideddrivetip()">
						</td>
					</tr>
				 </table>
				<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
			</form>
			
			<br>
			<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
			<br>

			<%
				int courseIdx = -1;
				int sessionIdx = -1;
				for (CourseDetailsBundle courseDetails : data.courses) {
					courseIdx++;
			%>
			<div class="backgroundBlock">
				<div class="result_team home_courses_div" id="course<%=courseIdx%>">
					<div class="result_homeTitle">
						<h2 class="color_white">[<%=courseDetails.course.id%>] :
							<%=PageData.sanitizeForHtml(courseDetails.course.name)%>
						</h2>
					</div>
					<div class="result_homeLinks blockLink rightalign">
						<a class="t_course_view<%=courseIdx%> color_white"
							href="<%=data.getStudentCourseDetailsLink(courseDetails.course.id)%>"
							onmouseover="ddrivetip('<%=Const.Tooltips.STUDENT_COURSE_DETAILS%>')"
							onmouseout="hideddrivetip()">
							View Team
						</a>
					</div>
					<div style="clear: both;"></div>
					<br>
					<%
						if (courseDetails.evaluations.size() > 0 || 
							courseDetails.feedbackSessions.size() > 0) {
					%>
							<table class="dataTable">
								<tr>
									<th class="leftalign bold color_white">Session Name</th>
									<th class="centeralign bold color_white">Deadline</th>
									<th class="centeralign bold color_white">Status</th>
									<th class="centeralign bold color_white">Action(s)</th>
								</tr>
							<%
								for (EvaluationDetailsBundle edd : courseDetails.evaluations) {
									sessionIdx++;
							%>
									<tr class="home_evaluations_row" id="evaluation<%=sessionIdx%>">
										<td class="t_eval_name"><%=PageData.sanitizeForHtml(edd.evaluation.name)%></td>
										<td class="t_eval_deadline centeralign"><%=TimeHelper.formatTime(edd.evaluation.endTime)%></td>
										<td class="t_eval_status centeralign"><span
											onmouseover="ddrivetip(' <%=data.getStudentHoverMessageForEval(data.getStudentStatusForEval(edd.evaluation))%>')"
											onmouseout="hideddrivetip()"><%=data.getStudentStatusForEval(edd.evaluation)%></span></td>
										<td class="centeralign"><%=data.getStudentEvaluationActions(edd.evaluation,sessionIdx)%>
										</td>
									</tr>
							<%
								}
								for (FeedbackSessionDetailsBundle fsd : courseDetails.feedbackSessions) {
									sessionIdx++;
							%>
									<tr class="home_evaluations_row" id="evaluation<%=sessionIdx%>">
										<td class="t_eval_name"><%=PageData.sanitizeForHtml(fsd.feedbackSession.feedbackSessionName)%></td>
										<td class="t_eval_deadline centeralign"><%=TimeHelper.formatTime(fsd.feedbackSession.endTime)%></td>
										<td class="t_eval_status centeralign"><span
											onmouseover="ddrivetip(' <%=data.getStudentHoverMessageForSession(fsd.feedbackSession)%>')"
											onmouseout="hideddrivetip()"><%=data.getStudentStatusForSession(fsd.feedbackSession)%></span></td>
										<td class="centeralign"><%=data.getStudentFeedbackSessionActions(fsd.feedbackSession,sessionIdx)%>
										</td>
									</tr>
							<%
								}
							%>
							</table>
					<%
						} else {
					%>
							<table class="dataTable">
								<tr>
									<th class="centeralign bold color_white">
										Currently, there are no open evaluation/feedback sessions in this course. When a session is open for submission you will be notified.
									</th>
								</tr>
							</table>
					<%
						}
					%>
				</div>
			</div>
			<br>
			<br>
			<br>
			<%
				out.flush();
							}
			%>
		</div>
		
	</div>
	
	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>