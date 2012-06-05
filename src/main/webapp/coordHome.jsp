<%@ page import="teammates.api.Common" %>
<%@ page import="teammates.datatransfer.CourseData"%>
<%@ page import="teammates.datatransfer.EvaluationData"%>
<%@ page import="teammates.jsp.CoordHomeHelper"%>

<%
	// See if user is logged in, if not we redirect them to the login page
	if (!CoordHomeHelper.isUserLoggedIn()) {
		response.sendRedirect(CoordHomeHelper.getLoginUrl(request));
		return;
	}

	CoordHomeHelper helper = new CoordHomeHelper(request);
%>
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
	
	<script language="JavaScript" src="/js/coordCoursePageNew.js"></script>
	<script language="JavaScript" src="/js/coordinatorNew.js"></script>

</head>

<body>
	<div id="dhtmltooltip"></div>
	<%
		// Check if user is allowed to view this page
			if (!helper.user.isCoord() && !helper.user.isAdmin()) {
	%>
		<p>
			You are not authorized to view this page. <br /> <br />
			<a href="logout.jsp">Logout and return to main page.</a>
		</p>
	<%	} else { // Is coordinator %>
		<div id="frameTop">
			<jsp:include page="coordHeader.jsp" />
		</div>
	
		<div id="frameBody">
			<div id="frameBodyWrapper">
				<div id="topOfPage"></div>
				<div id="headerOperation">
					<h1>COORDINATOR HOME</h1>
					<br />
					<div class='result_team'>
						<div class='result_addOrJoinCourse'>
							<a href="coordCourse.jsp" name='addNewCourse' id='addNewCourse'>
								Add New Course </a>
						</div>
					</div>
				</div>
				<br />
				<%	if (helper.statusMessage != null) { %>
					<div id="statusMessage"
						style="display:block;<%if (helper.error) out.println("background:#FF9999");%>">
						<% out.println(helper.statusMessage); %>
					</div>
				<%	} else { %>
				<div id="statusMessage" style="display: none"></div>
				<%	} %>
				<div id="coordinatorStudentTable">
					<%	CourseData[] summary = helper.summary;
						int idx = 0;
						int evalIdx = 0;
						for (idx = 0; idx < summary.length; idx++) {
							// This will print the latest one first
							CourseData course = summary[summary.length-1-idx];
					%>
					<div class='result_team home_courses_div' id='course<%= idx %>'>
						<div class='result_homeTitle'>
							<h2>[<%= course.id %>] :
								<%= course.name %>
							</h2>
						</div>
						<div class='result_homeLinks'>
							<a class='t_course_enroll<%= idx %>'
								href='<%= helper.getCourseEnrollLink(course.id) %>'
								onmouseover='ddrivetip("<%= Common.HOVER_MESSAGE_ENROLL %>")'
								onmouseout='hideddrivetip()'>
								Enroll
							</a>
							<a class='t_course_view<%= idx %>'
								href='<%= helper.getCourseViewLink(course.id) %>'
								onmouseover='ddrivetip("<%= Common.HOVER_MESSAGE_VIEW_COURSE %>")'
								onmouseout='hideddrivetip()'>
								View
							</a>
							<a class='t_course_add_eval<%= idx %>' href='coordEval.jsp'
								onmouseover='ddrivetip("<%= Common.HOVER_MESSAGE_ADD_EVALUATION %>")'
								onmouseout='hideddrivetip()'>
								Add Evaluation
							</a>
							<a class='t_course_delete<%= idx %>'
								href='<%= helper.getCourseDeleteLink(course.id,"coordHome.jsp") %>'
								onclick='hideddrivetip(); return toggleDeleteCourseConfirmation("<%= course.id %>")'
								onmouseover='ddrivetip("<%= Common.HOVER_MESSAGE_DELETE_COURSE %>")'
								onmouseout='hideddrivetip()'>
								Delete
							</a>
						</div>
						<div style='clear: both;'></div>
						<br />
						<%	EvaluationData[] evaluationsArr = CoordHomeHelper.getEvaluationsForCourse(course);
							if (evaluationsArr.length > 0) {
						%>
							<table id='dataform'>
								<tr>
									<th class='leftalign'>EVALUATION NAME</th>
									<th class='centeralign'>STATUS</th>
									<th class='centeralign'><span
										onmouseover='ddrivetip("Number of students submitted / Class size")'
										onmouseout='hideddrivetip()'>RESPONSE RATE</span></th>
									<th class='centeralign'>ACTION(S)</th>
								</tr>
								<%	for (int i=evaluationsArr.length-1; i>=0; i--) {
										EvaluationData eval = evaluationsArr[i];
								%>
									<tr class='home_evaluations_row' id='evaluation<%= evalIdx %>'>
										<td class='t_eval_name'><%= eval.name %></td>
										<td class='t_eval_status centeralign'><span
											onmouseover='ddrivetip(" <%= CoordHomeHelper.getHoverMessageForEval(eval) %>")'
											onmouseout='hideddrivetip()'><%= CoordHomeHelper.getStatusForEval(eval) %></span></td>
										<td class='t_eval_response centeralign'><%= eval.submittedTotal %>
											/ <%= eval.expectedTotal %></td>
										<td class='centeralign'><%= CoordHomeHelper.getEvaluationActions(eval,evalIdx, true) %>
										</td>
									</tr>
								<%		evalIdx++;
									}
								%>
							</table>
							<br />
						<%		out.flush();
							}
						%>
					</div>
					<br /> <br /> <br />
					<%	} %>
				</div>
			</div>
		</div>
	
		<div id="frameBottom">
			<jsp:include page="footer.jsp" />
		</div>
	<%
		} // Endif (isCoordinator)
	%>
</body>
</html>