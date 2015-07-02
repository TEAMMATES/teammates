<%@ tag description="studentHome - Course table" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="actions" type="teammates.ui.template.StudentFeedbackSessionActions" required="true" %>
<a class="btn btn-default btn-xs btn-tm-actions<c:if test="${not actions.sessionPublished}"> disabled</c:if>"
   <c:if test="${not actions.sessionPublished}">onclick="return false"</c:if>
   href="${actions.studentFeedbackResultsLink}"
   name="viewFeedbackResults${actions.index}"
   id="viewFeedbackResults${actions.index}"
   data-toggle="tooltip"
   data-placement="top"
   title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTS %>"
   role="button">
    View Responses
</a>
<c:choose>
    <c:when test="${actions.hasSubmitted}">
        <a class="btn btn-default btn-xs btn-tm-actions"
           href="${actions.studentFeedbackResponseEditLink}"
           name="editFeedbackResponses${actions.index}"
           id="editFeedbackResponses${actions.index}"
           data-toggle="tooltip"
           data-placement="top"
           title="${actions.tooltipText}"
           role="button">
            ${actions.buttonText}
        </a>
    </c:when>
    <c:otherwise>
        <a class="btn btn-default btn-xs btn-tm-actions<c:if test="${not actions.sessionVisible}"> disabled</c:if>"
           <c:if test="${not actions.sessionVisible}">onclick="return false"</c:if>
           href="${actions.studentFeedbackResponseEditLink}"
           id="submitFeedback${actions.index}"
           data-toggle="tooltip"
           data-placement="top"
           title="${actions.tooltipText}"
           role="button">
            ${actions.buttonText}
        </a>
    </c:otherwise>
</c:choose>