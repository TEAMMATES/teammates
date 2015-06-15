<%@ tag description="instructorFeedbacks - feedback sessions table/list" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.logic.core.Emails.EmailType" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>

<%@ attribute name="fqForm" type="teammates.ui.template.FeedbackQuestionEditForm" required="true"%>

<form class="form-horizontal form_question" role="form" method="post"
    action="${fqForm.action}"
    id="form_editquestion-${fqForm.question.questionNumber}" name="form_editquestions"
    onsubmit="tallyCheckboxes(${fqForm.question.questionNumber})"
    ${ fqForm.questionHasResponses ? "editStatus=\"hasResponses\"" : "" }>
    <div class="panel panel-primary questionTable" id="questionTable${fqForm.question.questionNumber}">
        <div class="panel-heading">
            <div class="row">
                <div class="col-sm-12">
                    <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary"
                            name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBER %>"
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBER %>-${fqForm.question.questionNumber}">
                            <c:forEach items="${fqForm.questionNumberOptions}" var="option">
                                <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                    ${option.content}
                                </option>
                            </c:forEach>
                        </select>
                        &nbsp;${fqForm.questionDetails.questionTypeDisplayName}
                    </span>
                    <span class="pull-right">
                        <a class="btn btn-primary btn-xs"
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_GETLINK %>-${fqForm.question.questionNumber}"
                            data-toggle="tooltip" data-placement="top"
                            title="<%= Const.Tooltips.FEEDBACK_QUESTION_GETLINK %>"
                            onclick="getQuestionLink(${fqForm.question.questionNumber})">
                            Get Link
                        </a>
                        <a class="btn btn-primary btn-xs"
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_EDITTEXT %>-${fqForm.question.questionNumber}"
                            data-toggle="tooltip" data-placement="top"
                            title="<%= Const.Tooltips.FEEDBACK_QUESTION_EDIT %>"
                            onclick="enableEdit(${fqForm.question.questionNumber},${fqForm.numOfQuestionsOnPage})">
                            Edit
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none"
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_SAVECHANGESTEXT %>-${fqForm.question.questionNumber}">
                            Save Changes
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none"
                            onclick="cancelEdit(${fqForm.question.questionNumber})"
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_CANCELEDIT %>-${fqForm.question.questionNumber}"
                            data-toggle="tooltip" data-placement="top"
                            title="<%= Const.Tooltips.FEEDBACK_QUESTION_CANCEL %>">
                            Cancel
                        </a>
                        <a class="btn btn-primary btn-xs"
                            onclick="deleteQuestion(${fqForm.question.questionNumber})"
                            data-toggle="tooltip" data-placement="top"
                            title="<%= Const.Tooltips.FEEDBACK_QUESTION_DELETE %>">
                            Delete
                        </a>
                    </span>
                </div>
            </div>
        </div>
        <div class="panel-body">
            <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-blue">
                <div>
                    <!-- Do not add whitespace between the opening and closing tags-->
                    <textarea class="form-control textvalue nonDestructive" rows="5"
                        name="<%= Const.ParamsNames.FEEDBACK_QUESTION_TEXT %>"
                        id="<%= Const.ParamsNames.FEEDBACK_QUESTION_TEXT %>-${fqForm.question.questionNumber}"
                        data-toggle="tooltip" data-placement="top"
                        title="<%= Const.Tooltips.FEEDBACK_QUESTION_INPUT_INSTRUCTIONS %>"
                        tabindex="9"
                        disabled="disabled">${fqForm.questionText}</textarea>
                </div>
                ${fqForm.questionSpecificEditFormHtml}
            </div>
            <br>
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
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE %>-${fqForm.question.questionNumber}"
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
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE %>-${fqForm.question.questionNumber}"
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
                    <label id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>_text-${fqForm.question.questionNumber}" class="control-label col-sm-4 small">
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
                                id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>-${fqForm.question.questionNumber}" 
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
                        id="visibilityOptionsLabel-${fqForm.question.questionNumber}"
                        onchange="toggleVisibilityOptions(this)">
                        <input type="radio">
                        <span class="glyphicon glyphicon-pencil"></span> Edit Visibility
                    </label>
                    <label class="btn btn-xs btn-info active visibilityMessageButton" id="visibilityMessageButton-${fqForm.question.questionNumber}" onchange="toggleVisibilityMessage(this)">
                        <input type="radio">
                        <span class="glyphicon glyphicon-eye-open"></span> Preview Visibility
                    </label>
                </div>
            </div>
            <div class="col-sm-12 background-color-light-green">
                <div class="col-sm-12 text-muted visibilityMessage" id="visibilityMessage-${fqForm.question.questionNumber}">
                    This is the visibility as seen by the feedback giver.
                    <ul class="background-color-warning">
                    <c:forEach items="${fqForm.generalSettings.visibilityMessages}" var="msg">
                        <li>${msg}</li>
                    </c:forEach>
                    </ul>
                </div>
            </div>
            <div class="col-sm-12 margin-bottom-15px background-color-light-green">
                <div class="visibilityOptions" id="visibilityOptions-${fqForm.question.questionNumber}">
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
                                    <c:if test="${fqForm.generalSettings.isResponseVisible['RECEIVER']}"> checked="checked"</c:if> >
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}"
                                    type="checkbox" value="<%= FeedbackParticipantType.RECEIVER %>" disabled="disabled"
                                    <c:if test="${fqForm.generalSettings.isGiverNameVisible['RECEIVER']}"> checked="checked"</c:if> >
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}"
                                    name="receiverFollowerCheckbox" type="checkbox"
                                    value="<%= FeedbackParticipantType.RECEIVER %>" disabled="disabled"
                                    <c:if test="${fqForm.generalSettings.isRecipientNameVisible['RECEIVER']}"> checked="checked"</c:if> >
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
                                    <c:if test="${fqForm.generalSettings.isResponseVisible['OWN_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}"
                                    type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>" disabled="disabled"
                                    <c:if test="${fqForm.generalSettings.isGiverNameVisible['OWN_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}"
                                    type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>" disabled="disabled"
                                    <c:if test="${fqForm.generalSettings.isRecipientNameVisible['OWN_TEAM_MEMBERS']}"> checked="checked"</c:if> >
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
                                <c:if test="${fqForm.generalSettings.isResponseVisible['RECEIVER_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>" disabled="disabled"
                                <c:if test="${fqForm.generalSettings.isGiverNameVisible['RECEIVER_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>" disabled="disabled"
                                <c:if test="${fqForm.generalSettings.isRecipientNameVisible['RECEIVER_TEAM_MEMBERS']}"> checked="checked"</c:if> >
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
                                <c:if test="${fqForm.generalSettings.isResponseVisible['STUDENTS']}"> checked="checked"</c:if> >
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}"
                                type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>" disabled="disabled"
                                <c:if test="${fqForm.generalSettings.isGiverNameVisible['STUDENTS']}"> checked="checked"</c:if> >
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}"
                                type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>" disabled="disabled"
                                <c:if test="${fqForm.generalSettings.isRecipientNameVisible['STUDENTS']}"> checked="checked"</c:if> >
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
                                <c:if test="${fqForm.generalSettings.isResponseVisible['INSTRUCTORS']}"> checked="checked"</c:if> >
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}"
                                    type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>" disabled="disabled"
                                <c:if test="${fqForm.generalSettings.isGiverNameVisible['INSTRUCTORS']}"> checked="checked"</c:if> >
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}"
                                    type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>" disabled="disabled"
                                <c:if test="${fqForm.generalSettings.isRecipientNameVisible['INSTRUCTORS']}"> checked="checked"</c:if> >
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <div>
                <span class="pull-right">
                    <input id="button_question_submit-${fqForm.question.questionNumber}"
                           type="submit" class="btn btn-primary"
                           value="Save Changes" tabindex="0"
                           style="display:none">
                </span>
            </div>
        </div>
    </div>
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${fqForm.feedbackSessionName}">
    <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${fqForm.courseId}">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_ID %>" value="${fqForm.question.id}">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBER %>" value="${fqForm.question.questionNumber}">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_TYPE %>" value="${fqForm.question.questionType}">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE %>" id="<%= Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE %>-${fqForm.question.questionNumber}" value="edit">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO %>" >
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO %>" >
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO %>" >
    <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
</form>
<br><br>