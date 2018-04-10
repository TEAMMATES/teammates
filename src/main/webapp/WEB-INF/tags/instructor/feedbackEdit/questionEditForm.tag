<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks - form for editing feedback question" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbackEdit" prefix="feedbackEdit" %>

<%@ attribute name="fqForm" type="teammates.ui.template.FeedbackQuestionEditForm" required="true"%>

<form class="form-horizontal form_question tally-checkboxes" role="form" method="post"
    action="${fqForm.action}"
    id="form_editquestion-${fqForm.questionIndex}" name="form_editquestions"
    data-qnnumber="${fqForm.questionIndex}"
    ${ fqForm.questionHasResponses ? 'editStatus="hasResponses"' : '' }>
  <div class="panel panel-primary questionTable" id="questionTable-${fqForm.questionIndex}">
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
            <a class="btn btn-primary btn-xs btn-edit-qn"
                id="<%= Const.ParamsNames.FEEDBACK_QUESTION_EDITTEXT %>-${fqForm.questionIndex}"
                data-toggle="tooltip" data-placement="top"
                title="<%= Const.Tooltips.FEEDBACK_QUESTION_EDIT %>"
                data-qnnumber="${fqForm.questionIndex}">
              <span class="glyphicon glyphicon-pencil"></span> Edit
            </a>
            <a class="btn btn-primary btn-xs" style="display:none"
                id="<%= Const.ParamsNames.FEEDBACK_QUESTION_SAVECHANGESTEXT %>-${fqForm.questionIndex}">
              <span class="glyphicon glyphicon-ok"></span> Save
            </a>
            <a class="btn btn-primary btn-xs btn-discard-changes" style="display:none"
                data-qnnumber="${fqForm.questionIndex}"
                id="<%= Const.ParamsNames.FEEDBACK_QUESTION_DISCARDCHANGES %>-${fqForm.questionIndex}"
                data-toggle="tooltip" data-placement="top"
                title="<%= Const.Tooltips.FEEDBACK_QUESTION_DISCARDCHANGES %>">
              <span class="glyphicon glyphicon-ban-circle"></span> Discard
            </a>
            <a class="btn btn-primary btn-xs btn-delete-qn"
                data-qnnumber="${fqForm.questionIndex}"
                data-toggle="tooltip" data-placement="top">
              <span class=" glyphicon glyphicon-trash"></span> Delete
            </a>
          </span>
        </div>
      </div>
    </div>
    <div class="visibility-checkbox-delegate panel-body">
      <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
        <div class="form-group" style="padding: 15px;">
          <h5 class="col-sm-2">
            <label class="control-label" for="<%= Const.ParamsNames.FEEDBACK_QUESTION_TEXT %>-${fqForm.questionIndex}">
              Question
            </label>
          </h5>
          <div class="col-sm-10">
            <%-- Do not add whitespace between the opening and closing tags --%>
            <textarea class="form-control textvalue nonDestructive" rows="2"
                name="<%= Const.ParamsNames.FEEDBACK_QUESTION_TEXT %>"
                id="<%= Const.ParamsNames.FEEDBACK_QUESTION_TEXT %>-${fqForm.questionIndex}"
                data-toggle="tooltip" data-placement="top"
                title="<%= Const.Tooltips.FEEDBACK_QUESTION_INPUT_INSTRUCTIONS %>"
                placeholder="<%= Const.PlaceholderText.FEEDBACK_QUESTION %>"
                tabindex="9"
                disabled>${fn:escapeXml(fqForm.questionText)}</textarea>
          </div>
        </div>
        <div class="form-group" style="padding: 0 15px;">
          <h5 class="col-sm-2">
            <label class="align-left"
                for="<%= Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION %>-${fqForm.questionIndex}">
              [Optional]<br>Description
            </label>
          </h5>
          <div class="col-sm-10">
            <div class="well panel panel-default panel-body question-description"
                data-placeholder="<%= Const.PlaceholderText.FEEDBACK_QUESTION_DESCRIPTION %>"
                id="<%= Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION %>-${fqForm.questionIndex}"
                data-toggle="tooltip" data-placement="top"
                title="<%= Const.Tooltips.FEEDBACK_QUESTION_INPUT_DESCRIPTION %>"
                tabindex="9">
              ${fqForm.questionDescription}
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
  <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN%>" value="${data.sessionToken}">
</form>
<br><br>
