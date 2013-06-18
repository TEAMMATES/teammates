<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%
InstructorFeedbackResultsPageData data = (InstructorFeedbackResultsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Feedback Session Results</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
	<link rel="stylesheet" href="/stylesheets/instructorFeedback.css" type="text/css" media="screen">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body onload="">
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Common.JSP_INSTRUCTOR_HEADER_NEW%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Feedback Results - Instructor</h1>
			</div>			
			<jsp:include page="<%=Common.JSP_INSTRUCTOR_FEEDBACK_RESULTS_TOP%>" />
			<br>
			<%
				Map<String, Map<String, List<FeedbackResponseAttributes>>> allResponses = 
						data.bundle.getResponsesSortedByGiver();
				
					for (Map.Entry<String, Map<String, List<FeedbackResponseAttributes>>>
									responsesFromGiver : allResponses.entrySet()) {
			%>
			<table class="inputTable responseTable">
				<tr><th><span class ="bold">From: </span><%= data.bundle.emailNameTable.get(responsesFromGiver.getKey()) %></th></tr>
				<tr>
					<td>
				<% 			for (Map.Entry<String, List<FeedbackResponseAttributes>>
										responsesFromGiverToRecipient : responsesFromGiver.getValue().entrySet()) {
				%>			
					<table class="inputTable" style="width:80%">
						<tr><th><span class ="bold">To: </span><%= data.bundle.emailNameTable.get(responsesFromGiverToRecipient.getKey()) %></th></tr>
				<% 				
								int qnIndx = 1;
								for (FeedbackResponseAttributes	singleResponse : responsesFromGiverToRecipient.getValue()) {
				%>
						<tr><td>
							<span class="bold">Question <%=qnIndx%>: [<%=data.bundle.questions.get(singleResponse.feedbackQuestionId).questionText.getValue() %>]</span>
						</td></tr>
						<tr><td><%= singleResponse.answer.getValue()%></td></tr>
				<%				qnIndx++;
								}
								if (responsesFromGiverToRecipient.getValue().isEmpty()) {
				%>
									<tr><td class="bold color_red">No feedback from this user.</td></tr>
				<%
								}
				%>
					</table>
					<br>
				<%
							}
				%>
				</td></tr>
			</table>
			<br><br>
			<%
					}
			%>
			
			
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER_NEW %>" />
	</div>
</body>
</html>