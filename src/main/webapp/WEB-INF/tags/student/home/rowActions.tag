<%@ tag description="studentHome - Course table" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="actions" type="teammates.ui.template.StudentFeedbackSessionActions" required="true" %>
<%@ attribute name="index" required="true" %>
<a class="btn btn-default btn-xs btn-tm-actions<c:if test="${not actions.sessionPublished}"> disabled</c:if>"
    href="${actions.sessionPublished ? actions.studentFeedbackResultsLink : 'javascript:;'}"
    name="viewFeedbackResults${index}"
    id="viewFeedbackResults${index}"
    data-toggle="tooltip"
    data-placement="top"
    title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTS %>"
    role="button">
  View Responses
</a>
<c:choose>
  <c:when test="${actions.submitted}">
    <a class="btn btn-default btn-xs btn-tm-actions"
        href="${actions.studentFeedbackResponseEditLink}"
        name="editFeedbackResponses${index}"
        id="editFeedbackResponses${index}"
        data-toggle="tooltip"
        data-placement="top"
        title="${actions.tooltipText}"
        role="button">
      ${actions.buttonText}
    </a>
  </c:when>
  <c:otherwise>
    <a class="btn btn-default btn-xs btn-tm-actions<c:if test="${not actions.sessionVisible}"> disabled</c:if>"
        href="${actions.sessionVisible ? actions.studentFeedbackResponseEditLink : 'javascript:;'}"
        id="submitFeedback${index}"
        data-toggle="tooltip"
        data-placement="top"
        title="${actions.tooltipText}"
        role="button">
      ${actions.buttonText}
    </a>
  </c:otherwise>
</c:choose>
