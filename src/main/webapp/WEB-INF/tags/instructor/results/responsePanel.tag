<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResults - participant > participant > question" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>

<%@ attribute name="responsePanel" type="teammates.ui.template.InstructorFeedbackResultsResponsePanel" required="true" %>

<div class="panel panel-info">
  <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
  <div class="panel-heading">
    Question ${responsePanel.question.questionNumber}: <span class="text-preserve-space">${responsePanel.questionText}${responsePanel.additionalInfoText}</span>
  </div>
  <div class="panel-body">
    <div style="clear:both; overflow: hidden">
      <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
      <div class="pull-left text-preserve-space">${responsePanel.displayableResponse}</div>
      <c:if test="${responsePanel.question.questionDetails.instructorCommentsOnResponsesAllowed}">
        <button type="button" class="btn btn-default btn-xs icon-button pull-right show-frc-add-form" id="button_add_comment"
            data-recipientindex="${responsePanel.recipientIndex}" data-giverindex="${responsePanel.giverIndex}"
            data-qnindex="${responsePanel.qnIndex}" data-sectionindex="${responsePanel.sectionId}"
            data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COMMENT_ADD%>"
            <c:if test="${!responsePanel.allowedToAddComment}">
              disabled
            </c:if>>
          <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
        </button>
      </c:if>
    </div>

    <c:set var="firstIndex"  value="${responsePanel.recipientIndex}"/>
    <c:set var="secondIndex" value="${responsePanel.giverIndex}"/>
    <c:set var="thirdIndex"  value="${responsePanel.qnIndex}"/>
    <c:set var="fourthIndex" value="${responsePanel.sectionId}"/>
    <c:if test="${responsePanel.question.questionDetails.feedbackParticipantCommentsOnResponsesAllowed
    and not empty responsePanel.feedbackParticipantComment}">
      <br>
      <shared:feedbackResponseCommentRowForFeedbackParticipant frc="${responsePanel.feedbackParticipantComment}"/>
    </c:if>
    <c:if test="${responsePanel.question.questionDetails.instructorCommentsOnResponsesAllowed}">
      <ul class="list-group" id="responseCommentTable-${responsePanel.sectionId}-${responsePanel.recipientIndex}-${responsePanel.giverIndex}-${responsePanel.qnIndex}"
          style="${not empty responsePanel.instructorComments ? 'margin-top:15px;': 'display:none'}">
        <c:forEach items="${responsePanel.instructorComments}" var="responseComment" varStatus="status">
          <shared:feedbackResponseCommentRow frc="${responseComment}" firstIndex="${firstIndex}"
              secondIndex="${secondIndex}" thirdIndex="${thirdIndex}"
              fourthIndex="${fourthIndex}" frcIndex="${status.count}"/>
        </c:forEach>
        <shared:feedbackResponseCommentAdd frc="${responsePanel.frcForAdding}" firstIndex="${firstIndex}"
            secondIndex="${secondIndex}" thirdIndex="${thirdIndex}" fourthIndex="${fourthIndex}" />
      </ul>
    </c:if>

  </div>

</div>
