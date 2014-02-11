<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@page import="teammates.ui.controller.InstructorFeedbacksPageData"%>
<%@ page import="java.util.Date"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="teammates.common.datatransfer.InstructorAttributes"%>
<%@ page import="teammates.logic.core.Emails.EmailType"%>
<%@ page import="teammates.ui.controller.InstructorFeedbackEditPageData"%>
<%
	InstructorFeedbackEditPageData data = (InstructorFeedbackEditPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>TEAMMATES - Instructor</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/instructorFeedbacks.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/instructor.js"></script>
	<script type="text/javascript" src="/js/instructorFeedbacks.js"></script>
	<script type="text/javascript" src="/js/instructorFeedbackEdit.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body onload="readyFeedbackEditPage(); initializetooltip();">
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Edit Feedback Session</h1>
			</div>
			
			<form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE%>" id="form_editfeedbacksession">
				<div class="sessionDetailsBackground">
				<table class="inputTable sessionTable" id="sessionNameTable">
					<tr>
						<td class="label bold">Course:</td>
						<td><%=InstructorFeedbackEditPageData.sanitizeForHtml(data.session.courseId)%></td>
						<td class="rightalign" colspan="2">
							<a href="#" class="color_blue pad_right" id="fsEditLink" 
							onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_EDIT%>')" onmouseout="hideddrivetip()" 
							onclick="enableEditFS()">Edit</a>
							<a href="#" class="color_green pad_right" style="display:none;" id="fsSaveLink">Save Changes</a>
							<a href="<%=data.getInstructorFeedbackSessionDeleteLink(data.session.courseId, data.session.feedbackSessionName, "")%>" 
							onclick="hideddrivetip(); return toggleDeleteFeedbackSessionConfirmation('<%=data.session.courseId%>','<%=data.session.feedbackSessionName%>');"
							onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_DELETE%>')" onmouseout="hideddrivetip()"
							class="color_red" id="fsDeleteLink">Delete</a>
						</td>
					</tr>
					<tr>
						<td style="width:200px" class="label bold">Feedback session name:</td>
						<td><%=InstructorFeedbackEditPageData.sanitizeForHtml(data.session.feedbackSessionName)%></td>
						
						<td class="rightalign"><span class="label bold" style="padding-right:10px">Time zone: </span> <select
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>"
							onmouseover="ddrivetip('<%=Const.Tooltips.EVALUATION_INPUT_TIMEZONE%>')"
							onmouseout="hideddrivetip()" tabindex="3">
								<%
									for (String opt : data.getTimeZoneOptionsAsHtml())
										out.println(opt);
								%>
						</select></td>
					</tr>
				</table>
				<br>
				<table class="inputTable sessionTable" id="timeFrameTable">
					<tr>
						<td class="label bold middlealign" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_STARTDATE%>')"
							onmouseout="hideddrivetip()">Submission<br>Opening Time:</td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_STARTDATE%>')"
							onmouseout="hideddrivetip()">
							<input style="width: 100px;" type="text"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
							onclick="cal.select(this,'<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>','dd/MM/yyyy')"
							value="<%=TimeHelper.formatDate(data.session.startTime)%>"
							readonly="readonly" tabindex="7"> @ <select
							style="width: 70px;"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTTIME%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTTIME%>" tabindex="4">
								<%
									for(String opt: data.getTimeOptionsAsHtml(data.session.startTime)) out.println(opt);
								%>
						</select></td>
						<td class="label bold middlealign" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_ENDDATE%>')"
							onmouseout="hideddrivetip()">Submission<br>Closing Time:</td>
						<td class="nowrap" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_ENDDATE%>')"
							onmouseout="hideddrivetip()">
							<input style="width: 100px;" type="text"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
							onclick="cal.select(this,'<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>','dd/MM/yyyy')"							
							value="<%=TimeHelper.formatDate(data.session.endTime)%>"
							readonly="readonly" tabindex="8"> @ <select
							style="width: 70px;"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDTIME%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDTIME%>" tabindex="4">
								<%
									for (String opt : data.getTimeOptionsAsHtml(data.session.endTime))
										out.println(opt);
								%>
						</select></td>
						<td class="label bold" onmouseover="ddrivetip('<%=Const.Tooltips.EVALUATION_INPUT_GRACEPERIOD%>')"
							onmouseout="hideddrivetip()">Grace Period:</td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.EVALUATION_INPUT_GRACEPERIOD%>')"
							onmouseout="hideddrivetip()">
							<select style="width: 75px;" name="<%=Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD%>"
								id="<%=Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD%>"
								tabindex="7">
									<%
										for(String opt: data.getGracePeriodOptionsAsHtml()) out.println(opt);
									%>
						</select></td>
					</tr>
				</table>
				<br>
				<table class="inputTable sessionTable" id="sessionViewableTable">
					<tr>
						<td class="label bold" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLELABEL%>')"
							onmouseout="hideddrivetip()">Session visible from:</td>
						<td
							onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_VISIBLEDATE%>')"
							onmouseout="hideddrivetip()"><input type="radio"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_custom"
							value="custom"
							<%if(TimeHelper.isSpecialTime(data.session.sessionVisibleFromTime) == false)
									out.print("checked=\"checked\"");%>> <input style="width: 100px;" type="text"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>"
							onclick="cal.select(this,'<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>','dd/MM/yyyy')"
							value="<%=TimeHelper.isSpecialTime(data.session.sessionVisibleFromTime) ? "" : TimeHelper.formatDate(data.session.sessionVisibleFromTime)%>"
							<%if(TimeHelper.isSpecialTime(data.session.sessionVisibleFromTime))
									out.print("disabled=\"disabled\"");%>
							readonly="readonly" tabindex="3"> @ <select
							style="width: 70px;"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>"
							tabindex="4"
							<%if(TimeHelper.isSpecialTime(data.session.sessionVisibleFromTime))
									out.print("disabled=\"disabled\"");%>>
								<%
									Date date = TimeHelper.isSpecialTime(
											data.session.sessionVisibleFromTime) ? null
											: data.session.sessionVisibleFromTime;
									for (String opt : data.getTimeOptionsAsHtml(date))
										out.println(opt);
								%>
						</select></td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLEATOPEN%>')"
							onmouseout="hideddrivetip()">
							<input type="radio" 
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_atopen" value="atopen"
							<%if(Const.TIME_REPRESENTS_FOLLOW_OPENING.equals(data.session.sessionVisibleFromTime)) 
									out.print("checked=\"checked\"");%>>
							 Submissions opening time</td>
						<td colspan="2" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLENEVER%>')"
							onmouseout="hideddrivetip()"
							<%if(data.session.isPrivateSession()) out.print("checked=\"checked\"");%>>
							<input type="radio" name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_never" value="never"
							<%if(Const.TIME_REPRESENTS_NEVER.equals(data.session.sessionVisibleFromTime)) 
									out.print("checked=\"checked\"");%>>
							 Never (This is a private session)</td>
					</tr>
					<tr id="response_visible_from_row">
						<td class="label bold" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELABEL%>')"
							onmouseout="hideddrivetip()">Responses visible from:</td>
						<td
							onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_PUBLISHDATE%>')"
							onmouseout="hideddrivetip()"><input type="radio"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_custom"
							value="custom"
							<%if(TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime) == false)
									out.print("checked=\"checked\"");%>>
							<input style="width: 100px;" type="text"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>"							
							value="<%=TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime) ? "" : TimeHelper.formatDate(data.session.resultsVisibleFromTime)%>"
							readonly="readonly" tabindex="5"
							<%if(TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime))
									out.print("disabled=\"disabled\"");%>
							onclick="cal.select(this,'<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>','dd/MM/yyyy')">
							@ <select style="width: 70px;"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>"
							tabindex="6"
							onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_PUBLISHDATE%>')"
							onmouseout="hideddrivetip()"
							<%if(TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime))
									out.print("disabled=\"disabled\"");%>>
								<%
									date = ((TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime)) ? null
											: data.session.resultsVisibleFromTime);
									for (String opt : data.getTimeOptionsAsHtml(date)){
										out.println(opt);
									}
								%>
						</select></td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLEATVISIBLE%>')"
							onmouseout="hideddrivetip()">
							<input type="radio" name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_atvisible" value="atvisible"
							<%if(data.session!=null && Const.TIME_REPRESENTS_FOLLOW_VISIBLE.equals(data.session.resultsVisibleFromTime)) 
									out.print("checked=\"checked\"");%>>
							 Immediately</td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELATER%>')"
							onmouseout="hideddrivetip()"><input type="radio" name="resultsVisibleFromButton"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_later" value="later"
							<%if(Const.TIME_REPRESENTS_LATER.equals(data.session.resultsVisibleFromTime) ||
								 Const.TIME_REPRESENTS_NOW.equals(data.session.resultsVisibleFromTime)) 
									out.print("checked=\"checked\"");%>>
							 Publish manually </td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLENEVER%>')"
							onmouseout="hideddrivetip()"><input type="radio"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_never" value="never"
							<%if(Const.TIME_REPRESENTS_NEVER.equals(data.session.resultsVisibleFromTime)) 
									out.print("checked=\"checked\"");%>>
							 Never</td>
					</tr>
				</table>
				<br>
				<table class="inputTable sessionTable" id="sessionEmailReminderTable">
					<tr>
						<td>
							<span class="bold">Send Emails For:</span>
						</td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SENDJOINEMAIL%>')"
							onmouseout="hideddrivetip()">
							<label><input type="checkbox" checked="checked" disabled="disabled" class="disabled">
								Join Reminder
							</label>
						</td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SENDOPENEMAIL%>')"
							onmouseout="hideddrivetip()">
							<label><input type="checkbox" 
								<%=data.session.isOpeningEmailEnabled ? "checked=\"checked\"" : ""%>
								name="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>"
								id="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>_open"
								value="<%=EmailType.FEEDBACK_OPENING.toString()%>">
									Session Opening Reminder
							</label>
						</td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SENDCLOSINGEMAIL%>')"
							onmouseout="hideddrivetip()">
							<label><input type="checkbox"
								<%=data.session.isClosingEmailEnabled ? "checked=\"checked\"" : ""%>
								name="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>"
								id="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>_closing"
								value="<%=EmailType.FEEDBACK_CLOSING.toString()%>">
									Session Closing Reminder
							</label>
						</td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SENDPUBLISHEDEMAIL%>')"
							onmouseout="hideddrivetip()">
							<label><input type="checkbox"
								<%=data.session.isPublishedEmailEnabled ? "checked=\"checked\"" : ""%>
								name="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>"
								id="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>_published"
								value="<%=EmailType.FEEDBACK_PUBLISHED.toString()%>">
									Results Published Announcement
							</label>
						</td>
					</tr>
				</table>
				<br>				
				<table class="inputTable" id="instructionsTable">
					<tr>
						<td class="label bold middlealign" >Instructions to students:</td>
						<td><textarea rows="4" cols="100%" class="textvalue" name="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>" id="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>"
								onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_INSTRUCTIONS%>')"
								onmouseout="hideddrivetip()" tabindex="8"><%=InstructorFeedbacksPageData.sanitizeForHtml(data.session.instructions.getValue())%></textarea>							
						</td>
					</tr>
				</table>
				<br><div class="rightalign"><input id="button_submit_edit"
							type="submit" class="button" style="display:none;"
							onclick="return checkEditFeedbackSession(this.form);"
							value="Save Changes"></div>
				</div>
				<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.session.feedbackSessionName%>">
				<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.session.courseId%>">
				<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
			</form>
			
			<br>
			<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
			<br>
			
			<%
							if (data.questions.isEmpty()) {
			%>
				<div class="centeralign bold" id="empty_message"><%=Const.StatusMessages.FEEDBACK_QUESTION_EMPTY%></div><br><br>
			<%
				}
			%>
			
			<%
							for(FeedbackQuestionAttributes question : data.questions) {
								FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();													
			%>
			<form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT%>" 
			id="form_editquestion-<%=question.questionNumber%>" name="form_editquestions" class="form_question" 
			onsubmit="tallyCheckboxes(<%=question.questionNumber%>)"
			<%=data.questionHasResponses.get(question.getId()) ? "editStatus=\"hasResponses\"" : "" %>
			>
			<table class="inputTable questionTable" id="questionTable<%=question.questionNumber%>">
			<tr>
				<td class="bold" colspan="3">Question
					<select class="questionNumber nonDestructive" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>-<%=question.questionNumber%>">
					<%
						for(int opt = 1; opt < data.questions.size()+1; opt++){
							out.println("<option value=" + opt +">" + opt + "</option>");
						}
					%>
					</select>
					<%=questionDetails.getQuestionTypeDisplayName()%>
				</td>
				<td class="rightalign">
					<a href="#" class="color_blue pad_right" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_EDITTEXT%>-<%=question.questionNumber%>"
					onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_QUESTION_EDIT%>')" onmouseout="hideddrivetip()" 
					onclick="enableEdit(<%=question.questionNumber%>,<%=data.questions.size()%>)">Edit</a>
					<a href="#" class="color_green pad_right" style="display:none"
					 id="<%=Const.ParamsNames.FEEDBACK_QUESTION_SAVECHANGESTEXT%>-<%=question.questionNumber%>">Save Changes</a>
					<a href="#" class="color_red" onclick="deleteQuestion(<%=question.questionNumber%>)"
					onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_QUESTION_DELETE%>')" onmouseout="hideddrivetip()">Delete</a>
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<textarea rows="5" style="width:100%"
						class="textvalue nonDestructive" 
						name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TEXT%>"
						id="<%=Const.ParamsNames.FEEDBACK_QUESTION_TEXT%>-<%=question.questionNumber%>"
						onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_QUESTION_INPUT_INSTRUCTIONS%>')"
						onmouseout="hideddrivetip()" tabindex="9"
						disabled="disabled"><%=InstructorFeedbackEditPageData.sanitizeForHtml(questionDetails.questionText)%></textarea>
				</td>
			</tr>
			<%=questionDetails.getQuestionSpecificEditFormHtml(question.questionNumber)%>		
			<tr>
					<td class="bold" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_GIVER%>')" onmouseout="hideddrivetip()">Feedback Giver:</td>
					<td><select class="participantSelect" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE%>-<%=question.questionNumber%>" disabled="disabled"
								onchange="feedbackGiverUpdateVisibilityOptions(this)">
						<%
							for(String opt: data.getParticipantOptions(question, true)) out.println(opt);
						%>
					</select></td>
					<td class="bold nowrap" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RECIPIENT%>')" onmouseout="hideddrivetip()">
					Feedback Recipient:
					</td>
					<td><select class="participantSelect" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>-<%=question.questionNumber%>" 
						disabled="disabled" onchange="feedbackRecipientUpdateVisibilityOptions(this)">
						<%
							for(String opt: data.getParticipantOptions(question, false)) out.println(opt);
						%>
					</select></td>
				</tr>
				<tr>
					<td class="bold nowrap"><a class="visibilityOptionsLabel color_brown" href="#" onclick="toggleVisibilityOptions(this)">[+] Show Visibility Options</a></td>
					<td></td>
					<td class="numberOfEntitiesElements<%=question.questionNumber%>"><span id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>_text-<%=question.questionNumber%>" class="bold">The maximum number of <span id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>_text_inner-<%=question.questionNumber%>" class="bold"></span> each<br>respondant should give feedback to:</span></td>
					<td class="numberOfEntitiesElements<%=question.questionNumber%>">
					<input class="nonDestructive" type="radio" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE%>" <%=question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS ? "" : "checked=\"checked\""%> value="custom" disabled="disabled"> 
					<input class="nonDestructive numberOfEntitiesBox" type="number" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>-<%=question.questionNumber%>"  min="1" max="250" value=<%=question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS ? 1 : question.numberOfEntitiesToGiveFeedbackTo%> disabled="disabled"> 
					<input class="nonDestructive" type="radio" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE%>" <%=question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS ? "checked=\"checked\"" : ""%> value="max" disabled="disabled"> 
					<span class="label">Unlimited</span>
					</td>
				</tr>
				<tr class="visibilityOptions">
					<td colspan="4">
						<table class="dataTable participantTable">
							<tr>
								<th class="color_white bold">User/Group</th>
								<th class="color_white bold">Can see answer</th>
								<th class="color_white bold">Can see giver's name</th>
								<th class="color_white bold">Can see recipient's name</th>
							</tr>
							<tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT%>')"
							onmouseout="hideddrivetip()">
								<td>Recipient(s)</td>
								<td><input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" name="receiverLeaderCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" disabled="disabled"
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER)) {%> checked="checked" <%}%>/></td>
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER)) {%> checked="checked" <%}%>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" name="receiverFollowerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER)) {%> checked="checked" <%}%>/></td>
							</tr>
							<tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_GIVER_TEAM_MEMBERS%>')"
							onmouseout="hideddrivetip()">
								<td>Giver's Team Members</td>
								<td><input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {%> checked="checked" <%}%>/></td>								
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {%> checked="checked" <%}%>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {%> checked="checked" <%}%>/></td>
							</tr>
							<tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT_TEAM_MEMBERS%>')"
							onmouseout="hideddrivetip()">
								<td>Recipient's Team Members</td>
								<td><input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {%> checked="checked" <%}%>/></td>								
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {%> checked="checked" <%}%>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {%> checked="checked" <%}%>/></td>
							</tr>
							<tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_OTHER_STUDENTS%>')"
							onmouseout="hideddrivetip()">
								<td>Other students</td>
								<td><input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>" disabled="disabled"
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.STUDENTS)) {%> checked="checked" <%}%>/></td>								
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.STUDENTS)) {%> checked="checked" <%}%>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.STUDENTS)) {%> checked="checked" <%}%>/></td>
							</tr>
							<tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_INSTRUCTORS%>')"
							onmouseout="hideddrivetip()">
								<td>Instructors</td>
								<td><input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" disabled="disabled"
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS)) {%> checked="checked" <%}%>/></td>								
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS)) {%> checked="checked" <%}%>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS)) {%> checked="checked" <%}%>/></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
				<td colspan="6" class="rightalign"><input id="button_question_submit-<%=question.questionNumber%>"
						type="submit" class="button"
						value="Save Changes" tabindex="0"
						style="display:none"></td>
				</tr>
			</table>
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.session.feedbackSessionName%>">
			<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.session.courseId%>">
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_ID%>" value="<%=question.getId()%>">
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>" value="<%=question.questionNumber%>">
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TYPE%>" value=<%=question.questionType.toString()%>>
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE%>-<%=question.questionNumber%>" value="edit">
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO%>" >
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO%>" >
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO%>" >
			<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
			</form>
			<br><br>
			<%
				}
			%>
			
			<form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD%>" name="form_addquestions" class="form_question" onsubmit="tallyCheckboxes('')" >			
			<table class="inputTable" id="addNewQuestionTable">
				<tr>
					<td class="bold">
						Question Type
						<select class="questionType" 
								name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TYPE%>"
								id="questionTypeChoice">
							<option value = "TEXT"><%=Const.FeedbackQuestionTypeNames.TEXT%></option>
							<option value = "MCQ"><%=Const.FeedbackQuestionTypeNames.MCQ%></option>
							<option value = "MSQ"><%=Const.FeedbackQuestionTypeNames.MSQ%></option>
							<option value = "NUMSCALE"><%=Const.FeedbackQuestionTypeNames.NUMSCALE%></option>
						</select>
					</td>
					<td>
						<input id="button_openframe" class="button centeralign" value="Add New Question" 
							onclick="showNewQuestionFrame(document.getElementById('questionTypeChoice').value)">
					</td>
				</tr>
			</table>
			
			<table class="inputTable questionTable" id="questionTableNew" style="display:none;">
				<tr>
					<td class="bold" colspan="3">
						Question 
						<select class="questionNumber nonDestructive" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>">
						<%
							for(int opt = 1; opt < data.questions.size()+2; opt++){
								out.println("<option value=" + opt +">" + opt + "</option>");
								
							}
						%>
						</select>
						<span id="questionTypeHeader"></span>
					</td>
					<td class="rightalign">
						<a href="#" class="color_red" 
							onclick="deleteQuestion(-1)"
							onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_QUESTION_DELETE%>')" 
							onmouseout="hideddrivetip()">
								Delete
							</a>
					</td>
				</tr>
				<tr>
					<td colspan="4"><textarea rows="5" style="width:100%"
							class="textvalue" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TEXT%>"
							id="<%=Const.ParamsNames.FEEDBACK_QUESTION_TEXT%>"
							onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_QUESTION_INPUT_INSTRUCTIONS%>')"
							onmouseout="hideddrivetip()" tabindex="9"></textarea>
					</td>
				</tr>
				<tr id="mcqForm">
					<td colspan="2">
						<table id="mcqChoiceTable">
							<tr id="mcqOptionRow-0">
								<td><input type="radio" disabled="disabled"></td>
								<td>
									<input type="text" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE%>-0"
										id="<%=Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE%>-0" class="mcqOptionTextBox">
									<a href="#" class="removeOptionLink" 
										id="mcqRemoveOptionLink" 
										onclick="removeMcqOption(0,-1)"
										tabindex="-1"> x</a>
								</td>
							</tr>
							<tr id="mcqOptionRow-1">
								<td><input type="radio" disabled="disabled"></td>
								<td>
									<input type="text" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE%>-1"
										id="<%=Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE%>-1"
										class="mcqOptionTextBox">
									<a href="#" class="removeOptionLink" 
										id="mcqRemoveOptionLink" 
										onclick="removeMcqOption(1,-1)"
										tabindex="-1"> x</a>
								</td>
							</tr>
							<tr id="mcqAddOptionRow">
								<td colspan="2">
									<a href="#" class="color_blue" id="mcqAddOptionLink"
										onclick="addMcqOption(-1)">
										+add more options
									</a>
								</td>
							</tr>
						</table>
						<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED%>" 
							id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED%>">
					</td>
					<td colspan="2" style="vertical-align:top;">
						<label><input type="checkbox" 
							id="generateOptionsCheckbox"
							onchange="toggleMcqGeneratedOptions(this,-1)">Or, generate options from the list of all </label>
						<select id="mcqGenerateForSelect" 
							onchange="changeMcqGenerateFor(-1)"
							disabled="disabled">
							<option value="<%=FeedbackParticipantType.STUDENTS.toString()%>">students</option>
							<option value="<%=FeedbackParticipantType.TEAMS.toString()%>">teams</option>
						</select>
					</td>
				</tr>
				<tr id="msqForm">
					<td colspan="2">
						<table id="msqChoiceTable">
							<tr id="msqOptionRow-0">
								<td><input type="checkbox" disabled="disabled"></td>
								<td>
									<input type="text" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE%>-0"
										id="<%=Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE%>-0" class="msqOptionTextBox">
									<a href="#" class="removeOptionLink" 
										id="msqRemoveOptionLink" 
										onclick="removeMsqOption(0,-1)"
										tabindex="-1"> x</a>
								</td>
							</tr>
							<tr id="msqOptionRow-1">
								<td><input type="checkbox" disabled="disabled"></td>
								<td>
									<input type="text" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE%>-1"
										id="<%=Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE%>-1"
										class="msqOptionTextBox">
									<a href="#" class="removeOptionLink" 
										id="msqRemoveOptionLink" 
										onclick="removeMsqOption(1,-1)"
										tabindex="-1"> x</a>
								</td>
							</tr>
							<tr id="msqAddOptionRow">
								<td colspan="2">
									<a href="#" class="color_blue" id="msqAddOptionLink"
										onclick="addMsqOption(-1)">
										+add more options
									</a>
								</td>
							</tr>
						</table>
						<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED%>" 
							id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED%>">
					</td>
					<td colspan="2" style="vertical-align:top;">
						<label><input type="checkbox" 
							id="generateOptionsCheckbox"
							onchange="toggleMsqGeneratedOptions(this,-1)">Or, generate options from the list of all </label>
						<select id="msqGenerateForSelect" 
							onchange="changeMsqGenerateFor(-1)"
							disabled="disabled">
							<option value="<%=FeedbackParticipantType.STUDENTS.toString()%>">students</option>
							<option value="<%=FeedbackParticipantType.TEAMS.toString()%>">teams</option>
						</select>
					</td>
				</tr>
				<tr id="numScaleForm">
					<td colspan="4">
						<table>
							<tr>
								<td>
									Minimum scale:
									<input type="number" class="minScaleBox" id="minScaleBox"
										name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN%>"
										value="1" onChange="updateNumScalePossibleValues(-1)">
								</td>
							</tr>
							<tr>
								<td>
									Maximum scale:
									<input type="number" class="maxScaleBox" id="maxScaleBox"
										name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX%>"
										value="5" onChange="updateNumScalePossibleValues(-1)">
								</td>
							</tr>
							<tr>
								<td>
									Increment:	
									<input type="number" class="stepBox" id="stepBox"
										name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP%>"
										value="0.5" min="0.001" step="0.001"
										onChange="updateNumScalePossibleValues(-1)">
									<span id="numScalePossibleValues">[Possible values: 1, 1.5, 2, ..., 4, 4.5, 5]</span>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="bold" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_GIVER%>')" onmouseout="hideddrivetip()">Feedback Giver:</td>
					<td><select class="participantSelect" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE%>"
								onchange="feedbackGiverUpdateVisibilityOptions(this)">
						<%
							for(String opt: data.getParticipantOptions(null, true)) out.println(opt);
						%>
					</select></td>
					<td class="bold nowrap" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RECIPIENT%>')" onmouseout="hideddrivetip()">
					Feedback Recipient:
					</td>
					<td><select class="participantSelect" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>" 
								onchange="feedbackRecipientUpdateVisibilityOptions(this)">
						<%
							for(String opt: data.getParticipantOptions(null, false)) out.println(opt);
						%>
					</select>
					</td>
				</tr>
				<tr>
					<td></td><td></td>
					<td class="numberOfEntitiesElements"><span id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>_text-" class="bold">The maximum number of <span id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>_text_inner-" class="bold"></span> <br>each respondant should give feedback to:</span></td>
					<td class="numberOfEntitiesElements">
					<input type="radio" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE%>" value="custom"> 
					<input type="number" class="numberOfEntitiesBox" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>-" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>" min="1" max="250" value="1"> 
					<input type="radio" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE%>" value="max" checked="checked"> 					
					<span class="label">Unlimited</span>
					</td>
				</tr>

				<tr>
					<td colspan="4">
						<table class="dataTable participantTable">
							<tr>
								<th class="color_white bold">User/Group</th>
								<th class="color_white bold">Can see answer</th>
								<th class="color_white bold">Can see giver's name</th>
								<th class="color_white bold">Can see recipient's name</th>
							</tr>
							<tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT%>')"
							onmouseout="hideddrivetip()">
								<td>Recipient(s)</td>
								<td><input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" name="receiverLeaderCheckbox" checked="checked"/></td>
								<td><input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" checked="checked"/></td>
								<td><input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" name="receiverFollowerCheckbox" disabled="disabled" checked="checked"/></td>
							</tr>
							<tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_GIVER_TEAM_MEMBERS%>')"
							onmouseout="hideddrivetip()">
								<td>Giver's Team Members</td>
								<td><input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>"/></td>
								<td><input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>"/></td>
								<td><input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>"/></td>
							</tr>
							<tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT_TEAM_MEMBERS%>')"
							onmouseout="hideddrivetip()">
								<td>Recipient's Team Members</td>
								<td><input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>"/></td>								
								<td><input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>"/></td>
								<td><input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>"/></td>
							</tr>
							<tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_OTHER_STUDENTS%>')"
							onmouseout="hideddrivetip()">
								<td>Other students</td>
								<td><input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>"/></td>								
								<td><input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>"/></td>
								<td><input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>"/></td>
							</tr>
							<tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_INSTRUCTORS%>')"
							onmouseout="hideddrivetip()">
								<td>Instructors</td>
								<td><input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" checked="checked"/></td>								
								<td><input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" checked="checked"/></td>
								<td><input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" checked="checked"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="centeralign" colspan="4"><input id="button_submit_add"
						type="submit" class="button" value="Save Question" tabindex="9">
					</td>					
				</tr>
			</table>
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>" value="<%=data.questions.size()+1%>">
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.session.feedbackSessionName%>">
			<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.session.courseId%>">
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO%>" >
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO%>" >
			<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO%>" >
			<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
			<input type="hidden" 
				id="<%=Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS%>" 
				name="<%=Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS%>" 
				value="<%=FeedbackParticipantType.NONE.toString()%>"> 
		</form>			
		<br><br>
		<table class="inputTable" id="questionPreviewTable">
			<tr>
				<td class="bold">
					Preview Session:
				</td>
				<td>
					<form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASSTUDENT%>" 
						name="form_previewasstudent" class="form_preview" target="_blank">			
						<select name="<%=Const.ParamsNames.PREVIEWAS%>">
							<%
								for(StudentAttributes student : data.studentList) {
							%>
									<option value="<%=student.email%>">[<%=student.team%>] <%=student.name%></option>
							<%
								}
							%>
						</select>
						<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.session.feedbackSessionName%>">
						<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.session.courseId%>">
						<input id="button_preview_student" type="submit" class="button" value="Preview as Student">
					</form>
				</td>
				<td>
					<form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR%>" 
						name="form_previewasinstructor" class="form_preview" target="_blank">			
						<select name="<%=Const.ParamsNames.PREVIEWAS%>">
						<%
							for(InstructorAttributes instructor : data.instructorList) {
						%>
								<option value="<%=instructor.email%>"><%=instructor.name%></option>
						<%
							}
						%>
						</select>
						<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.session.feedbackSessionName%>">
						<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.session.courseId%>">
						<input id="button_preview_instructor" type="submit" class="button" value="Preview as Instructor">
					</form>
				</td>
			</tr>
		</table>
		<br><br>
		</div>
	</div>
	
	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>