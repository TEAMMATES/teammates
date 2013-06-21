<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes.EvalStatus" %>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes" %>
<%@ page import="teammates.common.datatransfer.StudentAttributes" %>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes" %>
<%@ page import="teammates.ui.controller.StudentEvalSubmissionEditPageData"%>
<%@ page import="java.util.Date" %>
<%
	StudentEvalSubmissionEditPageData data = (StudentEvalSubmissionEditPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Student</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/studentEvalEdit.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/studentEvalEdit-print.css" type="text/css" media="print">
	

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
		<jsp:include page="<%=Common.JSP_STUDENT_HEADER_NEW%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Evaluation Submission</h1>
			</div>
			
			<table class="inputTable" id="studentEvaluationInformation">
				<tr>
					<td class="label rightalign bold" width="30%">Course ID:</td>
					<td id="<%=Common.PARAM_COURSE_ID%>"><%=data.eval.courseId%></td>
				</tr>
				<tr>
					<td class="label rightalign bold" width="30%">Evaluation name:</td>
					<td id="<%=Common.PARAM_EVALUATION_NAME%>"><%=StudentEvalSubmissionEditPageData.escapeForHTML(data.eval.name)%></td>
				</tr>
				<tr>
					<td class="label rightalign bold" width="30%">Opening time:</td>
					<td id="<%=Common.PARAM_EVALUATION_STARTTIME%>"><%=Common.formatTime(data.eval.startTime)%></td>
				</tr>
				<tr>
					<td class="label rightalign bold" width="30%">Closing time:</td>
					<td id="<%=Common.PARAM_EVALUATION_DEADLINETIME%>"><%=Common.formatTime(data.eval.endTime)%></td>
				</tr>
				<tr>
					<td class="label rightalign bold" width="30%">Instructions:</td>
					<td id="<%=Common.PARAM_EVALUATION_INSTRUCTIONS%>"><%=StudentEvalSubmissionEditPageData.escapeForHTML(data.eval.instructions)%></td>
				</tr>
			</table>
			
			<br>
			<br>
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE_NEW %>" />
			<br>
			<br>
			
			<form name="form_submitevaluation" id="form_submitevaluation" method="post"
					action="<%= Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT_HANDLER %>">
				<jsp:include page="<%= Common.JSP_EVAL_SUBMISSION_EDIT %>">
				<jsp:param name="isStudent" value="true" />
				</jsp:include>
				<br>
				<br>
				<div id="studentEvaluationSubmissionButtons" class="centeralign">
					<input type="submit" class="button" name="submitEvaluation"
							onclick="return checkEvaluationForm(this.form)"
							id="button_submit" value="Submit Evaluation" <%=data.disableAttribute %>>
				</div>
				<input type="hidden" name="<%= Common.PARAM_USER_ID %>" value="<%= data.account.googleId %>">
			</form>
		 	<br>
		 	<br>
		 	<br>
		
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER_NEW %>" />
	</div>
</body>
</html>