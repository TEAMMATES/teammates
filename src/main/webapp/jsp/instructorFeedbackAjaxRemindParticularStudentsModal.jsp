<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="feedbacks" %>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.ui.controller.InstructorFeedbackRemindParticularStudentsPageData"%>
<%
    InstructorFeedbackRemindParticularStudentsPageData data = (InstructorFeedbackRemindParticularStudentsPageData) request.getAttribute("data");
%>
<% for (String userToRemind: data.responseStatus.noResponse) { %>
    <div class="checkbox">
        <label>
            <input type="checkbox" name="<%= Const.ParamsNames.SUBMISSION_REMIND_USERLIST %>" value="<%= userToRemind %>">
            <%= data.responseStatus.emailNameTable.get(userToRemind) %>
        </label>
    </div>
<% } %>
<input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="<%= data.courseId %>">
<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%= data.fsName %>">