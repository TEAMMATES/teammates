<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackSubmissionEdit" prefix="feedbackSubmissionEdit" %>

<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${data.bundle.feedbackSession.feedbackSessionName}">
<input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${data.bundle.feedbackSession.courseId}">

<c:choose>
    <c:when test="${not empty data.account.googleId}">
        <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
    </c:when>
    <c:otherwise>
        <input type="hidden" name="<%=Const.ParamsNames.REGKEY %>" value="${data.student.key}">
        <input type="hidden" name="<%=Const.ParamsNames.STUDENT_EMAIL %>" value="${data.account.email}">
    </c:otherwise>
</c:choose>

<t:statusMessage />
<feedbackSubmissionEdit:feedbackSessionDetailsPanel feedbackSession="${data.bundle.feedbackSession}"/>

<c:if test="${data.moderation}">
    <div class="row">
        <span class="help-block align-center">
            <%= Const.FEEDBACK_SESSION_QUESTIONS_HIDDEN %>
        </span>
    </div>
</c:if>

<c:forEach items="${data.questionsWithResponses}" var="questionWithResponses">
    <c:if test="${questionWithResponses.numOfResponseBoxes ne 0}">
        <feedbackSubmissionEdit:questionWithResponses isSessionOpenForSubmission="${data.sessionOpenForSubmission}" 
                                                      isShowRealQuestionNumber="${data.showRealQuestionNumber}" 
                                                      questionWithResponses="${questionWithResponses}"/>
    </c:if>
</c:forEach>
