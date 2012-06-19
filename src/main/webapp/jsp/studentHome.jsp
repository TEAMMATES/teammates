<%@ page import="teammates.api.Common" %>
<%@ page import="teammates.datatransfer.CourseData" %>
<%@ page import="teammates.datatransfer.EvaluationData" %>
<%@ page import="teammates.jsp.StudentHomeHelper"%>
<% StudentHomeHelper helper = (StudentHomeHelper)request.getAttribute("helper"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Student</title>
	<link rel=stylesheet href="/stylesheets/main.css" type="text/css" />
	<link rel=stylesheet href="/stylesheets/evaluation.css" type="text/css" />

	<script language="JavaScript" src="/js/jquery-1.6.2.min.js"></script>
	<script language="JavaScript" src="/js/tooltip.js"></script>
	
	<script language="JavaScript" src="/js/studentNew.js"></script>	
</head>

<body>
	<div id="dhtmltooltip"></div>

	<div id="frameTop">
		<jsp:include page="<%= Common.JSP_STUDENT_HEADER %>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Student Home</h1>
				<br />
				<div class="result_addOrJoinCourse">
					<form method="post" action="<%= Common.PAGE_STUDENT_JOIN_COURSE %>" name="form_joincourse">
						<table class="headerform">
							<tr>
								<td width="30%" class="attribute">Registration Key:</td>
								<td width="30%">
									<input class="keyvalue" type="text"
											name="<%= Common.PARAM_REGKEY %>"
											id="<%= Common.PARAM_REGKEY %>"
											onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_JOIN_COURSE %>')"
											onmouseout="hideddrivetip()" tabindex=1 />
								</td>
								<td width="30%">
									<input id="button_join_course" type="submit" class="button"
											onclick="return this.form.<%= Common.PARAM_REGKEY %>.value!=''"
											value="Join Course" tabindex=2 />
								</td>
							</tr>
						 </table>
						<% if(helper.isMasqueradeMode()){ %>
							<input type="hidden" name="<%= Common.PARAM_USER_ID %>" value="<%= helper.requestedUser %>" />
						<% } %>
					</form>
				</div>
			</div>
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
			<div id="studentHomeTable">
				<%	int idx = -1;
					int evalIdx = -1;
					for (CourseData course: helper.courses) { idx++;
				%>
				<div class="result_team home_courses_div" id="course<%= idx %>">
					<div class="result_homeTitle">
						<h2>[<%= course.id %>] :
							<%= StudentHomeHelper.escapeHTML(course.name) %>
						</h2>
					</div>
					<div class="result_homeLinks">
						<!-- <a class="t_course_profile<%= idx %>"
							href="<%= helper.getStudentCourseProfileLink(course.id) %>"
							onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_STUDENT_COURSE_PROFILE %>')"
							onmouseout="hideddrivetip()">
							Profile
						</a> -->
						<a class="t_course_view<%= idx %>"
							href="<%= helper.getStudentCourseDetailsLink(course.id) %>"
							onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_STUDENT_COURSE_DETAILS %>')"
							onmouseout="hideddrivetip()">
							View Team
						</a>
					</div>
					<div style="clear: both;"></div>
					<br />
					<%	if (course.evaluations.size() > 0) { %>
						<table id="dataform">
							<tr>
								<th class="leftalign">Evaluation Name</th>
								<th class="centeralign">Deadline</th>
								<th class="centeralign">Status</th>
								<th class="centeralign">Action(s)</th>
							</tr>
							<%	for (EvaluationData eval: course.evaluations) { evalIdx++; %>
								<tr class="home_evaluations_row" id="evaluation<%= evalIdx %>">
									<td class="t_eval_name"><%= StudentHomeHelper.escapeHTML(eval.name) %></td>
									<td class="t_eval_deadline centeralign"><%= Common.formatTime(eval.endTime) %></td>
									<td class="t_eval_status centeralign"><span
										onmouseover="ddrivetip(' <%= helper.getStudentHoverMessageForEval(eval)%>')"
										onmouseout="hideddrivetip()"><%= helper.getStudentStatusForEval(eval)%></span></td>
									<td class="centeralign"><%= helper.getStudentEvaluationActions(eval,evalIdx) %>
									</td>
								</tr>
							<%	} %>
						</table>
						<br />
					<%	} %>
				</div>
				<br /> <br /> <br />
				<%		out.flush();
					}
				%>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>