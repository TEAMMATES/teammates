<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page
	import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page
	import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%
	InstructorFeedbackResultsPageData data = (InstructorFeedbackResultsPageData) request
			.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
<link rel="shortcut icon" href="/favicon.png">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>TEAMMATES - Feedback Session Results</title>
<link rel="stylesheet" href="/stylesheets/common.css" type="text/css"
	media="screen">
<link rel="stylesheet" href="/stylesheets/common-print.css"
	type="text/css" media="print">
<link rel="stylesheet" href="/stylesheets/instructorFeedbacks.css"
	type="text/css" media="screen">

<script type="text/javascript" src="/js/googleAnalytics.js"></script>
<script type="text/javascript" src="/js/jquery-minified.js"></script>
<script type="text/javascript" src="/js/tooltip.js"></script>
<script type="text/javascript" src="/js/AnchorPosition.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Feedback Results - Instructor</h1>
			</div>
			<jsp:include
				page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_TOP%>" />
			<br>
			<%
				Map<String, Map<String, List<FeedbackResponseAttributes>>> allResponses = data.bundle 
						.getResponsesSortedByRecipient();

				for (Map.Entry<String, Map<String, List<FeedbackResponseAttributes>>> responsesForRecipient : allResponses
						.entrySet()) {					
			%>
			<div class="backgroundBlock">
				<h2 class="color_white">
					To: <%=responsesForRecipient.getKey()%></h2>

				<%
					for (Map.Entry<String, List<FeedbackResponseAttributes>> responsesForRecipientFromGiver : responsesForRecipient
								.getValue().entrySet()) {
				%>
				<table class="resultTable" style="width: 100%">
					<thead>
						<tr>
							<th class="leftalign"><span class="bold">From: </span><%=responsesForRecipientFromGiver.getKey()%></th>
						</tr>
					</thead>
					<%
						int qnIndx = 1;
								for (FeedbackResponseAttributes singleResponse : responsesForRecipientFromGiver
										.getValue()) {
					%>
					<tr class="resultSubheader">
						<td class="multiline"><span class="bold">Question <%=data.bundle.questions
								.get(singleResponse.feedbackQuestionId).questionNumber%>: </span><%=
										data.bundle.getQuestionText(singleResponse.feedbackQuestionId)%>
						</td>
					</tr>
					<tr>
						<td class="multiline"><span class="bold">Response: </span><%=singleResponse.getResponseDetails().getAnswerHtml()%></td>
					</tr>
					<%
						qnIndx++;
								}
								if (responsesForRecipientFromGiver.getValue().isEmpty()) {
					%>
					<tr>
						<td class="bold color_red">No feedback from this user.</td>
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
			<br>
			<br>
			<%
				}
			%>


		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>