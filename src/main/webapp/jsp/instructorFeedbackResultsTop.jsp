<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%
	InstructorFeedbackResultsPageData data = (InstructorFeedbackResultsPageData)request.getAttribute("data");
%>

<table class="inputTable">
	<tr>
		<td class="bold">Course:</td>
		<td><%=data.bundle.feedbackSession.courseId%></td>
		<td colspan="2" class="rightalign"><a
			href="<%=data.getInstructorFeedbackSessionEditLink(data.bundle.feedbackSession.courseId, data.bundle.feedbackSession.feedbackSessionName)%>">[Edit]</a>
		</td>
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
	<tr>
		<td class="bold">Results visible from:</td>
		<td>
			<%
				if (data.bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
								if (data.bundle.feedbackSession.sessionVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
			%>
						<%=data.bundle.feedbackSession.startTime.toString()%>
					<%
						} else if (data.bundle.feedbackSession.sessionVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER)) {
					%>
						Never.
					<%
						} else {
					%>
						<%=data.bundle.feedbackSession.sessionVisibleFromTime.toString()%>
					<%
						}
					%>
			<%
				} else if (data.bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_LATER)) {
			%>
				I'll make it visible later.
			<%
				} else if (data.bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER)) {
			%>
				Never.
			<%
				} else {
			%>
				<%=data.bundle.feedbackSession.resultsVisibleFromTime.toString()%>
			<%
				} 
						boolean noResponses = data.bundle.responses.isEmpty();
			%>
		</td>
	</tr>
</table>
<br><br>
<form method="post"
	action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD%>">
	<div id="feedbackDataButtons" style="float:right; padding-right: <%=noResponses ? "40%;" : "5%;"%>">
		<input id="button_download" type="submit" class="button"
			name="<%=Const.ParamsNames.FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON%>"
			value="Download"> <input id="button_upload" type="submit"
			class="button"
			name="<%=Const.ParamsNames.FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON%>"
			value="Upload more data">
	</div>
</form>
<br>
<br>
<br>
<%
	if (noResponses == false) {
%>
<form method="post"
	action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS%>">
	<table class="inputTable sortTypeTable">
		<tr>
			<td><input type="radio"
				name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="giver"
				onclick="this.form.submit()"
				<%=(data.sortType!=null) ? data.sortType.equals("giver") ? "checked=\"checked\"" : "" : ""%>><span
				class="label bold"> Sort by giver (Paragraph format)</span></td>
			<td><input type="radio"
				name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="recipient"
				onclick="this.form.submit()"
				<%=(data.sortType!=null) ? data.sortType.equals("recipient") ? "checked=\"checked\"" : "" : "checked=\"checked\""%>><span
				class="label bold"> Sort by recipient (Paragraph format)</span></td>
			<td><input type="radio"
				name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="table"
				onclick="this.form.submit()"
				<%=(data.sortType!=null) ? data.sortType.equals("table") ? "checked=\"checked\"" : "" : ""%>><span
				class="label bold"> View as table</span></td>
		</tr>
	</table>
	<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
		value="<%=data.bundle.feedbackSession.feedbackSessionName%>">
	<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>"
		value="<%=data.bundle.feedbackSession.courseId%>">
	<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" 
		value="<%=data.account.googleId%>">
</form>
<%
	}
%>
<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />

<% if (noResponses) { %>
	<br><br><br>
	<div class="bold color_red centeralign">There are no responses for this feedback session yet.</div>
<% } %>