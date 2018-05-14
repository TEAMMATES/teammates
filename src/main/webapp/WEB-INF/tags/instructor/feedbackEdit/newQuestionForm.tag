<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks - new feedback question form" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbackEdit" prefix="feedbackEdit" %>

<%@ attribute name="fqForm" type="teammates.ui.template.FeedbackQuestionEditForm" required="true"%>
<%@ attribute name="nextQnNum" required="true"%>

<c:set var="NEW_QUESTION" value="-1" />

<form id="form_editquestion-${NEW_QUESTION}" class="form-horizontal form_question tally-checkboxes" role="form" method="post"
    action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD %>"
    name="form_addquestions" data-qnnumber="${NEW_QUESTION}">
  <div class="well well-plain" id="addNewQuestionTable">

    <div class="row">
      <div class="col-sm-offset-3 col-sm-9">
        <div class="btn-group">
          <button
              id = "button_openframe"
              class="btn btn-primary margin-bottom-7px dropdown-toggle"
              type="button" data-toggle="dropdown">
            Add New Question <span class="caret"></span>
          </button>
          <ul id="add-new-question-dropdown" class="dropdown-menu">
            ${fqForm.questionTypeOptions}
          </ul>
        </div>

        <a href="/instructorHelp.jsp#questions"
            target="_blank" rel="noopener noreferrer">
          <i class="glyphicon glyphicon-info-sign"></i>
        </a>
        <a id="button_copy" class="btn btn-primary margin-bottom-7px"
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

  <div class="panel panel-primary questionTable" id="questionTable-${NEW_QUESTION}" style="display:none;">
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
            <a class="btn btn-primary btn-xs btn-discard-changes"
                data-qnnumber="${NEW_QUESTION}" data-toggle="tooltip" data-placement="top"
                title="<%= Const.Tooltips.FEEDBACK_QUESTION_CANCEL_NEW %>">
              Cancel
            </a>
          </span>
        </div>
      </div>
    </div>
    <div class="panel-body">
      <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
        <div class="form-group" style="padding: 15px;">
          <h5 class="col-sm-2">
            <label class="control-label" for="<%= Const.ParamsNames.FEEDBACK_QUESTION_TEXT %>-${NEW_QUESTION}">
              Question
            </label>
          </h5>
          <div class="col-sm-10">
            <%-- Do not add whitespace between the opening and closing tags --%>
            <textarea class="form-control textvalue nonDestructive" rows="2"
                name="<%= Const.ParamsNames.FEEDBACK_QUESTION_TEXT %>"
                id="<%= Const.ParamsNames.FEEDBACK_QUESTION_TEXT %>-${NEW_QUESTION}"
                data-toggle="tooltip" data-placement="top"
                title="<%= Const.Tooltips.FEEDBACK_QUESTION_INPUT_INSTRUCTIONS %>"
                placeholder="<%= Const.PlaceholderText.FEEDBACK_QUESTION %>"
                tabindex="9"
                disabled></textarea>
          </div>
        </div>
        <div class="form-group" style="padding: 0 15px;">
          <h5 class="col-sm-2">
            <label class="align-left"
                for="<%= Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION %>-${NEW_QUESTION}">
              [Optional]<br>Description
            </label>
          </h5>
          <div class="col-sm-10">
            <div class="panel panel-default panel-body question-description"
                id="<%= Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION %>-${NEW_QUESTION}"
                data-toggle="tooltip" data-placement="top"
                title="<%= Const.Tooltips.FEEDBACK_QUESTION_INPUT_DESCRIPTION %>"
                data-placeholder="<%= Const.PlaceholderText.FEEDBACK_QUESTION_DESCRIPTION %>"
                tabindex="9">
            </div>
            <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION %>">
          </div>
          ${fqForm.questionSpecificEditFormHtml}
        </div>
      </div>
      <feedbackEdit:questionFeedbackPathSettings fqForm="${fqForm}"/>
      <feedbackEdit:questionVisibilityOptions fqForm="${fqForm}"/>

      <div>
        <span class="pull-right">
          <button id="button_submit_add" class="btn btn-primary" type="submit" tabindex="9">
            Save Question
          </button>
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
  <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN %>" value="${data.sessionToken}">
</form>
