<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.ui.controller.InstructorEvalEditHelper"%>
<%	InstructorEvalEditHelper helper = (InstructorEvalEditHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Instructor</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/instructorEvalEdit.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/instructorEvalEdit-print.css" type="text/css" media="print">
	
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/instructor.js"></script>
	<script type="text/javascript" src="/js/instructorEval.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%= Common.JSP_INSTRUCTOR_HEADER %>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Edit Evaluation</h1>
			</div>
			
			<form method="post" action="<%= Common.PAGE_INSTRUCTOR_EVAL_EDIT %>" name="form_addevaluation">
				<table class="inputTable" id="instructorEvaluationManagement">
					<tr>
						<td class="label bold" >Course ID:</td>
						<td style="vertical-align: middle;">
							<input type="hidden" name="<%= Common.PARAM_COURSE_ID %>" value="<%=helper.newEvaluationToBeCreated.course%>">
							<%=helper.newEvaluationToBeCreated.course%>
						</td>
						<td class="label bold" >Opening time:</td>
						<td><input style="width: 100px;" type="text"
									name="<%=Common.PARAM_EVALUATION_START%>"
									id="<%=Common.PARAM_EVALUATION_START%>" 
									onclick ="cal.select(this,'<%=Common.PARAM_EVALUATION_START%>','dd/MM/yyyy')"
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_START%>')"
									onmouseout="hideddrivetip()"
									value="<%=Common.formatDate(helper.newEvaluationToBeCreated.startTime)%>"
									readonly="readonly" tabindex="3">
									@
							<select style="width: 70px;"
									name="<%=Common.PARAM_EVALUATION_STARTTIME%>"
									id="<%=Common.PARAM_EVALUATION_STARTTIME%>"
									tabindex="4">
								<%
									for(String opt: helper.getTimeOptions(true)) out.println(opt);
								%>
							</select></td>
					</tr>
					<tr>
						<td class="label bold" >Evaluation name:</td>
						<td style="vertical-align: middle;">
							<input type="hidden" name="<%=Common.PARAM_EVALUATION_NAME%>" value="<%=InstructorEvalEditHelper.escapeForHTML(helper.newEvaluationToBeCreated.name)%>">
							<%=InstructorEvalEditHelper.escapeForHTML(helper.newEvaluationToBeCreated.name)%>
						</td>
						<td class="label bold" >Closing time:</td>
						<td><input style="width: 100px;" type="text"
									name="<%=Common.PARAM_EVALUATION_DEADLINE%>" id="<%=Common.PARAM_EVALUATION_DEADLINE%>"
									onclick ="cal.select(this,'<%=Common.PARAM_EVALUATION_DEADLINE%>','dd/MM/yyyy')"
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_DEADLINE%>')"
									onmouseout="hideddrivetip()"
									value="<%=Common.formatDate(helper.newEvaluationToBeCreated.endTime)%>"
									readonly="readonly" tabindex="5">
									@
							<select style="width: 70px;"
									name="<%=Common.PARAM_EVALUATION_DEADLINETIME%>"
									id="<%=Common.PARAM_EVALUATION_DEADLINETIME%>"
									tabindex="6">
								<%
									for(String opt: helper.getTimeOptions(false)) out.println(opt);
								%>
							</select></td>
					</tr>
					<tr>
						<td class="label bold" >Peer feedback:</td>
						<td><input type="radio" name="<%=Common.PARAM_EVALUATION_COMMENTSENABLED%>"
									id="commentsstatus_enabled" value="true"
									<%=helper.newEvaluationToBeCreated.p2pEnabled ? "checked=\"checked\"" : ""%>
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_COMMENTSSTATUS%>')"
									onmouseout="hideddrivetip()">
							<label for="commentsstatus_enabled">Enabled</label>
							<input type="radio" name="<%=Common.PARAM_EVALUATION_COMMENTSENABLED%>"
									id="commentsstatus_disabled" value="false"
									<%=!helper.newEvaluationToBeCreated.p2pEnabled ? "checked=\"checked\"" : ""%>
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_COMMENTSSTATUS%>')"
									onmouseout="hideddrivetip()">
							<label for="commentsstatus_disabled">Disabled</label>
						</td>
						<td class="bold label" >Time zone:</td>
						<td><select style="width: 100px;" name="<%=Common.PARAM_EVALUATION_TIMEZONE%>" id="<%=Common.PARAM_EVALUATION_TIMEZONE%>"
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_TIMEZONE%>')"
									onmouseout="hideddrivetip()" disabled="disabled" tabindex="7">
							<%
								for(String opt: helper.getTimeZoneOptions()) out.println(opt);
							%>
							</select>
							<input type="hidden" name="<%=Common.PARAM_EVALUATION_TIMEZONE%>" value="<%=helper.newEvaluationToBeCreated.timeZone%>">
						</td>
					</tr>
					<tr>
					<td></td>
					<td></td>
					<td class="bold label" >Grace Period:</td>
					<td class="inputField">
						<select style="width: 70px;" name="<%=Common.PARAM_EVALUATION_GRACEPERIOD%>"
								id="<%=Common.PARAM_EVALUATION_GRACEPERIOD%>"
								onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_GRACEPERIOD%>')"
								onmouseout="hideddrivetip()" tabindex="7">
							<%
								for(String opt: helper.getGracePeriodOptions()) out.println(opt);
							%>
						</select></td>
					</tr>
					<tr>
						<td class="label bold middlealign">Instructions to students:</td>
						<td colspan="3">
						<table><tr><td>
							<textarea rows="3" cols="90" class="textvalue" name="<%=Common.PARAM_EVALUATION_INSTRUCTIONS%>" id="<%=Common.PARAM_EVALUATION_INSTRUCTIONS%>"
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_INSTRUCTIONS%>')"
									onmouseout="hideddrivetip()" tabindex="8"><%=InstructorEvalEditHelper.escapeForHTML(helper.newEvaluationToBeCreated.instructions)%></textarea>
							<p align=right><font color=grey>[maximum length = 500 characters]</font></p>
						</td></tr></table>
						</td>
					</tr>
					<tr>
					<td colspan="4" class="centeralign">
							<input id="button_submit" type="submit" class="button"
									onclick="return checkEditEvaluation(this.form);"
									value="Save Changes" tabindex="9">
							<input id="button_back" type="button" class="button"
									onclick="window.location.href='<%= helper.getInstructorEvaluationLink() %>'"
									value="Cancel" tabindex="10"></td>
					</tr>
				</table>
				<% if(helper.isMasqueradeMode()){ %>
					<input type="hidden" name="<%= Common.PARAM_USER_ID %>" value="<%= helper.requestedUser %>">
				<% } %>
			</form>
			
			<br>
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
			<br>
			<br>
			<br>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>