<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResults - moderation button" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ attribute name="moderationButton" type="teammates.ui.template.InstructorFeedbackResultsModerationButton" required="true" %>

<form class="inline" method="post" action="${moderationButton.moderateFeedbackResponseLink}" target="_blank">
  <input type="submit" class="${moderationButton.className}" value="${moderationButton.buttonText}" <c:if test="${moderationButton.disabled}">disabled="disabled"</c:if> data-toggle="tooltip" title="<%=Const.Tooltips.FEEDBACK_SESSION_MODERATE_FEEDBACK%>">
  <input type="hidden" name="courseid" value="${moderationButton.courseId}">
  <input type="hidden" name="fsname" value="${moderationButton.feedbackSessionName}">
  <c:if test="${moderationButton.questionId != null}">
    <input type="hidden" name="moderatedquestionid" value="${moderationButton.questionId}">
  </c:if>
  <input type="hidden" name="moderatedperson" value="${fn:escapeXml(moderationButton.giverIdentifier)}">
</form>
