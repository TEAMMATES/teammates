<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.common.datatransfer.CourseDataDetails" %>
<%@ page import="teammates.common.datatransfer.EvaluationData" %>
<%@ page import="teammates.ui.controller.StudentHomeHelper"%>
<%
	StudentHomeHelper helper = (StudentHomeHelper)request.getAttribute("helper");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Student</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/studentHome.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/studentHome-print.css" type="text/css" media="print">

	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/student.js"></script>
	<jsp:include page="../enableJS.jsp"></jsp:include>	
</head>

<body>
	<div id="dhtmltooltip"></div>

	<div id="frameTop">
		<jsp:include page="<%=Common.JSP_STUDENT_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Student Home</h1>
			</div>
		
			<form method="post" action="<%=Common.PAGE_STUDENT_JOIN_COURSE%>" name="form_joincourse">
				<table class="inputTable" id="result_addOrJoinCourse">
					<tr>
						<td class="label bold">Registration Key:</td>
						<td>
							<input class="keyvalue" type="text"
									name="<%=Common.PARAM_REGKEY%>"
									id="<%=Common.PARAM_REGKEY%>"
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_JOIN_COURSE%>')"
									onmouseout="hideddrivetip()" tabindex="1">
						</td>
						<td>
							<input id="button_join_course" type="submit" class="button"
									onclick="return this.form.<%=Common.PARAM_REGKEY%>.value!=''"
									value="Join Course" tabindex="2">
						</td>
					</tr>
				 </table>
				<%
					if(helper.isMasqueradeMode()){
				%>
					<input type="hidden" name="<%=Common.PARAM_USER_ID%>" value="<%=helper.requestedUser%>">
				<%
					}
				%>
			</form>
			
			<br>
			<jsp:include page="<%=Common.JSP_STATUS_MESSAGE%>" />
			<br>
			
			<%
							int idx = -1;
								int evalIdx = -1;
								for (CourseDataDetails courseDetails: helper.courses) { idx++;
						%>
			<div class="backgroundBlock">
				<div class="result_team home_courses_div" id="course<%= idx %>">
					<div class="result_homeTitle">
						<h2 class="color_white">[<%= courseDetails.course.id %>] :
							<%=StudentHomeHelper.escapeForHTML(courseDetails.course.name)%>
						</h2>
					</div>
					<div class="result_homeLinks blockLink rightalign">
						<a class="t_course_view<%=idx%> color_white"
							href="<%=helper.getStudentCourseDetailsLink(courseDetails.course.id)%>"
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_STUDENT_COURSE_DETAILS%>')"
							onmouseout="hideddrivetip()">
							View Team
						</a>
					</div>
					<div style="clear: both;"></div>
					<br>
					<%
						if (courseDetails.evaluations.size() > 0) {
					%>
						<table class="dataTable">
							<tr>
								<th class="leftalign bold color_white">Evaluation Name</th>
								<th class="centeralign bold color_white">Deadline</th>
								<th class="centeralign bold color_white">Status</th>
								<th class="centeralign bold color_white">Action(s)</th>
							</tr>
							<%
								for (EvaluationData eval: courseDetails.evaluations) { evalIdx++;
							%>
								<tr class="home_evaluations_row" id="evaluation<%=evalIdx%>">
									<td class="t_eval_name"><%=StudentHomeHelper.escapeForHTML(eval.name)%></td>
									<td class="t_eval_deadline centeralign"><%= Common.formatTime(eval.endTime) %></td>
									<td class="t_eval_status centeralign"><span
										onmouseover="ddrivetip(' <%= helper.getStudentHoverMessageForEval(eval)%>')"
										onmouseout="hideddrivetip()"><%= helper.getStudentStatusForEval(eval)%></span></td>
									<td class="centeralign"><%= helper.getStudentEvaluationActions(eval,evalIdx) %>
									</td>
								</tr>
							<%	} %>
						</table>
						
					<%	} %>
				</div>
			</div>
			<br>
			<br>
			<br>
			<%		out.flush();
				}
			%>
		</div>
		
	</div>
	
	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>