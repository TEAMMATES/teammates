<%@ tag description="Instructor Feedbacks Remind Particular Student AJAX Loaded Checkboxes for the Remind Modal" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>

<c:forEach items="${data.responseStatus.noResponse}" var="userToRemindEmail">
    <div class="checkbox">
        <label>
            <input type="checkbox" name="<%= Const.ParamsNames.SUBMISSION_REMIND_USERLIST %>" value="${userToRemindEmail}">
            ${data.responseStatus.emailNameTable[userToRemindEmail]}
        </label>
    </div>
</c:forEach>

<input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${data.courseId}">
<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${data.fsName}">
