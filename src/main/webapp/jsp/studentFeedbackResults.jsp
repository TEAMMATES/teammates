<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ListIterator"%>
<%@ page import="teammates.common.util.Assumption"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackTextQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackMcqQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractResponseDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackTextResponseDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackMcqResponseDetails"%>
<%@ page import="teammates.ui.controller.StudentFeedbackResultsPageData"%>
<%
	StudentFeedbackResultsPageData data = (StudentFeedbackResultsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>TEAMMATES - Submit Feedback</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
	<link rel="stylesheet" href="/stylesheets/studentFeedback.css" type="text/css" media="screen">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	<script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
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
				<td><%=StudentFeedbackResultsPageData.displayDateTime(data.bundle.feedbackSession.startTime)%></td>
				<td class="bold">To:</td>
				<td><%=StudentFeedbackResultsPageData.displayDateTime(data.bundle.feedbackSession.endTime)%></td>
			</tr>
			</table>
			<br>
			<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
			<br>
			<%
				int qnIndx = 0;
				Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses = data.bundle
						.getQuestionResponseMap();

				for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionWithResponses : questionsWithResponses
						.entrySet()) {
					qnIndx++;
					
					FeedbackAbstractQuestionDetails questionDetails = questionWithResponses.getKey().getQuestionDetails();
			%>
					<div class="backgroundBlock">
						<h2 class="color_white">
							Question <%=qnIndx%>: <%=StudentFeedbackResultsPageData.sanitizeForHtml(questionDetails.questionText)%><%=
							questionDetails.getQuestionAdditionalInfoHtml(qnIndx, "")%>
						</h2>
					<%
						ListIterator<FeedbackResponseAttributes> itr = questionWithResponses.getValue().listIterator();
						String previousRecipientEmail = null;
						while(itr.hasNext()) {
							FeedbackResponseAttributes singleResponse = itr.next();
							
							// New table if previous recipient != current or is first response					
							if(previousRecipientEmail == null || previousRecipientEmail.equals(singleResponse.recipientEmail) == false) {
								previousRecipientEmail = singleResponse.recipientEmail;
					%>
								<table class="resultTable" style="width: 100%">
									<thead>
										<tr>
											<th class="leftalign">
												<span class="bold">To: </span>
											<%
												String recipientName = 
													data.bundle.getRecipientNameForResponse(questionWithResponses.getKey(), singleResponse);
																	
												if(questionWithResponses.getKey().recipientType ==  FeedbackParticipantType.TEAMS) {
													if(data.student.team.equals(singleResponse.recipientEmail)) {
														recipientName = "Your Team ("+ recipientName +")";
													}
												} else if (data.student.email.equals(singleResponse.recipientEmail) 
															&& data.student.name.equals(recipientName)) {
													recipientName = "You";
												}
												
												out.print(recipientName);
											%>
											</th>
										</tr>
									</thead>
						<%
							}
						%>
							<tr class="resultSubheader">
								<td>
									<span class="bold">
										From: 
									<%
										String giverName = 
											data.bundle.getGiverNameForResponse(questionWithResponses.getKey(), singleResponse);
										
										if(questionWithResponses.getKey().giverType ==  FeedbackParticipantType.TEAMS) {
											if(data.student.team.equals(giverName)) {
												giverName = "Your Team ("+ giverName +")";
											}
										} else if (data.student.email.equals(singleResponse.giverEmail)) {
											giverName = "You";
										}
										
										out.print(giverName);
									%>
									</span>
								</td>
							</tr>
							<tr>
								<td class="multiline"><%=singleResponse.getResponseDetails().getAnswerHtml()%></td>
							</tr>
						<%
							// Close table if going to be new recipient
							boolean closeTable = true;
							if(!itr.hasNext()) {
								closeTable = true;
							} else if (itr.next().recipientEmail.equals(singleResponse.recipientEmail)) {
								itr.previous();
								closeTable = false;
							} else {
								itr.previous();
							}
							if (closeTable) {
						%>
								</table>
								<br>
					<%
							}
						}
					%>
					</div>
					<br>
			<% 
				}
			   	if (questionsWithResponses.isEmpty()) {
			%>				
					<br><br><br>
					<div class="bold color_red centeralign">There are currently no responses for you for this feedback session.</div>
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