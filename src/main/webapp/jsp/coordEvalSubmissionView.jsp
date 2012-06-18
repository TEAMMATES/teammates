<%@ page import="teammates.api.Common"%>
<%@ page import="teammates.datatransfer.CourseData"%>
<%@ page import="teammates.datatransfer.EvaluationData"%>
<%@ page import="teammates.datatransfer.SubmissionData"%>
<%@ page import="teammates.jsp.CoordEvalSubmissionViewHelper"%>
<%	CoordEvalSubmissionViewHelper helper = (CoordEvalSubmissionViewHelper)request.getAttribute("helper"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Coordinator</title>
	<link rel=stylesheet href="/stylesheets/main.css" type="text/css" />
	<link rel=stylesheet href="/stylesheets/evaluation.css" type="text/css" />
	
	<script language="JavaScript" src="/js/jquery-1.6.2.min.js"></script>
	<script language="JavaScript" src="/js/tooltip.js"></script>
	<script language="JavaScript" src="/js/date.js"></script>
	<script language="JavaScript" src="/js/CalendarPopup.js"></script>
	<script language="JavaScript" src="/js/AnchorPosition.js"></script>
	<script language="JavaScript" src="/js/helperNew.js"></script>
	<script language="JavaScript" src="/js/commonNew.js"></script>
	
	<script language="JavaScript" src="/js/coordinatorNew.js"></script>
	<script language="JavaScript" src="/js/coordEval.js"></script>

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
				<h1>View Student's Result</h1>
				<table class="evaluation_info">
					<tr>
						<td>Course ID:</td>
						<td><%= helper.evaluation.course %></td>
					</tr>
					<tr>
						<td>Evaluation Name:</td>
						<td><%= CoordEvalSubmissionViewHelper.escapeHTML(helper.evaluation.name) %></td>
					</tr>
				</table>
			</div>
			<div id="studentEvaluationSubmissions">
				<table class="result_table">
					<thead><tr>
						<th colspan="2" width="10%">
							<span class="fontcolor"><%= helper.byReviewer ? "Reviewer" : "Reviewee" %>: </span><%= helper.student.name %></th>
						<th><span class="fontcolor"
								onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_CLAIMED %>')"
								onmouseout="hideddrivetip()">
							Claimed Contributions: </span><%= CoordEvalSubmissionViewHelper.printSharePoints(helper.result.claimedToCoord,true) %></th>
						<th><span class="fontcolor"
								onmouseover="ddrivetip('<%= Common.HOVER_MESSAGE_PERCEIVED %>')"
								onmouseout="hideddrivetip()">
							Perceived Contributions: </span><%= CoordEvalSubmissionViewHelper.printSharePoints(helper.result.perceivedToCoord,true) %></th>
					</tr></thead>
					<tr>
						<td colspan="4"><b>Self evaluation:</b><br />
								<%= CoordEvalSubmissionViewHelper.printJustification(helper.result.getSelfEvaluation()) %></td>
						</tr>
						<tr>
							<td colspan="4"><b>Comments about team:</b><br />
								<%= CoordEvalSubmissionViewHelper.printComments(helper.result.getSelfEvaluation(), helper.evaluation.p2pEnabled) %></td>
						</tr>
					<tr class="result_subheader">
						<td width="15%"><%= helper.byReviewer ? "To" : "From" %> Student</td>
						<td width="5%">Contribution</td>
						<td width="40%">Comments</td>
						<td width="40%">Messages</td>
					</tr>
					<%	for(SubmissionData sub: (helper.byReviewer ? helper.result.outgoing : helper.result.incoming)){ if(sub.reviewer.equals(sub.reviewee)) continue; %>
						<tr>
							<td><b><%= CoordEvalSubmissionViewHelper.escapeHTML(helper.byReviewer ? sub.revieweeName : sub.reviewerName) %></b></td>
							<td><%= CoordEvalSubmissionViewHelper.printSharePoints(sub.normalized,false) %></td>
							<td><%= CoordEvalSubmissionViewHelper.printJustification(sub) %></td>
							<td><%= CoordEvalSubmissionViewHelper.printComments(sub, helper.evaluation.p2pEnabled) %></td>
						</tr>
					<%	} %>
				</table>
				<br /><br />
				<input type="button" class="button" id="button_back" value="Close"
						onclick="window.close()"/>
				<% if(helper.byReviewer){ %>
					<input type="button" class="button" id="edit_evaluation" value="Edit Submission"
							onclick="window.location.href='<%= helper.getCoordEvaluationSubmissionEditLink(helper.evaluation.course, helper.evaluation.name, helper.student.email) %>'"/>
				<% } %>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>