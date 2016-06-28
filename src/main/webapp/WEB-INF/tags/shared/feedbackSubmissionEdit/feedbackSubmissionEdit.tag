<%@ tag description="Student/Instructor feedback submission edit page" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/shared/feedbackSubmissionEdit" prefix="tsfse" %>
<%@ attribute name="isInstructor" required="true" %>
<%@ attribute name="moderatedPersonEmail" required="true" %>
<%@ attribute name="moderatedPersonName" required="true" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/feedbackSubmissionsEdit.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
</c:set>

<c:if test="${data.headerHidden}">
    <c:set var="altHeader">
        <nav class="navbar navbar-default navbar-fixed-top">
            <c:choose>
                <c:when test="${data.preview}">
                    <h3 class="text-center">Previewing Session as ${isInstructor ? "Instructor" : "Student"} ${moderatedPersonName} (${moderatedPersonEmail})</h3>
                </c:when>
                <c:when test="${data.moderation}">
                    <h3 class="text-center">Moderating Responses for ${isInstructor ? "Instructor" : "Student"} ${moderatedPersonName} (${moderatedPersonEmail})</h3>
                </c:when>
            </c:choose>
        </nav>
    </c:set>
</c:if>

<c:choose>
    <c:when test="${isInstructor}">
        <ti:instructorPage pageTitle="TEAMMATES - Submit Feedback" bodyTitle="Submit Feedback" jsIncludes="${jsIncludes}" altNavBar="${altHeader}">
            <tsfse:feedbackSubmissionForm moderatedPersonEmail="${moderatedPersonEmail}"/>    
        </ti:instructorPage>
    </c:when>
    <c:otherwise>
        <ts:studentPage pageTitle="TEAMMATES - Submit Feedback" bodyTitle="Submit Feedback" jsIncludes="${jsIncludes}" altNavBar="${altHeader}">
            <c:if test="${not data.headerHidden}">
                <ts:studentMessageOfTheDay />
            </c:if>
            <c:if test="${empty data.account.googleId}">
                <div id="registerMessage" class="alert alert-info">
                    ${data.registerMessage}
                </div>
            </c:if>
            <tsfse:feedbackSubmissionForm moderatedPersonEmail="${moderatedPersonEmail}"/>
        </ts:studentPage>
    </c:otherwise>
</c:choose>
