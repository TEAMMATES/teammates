<%@ tag description="instructorFeedbacks and instructorHome - Feedback Session publish/unpublish button" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="publishButton" type="teammates.ui.template.FeedbackSessionPublishButton" required="true" %>
<%@ attribute name="buttonType" required="true" %>
<c:choose>
    <c:when test="${publishButton.hasUnpublish}">
        <a class="btn ${buttonType} btn-tm-actions session-unpublish-for-test"
           href="${publishButton.unpublishLink}"
           title="<%= Const.Tooltips.FEEDBACK_SESSION_UNPUBLISH %>"
           data-toggle="tooltip"
           data-placement="top"
           onclick="return toggleUnpublishEvaluation(${publishButton.toggleUnpublishSessionParams});"
           <c:if test="${not publishButton.allowedToUnpublish}">disabled="disabled"</c:if>>
            Unpublish Results
        </a>
    </c:when>
    <c:otherwise>
        <a class="btn ${buttonType} btn-tm-actions session-publish-for-test<c:if test="${not publishButton.hasPublish}"> disabled</c:if>"
           href="${publishButton.publishLink}"
           title="${publishButton.tooltipText}"
           data-toggle="tooltip"
           data-placement="top"
           <c:if test="${publishButton.hasPublish}">onclick="return togglePublishEvaluation(${publishButton.togglePublishSessionParams});"</c:if>
           <c:if test="${not publishButton.hasPublish}">onclick="return false"</c:if>
           <c:if test="${not publishButton.allowedToPublish}">disabled="disabled"</c:if>>
            Publish Results
        </a>
    </c:otherwise>
</c:choose>