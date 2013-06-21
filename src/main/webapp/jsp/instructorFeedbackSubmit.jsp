<%@ page import="java.util.List"%>
<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.FieldValidator"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.ui.controller.StudentFeedbackSubmitPageData"%>
<%
	StudentFeedbackSubmitPageData data = (StudentFeedbackSubmitPageData)request.getAttribute("data");
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
	<script type="text/javascript" src="/js/studentFeedback.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body onload="initializetooltip(); formatRecipientLists();">
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Common.JSP_INSTRUCTOR_HEADER_NEW%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Submit Feedback</h1>
			</div>
			
			<form method="post" action="<%=Common.PAGE_STUDENT_FEEDBACK_SUBMIT_SAVE%>" name="form_student_submit_response">
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
					List<FeedbackQuestionAttributes> questions = data.bundle.getSortedQuestions();
					for (FeedbackQuestionAttributes question : questions) {
						int numOfResponseBoxes = question.numberOfEntitiesToGiveFeedbackTo;
						int maxResponsesPossible = data.bundle.recipientList.get(question.getId()).size();
						if (numOfResponseBoxes == Common.MAX_POSSIBLE_RECIPIENTS ||
								numOfResponseBoxes > maxResponsesPossible) {
							numOfResponseBoxes = maxResponsesPossible;
						}
			%>
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_QUESTION_TYPE%>-<%=Integer.toString(qnIndx)%>" value="TEXT"/>
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_QUESTION_ID%>-<%=Integer.toString(qnIndx)%>" value="<%=question.getId()%>"/>
			<table class="inputTable responseTable">
				<tr><td class="bold" colspan="2">Question <%=qnIndx%></td></tr>
				<tr style="border-bottom: dotted 3px white;"><td colspan="2"><%=question.questionText.getValue()%></td></tr>
				<tr><td class="bold" colspan="2">The visibility of your response:</tr>
				<tr style="border-bottom: 3px solid black;"><td colspan="2">
					<ul>
					<%
						if(question.getVisibilityMessage().isEmpty()) {
					%>
					<li>No-one but the feedback session creator can see your responses.</li>	
					<%
						}
						for(String line : question.getVisibilityMessage()) {
					%>
					<li><%=line%></li>
					<% } %>
					</ul>	
				</td></tr>
				<tr><td class="bold centeralign" style="padding-top: 15px; padding-bottom: 0px" colspan="3">Your Feedback</td></tr>
				<%
				int responseIndx = 0;
				List<FeedbackResponseAttributes> existingResponses = 
					data.bundle.questionResponseBundle.get(question);
				for(FeedbackResponseAttributes existingResponse : existingResponses) {
				%>
				<tr>
				<td class="middlealign nowrap"><span class="label bold">To: </span> 
				<select class="participantSelect middlealign" 
				name="<%=Common.PARAM_FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
				<%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none\"" : ""  %>>
				<%
					for(String opt: data.getRecipientOptionsForQuestion(question.getId(), existingResponse.recipient)) out.println(opt);
				%>
				</select></td>
				<td>
				<textarea rows="4" cols="100%" class="textvalue" 
				<%=data.bundle.feedbackSession.isOpened() ? "" : "disabled=\"disabled\" onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_FEEDBACK_SUBMIT_NOT_YET_OPEN+"')\" onmouseout=\"hideddrivetip()\""%>
				name="<%=Common.PARAM_FEEDBACK_RESPONSE_TEXT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"><%=existingResponse.answer.getValue()%></textarea>
				<input type="hidden" name="<%=Common.PARAM_FEEDBACK_RESPONSE_ID%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>" value="<%=existingResponse.getId()%>"/>
				</td>
				</tr>
				<%
					responseIndx++;
						}
						if (numOfResponseBoxes == 0) {
				%>
					<tr><td class="centeralign color_red bold"><br>There is nobody for you to give feedback to.</td></tr>
				<%
					}
						while(responseIndx < numOfResponseBoxes) {
				%>
				<tr>
				<td class="middlealign nowrap"><span class="label bold">To: </span> 
				<select class="participantSelect middlealign newResponse" 
				name="<%=Common.PARAM_FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
				<%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none\"" : ""%>>
				<%
					for(String opt: data.getRecipientOptionsForQuestion(question.getId(), null)) out.println(opt);
				%>
				</select></td>
				<td class="responseText"><textarea rows="4" class="textvalue" name="<%=Common.PARAM_FEEDBACK_RESPONSE_TEXT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"></textarea></td>
				</tr>
				<%
					responseIndx++;
						}
				%>
			</table>
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_QUESTION_RESPONSETOTAL%>-<%=Integer.toString(qnIndx)%>" value="<%=numOfResponseBoxes%>"/>
			<br><br>
			<%
				qnIndx++;
					}
			%>
			<div class="bold centeralign">
			<%
				if (data.bundle.questionResponseBundle.isEmpty()) {
			%>
			There are no questions for you to answer here!
			<%
				} else if (data.bundle.feedbackSession.isOpened()) {
			%>
			<input type="submit" class="button" onclick="reenableFieldsForSubmission()" value="Submit Feedback"/>
			<%
				} else {
			%>
			<%=Common.HOVER_MESSAGE_FEEDBACK_SUBMIT_NOT_YET_OPEN%>
			<%
			   }
			%>
			</div>
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_SESSION_NAME%>" value="<%=data.bundle.feedbackSession.feedbackSessionName%>"/>
			<input type="hidden" name="<%=Common.PARAM_COURSE_ID%>" value="<%=data.bundle.feedbackSession.courseId%>"/>
			<br><br>	
			</form>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER_NEW %>" />
	</div>
</body>
</html>
