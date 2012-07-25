<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.common.datatransfer.CourseData"%>
<%@ page import="teammates.common.datatransfer.EvaluationData"%>
<%@ page import="teammates.ui.controller.CoordHomeHelper"%>
<%	CoordHomeHelper helper = (CoordHomeHelper)request.getAttribute("helper"); %>
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
	<script language="JavaScript" src="/js/common.js"></script>
	
	<script language="JavaScript" src="/js/coordinator.js"></script>


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
				<h1>Coordinator Home</h1>
				<br />
				<div class="result_team">
					<div class="result_addOrJoinCourse">
						<a href="<%= helper.getCoordCourseLink() %>" name="addNewCourse" id="addNewCourse">
							Add New Course </a>
					</div>
				</div>
			</div>
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
			<div id="coordinatorHomeTable">
				<%	int idx = -1;
					int evalIdx = -1;
					for (CourseData course: helper.courses) { idx++;
				%>
				<div class="result_team home_courses_div" id="course<%= idx %>">
					<div class="result_homeTitle">
						<h2>[<%= course.id %>] :
							<%=CoordHomeHelper.escapeForHTML(course.name)%>
						</h2>
					</div>
					<div class="result_homeLinks">
						<a class="t_course_enroll<%=idx%>"
							href="<%=helper.getCoordCourseEnrollLink(course.id)%>"
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_COURSE_ENROLL%>')"
							onmouseout="hideddrivetip()">
							Enroll
						</a>
						<a class="t_course_view<%=idx%>"
							href="<%=helper.getCoordCourseDetailsLink(course.id)%>"
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_COURSE_DETAILS%>')"
							onmouseout="hideddrivetip()">
							View
						</a>
						<a class="t_course_add_eval<%=idx%>"
							href="<%=helper.getCoordEvaluationLink()%>"
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_COURSE_ADD_EVALUATION%>')"
							onmouseout="hideddrivetip()">
							Add Evaluation
						</a>
						<a class="t_course_delete<%=idx%>"
							href="<%=helper.getCoordCourseDeleteLink(course.id,true)%>"
							onclick="hideddrivetip(); return toggleDeleteCourseConfirmation('<%=course.id%>')"
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_COURSE_DELETE%>')"
							onmouseout="hideddrivetip()">
							Delete
						</a>
					</div>
					<div style="clear: both;"></div>
					<br />
					<%
						if (course.evaluations.size() > 0) {
					%>
						<table id="dataform">
							<tr>
								<th class="leftalign">Evaluation Name</th>
								<th class="centeralign">Status</th>
								<th class="centeralign"><span
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_RESPONSE_RATE%>')"
									onmouseout="hideddrivetip()">Response Rate</span></th>
								<th class="centeralign">Action(s)</th>
							</tr>
							<%
								for (EvaluationData eval: course.evaluations){ evalIdx++;
							%>
								<tr class="home_evaluations_row" id="evaluation<%=evalIdx%>">
									<td class="t_eval_name<%=idx%>"><%=CoordHomeHelper.escapeForHTML(eval.name)%></td>
									<td class="t_eval_status<%= idx %> centeralign"><span
										onmouseover="ddrivetip('<%= CoordHomeHelper.getCoordHoverMessageForEval(eval) %>')"
										onmouseout="hideddrivetip()"><%= CoordHomeHelper.getCoordStatusForEval(eval) %></span></td>
									<td class="t_eval_response<%= idx %> centeralign"><%= eval.submittedTotal %>
										/ <%= eval.expectedTotal %></td>
									<td class="centeralign"><%= helper.getCoordEvaluationActions(eval,evalIdx, true) %>
									</td>
								</tr>
							<%	} %>
						</table>
						<br />
					<%
						}
					%>
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