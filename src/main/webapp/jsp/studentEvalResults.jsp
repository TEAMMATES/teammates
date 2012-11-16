<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.common.datatransfer.SubmissionData" %>
<%@ page import="teammates.ui.controller.StudentEvalResultsHelper"%>
<%@ page import="teammates.ui.controller.CoordEvalResultsHelper"%>
<% StudentEvalResultsHelper helper = (StudentEvalResultsHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Student</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css">
	<link rel="stylesheet" href="/stylesheets/studentEvalResults.css" type="text/css">

	<script type="text/javascript" src="/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/student.js"></script>
	<jsp:include page="../enableJS.jsp"></jsp:include>	
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
				<div style="margin: 5px auto; width: 940px; text-align: right; font-size: small; font-style: italic;">E = Equal Share</div>
				<div class="backgroundBlock evalResultHeader"><span class="blockText">Your Result:</span></div>
				<table class="result_studentform">
					<tr>
						<td width="15%">Evaluation:</td>
						<td colspan="2"><%=StudentEvalResultsHelper.escapeForHTML(helper.eval.name)%>
							in
							<%=helper.eval.course%>
						</td>
					</tr>
					<tr>
						<td>Student:</td>
						<td colspan="2"><%=StudentEvalResultsHelper.escapeForHTML(helper.student.name)%>
							in
							<%=StudentEvalResultsHelper.escapeForHTML(helper.student.team)%>
						</td>
					</tr>
					<tr>
						<td>My View:</td>
						<td width="12%">
							Of me: <%=StudentEvalResultsHelper.colorizePoint(helper.evalResult.outgoing.get(0).points)%>
						</td>
						<td>
							Of others: <%=StudentEvalResultsHelper.getPointsListOriginal(helper.outgoing)%>
						</td>
					</tr>
					<tr>
						<td>Teammates View:</td>
						<td>
							Of me: <%=StudentEvalResultsHelper.colorizePoint(helper.evalResult.incoming.get(0).normalizedToStudent)%>
						</td>
						<td>
							Of others: <%=StudentEvalResultsHelper.getNormalizedToStudentsPointsList(helper.incoming)%>
						</td>
					</tr>
				</table>
				<table class="resultTable">
					<tr class="resultSubheader"><td>Feedback from teammates:</td></tr>
					<tr>
						<td>
							<ul>
								<%
									for(SubmissionData sub: helper.incoming) {
								%>
									<li><%=StudentEvalResultsHelper.escapeForHTML(sub.p2pFeedback.getValue())%></li>
								<%
									}
								%>
							</ul>
						</td>
					</tr>
					<tr class="resultSubheader"><td>What others said about their own contribution:</td></tr>
					<tr>
						<td>
							<ul>
								<%
									for(SubmissionData sub: helper.selfEvaluations){
								%>
									<li><%=StudentEvalResultsHelper.escapeForHTML(sub.reviewerName)%>: 
										<%=StudentEvalResultsHelper.escapeForHTML(sub.justification.getValue())%></li>
								<%
									}
								%>
							</ul>
						</td>
					</tr>
				</table>
				<br><br>
				<div class="backgroundBlock evalResultHeader"><span class="blockText">Your Submission:</span></div>
				<table class="result_studentform">
					<tr>
						<td width="15%">Points to yourself:</td>
						<td><%=StudentEvalResultsHelper.colorizePoint(helper.evalResult.getSelfEvaluation().points)%></td>
					</tr>
					<tr>
						<td>Your contribution:</td>
						<td><%=StudentEvalResultsHelper.escapeForHTML(helper.evalResult.getSelfEvaluation().justification.getValue())%></td>
					</tr>
					<tr>
						<td>Team dynamics:</td>
						<td><%=StudentEvalResultsHelper.escapeForHTML(helper.evalResult.getSelfEvaluation().p2pFeedback.getValue())%></td>
					</tr>
				</table>
				<table class="dataTable">
					<tr>
						<th width="18%">Teammate Name</th>
						<th>Points</th>
						<th>Comments about teammate</th>
						<th>Feedback to teammate</th>
					</tr>
					<%
						for(SubmissionData sub: helper.outgoing){
					%>
						<tr>
							<td><%=StudentEvalResultsHelper.escapeForHTML(sub.revieweeName)%></td>
							<td><%=StudentEvalResultsHelper.colorizePoint(sub.points)%></td> 
							<td><%=StudentEvalResultsHelper.escapeForHTML(sub.justification.getValue())%></td>
							<td><%=StudentEvalResultsHelper.escapeForHTML(sub.p2pFeedback.getValue())%></td>
						</tr>
					<%	} %>
				</table>
				<br><br>
				<br><br>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>