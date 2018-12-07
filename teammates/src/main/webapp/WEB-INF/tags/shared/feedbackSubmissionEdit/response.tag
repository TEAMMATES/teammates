<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="questionWithResponses.tag - Display question with responses" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="questionWithResponses" type="teammates.ui.template.StudentFeedbackSubmissionEditQuestionsWithResponses" required="true" %>
<%@ attribute name="response" type="teammates.ui.template.FeedbackSubmissionEditResponse" required="true" %>
<%@ attribute name="isSessionOpenForSubmission" type="java.lang.Boolean" required="true" %>
<%@ attribute name="moderatedPersonEmail" required="true" %>

<c:set var="isNumResponsesMax" value="${questionWithResponses.numOfResponseBoxes eq questionWithResponses.maxResponsesPossible}"/>
<c:set var="isRecipientNameHidden" value="${questionWithResponses.question.recipientNameHidden}"/>
<c:set var="recipientType" value="${questionWithResponses.question.recipientType}"/>
<c:set var="hasFeedbackParticipantCommentOnResponse" value="${not empty response.feedbackParticipantCommentOnResponse}"/>
<c:set var="responseIndex" value="${response.responseIndx}"/>
<c:set var="questionIndex" value="${questionWithResponses.question.qnIndx}"/>

<c:choose>
  <c:when test="${isRecipientNameHidden}"><c:set var="divClassType" value="col-sm-12"/></c:when>
  <c:when test="${isNumResponsesMax}"><c:set var="divClassType" value="col-sm-9"/></c:when>
  <c:otherwise><c:set var="divClassType" value="col-sm-7"/></c:otherwise>
</c:choose>

<c:set var="autoWidth" value="" />
<c:if test="${questionWithResponses.question.questionTypeConstsum}">
  <c:set var="autoWidth" value="width-auto" />
</c:if>

<br>
<div class="evalueeForm-${questionWithResponses.question.qnIndx} form-group margin-0" style="padding-left: 75px">
  <div ${isNumResponsesMax ? 'class="col-sm-12 form-inline mobile-align-left"' : 'class="col-sm-5 form-inline mobile-align-left"'}
      ${isRecipientNameHidden ?  'style="display:none"' : 'style="text-align:left"'}>

    <label>
      <select class="participantSelect middlealign<c:if test="${not response.existingResponse}"> newResponse</c:if> form-control"
          name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT %>-${questionWithResponses.question.qnIndx}-${response.responseIndx}"
          style="${isNumResponsesMax ? 'display:none;max-width:125px' : 'width:275px;max-width:275px'}"
          ${isSessionOpenForSubmission ? '' : 'disabled' }>

        <c:forEach items="${response.recipientOptionsForQuestion}" var="option">
          ${option}
        </c:forEach>
      </select>
    </label>
      <c:choose>
        <c:when test="${recipientType == 'STUDENT'}"> (Student)</c:when>
        <c:when test="${recipientType == 'INSTRUCTOR'}"> (Instructor)</c:when>
        <c:when test="${recipientType == 'TEAM'}"> (Team)</c:when>
      </c:choose>:
  </div>
  <c:choose>
    <c:when test="${questionWithResponses.question.questionTypeConstsum}">
      ${response.submissionFormHtml}
    </c:when>
    <c:otherwise>
      <div class="${divClassType}">
        ${response.submissionFormHtml}
      </div>
    </c:otherwise>
  </c:choose>
  <c:if test="${questionWithResponses.feedbackParticipantCommentsOnResponsesAllowed}">
    <div class="col-sm-12" <c:if test="${hasFeedbackParticipantCommentOnResponse}">style="display: none"</c:if>>
      <br>
      <button type="button" class="btn-link show-frc-add-form"
              id="button_add_comment-${questionIndex}-${responseIndex}"
              data-responseindex="${responseIndex}" data-qnindex="${questionIndex}" data-toggle="tooltip"
              data-placement="top" title="<%=Const.Tooltips.COMMENT_ADD_FOR_FEEDBACK_PARTICIPANT%>"
          <c:if test="${data.preview or (not data.submittable)}"> disabled </c:if>>
        Want to add comment to your response? Click here.
      </button>
    </div>
    <div class="col-sm-12" <c:if test="${not hasFeedbackParticipantCommentOnResponse}">style="display: none"</c:if>>
    <br>
      <ul class="list-group" id="responseCommentTable-${questionIndex}-${responseIndex}">
        <c:if test="${hasFeedbackParticipantCommentOnResponse}">
          <shared:feedbackResponseCommentRowForFeedbackParticipant frc="${response.feedbackParticipantCommentOnResponse}"
              responseIndex="${response.responseIndx}" qnIndex="${questionWithResponses.question.qnIndx}"
              moderatedPersonEmail="${moderatedPersonEmail}"
              isPreview="${data.preview}" isSubmittable="${data.submittable}" isModeration="${data.moderation}"/>
        </c:if>

        <shared:feedbackResponseCommentAddFormForFeedbackParticipant frc="${response.feedbackResponseCommentAdd}"
            responseIndex="${response.responseIndx}" qnIndex="${questionWithResponses.question.qnIndx}"/>
      </ul>
    </div>
  </c:if>
  <c:if test="${response.existingResponse}">
    <input type="hidden"
           name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>-${questionWithResponses.question.qnIndx}-${response.responseIndx}"
           value="<c:out value="${response.responseId}"/>">
  </c:if>
</div>
