<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="java.util.Date"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.FieldValidator"%>
<%@ page import="teammates.logic.core.Emails.EmailType"%>
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
    <title>TEAMMATES - Instructor</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css"/>
    <link rel="stylesheet" href="/stylesheets/datepicker.css" type="text/css" media="screen">
    
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/js/date.js"></script>
    <script type="text/javascript" src="/js/datepicker.js"></script>
    <script type="text/javascript" src="/js/AnchorPosition.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbacks.js"></script>
    <script type="text/javascript" src="/js/ajaxResponseRate.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body onload="readyFeedbackPage(); initializetooltip();">
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div id="frameBodyWrapper" class="container theme-showcase">
            <div id="topOfPage"></div>
            <h1>Add New Feedback Session</h1>
            
            <p class="bold centeralign middlealign"><span style="padding-right:10px">Session Type</span>
                <select style="width:730px" 
                    name="feedbackchangetype"
                    id="feedbackchangetype"
                    onmouseover="ddrivetip('Select a different type of session here.')"
                    onmouseout="hideddrivetip()" tabindex="0">
                    <option value="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE%>" selected="selected">Feedback Session with customizable questions</option>
                    <option value="<%=Const.ActionURIs.INSTRUCTOR_EVALS_PAGE%>">Standard Team Peer Evaluation with fixed questions</option>
                </select>
            </p>

            <div class="well well-plain">
                <form role="form">
                    <div class="panel panel-primary">
                        <div class="panel-heading">Basic info</div>
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label class="col-sm-4 control-label">Course</label>
                                        <div class="col-sm-8">
                                            <select class="form-control"
                                                name="<%=Const.ParamsNames.COURSE_ID%>"
                                                id="<%=Const.ParamsNames.COURSE_ID%>"
                                                onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_COURSE%>')"
                                                onmouseout="hideddrivetip()"
                                                tabindex="1">
                                                <%
                                                	for (String opt : data.getCourseIdOptions())
                                                		out.println(opt);
                                                %>
                                            </select>

                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label class="col-sm-4 control-label">Time Zone</label>
                                        <div class="col-sm-8">
                                            <select class="form-control" 
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>" 
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>"
                                                onmouseover="ddrivetip('<%=Const.Tooltips.EVALUATION_INPUT_TIMEZONE%>')"
                                                onmouseout="hideddrivetip()" tabindex="2">
                                                <%
                                                    for(String opt: data.getTimeZoneOptionsAsHtml()) out.println(opt);
                                                %>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <br>
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label class="col-sm-2 control-label">Session name:</label>
                                        <div class="col-sm-10">
                                            <input  class="form-control"
                                                type="text"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" id="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
                                                onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_INPUT_NAME%>')"
                                                onmouseout="hideddrivetip()" maxlength=<%=FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH%>
                                                value="<%if(data.newFeedbackSession!=null) out.print(InstructorFeedbacksPageData.sanitizeForHtml(data.newFeedbackSession.feedbackSessionName));%>"
                                                placeholder="e.g. Feedback for Project Presentation 1">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <div class="form-group">
                                            <label for="instructions"
                                                class="col-sm-2 control-label">Instructions</label>
                                            <div class="col-sm-10">
                                                <textarea   class="form-control" rows="4" cols="100%" 
                                                    name="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>" 
                                                    id="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>"
                                                    onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_INSTRUCTIONS%>')"
                                                    onmouseout="hideddrivetip()"
                                                    placeholder="e.g. Please answer all the given questions."
                                                    ><%
                                                        if(data.newFeedbackSession==null) {
                                                            out.print("Please answer all the given questions.");
                                                        } else {
                                                            out.print(InstructorFeedbacksPageData.sanitizeForHtml(data.newFeedbackSession.instructions.getValue()));
                                                        }
                                            %></textarea>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="panel panel-primary">
                        <div class="panel-heading">Duration</div>
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="row">
                                        <div class="col-md-12">
                                            <label class="label-control">Submission opening time:</label>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <input class="form-control col-sm-2"
                                                type="text"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
                                                value="<%=(data.newFeedbackSession==null? TimeHelper.formatDate(TimeHelper.getNextHour()) : TimeHelper.formatDate(data.newFeedbackSession.startTime))%>"
                                                placeholder="Date">
                                        </div>
                                        <div class="col-md-6">
                                            <select class="form-control"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTTIME%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTTIME%>">
                                                    <%
                                                        Date date;
                                                        date = (data.newFeedbackSession == null ? null
                                                                : data.newFeedbackSession.startTime);
                                                        for (String opt : data.getTimeOptionsAsHtml(date))
                                                            out.println(opt);
                                                    %>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4 border-left-gray">
                                    <div class="row">
                                        <div class="col-md-12">
                                            <label class="label-control">Submission closing time:</label>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <input class="form-control col-sm-2"
                                                type="text"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
                                                value="<%=(data.newFeedbackSession==null? "" : TimeHelper.formatDate(data.newFeedbackSession.endTime))%>"
                                                placeHolder="Date">
                                        </div>
                                        <div class="col-md-6">
                                            <select class="form-control"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDTIME%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDTIME%>">
                                                    <%
                                                        date = (data.newFeedbackSession == null ? null
                                                                : data.newFeedbackSession.endTime);
                                                        for (String opt : data.getTimeOptionsAsHtml(date))
                                                            out.println(opt);
                                                    %>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4 border-left-gray">
                                    <div class="row">
                                        <div class="col-md-12">
                                            <label class="control-label">Grace period</label>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-sm-8">
                                            <select class="form-control"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD%>">
                                                    <%
                                                        for(String opt: data.getGracePeriodOptionsAsHtml()) out.println(opt);
                                                    %>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                    <div class="panel panel-primary">
                        <div class="panel-heading">Visibility
                            settings</div>
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-md-6 border-gray">
                                    <strong>Session visible
                                        from:</strong><br>
                                    <div class="radio">
                                        <div class="row">
                                            <div class="col-md-2">
                                                <label>At <input
                                                    name="optionsRadios"
                                                    id="optionsRadios1"
                                                    value="option1"
                                                    checked=""
                                                    type="radio">
                                                </label>
                                            </div>
                                            <div class="col-md-5">
                                                <input type="datepicker"
                                                    class="form-control col-sm-2"
                                                    id="startdate"
                                                    placeholder="Date">
                                            </div>
                                            <div class="col-md-4">
                                                <select
                                                    class="form-control">
                                                    <option
                                                        value="cs1101"
                                                        selected="selected">2359</option>
                                                    <option value="0100">0100
                                                        min</option>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="radio">
                                        <label> <input
                                            name="optionsRadios"
                                            id="optionsRadios1"
                                            value="option1" type="radio">
                                            Submission opening time
                                        </label>
                                    </div>
                                    <div class="radio">
                                        <label> <input
                                            name="optionsRadios"
                                            id="optionsRadios2"
                                            value="private" type="radio">
                                            Never (this is a private
                                            session)
                                        </label>
                                    </div>
                                </div>

                                <div class="col-md-6 border-left-gray">
                                    <strong>Responses visible
                                        from:</strong><br>
                                    <div class="radio">
                                        <div class="row">
                                            <div class="col-md-2">
                                                <label>At <input
                                                    name="optionsRadios"
                                                    id="optionsRadios1"
                                                    value="option1"
                                                    checked=""
                                                    type="radio">
                                                </label>
                                            </div>
                                            <div class="col-md-5">
                                                <input type="datepicker"
                                                    class="form-control col-sm-2"
                                                    id="startdate"
                                                    placeholder="Date">
                                            </div>
                                            <div class="col-md-4">
                                                <select
                                                    class="form-control">
                                                    <option
                                                        value="cs1101"
                                                        selected="selected">2359</option>
                                                    <option value="0100">0100
                                                        min</option>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="radio">
                                        <label> <input
                                            name="optionsRadios"
                                            id="optionsRadios2"
                                            value="private" type="radio">
                                            Immediately
                                        </label>
                                    </div>
                                    <div class="radio">
                                        <label> <input
                                            name="optionsRadios"
                                            id="optionsRadios1"
                                            value="option1" type="radio">
                                            Publish manually
                                        </label>
                                    </div>
                                    <div class="radio">
                                        <label> <input
                                            name="optionsRadios"
                                            id="optionsRadios2"
                                            value="private" type="radio">
                                            Never
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="panel panel-primary">
                        <div class="panel-heading">Alerts</div>
                        <div class="panel-body">
                            <div class="form-group">
                                <label for="emailflag"
                                    class="col-sm-1 control-label">Send
                                    emails for:</label>
                                <div class="col-sm-2">
                                    <div class="checkbox">
                                        <label> <input
                                            type="checkbox" value="">
                                            Join reminder
                                        </label>
                                    </div>
                                </div>
                                <div class="col-sm-3">
                                    <div class="checkbox">
                                        <label> <input
                                            type="checkbox" value="">
                                            Session opening reminder
                                        </label>
                                    </div>
                                </div>
                                <div class="col-sm-3">
                                    <div class="checkbox">
                                        <label> <input
                                            type="checkbox" value="">
                                            Session closing reminder
                                        </label>
                                    </div>
                                </div>
                                <div class="col-sm-3">
                                    <div class="checkbox">
                                        <label> <input
                                            type="checkbox" value="">
                                            Results published
                                            announcement
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-10">
                            <button type="submit"
                                class="btn btn-primary">Submit</button>
                        </div>
                    </div>
                </form>
                <br> <br>
            </div>
            
            <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD%>" name="form_addfeedbacksession">
                <table class="inputTable sessionTable" id="sessionNameTable">
                    <tr>
                        <td class="label bold" >Course:</td>
                        <td></td>
                        <td class="label bold" >Time zone:</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="label bold">Feedback session name:</td>
                        <td colspan="3"></td>
                    </tr>
                </table>
                <br>
                <table class="inputTable sessionTable" id="timeFrameTable">
                    <tr>
                        <td class="label bold middlealign" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_STARTDATE%>')"
                            onmouseout="hideddrivetip()">Submission<br>Opening Time:</td>
                        <td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_STARTDATE%>')"
                            onmouseout="hideddrivetip()">
                             @ </td>
                        <td class="label bold middlealign" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_ENDDATE%>')"
                            onmouseout="hideddrivetip()">Submission<br>Closing Time:</td>
                        <td
                            onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_ENDDATE%>')"
                            onmouseout="hideddrivetip()"> @ </td>
                        <td class="label bold" onmouseover="ddrivetip('<%=Const.Tooltips.EVALUATION_INPUT_GRACEPERIOD%>')"
                            onmouseout="hideddrivetip()">Grace Period:</td>
                        <td onmouseover="ddrivetip('<%=Const.Tooltips.EVALUATION_INPUT_GRACEPERIOD%>')"
                            onmouseout="hideddrivetip()">
                            </td>
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
                            value="<%=Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_CUSTOM %>"
                            <%if(data.newFeedbackSession!=null &&
                                !TimeHelper.isSpecialTime(data.newFeedbackSession.sessionVisibleFromTime))
                                    out.print("checked=\"checked\"");%>
                            > <input style="width: 100px;" type="text"
                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>"
                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>"
                            value="<%=((data.newFeedbackSession==null || TimeHelper.isSpecialTime(data.newFeedbackSession.sessionVisibleFromTime)) ? "" : TimeHelper.formatDate(data.newFeedbackSession.sessionVisibleFromTime))%>"
                            <%if(data.newFeedbackSession==null ||
                                TimeHelper.isSpecialTime(data.newFeedbackSession.sessionVisibleFromTime))
                                    out.print("disabled=\"disabled\"");%>
                            readonly="readonly" tabindex="9"> @ <select
                            style="width: 70px;"
                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>"
                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>"
                            <%if(data.newFeedbackSession==null ||
                                TimeHelper.isSpecialTime(data.newFeedbackSession.sessionVisibleFromTime))
                                    out.print("disabled=\"disabled\"");%>
                            tabindex="10"
                            >
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
                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_atopen" 
                            value="<%=Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN %>"
                            <%if(data.newFeedbackSession==null || Const.TIME_REPRESENTS_FOLLOW_OPENING.equals(data.newFeedbackSession.sessionVisibleFromTime))
                                    out.print("checked=\"checked\"");%>>
                             Submissions opening time</td>
                        <td colspan="2" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLENEVER%>')"
                            onmouseout="hideddrivetip()">
                            <input type="radio" name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_never" value="never"
                            <%if(data.newFeedbackSession!=null &&
                                    Const.TIME_REPRESENTS_NEVER.equals(data.newFeedbackSession.sessionVisibleFromTime))
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
                            value="<%=Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_CUSTOM%>"
                            <%if(data.newFeedbackSession!=null &&
                                !TimeHelper.isSpecialTime(data.newFeedbackSession.resultsVisibleFromTime))
                                    out.print("checked=\"checked\"");%>>
                            <input style="width: 100px;" type="text"
                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>"
                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>"
                            value="<%=((data.newFeedbackSession==null || TimeHelper.isSpecialTime(data.newFeedbackSession.resultsVisibleFromTime)) ? "" : TimeHelper.formatDate(data.newFeedbackSession.resultsVisibleFromTime))%>"
                            readonly="readonly" tabindex="11"
                            <%if(data.newFeedbackSession==null ||
                                TimeHelper.isSpecialTime(data.newFeedbackSession.resultsVisibleFromTime))
                                    out.print("disabled=\"disabled\"");%>
                            >
                            @ <select style="width: 70px;"
                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>"
                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>"
                            tabindex="12"
                            onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_PUBLISHDATE%>')"
                            onmouseout="hideddrivetip()"
                            <%if(data.newFeedbackSession==null ||
                                TimeHelper.isSpecialTime(data.newFeedbackSession.resultsVisibleFromTime))
                                    out.print("disabled=\"disabled\"");%>>
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
                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_atvisible" 
                            value="<%=Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE %>"
                            <%if(data.newFeedbackSession!=null && Const.TIME_REPRESENTS_FOLLOW_VISIBLE.equals(data.newFeedbackSession.resultsVisibleFromTime))
                                    out.print("checked=\"checked\"");%>>
                             Immediately</td>
                        <td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELATER%>')"
                            onmouseout="hideddrivetip()"><input type="radio" name="resultsVisibleFromButton"
                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_later" 
                            value="<%=Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER %>"
                            <%if(data.newFeedbackSession==null ||
                            Const.TIME_REPRESENTS_LATER.equals(data.newFeedbackSession.resultsVisibleFromTime) ||
                            Const.TIME_REPRESENTS_NOW.equals(data.newFeedbackSession.resultsVisibleFromTime))
                                    out.print("checked=\"checked\"");%>>
                             Publish manually </td>
                        <td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLENEVER%>')"
                            onmouseout="hideddrivetip()"><input type="radio"
                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_never" 
                            value="<%=Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_NEVER %>"
                            <%if(data.newFeedbackSession!=null && Const.TIME_REPRESENTS_NEVER.equals(data.newFeedbackSession.resultsVisibleFromTime))
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
                            <label><input type="checkbox" checked="checked" disabled="disabled">
                                Join Reminder
                            </label>
                        </td>
                        <td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SENDOPENEMAIL%>')"
                            onmouseout="hideddrivetip()">
                            <label><input type="checkbox" checked="checked"
                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>"
                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>_open"
                                value="<%=EmailType.FEEDBACK_OPENING.toString()%>">
                                    Session Opening Reminder
                            </label>
                        </td>
                        <td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SENDCLOSINGEMAIL%>')"
                            onmouseout="hideddrivetip()">
                            <label><input type="checkbox" checked="checked"
                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>"
                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>_closing"
                                value="<%=EmailType.FEEDBACK_CLOSING.toString()%>">
                                    Session Closing Reminder
                            </label>
                        </td>
                        <td onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_SENDPUBLISHEDEMAIL%>')"
                            onmouseout="hideddrivetip()">
                            <label><input type="checkbox" checked="checked"
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
                        <td>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" class="centeralign"><input id="button_submit"
                            type="submit" class="button"
                            
                            value="Create Feedback Session" tabindex="14"></td>
                    </tr>
                </table>
                <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
            </form>
            
            <br>
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
                        for (FeedbackSessionAttributes fdb : data.existingFeedbackSessions) {
                            sessionIdx++;
                %>
                <tr class="sessions_row" id="session<%=sessionIdx%>">
                    <td class="t_session_coursecode"><%=fdb.courseId%></td>
                    <td class="t_session_name"><%=InstructorFeedbacksPageData
                            .sanitizeForHtml(fdb.feedbackSessionName)%></td>
                    <td class="t_session_status centeralign"><span
                        onmouseover="ddrivetip(' <%=InstructorFeedbacksPageData
                            .getInstructorHoverMessageForFeedbackSession(fdb)%>')"
                        onmouseout="hideddrivetip()"><%=InstructorFeedbacksPageData
                            .getInstructorStatusForFeedbackSession(fdb)%></span></td>
                    <td class="t_session_response centeralign<% if(!TimeHelper.isOlderThanAYear(fdb.createdTime)) { out.print(" recent");} %>">
                        <a oncontextmenu="return false;" href="<%=data.getFeedbackSessionStatsLink(fdb.courseId, fdb.feedbackSessionName)%>">Show</a></td>
                    <td class="centeralign no-print"><%=data.getInstructorFeedbackSessionActions(
                            fdb, false)%>
                    </td>
                </tr>
                <%
                        }
                        for (EvaluationAttributes edd : data.existingEvalSessions) {
                            sessionIdx++;
                %>
                <tr class="sessions_row" id="evaluation<%=sessionIdx%>">
                    <td class="t_session_coursecode"><%=edd.courseId%></td>
                    <td class="t_session_name"><%=InstructorFeedbacksPageData
                            .sanitizeForHtml(edd.name)%></td>
                    <td class="t_session_status centeralign"><span
                        onmouseover="ddrivetip(' <%=InstructorFeedbacksPageData
                            .getInstructorHoverMessageForEval(edd)%>')"
                        onmouseout="hideddrivetip()"><%=InstructorFeedbacksPageData
                            .getInstructorStatusForEval(edd)%></span></td>
                    <td class="t_session_response centeralign<% if(!TimeHelper.isOlderThanAYear(edd.endTime)) { out.print(" recent");} %>">
                        <a oncontextmenu="return false;" href="<%=data.getEvaluationStatsLink(edd.courseId, edd.name)%>">Show</a></td>
                    <td class="centeralign no-print"><%=data.getInstructorEvaluationActions(
                            edd, false)%>
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

    <div id="frameBottom">
        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </div>
</body>
</html>