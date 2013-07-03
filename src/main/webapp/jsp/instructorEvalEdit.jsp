<%@ page import="teammates.common.util.Constants"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.ui.controller.InstructorEvalEditPageData"%>
<%
	InstructorEvalEditPageData data = (InstructorEvalEditPageData)request.getAttribute("data");
%>
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
		<jsp:include page="<%=Constants.VIEW_INSTRUCTOR_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Edit Evaluation</h1>
			</div>
			
			<form method="post" action="<%=Constants.ACTION_INSTRUCTOR_EVAL_EDIT_SAVE%>" name="form_addevaluation">
				<table class="inputTable" id="instructorEvaluationManagement">
					<tr>
						<td class="label bold" >Course ID:</td>
						<td style="vertical-align: middle;">
							<input type="hidden" name="<%=Constants.PARAM_COURSE_ID%>" value="<%=data.evaluation.courseId%>">
							<%=data.evaluation.courseId%>
						</td>
						<td class="label bold" >Opening time:</td>
						<td><input style="width: 100px;" type="text"
									name="<%=Constants.PARAM_EVALUATION_START%>"
									id="<%=Constants.PARAM_EVALUATION_START%>" 
									onclick ="cal.select(this,'<%=Constants.PARAM_EVALUATION_START%>','dd/MM/yyyy')"
									onmouseover="ddrivetip('<%=Constants.TOOLTIP_EVALUATION_INPUT_START%>')"
									onmouseout="hideddrivetip()"
									value="<%=TimeHelper.formatDate(data.evaluation.startTime)%>"
									readonly="readonly" tabindex="3">
									@
							<select style="width: 70px;"
									name="<%=Constants.PARAM_EVALUATION_STARTTIME%>"
									id="<%=Constants.PARAM_EVALUATION_STARTTIME%>"
									tabindex="4">
								<%
									for(String opt: data.getTimeOptionsAsHtml(true)) out.println(opt);
								%>
							</select></td>
					</tr>
					<tr>
						<td class="label bold" >Evaluation name:</td>
						<td style="vertical-align: middle;">
							<input type="hidden" name="<%=Constants.PARAM_EVALUATION_NAME%>" value="<%=InstructorEvalEditPageData.sanitizeForHtml(data.evaluation.name)%>">
							<%=InstructorEvalEditPageData.sanitizeForHtml(data.evaluation.name)%>
						</td>
						<td class="label bold" >Closing time:</td>
						<td><input style="width: 100px;" type="text"
									name="<%=Constants.PARAM_EVALUATION_DEADLINE%>" id="<%=Constants.PARAM_EVALUATION_DEADLINE%>"
									onclick ="cal.select(this,'<%=Constants.PARAM_EVALUATION_DEADLINE%>','dd/MM/yyyy')"
									onmouseover="ddrivetip('<%=Constants.TOOLTIP_EVALUATION_INPUT_DEADLINE%>')"
									onmouseout="hideddrivetip()"
									value="<%=TimeHelper.formatDate(data.evaluation.endTime)%>"
									readonly="readonly" tabindex="5">
									@
							<select style="width: 70px;"
									name="<%=Constants.PARAM_EVALUATION_DEADLINETIME%>"
									id="<%=Constants.PARAM_EVALUATION_DEADLINETIME%>"
									tabindex="6">
								<%
									for(String opt: data.getTimeOptionsAsHtml(false)) out.println(opt);
								%>
							</select></td>
					</tr>
					<tr>
						<td class="label bold" >Peer feedback:</td>
						<td><input type="radio" name="<%=Constants.PARAM_EVALUATION_COMMENTSENABLED%>"
									id="commentsstatus_enabled" value="true"
									<%=data.evaluation.p2pEnabled ? "checked=\"checked\"" : ""%>
									onmouseover="ddrivetip('<%=Constants.TOOLTIP_EVALUATION_INPUT_COMMENTSSTATUS%>')"
									onmouseout="hideddrivetip()">
							<label for="commentsstatus_enabled">Enabled</label>
							<input type="radio" name="<%=Constants.PARAM_EVALUATION_COMMENTSENABLED%>"
									id="commentsstatus_disabled" value="false"
									<%=!data.evaluation.p2pEnabled ? "checked=\"checked\"" : ""%>
									onmouseover="ddrivetip('<%=Constants.TOOLTIP_EVALUATION_INPUT_COMMENTSSTATUS%>')"
									onmouseout="hideddrivetip()">
							<label for="commentsstatus_disabled">Disabled</label>
						</td>
						<td class="bold label" >Time zone:</td>
						<td><select style="width: 100px;" name="<%=Constants.PARAM_EVALUATION_TIMEZONE%>" id="<%=Constants.PARAM_EVALUATION_TIMEZONE%>"
									onmouseover="ddrivetip('<%=Constants.TOOLTIP_EVALUATION_INPUT_TIMEZONE%>')"
									onmouseout="hideddrivetip()" disabled="disabled" tabindex="7">
							<%
								for(String opt: data.getTimeZoneOptionsAsHtml()) out.println(opt);
							%>
							</select>
							<input type="hidden" name="<%=Constants.PARAM_EVALUATION_TIMEZONE%>" value="<%=data.evaluation.timeZone%>">
						</td>
					</tr>
					<tr>
					<td></td>
					<td></td>
					<td class="bold label" >Grace Period:</td>
					<td class="inputField">
						<select style="width: 70px;" name="<%=Constants.PARAM_EVALUATION_GRACEPERIOD%>"
								id="<%=Constants.PARAM_EVALUATION_GRACEPERIOD%>"
								onmouseover="ddrivetip('<%=Constants.TOOLTIP_EVALUATION_INPUT_GRACEPERIOD%>')"
								onmouseout="hideddrivetip()" tabindex="7">
							<%
								for(String opt: data.getGracePeriodOptionsAsHtml()) out.println(opt);
							%>
						</select></td>
					</tr>
					<tr>
						<td class="label bold middlealign">Instructions to students:</td>
						<td colspan="3">
						<table><tr><td>
							<textarea rows="3" cols="90" class="textvalue" name="<%=Constants.PARAM_EVALUATION_INSTRUCTIONS%>" id="<%=Constants.PARAM_EVALUATION_INSTRUCTIONS%>"
									onmouseover="ddrivetip('<%=Constants.TOOLTIP_EVALUATION_INPUT_INSTRUCTIONS%>')"
									onmouseout="hideddrivetip()" tabindex="8"><%=InstructorEvalEditPageData.sanitizeForHtml(data.evaluation.instructions)%></textarea>
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
									onclick="window.location.href='<%=data.getInstructorEvaluationLink()%>'"
									value="Cancel" tabindex="10"></td>
					</tr>
				</table>
				<input type="hidden" name="<%=Constants.PARAM_USER_ID%>" value="<%=data.account.googleId%>">
			</form>
			
			<br>
			<jsp:include page="<%=Constants.VIEW_STATUS_MESSAGE%>" />
			<br>
			<br>
			<br>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Constants.VIEW_FOOTER%>" />
	</div>
</body>
</html>