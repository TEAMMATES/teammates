<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.ui.controller.FeedbackQuestionSubmissionEditPageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%
	FeedbackQuestionSubmissionEditPageData data = (FeedbackQuestionSubmissionEditPageData)request.getAttribute("data");
%>	
	<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.bundle.feedbackSession.feedbackSessionName%>"/>
	<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.bundle.feedbackSession.courseId%>"/>
	<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
	
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

	FeedbackQuestionAttributes question = data.bundle.question;
	int numOfResponseBoxes = question.numberOfEntitiesToGiveFeedbackTo;
	int maxResponsesPossible = data.bundle.recipientList.size();
	FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();

	if (numOfResponseBoxes == Const.MAX_POSSIBLE_RECIPIENTS ||
			numOfResponseBoxes > maxResponsesPossible) {
		numOfResponseBoxes = maxResponsesPossible;
	}
%>
	<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TYPE%>" value="<%=question.questionType%>"/>
	<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_ID%>" value="<%=question.getId()%>"/>
	<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL%>" value="<%=numOfResponseBoxes%>"/>
	<table class="inputTable responseTable">
		<tr style="border-bottom: 3px dotted white;">
			<td class="bold" colspan="2" style="white-space:pre-wrap;">Question <%=question.questionNumber%>:<br/><%=sanitizeForHtml(questionDetails.questionText)%></td>
		</tr>
		<tr>
			<td class="bold" colspan="2">Only the following persons can see your responses:
		</tr>
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
		<tr>
			<td class="bold centeralign" style="padding-top: 15px; padding-bottom: 0px" colspan="3">Your Response</td>
		</tr>
	<%
		int responseIndx = 0;
		List<FeedbackResponseAttributes> existingResponses = data.bundle.responseList;
		for (FeedbackResponseAttributes existingResponse : existingResponses) {
	%>
			<tr>
				<td class="middlealign nowrap" <%=(question.isRecipientNameHidden()) ? "style=\"display:none\"" : ""%>>
					<span class="label bold">To: </span> 
					<select class="participantSelect middlealign" 
						name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
						<%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none\"" : ""%>>
					<%
						for(String opt: data.getRecipientOptions(existingResponse.recipientEmail)){
							out.println(opt);
						}
					%>
					</select>
				</td>
				<td class="responseText">
					<%=questionDetails.getQuestionWithExistingResponseSubmissionFormHtml(
						data.isSessionOpenForSubmission, qnIndx, responseIndx, question.courseId, 
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
						for(String opt: data.getRecipientOptions(null)) {
																	out.println(opt);
																}
					%>
					</select>
				</td>
				<td class="responseText">
				<%=questionDetails.getQuestionWithoutExistingResponseSubmissionFormHtml(
						data.isSessionOpenForSubmission, qnIndx, responseIndx, question.courseId)%>
				</td>
			</tr>
	<%
			responseIndx++;
		}
		
		if (numOfResponseBoxes == 0) {
	%>
			<tr>
				<td class="bold">
					You are not assigned any recipients for this question.
				</td>
			</tr>
	<%	
		}
	%>
		</table>
		<br><br>
		