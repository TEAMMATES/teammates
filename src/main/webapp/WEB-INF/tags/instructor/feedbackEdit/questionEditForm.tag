<%@ tag description="instructorFeedbacks - form for editing feedback question" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.logic.core.Emails.EmailType" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbackEdit" prefix="feedbackEdit" %>

<%@ attribute name="fqForm" type="teammates.ui.template.FeedbackQuestionEditForm" required="true"%>
<%@ attribute name="numQn" required="true"%>


<form class="form-horizontal form_question" role="form" method="post"
    action="${fqForm.action}"
    id="form_editquestion-${fqForm.question.questionNumber}" name="form_editquestions"
    onsubmit="tallyCheckboxes(${fqForm.question.questionNumber})"
    ${ fqForm.questionHasResponses ? 'editStatus="hasResponses"' : '' }>
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
                                <option ${option.attributesToString}>
                                    ${option.content}
                                </option>
                            </c:forEach>
                        </select>
                        &nbsp;${fqForm.question.questionDetails.questionTypeDisplayName}
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
                            onclick="enableEdit(${fqForm.question.questionNumber},${numQn})">
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
                    <%-- Do not add whitespace between the opening and closing tags --%>
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
            <feedbackEdit:questionFeedbackPathSettings fqForm="${fqForm}"/>
            <feedbackEdit:questionVisibilityOptions fqForm="${fqForm}"/>
            
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