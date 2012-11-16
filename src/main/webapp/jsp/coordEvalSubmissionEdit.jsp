<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.datatransfer.CourseData"%>
<%@ page import="teammates.common.datatransfer.EvaluationData"%>
<%@ page import="teammates.common.datatransfer.SubmissionData"%>
<%@ page import="teammates.ui.controller.CoordEvalSubmissionEditHelper"%>
<%	CoordEvalSubmissionEditHelper helper = (CoordEvalSubmissionEditHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Coordinator</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css">
	<link rel="stylesheet" href="/stylesheets/coordEvalSubmissionEdit.css" type="text/css">
	
	<script type="text/javascript" src="/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/coordinator.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
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
				<h1>Edit Student's Submission</h1>
				<table class="inputTable" id="studentEvaluationInfo">
					<tr>
						<td class="label rightalign" width="30%">Course ID:</td>
						<td class="leftalign"><%= helper.eval.course %></td>
					</tr>
					<tr>
						<td class="label rightalign" width="30%">Evaluation Name:</td>
						<td class="leftalign"><%=CoordEvalSubmissionEditHelper.escapeForHTML(helper.eval.name)%></td>
					</tr>
				</table>
			</div>
			<div id="studentEvaluationSubmissions">
				<form name="form_submitevaluation" id="form_submitevaluation" method="post"
						action="<%= Common.PAGE_COORD_EVAL_SUBMISSION_EDIT_HANDLER %>">
					<jsp:include page="<%= Common.JSP_EVAL_SUBMISSION_EDIT %>" />
					<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
					<div id="studentEvaluationSubmissionButtons" class="centeralign">
						<input type="button" class="button" id="button_back"
								onclick="window.close(); opener.setStatusMessage('')" value="Cancel">
						<input type="submit" class="button" name="submitEvaluation"
								onclick="return checkEvaluationForm(this.form)"
								id="button_submit" value="Save Changes">
					</div>
					<% if(helper.isMasqueradeMode()){ %>
						<input type="hidden" name="<%= Common.PARAM_USER_ID %>" value="<%= helper.requestedUser %>">
					<% } %>
				</form>
		 		<br><br>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>