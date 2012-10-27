<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.datatransfer.CourseData"%>
<%@ page import="teammates.common.datatransfer.EvaluationData"%>
<%@ page import="teammates.ui.controller.CoordEvalHelper"%>
<%	CoordEvalHelper helper = (CoordEvalHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Coordinator</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css">
	<link rel="stylesheet" href="/stylesheets/coordEval.css" type="text/css">
	
	<script type="text/javascript" src="/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/coordinator.js"></script>
	<script type="text/javascript" src="/js/coordEval.js"></script>
	<% if(helper.submittedEval==null){ %>
	<script type="text/javascript">
		var doPageSpecificOnload = selectDefaultTimeOptions;
	</script>
	<%	} %>

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
				<h1>Add New Evaluation</h1>
			</div>
			<div id="coordinatorEvaluationManagement">
				<form method="post" action="" name="form_addevaluation">
					<table class="inputTable">
						<tr>
							<td class="label" >Course ID:</td>
							<td><select style="width: 260px;"
										name="<%= Common.PARAM_COURSE_ID %>"
										id="<%= Common.PARAM_COURSE_ID %>"
										onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_EVALUATION_INPUT_COURSE %>')"
										onmouseout="hideddrivetip()" tabindex="1">
								<% for(String opt: helper.getCourseIDOptions()) out.println(opt); %>
								</select></td>
							<td class="label" >Opening time:</td>
							<td><input style="width: 100px;" type="text"
										name="<%= Common.PARAM_EVALUATION_START %>"
										id="<%= Common.PARAM_EVALUATION_START %>" 
										onclick ="cal.select(this,'<%= Common.PARAM_EVALUATION_START %>','dd/MM/yyyy')"
										onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_EVALUATION_INPUT_START %>')"
										onmouseout="hideddrivetip()"
										value="<%= (helper.submittedEval==null? Common.formatDate(Common.getNextHour()) : Common.formatDate(helper.submittedEval.startTime)) %>"
										readonly="readonly" tabindex="3">
										@
								<select style="width: 70px;"
										name="<%= Common.PARAM_EVALUATION_STARTTIME %>"
										id="<%= Common.PARAM_EVALUATION_STARTTIME %>"
										tabindex="4">
									<% for(String opt: helper.getTimeOptions(true)) out.println(opt); %>
								</select></td>
						</tr>
						<tr>
							<td class="label" >Evaluation name:</td>
							<td><input style="width: 260px;" type="text"
										name="<%= Common.PARAM_EVALUATION_NAME %>" id="<%= Common.PARAM_EVALUATION_NAME %>"
										onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_EVALUATION_INPUT_NAME %>')"
										onmouseout="hideddrivetip()" maxlength =<%= EvaluationData.EVALUATION_NAME_MAX_LENGTH %>
										value="<%if(helper.submittedEval!=null) out.print(CoordEvalHelper.escapeForHTML(helper.submittedEval.name));%>"
										tabindex="2"></td>
							<td class="label" >Closing time:</td>
							<td><input style="width: 100px;" type="text"
										name="<%=Common.PARAM_EVALUATION_DEADLINE%>" id="<%=Common.PARAM_EVALUATION_DEADLINE%>"
										onclick ="cal.select(this,'<%=Common.PARAM_EVALUATION_DEADLINE%>','dd/MM/yyyy')"
										onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_DEADLINE%>')"
										onmouseout="hideddrivetip()"
										value="<%=(helper.submittedEval==null? "" : Common.formatDate(helper.submittedEval.endTime))%>"
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
							<td class="label" >Peer feedback:</td>
							<td><input type="radio" name="<%=Common.PARAM_EVALUATION_COMMENTSENABLED%>"
										id="commentsstatus_enabled" value="true"
										<%if(helper.submittedEval==null || helper.submittedEval.p2pEnabled) out.print("checked=\"checked\"");%>
										onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_COMMENTSSTATUS%>')"
										onmouseout="hideddrivetip()">
								<label for="commentsstatus_enabled">Enabled</label>
								<input type="radio" name="<%=Common.PARAM_EVALUATION_COMMENTSENABLED%>"
										id="commentsstatus_disabled" value="false"
										<%if(helper.submittedEval!=null && !helper.submittedEval.p2pEnabled) out.print("checked=\"checked\"");%>
										onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_COMMENTSSTATUS%>')"
										onmouseout="hideddrivetip()">
								<label for="commentsstatus_disabled">Disabled</label>
							</td>
							<td class="label" >Time zone:</td>
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
						<td class="label" >Grace Period:</td>
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
							<td class="label" >Instructions to students:</td>
							<td colspan="3">
								<%
									if(helper.submittedEval==null){
								%>
									<textarea rows="3" cols="110" class="textvalue" name="<%=Common.PARAM_EVALUATION_INSTRUCTIONS%>" id="<%=Common.PARAM_EVALUATION_INSTRUCTIONS%>"
											onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_INSTRUCTIONS%>')"
											onmouseout="hideddrivetip()" tabindex="8">Please submit your peer evaluation based on the overall contribution of your teammates so far.</textarea>
								<%
									} else {
								%>
									<textarea rows="3" cols="110" class="textvalue" name="<%=Common.PARAM_EVALUATION_INSTRUCTIONS%>" id="<%=Common.PARAM_EVALUATION_INSTRUCTIONS%>"
											onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_INSTRUCTIONS%>')"
											onmouseout="hideddrivetip()" tabindex="8"><%=CoordEvalHelper.escapeForHTML(helper.submittedEval.instructions)%></textarea>
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
			</div>
			<jsp:include page="<%=Common.JSP_STATUS_MESSAGE%>" />
			<div id="coordinatorEvaluationTable">
				<table class="dataTable">
					<tr>
						<th class="leftalign">
							<input class="buttonSortAscending" type="button" id="button_sortcourseid" 
									onclick="toggleSort(this,1)">Course ID</th>
						<th class="leftalign">
							<input class="buttonSortNone" type="button" id="button_sortname"
									onclick="toggleSort(this,2)">Evaluation</th>
						<th class="centeralign">Status</th>
						<th class="centeralign"><span
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_RESPONSE_RATE%>')"
							onmouseout="hideddrivetip()">Response Rate</span></th>
						<th class="centeralign">Action(s)</th>
					</tr>
					<%
						int evalIdx = -1;
									if (helper.evaluations.size() > 0) {
										for(EvaluationData eval: helper.evaluations){ evalIdx++;
					%>
								<tr class="evaluations_row" id="evaluation<%=evalIdx%>">
									<td class="t_eval_coursecode"><%=eval.course%></td>
									<td class="t_eval_name"><%=CoordEvalHelper.escapeForHTML(eval.name)%></td>
									<td class="t_eval_status centeralign"><span
										onmouseover="ddrivetip(' <%=CoordEvalHelper.getCoordHoverMessageForEval(eval)%>')"
										onmouseout="hideddrivetip()"><%=CoordEvalHelper.getCoordStatusForEval(eval)%></span></td>
									<td class="t_eval_response centeralign"><%= eval.submittedTotal %>
										/ <%= eval.expectedTotal %></td>
									<td class="centeralign"><%=helper.getCoordEvaluationActions(eval,evalIdx, false)%>
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
				<br><br><br>
				<%	if(evalIdx==-1){ %>
					No records found.<br>
					<br>
					<br>
				<%	} %>
				<br>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>