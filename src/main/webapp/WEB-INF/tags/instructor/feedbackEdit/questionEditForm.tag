<%@ tag description="instructorFeedbacks - form for editing feedback question" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbackEdit" prefix="feedbackEdit" %>

<%@ attribute name="fqForm" type="teammates.ui.template.FeedbackQuestionEditForm" required="true"%>
<%@ attribute name="numQn" required="true"%>


<form class="form-horizontal form_question" role="form" method="post"
    action="${fqForm.action}"
    id="form_editquestion-${fqForm.questionIndex}" name="form_editquestions"
    onsubmit="tallyCheckboxes(${fqForm.questionIndex})"
    ${ fqForm.questionHasResponses ? 'editStatus="hasResponses"' : '' }>
    <div class="panel panel-primary questionTable" id="questionTable${fqForm.questionIndex}">
        <div class="panel-heading">
            <div class="row">
                <div class="col-sm-7">
                    <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary"
                            name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBER %>"
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBER %>-${fqForm.questionIndex}">
                            <c:forEach items="${fqForm.questionNumberOptions}" var="option">
                                <option ${option.attributesToString}>
                                    ${option.content}
                                </option>
                            </c:forEach>
                        </select>
                        &nbsp;${fqForm.questionTypeDisplayName}
                    </span>
                </div>
                <div class="col-sm-5 mobile-margin-top-10px">
                    <span class="mobile-no-pull pull-right">
                        <a class="btn btn-primary btn-xs"
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_EDITTEXT %>-${fqForm.questionIndex}"
                            data-toggle="tooltip" data-placement="top"
                            title="<%= Const.Tooltips.FEEDBACK_QUESTION_EDIT %>"
                            onclick="enableEdit(${fqForm.questionIndex},${numQn})">
                            Edit
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none"
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_SAVECHANGESTEXT %>-${fqForm.questionIndex}">
                            Save Changes
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none"
                            onclick="discardChanges(${fqForm.questionIndex})"
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_DISCARDCHANGES %>-${fqForm.questionIndex}"
                            data-toggle="tooltip" data-placement="top"
                            title="<%= Const.Tooltips.FEEDBACK_QUESTION_DISCARDCHANGES %>">
                            Discard Changes
                        </a>
                        <a class="btn btn-primary btn-xs"
                            onclick="deleteQuestion(${fqForm.questionIndex})"
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
                        id="<%= Const.ParamsNames.FEEDBACK_QUESTION_TEXT %>-${fqForm.questionIndex}"
                        data-toggle="tooltip" data-placement="top"
                        title="<%= Const.Tooltips.FEEDBACK_QUESTION_INPUT_INSTRUCTIONS %>"
                        tabindex="9"
                        disabled>${fqForm.questionText}</textarea>
                </div>
                ${fqForm.questionSpecificEditFormHtml}
            </div>
            
            <br>
            <feedbackEdit:questionFeedbackPathSettings fqForm="${fqForm}"/>
            <feedbackEdit:questionVisibilityOptions fqForm="${fqForm}"/>
            
            <div>
                <span class="pull-right">
                    <input id="button_question_submit-${fqForm.questionIndex}"
                           type="submit" class="btn btn-primary"
                           value="Save Changes" tabindex="0"
                           style="display:none">
                </span>
            </div>
        </div>
    </div>
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${fqForm.feedbackSessionName}">
    <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${fqForm.courseId}">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_ID %>" value="${fqForm.questionId}">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBER %>" value="${fqForm.questionIndex}">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_TYPE %>" value="${fqForm.questionType}">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE %>" id="<%= Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE %>-${fqForm.questionIndex}" value="edit">
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO %>" >
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO %>" >
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO %>" >
    <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
</form>
<br><br>
