<%@ tag description="instructorFeedbacks - feedback question settings common to all types" %>
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
                disabled="disabled"
                onchange="feedbackGiverUpdateVisibilityOptions(this)">
                <c:forEach items="${fqForm.generalSettings.giverParticipantOptions}" var="option">
                    <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
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
                disabled="disabled" onchange="feedbackRecipientUpdateVisibilityOptions(this);getVisibilityMessageIfPreviewIsActive(this);">
                <c:forEach items="${fqForm.generalSettings.recipientParticipantOptions}" var="option">
                    <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                        ${option.content}
                    </option>
                </c:forEach>
            </select>
        </div>
    </div>
    <div class="col-sm-6">
    </div>
    <div class="col-sm-6 numberOfEntitiesElements${fqForm.question.questionNumber}">
        <label id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>_text${fqForm.questionNumberSuffix}" class="control-label col-sm-4 small">
            The maximum number of <span id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>_text_inner-${fqForm.question.questionNumber}"></span> each respondant should give feedback to:
        </label>
        <div class="col-sm-8 form-control-static">
            <div class="col-sm-6">
                <input class="nonDestructive" type="radio"
                    name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE %>"
                    <c:if test="isNumberOfEntitiesToGiveFeedbackToChecked">checked="checked"</c:if>
                    value="custom" disabled="disabled">
                <input class="nonDestructive numberOfEntitiesBox" type="number"
                    name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>"
                    id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>${fqForm.questionNumberSuffix}" 
                    value="${fqForm.generalSettings.numOfEntitiesToGiveFeedbackToValue}" 
                    min="1" max="250" disabled="disabled">
            </div>
            <div class="col-sm-6">
                <input class="nonDestructive" type="radio"
                    name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE %>"
                    <c:if test="${!isNumberOfEntitiesToGiveFeedbackToChecked}">checked="checked"</c:if>
                    value="max" disabled="disabled">
                <span class="">Unlimited</span>
            </div>
        </div>
    </div>
</div>
<br>
<div class="col-sm-12 padding-15px background-color-light-green">
    <div class="col-sm-12 padding-0">
        <b>Visibility</b> (Who can see the responses?)
    </div>
    <div class="col-sm-6 btn-group" data-toggle="buttons">
        <label class="btn btn-xs btn-info visibilityOptionsLabel"
            id="visibilityOptionsLabel${fqForm.questionNumberSuffix}"
            onchange="toggleVisibilityOptions(this)">
            <input type="radio">
            <span class="glyphicon glyphicon-pencil"></span> Edit Visibility
        </label>
        <label class="btn btn-xs btn-info active visibilityMessageButton" id="visibilityMessageButton${fqForm.questionNumberSuffix}" onchange="toggleVisibilityMessage(this)">
            <input type="radio">
            <span class="glyphicon glyphicon-eye-open"></span> Preview Visibility
        </label>
    </div>
</div>
<div class="col-sm-12 background-color-light-green">
    <div class="col-sm-12 text-muted visibilityMessage" id="visibilityMessage${fqForm.questionNumberSuffix}">
        This is the visibility as seen by the feedback giver.
        <ul class="background-color-warning">
        <c:forEach items="${fqForm.generalSettings.visibilityMessages}" var="msg">
            <li>${msg}</li>
        </c:forEach>
        </ul>
    </div>
</div>
<div class="col-sm-12 margin-bottom-15px background-color-light-green">
    <div class="visibilityOptions" id="visibilityOptions${fqForm.questionNumberSuffix}">
        <table class="dataTable participantTable table table-striped text-center background-color-white">
            <tr>
                <th class="text-center">User/Group</th>
                <th class="text-center">Can see answer</th>
                <th class="text-center">Can see giver's name</th>
                <th class="text-center">Can see recipient's name</th>
            </tr>
            <tr>
                <td class="text-left">
                    <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT %>">
                        Recipient(s)
                    </div>
                </td>
                <td>
                    <input class="visibilityCheckbox answerCheckbox${fqForm.question.questionNumber} centered"
                        name="receiverLeaderCheckbox" type="checkbox"
                        value="<%= FeedbackParticipantType.RECEIVER %>" disabled="disabled"
                        <c:if test="${fqForm.generalSettings.responseVisibleFor['RECEIVER']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.RECEIVER %>" disabled="disabled"
                        <c:if test="${fqForm.generalSettings.giverNameVisibleFor['RECEIVER']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}"
                        name="receiverFollowerCheckbox" type="checkbox"
                        value="<%= FeedbackParticipantType.RECEIVER %>" disabled="disabled"
                        <c:if test="${fqForm.generalSettings.recipientNameVisibleFor['RECEIVER']}"> checked="checked"</c:if> >
                </td>
            </tr>
            <tr>
                <td class="text-left">
                    <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_GIVER_TEAM_MEMBERS %>">
                        Giver's Team Members
                    </div>
                </td>
                <td>
                    <input class="visibilityCheckbox answerCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>" disabled="disabled"
                        <c:if test="${fqForm.generalSettings.responseVisibleFor['OWN_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>" disabled="disabled"
                        <c:if test="${fqForm.generalSettings.giverNameVisibleFor['OWN_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>" disabled="disabled"
                        <c:if test="${fqForm.generalSettings.recipientNameVisibleFor['OWN_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                </td>
            </tr>
            <tr>
                <td class="text-left">
                    <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT_TEAM_MEMBERS %>">
                        Recipient's Team Members
                    </div>
                </td>
                <td>
                    <input class="visibilityCheckbox answerCheckbox${fqForm.question.questionNumber}" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>" disabled="disabled"
                    <c:if test="${fqForm.generalSettings.responseVisibleFor['RECEIVER_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>" disabled="disabled"
                    <c:if test="${fqForm.generalSettings.giverNameVisibleFor['RECEIVER_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>" disabled="disabled"
                    <c:if test="${fqForm.generalSettings.recipientNameVisibleFor['RECEIVER_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                </td>
            </tr>
            <tr>
                <td class="text-left">
                    <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_OTHER_STUDENTS %>">
                        Other students
                    </div>
                </td>
                <td>
                    <input class="visibilityCheckbox answerCheckbox${fqForm.question.questionNumber}"
                    type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>" disabled="disabled"
                    <c:if test="${fqForm.generalSettings.responseVisibleFor['STUDENTS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}"
                    type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>" disabled="disabled"
                    <c:if test="${fqForm.generalSettings.giverNameVisibleFor['STUDENTS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}"
                    type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>" disabled="disabled"
                    <c:if test="${fqForm.generalSettings.recipientNameVisibleFor['STUDENTS']}"> checked="checked"</c:if> >
                </td>
            </tr>
            <tr>
                <td class="text-left">
                    <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_INSTRUCTORS %>">
                        Instructors
                    </div>
                </td>
                <td>
                    <input class="visibilityCheckbox answerCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>" disabled="disabled"
                    <c:if test="${fqForm.generalSettings.responseVisibleFor['INSTRUCTORS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>" disabled="disabled"
                    <c:if test="${fqForm.generalSettings.giverNameVisibleFor['INSTRUCTORS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>" disabled="disabled"
                    <c:if test="${fqForm.generalSettings.recipientNameVisibleFor['INSTRUCTORS']}"> checked="checked"</c:if> >
                </td>
            </tr>
        </table>
    </div>
</div>