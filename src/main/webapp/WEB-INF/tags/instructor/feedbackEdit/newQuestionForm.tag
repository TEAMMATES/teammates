<%@ tag description="instructorFeedbacks - feedback sessions table/list" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.logic.core.Emails.EmailType" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>

<%@ attribute name="fqForm" type="teammates.ui.template.FeedbackQuestionEditForm" required="true"%>

<form class="form-horizontal form_question" role="form" method="post"
    action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD %>"
    name="form_addquestions" onsubmit="tallyCheckboxes('')" >
    <div class="well well-plain inputTable" id="addNewQuestionTable">
        <div class="row">
            <div class="col-sm-6">
                <label for="questionTypeChoice" class="control-label col-sm-3">
                    Question Type
                </label>
                <div class="col-sm-8">
                    <select class="form-control questionType"
                        name="<%= Const.ParamsNames.FEEDBACK_QUESTION_TYPE %>"
                        id="questionTypeChoice">
                        ${fqForm.questionTypeOptions}
                    </select>
                </div>
                <div class="col-sm-1">
                    <h5><a href="/instructorHelp.html#fbQuestionTypes" target="_blank"><span class="glyphicon glyphicon-info-sign"></span></a></h5>
                </div>
            </div>
            <div class="col-sm-2">
                <a id="button_openframe" class="btn btn-primary"
                    onclick="showNewQuestionFrame(document.getElementById('questionTypeChoice').value)">
                    &nbsp;&nbsp;&nbsp;Add New Question&nbsp;&nbsp;&nbsp;
                </a>
            </div>
            <div class="col-sm-2">
                <a id="button_copy" class="btn btn-primary">
                    &nbsp;&nbsp;&nbsp;Copy Question&nbsp;&nbsp;&nbsp;
                </a>
            </div>
            <div class="col-sm-2">
                <a class="btn btn-primary"
                    href="${fqForm.doneEditingLink}">
                    &nbsp;&nbsp;&nbsp;Done Editing&nbsp;&nbsp;&nbsp;
                </a>
            </div>
        </div>
    </div>

    <div class="panel panel-primary questionTable" id="questionTableNew" style="display:none;">
        <div class="panel-heading">
            <strong>Question</strong>
            <select class="questionNumber nonDestructive text-primary"
                name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBER %>"
                id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBER %>">
                <c:forEach items="${fqForm.questionNumberOptions}" var="option">
                    <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                        ${option.content}
                    </option>
                </c:forEach>
            </select>
            &nbsp;
            <span id="questionTypeHeader"></span>
            <span class="pull-right">
                <a class="btn btn-primary btn-xs" onclick="cancelEdit(-1)" data-toggle="tooltip" data-placement="top"
                    title="<%= Const.Tooltips.FEEDBACK_QUESTION_CANCEL_NEW %>">
                    Cancel
                </a>
                <a class="btn btn-primary btn-xs" onclick="deleteQuestion(-1)" data-toggle="tooltip" data-placement="top"
                    title="<%= Const.Tooltips.FEEDBACK_QUESTION_DELETE %>">
                    Delete
                </a>
            </span>
        </div>
        <div class="panel-body">
            <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-blue">
                <div>
                    <textarea class="form-control textvalue nonDestructive" rows="5"
                        name="questiontext" id="questiontext"
                        data-toggle="tooltip" data-placement="top"
                        title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?"
                        tabindex="9" disabled="disabled"></textarea>
                </div>
                ${fqForm.questionSpecificEditFormHtml}
            </div>
            <br>
            <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
                <div class="col-sm-12 padding-0">
                    <b>Feedback Path</b> (Who is giving feedback about whom?)
                </div>
                <div class="col-sm-6 padding-0" data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.FEEDBACK_SESSION_GIVER %>">
                    <label class="col-sm-5 control-label">
                        Who will give the feedback:
                    </label>
                    <div class="col-sm-7">
                        <select class="form-control participantSelect"
                            name="<%= Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE %>"
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE %>"
                            onchange="feedbackGiverUpdateVisibilityOptions(this)">
                            <c:forEach items="${fqForm.giverParticipantOptions}" var="option">
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
                            name="<%= Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE %>"
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE %>"
                            onchange="feedbackRecipientUpdateVisibilityOptions(this);getVisibilityMessageIfPreviewIsActive(this);">
                            <c:forEach items="${fqForm.recipientParticipantOptions}" var="option">
                                <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                    ${option.content}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="col-sm-6"></div>
                <div class="col-sm-6 numberOfEntitiesElements">
                    <label id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>_text-" class="control-label col-sm-4 small">
                        The maximum number of <span id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>_text_inner-"></span> each respondant should give feedback to:
                    </label>
                    <div class="col-sm-8 form-control-static">
                        <div class="col-sm-6">
                            <input class="nonDestructive" type="radio"
                                name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE %>" value="custom">
                            <input class="nonDestructive numberOfEntitiesBox" type="number"
                                name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>"
                                id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>"
                                min="1" max="250" value="1">
                        </div>
                        <div class="col-sm-6">
                            <input class="nonDestructive" type="radio"
                                name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE %>"
                                checked="checked" value="max">
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
                    <label class="btn btn-xs btn-info visibilityOptionsLabel" onchange="toggleVisibilityOptions(this)">
                        <input type="radio">
                        <span class="glyphicon glyphicon-pencil"></span> Edit Visibility
                    </label>
                    <label class="btn btn-xs btn-info active visibilityMessageButton" onchange="toggleVisibilityMessage(this)">
                        <input type="radio">
                        <span class="glyphicon glyphicon-eye-open"></span> Preview Visibility
                    </label>
                </div>
            </div>
            <div class="col-sm-12 background-color-light-green">
                <div class="col-sm-12 text-muted visibilityMessage">

                </div>
            </div>
            <div class="col-sm-12 margin-bottom-15px background-color-light-green">
                <div class="visibilityOptions">
                    <table class="dataTable participantTable table table-striped text-center background-color-white">
                        <tr>
                            <th class="text-center">User/Group</th>
                            <th class="text-center">Can see answer</th>
                            <th class="text-center">Can see giver's name</th>
                            <th class="text-center">Can see recipient's name</th>
                        </tr>
                        <tr>
                            <td class="text-left">
                                <div data-toggle="tooltip" data-placement="top"
                                    title="<%= Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT %>">
                                    Recipient(s)
                                </div>
                            </td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox"
                                    type="checkbox" value="<%= FeedbackParticipantType.RECEIVER %>" checked="checked">
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox"
                                    type="checkbox" value="<%= FeedbackParticipantType.RECEIVER %>" checked="checked">
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox"
                                    type="checkbox" value="<%= FeedbackParticipantType.RECEIVER %>" checked="checked" disabled="disabled">
                            </td>
                        </tr>
                        <tr>
                            <td class="text-left">
                                <div data-toggle="tooltip" data-placement="top"
                                    title="<%= Const.Tooltips.VISIBILITY_OPTIONS_GIVER_TEAM_MEMBERS %>">
                                    Giver's Team Members
                                </div>
                            </td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>"/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>"/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="text-left">
                                <div data-toggle="tooltip" data-placement="top"
                                    title="<%= Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT_TEAM_MEMBERS %>">
                                    Recipient's Team Members
                                </div>
                            </td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>"/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>"/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="text-left">
                                <div data-toggle="tooltip" data-placement="top"
                                    title="<%= Const.Tooltips.VISIBILITY_OPTIONS_OTHER_STUDENTS %>">
                                    Other students
                                </div>
                            </td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>"/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>"/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="text-left">
                                <div data-toggle="tooltip" data-placement="top"
                                    title="<%= Const.Tooltips.VISIBILITY_OPTIONS_INSTRUCTORS %>">
                                    Instructors
                                </div>
                            </td>
                            <td>
                                <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>" checked="checked"/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>" checked="checked"/>
                            </td>
                            <td>
                                <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>"checked="checked"/>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <div>
                <span class="pull-right">
                    <input id="button_submit_add" class="btn btn-primary"
                        type="submit" value="Save Question" tabindex="9">
                </span>
            </div>
        </div>
    </div>
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBER %>" value="${numOfQuestionsOnPage+1}">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${fqForm.feedbackSessionName}">
    <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${fqForm.courseId}">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO %>" >
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO %>" >
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO %>" >
    <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS %>"
        value="<%= FeedbackParticipantType.NONE.toString() %>"
        id="<%= Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS %>">
</form>