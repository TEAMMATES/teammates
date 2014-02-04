<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractResponseDetails"%>
<%@ page import="teammates.ui.controller.FeedbackSubmissionEditPageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%
	FeedbackSubmissionEditPageData data = (FeedbackSubmissionEditPageData)request.getAttribute("data");
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
	<script type="text/javascript" src="/js/feedbackSubmissionsEdit.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
<%
	if (!data.isPreview) {
%>
		<jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />
<%	
	} else { 
%>
		<div id="frameTopWrapper">
			<h1 class="color_white centeralign">Previewing Session as Student <%=data.previewName%> (<%=data.previewEmail%>)</h1>
		</div>
<% 
	}
%>
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Submit Feedback</h1>
			</div>
			
			<form method="post" action="<%=Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_SAVE%>" name="form_student_submit_response">
				<table class="inputTable">
					<tr>
						<td class="bold">Course:</td>
						<td colspan="2"><%=sanitizeForHtml(data.bundle.feedbackSession.courseId)%></td>
					</tr>
					<tr>
						<td class="bold">Session Name:</td>
						<td colspan="3"><%=sanitizeForHtml(data.bundle.feedbackSession.feedbackSessionName)%></td>				
					</tr>
					<tr>
						<td class="bold">Open from:</td>
						<td><%=TimeHelper.formatTime(data.bundle.feedbackSession.startTime)%></td>
						<td class="bold">To:</td>
						<td><%=TimeHelper.formatTime(data.bundle.feedbackSession.endTime)%></td>
					</tr>
					<tr>
						<td class="bold middlealign">Instructions:</td>
						<td class="multiline" colspan="3"><%=sanitizeForHtml(data.bundle.feedbackSession.instructions.getValue())%></td>
					</tr>
				</table>
				<br>
				<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
				<br>
			<%
				int qnIndx = 1;
				List<FeedbackQuestionAttributes> questions = data.bundle.getSortedQuestions();
				for (FeedbackQuestionAttributes question : questions) {
					int numOfResponseBoxes = question.numberOfEntitiesToGiveFeedbackTo;
					int maxResponsesPossible = data.bundle.recipientList.get(question.getId()).size();
					FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();

					if (numOfResponseBoxes == Const.MAX_POSSIBLE_RECIPIENTS ||
							numOfResponseBoxes > maxResponsesPossible) {
						numOfResponseBoxes = maxResponsesPossible;
					}
					if (numOfResponseBoxes == 0) {
						// Don't display question if no recipients.
						continue;
					}
			%>
					<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TYPE%>-<%=Integer.toString(qnIndx)%>" value="<%=question.questionType%>"/>
					<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_ID%>-<%=Integer.toString(qnIndx)%>" value="<%=question.getId()%>"/>
					<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL%>-<%=Integer.toString(qnIndx)%>" value="<%=numOfResponseBoxes%>"/>
					<table class="inputTable responseTable">
						<tr style="border-bottom: 3px dotted white;">
							<td class="bold" colspan="2" style="white-space:pre-wrap;">Question <%=qnIndx%>:</br><%=sanitizeForHtml(questionDetails.questionText)%></td>
						</tr>
						<tr><td class="bold" colspan="2">Only the following persons can see your responses:</tr>
						<tr style="border-bottom: 3px dotted white;">
							<td colspan="2"
								onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_RESPONSE_VISIBILITY_INFO%>')"
								onmouseout="hideddrivetip()">
								<ul>
								<%
									if (question.getVisibilityMessage().isEmpty()) {
								%>
										<li>No-one but the feedback session creator can see your responses.</li>
								<%
									}
									for (String line : question.getVisibilityMessage()) {
								%>
										<li><%=line%></li>
								<%
									}
								%>
								</ul>
							</td>
						</tr>
						<tr><td class="bold centeralign" style="padding-top: 15px; padding-bottom: 0px" colspan="3">Your Response</td></tr>
					<%
						int responseIndx = 0;
						List<FeedbackResponseAttributes> existingResponses =
								data.bundle.questionResponseBundle.get(question);
						for (FeedbackResponseAttributes existingResponse : existingResponses) {
					%>
							<tr>
								<td class="middlealign nowrap" <%=(question.isRecipientNameHidden()) ? "style=\"display:none\"" : ""%>>
									<span class="label bold">To: </span> 
									<select class="participantSelect middlealign" 
										name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
										<%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none\"" : ""%>>
									<%
										for(String opt: data.getRecipientOptionsForQuestion(question.getId(), existingResponse.recipientEmail)){
											out.println(opt);
										}
									%>
									</select>
								</td>
								<td class="responseText">
									<%=questionDetails.getQuestionWithExistingResponseSubmissionFormHtml(
										data.bundle.feedbackSession.isOpened(), 
										qnIndx, responseIndx, question.courseId, 
										existingResponse.getResponseDetails())%>
									<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_ID%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>" value="<%=existingResponse.getId()%>"/>
								</td>
							</tr>
					<%
						responseIndx++;
									}
									while (responseIndx < numOfResponseBoxes) {
					%>
							<tr>
								<td class="middlealign nowrap" <%=(question.isRecipientNameHidden()) ? "style=\"display:none\"" : ""%>>
									<span class="label bold">To: </span> 
									<select class="participantSelect middlealign newResponse" 
										name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
										<%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none\"" : ""%>>
									<%
										for(String opt: data.getRecipientOptionsForQuestion(question.getId(), null)) {
																		out.println(opt);
																	}
									%>
									</select>
								</td>
								<td class="responseText">
								<%=questionDetails.getQuestionWithoutExistingResponseSubmissionFormHtml(
										data.bundle.feedbackSession.isOpened(), 
										qnIndx, responseIndx, question.courseId)%>
								</td>
							</tr>
					<%
							responseIndx++;
						}
					%>
					</table>
					<br><br>
			<%
					qnIndx++;
				}
			%>
				<div class="bold centeralign">
				<%
					if(data.bundle.questionResponseBundle.isEmpty()) {
				%>
						There are no questions for you to answer here!
				<%
					} else if (data.isPreview) {
				%>
						<input disabled="disabled" type="submit" class="button" id="response_submit_button" onmouseover="ddrivetip('You can save your responses at any time and come back later to continue.')" onmouseout="hideddrivetip()" value="Save Feedback"/>
				<%
					} else if (data.bundle.feedbackSession.isOpened()) {
				%>
						<input type="submit" class="button" id="response_submit_button" onmouseover="ddrivetip('You can save your responses at any time and come back later to continue.')" onmouseout="hideddrivetip()" value="Save Feedback"/>
						<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.bundle.feedbackSession.feedbackSessionName%>"/>
						<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.bundle.feedbackSession.courseId%>"/>
						<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
				<%
					} else {
				%>
						<%=Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN%>
				<%
					}
				%>
				</div>
				<br><br>	
			</form>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>
