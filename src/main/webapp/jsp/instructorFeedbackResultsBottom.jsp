<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="r" %>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<c:set var="showAll" value="${!data.loadingByAjax}" />

<c:if test="${data.allSectionsSelected}">
    <div class="panel panel-warning">
        <div class="panel-heading<c:if test="${not showAll}"> ajax_response_rate_submit</c:if>">
            <form style="display:none;" id="responseRate" class="responseRateForm" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
                <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${data.bundle.feedbackSession.courseId}">
                <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="${data.bundle.feedbackSession.feedbackSessionName}">
                <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
                <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>" value="-1">
            </form>
            <div class="display-icon pull-right">
                <span class="glyphicon${showAll ? ' glyphicon-chevron-up' : ' glyphicon-chevron-down'} pull-right"></span>
            </div>
            Participants who have not responded to any question
        </div>
        <div class="panel-collapse collapse<c:if test="${showAll}"> in</c:if>" id="responseStatus">
            <c:if test="${showAll}">
                <r:noResponsePanel noResponsePanel="${data.noResponsePanel}" />
            </c:if>
        </div>
    </div>
</c:if>