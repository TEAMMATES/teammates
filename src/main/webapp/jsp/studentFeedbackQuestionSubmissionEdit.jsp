<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackSubmissionEdit" prefix="feedbackSubmissionEdit" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/feedbackSubmissionsEdit.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
</c:set>

<c:if test="${data.headerHidden}">
    <c:set var="altHeader">    
        <c:choose>
            <c:when test="${data.preview}">
                <nav class="navbar navbar-default navbar-fixed-top">
                    <h3 class="text-center">Previewing Session as Student ${data.studentToViewPageAs.name} (${data.studentToViewPageAs.email})</h3>
                </nav>
            </c:when>
            <c:when test="${data.moderation}">
                <nav class="navbar navbar-default navbar-fixed-top">
                    <h3 class="text-center">Moderating Responses for Student ${data.studentToViewPageAs.name} (${data.studentToViewPageAs.email})</h3>
                </nav>
            </c:when>
            <c:otherwise>
                <%-- Cannot be empty to prevent default navbar --%>
                <span style="display:none"></span>
            </c:otherwise>
        </c:choose>
    </c:set>
</c:if>

<c:set var="altFooter">
    <%-- Cannot be empty to prevent default footer --%>
    <span style="display:none"></span>
</c:set>

<ts:studentPageCustom bodyTitle="Submit Feedback Question" pageTitle="TEAMMATES - Submit Feedback Question" jsIncludes="${jsIncludes}" altNavBar="${altHeader}" altFooter="${altFooter}">
    <c:if test="${not data.headerHidden}">
        <ts:studentMessageOfTheDay/>
    </c:if>
    
    <c:if test="${empty data.account.googleId}">
        <div id="registerMessage" class="alert alert-info">
            ${data.registerMessage}
        </div>
    </c:if>
    
    <feedbackSubmissionEdit:feedbackQuestionSubmissionForm feedbackSubmissionForm="${data}"/>
</ts:studentPageCustom>

