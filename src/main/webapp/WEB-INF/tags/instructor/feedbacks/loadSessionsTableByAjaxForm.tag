<%@ tag description="instructorFeedbacks  - Copy From Another FS modal" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
 
 <%@ attribute name="fsForm" type="teammates.ui.template.FeedbackSessionsForm" required="true"%>
 
 <form style="display:none;" id="ajaxForSessions" class="ajaxForSessionsForm"
    action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE %>">
    <input type="hidden"
        name="<%= Const.ParamsNames.USER_ID %>"
        value="${data.account.googleId}">
    <input type="hidden"
        name="<%= Const.ParamsNames.IS_USING_AJAX %>"
        value="on">
    <c:if test="${fsForm.feedbackSessionNameForSessionList != null && fsForm.courseIdForNewSession != null}">
        <input type="hidden"
            name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>"
            value="${fsForm.feedbackSessionNameForSessionList}">
        <input type="hidden"
            name="<%= Const.ParamsNames.COURSE_ID %>"
            value="${fsForm.courseIdForNewSession}">
    </c:if>
</form>