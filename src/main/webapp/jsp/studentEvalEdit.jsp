<%@ page import="teammates.api.Common" %>
<%@ page import="teammates.datatransfer.EvaluationData" %>
<%@ page import="teammates.datatransfer.StudentData" %>
<%@ page import="teammates.datatransfer.SubmissionData" %>
<%@ page import="teammates.jsp.StudentEvalEditHelper"%>
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
	<script language="JavaScript" src="/js/helperNew.js"></script>
	<script language="JavaScript" src="/js/commonNew.js"></script>
	
	<script language="JavaScript" src="/js/studentNew.js"></script>	
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
						<td><%= helper.eval.course %></td>
					</tr>
					<tr>
						<td class="fieldname">Evaluation name:</td>
						<td><%= StudentEvalEditHelper.escapeHTML(helper.eval.name) %></td>
					</tr>
					<tr>
						<td class="fieldname">Opening time:</td>
						<td><%= Common.formatTime(helper.eval.startTime) %></td>
					</tr>
					<tr>
						<td class="fieldname">Closing time:</td>
						<td><%= Common.formatTime(helper.eval.endTime) %></td>
					</tr>
					<tr>
						<td class="fieldname">Instructions:</td>
						<td><%= StudentEvalEditHelper.escapeHTML(helper.eval.instructions) %></td>
					</tr>
				</table>
			</div>
			<div id="studentEvaluationSubmissions">
				<form name="form_submitevaluation" id="form_submitevaluation" method="post"
						action="<%= Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT_HANDLER %>">
					<input type="hidden" value="<%= helper.eval.course %>"
							name="<%= Common.PARAM_COURSE_ID %>"
							id="<%= Common.PARAM_COURSE_ID %>" />
					<input type="hidden" value="<%= StudentEvalEditHelper.escapeHTML(helper.eval.name) %>"
							name="<%= Common.PARAM_EVALUATION_NAME %>"
							id="<%= Common.PARAM_EVALUATION_NAME %>" />
					<input type="hidden" value="<%= StudentEvalEditHelper.escapeHTML(helper.student.team) %>"
							name="<%= Common.PARAM_TEAM_NAME %>"
							id="<%= Common.PARAM_TEAM_NAME %>" />
					<input type="hidden" value="<%= helper.student.email %>"
							name="<%= Common.PARAM_FROM_EMAIL %>"
							id="<%= Common.PARAM_FROM_EMAIL %>" />
					<table class="headerform">
						<%	int idx = 0;
							for(SubmissionData sub: helper.submissions){ %>
							<tr style="display:none">
								<td>
		 							<input type="text" value="<%= sub.reviewee %>"
		 									name="<%= Common.PARAM_TO_EMAIL %>"
		 									id="<%= Common.PARAM_TO_EMAIL+idx %>" />
		 						</td>
		 					</tr>
		 					<tr>
		 						<td class="reportheader" colspan="2">
		 							<%= helper.getEvaluationSectionTitle(sub) %>
		 						</td>
		 					</tr>
		 					<tr>
		 						<td class="lhs">Estimated contribution:</td>
		 						<td>
		 							<select style="width: 150px"
		 									name="<%= Common.PARAM_POINTS %>"
		 									id="<%= Common.PARAM_POINTS+idx %>">
		 								<%= helper.getEvaluationOptions(sub) %>
		 							</select>
		 						</td>
		 					</tr>
		 					<tr>
		 						<td class="lhs"><%= helper.getJustificationInstr(sub) %></td>
		 						<td>
		 							<textarea class="textvalue" rows="8" cols="100" 
		 									name="<%= Common.PARAM_JUSTIFICATION %>"
		 									id="<%= Common.PARAM_JUSTIFICATION+idx %>"><%= StudentEvalEditHelper.escapeHTML(sub.justification.getValue()) %></textarea>
		 						</td>
		 					</tr>
		 					<tr>
		 						<td class="lhs"><%= helper.getCommentsInstr(sub) %></td>
								<%	if(helper.eval.p2pEnabled){ %>
									<td><textarea class = "textvalue"
											rows="8" cols="100"
											name="<%= Common.PARAM_COMMENTS %>"
									 		id="<%= Common.PARAM_COMMENTS+idx %>"><%= StudentEvalEditHelper.escapeHTML(sub.p2pFeedback.getValue()) %></textarea>
									</td>
								<%	} else { %>
									<td>
										<font color="red">
											<textarea class="textvalue"
													rows="1" cols="100"
													name="<%= Common.PARAM_COMMENTS %>"
													id="<%= Common.PARAM_COMMENTS+idx %>"
													disabled="disabled">N.A.</textarea>
										</font>
									</td>
								<%	} %>
							</tr>
							<tr><td colspan="2"></td></tr>
						<%		idx++;
							} %>
					</table>
					<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
					<div id="studentEvaluationSubmissionButtons">
						<input type="button" class="button t_back" id="button_back"
								onclick="window.location.href='<%= Common.PAGE_STUDENT_HOME %>'" value="Back" />
						<input type="submit" class="button" name="submitEvaluation"
								onclick="return checkEvaluationForm(this.form)"
								id="submitEvaluation" value="Submit Evaluation" />
					</div>
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