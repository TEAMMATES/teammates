<%@ tag description="instructorFeedback & instructorFeedbackEdit - feedback session form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="feedbacks" %>

<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.logic.core.Emails.EmailType" %>

<%@ attribute name="fsForm" type="teammates.ui.template.FeedbackSessionsForm" required="true"%>

<div class="well well-plain">
    <form class="form-group" method="post"
        action="${fsForm.formSubmitAction}"
        id="form_feedbacksession">
        
        <feedbacks:feedbackSessionsFormHeader fsForm="${fsForm}" />
        
        <br>
        <div class="panel panel-primary">
            <div class="panel-body">
                <div class="row">
                    <div class="col-md-6"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_COURSE %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="form-group<c:if test="${fsForm.showNoCoursesMessage}"> has-error</c:if>">
                            <h5 class="col-sm-4">
                                <label class="control-label"
                                    for="<%= Const.ParamsNames.COURSE_ID %>">
                                    Course ID
                                </label>
                            </h5>
                            <div class="col-sm-8">
                                <c:choose>
                                    <c:when test="${fsForm.courseIdEditable}">
                                        <select class="form-control<c:if test="${fsForm.showNoCoursesMessage}"> text-color-red</c:if>"
                                            name="<%= Const.ParamsNames.COURSE_ID %>"
                                            id="<%= Const.ParamsNames.COURSE_ID %>">
                                            <c:forEach items="${fsForm.coursesSelectField}" var="option">
                                                <option ${option.attributesToString}>${option.content}</option>
                                            </c:forEach>
                                        </select>
                                    </c:when>
                                    <c:otherwise>
                                            <div class="form-control-static">
                                                    ${fsForm.courseId}
                                            </div>
                                    </c:otherwise>
                                </c:choose>
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
                                    <c:forEach items="${fsForm.timezoneSelectField}" var="option">
                                        <option ${option.attributesToString}>
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
                                <c:choose>
                                    <c:when test="${fsForm.fsNameEditable}">
                                        <input class="form-control" type="text"
                                            name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>"
                                            id="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>"
                                            maxlength="<%= FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH %>"
                                            placeholder="e.g. Feedback for Project Presentation 1"
                                            value="${fsForm.fsName}">
                                    </c:when>
                                    <c:otherwise>
                                            <div class="form-control-static">
                                                ${fsForm.fsName}
                                            </div>
                                    </c:otherwise>
                                </c:choose>
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
                                    placeholder="e.g. Please answer all the given questions.">${fsForm.instructions}</textarea>
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
                                    value="${fsForm.fsStartDate}"
                                    placeholder="Date">
                            </div>
                            <div class="col-md-6">
                                <select class="form-control"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTTIME %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTTIME %>">
                                     <c:forEach items="${fsForm.fsStartTimeOptions}" var="option">
                                        <option ${option.attributesToString}>
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
                                    value="${fsForm.fsEndDate}"
                                    placeHolder="Date">
                            </div>
                            <div class="col-md-6">
                                <select class="form-control"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDTIME %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDTIME %>">
                                    <c:forEach items="${fsForm.fsEndTimeOptions}" var="option">
                                        <option ${option.attributesToString}>
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
                                    <c:forEach items="${fsForm.gracePeriodOptions}" var="option">
                                        <option ${option.attributesToString}>
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
                <a id="editUncommonSettingsButton" data-edit="[Edit]" data-done="[Done]" ${fsForm.editButtonTags.attributesToString}>[Edit]</a>
                <br><br>
            </div>
        </div>
        
        <feedbacks:feedbackSessionsFormAdditionalSettings additionalSettings="${fsForm.additionalSettings}"/>
        
        <div class="form-group">
            <div class="row">
                <div class="col-md-offset-5 col-md-3">
                    <button id="button_submit" type="submit" class="btn btn-primary"
                        <c:if test="${fsForm.submitButtonDisabled}">disabled="disabled"</c:if>
                        <c:if test="${!fsForm.submitButtonVisible}"> style="display:none;" </c:if>
                    >
                            ${fsForm.submitButtonText}
                    </button>
                </div>
            </div>
        </div>
        <c:if test="${fsForm.showNoCoursesMessage}"> 
            <div class="row">
                <div class="col-md-12 text-center">
                    <b>You need to have an active(unarchived) course to create a session!</b>
                </div>
            </div>
        </c:if>
        <c:if test="${!fsForm.fsNameEditable}">
            <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${fsForm.fsName}">
        </c:if>
        <c:if test="${!fsForm.courseIdEditable}">
            <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${fsForm.courseId}">
        </c:if>
        <input type="hidden"
            name="<%= Const.ParamsNames.USER_ID %>"
            value="${data.account.googleId}">
    </form>
</div>