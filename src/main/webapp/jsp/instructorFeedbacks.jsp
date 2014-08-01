<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="java.util.Date"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.FieldValidator"%>
<%@ page import="teammates.logic.core.Emails.EmailType"%>
<%@ page import="teammates.common.datatransfer.EvaluationDetailsBundle"%>
<%@ page
    import="teammates.common.datatransfer.FeedbackSessionDetailsBundle"%>
<%@ page import="teammates.ui.controller.InstructorFeedbacksPageData"%>
<%
    InstructorFeedbacksPageData data = (InstructorFeedbacksPageData) request
            .getAttribute("data");

%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="shortcut icon" href="/favicon.png">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>TEAMMATES - Instructor</title>
        <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css" />
        <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css" />
        <link rel="stylesheet" href="/stylesheets/datepicker.css" type="text/css" media="screen">
        <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css" />
        
        <script type="text/javascript" src="/js/googleAnalytics.js"></script>
        <script type="text/javascript" src="/js/jquery-minified.js"></script>
        <script type="text/javascript"
            src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
        <script type="text/javascript" src="/js/date.js"></script>
        <script type="text/javascript" src="/js/datepicker.js"></script>
                <script type="text/javascript" src="/js/common.js"></script>
        <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
        
        <script type="text/javascript" src="/js/instructor.js"></script>
        <script type="text/javascript" src="/js/instructorFeedbacks.js"></script>
        <script type="text/javascript" src="/js/ajaxResponseRate.js"></script>
        <jsp:include page="../enableJS.jsp"></jsp:include>
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
            <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>

<body onload="readyFeedbackPage();">
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div id="frameBodyWrapper" class="container theme-showcase">
        <div id="topOfPage"></div>
        <h1>Add New Feedback Session</h1>

        <div class="well well-plain">
            <form class="form-group" method="post"
                action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD%>"
                name="form_addfeedbacksession">
                <div class="row">
                    <h4 class="label-control col-md-2 text-md">Create new </h4>
                    <div class="col-md-5">
                        <div class="col-md-10"
                        title="Select a session type here."
                        data-toggle="tooltip"
                        data-placement="top">
                            <select class="form-control"
                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_TYPE%>"
                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_TYPE%>">
                                <option value="STANDARD">
                                    Session with your own questions
                                </option>
                                <option value="TEAMEVALUATION" selected="selected">
                                    Team peer evaluation session
                                </option>
                            </select>
                        </div>
                        <div class="col-md-1">
                            <h5><a href="/instructorHelp.html#fbSetupSession" target="_blank"><span class="glyphicon glyphicon-info-sign"></span></a></h5>
                        </div>
                    </div>
                    <h4 class="label-control col-md-1 text-md">Or: </h4>
                    <div class="col-md-3">
                        <a id="button_copy" class="btn btn-info" style="vertical-align:middle;">Copy
                            from previous feedback sessions</a>
                    </div>
                </div>
                <br>
            
                <div class="panel panel-primary">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-md-6"
                                title="<%=Const.Tooltips.FEEDBACK_SESSION_COURSE%>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <div class="form-group">
                                    <h5 class="col-sm-4">
                                        <label for="<%=Const.ParamsNames.COURSE_ID%>"
                                            class="control-label">Course</label>
                                    </h5>
                                    <div class="col-sm-8">
                                        <select class="form-control"
                                            name="<%=Const.ParamsNames.COURSE_ID%>"
                                            id="<%=Const.ParamsNames.COURSE_ID%>">
                                            <%
                                                for (String opt : data.getCourseIdOptions())
                                                    out.println(opt);
                                            %>
                                        </select>

                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6"
                                title="<%=Const.Tooltips.EVALUATION_INPUT_TIMEZONE%>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <div class="form-group">
                                    <h5 class="col-sm-4">
                                        <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>"
                                            class="control-label">Timezone</label>
                                    </h5>
                                    <div class="col-sm-8">
                                        <select class="form-control"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>">
                                            <%
                                                for (String opt : data.getTimeZoneOptionsAsHtml())
                                                    out.println(opt);
                                            %>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-md-12"
                                title="<%=Const.Tooltips.FEEDBACK_SESSION_INPUT_NAME%>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <div class="form-group">
                                    <h5 class="col-sm-2">
                                        <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
                                            class="control-label">Session
                                            name</label>
                                    </h5>
                                    <div class="col-sm-10">
                                        <input class="form-control"
                                            type="text"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
                                            maxlength=<%=FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH%>
                                            value="<%if (data.newFeedbackSession != null)
                                                       out.print(InstructorFeedbacksPageData.sanitizeForHtml(data.newFeedbackSession.feedbackSessionName));%>"
                                            placeholder="e.g. Feedback for Project Presentation 1">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <br>
                        <div class="row" id="instructionsRow">
                            <div class="col-md-12"
                                title="<%=Const.Tooltips.FEEDBACK_SESSION_INSTRUCTIONS%>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <div class="form-group">
                                    <h5 class="col-sm-2">
                                        <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>"
                                            class="control-label">Instructions</label>
                                    </h5>
                                    <div class="col-sm-10">
                                        <textarea
                                            class="form-control"
                                            rows="4" cols="100%"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>"
                                            placeholder="e.g. Please answer all the given questions."
                                            ><%
                                                if (data.newFeedbackSession == null) {
                                                    out.print("Please answer all the given questions.");
                                                } else {
                                                    out.print(InstructorFeedbacksPageData
                                                            .sanitizeForHtml(data.newFeedbackSession.instructions
                                                                    .getValue()));
                                                }
                                        %></textarea>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="panel panel-primary" id="timeFramePanel">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-md-5"
                                title="<%=Const.Tooltips.FEEDBACK_SESSION_STARTDATE%>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <div class="row">
                                    <div class="col-md-6">
                                        <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
                                            class="label-control">
                                            Submission opening time</label>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6">
                                        <input
                                            class="form-control col-sm-2"
                                            type="text"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
                                            value="<%=(data.newFeedbackSession == null ? TimeHelper.formatDate(TimeHelper.getNextHour()) : 
                                                       TimeHelper.formatDate(data.newFeedbackSession.startTime))%>"
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
                            <div class="col-md-5 border-left-gray"
                                title="<%=Const.Tooltips.FEEDBACK_SESSION_ENDDATE%>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <div class="row">
                                    <div class="col-md-6">
                                        <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
                                            class="label-control">Submission
                                            closing time</label>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6">
                                        <input
                                            class="form-control col-sm-2"
                                            type="text"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
                                            value="<%=(data.newFeedbackSession == null ? "" : TimeHelper.formatDate(data.newFeedbackSession.endTime))%>"
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
                            <div class="col-md-2 border-left-gray"
                                title="<%=Const.Tooltips.EVALUATION_INPUT_GRACEPERIOD%>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <div class="row">
                                    <div class="col-md-12"> 
                                        <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD%>"
                                            class="control-label">
                                            Grace period</label>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-12">
                                        <select class="form-control"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD%>">
                                            <%
                                                for (String opt : data.getGracePeriodOptionsAsHtml())
                                                    out.println(opt);
                                            %>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
                <div class="row" id="uncommonSettingsInfo">
                    <div class="col-md-12 text-muted">
                        <span id="uncommonSettingsInfoText">
                        </span>
                        <a id="editUncommonSettingsButton" data-edit="[Edit]" data-done="[Done]">[Edit]</a>
                        <br><br>
                    </div>
                </div>
                <div class="panel panel-primary" style="display:none;" id="sessionResponsesVisiblePanel">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="row">
                                    <div class="col-md-6"
                                        title="<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLELABEL%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <label class="label-control">
                                            Session visible from </label>
                                    </div>
                                </div>
                                <div class="row radio">
                                    <div class="col-md-2"
                                        title="<%=Const.Tooltips.FEEDBACK_SESSION_VISIBLEDATE%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <label
                                            for="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_custom">At
                                        </label> <input type="radio"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_custom"
                                            value="<%=Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_CUSTOM%>"
                                            <%if (data.newFeedbackSession != null && !TimeHelper.isSpecialTime(data.newFeedbackSession.sessionVisibleFromTime))
                                                out.print("checked=\"checked\"");%>>
                                    </div>
                                    <div class="col-md-5">
                                        <input
                                            class="form-control col-sm-2"
                                            type="text"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>"
                                            value="<%=((data.newFeedbackSession == null || TimeHelper.isSpecialTime(data.newFeedbackSession.sessionVisibleFromTime)) ? "": TimeHelper.formatDate(data.newFeedbackSession.sessionVisibleFromTime))%>"
                                            <%if (data.newFeedbackSession == null || TimeHelper.isSpecialTime(data.newFeedbackSession.sessionVisibleFromTime))
                                                    out.print("disabled=\"disabled\"");
                                            %>>
                                    </div>
                                    <div class="col-md-4">
                                        <select class="form-control"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>"
                                            <%if (data.newFeedbackSession == null || TimeHelper.isSpecialTime(data.newFeedbackSession.sessionVisibleFromTime))
                                                    out.print("disabled=\"disabled\"");
                                            %>>

                                            <%
                                                date = ((data.newFeedbackSession == null || TimeHelper
                                                        .isSpecialTime(data.newFeedbackSession.sessionVisibleFromTime)) ? null
                                                        : data.newFeedbackSession.sessionVisibleFromTime);
                                                for (String opt : data.getTimeOptionsAsHtml(date))
                                                    out.println(opt);
                                            %>
                                        </select>
                                    </div>
                                </div>
                                <div class="row radio">
                                    <div class="col-md-6"
                                        title="<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLEATOPEN%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <label
                                            for="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_atopen">Submission
                                            opening time </label> <input
                                            type="radio"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_atopen"
                                            value="<%=Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN%>"
                                            <%if (data.newFeedbackSession == null || Const.TIME_REPRESENTS_FOLLOW_OPENING.equals(data.newFeedbackSession.sessionVisibleFromTime))
                                                    out.print("checked=\"checked\"");
                                            %>>
                                    </div>
                                </div>
                                <div class="row radio">
                                    <div class="col-md-6"
                                        title="<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLENEVER%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <label
                                            for="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_never">Never
                                            (this is a private session)</label>
                                        <input type="radio"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_never"
                                            value="never"
                                            <%if (data.newFeedbackSession != null && Const.TIME_REPRESENTS_NEVER.equals(data.newFeedbackSession.sessionVisibleFromTime))
                                                    out.print("checked=\"checked\"");
                                            %>>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-6 border-left-gray" id="responsesVisibleFromColumn">
                                <div class="row">
                                    <div class="col-md-6"
                                        title="<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELABEL%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <label class="label-control">Responses
                                            visible from</label>
                                    </div>
                                </div>
                                <div class="row radio">
                                    <div class="col-md-2"
                                        title="<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLECUSTOM%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <label
                                            for="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_custom">At</label>

                                        <input type="radio"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_custom"
                                            value="<%=Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_CUSTOM%>"
                                            <%if (data.newFeedbackSession != null && !TimeHelper.isSpecialTime(data.newFeedbackSession.resultsVisibleFromTime))
                                                out.print("checked=\"checked\"");
                                            %>>
                                    </div>
                                    <div class="col-md-5">
                                        <input class="form-control"
                                            type="text"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>"
                                            value="<%=((data.newFeedbackSession == null || TimeHelper.isSpecialTime(data.newFeedbackSession.resultsVisibleFromTime)) ? "" : 
                                                    TimeHelper.formatDate(data.newFeedbackSession.resultsVisibleFromTime))%>"
                                            <%if (data.newFeedbackSession == null || TimeHelper.isSpecialTime(data.newFeedbackSession.resultsVisibleFromTime))
                                                out.print("disabled=\"disabled\"");%>>
                                    </div>
                                    <div class="col-md-4">
                                        <select class="form-control"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>"
                                            title="<%=Const.Tooltips.FEEDBACK_SESSION_PUBLISHDATE%>"
                                            data-toggle="tooltip" 
                                            data-placement="top"
                                            <%if (data.newFeedbackSession == null || TimeHelper.isSpecialTime(data.newFeedbackSession.resultsVisibleFromTime))
                                                out.print("disabled=\"disabled\"");%>>
                                            <%
                                                date = (data.newFeedbackSession == null || TimeHelper
                                                        .isSpecialTime(data.newFeedbackSession.resultsVisibleFromTime)) ? null
                                                        : data.newFeedbackSession.resultsVisibleFromTime;
                                                for (String opt : data.getTimeOptionsAsHtml(date))
                                                    out.println(opt);
                                            %>

                                        </select>
                                    </div>
                                </div>
                                <div class="row radio">
                                    <div class="col-md-3"
                                        title="<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLEATVISIBLE%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <label
                                            for="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_atvisible">Immediately</label>
                                        <input type="radio"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_atvisible"
                                            value="<%=Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE%>"
                                            <%if (data.newFeedbackSession != null && Const.TIME_REPRESENTS_FOLLOW_VISIBLE.equals(data.newFeedbackSession.resultsVisibleFromTime))
                                                    out.print("checked=\"checked\"");
                                            %>>
                                    </div>
                                </div>
                                <div class="row radio">
                                    <div class="col-md-4"
                                        title="<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELATER%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <label
                                            for="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_later">Publish
                                            manually</label> <input type="radio"
                                            name="resultsVisibleFromButton"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_later"
                                            value="<%=Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER%>"
                                            <%if (data.newFeedbackSession == null || Const.TIME_REPRESENTS_LATER.equals(data.newFeedbackSession.resultsVisibleFromTime) || Const.TIME_REPRESENTS_NOW.equals(data.newFeedbackSession.resultsVisibleFromTime))
                                                    out.print("checked=\"checked\"");
                                            %>>
                                    </div>
                                </div>
                                <div class="row radio">
                                    <div class="col-md-2"
                                        title="<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLENEVER%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <label
                                            for="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_never">Never</label>
                                        <input type="radio"
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_never"
                                            value="<%=Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_NEVER%>"
                                            <%if (data.newFeedbackSession != null && Const.TIME_REPRESENTS_NEVER.equals(data.newFeedbackSession.resultsVisibleFromTime))
                                                    out.print("checked=\"checked\"");
                                            %>>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="panel panel-primary" style="display:none;" id="sendEmailsForPanel">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-md-12">
                                <label class="control-label">Send
                                    emails for</label>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-sm-3"
                                title="<%=Const.Tooltips.FEEDBACK_SESSION_SENDOPENEMAIL%>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <div class="checkbox">
                                    <label>Session opening
                                        reminder</label> <input type="checkbox"
                                        checked="checked"
                                        name="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>"
                                        id="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>_open"
                                        value="<%=EmailType.FEEDBACK_OPENING.toString()%>">
                                </div>
                            </div>
                            <div class="col-sm-3"
                                title="<%=Const.Tooltips.FEEDBACK_SESSION_SENDCLOSINGEMAIL%>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <div class="checkbox">
                                    <label
                                        for="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>_closing">Session
                                        closing reminder</label> <input
                                        type="checkbox"
                                        checked="checked"
                                        name="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>"
                                        id="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>_closing"
                                        value="<%=EmailType.FEEDBACK_CLOSING.toString()%>">
                                </div>
                            </div>
                            <div class="col-sm-4"
                                title="<%=Const.Tooltips.FEEDBACK_SESSION_SENDPUBLISHEDEMAIL%>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <div class="checkbox">
                                    <label
                                        for="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>_published">Results
                                        published announcement</label> <input
                                        type="checkbox"
                                        checked="checked"
                                        name="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>"
                                        id="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>_published"
                                        value="<%=EmailType.FEEDBACK_PUBLISHED.toString()%>">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="form-group">
                    <div class="col-md-offset-5 col-md-3">
                        <button id="button_submit" type="submit" class="btn btn-primary">Create Feedback Session</button>
                    </div>
                </div>
                <input type="hidden"
                    name="<%=Const.ParamsNames.USER_ID%>"
                    value="<%=data.account.googleId%>">
            </form>
            <br> <br>
        </div>

        <br>
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <br>

        <table class="table-responsive table table-striped table-bordered">
            <thead>
                <tr class="fill-primary">
                    <th id="button_sortid" onclick="toggleSort(this,1);"
                        class="button-sort-ascending">Course ID <span
                        class="icon-sort unsorted"></span>
                    </th>
                    <th id="button_sortname" onclick="toggleSort(this,2)"
                        class="button-sort-none">Session Name <span
                        class="icon-sort unsorted"></span>
                    </th>
                    <th>Status</th>
                    <th><span
                        title="<%=Const.Tooltips.EVALUATION_RESPONSE_RATE%>"
                        data-toggle="tooltip" data-placement="top">
                            Response Rate</span></th>
                <th class="no-print">Action(s)</th>
            </tr>
            </thead>
            <%
                int sessionIdx = -1;
                if (data.existingFeedbackSessions.size() > 0
                        || data.existingEvalSessions.size() > 0) {
                    for (FeedbackSessionAttributes fdb : data.existingFeedbackSessions) {
                        sessionIdx++;
            %>
            <tr class="sessionsRow" id="session<%=sessionIdx%>">
                <td><%=fdb.courseId%></td>
                <td><%=InstructorFeedbacksPageData
                            .sanitizeForHtml(fdb.feedbackSessionName)%></td>
                <td><span title="<%=InstructorFeedbacksPageData.getInstructorHoverMessageForFeedbackSession(fdb)%>" 
                        data-toggle="tooltip" data-placement="top">
                        <%=InstructorFeedbacksPageData.getInstructorStatusForFeedbackSession(fdb)%>
                    </span>
                </td>
                <td
                    class="session-response-for-test<%if (!TimeHelper.isOlderThanAYear(fdb.createdTime)) {
                        out.print(" recent");
                    }%>">
                    <a oncontextmenu="return false;"
                    href="<%=data.getFeedbackSessionStatsLink(fdb.courseId,
                            fdb.feedbackSessionName)%>">Show</a>
                </td>
                <td class="no-print"><%=data.getInstructorFeedbackSessionActions(fdb, false, data.instructors.get(fdb.courseId))%></td>
            </tr>
            <%
                }
                    for (EvaluationAttributes edd : data.existingEvalSessions) {
                        sessionIdx++;
            %>
            <tr class="sessionsRow" id="evaluation<%=sessionIdx%>">
                <td><%=edd.courseId%></td>
                <td><%=InstructorFeedbacksPageData
                            .sanitizeForHtml(edd.name)%></td>
                <td><span title="<%=InstructorFeedbacksPageData.getInstructorHoverMessageForEval(edd)%>"
                        data-toggle="tooltip" data-placement="top">
                        <%=InstructorFeedbacksPageData.getInstructorStatusForEval(edd)%>
                    </span>
                </td>
                <td
                    class="session-response-for-test<%if (!TimeHelper.isOlderThanAYear(edd.endTime)) {
                        out.print(" recent");
                    }%>">
                    <a oncontextmenu="return false;"
                    href="<%=data.getEvaluationStatsLink(edd.courseId,
                            edd.name)%>">Show</a>
                </td>
                <td class="no-print"><%=data.getInstructorEvaluationActions(edd, false, data.instructors.get(edd.courseId))%></td>
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
        <!-- Modal -->
        <div class="modal fade" id="copyModal" tabindex="-1" role="dialog" aria-labelledby="copyModalTitle" aria-hidden="true">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title" id="copyModalTitle">Creating a new session by copying a previous session</h4>
              </div>
              <div class="modal-body">
                    <form class="form" id="copyModalForm" role="form" method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_COPY%>">
                    <!-- Course -->
                    <div class="form-group">
                        <label for="modalCopiedCourseId"
                                class="control-label">Create in course</label>
                        <select class="form-control"
                            name="<%=Const.ParamsNames.COPIED_COURSE_ID%>"
                            id="modalCopiedCourseId">
                            <%
                                for (String opt : data.getCourseIdOptions())
                                    out.println(opt);
                            %>
                        </select>
                    </div>
                    <!-- Session Name -->
                    <div class="form-group">
                        <label for="modalCopiedSessionName"
                            class="control-label">Name for new session</label>
                        <input class="form-control"
                            type="text"
                            name="<%=Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME%>"
                            id="modalCopiedSessionName"
                            maxlength=<%=FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH%>
                            value="<%if (data.newFeedbackSession != null)
                                       out.print(InstructorFeedbacksPageData.sanitizeForHtml(data.newFeedbackSession.feedbackSessionName));%>"
                            placeholder="e.g. Feedback for Project Presentation 1">
                    </div>
                    <!-- Previous Session -->
                    <label>Copy sessions/questions from</label>
                    <table class="table-responsive table table-bordered table-hover margin-0" id="copyTableModal">
                        <thead class="fill-primary">
                            <th style="width:20px;">&nbsp;</th>
                            <th> Course ID </th>
                            <th> Feedback Session Name </th>
                        </thead>

                        <% for (FeedbackSessionAttributes fdb : data.existingFeedbackSessions) {
                            if(data.instructors.get(fdb.courseId).isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)){ %>
                            <tr style="cursor:pointer;">
                                <td><input type="radio"></td>
                                <td><%=fdb.courseId%></td>
                                <td>
                                    <%=InstructorFeedbacksPageData.sanitizeForHtml(fdb.feedbackSessionName)%>
                                </td>
                            </tr>
                        <%      }
                            } 
                        %>
                    </table>
                            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
                                    value="" id="modalSessionName">
                            <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>"
                                    value="" id="modalCourseId">
                            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>"
                                    value="<%=data.account.googleId%>">
                    </form>
              </div>
              <div class="modal-footer margin-0">
                <button type="button" class="btn btn-primary" id="button_copy_submit" disabled="disabled">Copy</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
              </div>
            </div>
          </div>
        </div>
        <br> <br> <br>
        <%
            if (sessionIdx == -1) {
        %>
        <div class="align-center">No records found.</div>
        <br> <br> <br>
        <%
            }
        %>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>