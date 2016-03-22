<%@ tag description="instructorFeedbacks and instructorHome - Feedback Session publish/unpublish button" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="publishButton" type="teammates.ui.template.FeedbackSessionPublishButton" required="true" %>
<%@ attribute name="buttonType" required="true" %>
<a class="btn ${buttonType} btn-tm-actions session-${publishButton.actionNameLowercase}-for-test"
   href="${publishButton.actionLink}"
   title="${publishButton.tooltipText}"
   data-toggle="tooltip"
   data-placement="top"
   <c:if test="${publishButton.actionAllowed}">onclick="return ${publishButton.onclickAction}"</c:if>
   <c:if test="${not publishButton.actionAllowed}">onclick="return false" disabled</c:if>>
    ${publishButton.actionName} Results
</a>