<%@ tag description="instructorFeedbacks - new feedback question form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbackEdit" prefix="feedbackEdit" %>

<%@ attribute name="fqForm" type="teammates.ui.template.FeedbackQuestionEditForm" required="true"%>
<%@ attribute name="nextQnNum" required="true"%>

<form class="form-horizontal form_question" role="form" method="post"
    action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD %>"
    name="form_addquestions" onsubmit="tallyCheckboxes('')" >
    <div class="well well-plain inputTable" id="addNewQuestionTable">
       
        <div class="row">
            <div class="col-sm-12 row">
                <div class="col-sm-offset-3 col-sm-9">

                    <button
                        id = "button_openframe"
                        class="btn btn-primary margin-bottom-7px dropdown-toggle"
                        type="button" data-toggle="dropdown">
                        Add New Question <span class="caret"></span>
                    </button>
                    <ul id="add-new-question-dropdown" class="dropdown-menu">
                        ${fqForm.questionTypeOptions}
                    </ul>

                    <a href="/instructorHelp.html#fbQuestionTypes"
                        target="_blank"> <i
                        class="glyphicon glyphicon-info-sign"></i>
                    </a> <a id="button_copy" class="btn btn-primary margin-bottom-7px" 
                            data-actionlink="${data.instructorQuestionCopyPageLink}"
                            data-fsname="${fqForm.feedbackSessionName}" data-courseid="${fqForm.courseId}"
                            data-target="#copyModal" data-toggle="modal">
                        Copy Question
                    </a>
                    <a id="button_done_editing" class="btn btn-primary margin-bottom-7px"
                        href="${fqForm.doneEditingLink}">
                        Done Editing
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="panel panel-primary questionTable" id="questionTableNew" style="display:none;">
        <div class="panel-heading">
            <div class="row">
                <div class="col-sm-7">
                    <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary"
                            name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBER %>"
                            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBER %>">
                            <c:forEach items="${fqForm.questionNumberOptions}" var="option">
                                <option ${option.attributesToString}>
                                    ${option.content}
                                </option>
                            </c:forEach>
                        </select>
                        &nbsp;
                    </span>
                    <span id="questionTypeHeader"></span>
                </div>
                <div class="col-sm-5 mobile-margin-top-10px">
                    <span class="mobile-no-pull pull-right">
                        <a class="btn btn-primary btn-xs"
                            onclick="discardChanges(-1)" data-toggle="tooltip" data-placement="top"
                            title="<%= Const.Tooltips.FEEDBACK_QUESTION_CANCEL_NEW %>">
                            Cancel
                        </a>
                        <a class="btn btn-primary btn-xs"
                            onclick="deleteQuestion(-1)" data-toggle="tooltip" data-placement="top"
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
                    <textarea class="form-control textvalue nonDestructive" rows="5"
                        name="questiontext" id="questiontext"
                        data-toggle="tooltip" data-placement="top"
                        title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?"
                        tabindex="9" disabled></textarea>
                </div>
                ${fqForm.questionSpecificEditFormHtml}
            </div>
            <br>
            <feedbackEdit:questionFeedbackPathSettings fqForm="${fqForm}"/>
            <feedbackEdit:questionVisibilityOptions fqForm="${fqForm}"/>
          
            <div>
                <span class="pull-right">
                    <input id="button_submit_add" class="btn btn-primary"
                        type="submit" value="Save Question" tabindex="9">
                </span>
            </div>
        </div>
    </div>
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBER %>" value="${nextQnNum}">
    <input type="hidden" id="<%= Const.ParamsNames.FEEDBACK_QUESTION_TYPE %>" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_TYPE %>">
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
