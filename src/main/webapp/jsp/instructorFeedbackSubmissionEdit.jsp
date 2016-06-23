<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/shared/feedbackSubmissionEdit" prefix="tsfse" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/feedbackSubmissionsEdit.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
</c:set>

<c:if test="${data.headerHidden}">
    <c:set var="altHeader">
        <c:choose>
            <c:when test="${data.preview}">
                <nav class="navbar navbar-default navbar-fixed-top">
                    <h3 class="text-center">Previewing Session as Instructor ${data.previewInstructor.name} (${data.previewInstructor.email})</h3>
                </nav>
            </c:when>
            <c:when test="${data.moderation}">
                <nav class="navbar navbar-default navbar-fixed-top">
                    <h3 class="text-center">Moderating Responses for Instructor ${data.previewInstructor.name} (${data.previewInstructor.email})</h3>
                </nav>
            </c:when>
            <c:otherwise>
                <%-- For when header is hidden but it is not a preview --%>
                <span style="display:none"></span>
            </c:otherwise>
        </c:choose>
    </c:set>
</c:if>

<ti:instructorPage pageTitle="TEAMMATES - Submit Feedback" bodyTitle="Submit Feedback" jsIncludes="${jsIncludes}" altNavBar="${altHeader}">
    <tsfse:feedbackSubmissionForm feedbackSubmissionForm="${data}" moderatedPersonEmail="${data.previewInstructor.email}"/>    
</ti:instructorPage>

