<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@page import="teammates.ui.controller.InstructorFeedbacksPageData"%>
<%@ page import="java.util.Date"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackMcqQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackMsqQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackNumericalScaleQuestionDetails"%>
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
    <!-- Bootstrap core CSS -->
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css">
    <link rel="stylesheet" href="/stylesheets/datepicker.css" type="text/css">

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/js/date.js"></script>
    <script type="text/javascript" src="/js/datepicker.js"></script>
    <script type="text/javascript" src="/js/AnchorPosition.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>

    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbacks.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body onload="readyFeedbackEditPage();">
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div id="frameBodyWrapper" class="container">
            <div id="topOfPage"></div>
            <h1>Edit Feedback Session</h1>
            <br>

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
                            >
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
            <form class="form-horizontal form_question" role="form" method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT%>"
            id="form_editquestion-<%=question.questionNumber%>" name="form_editquestions"
            onsubmit="tallyCheckboxes(<%=question.questionNumber%>)"
            <%=data.questionHasResponses.get(question.getId()) ? "editStatus=\"hasResponses\"" : "" %>
            >
            <div class="panel panel-primary inputTable questionTable" id="questionTable<%=question.questionNumber%>">
            <div class="panel-heading">
                <strong>Question</strong>
                <select class="questionNumber nonDestructive text-primary" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>-<%=question.questionNumber%>">
                <%
                    for(int opt = 1; opt < data.questions.size()+1; opt++){
                        out.println("<option value=" + opt +">" + opt + "</option>");
                    }
                %>
                </select>
                &nbsp;
                <%=questionDetails.getQuestionTypeDisplayName()%>
                <span class="pull-right">
                    <a class="btn btn-primary btn-xs" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_GETLINK%>-<%=question.questionNumber%>"
                    onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_QUESTION_GETLINK%>')" onmouseout="hideddrivetip()"
                    onclick="getQuestionLink(<%=question.questionNumber%>)">Get Link</a>
                    <a class="btn btn-primary btn-xs" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_EDITTEXT%>-<%=question.questionNumber%>"
                    onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_QUESTION_EDIT%>')" onmouseout="hideddrivetip()"
                    onclick="enableEdit(<%=question.questionNumber%>,<%=data.questions.size()%>)">Edit</a>
                    <a class="btn btn-primary btn-xs" style="display:none"
                     id="<%=Const.ParamsNames.FEEDBACK_QUESTION_SAVECHANGESTEXT%>-<%=question.questionNumber%>">Save Changes</a>
                    <a class="btn btn-primary btn-xs" onclick="deleteQuestion(<%=question.questionNumber%>)"
                    onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_QUESTION_DELETE%>')" onmouseout="hideddrivetip()">Delete</a>
                </span>
            </div>
            <div class="panel-body">
                <div>
                    <textarea rows="5"
                        class="form-control textvalue nonDestructive"
                        name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TEXT%>"
                        id="<%=Const.ParamsNames.FEEDBACK_QUESTION_TEXT%>-<%=question.questionNumber%>"
                        onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_QUESTION_INPUT_INSTRUCTIONS%>')"
                        onmouseout="hideddrivetip()" tabindex="9"
                        disabled="disabled"><%=InstructorFeedbackEditPageData.sanitizeForHtml(questionDetails.questionText)%></textarea>
                </div>
                <%=questionDetails.getQuestionSpecificEditFormHtml(question.questionNumber)%>
                <br>
                <div>
                    <div class="col-sm-6" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_GIVER%>')" onmouseout="hideddrivetip()">  
                        <label class="col-sm-4 control-label">
                            Feedback Giver:
                        </label>
                        <div class="col-sm-8">
                            <select class="form-control participantSelect" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE%>-<%=question.questionNumber%>" disabled="disabled"
                                        onchange="feedbackGiverUpdateVisibilityOptions(this)">
                                <%
                                    for(String opt: data.getParticipantOptions(question, true)) out.println(opt);
                                %>
                            </select>
                        </div>
                    </div>
                    <div class="col-sm-6" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RECIPIENT%>')" onmouseout="hideddrivetip()">
                        <label class="col-sm-4 control-label">
                            Feedback Recipient:
                        </label>
                        <div class="col-sm-8">
                            <select class="form-control participantSelect" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>-<%=question.questionNumber%>"
                                disabled="disabled" onchange="feedbackRecipientUpdateVisibilityOptions(this)">
                                <%
                                    for(String opt: data.getParticipantOptions(question, false)) out.println(opt);
                                %>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <br>
                    <div class="col-sm-6">
                        <a class="visibilityOptionsLabel btn btn-xs  btn-info" onclick="toggleVisibilityOptions(this)">
                            <span class="glyphicon glyphicon-eye-open">
                            </span> Show Visibility Options
                        </a>
                    </div>
                    <div class="col-sm-6 numberOfEntitiesElements<%=question.questionNumber%>">
                        <label id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>_text-<%=question.questionNumber%>" class="control-label col-sm-4 small">
                            The maximum number of <span id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>_text_inner-<%=question.questionNumber%>"></span> each respondant should give feedback to:
                        </label>
                        <div class="col-sm-8 form-control-static">
                            <div class="col-sm-6">
                                <input class="nonDestructive" type="radio" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE%>" <%=question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS ? "" : "checked=\"checked\""%> value="custom" disabled="disabled">
                                    <input class="nonDestructive numberOfEntitiesBox" type="number" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>-<%=question.questionNumber%>"  min="1" max="250" value=<%=question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS ? 1 : question.numberOfEntitiesToGiveFeedbackTo%> disabled="disabled">
                            </div>
                            <div class="col-sm-6">
                                <input class="nonDestructive" type="radio" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE%>" <%=question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS ? "checked=\"checked\"" : ""%> value="max" disabled="disabled">
                                <span class="">Unlimited</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="visibilityOptions">
                    <br>
                    <table class="dataTable participantTable table table-striped text-center">
                        <tr>
                            <th class="text-center">User/Group</th>
                            <th class="text-center">Can see answer</th>
                            <th class="text-center">Can see giver's name</th>
                            <th class="text-center">Can see recipient's name</th>
                        </tr>
                        <tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT%>')" onmouseout="hideddrivetip()">
                            <td class="text-left">Recipient(s)</td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%> centered" name="receiverLeaderCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" disabled="disabled"
                                <%if(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER)) {%> checked="checked" <%}%>/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" disabled="disabled"
                                <%if(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER)) {%> checked="checked" <%}%>/>     
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" name="receiverFollowerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" disabled="disabled"
                                <%if(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER)) {%> checked="checked" <%}%>/>
                            </td>
                        </tr>
                        <tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_GIVER_TEAM_MEMBERS%>')"
                            onmouseout="hideddrivetip()">
                            <td class="text-left">Giver's Team Members</td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>" disabled="disabled"
                                <%if(question.showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {%> checked="checked" <%}%>/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>" disabled="disabled"
                                <%if(question.showGiverNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {%> checked="checked" <%}%>/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>" disabled="disabled"
                                <%if(question.showRecipientNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {%> checked="checked" <%}%>/>
                            </td>
                        </tr>
                        <tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT_TEAM_MEMBERS%>')" onmouseout="hideddrivetip()">
                            <td class="text-left">Recipient's Team Members</td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>" disabled="disabled"
                                <%if(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {%> checked="checked" <%}%>/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>" disabled="disabled"
                                <%if(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {%> checked="checked" <%}%>/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>" disabled="disabled"
                                <%if(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {%> checked="checked" <%}%>/>
                            </td>
                        </tr>
                        <tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_OTHER_STUDENTS%>')" onmouseout="hideddrivetip()">
                            <td class="text-left">
                                Other students
                            </td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>" disabled="disabled"
                                <%if(question.showResponsesTo.contains(FeedbackParticipantType.STUDENTS)) {%> checked="checked" <%}%>/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>" disabled="disabled"
                                <%if(question.showGiverNameTo.contains(FeedbackParticipantType.STUDENTS)) {%> checked="checked" <%}%>/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>" disabled="disabled"
                                <%if(question.showRecipientNameTo.contains(FeedbackParticipantType.STUDENTS)) {%> checked="checked" <%}%>/>
                            </td>
                        </tr>
                        <tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_INSTRUCTORS%>')" onmouseout="hideddrivetip()">
                            <td class="text-left">
                                Instructors
                            </td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" disabled="disabled"
                                <%if(question.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS)) {%> checked="checked" <%}%>/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" disabled="disabled"
                                <%if(question.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS)) {%> checked="checked" <%}%>/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox<%=question.questionNumber%>" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" disabled="disabled"
                                <%if(question.showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS)) {%> checked="checked" <%}%>/>
                            </td>
                        </tr>
                    </table>
                </div>
                <div>
                    <span class="pull-right">
                        <input  id="button_question_submit-<%=question.questionNumber%>"
                                type="submit" class="btn btn-primary"
                                value="Save Changes" tabindex="0"
                                style="display:none">
                    </span>
                </div>
            </div>
            </div>
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

            <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD%>" name="form_addquestions" class="form-horizontal form_question" role="form" onsubmit="tallyCheckboxes('')" >
            <div class="well well-plain inputTable" id="addNewQuestionTable">
                <div class="row">
                    <div class="col-sm-6">
                        <label for="questionTypeChoice" class="control-label col-sm-4">
                            Question Type
                        </label>
                        <div class="col-sm-8">
                            <select class="form-control questionType"
                                    name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TYPE%>"
                                    id="questionTypeChoice">
                                <option value = "TEXT"><%=Const.FeedbackQuestionTypeNames.TEXT%></option>
                                <option value = "MCQ"><%=Const.FeedbackQuestionTypeNames.MCQ%></option>
                                <option value = "MSQ"><%=Const.FeedbackQuestionTypeNames.MSQ%></option>
                                <option value = "NUMSCALE"><%=Const.FeedbackQuestionTypeNames.NUMSCALE%></option>
                            </select>
                        </div>
                    </div>
                    <div class="col-sm-3">
                        <input id="button_openframe" class="btn btn-primary" value="Add New Question"
                            onclick="showNewQuestionFrame(document.getElementById('questionTypeChoice').value)">
                    </div>
                    <div class="col-sm-3">
                        <a class="btn btn-primary" href="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE + "?" + Const.ParamsNames.USER_ID + "=" + data.account.googleId + "&" + Const.ParamsNames.COURSE_ID + "=" + data.session.courseId%>" class="button">Done Editing</a>
                    </div>
                </div>
            </div>

            <div class="panel panel-primary inputTable questionTable" id="questionTableNew" style="display:none;">
                <div class="panel-heading">
                    <strong>Question</strong>
                    <select class="questionNumber nonDestructive text-primary" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>">
                    <%
                        for(int opt = 1; opt < data.questions.size()+1; opt++){
                            out.println("<option value=" + opt +">" + opt + "</option>");
                        }
                    %>
                    </select>
                    &nbsp;
                    <span id="questionTypeHeader"></span>
                    <span class="pull-right">
                        <a class="btn btn-primary btn-xs" onclick="deleteQuestion(-1)" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_QUESTION_DELETE%>')" onmouseout="hideddrivetip()">Delete
                        </a>
                    </span>
                </div>
                <div class="panel-body">
                    <div>
                        <textarea rows="5"
                            class="form-control textvalue nonDestructive"
                            name="questiontext"
                            id="questiontext-1"
                            onmouseover="ddrivetip('Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?')"
                            onmouseout="hideddrivetip()" tabindex="9"
                            disabled="disabled"></textarea>
                    </div>
                    <div id="mcqForm">
                        <%
                            FeedbackMcqQuestionDetails fMcqQd = new FeedbackMcqQuestionDetails();
                            fMcqQd.numOfMcqChoices = 2;
                            fMcqQd.mcqChoices.add("");
                            fMcqQd.mcqChoices.add("");
                        %>
                        <%=fMcqQd.getQuestionSpecificEditFormHtml(-1)%>
                    </div>
                    <div id="msqForm">
                        <%
                            FeedbackMsqQuestionDetails fMsqQd = new FeedbackMsqQuestionDetails();
                            fMsqQd.numOfMsqChoices = 2;
                            fMsqQd.msqChoices.add("");
                            fMsqQd.msqChoices.add("");
                        %>
                        <%=fMsqQd.getQuestionSpecificEditFormHtml(-1)%>
                    </div>
                    <div id="numScaleForm">
                        <%
                            FeedbackNumericalScaleQuestionDetails fNumQd = new FeedbackNumericalScaleQuestionDetails();
                            fNumQd.minScale = 1;
                            fNumQd.maxScale = 5;
                            fNumQd.step = 1;
                        %>
                        <%=fNumQd.getQuestionSpecificEditFormHtml(-1)%>
                    </div>
                    <br>
                    <div>
                        <div class="col-sm-6" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_GIVER%>')" onmouseout="hideddrivetip()">  
                            <label class="col-sm-4 control-label">
                                Feedback Giver:
                            </label>
                            <div class="col-sm-8">
                                <select class="form-control participantSelect" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE%>" onchange="feedbackGiverUpdateVisibilityOptions(this)">
                                    <%
                                        for(String opt: data.getParticipantOptions(null, true)) out.println(opt);
                                    %>
                                </select>
                            </div>
                        </div>
                        <div class="col-sm-6" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RECIPIENT%>')" onmouseout="hideddrivetip()">
                            <label class="col-sm-4 control-label">
                                Feedback Recipient:
                            </label>
                            <div class="col-sm-8">
                                <select class="form-control participantSelect" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>" onchange="feedbackRecipientUpdateVisibilityOptions(this)">
                                    <%
                                        for(String opt: data.getParticipantOptions(null, false)) out.println(opt);
                                    %>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <br>
                        <div class="col-sm-6">
                            <a class="visibilityOptionsLabel btn btn-xs  btn-info" onclick="toggleVisibilityOptions(this)">
                                <span class="glyphicon glyphicon-eye-open">
                                </span> Show Visibility Options
                            </a>
                        </div>
                        <div class="col-sm-6 numberOfEntitiesElements">
                            <label id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>_text-" class="control-label col-sm-4 small">
                                The maximum number of <span id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>_text_inner-"></span> each respondant should give feedback to:
                            </label>
                            <div class="col-sm-8 form-control-static">
                                <div class="col-sm-6">
                                    <input class="nonDestructive" type="radio" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE%>" value="custom">
                                        <input class="nonDestructive numberOfEntitiesBox" type="number" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES%>"  min="1" max="250" value="1">
                                </div>
                                <div class="col-sm-6">
                                    <input class="nonDestructive" type="radio" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE%>" checked="checked" value="max">
                                    <span class="">Unlimited</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="visibilityOptions">
                        <br>
                        <table class="dataTable participantTable table table-striped text-center">
                            <tr>
                                <th class="text-center">User/Group</th>
                                <th class="text-center">Can see answer</th>
                                <th class="text-center">Can see giver's name</th>
                                <th class="text-center">Can see recipient's name</th>
                            </tr>
                            <tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT%>')" onmouseout="hideddrivetip()">
                                <td class="text-left">Recipient(s)</td>
                                <td>
                                    <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" checked="checked"/>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" checked="checked"/>     
                                </td>
                                <td>
                                    <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>" disabled="disabled" checked="checked"/>
                                </td>
                            </tr>
                            <tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_GIVER_TEAM_MEMBERS%>')"
                                onmouseout="hideddrivetip()">
                                <td class="text-left">Giver's Team Members</td>
                                <td>
                                    <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>"/>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>"/>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>"/>
                                </td>
                            </tr>
                            <tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT_TEAM_MEMBERS%>')" onmouseout="hideddrivetip()">
                                <td class="text-left">Recipient's Team Members</td>
                                <td>
                                    <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>"/>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>"/>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>"/>
                                </td>
                            </tr>
                            <tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_OTHER_STUDENTS%>')" onmouseout="hideddrivetip()">
                                <td class="text-left">
                                    Other students
                                </td>
                                <td>
                                    <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>"/>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>"/>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>"/>
                                </td>
                            </tr>
                            <tr onmouseover="ddrivetip('<%=Const.Tooltips.VISIBILITY_OPTIONS_INSTRUCTORS%>')" onmouseout="hideddrivetip()">
                                <td class="text-left">
                                    Instructors
                                </td>
                                <td>
                                    <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" checked="checked"/>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>" checked="checked"/>
                                </td>
                                <td>
                                    <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>"checked="checked"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div>
                        <span class="pull-right">
                            <input  id="button_submit_add"
                                    type="submit" class="btn btn-primary"
                                    value="Save Question" tabindex="9">
                        </span>
                    </div>
                </div>
            </div>
            </div>
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
        <div class="well well-plain inputTable" id="questionPreviewTable">
            <div class="row">
                <form class="form-horizontal">
                    <label class="control-label col-sm-2 text-right">
                        Preview Session:
                    </label>
                </form>
                <div class="col-sm-5" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_PREVIEW_ASSTUDENT%>')" onmouseout="hideddrivetip()">
                    <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASSTUDENT%>"
                        name="form_previewasstudent" class="form_preview" target="_blank">
                        
                        <div class="col-sm-6">
                            <select class="form-control" name="<%=Const.ParamsNames.PREVIEWAS%>">
                                <%
                                    for(StudentAttributes student : data.studentList) {
                                %>
                                        <option value="<%=student.email%>">[<%=student.team%>] <%=student.name%></option>
                                <%
                                    }
                                %>
                            </select>
                        </div>
                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.session.feedbackSessionName%>">
                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.session.courseId%>">
                        <div class="col-sm-6">
                            <input id="button_preview_student" type="submit" class="btn btn-primary" value="Preview as Student"
                            <%=data.studentList.isEmpty() ? "disabled=\"disabled\" style=\"background: #66727A;\"" : ""%>>
                        </div>
                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                    </form>
                </div>
                <div class="col-sm-5" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_PREVIEW_ASINSTRUCTOR%>')" onmouseout="hideddrivetip()">
                    <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR%>"
                        name="form_previewasinstructor" class="form_preview" target="_blank">
                        <div class="col-sm-6">
                            <select class="form-control" name="<%=Const.ParamsNames.PREVIEWAS%>">
                            <%
                                for(InstructorAttributes instructor : data.instructorList) {
                            %>
                                    <option value="<%=instructor.email%>"><%=instructor.name%></option>
                            <%
                                }
                            %>
                            </select>
                        </div>
                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.session.feedbackSessionName%>">
                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.session.courseId%>">
                        <div class="col-sm-6">
                            <input id="button_preview_instructor" type="submit" class="btn btn-primary" value="Preview as Instructor">
                        </div>
                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                    </form>
                </div>
            </div>
        </div>
        <br><br>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>