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
	<script language="JavaScript" src="/js/helperNew.js"></script>
	<script language="JavaScript" src="/js/commonNew.js"></script>
	
	<script language="JavaScript" src="/js/studentNew.js"></script>	
</head>

<body>
	<div id="dhtmltooltip"></div>

	<div id="frameTop">
		<jsp:include page="/studentHeader.jsp" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>STUDENT HOME</h1>
				<br />
				<div class="result_addOrJoinCourse">
					<form method="post" action="<%= Common.JSP_STUDENT_JOIN_COURSE %>" name="form_joincourse">
						<table class="headerform">
							<tr>
								<td width="30%" class="attribute">Registration Key:</td>
								<td width="30%">
									<input class="keyvalue" type="text"
											name="<%= Common.PARAM_JOIN_COURSE %>"
											id="<%= Common.PARAM_JOIN_COURSE %>"
											onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_JOIN_COURSE %>')"
											onmouseout="hideddrivetip()" tabindex=1 />
								</td>
								<td width="30%">
									<input id="btnJoinCourse" type="submit" class="button"
											onclick="return this.form.<%= Common.PARAM_JOIN_COURSE %>.value!=''"
											value="Join Course" tabindex=2 />
								</td>
							</tr>
						 </table>
					</form>
				</div>
			</div>
			<jsp:include page="/statusMessage.jsp" />
			<div id="studentHomeTable">
				<%	int idx = 0;
					int evalIdx = 0;
					for (idx = 0; idx < helper.courses.size(); idx++) {
						CourseData course = helper.courses.get(idx);
				%>
				<div class="result_team home_courses_div" id="course<%= idx %>">
					<div class="result_homeTitle">
						<h2>[<%= course.id %>] :
							<%= StudentHomeHelper.escapeHTML(course.name) %>
						</h2>
					</div>
					<div class="result_homeLinks">
						<a class="t_course_profile<%= idx %>"
							href="<%= helper.getStudentCourseProfileLink(course.id) %>"
							onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_STUDENT_COURSE_PROFILE %>')"
							onmouseout="hideddrivetip()">
							Profile
						</a>
						<a class="t_course_view<%= idx %>"
							href="<%= helper.getStudentCourseDetailsLink(course.id) %>"
							onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_STUDENT_COURSE_DETAILS %>')"
							onmouseout="hideddrivetip()">
							View Team
						</a>
					</div>
					<div style="clear: both;"></div>
					<br />
					<%	EvaluationData[] evaluationsArr = StudentHomeHelper.getEvaluationsForCourse(course);
						if (evaluationsArr.length > 0) {
					%>
						<table id="dataform">
							<tr>
								<th class="leftalign">Evaluation Name</th>
								<th class="centeralign">Deadline</th>
								<th class="centeralign">Status</th>
								<th class="centeralign">Action(s)</th>
							</tr>
							<%	for (int i=evaluationsArr.length-1; i>=0; i--, evalIdx++) {
									EvaluationData eval = evaluationsArr[i];
							%>
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
		<jsp:include page="/footer.jsp" />
	</div>
</body>
</html>