<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
