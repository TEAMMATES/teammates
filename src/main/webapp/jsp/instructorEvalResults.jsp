<%@page import="teammates.common.datatransfer.StudentResultBundle"%>
<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.StudentData"%>
<%@ page import="teammates.common.datatransfer.TeamResultBundle"%>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes"%>
<%@ page import="teammates.ui.controller.InstructorEvalResultsHelper"%>
<%@ page import="teammates.ui.controller.InstructorEvalExportServlet"%>

<%
	InstructorEvalResultsHelper helper = (InstructorEvalResultsHelper)request.getAttribute("helper");
%>

<!DOCTYPE html>
<html>
<head>
<link rel="shortcut icon" href="/favicon.png">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Teammates - Instructor</title>
<link rel="stylesheet" href="/stylesheets/common.css" type="text/css"
	media="screen">
<link rel="stylesheet" href="/stylesheets/instructorEvalResults.css"
	type="text/css" media="screen">
<link rel="stylesheet" href="/stylesheets/common-print.css"
	type="text/css" media="print">
<link rel="stylesheet"
	href="/stylesheets/instructorEvalResults-print.css" type="text/css"
	media="print">

<script type="text/javascript" src="/js/googleAnalytics.js"></script>
<script type="text/javascript" src="/js/jquery-minified.js"></script>
<script type="text/javascript" src="/js/tooltip.js"></script>
<script type="text/javascript" src="/js/date.js"></script>
<script type="text/javascript" src="/js/CalendarPopup.js"></script>
<script type="text/javascript" src="/js/AnchorPosition.js"></script>
<script type="text/javascript" src="/js/common.js"></script>

<script type="text/javascript" src="/js/instructor.js"></script>
<script type="text/javascript" src="/js/instructorEvalResults.js"></script>
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
				<h1>Evaluation Results</h1>
			</div>

			<table class="inputTable" id="instructorEvaluationInformation">
				<tr>
					<td class="label rightalign bold" width="50%">Course ID:</td>
					<td><%=helper.evaluationResults.evaluation.course%></td>
				</tr>
				<tr>
					<td class="label rightalign bold">Evaluation name:</td>
					<td><%=InstructorEvalResultsHelper.escapeForHTML(helper.evaluationResults.evaluation.name)%></td>
				</tr>
				<tr>
					<td class="label rightalign bold">Opening time:</td>
					<td><%=Common.formatTime(helper.evaluationResults.evaluation.startTime)%></td>
				</tr>
				<tr>
					<td class="label rightalign bold">Closing time:</td>
					<td><%=Common.formatTime(helper.evaluationResults.evaluation.endTime)%></td>
				</tr>
				<tr>
					<td class="centeralign" colspan=2><span class="label bold">Report Type:</span> <input type="radio" name="radio_reporttype"
						id="radio_summary" value="instructorEvaluationSummaryTable"
						checked="checked" onclick="showReport(this.value)"> <label
						for="radio_summary">Summary</label> <input type="radio"
						name="radio_reporttype" id="radio_reviewer"
						value="instructorEvaluationDetailedReviewerTable"
						onclick="showReport(this.value)"> <label
						for="radio_reviewer">Detailed: By Reviewer</label> <input
						type="radio" name="radio_reporttype" id="radio_reviewee"
						value="instructorEvaluationDetailedRevieweeTable"
						onclick="showReport(this.value)"> <label
						for="radio_reviewee">Detailed: By Reviewee</label></td>
				</tr>
				<tr>
					<td colspan=2 class="centeralign">
						<form id="download_eval_report" method="GET"
							action="instructorEvalExport">
							<%
								if(InstructorEvalResultsHelper.getInstructorStatusForEval(helper.evaluationResults.evaluation).equals(Common.EVALUATION_STATUS_CLOSED)) {
							%>
							<input type="button" class="button" id="button_publish"
								value="Publish"
								onclick="if(togglePublishEvaluation('<%=helper.evaluationResults.evaluation.name%>')) window.location.href='<%=helper.getInstructorEvaluationPublishLink(helper.evaluationResults.evaluation.course,helper.evaluationResults.evaluation.name,false)%>';">
							<%
								} else if (InstructorEvalResultsHelper.getInstructorStatusForEval(helper.evaluationResults.evaluation).equals(Common.EVALUATION_STATUS_PUBLISHED)) {
							%>
							<input type="button" class="button" id="button_unpublish"
								value="Unpublish"
								onclick="if(toggleUnpublishEvaluation('<%=helper.evaluationResults.evaluation.name%>')) window.location.href='<%=helper.getInstructorEvaluationUnpublishLink(helper.evaluationResults.evaluation.course,helper.evaluationResults.evaluation.name,false)%>';">
							<%
								}
							%>
							<input type="hidden" name="<%=Common.PARAM_COURSE_ID%>"
								value="<%=helper.evaluationResults.evaluation.course%>">
							<input type="hidden" name="<%=Common.PARAM_EVALUATION_NAME%>"
								value="<%=InstructorEvalResultsHelper.escapeForHTML(helper.evaluationResults.evaluation.name)%>">
							<input type="submit" value="Download Report" class="button">
						</form>
					</td>
				</tr>
			</table>

			<br>
			<jsp:include page="<%=Common.JSP_STATUS_MESSAGE%>" />
			<br>

			<%
				out.flush();
			%>
			<div id="instructorEvaluationSummaryTable" class="evaluation_result">
				<div id="tablecaption">CC = Claimed Contribution; PC = Perceived Contribution; E = Equal Share</div>
				<table class="dataTable">
					<tr>
						<th class="centeralign color_white bold" width="13%"><input
							class="buttonSortAscending" type="button"
							id="button_sortteamname" onclick="toggleSort(this,1)">Team</th>
						<th class="centeralign color_white bold"><input
							class="buttonSortNone" type="button" id="button_sortname"
							onclick="toggleSort(this,2)">Student</th>
						<th class="centeralign color_white bold" width="8%"><input
							class="buttonSortNone" type="button" id="button_sortclaimed"
							onclick="toggleSort(this,3,sortByPoint)">CC</th>
						<th class="centeralign color_white bold" width="8%"><input
							class="buttonSortNone" type="button" id="button_sortperceived"
							onclick="toggleSort(this,4,sortByPoint)">PC</th>
						<th class="centeralign color_white bold" width="8%"><input
							class="buttonSortNone" type="button" id="button_sortdiff"
							onclick="toggleSort(this,5,sortByDiff)"> <span
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_DIFF%>')"
							onmouseout="hideddrivetip()">Diff</span></th>
						<th class="centeralign color_white bold" width="20%"><span
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_POINTS_RECEIVED%>')"
							onmouseout="hideddrivetip()"> Points Received</span></th>
						<th class="centeralign color_white bold no-print" width="11%">Action(s)</th>
					</tr>
					<%
						int idx = 0;
																														for(TeamResultBundle teamResultBundle: helper.evaluationResults.teamResults.values()){
																															for(StudentResultBundle studentResult: teamResultBundle.studentResults){
																																StudentData student = studentResult.student;
					%>
					<tr class="student_row" id="student<%=idx%>">
						<td><%=InstructorEvalResultsHelper.escapeForHTML(student.team)%></td>
						<td id="<%=Common.PARAM_STUDENT_NAME%>"><span
							onmouseover="ddrivetip('<%=InstructorEvalResultsHelper.escapeForJavaScript(student.comments)%>')"
							onmouseout="hideddrivetip()"> <%=student.name%>
						</span></td>
						<td><%=InstructorEvalResultsHelper.colorizePoints(studentResult.summary.claimedToInstructor)%></td>
						<td><%=InstructorEvalResultsHelper.colorizePoints(studentResult.summary.perceivedToInstructor)%></td>
						<td><%=InstructorEvalResultsHelper.printDiff(studentResult)%></td>
						<td><%=InstructorEvalResultsHelper.getPointsList(studentResult.incoming, true)%></td>
						<td class="centeralign no-print"><a class="color_black"
							name="viewEvaluationResults<%=idx%>"
							id="viewEvaluationResults<%=idx%>" target="_blank"
							href="<%=helper.getInstructorEvaluationSubmissionViewLink(helper.evaluationResults.evaluation.course, helper.evaluationResults.evaluation.name, student.email)%>"
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_SUBMISSION_VIEW_REVIEWER%>')"
							onmouseout="hideddrivetip()"> View</a> <a class="color_black"
							name="editEvaluationResults<%=idx%>"
							id="editEvaluationResults<%=idx%>" target="_blank"
							href="<%=helper.getInstructorEvaluationSubmissionEditLink(helper.evaluationResults.evaluation.course, helper.evaluationResults.evaluation.name, student.email)%>"
							onclick="return openChildWindow(this.href)"
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_SUBMISSION_EDIT%>')"
							onmouseout="hideddrivetip()">Edit</a></td>
					</tr>
					<%
						idx++;
																												}
																											}
					%>
				</table>
				<br> <br> <br>
			</div>

			<%
				out.flush();
			%>
			<%
				for(boolean byReviewer = true, repeat=true; repeat; repeat = byReviewer, byReviewer=false){
			%>
			<div
				id="instructorEvaluationDetailed<%=byReviewer ? "Reviewer" : "Reviewee"%>Table"
				class="evaluation_result" style="display: none;">
				<div>
					<h2>
						Detailed Evaluation Results - By <%=byReviewer ? "Reviewer" : "Reviewee"%></h2>
				</div>

				<%
					boolean firstTeam = true;
							for(TeamResultBundle teamResultBundle: helper.evaluationResults.teamResults.values()){
				%>
				<%
					if(firstTeam) firstTeam = false; else out.print("<br>");
				%>
				<br>
				<div class="backgroundBlock">
					<h2 class="color_white"><%=InstructorEvalResultsHelper.escapeForHTML(teamResultBundle.getTeamName())%></h2>
					<%
						boolean firstStudent = true;
									for(StudentResultBundle studentResult: teamResultBundle.studentResults){
										StudentData student = studentResult.student;
					%>
					<%
						if(firstStudent) firstStudent = false; else out.print("<br>");
					%>
					<table class="resultTable">
						<thead>
							<tr>
								<th colspan="2" width="10%" class="leftalign bold"><span
									class="resultHeader"><%=byReviewer ? "Reviewer" : "Reviewee"%>:
								</span><%=student.name%></th>
								<th class="leftalign bold"><span class="resultHeader"
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_CLAIMED%>')"
									onmouseout="hideddrivetip()"> Claimed Contributions: </span><%=InstructorEvalResultsHelper.printSharePoints(studentResult.summary.claimedToInstructor,true)%></th>
								<th class="leftalign bold"><span class="resultHeader"
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_PERCEIVED%>')"
									onmouseout="hideddrivetip()"> Perceived Contributions: </span><%=InstructorEvalResultsHelper.printSharePoints(studentResult.summary.perceivedToInstructor,true)%>
								</th>
								<th class="rightalign no-print">
									<%
										if(byReviewer){
									%> <a target="_blank" class="color_black"
									href="<%=helper.getInstructorEvaluationSubmissionEditLink(student.course, helper.evaluationResults.evaluation.name, student.email)%>"
									onclick="return openChildWindow(this.href)"> Edit</a> <%
 	}
 %>
								</th>
							</tr>
						</thead>
						<tr>
							<td colspan="5"><span class="bold">Self evaluation:</span><br>
								<%=InstructorEvalResultsHelper.printJustification(studentResult.getSelfEvaluation())%><br></td>
						</tr>
						<tr>
							<td colspan="5"><span class="bold">Comments about team:</span><br> <%=InstructorEvalResultsHelper.formatP2PFeedback(InstructorEvalResultsHelper.escapeForHTML(studentResult.getSelfEvaluation().p2pFeedback.getValue()), helper.evaluationResults.evaluation.p2pEnabled)%><br></td>
						</tr>
						<tr class="resultSubheader bold">
							<td width="15%"><%=byReviewer ? "To" : "From"%> Student</td>
							<td width="5%">Contribution</td>
							<td width="40%">Comments</td>
							<td colspan="2" width="40%">Messages</td>
						</tr>
						<%
							for(SubmissionAttributes sub: (byReviewer ? studentResult.outgoing : studentResult.incoming)){ if(sub.reviewer.equals(sub.reviewee)) continue;
						%>
						<tr>
							<td><b><%=InstructorEvalResultsHelper.escapeForHTML(byReviewer ? sub.revieweeName : sub.reviewerName)%></b></td>
							<td><%=InstructorEvalResultsHelper.printSharePoints(sub.normalizedToInstructor,false)%></td>
							<td><%=InstructorEvalResultsHelper.printJustification(sub)%></td>
							<td colspan="2"><%=InstructorEvalResultsHelper.formatP2PFeedback(InstructorEvalResultsHelper.escapeForHTML(sub.p2pFeedback.getValue()), helper.evaluationResults.evaluation.p2pEnabled)%></td>
						</tr>
						<%
							}
						%>
					</table>
					<br>
					<%
						}
					%>
				</div>
				<%
					}
				%>
				<br> <br>
				<div class="centeralign">
					<input type="button" class="button" name="button_top"
						id="button_top" value="To Top" onclick="scrollToTop()">
				</div>
				<br> <br> <br>
			</div>
			<%
				}
			%>
			<%
				out.flush();
			%>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Common.JSP_FOOTER%>" />
	</div>
	<script>
		setStatusMessage("");
	</script>
</body>
</html>