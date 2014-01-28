<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@page import="teammates.ui.controller.StudentEvalResultsPageData"%>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes" %>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="teammates.ui.controller.StudentEvalResultsPageData"%>
<%
	StudentEvalResultsPageData data = (StudentEvalResultsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>TEAMMATES - Student</title>
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
		<jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Evaluation Results</h1>
			</div>
			
			<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
			
			<div id="studentEvaluationResults">
				<div id="equalShareTag">E = Equal Share</div>
				<div class="backgroundBlock evalResultHeader">
					<span class="color_white bold">Your Result:</span>
				</div>
				
				<table class="result_studentform">
					<tr>
						<td width="15%" class="bold color_white">Evaluation:</td>
						<td colspan="2"><%=PageData.sanitizeForHtml(data.eval.name)%>
							in
							<%=data.eval.courseId%>
						</td>
					</tr>
					<tr>
						<td class="bold color_white">Student:</td>
						<td colspan="2"><%=PageData.sanitizeForHtml(data.student.name)%>
							in
							<%=PageData.sanitizeForHtml(data.student.team)%>
						</td>
					</tr>
					<tr>
						<td class="bold color_white">My View:</td>
						<td width="12%">
							Of me: <%=StudentEvalResultsPageData.getPointsAsColorizedHtml(data.evalResult.outgoing.get(0).points)%>
						</td>
						<td>
							Of others: <%=StudentEvalResultsPageData.getPointsListOriginal(data.outgoing)%>
						</td>
					</tr>
					<tr>
						<td class="bold color_white">Team's View:</td>
						<td>
							Of me: <%=StudentEvalResultsPageData.getPointsAsColorizedHtml(data.evalResult.incoming.get(0).details.normalizedToStudent)%>
						</td>
						<td>
							Of others: <%=StudentEvalResultsPageData.getNormalizedToStudentsPointsList(data.incoming)%>
						</td>
					</tr>
				</table>
				
				<table class="resultTable">
					<tr class="resultSubheader bold color_black"><td>Anonymous Feedback from Teammates:</td></tr>
					<tr>
						<td>
							<ul>
								<%
									for(SubmissionAttributes sub: data.incoming) {
								%>
									<li><%=StudentEvalResultsPageData.formatP2PFeedback(PageData.sanitizeForHtml(sub.p2pFeedback.getValue()), data.eval.p2pEnabled)%></li>
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
									for(SubmissionAttributes sub: data.selfEvaluations){
								%>
									<li><span class="bold"><%=PageData.sanitizeForHtml(sub.details.reviewerName)%>:</span> 
										<%=PageData.sanitizeForHtml(sub.justification.getValue())%></li>
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
						<td><%=StudentEvalResultsPageData.getPointsAsColorizedHtml(data.evalResult.getSelfEvaluation().points)%></td>
					</tr>
					<tr>
						<td class="bold color_white">Your contribution:</td>
						<td><%=PageData.sanitizeForHtml(data.evalResult.getSelfEvaluation().justification.getValue())%></td>
					</tr>
					<tr>
						<td class="bold color_white">Team dynamics:</td>
						<td><%=PageData.sanitizeForHtml(data.evalResult.getSelfEvaluation().p2pFeedback.getValue())%></td>
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
						for(SubmissionAttributes sub: data.outgoing){
					%>
						<tr>
							<td><%=PageData.sanitizeForHtml(sub.details.revieweeName)%></td>
							<td><%=StudentEvalResultsPageData.getPointsAsColorizedHtml(sub.points)%></td> 
							<td><%=PageData.sanitizeForHtml(sub.justification.getValue())%></td>
							<td><%=StudentEvalResultsPageData.formatP2PFeedback(PageData.sanitizeForHtml(sub.p2pFeedback.getValue()), data.eval.p2pEnabled)%></td>
						</tr>
					<%
						}
					%>
				</table>
				<br><br>
				
				<div style="line-height:120%">		
					<span class="bold">How do I interpret contribution ratings shown above?</span>
					<br>
					<ul class="bulletedList">
						<li>
							Compare values given under 'My view' to those under 'Team's view'.
							This tells you whether your perception matches with what the team thinks, 
							particularly regarding your own contribution. You may ignore minor differences.
						</li>
						<li>
							Team's view of your contribution is the average value of the contribution your 
							team members attributed to you, excluding the contribution you attributed to yourself. 
							That means you cannot boost your perceived contribution by claiming a high contribution 
							for yourself or attributing a low contribution to team members.
						</li>
						<li>
							Also note that the team’s view has been scaled up/down so that the sum of values in your 
							view matches the sum of values in team’s view. That way, you can make a direct comparison 
							between your view and the team’s view. As a result, the values you see in team’s view may 
							not match the values your team members see in their results.
						</li>
					</ul>
					<br>
					<span class="bold">How are these results used in grading?</span>
					<br>TEAMMATES does not calculate grades. It is up to the instructors to use evaluation results in any way they want. 
					However, TEAMMATES recommend that evaluation results are used only as flags to identify teams with contribution imbalances. 
					Once identified, the instructor is recommended to investigate further before taking action.
					<br><br>
					<span class="bold">How are the contribution numbers calculated?</span>
					<br>Here are the important things to note:
					<ul class="bulletedList">
						<li>
							The contribution you attributed to yourself is not used when calculating the perceived 
							contribution of you or team members.
						</li>
						<li>
							From the estimates you submit, we try to deduce the answer to this question: 
							In your opinion, if your teammates are doing the project by themselves without you, 
							how do they compare against each other in terms of contribution? This is because we 
							want to deduce your unbiased opinion about your team members’ contributions. Then, 
							we use those values to calculate the average perceived contribution of each team member.
						</li>
						<li>
							When deducing the above, we first adjust the estimates you submitted to remove artificial 
							inflations/deflations. For example, giving everyone [Equal share + 20%] is as same as giving 
							everyone [Equal share] because in both cases all members have done a similar share of work.
						</li>
					</ul>
					The actual calculation is a bit complex and the finer details can be found 
					<a href="/dev/spec.html#supplementaryrequirements-pointcalculationscheme">here</a>.
				</div>
				<br><br>
				<br><br>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>