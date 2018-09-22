<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="feedbackSubmissionEdit.jsp - Display question with responses" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/shared/feedbackSubmissionEdit" prefix="feedbackSubmissionEdit" %>

<%@ attribute name="questionWithResponses" type="teammates.ui.template.StudentFeedbackSubmissionEditQuestionsWithResponses" required="true" %>
<%@ attribute name="isShowRealQuestionNumber" type="java.lang.Boolean" required="true" %>
<%@ attribute name="isSessionOpenForSubmission" type="java.lang.Boolean" required="true" %>
<%@ attribute name="moderatedPersonEmail" required="true" %>

<c:set var="isRecipientNameHidden" value="${questionWithResponses.question.recipientNameHidden}"/>

<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_TYPE %>-${questionWithResponses.question.qnIndx}"
    value="${questionWithResponses.question.questionType}">

<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_ID %>-${questionWithResponses.question.qnIndx}"
    value="${questionWithResponses.question.questionId}">

<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL %>-${questionWithResponses.question.qnIndx}"
    value="${questionWithResponses.numOfResponseBoxes}">

<div class="form-horizontal">
  <div class="panel panel-primary"<c:if test="${questionWithResponses.question.moderatedQuestion}"> id="moderated-question"</c:if>>

    <div class="panel-heading">
      Question ${isShowRealQuestionNumber ? questionWithResponses.question.questionNumber : questionWithResponses.question.qnIndx}:
      <br>
      <%-- Note: When an element has class text-preserve-space, do not insert HTML spaces --%>
      <span class="text-preserve-space"><c:out value="${questionWithResponses.question.questionText}"/></span>
    </div>

    <div class="panel-body">
      <c:if test="${not empty questionWithResponses.question.questionDescription}">
        <div class="panel panel-default">
          <div class="panel-body">
            <b>More details:</b><br><hr>${questionWithResponses.question.questionDescription}
          </div>
        </div>

      </c:if>
      <p class="text-muted">Only the following persons can see your responses: </p>
      <ul class="text-muted">
        <c:if test="${empty questionWithResponses.question.visibilityMessages}">
          <li class="unordered">No-one but the feedback session creator can see your responses.</li>
        </c:if>

        <c:forEach items="${questionWithResponses.question.visibilityMessages}" var="line">
          <li class="unordered">${line}</li>
        </c:forEach>
      </ul>
      <c:if test="${not empty questionWithResponses.question.instructions}">
        <p class="text-muted">Instructions:</p>
        <ul class="text-muted">
          <c:forEach items="${questionWithResponses.question.instructions}" var="instruction">
            <li class="unordered">${instruction}</li>
          </c:forEach>
        </ul>
      </c:if>
      <div class="constraints-${questionWithResponses.question.qnIndx}"></div>
      <c:if test="${not isRecipientNameHidden}">
        <div class="evalueeLabel-${questionWithResponses.question.qnIndx} form-inline mobile-align-left">
          <label for="input" style="text-indent: 24px">
            <span data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.EVALUEE_DESCRIPTION %>">
              Evaluee/Recipient
            </span>
          </label>
        </div>
        <br>
      </c:if>
      <c:if test="${questionWithResponses.question.giverTeam}">
        <p class="text-warning">Please note that you are submitting this response on behalf of your team.</p>
      </c:if>

      <c:if test="${questionWithResponses.numOfResponseBoxes eq 0}">
        <p class="text-warning">${questionWithResponses.question.messageToDisplayIfNoRecipientAvailable}</p>
      </c:if>

      <c:forEach items="${questionWithResponses.responses}" var="response">
        <feedbackSubmissionEdit:response response="${response}" isSessionOpenForSubmission="${isSessionOpenForSubmission}"
            questionWithResponses="${questionWithResponses}" moderatedPersonEmail="${moderatedPersonEmail}"/>
      </c:forEach>
    </div>
  </div>
</div>
<br><br>
