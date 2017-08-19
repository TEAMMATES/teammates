<%@ tag description="instructorFeedbacks and instructorHome - Feedback Session publish/unpublish button" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="publishButton" type="teammates.ui.template.FeedbackSessionPublishButton" required="true" %>
<%@ attribute name="showTooltip" type="java.lang.Boolean" required="true" %>
<%@ attribute name="buttonType" %>
<a href="${publishButton.actionLink}"
    class="<c:if test="${not empty buttonType}">${buttonType} </c:if>session-${publishButton.actionNameLowercase}-for-test"
    <c:if test="${showTooltip}">
      title="${publishButton.tooltipText}"
      data-toggle="tooltip"
      data-placement="top"
    </c:if>
    data-sending-published-email="${publishButton.sendingPublishedEmail}"
    data-fsname="${publishButton.feedbackSessionName}"
    <c:if test="${not publishButton.actionAllowed}">disabled</c:if>>
  ${publishButton.actionName} Results
</a>
