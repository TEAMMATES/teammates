<%@ tag description="instructorFeedbackEdit - feedback question feedback path settings" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.logic.core.Emails.EmailType" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>

<%@ attribute name="fqForm" type="teammates.ui.template.FeedbackQuestionEditForm" required="true"%>

<div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
    <div class="col-sm-12 padding-0">
        <b>Feedback Path</b> (Who is giving feedback about whom?)
    </div>
    <div class="col-sm-6 padding-0"
        data-toggle="tooltip" data-placement="top"
        title="<%= Const.Tooltips.FEEDBACK_SESSION_GIVER %>">  
        <label class="col-sm-5 control-label">
            Who will give the feedback:
        </label>
        <div class="col-sm-7">
            <select class="form-control participantSelect"
                id="<%= Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE %>${fqForm.questionNumberSuffix}"
                name="<%= Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE %>"
                <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                onchange="feedbackGiverUpdateVisibilityOptions(this)">
                <c:forEach items="${fqForm.feedbackPathSettings.giverParticipantOptions}" var="option">
                    <option ${option.attributesToString}>
                        ${option.content}
                    </option>
                </c:forEach>
            </select>
        </div>
    </div>
    <div class="col-sm-6 padding-0" data-toggle="tooltip" data-placement="top"
        title="<%= Const.Tooltips.FEEDBACK_SESSION_RECIPIENT %>">
        <label class="col-sm-5 control-label">
            Who the feedback is about:
        </label>
        <div class="col-sm-7">
            <select class="form-control participantSelect"
                id="<%= Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE %>${fqForm.questionNumberSuffix}"
                name="<%= Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE %>"
                <c:if test="${!fqForm.editable}">disabled="disabled"</c:if> onchange="feedbackRecipientUpdateVisibilityOptions(this);getVisibilityMessageIfPreviewIsActive(this);">
                <c:forEach items="${fqForm.feedbackPathSettings.recipientParticipantOptions}" var="option">
                    <option ${option.attributesToString}>
                        ${option.content}
                    </option>
                </c:forEach>
            </select>
        </div>
    </div>
    <div class="col-sm-6">
    </div>
    <div class="col-sm-6 numberOfEntitiesElements${fqForm.question.questionNumber}">
        <label id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>_text-${fqForm.question.questionNumber}" class="control-label col-sm-4 small">
            The maximum number of <span id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>_text_inner-${fqForm.question.questionNumber}"></span> each respondant should give feedback to:
        </label>
        <div class="col-sm-8 form-control-static">
            <div class="col-sm-6">
                <input class="nonDestructive" type="radio"
                    name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE %>"
                    <c:if test="${fqForm.feedbackPathSettings.numberOfEntitiesToGiveFeedbackToChecked}">checked="checked"</c:if>
                    value="custom" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>>
                <input class="nonDestructive numberOfEntitiesBox" type="number"
                    name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>"
                    id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>${fqForm.questionNumberSuffix}" 
                    value="${fqForm.feedbackPathSettings.numOfEntitiesToGiveFeedbackToValue}" 
                    min="1" max="250" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>>
            </div>
            <div class="col-sm-6">
                <input class="nonDestructive" type="radio"
                    name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE %>"
                    <c:if test="${!fqForm.feedbackPathSettings.numberOfEntitiesToGiveFeedbackToChecked}">checked="checked"</c:if>
                    value="max" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>>
                <span class="">Unlimited</span>
            </div>
        </div>
    </div>
</div>

<br>
