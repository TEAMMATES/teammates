<%@ tag description="instructorFeedbackResults - remind button" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="remindButton" type="teammates.ui.template.InstructorFeedbackResultsRemindButton" required="true" %>

<div style="display: inline-block; padding-right: 5px;" class="remind-no-response">
    <a href="#" data-actionlink="${remindButton.urlLink}" class="${remindButton.className}" data-toggle="modal"
            <c:if test="${remindButton.disabled}">disabled="disabled"</c:if> data-target="#remindModal">
        ${remindButton.buttonText}
    </a>
</div>
