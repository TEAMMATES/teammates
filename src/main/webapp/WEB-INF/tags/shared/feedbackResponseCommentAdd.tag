<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Feedback Response Add Comment" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>
<%@ attribute name="frc" type="teammates.ui.template.FeedbackResponseCommentRow" required="true" %>
<%@ attribute name="firstIndex" %>
<%@ attribute name="secondIndex" %>
<%@ attribute name="thirdIndex" %>
<%@ attribute name="fourthIndex" %>
<%@ attribute name="isOnQuestionsPage" %>

<c:choose>
  <c:when test="${not empty fourthIndex}">
    <c:set var="divId" value="${fourthIndex}-${firstIndex}-${secondIndex}-${thirdIndex}" />
  </c:when>
  <c:when test="${not empty firstIndex && not empty secondIndex && not empty thirdIndex}">
    <c:set var="divId" value="${firstIndex}-${secondIndex}-${thirdIndex}" />
  </c:when>
</c:choose>

<c:set var="submitLink"><%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD %></c:set>
<li class="list-group-item list-group-item-warning"
    id="showResponseCommentAddForm-${divId}" style="display: none;">
  <shared:feedbackResponseCommentForm fsIndex="${firstIndex}"
      secondIndex="${secondIndex}"
      thirdIndex="${thirdIndex}"
      fourthIndex="${fourthIndex}"
      frc="${frc}"
      divId="${divId}"
      formType="Add"
      textAreaId="responseCommentAddForm"
      submitLink="${submitLink}"
      buttonText="Add"
      isOnQuestionsPage="${isOnQuestionsPage}" />
</li>
