<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%
	InstructorFeedbackResultsPageData data = (InstructorFeedbackResultsPageData)request.getAttribute("data");
%>

<table class="inputTable">
	<tr>
		<td class="bold">Course:</td>
		<td><%=InstructorFeedbackResultsPageData.sanitizeForHtml(data.bundle.feedbackSession.courseId)%></td>
		<td colspan="2" class="rightalign"><a
			href="<%=data.getInstructorFeedbackSessionEditLink(data.bundle.feedbackSession.courseId, data.bundle.feedbackSession.feedbackSessionName)%>">[Edit]</a>
		</td>
	</tr>
	<tr>
		<td class="bold">Session Name:</td>
		<td colspan="3"><%=InstructorFeedbackResultsPageData.sanitizeForHtml(data.bundle.feedbackSession.feedbackSessionName)%></td>
	</tr>
	<tr>
		<td class="bold">Open from:</td>
		<td><%=TimeHelper.formatTime(data.bundle.feedbackSession.startTime)%></td>
		<td class="bold">To:</td>
		<td><%=TimeHelper.formatTime(data.bundle.feedbackSession.endTime)%></td>
	</tr>
	<tr>
		<td class="bold">Results visible from:</td>
		<td>
			<%
				if (data.bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
								if (data.bundle.feedbackSession.sessionVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
			%>
						<%=TimeHelper.formatTime(data.bundle.feedbackSession.startTime)%>
					<%
						} else if (data.bundle.feedbackSession.sessionVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER)) {
					%>
						Never
					<%
						} else {
					%>
						<%=TimeHelper.formatTime(data.bundle.feedbackSession.sessionVisibleFromTime)%>
					<%
						}
					%>
			<%
				} else if (data.bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_LATER)) {
			%>
				I want to manually publish the results.
			<%
				} else if (data.bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER)) {
			%>
				Never
			<%
				} else {
			%>
				<%=TimeHelper.formatTime(data.bundle.feedbackSession.resultsVisibleFromTime)%>
			<%
				} 
						boolean noResponses = data.bundle.responses.isEmpty();
			%>
		</td>
	</tr>
	<tr>
		<td align="center" colspan="10">
			<form method="post"
			  		action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD%>">
			<div id="feedbackDataButtons">
			<input id="button_download" type="submit" class="button"
					name="<%=Const.ParamsNames.FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON%>"
					value="Download results">
			</div>
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.bundle.feedbackSession.feedbackSessionName%>">
			<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.bundle.feedbackSession.courseId%>">
			</form>
		</td>
	</tr>
</table>

<br><br><br>

<%
	if (noResponses == false) {
%>
<form method="post"
	action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
	<table class="inputTable sortTypeTable">
		<tr>
			<td><label><input type="radio"
				name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="giver"
				onclick="this.form.submit()"
				<%=(data.sortType!=null) ? data.sortType.equals("giver") ? "checked=\"checked\"" : "" : ""%>><span
				class="label bold"> Sort by giver (Paragraph format)</span></label></td>
			<td><label><input type="radio"
				name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="recipient"
				onclick="this.form.submit()"
				<%=(data.sortType!=null) ? data.sortType.equals("recipient") ? "checked=\"checked\"" : "" : "checked=\"checked\""%>><span
				class="label bold"> Sort by recipient (Paragraph format)</span></label></td>
			<td><label><input type="radio"
				name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="table"
				onclick="this.form.submit()"
				<%=(data.sortType!=null) ? data.sortType.equals("table") ? "checked=\"checked\"" : "" : ""%>><span
				class="label bold"> View as table</span></label></td>
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
	<div class="bold color_red centeralign">There are no responses for this feedback session yet.</div>
<% } %>