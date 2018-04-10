<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackEdit - feedback question feedback path settings" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>

<%@ attribute name="fqForm" type="teammates.ui.template.FeedbackQuestionEditForm" required="true"%>
<c:set var="isNewQuestion" value="${fqForm.questionIndex eq -1}" />

<div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
  <div class="margin-bottom-7px">
    <b class="feedback-path-title">Feedback Path</b> (Who is giving feedback about whom?)
  </div>
  <div class="feedback-path-dropdown btn-group">
    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
      <c:choose>
        <c:when test="${isNewQuestion}">
          Please select a feedback path <span class="caret"></span>
        </c:when>
        <c:when test="${fqForm.feedbackPathSettings.commonPathSelected}">
          ${fqForm.feedbackPathSettings.selectedGiver.displayNameGiver} will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> ${fqForm.feedbackPathSettings.selectedRecipient.displayNameRecipient}
        </c:when>
        <c:otherwise>
          Predefined combinations:
        </c:otherwise>
      </c:choose>
    </button>
    <ul class="dropdown-menu">
      <li class="dropdown-header">Common feedback path combinations</li>
      <c:forEach items="<%= Const.FeedbackQuestion.COMMON_FEEDBACK_PATHS %>" var="commonPath">
        <li class="dropdown-submenu">
          <c:set var="commonGiver" value="${commonPath.key}" />
          <a>${commonGiver.displayNameGiver} will give feedback on...</a>
          <ul class="dropdown-menu" data-toggle="tooltip" data-trigger="manual" data-placement="top" data-container="body" title="Choose an option">
            <li>
              <c:forEach items="${commonPath.value}" var="commonRecipient">
                <a class="feedback-path-dropdown-option" href="javascript:;"
                    data-giver-type="${commonGiver}" data-recipient-type="${commonRecipient}"
                    data-path-description="${commonGiver.displayNameGiver} will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> ${commonRecipient.displayNameRecipient}">
                  ${commonRecipient.displayNameRecipient}
                </a>
              </c:forEach>
            </li>
          </ul>
        </li>
      </c:forEach>
      <li role="separator" class="divider"></li>
      <li><a class="feedback-path-dropdown-option feedback-path-dropdown-option-other" href="javascript:;" data-path-description="Predefined combinations:">Other predefined combinations...</a></li>
    </ul>
  </div>
  <div class="feedback-path-others margin-top-7px"<c:if test="${fqForm.feedbackPathSettings.commonPathSelected || isNewQuestion}"> style="display:none;"</c:if>>
    <div class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px"
        data-toggle="tooltip" data-placement="top"
        title="<%= Const.Tooltips.FEEDBACK_SESSION_GIVER %>">
      <label class="col-sm-4 col-lg-5 control-label">
        Who will give the feedback:
      </label>
      <div class="col-sm-8 col-lg-7">
        <select class="form-control participantSelect"
            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE %>-${fqForm.questionIndex}"
            name="<%= Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE %>"
            <c:if test="${!fqForm.editable}">disabled</c:if>>
          <c:forEach items="<%= FeedbackParticipantType.GIVERS %>" var="giverType">
            <option <c:if test="${fqForm.feedbackPathSettings.selectedGiver eq giverType}">selected </c:if>value="${giverType}">
              ${giverType.displayNameGiver}
            </option>
          </c:forEach>
        </select>
      </div>
    </div>
    <div class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top"
        title="<%= Const.Tooltips.FEEDBACK_SESSION_RECIPIENT %>">
      <label class="col-sm-4 col-lg-5 control-label">
        Who the feedback is about:
      </label>
      <div class="col-sm-8 col-lg-7">
        <select class="form-control participantSelect"
            id="<%= Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE %>-${fqForm.questionIndex}"
            name="<%= Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE %>"
            <c:if test="${!fqForm.editable}">disabled</c:if>>
          <c:forEach items="<%= FeedbackParticipantType.RECIPIENTS %>" var="recipientType">
            <option <c:if test="${fqForm.feedbackPathSettings.selectedRecipient eq recipientType}">selected </c:if>value="${recipientType}">
              ${recipientType.displayNameRecipient}
            </option>
          </c:forEach>
        </select>
      </div>
    </div>
    <div class="col-sm-12 row numberOfEntitiesElements">
      <label class="control-label col-sm-4 small">
        The maximum number of <span class='number-of-entities-inner-text'></span> each respondent should give feedback to:
      </label>
      <div class="col-sm-8 form-control-static">
        <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
          <input class="nonDestructive" type="radio"
              name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE %>"
              <c:if test="${fqForm.feedbackPathSettings.numberOfEntitiesToGiveFeedbackToChecked}">checked=""</c:if>
              value="custom" <c:if test="${!fqForm.editable}">disabled</c:if>>
          <input class="nonDestructive numberOfEntitiesBox width-75-pc" type="number"
              name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES %>"
              value="${fqForm.feedbackPathSettings.numOfEntitiesToGiveFeedbackToValue}"
              min="1" max="250" <c:if test="${!fqForm.editable}">disabled</c:if>>
        </div>
        <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
          <input class="nonDestructive" type="radio"
              name="<%= Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE %>"
              <c:if test="${!fqForm.feedbackPathSettings.numberOfEntitiesToGiveFeedbackToChecked}">checked=""</c:if>
              value="max" <c:if test="${!fqForm.editable}">disabled</c:if>>
          <span class="">Unlimited</span>
        </div>
      </div>
    </div>
  </div>
</div>
<br>
