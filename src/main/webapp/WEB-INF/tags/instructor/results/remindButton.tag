<%@ tag description="instructorFeedbackResults - remind button" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="remindButton" type="teammates.ui.template.InstructorFeedbackResultsRemindButton" required="true" %>

<div class="remind-no-response">
  <a href="javascript:;" data-actionlink="${remindButton.urlLink}" class="${remindButton.className}" data-toggle="modal"
      <c:if test="${remindButton.disabled}">disabled=""</c:if> data-target="#remindModal">
    ${remindButton.buttonText}
  </a>
</div>
