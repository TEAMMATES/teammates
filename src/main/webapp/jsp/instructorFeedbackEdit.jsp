<%@ page import="java.util.Date"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.ui.controller.InstructorFeedbackEditPageData"%>
<%
	InstructorFeedbackEditPageData data = (InstructorFeedbackEditPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Instructor</title>
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
				<table class="inputTable sessionTable" id="sessionNameTable">
					<tr>
						<td class="label bold">Course:</td>
						<td><%=data.session.courseId%></td>
						<td></td>
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
						<td class="label bold" >Feedback session name:</td>
						<td><%=data.session.feedbackSessionName%></td>
						<td></td>
						<td class="label bold">Time zone: </td>
						<td><select name="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>" id="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>"
									onmouseover="ddrivetip('<%=Const.Tooltips.EVALUATION_INPUT_TIMEZONE%>')"
									onmouseout="hideddrivetip()" tabindex="3"
									disabled="disabled">
										<%
											for(String opt: data.getTimeZoneOptionsAsHtml()) out.println(opt);
										%>
						</select></td>
					</tr>
					<tr>
						<td class="label bold" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLELABEL%>')"
							onmouseout="hideddrivetip()">Session visible<br> from:</td>
						<td class="nowrap"
							onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_VISIBLEDATE%>')"
							onmouseout="hideddrivetip()"><input type="radio" name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_custom" value="custom"
							<%if(TimeHelper.isSpecialTime(data.session.sessionVisibleFromTime) == false)  
									out.print("checked=\"checked\"");%>
							onclick="document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>').disabled='';
							document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>').disabled=''">
							<input style="width: 100px;" type="text"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>"
							onclick="cal.select(this,'<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>','dd/MM/yyyy')"
							value="<%=TimeHelper.isSpecialTime(data.session.sessionVisibleFromTime) ? "" : TimeHelper.formatDate(data.session.sessionVisibleFromTime)%>"
							readonly="readonly" tabindex="3"
							<%if(TimeHelper.isSpecialTime(data.session.sessionVisibleFromTime) == true)  
									out.print("disabled=\"disabled\"");%>							
							> @ <select
							style="width: 70px;"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>" tabindex="4"
							<%Date date = null;
								if(TimeHelper.isSpecialTime(data.session.sessionVisibleFromTime) == true) {  
									out.print("disabled=\"disabled\"");
									date = null;
								} else {
									date = data.session.sessionVisibleFromTime; 
								}%>		
							>
								<%
									for(String opt: data.getTimeOptionsAsHtml(date)) out.println(opt);
								%>
						</select></td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLEATOPEN%>')"
							onmouseout="hideddrivetip()" class="nowrap">
							<input type="radio" name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_atopen" value="atopen"
							<%if(data.session.sessionVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) 
									out.print("checked=\"checked\"");%>							
							onclick="document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>').disabled='disabled';
							document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>').disabled='disabled';">
							 At submission opening time</td>
						<td colspan="2" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLENEVER%>')"
							onmouseout="hideddrivetip()"><input type="radio" name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_never" value="never"
							<%if(data.session.sessionVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER)) 
									out.print("checked=\"checked\"");%>
							onclick="document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>').disabled='disabled'
							document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>').disabled='disabled';">
							 Never</td>
					</tr>
					<tr>
						<td class="label bold" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELABEL%>')"
							onmouseout="hideddrivetip()">Responses visible from:</td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_PUBLISHDATE%>')"
							onmouseout="hideddrivetip()"><input type="radio" name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_custom" value="custom"
							<%if(TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime) == false) 
									out.print("checked=\"checked\"");%>
							onclick="document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>').disabled=''
							document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>').disabled=''"> 
							<input style="width: 100px;" type="text"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>"
							onclick="if(document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_custom').checked){cal.select(this,'<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>','dd/MM/yyyy');}
							else{return false;}"
							value="<%=TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime) ? "" : TimeHelper.formatDate(data.session.resultsVisibleFromTime)%>"
							readonly="readonly" tabindex="5"
							<%if(TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime) == true)  
									out.print("disabled=\"disabled\"");%>
							> @ <select
							style="width: 70px;"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>" tabindex="6"
							<%if(TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime) == true) {
									out.print("disabled=\"disabled\"");
									date = null;
								} else {
									date = data.session.resultsVisibleFromTime; 
								}%>
							>
								<%
									for(String opt: data.getTimeOptionsAsHtml(date)) out.println(opt);
								%>
						</select></td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLEATVISIBLE%>')"
							onmouseout="hideddrivetip()"><input type="radio" name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_atvisible" value="atvisible"
							<%if(data.session.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) 
									out.print("checked=\"checked\"");%>
							onclick="document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>').disabled='disabled';
							document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>').disabled='disabled'">
							 Once the session is visible</td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELATER%>')"
							onmouseout="hideddrivetip()"><input type="radio" name="resultsVisibleFromButton"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_later" value="later"
							<%if(data.session.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_LATER)) 
									out.print("checked=\"checked\"");%>
							onclick="document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>').disabled='disabled';
							document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>').disabled='disabled'">
							 Decide later </td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLENEVER%>')"
							onmouseout="hideddrivetip()"><input type="radio"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_never" value="never"
							<%if(data.session.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER)) 
									out.print("checked=\"checked\"");%>
							onclick="document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>').disabled='disabled'
							document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>').disabled='disabled'">
							 Never</td>
					</tr>
					<tr>
						<td class="label bold">Opening time:</td>
						<td class="nowrap" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_STARTDATE%>')"
							onmouseout="hideddrivetip()"><input style="width: 100px;" type="text"
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
						<td class="nowrap" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_ENDDATE%>')"
							onmouseout="hideddrivetip()">
							<span class="label bold">Closing Time: </span> 
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
															for(String opt: data.getTimeOptionsAsHtml(data.session.startTime)) out.println(opt);
														%>
						</select></td>
						<td class="label bold rightalign nowrap" onmouseover="ddrivetip('<%=Const.Tooltips.EVALUATION_INPUT_GRACEPERIOD%>')"
							onmouseout="hideddrivetip()">Grace Period:</td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.EVALUATION_INPUT_GRACEPERIOD%>')"
							onmouseout="hideddrivetip()">
							<select style="width: 75px;" name="<%=Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD%>"
								id="<%=Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD%>" tabindex="7">
									<%
										for(String opt: data.getGracePeriodOptionsAsHtml()) out.println(opt);
									%>
						</select></td>
					</tr>
					<tr>
						<td class="label bold" >Instructions to students:</td>
						<td colspan="4" style="padding-right:15px;">
							<textarea rows="4" style="width:100%;" class="textvalue" name="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>" id="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>"
								onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_INSTRUCTIONS%>')"
								onmouseout="hideddrivetip()" tabindex="8"><%=InstructorFeedbackEditPageData.sanitizeForHtml(data.session.instructions.getValue())%></textarea>
							
						</td>
					</tr>
					<tr>
						<td colspan="5" class="rightalign"><input id="button_submit_edit"
							type="submit" class="button" style="display:none;"
							value="Save Changes"></td>
					</tr>
				</table>
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
						%>
			<form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT%>" id="form_editquestion-<%=question.questionNumber%>" name="form_editquestions" class="form_question" onsubmit="tallyCheckboxes(<%=question.questionNumber%>)">
			<table class="inputTable questionTable" id="questionTable<%=question.questionNumber%>">
			<tr>
				<td class="bold">Question <%=question.questionNumber%></td>
				<td></td>
				<td></td>
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
					<td colspan="4"><textarea rows="5" style="width:100%"
							class="textvalue" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TEXT%>"
							id="<%=Const.ParamsNames.FEEDBACK_QUESTION_TEXT%>-<%=question.questionNumber%>"
							onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_QUESTION_INPUT_INSTRUCTIONS%>')"
							onmouseout="hideddrivetip()" tabindex="9"
							disabled="disabled"><%=question.questionText.getValue()%></textarea>
					</td>
				</tr>
				<tr>
					<td class="bold">Feedback Giver:</td>
					<td><select class="participantSelect" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE%>-<%=question.questionNumber%>" disabled="disabled">
						<%
							for(String opt: data.getParticipantOptions(question, true)) out.println(opt);
						%>
					</select></td>
					<td class="bold nowrap">Feedback Recipient:</td>
					<td><select class="participantSelect" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>-<%=question.questionNumber%>" disabled="disabled">
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
					<input type="radio" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE%>" <%=question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS ? "" : "checked=\"checked\""%> value="custom" disabled="disabled"> 
					<input type="number" class="numberOfEntitiesBox" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>-<%=question.questionNumber%>"  min="1" max="250" value=<%=question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS ? 1 : question.numberOfEntitiesToGiveFeedbackTo%> disabled="disabled"> 
					<input type="radio" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE%>" <%=question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS ? "checked=\"checked\"" : ""%> value="max" disabled="disabled"> 
					<span class="label">Unlimited</span>
					</td>
				</tr>
				<tr class="visibilityOptions">
					<td colspan="4">
						<table class="dataTable participantTable">
							<tr>
								<th class="bold">User/Group</th>
								<th>Show answer to</th>
								<th>Show giver name to</th>
								<th>Show recipient name to</th>
							</tr>
							<tr>
								<td>Recipient(s)</td>
								<td><input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" name="receiverLeaderCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" disabled="disabled"
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER)) {%> checked="checked" <%}%>/></td>
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER)) {%> checked="checked" <%}%>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" name="receiverFollowerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER)) {%> checked="checked" <%}%>/></td>
							</tr>
							<tr>
								<td>Giver's Team Members</td>
								<td><input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {%> checked="checked" <%}%>/></td>								
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {%> checked="checked" <%}%>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {%> checked="checked" <%}%>/></td>
							</tr>
							<tr>
								<td>Recipient's Team Members</td>
								<td><input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {%> checked="checked" <%}%>/></td>								
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {%> checked="checked" <%}%>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {%> checked="checked" <%}%>/></td>
							</tr>
							<tr>
								<td>Other students</td>
								<td><input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>" disabled="disabled"
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.STUDENTS)) {%> checked="checked" <%}%>/></td>								
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.STUDENTS)) {%> checked="checked" <%}%>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.STUDENTS)) {%> checked="checked" <%}%>/></td>
							</tr>
							<tr>
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
			<div class="centeralign">
			<input id="button_openframe" class="button centeralign" value="Add New Question" 
						onclick="showNewQuestionFrame()">
			</div>
			<form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD%>" name="form_addquestions" class="form_question" onsubmit="tallyCheckboxes('')" >			
			<table class="inputTable questionTable" id="questionTableNew" hidden="hidden">
				<tr>
					<td class="bold">Question <%=data.questions.size()+1%></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="4"><textarea rows="5" cols="140"
							class="textvalue" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TEXT%>"
							id="<%=Const.ParamsNames.FEEDBACK_QUESTION_TEXT%>"
							onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_QUESTION_INPUT_INSTRUCTIONS%>')"
							onmouseout="hideddrivetip()" tabindex="9"></textarea>
					</td>
				</tr>
				<tr>
					<td class="bold">Feedback Giver:</td>
					<td><select class="participantSelect" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE%>">
						<%
							for(String opt: data.getParticipantOptions(null, true)) out.println(opt);
						%>
					</select></td>
					<td class="bold nowrap">Feedback Recipient:</td>
					<td><select class="participantSelect" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>">
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
					<input type="radio" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE%>" value="custom" checked="checked"> 
					<input type="number" class="numberOfEntitiesBox" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>-" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>" min="1" max="250" value="1"> 
					<input type="radio" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE%>" value="max"> 					
					<span class="label">Unlimited</span>
					</td>
				</tr>

				<tr>
					<td colspan="4">
						<table class="dataTable participantTable">
							<tr>
								<th>User/Group</th>
								<th>Show answer to</th>
								<th>Show giver name to</th>
								<th>Show receiver name to</th>
							</tr>
							<tr>
								<td>Recipient(s)</td>
								<td><input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" name="receiverLeaderCheckbox" checked="checked"/></td>
								<td><input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" checked="checked"/></td>
								<td><input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" name="receiverFollowerCheckbox" disabled="disabled" checked="checked"/></td>
							</tr>
							<tr>
								<td>Giver's Team Members</td>
								<td><input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>"/></td>
								<td><input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>"/></td>
								<td><input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>"/></td>
							</tr>
							<tr>
								<td>Recipient's Team Members</td>
								<td><input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>"/></td>								
								<td><input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>"/></td>
								<td><input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>"/></td>
							</tr>
							<tr>
								<td>Other students</td>
								<td><input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>"/></td>								
								<td><input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>"/></td>
								<td><input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>"/></td>
							</tr>
							<tr>
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
						type="submit" class="button" value="Add Question" tabindex="9">
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
		</form>			
		<br><br>
		</div>
	</div>
	
	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>