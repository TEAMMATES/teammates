<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackSubmissionEdit" prefix="feedbackSubmissionEdit" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/feedbackSubmissionsEdit.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
</c:set>



<c:if test="${data.headerHidden}">
    <c:set var="altHeader">
        <nav class="navbar navbar-default navbar-fixed-top">
            <c:choose>
                <c:when test="${data.preview}">
                    <h3 class="text-center">Previewing Session as Student ${data.studentToViewPageAs.name} (${data.studentToViewPageAs.email})</h3>
                </c:when>
                <c:when test="${data.moderation}">
                    <h3 class="text-center">Moderating Responses for Student ${data.studentToViewPageAs.name} (${data.studentToViewPageAs.email})</h3>
                </c:when>
            </c:choose>
        </nav>
    </c:set>
</c:if>


<ts:studentPageCustom bodyTitle="Submit Feedback" pageTitle="TEAMMATES - Submit Feedback" jsIncludes="${jsIncludes}" altNavBar="${altHeader}">
    <c:if test="${not data.headerHidden}">
        <ts:studentMessageOfTheDay/>
    </c:if>
       
    <c:if test="${empty data.account.googleId}">
        <div id="registerMessage" class="alert alert-info">
            ${data.registerMessage}
        </div>
    </c:if>
    
    <feedbackSubmissionEdit:feedbackSubmissionForm feedbackSubmissionForm="${data}"/>
    
</ts:studentPageCustom>
