<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.FeedbackParticipantType"%>
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
	<link rel="stylesheet" href="/stylesheets/instructorFeedback.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/instructorEval-print.css" type="text/css" media="print">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/instructor.js"></script>
	<script type="text/javascript" src="/js/instructorFeedback.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body onload="readyFeedbackEditPage(); initializetooltip();">
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Common.JSP_INSTRUCTOR_HEADER_NEW%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Edit Feedback Session</h1>
			</div>
			
			<form method="post" action="<%=Common.PAGE_INSTRUCTOR_FEEDBACK_EDIT_SAVE%>" name="form_editfeedbacksession">
				<table class="inputTable sessionTable" id="sessionNameTable">
					<tr>
						<td class="label bold">Course:</td>
						<td colspan="3"><%=data.session.courseId%></td>
						<td class="label bold">Time zone:</td>
						<td><select name="<%=Common.PARAM_FEEDBACK_SESSION_TIMEZONE%>" id="<%=Common.PARAM_FEEDBACK_SESSION_TIMEZONE%>"
									onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_TIMEZONE%>')"
									onmouseout="hideddrivetip()" tabindex="3"
									disabled="disabled">
										<%
											for(String opt: data.getTimeZoneOptions()) out.println(opt);
										%>
							</select></td>
					</tr>
					<tr>
						<td class="label bold" >Feedback session name:</td>
						<td style="width:620px" colspan="5"><%=data.session.feedbackSessionName%></td>
					</tr>
					<tr>
						<td class="label bold">Session visible from:</td>
						<td colspan="2"><input type="radio" name="<%=Common.PARAM_FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_custom" value="custom"
							<%if(Common.isSpecialTime(data.session.sessionVisibleFromTime) == false)  
									out.print("checked=\"checked\"");
							%>
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_FEEDBACK_SESSION_SESSIONVISIBLECUSTOM%>')"
							onmouseout="hideddrivetip()"
							onclick="document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_VISIBLEDATE%>').disabled='';
							document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_VISIBLETIME%>').disabled=''">
							<input style="width: 100px;" type="text"
							name="<%=Common.PARAM_FEEDBACK_SESSION_VISIBLEDATE%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_VISIBLEDATE%>"
							onclick="cal.select(this,'<%=Common.PARAM_FEEDBACK_SESSION_VISIBLEDATE%>','dd/MM/yyyy')"
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_FEEDBACK_SESSION_VISIBLEDATE%>')"
							onmouseout="hideddrivetip()"
							value="<%=Common.isSpecialTime(data.session.sessionVisibleFromTime) ? "" : Common.formatDate(data.session.sessionVisibleFromTime)%>"
							readonly="readonly" tabindex="3"
							<%if(Common.isSpecialTime(data.session.sessionVisibleFromTime) == true)  
									out.print("disabled=\"disabled\"");
							%>							
							> @ <select
							style="width: 70px;"
							name="<%=Common.PARAM_FEEDBACK_SESSION_VISIBLETIME%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_VISIBLETIME%>" tabindex="4"
							<%if(Common.isSpecialTime(data.session.sessionVisibleFromTime) == true)  
									out.print("disabled=\"disabled\"");
							%>		
							>
								<%
									for(String opt: data.getTimeOptions(true)) out.println(opt);
								%>
						</select></td>
						<td><input type="radio" name="<%=Common.PARAM_FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_atopen" value="atopen"
							<%if(data.session.sessionVisibleFromTime.equals(Common.TIME_REPRESENTS_FOLLOW_OPENING)) 
									out.print("checked=\"checked\"");%>
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_FEEDBACK_SESSION_SESSIONVISIBLEATOPEN%>')"
							onmouseout="hideddrivetip()"
							onclick="document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_VISIBLEDATE%>').disabled='disabled';
							document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_VISIBLETIME%>').disabled='disabled';">
							 At opening time</td>
						<td colspan="2"><input type="radio" name="<%=Common.PARAM_FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_never" value="never"
							<%if(data.session.sessionVisibleFromTime.equals(Common.TIME_REPRESENTS_NEVER)) 
									out.print("checked=\"checked\"");%>
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_FEEDBACK_SESSION_SESSIONVISIBLENEVER%>')"
							onmouseout="hideddrivetip()"
							onclick="document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_VISIBLEDATE%>').disabled='disabled'
							document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_VISIBLETIME%>').disabled='disabled';">
							 Never (This is a private session)</td>
					</tr>
					<tr>
						<td class="label bold">Results visible from:</td>
						<td colspan="2"><input type="radio" name="<%=Common.PARAM_FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_custom" value="custom"
							<%if(Common.isSpecialTime(data.session.resultsVisibleFromTime) == false) 
									out.print("checked=\"checked\"");%>
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_FEEDBACK_SESSION_RESULTSVISIBLECUSTOM%>')"
							onmouseout="hideddrivetip()"
							onclick="document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_PUBLISHDATE%>').disabled=''
							document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_PUBLISHTIME%>').disabled=''"> 
							<input style="width: 100px;" type="text"
							name="<%=Common.PARAM_FEEDBACK_SESSION_PUBLISHDATE%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_PUBLISHDATE%>"
							onclick="if(document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_custom').checked){cal.select(this,'<%=Common.PARAM_FEEDBACK_SESSION_PUBLISHDATE%>','dd/MM/yyyy');}
							else{return false;}"
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_FEEDBACK_SESSION_PUBLISHDATE%>')"
							onmouseout="hideddrivetip()"
							value="<%=Common.isSpecialTime(data.session.resultsVisibleFromTime) ? "" : Common.formatDate(data.session.resultsVisibleFromTime)%>"
							readonly="readonly" tabindex="5"
							<%if(Common.isSpecialTime(data.session.resultsVisibleFromTime) == true)  
									out.print("disabled=\"disabled\"");
							%>
							> @ <select
							style="width: 70px;"
							name="<%=Common.PARAM_FEEDBACK_SESSION_PUBLISHTIME%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_PUBLISHTIME%>" tabindex="6"
							<%if(Common.isSpecialTime(data.session.resultsVisibleFromTime) == true)  
									out.print("disabled=\"disabled\"");
							%>
							>
								<%
									for(String opt: data.getTimeOptions(true)) out.println(opt);
								%>
						</select></td>
						<td><input type="radio" name="<%=Common.PARAM_FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_atvisible" value="atvisible"
							<%if(data.session.resultsVisibleFromTime.equals(Common.TIME_REPRESENTS_FOLLOW_VISIBLE)) 
									out.print("checked=\"checked\"");%>
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_FEEDBACK_SESSION_RESULTSVISIBLEATVISIBLE%>')"
							onmouseout="hideddrivetip()"
							onclick="document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_PUBLISHDATE%>').disabled='disabled';
							document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_PUBLISHTIME%>').disabled='disabled'">
							 Once the session is visible</td>
						<td><input type="radio" name="resultsVisibleFromButton"
							id="<%=Common.PARAM_FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_later" value="later"
							<%if(data.session.resultsVisibleFromTime.equals(Common.TIME_REPRESENTS_LATER)) 
									out.print("checked=\"checked\"");%>
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_FEEDBACK_SESSION_PUBLISHDATE%>')"
							onmouseout="hideddrivetip()"
							onclick="document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_PUBLISHDATE%>').disabled='disabled';
							document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_PUBLISHTIME%>').disabled='disabled'">
							 Later </td>
						<td><input type="radio"
							name="<%=Common.PARAM_FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_never" value="never"
							<%if(data.session.resultsVisibleFromTime.equals(Common.TIME_REPRESENTS_NEVER)) 
									out.print("checked=\"checked\"");%>
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_FEEDBACK_SESSION_RESULTSVISIBLENEVER%>')"
							onmouseout="hideddrivetip()"
							onclick="document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_PUBLISHDATE%>').disabled='disabled'
							document.getElementById('<%=Common.PARAM_FEEDBACK_SESSION_PUBLISHTIME%>').disabled='disabled'">
							 Never</td>
					</tr>
					<tr>
						<td class="label bold">Opening time:</td>
						<td class="nowrap"><input style="width: 100px;" type="text"
							name="<%=Common.PARAM_FEEDBACK_SESSION_STARTDATE%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_STARTDATE%>"
							onclick="cal.select(this,'<%=Common.PARAM_FEEDBACK_SESSION_STARTDATE%>','dd/MM/yyyy')"
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_FEEDBACK_SESSION_STARTDATE%>')"
							onmouseout="hideddrivetip()"
							value="<%=Common.formatDate(data.session.startTime)%>"
							readonly="readonly" tabindex="7"> @ <select
							style="width: 70px;"
							name="<%=Common.PARAM_FEEDBACK_SESSION_STARTTIME%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_STARTTIME%>" tabindex="4">
								<%
									for(String opt: data.getTimeOptions(true)) out.println(opt);
								%>
						</select></td>
						<td class="label bold rightalign nowrap">Closing Time:</td>
						<td class="nowrap"><input style="width: 100px;" type="text"
							name="<%=Common.PARAM_FEEDBACK_SESSION_ENDDATE%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_ENDDATE%>"
							onclick="cal.select(this,'<%=Common.PARAM_FEEDBACK_SESSION_ENDDATE%>','dd/MM/yyyy')"
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_FEEDBACK_SESSION_ENDDATE%>')"
							onmouseout="hideddrivetip()"
							value="<%=Common.formatDate(data.session.endTime)%>"
							readonly="readonly" tabindex="8"> @ <select
							style="width: 70px;"
							name="<%=Common.PARAM_FEEDBACK_SESSION_ENDTIME%>"
							id="<%=Common.PARAM_FEEDBACK_SESSION_ENDTIME%>" tabindex="4">
								<%
									for(String opt: data.getTimeOptions(true)) out.println(opt);
								%>
						</select></td>
						<td class="label bold rightalign nowrap">Grace Period:</td>
						<td><select style="width: 75px;" name="<%=Common.PARAM_FEEDBACK_SESSION_GRACEPERIOD%>"
								id="<%=Common.PARAM_FEEDBACK_SESSION_GRACEPERIOD%>"
								onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_GRACEPERIOD%>')"
								onmouseout="hideddrivetip()" tabindex="7">
									<%
										for(String opt: data.getGracePeriodOptions()) out.println(opt);
									%>
						</select></td>
					</tr>
					<tr>
						<td class="label bold middlealign" >Instructions to students:</td>
						<td colspan="5">
							<textarea rows="4" cols="120" class="textvalue" name="<%=Common.PARAM_FEEDBACK_SESSION_INSTRUCTIONS%>" id="<%=Common.PARAM_FEEDBACK_SESSION_INSTRUCTIONS%>"
								onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_INPUT_INSTRUCTIONS%>')"
								onmouseout="hideddrivetip()" tabindex="8"><%=InstructorFeedbackEditPageData.escapeForHTML(data.session.instructions.getValue())%></textarea>
							
						</td>
					</tr>
					<tr>
						<td colspan="6" class="rightalign"><input id="button_submit_edit"
							type="submit" class="button"
							value="Save Changes"></td>
					</tr>
				</table>
				<input type="hidden" name="<%=Common.PARAM_FEEDBACK_SESSION_NAME%>" value="<%=data.session.feedbackSessionName%>">
				<input type="hidden" name="<%=Common.PARAM_COURSE_ID%>" value="<%=data.session.courseId%>">
			</form>
			
			<br>
			<jsp:include page="<%=Common.JSP_STATUS_MESSAGE_NEW%>" />
			<br>
			
			<% if (data.questions.isEmpty()) {%>
				<div class="centeralign bold" id="empty_message"><%=Common.MESSAGE_FEEDBACK_QUESTION_EMPTY%></div><br><br>
			<% } %>
			
			<% for(FeedbackQuestionAttributes question : data.questions) { %>
			<form method="post" action="<%=Common.PAGE_INSTRUCTOR_FEEDBACK_QUESTION_EDIT%>" id="form_editquestion-<%=question.questionNumber%>" name="form_editquestions">
			<table class="inputTable questionTable" id="questionTable<%=question.questionNumber%>">
			<tr>
				<td class="bold">Question <%=question.questionNumber%></td>
				<td></td>
				<td></td>
				<td class="rightalign color_red">
				<a href="#" class="color_blue" id="<%=Common.PARAM_FEEDBACK_QUESTION_EDITTEXT%>-<%=question.questionNumber%>" 
				onclick="enableEdit(<%=question.questionNumber%>,<%=data.questions.size()%>)">Edit</a>
				<a href="#" class="color_green" style="display:none" id="<%=Common.PARAM_FEEDBACK_QUESTION_SAVECHANGESTEXT%>-<%=question.questionNumber%>"
				onclick="document.getElementById('form_editquestion-<%=question.questionNumber%>').submit()">Save Changes</a>				
				&nbsp;&nbsp;
				<a href="#" class="color_red" onclick="deleteQuestion(<%=question.questionNumber%>)">Delete</a>
				</td>
			</tr>
			<tr>
					<td colspan="4"><textarea rows="5" cols="140"
							class="textvalue" name="<%=Common.PARAM_FEEDBACK_QUESTION_TEXT%>"
							id="<%=Common.PARAM_FEEDBACK_QUESTION_TEXT%>-<%=question.questionNumber%>"
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_FEEDBACK_QUESTION_INPUT_INSTRUCTIONS%>')"
							onmouseout="hideddrivetip()" tabindex="9"
							disabled="disabled"><%=question.questionText.getValue()%></textarea>
					</td>
				</tr>
				<tr>
					<td class="bold">Feedback Giver:</td>
					<td><select class="participantSelect" name="<%=Common.PARAM_FEEDBACK_QUESTION_GIVERTYPE%>" id="<%=Common.PARAM_FEEDBACK_QUESTION_GIVERTYPE%>-<%=question.questionNumber%>" disabled="disabled">
						<%
							for(String opt: data.getParticipantOptions(question, true)) out.println(opt);
						%>
					</select></td>
					<td class="bold nowrap">Feedback Recipient:</td>
					<td><select class="participantSelect" name="<%=Common.PARAM_FEEDBACK_QUESTION_RECIPIENTTYPE%>" id="<%=Common.PARAM_FEEDBACK_QUESTION_RECIPIENTTYPE%>-<%=question.questionNumber%>" disabled="disabled">
						<%
							for(String opt: data.getParticipantOptions(question, false)) out.println(opt);
						%>
					</select></td>
				</tr>
				<tr>
					<td></td><td></td>
					<td><span id="<%=Common.PARAM_FEEDBACK_QUESTION_NUMBEROFENTITIES%>_text-<%=question.questionNumber%>"class="bold">The maximum number of <span id="<%=Common.PARAM_FEEDBACK_QUESTION_NUMBEROFENTITIES%>_text_inner-<%=question.questionNumber%>"class="bold"></span> <br>each respondant should give feedback to:</span></td>
					<td><input type="number" name="<%=Common.PARAM_FEEDBACK_QUESTION_NUMBEROFENTITIES%>" id="<%=Common.PARAM_FEEDBACK_QUESTION_NUMBEROFENTITIES%>-<%=question.questionNumber%>"  min="1" max="250" value=<%=question.numberOfEntitiesToGiveFeedbackTo == Common.MAX_POSSIBLE_RECIPIENTS ? 1 : question.numberOfEntitiesToGiveFeedbackTo%> disabled="disabled"></td>
				</tr>

				<tr>
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
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER)) { %> checked="checked" <% } %>/></td>
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER)) { %> checked="checked" <% } %>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" name="receiverFollowerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER)) { %> checked="checked" <% } %>/></td>
							</tr>
							<tr>
								<td>Giver's Team Members</td>
								<td><input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) { %> checked="checked" <% } %>/></td>								
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) { %> checked="checked" <% } %>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) { %> checked="checked" <% } %>/></td>
							</tr>
							<tr>
								<td>Recipient's Team Members</td>
								<td><input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) { %> checked="checked" <% } %>/></td>								
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) { %> checked="checked" <% } %>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) { %> checked="checked" <% } %>/></td>
							</tr>
							<tr>
								<td>Other students</td>
								<td><input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>" disabled="disabled"
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.STUDENTS)) { %> checked="checked" <% } %>/></td>								
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.STUDENTS)) { %> checked="checked" <% } %>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.STUDENTS)) { %> checked="checked" <% } %>/></td>
							</tr>
							<tr>
								<td>Instructors</td>
								<td><input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" disabled="disabled"
								<%if(question.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS)) { %> checked="checked" <% } %>/></td>								
								<td><input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" disabled="disabled"
								<%if(question.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS)) { %> checked="checked" <% } %>/></td>
								<td><input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" disabled="disabled"
								<%if(question.showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS)) { %> checked="checked" <% } %>/></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
				<td colspan="6" class="rightalign"><input id="button_question_submit-<%=question.questionNumber%>"
						type="submit" class="button"
						value="Save Changes" tabindex="0"
						onclick="tallyCheckboxes(<%=question.questionNumber%>)"
						style="display:none"></td>
				</tr>
			</table>
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_SESSION_NAME%>" value="<%=data.session.feedbackSessionName%>">
			<input type="hidden" name="<%=Common.PARAM_COURSE_ID%>" value="<%=data.session.courseId%>">
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_QUESTION_ID%>" value="<%=question.getId()%>">
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_QUESTION_NUMBER%>" value="<%=question.questionNumber%>">
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_QUESTION_EDITTYPE%>" id="<%=Common.PARAM_FEEDBACK_QUESTION_EDITTYPE%>-<%=question.questionNumber%>" value="edit">
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_QUESTION_SHOWRESPONSESTO%>" >
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_QUESTION_SHOWGIVERTO%>" >
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_QUESTION_SHOWRECIPIENTTO%>" >
			</form>
			<br><br>
			<%
				}
			%>
			<div class="centeralign">
			<input id="button_openframe" class="button centeralign" value="Add New Question" 
						onclick="showNewQuestionFrame()">
			</div>
			<form method="post" action="<%=Common.PAGE_INSTRUCTOR_FEEDBACK_QUESTION_ADD%>" name="form_addquestions">			
			<table class="inputTable questionTable" id="questionTableNew" hidden="hidden">
				<tr>
					<td class="bold">Question <%=data.questions.size()+1%></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="4"><textarea rows="5" cols="140"
							class="textvalue" name="<%=Common.PARAM_FEEDBACK_QUESTION_TEXT%>"
							id="<%=Common.PARAM_FEEDBACK_QUESTION_TEXT%>"
							onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_FEEDBACK_QUESTION_INPUT_INSTRUCTIONS%>')"
							onmouseout="hideddrivetip()" tabindex="9"></textarea>
					</td>
				</tr>
				<tr>
					<td class="bold">Feedback Giver:</td>
					<td><select class="participantSelect" name="<%=Common.PARAM_FEEDBACK_QUESTION_GIVERTYPE%>">
						<%
							for(String opt: data.getParticipantOptions(null, true)) out.println(opt);
						%>
					</select></td>
					<td class="bold nowrap">Feedback Recipient:</td>
					<td><select class="participantSelect" id="<%=Common.PARAM_FEEDBACK_QUESTION_RECIPIENTTYPE%>" name="<%=Common.PARAM_FEEDBACK_QUESTION_RECIPIENTTYPE%>">
						<%
							for(String opt: data.getParticipantOptions(null, false)) out.println(opt);
						%>
					</select>
					</td>
				</tr>
				<tr>
					<td></td><td></td>
					<td><span id="<%=Common.PARAM_FEEDBACK_QUESTION_NUMBEROFENTITIES%>_text-"class="bold">The maximum number of <span id="<%=Common.PARAM_FEEDBACK_QUESTION_NUMBEROFENTITIES%>_text_inner-"class="bold"></span> <br>each respondant should give feedback to:</span></td>
					<td><input type="number" id="<%=Common.PARAM_FEEDBACK_QUESTION_NUMBEROFENTITIES%>-" name="<%=Common.PARAM_FEEDBACK_QUESTION_NUMBEROFENTITIES%>" min="1" max="250" value="1"></td>
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
						type="submit" class="button" value="Add Question" 
						onclick="tallyCheckboxes('')" tabindex="9">
					</td>					
				</tr>
			</table>
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_QUESTION_NUMBER%>" value="<%=data.questions.size()+1%>">
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_SESSION_NAME%>" value="<%=data.session.feedbackSessionName%>">
			<input type="hidden" name="<%=Common.PARAM_COURSE_ID%>" value="<%=data.session.courseId%>">
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_QUESTION_SHOWRESPONSESTO%>" >
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_QUESTION_SHOWGIVERTO%>" >
			<input type="hidden" name="<%=Common.PARAM_FEEDBACK_QUESTION_SHOWRECIPIENTTO%>" >
		</form>			
		<br><br>
		</div>
	</div>
	
	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER_NEW %>" />
	</div>
</body>
</html>