<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@page import="teammates.ui.controller.InstructorFeedbacksPageData"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackMcqQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackMsqQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackNumericalScaleQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackConstantSumQuestionDetails"%>
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
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css">
    <link rel="stylesheet" href="/stylesheets/datepicker.css" type="text/css">

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/js/datepicker.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>

    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbacks.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit.js"></script>

    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body onload="readyFeedbackEditPage();
    bindUncommonSettingsEvents();
    updateUncommonSettingsInfo();
    hideUncommonPanels();">
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div id="frameBodyWrapper" class="container">
            <h1>Edit Feedback Session</h1>
            <br>

            <div class="well well-plain">
                <form class="form-group" method="post"
                    action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE%>"
                    id="form_editfeedbacksession">
                    <div class="row">
                        <div class="col-sm-12">
                            <span class="pull-right">
                                <a class="btn btn-primary btn-sm" id="fsEditLink"
                                    title="<%=Const.Tooltips.FEEDBACK_SESSION_EDIT%>"
                                    data-toggle="tooltip"
                                    data-placement="top"
                                    onclick="enableEditFS()">
                                    Edit
                                </a>
                                <button type="submit" id="fsSaveLink" style="display:none;" class="btn btn-primary btn-sm" onclick="return checkEditFeedbackSession(this.form);">
                                    Save Changes
                                </button>
                                <a href="<%=data.getInstructorFeedbackSessionDeleteLink(data.session.courseId, data.session.feedbackSessionName, "")%>"
                                onclick="return toggleDeleteFeedbackSessionConfirmation('<%=data.session.courseId%>','<%=data.session.feedbackSessionName%>');"
                                title="<%=Const.Tooltips.FEEDBACK_SESSION_DELETE%>"
                                data-toggle="tooltip"
                                data-placement="top"
                                class="btn btn-primary btn-sm" id="fsDeleteLink">
                                    Delete
                                </a>
                            </span>
                        </div>
                    </div>
                    <br>
                    <div class="panel panel-primary">
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label
                                            class="col-sm-4 control-label"
                                            title="<%=Const.Tooltips.FEEDBACK_SESSION_COURSE%>"
                                            data-toggle="tooltip"
                                            data-placement="top">Course</label>
                                        <div class="col-sm-8">
                                            <div class="form-control-static">
                                                <%=InstructorFeedbackEditPageData.sanitizeForHtml(data.session.courseId)%>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <h5 class="col-sm-4">
                                            <label
                                                for="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>"
                                                class="col-sm-4 control-label"
                                                title="<%=Const.Tooltips.EVALUATION_INPUT_TIMEZONE%>"
                                                data-toggle="tooltip"
                                                data-placement="top">Timezone</label>
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
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label
                                            for="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
                                            class="col-sm-2 control-label"
                                            title="<%=Const.Tooltips.FEEDBACK_SESSION_INPUT_NAME%>"
                                            data-toggle="tooltip"
                                            data-placement="top">Session
                                            name</label>
                                        <div class="col-sm-10">
                                            <div class="form-control-static">
                                                <%=InstructorFeedbackEditPageData.sanitizeForHtml(data.session.feedbackSessionName)%>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <div class="form-group">
                                            <label
                                                for="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>"
                                                class="col-sm-2 control-label"
                                                title="<%=Const.Tooltips.FEEDBACK_SESSION_INSTRUCTIONS%>"
                                                data-toggle="tooltip"
                                                data-placement="top">Instructions</label>
                                            <div class="col-sm-10">
                                                <textarea
                                                    class="form-control"
                                                    rows="4" cols="100%"
                                                    name="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>"
                                                    id="<%=Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS%>"
                                                    ><%=InstructorFeedbacksPageData.sanitizeForHtml(data.session.instructions.getValue())
                                                    %></textarea>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="panel panel-primary">
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-md-5" title="<%=Const.Tooltips.FEEDBACK_SESSION_STARTDATE%>"
                                                data-toggle="tooltip"
                                                data-placement="top">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <label
                                                for="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
                                                class="label-control"
                                                >Submission opening time</label>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <input
                                                class="form-control col-sm-2"
                                                type="text"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
                                                value="<%=TimeHelper.formatDate(data.session.startTime)%>"
                                                placeholder="Date">
                                        </div>
                                        <div class="col-md-6">
                                            <select class="form-control"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTTIME%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTTIME%>">
                                                <%
                                                    for(String opt: data.getTimeOptionsAsHtml(data.session.startTime)) out.println(opt);
                                                %>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-5 border-left-gray" title="<%=Const.Tooltips.FEEDBACK_SESSION_ENDDATE%>"
                                                data-toggle="tooltip"
                                                data-placement="top">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <label
                                                for="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
                                                class="label-control"
                                                >Submission closing time</label>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <input
                                                class="form-control col-sm-2"
                                                type="text"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
                                                value="<%=TimeHelper.formatDate(data.session.endTime)%>"
                                                placeHolder="Date">
                                        </div>
                                        <div class="col-md-6">
                                            <select class="form-control"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDTIME%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDTIME%>">
                                                <%
                                                    for (String opt : data.getTimeOptionsAsHtml(data.session.endTime))
                                                        out.println(opt);
                                                %>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-2 border-left-gray" title="<%=Const.Tooltips.EVALUATION_INPUT_GRACEPERIOD%>"
                                                data-toggle="tooltip"
                                                data-placement="top">
                                    <div class="row">
                                        <div class="col-md-12">
                                            <label
                                                for="<%=Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD%>"
                                                class="control-label"
                                                >Grace
                                                period</label>
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
                            <a id="editUncommonSettingsButton" data-edit="[Edit]" data-done="[Done]" onclick="enableEditFS()">[Edit]</a>
                        </div>
                    </div>
                    <div class="panel panel-primary" id="sessionResponsesVisiblePanel">
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="row">
                                        <div class="col-md-6"
                                            title="<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLELABEL%>"
                                            data-toggle="tooltip"
                                            data-placement="top">
                                            <label class="label-control">Session visible from </label>
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
                                                <%if(TimeHelper.isSpecialTime(data.session.sessionVisibleFromTime) == false)
                                                    out.print("checked=\"checked\"");%>>
                                        </div>
                                        <div class="col-md-5">
                                            <input
                                                class="form-control col-sm-2"
                                                type="text"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE%>"
                                                value="<%=TimeHelper.isSpecialTime(data.session.sessionVisibleFromTime) ? "" : TimeHelper.formatDate(data.session.sessionVisibleFromTime)%>"
                                                <%if(TimeHelper.isSpecialTime(data.session.sessionVisibleFromTime))
                                                    out.print("disabled=\"disabled\"");%>>
                                        </div>
                                        <div class="col-md-4">
                                            <select class="form-control"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME%>"
                                                <%if(TimeHelper.isSpecialTime(data.session.sessionVisibleFromTime))
                                                    out.print("disabled=\"disabled\"");%>>
                                                <%
                                                    Date date = TimeHelper.isSpecialTime(
                                                            data.session.sessionVisibleFromTime) ? null
                                                            : data.session.sessionVisibleFromTime;
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
                                                for="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_atopen">Submission opening time
                                            </label>
                                            <input
                                                type="radio"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_atopen"
                                                value="<%=Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN%>"
                                                <%if(Const.TIME_REPRESENTS_FOLLOW_OPENING.equals(data.session.sessionVisibleFromTime))
                                                    out.print("checked=\"checked\"");%>>
                                        </div>
                                    </div>
                                    <div class="row radio">
                                        <div class="col-md-6"
                                            title="<%=Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLENEVER%>"
                                            data-toggle="tooltip"
                                            data-placement="top">
                                            <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_never">
                                                Never (this is a private session)
                                            </label>
                                            <input type="radio"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON%>_never"
                                                value="never"
                                                <%if(data.session.isPrivateSession()) out.print("checked=\"checked\"");%>
                                                <%if(Const.TIME_REPRESENTS_NEVER.equals(data.session.sessionVisibleFromTime))
                                                    out.print("checked=\"checked\"");%>>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6 border-left-gray">
                                    <div class="row">
                                        <div class="col-md-6"
                                            title="<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELABEL%>"
                                            data-toggle="tooltip"
                                            data-placement="top">
                                            <label class="label-control">Responses visible from</label>
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
                                                <%if(TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime) == false)
                                                    out.print("checked=\"checked\"");%>>
                                        </div>
                                        <div class="col-md-5">
                                            <input class="form-control"
                                                type="text"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE%>"
                                                value="<%=TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime) ? "" : TimeHelper.formatDate(data.session.resultsVisibleFromTime)%>"
                                                <%if(TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime))
                                                        out.print("disabled=\"disabled\"");%>
                                                >
                                        </div>
                                        <div class="col-md-4">
                                            <select class="form-control"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME%>"
                                                title="<%=Const.Tooltips.FEEDBACK_SESSION_PUBLISHDATE%>"
                                                data-toggle="tooltip" 
                                                data-placement="top"
                                                <%if(TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime))
                                                    out.print("disabled=\"disabled\"");%>>
                                                <%
                                                    date = ((TimeHelper.isSpecialTime(data.session.resultsVisibleFromTime)) ? null
                                                            : data.session.resultsVisibleFromTime);
                                                    for (String opt : data.getTimeOptionsAsHtml(date)){
                                                        out.println(opt);
                                                    }
                                                %>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="row radio">
                                        <div class="col-md-3"
                                            title="<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLEATVISIBLE%>"
                                            data-toggle="tooltip"
                                            data-placement="top">
                                            <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_atvisible">
                                                Immediately
                                            </label>
                                            <input type="radio"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_atvisible"
                                                value="<%=Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE%>"
                                                <%if(data.session!=null && Const.TIME_REPRESENTS_FOLLOW_VISIBLE.equals(data.session.resultsVisibleFromTime))
                                                    out.print("checked=\"checked\"");%>>
                                        </div>
                                    </div>
                                    <div class="row radio">
                                        <div class="col-md-4"
                                            title="<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELATER%>"
                                            data-toggle="tooltip"
                                            data-placement="top">
                                            <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_later">
                                                Publish manually
                                            </label>
                                            <input type="radio"
                                                name="resultsVisibleFromButton"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_later"
                                                value="<%=Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER%>"
                                                <%if(Const.TIME_REPRESENTS_LATER.equals(data.session.resultsVisibleFromTime) || Const.TIME_REPRESENTS_NOW.equals(data.session.resultsVisibleFromTime))
                                                    out.print("checked=\"checked\"");%>>
                                        </div>
                                    </div>
                                    <div class="row radio">
                                        <div class="col-md-2"
                                            title="<%=Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLENEVER%>"
                                            data-toggle="tooltip"
                                            data-placement="top">
                                            <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_never">
                                                Never
                                            </label>
                                            <input type="radio"
                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>"
                                                id="<%=Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON%>_never"
                                                value="<%=Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_NEVER%>"
                                                <%if(Const.TIME_REPRESENTS_NEVER.equals(data.session.resultsVisibleFromTime))
                                                    out.print("checked=\"checked\"");%>>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="panel panel-primary" id="sendEmailsForPanel">
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-md-12">
                                    <label class="control-label">
                                        Send emails for
                                    </label>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-3"
                                    title="<%=Const.Tooltips.FEEDBACK_SESSION_SENDOPENEMAIL%>"
                                    data-toggle="tooltip"
                                    data-placement="top">
                                    <div class="checkbox">
                                        <label>
                                            Session opening reminder
                                        </label>
                                        <input type="checkbox"
                                            <%=data.session.isOpeningEmailEnabled ? "checked=\"checked\"" : ""%>
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
                                        <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>_closing">
                                            Session closing reminder
                                        </label>
                                        <input
                                            type="checkbox"
                                            <%=data.session.isClosingEmailEnabled ? "checked=\"checked\"" : ""%>
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
                                        <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>_published">
                                            Results published announcement
                                        </label> 
                                        <input
                                            type="checkbox"
                                            <%=data.session.isPublishedEmailEnabled ? "checked=\"checked\"" : ""%>
                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>"
                                            id="<%=Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL%>_published"
                                            value="<%=EmailType.FEEDBACK_PUBLISHED.toString()%>">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                        <div class="col-sm-12">
                            <button type="submit" id="button_submit_edit" style="display:none;" class="btn btn-primary center-block" onclick="return checkEditFeedbackSession(this.form);">
                                Save Changes
                            </button>
                        </div>
                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.session.feedbackSessionName%>">
                    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.session.courseId%>">
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                </form>
                <br> <br>
            </div>
            <br>
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            <br>

            <%
                            if (data.questions.isEmpty()) {
            %>
                <div class="align-center bold" id="empty_message"><%=Const.StatusMessages.FEEDBACK_QUESTION_EMPTY%></div><br><br>
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
            <div class="panel panel-primary questionTable" id="questionTable<%=question.questionNumber%>">
            <div class="panel-heading">
                <div class="row">
                    <div class="col-sm-12">
                        <span>
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
                        </span>
                        <span class="pull-right">
                            <a class="btn btn-primary btn-xs" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_GETLINK%>-<%=question.questionNumber%>"
                            data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_QUESTION_GETLINK%>"
                            onclick="getQuestionLink(<%=question.questionNumber%>)">Get Link</a>
                            <a class="btn btn-primary btn-xs" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_EDITTEXT%>-<%=question.questionNumber%>"
                             data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_QUESTION_EDIT%>"
                            onclick="enableEdit(<%=question.questionNumber%>,<%=data.questions.size()%>)">Edit</a>
                            <a class="btn btn-primary btn-xs" style="display:none"
                             id="<%=Const.ParamsNames.FEEDBACK_QUESTION_SAVECHANGESTEXT%>-<%=question.questionNumber%>">Save Changes</a>
                            <a class="btn btn-primary btn-xs" onclick="deleteQuestion(<%=question.questionNumber%>)"
                             data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_QUESTION_DELETE%>">Delete</a>
                        </span>
                    </div>
                </div>
            </div>
            <div class="panel-body">
                <div>
                    <textarea rows="5"
                        class="form-control textvalue nonDestructive"
                        name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TEXT%>"
                        id="<%=Const.ParamsNames.FEEDBACK_QUESTION_TEXT%>-<%=question.questionNumber%>"
                        data-toggle="tooltip" data-placement="top"
                        title="<%=Const.Tooltips.FEEDBACK_QUESTION_INPUT_INSTRUCTIONS%>"
                        tabindex="9"
                        disabled="disabled"><%=InstructorFeedbackEditPageData.sanitizeForHtml(questionDetails.questionText)%></textarea>
                </div>
                <%=questionDetails.getQuestionSpecificEditFormHtml(question.questionNumber)%>
                <br>
                <div>
                    <div class="col-sm-6" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_SESSION_GIVER%>">  
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
                    <div class="col-sm-6" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_SESSION_RECIPIENT%>">
                        <label class="col-sm-4 control-label">
                            Feedback Recipient:
                        </label>
                        <div class="col-sm-8">
                            <select class="form-control participantSelect" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>-<%=question.questionNumber%>"
                                disabled="disabled" onchange="feedbackRecipientUpdateVisibilityOptions(this);getVisibilityMessage(this);">
                                <%
                                    for(String opt: data.getParticipantOptions(question, false)) out.println(opt);
                                %>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <br><br>
                    <div class="col-sm-6 btn-group" data-toggle="buttons">
                        <label class="btn btn-xs btn-info visibilityOptionsLabel" onchange="toggleVisibilityOptions(this)">
                            <input type="radio">
                                <span class="glyphicon glyphicon-pencil"></span> Edit Visibility
                            </input>
                        </label>
                        <label class="btn btn-xs btn-info active visibilityMessageButton" onchange="toggleVisibilityMessage(this)">
                            <input type="radio">
                                <span class="glyphicon glyphicon-eye-open"></span> Preview Visibility
                            </input>
                        </label>
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
                <div class="row">
                    <div class="col-sm-12 text-muted visibilityMessage">
                        This is the visibility as seen by the feedback giver.
                        <ul class="background-color-warning">
                        <%
                            List<String> visibilityMessage = question.getVisibilityMessage();
                            for(String message : visibilityMessage){
                        %>
                                <li><%=message%></li>
                        <%
                            }
                        %>
                        </ul>
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
                        <tr>
                            <td class="text-left">
                                <div data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT%>">
                                    Recipient(s)
                                </div>
                            </td>
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
                        <tr>
                            <td class="text-left">
                                <div data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.VISIBILITY_OPTIONS_GIVER_TEAM_MEMBERS%>">
                                    Giver's Team Members
                                </div>
                            </td>
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
                        <tr>
                            <td class="text-left">
                                <div data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT_TEAM_MEMBERS%>">
                                    Recipient's Team Members
                                </div>
                            </td>
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
                        <tr>
                            <td class="text-left">
                                <div data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.VISIBILITY_OPTIONS_OTHER_STUDENTS%>">
                                    Other students
                                </div>
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
                        <tr>
                            <td class="text-left">
                                <div data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.VISIBILITY_OPTIONS_INSTRUCTORS%>">
                                    Instructors
                                </div>
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
                        <label for="questionTypeChoice" class="control-label col-sm-3">
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
                                <option value = "CONSTSUM_OPTION"><%=Const.FeedbackQuestionTypeNames.CONSTSUM_OPTION%></option>
                                <option value = "CONSTSUM_RECIPIENT"><%=Const.FeedbackQuestionTypeNames.CONSTSUM_RECIPIENT%></option>
                                <option value = "CONSTSUM" style="display:none"></option>
                                <option value = "CONTRIB"><%=Const.FeedbackQuestionTypeNames.CONTRIB%></option>
                            </select>
                        </div>
                        <div class="col-sm-1">
                            <h5><a href="/instructorHelp.html#fbQuestionTypes" target="_blank"><span class="glyphicon glyphicon-info-sign"></span></a></h5>
                        </div>
                    </div>
                    <div class="col-sm-2">
                        <a id="button_openframe" class="btn btn-primary" value="Add New Question"
                            onclick="showNewQuestionFrame(document.getElementById('questionTypeChoice').value)">&nbsp;&nbsp;&nbsp;Add New Question&nbsp;&nbsp;&nbsp;</a>

                    </div>
                    <div class="col-sm-2">
                        <a id="button_copy" class="btn btn-primary" value="Copy Question">&nbsp;&nbsp;&nbsp;Copy Question&nbsp;&nbsp;&nbsp;</a>
                    </div>
                    <div class="col-sm-2">
                        <a class="btn btn-primary" href="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE + "?" + Const.ParamsNames.USER_ID + "=" + data.account.googleId + "&" + Const.ParamsNames.COURSE_ID + "=" + data.session.courseId%>" class="button">&nbsp;&nbsp;&nbsp;Done Editing&nbsp;&nbsp;&nbsp;</a>
                    </div>
                </div>
            </div>

            <div class="panel panel-primary questionTable" id="questionTableNew" style="display:none;">
                <div class="panel-heading">
                    <strong>Question</strong>
                    <select class="questionNumber nonDestructive text-primary" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>">
                    <%
                        for(int opt = 1; opt < data.questions.size()+2; opt++){
                            out.println("<option value=" + opt +">" + opt + "</option>");
                        }
                    %>
                    </select>
                    &nbsp;
                    <span id="questionTypeHeader"></span>
                    <span class="pull-right">
                        <a class="btn btn-primary btn-xs" onclick="deleteQuestion(-1)" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_QUESTION_DELETE%>">Delete
                        </a>
                    </span>
                </div>
                <div class="panel-body">
                    <div>
                        <textarea rows="5"
                            class="form-control textvalue nonDestructive"
                            name="questiontext"
                            id="questiontext"
                            data-toggle="tooltip" data-placement="top" title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?"
                            tabindex="9"
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
                    <div id="constSumForm">
                        <%
                            FeedbackConstantSumQuestionDetails fConstSumQd = new FeedbackConstantSumQuestionDetails();
                            fConstSumQd.numOfConstSumOptions = 2;
                            fConstSumQd.constSumOptions.add("");
                            fConstSumQd.constSumOptions.add("");
                        %>
                        <%=fConstSumQd.getQuestionSpecificEditFormHtml(-1)%>
                    </div>
                    <br>
                    <div>
                        <div class="col-sm-6" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_SESSION_GIVER%>">
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
                        <div class="col-sm-6" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_SESSION_RECIPIENT%>">
                            <label class="col-sm-4 control-label">
                                Feedback Recipient:
                            </label>
                            <div class="col-sm-8">
                                <select class="form-control participantSelect" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>" id="<%=Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE%>" onchange="feedbackRecipientUpdateVisibilityOptions(this);getVisibilityMessage(this);">
                                    <%
                                        for(String opt: data.getParticipantOptions(null, false)) out.println(opt);
                                    %>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <br><br>
                        <div class="col-sm-6 btn-group" data-toggle="buttons">
                            <label class="btn btn-xs btn-info visibilityOptionsLabel" onchange="toggleVisibilityOptions(this)">
                                <input type="radio">
                                    <span class="glyphicon glyphicon-pencil"></span> Edit Visibility
                                </input>
                            </label>
                            <label class="btn btn-xs btn-info active visibilityMessageButton" onchange="toggleVisibilityMessage(this)">
                                <input type="radio">
                                    <span class="glyphicon glyphicon-eye-open"></span> Preview Visibility
                                </input>
                            </label>
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
                    <div class="row">
                        <div class="col-sm-12 text-muted visibilityMessage">

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
                            <tr>
                                <td class="text-left">
                                    <div data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT%>">
                                        Recipient(s)
                                    </div>
                                </td>
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
                            <tr>
                                <td class="text-left">
                                    <div data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.VISIBILITY_OPTIONS_GIVER_TEAM_MEMBERS%>">
                                        Giver's Team Members
                                    </div>
                                </td>
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
                            <tr>
                                <td class="text-left">
                                    <div data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT_TEAM_MEMBERS%>">
                                        Recipient's Team Members
                                    </div>
                                </td>
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
                            <tr>
                                <td class="text-left">
                                    <div data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.VISIBILITY_OPTIONS_OTHER_STUDENTS%>">
                                        Other students
                                    </div>
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
                            <tr>
                                <td class="text-left">
                                    <div data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.VISIBILITY_OPTIONS_INSTRUCTORS%>">
                                        Instructors
                                    </div>
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

        <!-- Modal -->
        <div class="modal fade" id="copyModal" tabindex="-1" role="dialog" aria-labelledby="copyModalTitle" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                        <h4 class="modal-title" id="copyModalTitle">Copy Questions</h4>
                    </div>
                <div class="modal-body padding-0">
                    <form class="form" id="copyModalForm" role="form" method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_COPY%>">
                        <!-- Previous Questions -->
                        <table class="table-responsive table table-hover table-bordered margin-0" id="copyTableModal">
                            <thead class="fill-primary">
                                <th style="width:30px;">&nbsp;</th>
                                <th onclick="toggleSort(this,2);" id="button_sortid" class="button-sort-ascending"> 
                                    Course ID <span class="icon-sort sorted-ascending"></span>
                                </th>
                                <th onclick="toggleSort(this,3);" id="button_sortfsname" class="button-sort-none" style="width:17%;">
                                    Session Name <span class="icon-sort unsorted"></span>
                                </th>
                                <th onclick="toggleSort(this,4);" id="button_sortfqtype" class="button-sort-none"> 
                                    Question Type <span class="icon-sort unsorted"></span>
                                </th>
                                <th onclick="toggleSort(this,5);" id="button_sortfqtext" class="button-sort-none"> 
                                    Question Text <span class="icon-sort unsorted"></span>
                                </th>
                            </thead>

                            <% 
                                if(data.instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)){
                                    for (FeedbackQuestionAttributes question : data.copiableQuestions) {
                                        FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
                            %>
                                <tr style="cursor:pointer;">
                                    <td><input type="checkbox"></td>
                                    <td><%=question.courseId%></td>
                                    <td><%=InstructorFeedbacksPageData.sanitizeForHtml(question.feedbackSessionName)%></td>
                                    <td><%= questionDetails.getQuestionTypeDisplayName() %></td>
                                    <td><%= questionDetails.questionText %></td>
                                    <input type="hidden" value="<%= question.getId() %>">
                                </tr>
                            <%      }
                                }
                            %>
                        </table>
                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.session.feedbackSessionName%>">
                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.session.courseId%>">
                    </form>
                </div>
                <div class="modal-footer margin-0">
                    <button type="button" class="btn btn-primary" id="button_copy_submit" disabled="disabled">Copy</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                </div>
            </div>
          </div>
        </div>

        <br><br>
        <div class="container">
            <div class="well well-plain inputTable" id="questionPreviewTable">
                <div class="row">
                    <form class="form-horizontal">
                        <label class="control-label col-sm-2 text-right">
                            Preview Session:
                        </label>
                    </form>
                    <div class="col-sm-5" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_PREVIEW_ASSTUDENT%>">
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
                    <div class="col-sm-5" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_PREVIEW_ASINSTRUCTOR%>">
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
        </div>
        <br><br>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>