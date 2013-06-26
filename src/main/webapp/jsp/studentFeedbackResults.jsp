<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ListIterator"%>
<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.FeedbackParticipantType"%>
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
	<link rel="stylesheet" href="/stylesheets/studentFeedback.css" type="text/css" media="screen">
	
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
		<jsp:include page="<%=Common.JSP_STUDENT_HEADER%>" />
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
			<jsp:include page="<%=Common.JSP_STATUS_MESSAGE%>" />
			<br>
			<%
				int qnIndx = 1;
					Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses = 
							data.bundle.getQuestionResponseMap();
					
						for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>
										questionWithResponses : questionsWithResponses.entrySet()) {
			%>
			<table class="inputTable responseTable">
			<tr style="border-bottom: 3px solid black;"><td colspan="2"><span class="bold" >Question <%=qnIndx %>: &nbsp;</span>[<%=questionWithResponses.getKey().questionText.getValue() %>]</td></tr>
			<%
						int responseIndx = 1;
						String prevRecipient = null;
						ListIterator<FeedbackResponseAttributes> itr = questionWithResponses.getValue().listIterator();
						while (itr.hasNext()) {
							FeedbackResponseAttributes responseForQn = itr.next();
			%>
			<%
							String recipient = data.bundle.emailNameTable.get(responseForQn.recipient);
							if (data.bundle.visibilityTable.get(responseForQn.getId())[1] == false) {
								String hash = Integer.toString(Math.abs(recipient.hashCode()));
								recipient = questionWithResponses.getKey().recipientType.toString().toLowerCase();
								recipient = "Anonymous " + recipient.substring(0, recipient.length()-1) + " " + hash;
							} else if(data.student.email!=null) {
								if(questionWithResponses.getKey().recipientType ==  FeedbackParticipantType.TEAMS) {
									if(data.student.team.equals(responseForQn.recipient)) {
										recipient = "Your Team ("+ recipient +")";
									}
								} else if (data.student.email.equals(responseForQn.recipient)) {
									recipient = "You";
								}
							}
							if (recipient.equals(prevRecipient) == false) {
								if(prevRecipient != null) {
			%>
			</table>
			</td></tr>
			<% 
								}
			%>
			<tr><td>
			<table class="inputTable" style="width:90%">
			<tr><th>To: <%=recipient %></th></tr>
			<%				 
							}
			%>
			<tr><td>From: <%
					String giver = data.bundle.emailNameTable.get(responseForQn.giverEmail);
					if (data.bundle.visibilityTable.get(responseForQn.getId())[0] == false) {
						String hash = Integer.toString(Math.abs(giver.hashCode()));
						giver = questionWithResponses.getKey().giverType.toString().toLowerCase();
						giver = "Anonymous " + giver.substring(0, giver.length()-1) + " " + hash;
					} else if(data.student.email!=null) {
						if(questionWithResponses.getKey().giverType ==  FeedbackParticipantType.TEAMS) {
							if(data.student.team.equals(responseForQn.giverEmail)) {
								giver = "Your Team ("+ giver +")";
							}
						} else if (data.student.email.equals(responseForQn.giverEmail)) {
							giver = "You";
						}
					}
					%><%=giver%>
			</td></tr>
			<tr <% 
					if (itr.hasNext()) {
						if(itr.next().recipient.equals(responseForQn.recipient)) {
				%>style="border-bottom: dotted 1px grey" 
				<% 
						}
						itr.previous();
					}
				%>>
				<td colspan="2" style="width:110px"><%=responseForQn.answer.getValue() %></td>
			</tr>
			<%
						prevRecipient = recipient;
						responseIndx++;
						}
			%>			
			</td></tr>
			</table>
			</table>
			<br>
			<%
					qnIndx++;
					}
			%>
			
			
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Common.JSP_FOOTER%>" />
	</div>
</body>
</html>