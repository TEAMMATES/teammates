<%@ page import="teammates.api.Common" %>
<%@ page import="teammates.datatransfer.SubmissionData" %>
<%@ page import="teammates.jsp.StudentEvalResultsHelper"%>
<%@ page import="teammates.jsp.CoordEvalResultsHelper"%>
<% StudentEvalResultsHelper helper = (StudentEvalResultsHelper)request.getAttribute("helper"); %>
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
				<h1>Evaluation Results</h1>
			</div>
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
			<div id="studentEvaluationResults">
				<table class="result_table"><tr class="result_header"><td>Your Result:</td></tr></table>
				<table class="result_studentform">
					<tr>
						<td width="15%">Evaluation:</td>
						<td colspan="2"><%= StudentEvalResultsHelper.escapeHTML(helper.eval.name) %>
							in
							<%= helper.eval.course %>
						</td>
					</tr>
					<tr>
						<td>Student:</td>
						<td colspan="2"><%= StudentEvalResultsHelper.escapeHTML(helper.student.name) %>
							in
							<%= StudentEvalResultsHelper.escapeHTML(helper.student.team) %>
						</td>
					</tr>
					<tr>
						<td>My View:</td>
						<td width="12%">
							Of me: <%= StudentEvalResultsHelper.colorizePoint(helper.evalResult.outgoing.get(0).points) %>
						</td>
						<td>
							Of others: <%= StudentEvalResultsHelper.getPointsListOriginal(helper.outgoing) %>
						</td>
					</tr>
					<tr>
						<td>Teammates View:</td>
						<td>
							Of me: <%= StudentEvalResultsHelper.colorizePoint(helper.evalResult.incoming.get(0).normalized) %>
						</td>
						<td>
							Of others: <%= StudentEvalResultsHelper.getPointsListNormalized(helper.incoming) %>
						</td>
					</tr>
				</table>
				<table class="result_table">
					<tr class="result_subheader"><td>Feedback from teammates:</td></tr>
					<tr>
						<td>
							<ul>
								<%	for(SubmissionData sub: helper.incoming){ %>
									<li><%= StudentEvalResultsHelper.escapeHTML(sub.p2pFeedback.getValue()) %></li>
								<%	} %>
							</ul>
						</td>
					</tr>
					<tr class="result_subheader"><td>What others think about themselves:</td></tr>
					<tr>
						<td>
							<ul>
								<%	for(SubmissionData sub: helper.selfEvaluations){ %>
									<li><%= StudentEvalResultsHelper.escapeHTML(sub.reviewerName) %>: 
										<%= StudentEvalResultsHelper.escapeHTML(sub.p2pFeedback.getValue()) %></li>
								<%	} %>
							</ul>
						</td>
					</tr>
				</table>
				<br /><br />
				<table class="result_table"><tr class="result_header"><td>Your Submission:</td></tr></table>
				<table class="result_studentform">
					<tr>
						<td width="15%">Points to yourself:</td>
						<td><%= StudentEvalResultsHelper.colorizePoint(helper.evalResult.getSelfEvaluation().points) %></td>
					</tr>
					<tr>
						<td>Your contribution:</td>
						<td><%= StudentEvalResultsHelper.escapeHTML(helper.evalResult.getSelfEvaluation().justification.getValue()) %></td>
					</tr>
					<tr>
						<td>Team dynamics:</td>
						<td><%= StudentEvalResultsHelper.escapeHTML(helper.evalResult.getSelfEvaluation().p2pFeedback.getValue()) %></td>
					</tr>
				</table>
				<table id="dataform" style="margin:0; width:100%">
					<tr>
						<th width="18%">Teammate Name</th>
						<th>Points</th>
						<th>Comments about teammate</th>
						<th>Feedback to teammate</th>
					</tr>
					<%	for(SubmissionData sub: helper.outgoing){ %>
						<tr>
							<td><%= StudentEvalResultsHelper.escapeHTML(sub.revieweeName) %></td>
							<td><%= StudentEvalResultsHelper.colorizePoint(sub.points) %></td> 
							<td><%= StudentEvalResultsHelper.escapeHTML(sub.justification.getValue()) %></td>
							<td><%= StudentEvalResultsHelper.escapeHTML(sub.p2pFeedback.getValue()) %></td>
						</tr>
					<%	} %>
				</table>
				<br /><br />
				<input type="button" class="button" id="button_back" value="Back"
						onclick="window.location.href='<%= helper.getStudentHomeLink() %>'" />
				<br /><br />
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>