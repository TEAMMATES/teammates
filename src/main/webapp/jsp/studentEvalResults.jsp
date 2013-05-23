<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes" %>
<%@ page import="teammates.ui.controller.StudentEvalResultsHelper"%>
<%@ page import="teammates.ui.controller.InstructorEvalResultsHelper"%>
<%
	StudentEvalResultsHelper helper = (StudentEvalResultsHelper)request.getAttribute("helper");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Student</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/studentEvalResults.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/studentEvalResults-print.css" type="text/css" media="print">

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
		<jsp:include page="<%=Common.JSP_STUDENT_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Evaluation Results</h1>
			</div>
			
			<jsp:include page="<%=Common.JSP_STATUS_MESSAGE%>" />
			
			<div id="studentEvaluationResults">
				<div id="equalShareTag">E = Equal Share</div>
				<div class="backgroundBlock evalResultHeader">
					<span class="color_white bold">Your Result:</span>
				</div>
				
				<table class="result_studentform">
					<tr>
						<td width="15%" class="bold color_white">Evaluation:</td>
						<td colspan="2"><%=StudentEvalResultsHelper.escapeForHTML(helper.eval.name)%>
							in
							<%=helper.eval.courseId%>
						</td>
					</tr>
					<tr>
						<td class="bold color_white">Student:</td>
						<td colspan="2"><%=StudentEvalResultsHelper.escapeForHTML(helper.student.name)%>
							in
							<%=StudentEvalResultsHelper.escapeForHTML(helper.student.team)%>
						</td>
					</tr>
					<tr>
						<td class="bold color_white">My View:</td>
						<td width="12%">
							Of me: <%=StudentEvalResultsHelper.colorizePoint(helper.evalResult.outgoing.get(0).points)%>
						</td>
						<td>
							Of others: <%=StudentEvalResultsHelper.getPointsListOriginal(helper.outgoing)%>
						</td>
					</tr>
					<tr>
						<td class="bold color_white">Team's View:</td>
						<td>
							Of me: <%=StudentEvalResultsHelper.colorizePoint(helper.evalResult.incoming.get(0).details.normalizedToStudent)%>
						</td>
						<td>
							Of others: <%=StudentEvalResultsHelper.getNormalizedToStudentsPointsList(helper.incoming)%>
						</td>
					</tr>
				</table>
				
				<table class="resultTable">
					<tr class="resultSubheader bold color_black"><td>Anonymous Feedback from Teammates:</td></tr>
					<tr>
						<td>
							<ul>
								<%
									for(SubmissionAttributes sub: helper.incoming) {
								%>
									<li><%=StudentEvalResultsHelper.formatP2PFeedback(StudentEvalResultsHelper.escapeForHTML(sub.p2pFeedback.getValue()), helper.eval.p2pEnabled)%></li>
								<%
									}
								%>
							</ul>
						</td>
					</tr>
					<tr class="resultSubheader bold color_black"><td>What others said about their own contribution:</td></tr>
					<tr>
						<td>
							<ul>
								<%
									for(SubmissionAttributes sub: helper.selfEvaluations){
								%>
									<li><span class="bold"><%=StudentEvalResultsHelper.escapeForHTML(sub.details.reviewerName)%>:</span> 
										<%=StudentEvalResultsHelper.escapeForHTML(sub.justification.getValue())%></li>
									<br>
								<%
									}
								%>
							</ul>
						</td>
					</tr>
				</table>
				<br>
				<br>
				
				<div class="backgroundBlock evalResultHeader"><span class="color_white bold">Your Submission:</span></div>
				<table class="result_studentform">
					<tr>
						<td width="15%" class="bold color_white">Points to yourself:</td>
						<td><%=StudentEvalResultsHelper.colorizePoint(helper.evalResult.getSelfEvaluation().points)%></td>
					</tr>
					<tr>
						<td class="bold color_white">Your contribution:</td>
						<td><%=StudentEvalResultsHelper.escapeForHTML(helper.evalResult.getSelfEvaluation().justification.getValue())%></td>
					</tr>
					<tr>
						<td class="bold color_white">Team dynamics:</td>
						<td><%=StudentEvalResultsHelper.escapeForHTML(helper.evalResult.getSelfEvaluation().p2pFeedback.getValue())%></td>
					</tr>
				</table>
				<table class="dataTable">
					<tr>
						<th width="18%" class="bold leftalign color_white">Teammate Name</th>
						<th class="bold centeralign color_white">Points</th>
						<th class="bold centeralign color_white">Comments about teammate</th>
						<th class="bold centeralign color_white">Feedback to teammate</th>
					</tr>
					<%
						for(SubmissionAttributes sub: helper.outgoing){
					%>
						<tr>
							<td><%=StudentEvalResultsHelper.escapeForHTML(sub.details.revieweeName)%></td>
							<td><%=StudentEvalResultsHelper.colorizePoint(sub.points)%></td> 
							<td><%=StudentEvalResultsHelper.escapeForHTML(sub.justification.getValue())%></td>
							<td><%=StudentEvalResultsHelper.formatP2PFeedback(StudentEvalResultsHelper.escapeForHTML(sub.p2pFeedback.getValue()), helper.eval.p2pEnabled)%></td>
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