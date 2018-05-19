<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="questionWithResponses.tag - Display question with responses" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="questionWithResponses" type="teammates.ui.template.StudentFeedbackSubmissionEditQuestionsWithResponses" required="true" %>
<%@ attribute name="response" type="teammates.ui.template.FeedbackSubmissionEditResponse" required="true" %>
<%@ attribute name="isSessionOpenForSubmission" type="java.lang.Boolean" required="true" %>
<%@ attribute name="questionIndex" type="java.lang.Integer" required="true" %>
<%@ attribute name="responseGiverIndex" type="java.lang.Integer" required="true" %>
<%@ attribute name="responseRecipientIndex" type="java.lang.Integer" required="true" %>
<%@ attribute name="isInstructor" required="true" %>
<%@ attribute name="moderatedPersonEmail" required="true" %>

<c:set var="isNumResponsesMax" value="${questionWithResponses.numOfResponseBoxes eq questionWithResponses.maxResponsesPossible}"/>
<c:set var="isRecipientNameHidden" value="${questionWithResponses.question.recipientNameHidden}"/>
<c:set var="recipientType" value="${questionWithResponses.question.recipientType}"/>
<c:set var="isGiverTeam" value="${questionWithResponses.question.giverTeam}"/>

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
<div class="form-group margin-0">
  <div ${isNumResponsesMax ? 'class="col-sm-3 form-inline mobile-align-left"' : 'class="col-sm-5 form-inline mobile-align-left"'}
      ${isRecipientNameHidden ?  'style="display:none"' : 'style="text-align:right"'}>

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
    <c:when test="${questionWithResponses.studentCommentsOnResponsesAllowed}">
      <button type="button" class="btn btn-default btn-xs icon-button pull-right show-frc-add-form" id="button_add_comment"
              data-recipientindex="${responseRecipientIndex}" data-giverindex="${responseGiverIndex}"
              data-qnindex="${questionIndex}" data-toggle="tooltip" data-placement="top"
              title="<%=Const.Tooltips.COMMENT_ADD%>">
        <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
      </button>
      <c:when test="${questionWithResponses.question.questionTypeConstsum}">
        ${response.submissionFormHtml}
      </c:when>
      <c:otherwise>
        <div class="${divClassType}">
            ${response.submissionFormHtml}
        </div>
      </c:otherwise>
      <br>
      <c:choose>
        <c:when test="${isGiverTeam}">
          <c:set var="giverRole" value="Team"/>
        </c:when>
        <c:when test="${isInstructor}">
          <c:set var="giverRole" value="Instructor"/>
        </c:when>
        <c:otherwise>
          <c:set var="giverRole" value="Student"/>
        </c:otherwise>
      </c:choose>
      <ul class="list-group" id="responseCommentTable-${responseRecipientIndex}-${responseGiverIndex}-${questionIndex}"
          style="${not empty response.commentsOnResponses ? 'margin-top:15px;': 'display:none'}">
        <c:forEach items="${response.commentsOnResponses}" var="responseComment" varStatus="status">
          <shared:feedbackResponseCommentRow frc="${responseComment}" firstIndex="${responseRecipientIndex}"
                                             secondIndex="${responseGiverIndex}" thirdIndex="${questionIndex}"
                                             frcIndex="${status.count}" isOnFeedbackSubmissionEditPage="true"
                                             moderatedPersonEmail="${moderatedPersonEmail}" giverRole="${giverRole}"
                                             isSessionOpenForSubmission="${isSessionOpenForSubmission}"/>
        </c:forEach>
        <shared:feedbackResponseCommentAdd frc="${response.feedbackResponseCommentAdd}" firstIndex="${responseRecipientIndex}"
                                           secondIndex="${responseGiverIndex}" thirdIndex="${questionIndex}"
                                           isOnFeedbackSubmissionEditPage="true"
                                           moderatedPersonEmail="${moderatedPersonEmail}"
                                           isPreview="${data.preview}" submitTable="${data.submittable}"
                                           giverRole="${giverRole}"/>
      </ul>
    </c:when>
    <c:when test="${questionWithResponses.question.questionTypeConstsum}">
      ${response.submissionFormHtml}
    </c:when>
    <c:otherwise>
      <div class="${divClassType}">
        ${response.submissionFormHtml}
      </div>
    </c:otherwise>
  </c:choose>
  <c:if test="${response.existingResponse}">
    <input type="hidden"
           name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>-${questionWithResponses.question.qnIndx}-${response.responseIndx}"
           value="<c:out value="${response.responseId}"/>">
  </c:if>
</div>
