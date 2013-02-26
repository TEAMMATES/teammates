<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.datatransfer.CourseData"%>
<%@ page import="teammates.common.datatransfer.EvaluationData"%>
<%@ page import="teammates.ui.controller.InstructorEvalHelper"%>
<%	InstructorEvalHelper helper = (InstructorEvalHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Instructor</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css">
	<link rel="stylesheet" href="/stylesheets/instructorEval.css" type="text/css">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/instructor.js"></script>
	<script type="text/javascript" src="/js/instructorEval.js"></script>
	<%
		if(helper.newEvaluationToBeCreated==null){
	%>
	<script type="text/javascript">
		var doPageSpecificOnload = selectDefaultTimeOptions;
	</script>
	<%
		}
	%>
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
				<h1>Add New Evaluation</h1>
			</div>
			
			<form method="post" action="" name="form_addevaluation">
				<table class="inputTable" id="instructorEvaluationManagement">
					<tr>
						<td class="label bold" >Course ID:</td>
						<td><select style="width: 260px;"
									name="<%=Common.PARAM_COURSE_ID%>"
									id="<%=Common.PARAM_COURSE_ID%>"
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_COURSE%>')"
									onmouseout="hideddrivetip()" tabindex="1">
							<%
								for(String opt: helper.getCourseIdOptions()) out.println(opt);
							%>
							</select></td>
						<td class="label bold" >Opening time:</td>
						<td><input style="width: 100px;" type="text"
									name="<%=Common.PARAM_EVALUATION_START%>"
									id="<%=Common.PARAM_EVALUATION_START%>" 
									onclick ="cal.select(this,'<%=Common.PARAM_EVALUATION_START%>','dd/MM/yyyy')"
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_START%>')"
									onmouseout="hideddrivetip()"
									value="<%=(helper.newEvaluationToBeCreated==null? Common.formatDate(Common.getNextHour()) : Common.formatDate(helper.newEvaluationToBeCreated.startTime))%>"
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
						<td><input style="width: 260px;" type="text"
									name="<%=Common.PARAM_EVALUATION_NAME%>" id="<%=Common.PARAM_EVALUATION_NAME%>"
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_NAME%>')"
									onmouseout="hideddrivetip()" maxlength =<%=EvaluationData.EVALUATION_NAME_MAX_LENGTH%>
									value="<%if(helper.newEvaluationToBeCreated!=null) out.print(InstructorEvalHelper.escapeForHTML(helper.newEvaluationToBeCreated.name));%>"
									tabindex="2" placeholder="e.g. Midterm Evaluation"></td>
						<td class="label bold" >Closing time:</td>
						<td><input style="width: 100px;" type="text"
									name="<%=Common.PARAM_EVALUATION_DEADLINE%>" id="<%=Common.PARAM_EVALUATION_DEADLINE%>"
									onclick ="cal.select(this,'<%=Common.PARAM_EVALUATION_DEADLINE%>','dd/MM/yyyy')"
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_DEADLINE%>')"
									onmouseout="hideddrivetip()"
									value="<%=(helper.newEvaluationToBeCreated==null? "" : Common.formatDate(helper.newEvaluationToBeCreated.endTime))%>"
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
						<td class="label bold">Peer feedback:</td>
						<td><input type="radio" name="<%=Common.PARAM_EVALUATION_COMMENTSENABLED%>"
									id="commentsstatus_enabled" value="true"
									<%if(helper.newEvaluationToBeCreated==null || helper.newEvaluationToBeCreated.p2pEnabled) out.print("checked=\"checked\"");%>
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_COMMENTSSTATUS%>')"
									onmouseout="hideddrivetip()">
							<label for="commentsstatus_enabled">Enabled</label>
							<input type="radio" name="<%=Common.PARAM_EVALUATION_COMMENTSENABLED%>"
									id="commentsstatus_disabled" value="false"
									<%if(helper.newEvaluationToBeCreated!=null && !helper.newEvaluationToBeCreated.p2pEnabled) out.print("checked=\"checked\"");%>
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_COMMENTSSTATUS%>')"
									onmouseout="hideddrivetip()">
							<label for="commentsstatus_disabled">Disabled</label>
						</td>
						<td class="label bold" >Time zone:</td>
						<td><select style="width: 100px;" name="<%=Common.PARAM_EVALUATION_TIMEZONE%>" id="<%=Common.PARAM_EVALUATION_TIMEZONE%>"
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_TIMEZONE%>')"
									onmouseout="hideddrivetip()" tabindex="7">
							<%
								for(String opt: helper.getTimeZoneOptions()) out.println(opt);
							%>
							</select>
						</td>
					</tr>
					<tr>
					<td></td>
					<td></td>
					<td class="label bold" >Grace Period:</td>
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
						<td class="label bold middlealign" >Instructions to students:</td>
						<td colspan="3">
							<%
								if(helper.newEvaluationToBeCreated==null){
							%>
								<textarea rows="3" cols="90" class="textvalue" name="<%=Common.PARAM_EVALUATION_INSTRUCTIONS%>" id="<%=Common.PARAM_EVALUATION_INSTRUCTIONS%>"
										onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_INSTRUCTIONS%>')"
										onmouseout="hideddrivetip()" tabindex="8">Please submit your peer evaluation based on the overall contribution of your teammates so far.</textarea>
							<%
								} else {
							%>
								<textarea rows="3" cols="90" class="textvalue" name="<%=Common.PARAM_EVALUATION_INSTRUCTIONS%>" id="<%=Common.PARAM_EVALUATION_INSTRUCTIONS%>"
										onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_INSTRUCTIONS%>')"
										onmouseout="hideddrivetip()" tabindex="8"><%=InstructorEvalHelper.escapeForHTML(helper.newEvaluationToBeCreated.instructions)%></textarea>
							<%
								}
							%>
						</td>
					</tr>
					<tr>
						<td colspan="4" class="centeralign">
							<input id="button_submit" type="submit" class="button"
									onclick="return checkAddEvaluation(this.form);"
									value="Create Evaluation" tabindex="9"></td>
					</tr>
				</table>
			</form>
			
			<br>
			<jsp:include page="<%=Common.JSP_STATUS_MESSAGE%>" />
			<br>
			

			<table class="dataTable">
				<tr>
					<th class="leftalign color_white bold">
						<input class="buttonSortAscending" type="button" id="button_sortcourseid" 
								onclick="toggleSort(this,1)">Course ID</th>
					<th class="leftalign color_white bold">
						<input class="buttonSortNone" type="button" id="button_sortname"
								onclick="toggleSort(this,2)">Evaluation</th>
					<th class="centeralign color_white bold">Status</th>
					<th class="centeralign color_white bold"><span
						onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_RESPONSE_RATE%>')"
						onmouseout="hideddrivetip()">Response Rate</span></th>
					<th class="centeralign color_white bold">Action(s)</th>
				</tr>
				<%
					int evalIdx = -1;
								if (helper.evaluations.size() > 0) {
									for(EvaluationData eval: helper.evaluations){ evalIdx++;
				%>
							<tr class="evaluations_row" id="evaluation<%=evalIdx%>">
								<td class="t_eval_coursecode"><%=eval.course%></td>
								<td class="t_eval_name"><%=InstructorEvalHelper.escapeForHTML(eval.name)%></td>
								<td class="t_eval_status centeralign"><span
									onmouseover="ddrivetip(' <%=InstructorEvalHelper.getInstructorHoverMessageForEval(eval)%>')"
									onmouseout="hideddrivetip()"><%=InstructorEvalHelper.getInstructorStatusForEval(eval)%></span></td>
								<td class="t_eval_response centeralign"><%= eval.submittedTotal %>
									/ <%= eval.expectedTotal %></td>
								<td class="centeralign"><%=helper.getInstructorEvaluationActions(eval,evalIdx, false)%>
								</td>
							</tr>
						<%	} %>
				<%	} else { %>
					<tr>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
				<%	} %>
			</table>
			<br
			><br>
			<br>
			<%	if(evalIdx==-1){ %>
				No records found.<br>
				<br>
				<br>
			<%	} %>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>