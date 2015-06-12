<%@ tag description="instructorFeedbacks - new feedback session form additional settings" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.logic.core.Emails.EmailType" %>

<%@ attribute name="fsForm" type="teammates.ui.template.FeedbackSessionsForm" required="true"%>
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
                            <c:if test="${fsForm.additionalSettings.sessionVisibleDateButtonChecked}">checked="checked"</c:if>
                        >
                    </div>
                    <div class="col-md-5">
                        <input class="form-control col-sm-2" type="text"
                            name="<%= Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE %>"
                            id="<%= Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE %>"
                            value="${fsForm.additionalSettings.sessionVisibleDateValue}"
                            <c:if test="${fsForm.additionalSettings.sessionVisibleDateDisabled}">disabled="disabled"</c:if>
                        >
                    </div>
                    <div class="col-md-4">
                        <select class="form-control"
                            name="<%= Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME %>"
                            id="<%= Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME %>"
                            <c:if test="${fsForm.additionalSettings.sessionVisibleDateDisabled}">disabled="disabled"</c:if>
                        >
                            <c:forEach items="${fsForm.additionalSettings.sessionVisibleTimeOptions}" var="option">
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
                            <c:if test="${fsForm.additionalSettings.sessionVisibleAtOpenChecked}">checked="checked"</c:if>
                         >
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
                            <c:if test="${fsForm.additionalSettings.sessionVisiblePrivateChecked}">checked="checked"</c:if>
                            >
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
                            <c:if test="${fsForm.additionalSettings.responseVisibleDateChecked}">checked="checked"</c:if>
                        >
                    </div>
                    <div class="col-md-5">
                        <input class="form-control"
                            type="text"
                            name="<%= Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE %>"
                            id="<%= Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE %>"
                            value="${fsForm.additionalSettings.responseVisibleDateValue}"
                            <c:if test="${fsForm.additionalSettings.responseVisibleDisabled}">disabled="disabled"</c:if>
                        >
                    </div>
                    <div class="col-md-4">
                        <select class="form-control"
                            name="<%= Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME %>"
                            id="<%= Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME %>"
                            title="<%= Const.Tooltips.FEEDBACK_SESSION_PUBLISHDATE %>"
                            data-toggle="tooltip"
                            data-placement="top"
                            <c:if test="${fsForm.additionalSettings.responseVisibleDisabled}">disabled="disabled"</c:if>
                        >
                            <c:forEach items="${fsForm.additionalSettings.responseVisibleTimeOptions}" var="option">
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
                            <c:if test="${fsForm.additionalSettings.responseVisibleImmediatelyChecked}">checked="checked"</c:if>
                        >
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
                            <c:if test="${fsForm.additionalSettings.responseVisiblePublishManuallyChecked}">checked="checked"</c:if>
                        >
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
                            <c:if test="${fsForm.additionalSettings.responseVisibleNeverChecked}">checked="checked"</c:if>
                        >
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