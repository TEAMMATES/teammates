<%@ tag description="instructorFeedbacks - new feedback session form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.logic.core.Emails.EmailType" %>

<%@ attribute name="newForm" type="teammates.ui.template.FeedbackSessionsNewForm" required="true"%>
<div class="well well-plain">
    <form class="form-group" method="post"
        action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD %>"
        name="form_addfeedbacksession">
        <div class="row">
            <h4 class="label-control col-md-2 text-md">Create new </h4>
            <div class="col-md-5">
                <div class="col-md-10" title="Select a session type here."
                    data-toggle="tooltip" data-placement="top">
                    <select class="form-control"
                        name="<%= Const.ParamsNames.FEEDBACK_SESSION_TYPE %>"
                        id="<%= Const.ParamsNames.FEEDBACK_SESSION_TYPE %>">
                        <c:forEach items="${newForm.feedbackSessionTypeOptions}" var="option">
                            <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                ${option.content}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-1">
                    <h5>
                        <a href="/instructorHelp.html#fbSetupSession" target="_blank">
                            <span class="glyphicon glyphicon-info-sign"></span>
                        </a>
                    </h5>
                </div>
            </div>
            <h4 class="label-control col-md-1 text-md">Or: </h4>
            <div class="col-md-3">
                <a id="button_copy" class="btn btn-info" style="vertical-align:middle;">Loading...</a>
            </div>
        </div>
        <br>

        <div class="panel panel-primary">
            <div class="panel-body">
                <div class="row">
                    <div class="col-md-6"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_COURSE %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="${newForm.formClasses}">
                            <h5 class="col-sm-4">
                                <label class="control-label"
                                    for="<%= Const.ParamsNames.COURSE_ID %>">
                                    Course ID
                                </label>
                            </h5>
                            <div class="col-sm-8">
                                <select class="${newForm.courseFieldClasses}"
                                    name="<%= Const.ParamsNames.COURSE_ID %>"
                                    id="<%= Const.ParamsNames.COURSE_ID %>">
                                    
                                    <c:forEach items="${newForm.coursesSelectField}" var="option">
                                        <option <c:forEach items="${option.attributes}" var="attr"
                                        > ${attr.key}="${attr.value}"</c:forEach> >${option.content}</option>
                                    </c:forEach>
                                    
                                    
                                </select>

                            </div>
                        </div>
                    </div>
                    <div class="col-md-6"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_INPUT_TIMEZONE %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="form-group">
                            <h5 class="col-sm-4">
                                <label class="control-label"
                                    for="<%= Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE %>">
                                    Timezone
                                </label>
                            </h5>
                            <div class="col-sm-8">
                                <select class="form-control"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE %>">
                                    <c:forEach items="${newForm.timezoneSelectField}" var="option">
                                        <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                            ${option.content}
                                        </option>
                                    </c:forEach>
                                    
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
                <br>
                <div class="row">
                    <div class="col-md-12"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_INPUT_NAME %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="form-group">
                            <h5 class="col-sm-2">
                                <label class="control-label"
                                    for="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>">
                                    Session name
                                </label>
                            </h5>
                            <div class="col-sm-10">
                                <input class="form-control" type="text"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>"
                                    maxlength=<%= FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH %>
                                    placeholder="e.g. Feedback for Project Presentation 1"
                                    value="${newForm.fsName}">
                            </div>
                        </div>
                    </div>
                </div>
                <br>
                <div class="row" id="instructionsRow">
                    <div class="col-md-12"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_INSTRUCTIONS %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="form-group">
                            <h5 class="col-sm-2">
                                <label class="control-label"
                                    for="<%= Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS %>">
                                    Instructions
                                </label>
                            </h5>
                            <div class="col-sm-10">
                                <textarea class="form-control"
                                    rows="4" cols="100%"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS %>"
                                    placeholder="e.g. Please answer all the given questions.">${newForm.instructions}</textarea>
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
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_STARTDATE %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="row">
                            <div class="col-md-6">
                                <label class="label-control"
                                    for="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTDATE %>">
                                    Submission opening time
                                </label>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <input class="form-control col-sm-2" type="text"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTDATE %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTDATE %>"
                                    value="${newForm.fsStartDate}"
                                    placeholder="Date">
                            </div>
                            <div class="col-md-6">
                                <select class="form-control"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTTIME %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTTIME %>">
                                     <c:forEach items="${newForm.fsStartTimeOptions}" var="option">
                                        <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                            ${option.content}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-5 border-left-gray"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_ENDDATE %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="row">
                            <div class="col-md-6">
                                <label class="label-control"
                                    for="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDDATE %>">
                                    Submission closing time
                                </label>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <input class="form-control col-sm-2" type="text"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDDATE %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDDATE %>"
                                    value="${newForm.fsEndDate}"
                                    placeHolder="Date">
                            </div>
                            <div class="col-md-6">
                                <select class="form-control"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDTIME %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDTIME %>">
                                    <c:forEach items="${newForm.fsEndTimeOptions}" var="option">
                                        <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                            ${option.content}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-2 border-left-gray"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_INPUT_GRACEPERIOD %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="row">
                            <div class="col-md-12">
                                <label class="control-label"
                                    for="<%= Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD %>">
                                    Grace period
                                </label>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-sm-12">
                                <select class="form-control"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD %>">
                                    <c:forEach items="${newForm.gracePeriodOptions}" var="option">
                                        <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                            ${option.content}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row" id="uncommonSettingsInfo">
            <div class="col-md-12 text-muted">
                <span id="uncommonSettingsInfoText"></span>
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
                                title="<%= Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLELABEL %>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <label class="label-control">Session visible from </label>
                            </div>
                        </div>
                        <div class="row radio"><%
                                
                            %>
                            <div class="col-md-2"
                                title="<%= Const.Tooltips.FEEDBACK_SESSION_VISIBLEDATE %>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>_custom">
                                    At
                                </label>
                                <input type="radio"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>_custom"
                                    value="<%= Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_CUSTOM %>"
                                    ${newForm.sessionVisibleDateButtonCheckedAttribute} >
                            </div>
                            <div class="col-md-5">
                                <input class="form-control col-sm-2" type="text"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE %>"
                                    value="${newForm.sessionVisibleDateValue}"
                                    ${newForm.sessionVisibleDateDisabledAttribute} >
                            </div>
                            <div class="col-md-4">
                                <select class="form-control"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME %>"
                                    ${newForm.sessionVisibleDateDisabledAttribute}>
                                    <c:forEach items="${newForm.sessionVisibleTimeOptions}" var="option">
                                        <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                            ${option.content}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row radio">
                            <div class="col-md-6"
                                title="<%= Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLEATOPEN %>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>_atopen">
                                    Submission opening time
                                </label>
                                <input type="radio"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>_atopen"
                                    value="<%= Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN %>"
                                    ${newForm.sessionVisibleAtOpenCheckedAttribute}>
                            </div>
                        </div>
                        <div class="row radio">
                            <div class="col-md-6"
                                title="<%= Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLENEVER %>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>_never">
                                    Never (this is a private session)
                                </label>
                                <input type="radio"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>_never"
                                    value="never"
                                    ${newForm.sessionVisiblePrivateCheckedAttribute}>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6 border-left-gray" id="responsesVisibleFromColumn">
                        <div class="row">
                            <div class="col-md-6"
                                title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELABEL %>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <label class="label-control">Responses visible from</label>
                            </div>
                        </div>
                        <div class="row radio"><%
                            %>
                            <div class="col-md-2"
                                title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLECUSTOM %>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_custom">
                                    At
                                </label>
                                <input type="radio"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_custom"
                                    value="<%= Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_CUSTOM %>"
                                    ${newForm.responseVisibleDateCheckedAttribute}>
                            </div>
                            <div class="col-md-5">
                                <input class="form-control"
                                    type="text"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE %>"
                                    value="${newForm.responseVisibleDateValue}"
                                    ${newForm.responseVisibleDisabledAttribute} >
                            </div>
                            <div class="col-md-4">
                                <select class="form-control"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME %>"
                                    title="<%= Const.Tooltips.FEEDBACK_SESSION_PUBLISHDATE %>"
                                    data-toggle="tooltip"
                                    data-placement="top"
                                    ${newForm.responseVisibleDisabledAttribute}>
                                    <c:forEach items="${newForm.responseVisibleTimeOptions}" var="option">
                                        <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                            ${option.content}
                                        </option>
                                    </c:forEach>

                                </select>
                            </div>
                        </div>
                        <div class="row radio">
                            <div class="col-md-3"
                                title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLEATVISIBLE %>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_atvisible">
                                    Immediately
                                </label>
                                <input type="radio"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_atvisible"
                                    value="<%= Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE %>"
                                    ${newForm.responseVisibleImmediatelyCheckedAttribute }>
                            </div>
                        </div>
                        <div class="row radio">
                            <div class="col-md-4"
                                title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELATER %>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_later">
                                    Publish manually
                                </label>
                                <input type="radio" name="resultsVisibleFromButton"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_later"
                                    value="<%= Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER %>"
                                    ${newForm.responseVisiblePublishManuallyCheckedAttribute }>
                            </div>
                        </div>
                        <div class="row radio">
                            <div class="col-md-2"
                                title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLENEVER %>"
                                data-toggle="tooltip"
                                data-placement="top">
                                <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_never">
                                    Never
                                </label>
                                <input type="radio"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_never"
                                    value="<%= Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_NEVER %>"
                                    ${newForm.responseVisibleNeverCheckedAttribute}>
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
                        <label class="control-label">Send emails for</label>
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-3"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_SENDOPENEMAIL %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="checkbox">
                            <label>Session opening reminder</label>
                            <input type="checkbox" checked="checked"
                                name="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>"
                                id="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>_open"
                                value="<%= EmailType.FEEDBACK_OPENING.toString() %>" disabled="disabled">
                        </div>
                    </div>
                    <div class="col-sm-3"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_SENDCLOSINGEMAIL %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="checkbox">
                            <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>_closing">
                                Session closing reminder
                            </label>
                            <input type="checkbox" checked="checked"
                                name="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>"
                                id="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>_closing"
                                value="<%= EmailType.FEEDBACK_CLOSING.toString() %>">
                        </div>
                    </div>
                    <div class="col-sm-4"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_SENDPUBLISHEDEMAIL %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="checkbox">
                            <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>_published">
                                Results published announcement
                            </label>
                            <input type="checkbox" checked="checked"
                                name="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>"
                                id="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>_published"
                                value="<%= EmailType.FEEDBACK_PUBLISHED.toString() %>">
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="form-group">
            <div class="row">
                <div class="col-md-offset-5 col-md-3">
                    <button id="button_submit" type="submit" class="btn btn-primary"
                        ${newForm.submitButtonDisabledAttribute}>
                        Create Feedback Session
                    </button>
                </div>
            </div>
        </div>
        <c:if test="${empty data.newForm.courses}"> 
            <div class="row">
                <div class="col-md-12 text-center">
                    <b>You need to have an active(unarchived) course to create a session!</b>
                </div>
            </div>
        </c:if>
        <input type="hidden"
            name="<%= Const.ParamsNames.USER_ID %>"
            value="${data.account.googleId}">
    </form>
    <form style="display:none;" id="ajaxForSessions" class="ajaxForSessionsForm"
        action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE %>">
        <input type="hidden"
            name="<%= Const.ParamsNames.USER_ID %>"
            value="${data.account.googleId}">
        <input type="hidden"
            name="<%= Const.ParamsNames.IS_USING_AJAX %>"
            value="on">
        <c:if test="${ data.newForm.feedbackSessionNameForSessionList != null && data.newForm.courseIdForNewSession != null }">
            <input type="hidden"
                name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>"
                value="${data.newForm.feedbackSessionNameForSessionList }">
            <input type="hidden"
                name="<%= Const.ParamsNames.COURSE_ID %>"
                value="${ data.newForm.courseIdForNewSession }">
        </c:if>
    </form>
</div>