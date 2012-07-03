<%@ page import="teammates.api.Common" %>
<%@ page import="teammates.datatransfer.EvaluationData" %>
<%@ page import="teammates.datatransfer.StudentData" %>
<%@ page import="teammates.datatransfer.SubmissionData" %>
<%@ page import="teammates.ui.StudentEvalEditHelper"%>
<% StudentEvalEditHelper helper = (StudentEvalEditHelper)request.getAttribute("helper"); %>
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
	
	<script language="JavaScript" src="/js/student.js"></script>	
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
				<h1>Evaluation Submission</h1>
			</div>
			<div id="studentEvaluationInformation">
				<table class="headerform">
					<tr>
						<td class="fieldname">Course ID:</td>
						<td id="<%= Common.PARAM_COURSE_ID %>"><%= helper.eval.course %></td>
					</tr>
					<tr>
						<td class="fieldname">Evaluation name:</td>
						<td id="<%= Common.PARAM_EVALUATION_NAME %>"><%=StudentEvalEditHelper.escapeForHTML(helper.eval.name)%></td>
					</tr>
					<tr>
						<td class="fieldname">Opening time:</td>
						<td id="<%=Common.PARAM_EVALUATION_STARTTIME%>"><%=Common.formatTime(helper.eval.startTime)%></td>
					</tr>
					<tr>
						<td class="fieldname">Closing time:</td>
						<td id="<%=Common.PARAM_EVALUATION_DEADLINETIME%>"><%=Common.formatTime(helper.eval.endTime)%></td>
					</tr>
					<tr>
						<td class="fieldname">Instructions:</td>
						<td id="<%=Common.PARAM_EVALUATION_INSTRUCTIONS%>"><%=StudentEvalEditHelper.escapeForHTML(helper.eval.instructions)%></td>
					</tr>
				</table>
			</div>
			<div id="studentEvaluationSubmissions">
				<form name="form_submitevaluation" id="form_submitevaluation" method="post"
						action="<%= Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT_HANDLER %>">
					<jsp:include page="<%= Common.JSP_EVAL_SUBMISSION_EDIT %>" />
					<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
					<div id="studentEvaluationSubmissionButtons">
						<input type="button" class="button" id="button_back"
								onclick="window.location.href='<%= helper.getStudentHomeLink() %>'" value="Back" />
						<input type="submit" class="button" name="submitEvaluation"
								onclick="return checkEvaluationForm(this.form)"
								id="button_submit" value="Submit Evaluation" />
					</div>
					<% if(helper.isMasqueradeMode()){ %>
						<input type="hidden" name="<%= Common.PARAM_USER_ID %>" value="<%= helper.requestedUser %>" />
					<% } %>
				</form>
		 		<br /><br />
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>