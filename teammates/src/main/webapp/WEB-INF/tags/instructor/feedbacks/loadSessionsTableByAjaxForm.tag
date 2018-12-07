<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks - form which is currently used to load the sessions table by ajax." pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>

<%@ attribute name="fsList" type="teammates.ui.template.FeedbackSessionsTable" required="true"%>

<form style="display:none;" id="ajaxForSessions" class="ajaxForSessionsForm"
    action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE %>">
  <input type="hidden"
      name="<%= Const.ParamsNames.USER_ID %>"
      value="${data.account.googleId}">
  <input type="hidden"
      name="<%= Const.ParamsNames.IS_USING_AJAX %>"
      value="on">
  <c:if test="${fsList.feedbackSessionNameToHighlight != null && fsList.courseIdForHighlight != null}">
    <input type="hidden"
        name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>"
        value="${fsList.feedbackSessionNameToHighlight}">
    <input type="hidden"
        name="<%= Const.ParamsNames.COURSE_ID %>"
        value="${fsList.courseIdForHighlight}">
  </c:if>
</form>
