<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.datatransfer.CourseData"%>
<%@ page import="teammates.common.datatransfer.EvaluationData"%>
<%@ page import="teammates.common.datatransfer.SubmissionData"%>
<%@ page import="teammates.ui.controller.CoordEvalSubmissionViewHelper"%>
<%	CoordEvalSubmissionViewHelper helper = (CoordEvalSubmissionViewHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Coordinator</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css">
	<link rel="stylesheet" href="/stylesheets/coordEvalSubmissionView.css" type="text/css">
	
	<script type="text/javascript" src="/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/coordinator.js"></script>

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
				<h1>View Student's Evaluation</h1>
				<table class="inputTable" id="studentEvaluationInfo">
					<tr>
						<td class="label rightalign" width="30%">Course ID:</td>
						<td class="leftalign"><%= helper.evaluation.course %></td>
					</tr>
					<tr>
						<td class="label rightalign" width="30%">Evaluation Name:</td>
						<td class="leftalign"><%=CoordEvalSubmissionViewHelper.escapeForHTML(helper.evaluation.name)%></td>
					</tr>
				</table>
			</div>
			<div id="studentEvaluationSubmissions">
			<%
				for(boolean byReviewee = true, repeat=true; repeat; repeat = byReviewee, byReviewee=false){
			%>
				<h2 style="text-align: center;"><%=CoordEvalSubmissionViewHelper.escapeForHTML(helper.student.name) + (byReviewee ? "'s Result" : "'s Submission")%></h2>
				<table class="resultTable">
					<thead><tr>
						<th colspan="2" width="10%">
							<span class="resultHeader"><%=byReviewee ? "Reviewee" : "Reviewer"%>: </span><%=helper.student.name%></th>
						<th><span class="resultHeader"
								onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_CLAIMED%>')"
								onmouseout="hideddrivetip()">
							Claimed Contributions: </span><%=CoordEvalSubmissionViewHelper.printSharePoints(helper.result.claimedToCoord,true)%></th>
						<th><span class="resultHeader"
								onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_PERCEIVED%>')"
								onmouseout="hideddrivetip()">
							Perceived Contributions: </span><%=CoordEvalSubmissionViewHelper.printSharePoints(helper.result.perceivedToCoord,true)%></th>
					</tr></thead>
					<tr>
						<td colspan="4"><span class="color_neutral">Self evaluation:</span><br>
								<%=CoordEvalSubmissionViewHelper.printJustification(helper.result.getSelfEvaluation())%></td>
						</tr>
						<tr>
							<td colspan="4"><span class="color_neutral">Comments about team:</span><br>
								<%=CoordEvalSubmissionViewHelper.printComments(helper.result.getSelfEvaluation(), helper.evaluation.p2pEnabled)%></td>
						</tr>
					<tr class="resultSubheader">
						<td width="15%"><%=byReviewee ? "From" : "To"%> Student</td>
						<td width="5%">Contribution</td>
						<td width="40%">Comments</td>
						<td width="40%">Messages</td>
					</tr>
					<%
						for(SubmissionData sub: (byReviewee ? helper.result.incoming : helper.result.outgoing)){ if(sub.reviewer.equals(sub.reviewee)) continue;
					%>
						<tr>
							<td><b><%=CoordEvalSubmissionViewHelper.escapeForHTML(byReviewee ? sub.reviewerName : sub.revieweeName)%></b></td>
							<td><%= CoordEvalSubmissionViewHelper.printSharePoints(sub.normalizedToCoord,false) %></td>
							<td><%= CoordEvalSubmissionViewHelper.printJustification(sub) %></td>
							<td><%= CoordEvalSubmissionViewHelper.printComments(sub, helper.evaluation.p2pEnabled) %></td>
						</tr>
					<%	} %>
				</table>
				<br><br>
				<% } %>
				<div class="centeralign">
					<input type="button" class="button" id="button_back" value="Close"
							onclick="window.close()">
					<input type="button" class="button" id="button_edit" value="Edit Submission"
							onclick="window.location.href='<%= helper.getCoordEvaluationSubmissionEditLink(helper.evaluation.course, helper.evaluation.name, helper.student.email) %>'">
				</div>
				<br><br>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>