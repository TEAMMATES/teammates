<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackSubmissionEdit" prefix="feedbackSubmissionEdit" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/feedbackSubmissionsEdit.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
</c:set>

<t:pageWithoutNavbar bodyTitle="Submit Feedback Question" pageTitle="TEAMMATES - Submit Feedback Question">
    <jsp:attribute name="jsIncludes">
        ${jsIncludes}
    </jsp:attribute>
    <jsp:attribute name="header">
        <c:choose>
            <c:when test="${not data.headerHidden}">
                <jsp:include page="<%= Const.ViewURIs.STUDENT_HEADER%>" />
                <jsp:include page="<%= Const.ViewURIs.STUDENT_MOTD%>" />
            </c:when>
            <c:otherwise>
                <feedbackSubmissionEdit:header preview="${data.preview}" moderation="${data.moderation}" 
                                               email="${data.studentToViewPageAs.email}" 
                                               name="${data.studentToViewPageAs.name}"/>
            </c:otherwise>
        </c:choose>
    </jsp:attribute>
    
    <jsp:body>
        <ts:registerMessage googleId="${data.account.googleId}" registerMessage="${data.registerMessage}"/>    
        <br>
        <feedbackSubmissionEdit:feedbackSubmissionForm 
                         submitAction="${data.submitActionQuestion}" preview="${data.preview}" 
                         moderatedStudentEmail="${data.studentToViewPageAs.email}" 
                         questionResponseBundle="${data.bundle.questionResponseBundle}" 
                         moderation="${data.moderation}" submittable="${data.submittable}"/>
    </jsp:body>
</t:pageWithoutNavbar>
