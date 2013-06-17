<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.FieldValidator"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.ui.controller.StudentFeedbackResultsPageData"%>
<%
StudentFeedbackResultsPageData data = (StudentFeedbackResultsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Submit Feedback</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
	<link rel="stylesheet" href="/stylesheets/studentFeedback.css" type="text/css" media="print">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	<script type="text/javascript" src="/js/studentFeedback.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body onload="formatRecipientLists()">
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Common.JSP_STUDENT_HEADER_NEW%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Feedback Results - Student</h1>
			</div>
			
			<table class="inputTable">
			<tr>
				<td class="bold">Course:</td>
				<td colspan="2"><%=data.bundle.feedbackSession.courseId%></td>
			</tr>
			<tr>
				<td class="bold">Session Name:</td>
				<td colspan="3"><%=data.bundle.feedbackSession.feedbackSessionName%></td>				
			</tr>
			<tr>
				<td class="bold">Open from:</td>
				<td><%=data.bundle.feedbackSession.startTime.toString()%></td>
				<td class="bold">To:</td>
				<td><%=data.bundle.feedbackSession.endTime.toString()%></td>
			</tr>
			</table>
			<br>
			<jsp:include page="<%=Common.JSP_STATUS_MESSAGE_NEW%>" />
			<br>
			<%
				int qnIndx = 1;
				Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses = 
						data.bundle.getQuestionResponseMapForStudent();
				
					for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>
									questionWithResponses : questionsWithResponses.entrySet()) {
			%>
			<table class="inputTable responseTable">
			<tr><td>Question <%=qnIndx %></td></tr>
			<%
						for (FeedbackResponseAttributes responseForQn : questionWithResponses.getValue()) {
			%>
			<tr>
				<td><%=responseForQn.recipient %></td>
				<td><%=responseForQn.giverEmail %></td>
				<td><%=responseForQn.answer.getValue() %></td>
			</tr>
			<%
						}
			%>
			</table>
			<br>
			<%
					qnIndx++;
					}
			%>
			
			
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER_NEW %>" />
	</div>
</body>
</html>