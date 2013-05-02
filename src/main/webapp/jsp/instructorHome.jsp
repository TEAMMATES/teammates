<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationDetailsBundle"%>
<%@ page import="teammates.ui.controller.InstructorHomeHelper"%>
<%
	InstructorHomeHelper helper = (InstructorHomeHelper)request.getAttribute("helper");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Instructor</title>
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
    <jsp:include page="../enableJS.jsp"></jsp:include>

</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Common.JSP_INSTRUCTOR_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Instructor Home</h1>
			</div>
			
			<jsp:include page="<%=Common.JSP_STATUS_MESSAGE%>" />
			
			<div class="backgroundBlock">
				<div class="blockLink rightalign">
					<a href="<%=helper.getInstructorCourseLink()%>" name="addNewCourse" id="addNewCourse" class="color_white bold">
						Add New Course </a>
				</div>
			</div>
			
			<%
							int idx = -1;
																				int evalIdx = -1;
																				for (CourseDetailsBundle courseDetails: helper.courses) { idx++;
						%>
			<br>
			<br>
			<br>
			<div class="backgroundBlock home_courses_div" id="course<%=idx%>">
				<div class="result_homeTitle">
					<h2 class="color_white">[<%=courseDetails.course.id%>] :
						<%=InstructorHomeHelper.escapeForHTML(courseDetails.course.name)%>
					</h2>
				</div>
				<div class="result_homeLinks blockLink rightalign">
					<a class="t_course_enroll<%=idx%> color_white bold"
						href="<%=helper.getInstructorCourseEnrollLink(courseDetails.course.id)%>"
						onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_COURSE_ENROLL%>')"
						onmouseout="hideddrivetip()">
						Enroll</a>
					<a class="t_course_view<%=idx%> color_white bold"
						href="<%=helper.getInstructorCourseDetailsLink(courseDetails.course.id)%>"
						onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_COURSE_DETAILS%>')"
						onmouseout="hideddrivetip()">
						View</a>
					<a class="t_course_edit<%=idx%> color_white bold"
						href="<%=helper.getInstructorCourseEditLink(courseDetails.course.id)%>"
						onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_COURSE_EDIT%>')"
						onmouseout="hideddrivetip()">
						Edit</a>
					<a class="t_course_add_eval<%=idx%> color_white bold"
						href="<%=helper.getInstructorEvaluationLink(courseDetails.course.id)%>"
						onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_COURSE_ADD_EVALUATION%>')"
						onmouseout="hideddrivetip()">
						Add Evaluation</a>
					<a class="t_course_delete<%=idx%> color_white bold"
						href="<%=helper.getInstructorCourseDeleteLink(courseDetails.course.id,true)%>"
						onclick="hideddrivetip(); return toggleDeleteCourseConfirmation('<%=courseDetails.course.id%>')"
						onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_COURSE_DELETE%>')"
						onmouseout="hideddrivetip()">
						Delete</a>
				</div>
				<div style="clear: both;"></div>
				<br>
				<%
					if (courseDetails.evaluations.size() > 0) {
				%>
					<table class="dataTable">
						<tr>
							<th class="leftalign color_white bold">Evaluation Name</th>
							<th class="centeralign color_white bold">Status</th>
							<th class="centeralign color_white bold"><span
								onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_RESPONSE_RATE%>')"
								onmouseout="hideddrivetip()">Response Rate</span></th>
							<th class="centeralign color_white bold no-print">Action(s)</th>
						</tr>
						<%
							for (EvaluationDetailsBundle edd: courseDetails.evaluations){ evalIdx++;
						%>
							<tr class="home_evaluations_row" id="evaluation<%=evalIdx%>">
								<td class="t_eval_name<%=idx%>"><%=InstructorHomeHelper.escapeForHTML(edd.evaluation.name)%></td>
								<td class="t_eval_status<%= idx %> centeralign"><span
									onmouseover="ddrivetip('<%= InstructorHomeHelper.getInstructorHoverMessageForEval(edd.evaluation) %>')"
									onmouseout="hideddrivetip()"><%= InstructorHomeHelper.getInstructorStatusForEval(edd.evaluation) %></span></td>
								<td class="t_eval_response<%= idx %> centeralign"><%= edd.submittedTotal %>
									/ <%= edd.expectedTotal %></td>
								<td class="centeralign no-print"><%= helper.getInstructorEvaluationActions(edd.evaluation,evalIdx, true) %>
								</td>
							</tr>
						<%	} %>
					</table>
					<br>
				<%
					}
				%>
			</div>
			<%		out.flush();
				}
			%>
		</div>	
		<br>
		<br>
		<br>
	</div>

	
	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>