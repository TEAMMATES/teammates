<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackSubmissionEdit" prefix="feedbackSubmissionEdit" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/feedbackSubmissionsEdit.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
</c:set>

<ts:studentPage pageTitle="TEAMMATES - Submit Feedback" bodyTitle="Submit Feedback" jsIncludes="${jsIncludes}">
    <c:choose>
        <c:when test="${not data.headerHidden}">
            <%-- <jsp:include page="<%= Const.ViewURIs.STUDENT_HEADER%>" /> --%>
            <jsp:include page="<%= Const.ViewURIs.STUDENT_MOTD%>" />
        </c:when>
        <c:otherwise>
            <feedbackSubmissionEdit:header preview="${data.preview}" moderation="${data.moderation}" 
                                           email="${data.studentToViewPageAs.email}" 
                                           name="${data.studentToViewPageAs.name}"/>
        </c:otherwise>
    </c:choose>
       
    <c:if test="${empty data.account.googleId}">
        <div id="registerMessage" class="alert alert-info">
            ${data.registerMessage}
        </div>
    </c:if>
    <feedbackSubmissionEdit:feedbackSubmissionForm feedbackSubmissionForm="${data}"/>
</ts:studentPage>
