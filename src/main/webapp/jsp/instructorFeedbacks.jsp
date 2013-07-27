<%@ page import="java.util.Date"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.FieldValidator"%>
<%@ page import="teammates.common.datatransfer.EvaluationDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionDetailsBundle"%>
<%@ page import="teammates.ui.controller.InstructorFeedbacksPageData"%>
<%
	InstructorFeedbacksPageData data = (InstructorFeedbacksPageData)request.getAttribute("data");
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
    <link rel="stylesheet" href="/stylesheets/instructorEvals-print.css" type="text/css" media="print">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/date.js"></script>
	<script type="text/javascript" src="/js/CalendarPopup.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	
	<script type="text/javascript" src="/js/instructor.js"></script>
	<script type="text/javascript" src="/js/instructorFeedbacks.js"></script>
	<%
		if(data.newFeedbackSession==null){
	%>
	<script type="text/javascript">
		var doPageSpecificOnload = selectDefaultTimeOptions;
	</script>
	<%
		}
	%>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body onload="readyFeedbackPage(); initializetooltip();">
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Add New Feedback Session</h1>
			</div>
			
			<p class="bold centeralign middlealign"><span style="padding-right:10px">Session Type</span>
			<select style="width:730px" name="feedbackchangetype"
									id="feedbackchangetype"
									onmouseover="ddrivetip('Select a different type of session here.')"
									onmouseout="hideddrivetip()" tabindex="0">
									<option value="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE%>" selected="selected">Feedback Session with customizable questions</option>
									<!-- <option value="TEAM">Team Feedback Session</option> -->
									<!-- <option value="PRIVATE">Private Feedback Session</option> -->
									<option value="<%=Const.ActionURIs.INSTRUCTOR_EVALS_PAGE%>">Standard Peer Evaluation Session</option>			
			</select></p>
			<br><br>
			<form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD%>" name="form_addfeedbacksession">
				<table class="inputTable sessionTable" id="sessionNameTable">
					<tr>
						<td class="label bold" >Course:</td>
						<td><select name="<%=Const.ParamsNames.COURSE_ID%>"
									id="<%=Const.ParamsNames.COURSE_ID%>"
									onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_COURSE%>')"
									onmouseout="hideddrivetip()" tabindex="1">
										<%
											for(String opt: data.getCourseIdOptions()) out.println(opt);
										%>
							</select></td>
						<td class="label bold" >Time zone:</td>
						<td><select name="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>" id="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>"
									onmouseover="ddrivetip('<%=Const.Tooltips.EVALUATION_INPUT_TIMEZONE%>')"
									onmouseout="hideddrivetip()" tabindex="3">
										<%
											for(String opt: data.getTimeZoneOptionsAsHtml()) out.println(opt);
										%>
							</select></td>
					</tr>
					<tr>
						<td class="label bold">Feedback session name:</td>
						<td colspan="3"><input  type="text"
									name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" id="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
									onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_INPUT_NAME%>')"
									onmouseout="hideddrivetip()" maxlength=<%=FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH%>
									value="<%if(data.newFeedbackSession!=null) out.print(InstructorFeedbacksPageData.sanitizeForHtml(data.newFeedbackSession.feedbackSessionName));%>"
									tabindex="2" placeholder="e.g. Feedback for Project Presentation 1"></td>
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
							value="<%=(data.newFeedbackSession==null? TimeHelper.formatDate(TimeHelper.getNextHour()) : TimeHelper.formatDate(data.newFeedbackSession.startTime))%>"
							readonly="readonly" tabindex="7"> @ <select
							style="width: 70px;"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTTIME%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTTIME%>" tabindex="4">
								<%
									Date date;
									date = (data.newFeedbackSession == null ? null
											: data.newFeedbackSession.startTime);
									for (String opt : data.getTimeOptionsAsHtml(date))
										out.println(opt);
								%>
						</select></td>
						<td class="label bold middlealign" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_ENDDATE%>')"
							onmouseout="hideddrivetip()">Submission<br>Closing Time:</td>
						<td
							onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_ENDDATE%>')"
							onmouseout="hideddrivetip()"><input style="width: 100px;"
							type="text"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
							onclick="cal.select(this,'<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>','dd/MM/yyyy')"
							value="<%=(data.newFeedbackSession==null? "" : TimeHelper.formatDate(data.newFeedbackSession.endTime))%>"
							readonly="readonly" tabindex="8"> @ <select
							style="width: 70px;"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDTIME%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDTIME%>" tabindex="4">
								<%
									date = (data.newFeedbackSession == null ? null
											: data.newFeedbackSession.endTime);
									for (String opt : data.getTimeOptionsAsHtml(date))
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
							value="custom"> <input style="width: 100px;" type="text"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>"
							onclick="cal.select(this,'<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>','dd/MM/yyyy')"
							value="<%=((data.newFeedbackSession==null || TimeHelper.isSpecialTime(data.newFeedbackSession.sessionVisibleFromTime)) ? "" : TimeHelper.formatDate(data.newFeedbackSession.sessionVisibleFromTime))%>"
							readonly="readonly" tabindex="3"> @ <select
							style="width: 70px;"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>"
							tabindex="4">
								<%
									date = ((data.newFeedbackSession == null || TimeHelper
											.isSpecialTime(data.newFeedbackSession.sessionVisibleFromTime)) ? null
											: data.newFeedbackSession.sessionVisibleFromTime);
									for (String opt : data.getTimeOptionsAsHtml(date))
										out.println(opt);
								%>
						</select></td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLEATOPEN%>')"
							onmouseout="hideddrivetip()">
							<input type="radio" 
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_atopen" value="atopen"
							checked="checked">
							 Submissions opening time</td>
						<td colspan="2" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLENEVER%>')"
							onmouseout="hideddrivetip()">
							<input type="radio" name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_never" value="never">
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
							<%if(data.newFeedbackSession!=null &&
									 data.newFeedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE) == false &&
									 data.newFeedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_LATER) == false &&
									 data.newFeedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER) == false) 
									out.print("checked=\"checked\"");%>
							onclick="document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>').disabled=''
							document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>').disabled=''">
							<input style="width: 100px;" type="text"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>"
							onclick="if(document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_custom').checked){cal.select(this,'<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>','dd/MM/yyyy');}
							else{return false;}"
							value="<%=((data.newFeedbackSession==null || TimeHelper.isSpecialTime(data.newFeedbackSession.resultsVisibleFromTime)) ? "" : TimeHelper.formatDate(data.newFeedbackSession.resultsVisibleFromTime))%>"
							readonly="readonly" tabindex="5"
							disabled="if(document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_custom').checked){'disabled'}else{''}">
							@ <select style="width: 70px;"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>"
							tabindex="6"
							onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_PUBLISHDATE%>')"
							onmouseout="hideddrivetip()"
							disabled="if(document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_custom').checked){'disabled'}else{''}">
								<%
									date = ((data.newFeedbackSession == null || TimeHelper
											.isSpecialTime(data.newFeedbackSession.sessionVisibleFromTime)) ? null
											: data.newFeedbackSession.sessionVisibleFromTime);
									for (String opt : data.getTimeOptionsAsHtml(date))
										out.println(opt);
								%>
						</select></td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLEATVISIBLE%>')"
							onmouseout="hideddrivetip()">
							<input type="radio" name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_atvisible" value="atvisible"
							<%if(data.newFeedbackSession!=null && data.newFeedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) 
									out.print("checked=\"checked\"");%>							
							onclick="document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>').disabled='disabled';
							document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>').disabled='disabled'">
							 Once the session is visible</td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_PUBLISHDATE%>')"
							onmouseout="hideddrivetip()"><input type="radio" name="resultsVisibleFromButton"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_later" value="later"
							<%if(data.newFeedbackSession==null || data.newFeedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_LATER)) 
									out.print("checked=\"checked\"");%>							
							onclick="document.getElementById('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELATER%>').disabled='disabled';
							document.getElementById('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELATER%>').disabled='disabled'">
							 Publish manually </td>
						<td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLENEVER%>')"
							onmouseout="hideddrivetip()"><input type="radio"
							name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
							id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_never" value="never"
							<%if(data.newFeedbackSession!=null && data.newFeedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER)) 
									out.print("checked=\"checked\"");%>
							onclick="document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>').disabled='disabled'
							document.getElementById('<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>').disabled='disabled'">
							 Never</td>
					</tr>
				</table>
				<br>
				<table class="inputTable" id="instructionsTable">
					<tr>
						<td class="label bold middlealign" >Instructions to students:</td>
						<td><textarea rows="4" cols="100%" class="textvalue" name="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>" id="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>"
								onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_INSTRUCTIONS%>')"
								onmouseout="hideddrivetip()" tabindex="8"><%=data.newFeedbackSession==null ? "Please answer all the given questions." :InstructorFeedbacksPageData.sanitizeForHtml(data.newFeedbackSession.instructions.getValue())%></textarea>							
						</td>
					</tr>
					<tr>
						<td colspan="2" class="centeralign"><input id="button_submit"
							type="submit" class="button"
							onclick="return checkAddFeedbackSession(this.form);"
							value="Create Feedback Session" tabindex="9"></td>
					</tr>
				</table>
				<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
			</form>
			
			<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
			<br>
			
			<table class="dataTable">
				<tr>
					<th class="leftalign color_white bold">
						<input class="buttonSortAscending" type="button" id="button_sortcourseid" 
								onclick="toggleSort(this,1)">Course ID</th>
					<th class="leftalign color_white bold">
						<input class="buttonSortNone" type="button" id="button_sortname"
								onclick="toggleSort(this,2)">Session Name</th>
					<th class="centeralign color_white bold">Status</th>
					<th class="centeralign color_white bold"><span
						onmouseover="ddrivetip('<%=Const.Tooltips.EVALUATION_RESPONSE_RATE%>')"
						onmouseout="hideddrivetip()">Response Rate</span></th>
					<th class="centeralign color_white bold no-print">Action(s)</th>
				</tr>
				<%
					int sessionIdx = -1;
					if (data.existingFeedbackSessions.size() > 0
							|| data.existingEvalSessions.size() > 0) {
						for (FeedbackSessionDetailsBundle fdb : data.existingFeedbackSessions) {
							sessionIdx++;
				%>
				<tr class="sessions_row" id="session<%=sessionIdx%>">
					<td class="t_session_coursecode"><%=fdb.feedbackSession.courseId%></td>
					<td class="t_session_name"><%=InstructorFeedbacksPageData
							.sanitizeForHtml(fdb.feedbackSession.feedbackSessionName)%></td>
					<td class="t_session_status centeralign"><span
						onmouseover="ddrivetip(' <%=InstructorFeedbacksPageData
							.getInstructorHoverMessageForFeedbackSession(fdb.feedbackSession)%>')"
						onmouseout="hideddrivetip()"><%=InstructorFeedbacksPageData
							.getInstructorStatusForFeedbackSession(fdb.feedbackSession)%></span></td>
					<td class="t_session_response centeralign"><%=fdb.stats.submittedTotal%>
						/ <%=fdb.stats.expectedTotal%></td>
					<td class="centeralign no-print"><%=data.getInstructorFeedbackSessionActions(
							fdb.feedbackSession, sessionIdx, false)%>
					</td>
				</tr>
				<%
						}
						for (EvaluationDetailsBundle edd : data.existingEvalSessions) {
							sessionIdx++;
				%>
				<tr class="sessions_row" id="evaluation<%=sessionIdx%>">
					<td class="t_session_coursecode"><%=edd.evaluation.courseId%></td>
					<td class="t_session_name"><%=InstructorFeedbacksPageData
							.sanitizeForHtml(edd.evaluation.name)%></td>
					<td class="t_session_status centeralign"><span
						onmouseover="ddrivetip(' <%=InstructorFeedbacksPageData
							.getInstructorHoverMessageForEval(edd.evaluation)%>')"
						onmouseout="hideddrivetip()"><%=InstructorFeedbacksPageData
							.getInstructorStatusForEval(edd.evaluation)%></span></td>
					<td class="t_session_response centeralign"><%=edd.stats.submittedTotal%>
						/ <%=edd.stats.expectedTotal%></td>
					<td class="centeralign no-print"><%=data.getInstructorEvaluationActions(
							edd.evaluation, sessionIdx, false)%>
					</td>
				</tr>
				<%
						}
					} else {
				%>
				<tr>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
				<%
					}
				%>
			</table>
			<br>
			<br>
			<br>
			<%
				if(sessionIdx==-1){
			%>
				<div class="centeralign">No records found.</div>
				<br>
				<br>
				<br>
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