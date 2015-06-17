<%@ tag description="studentFeedbackSubmissionEdit.jsp, studentFeedbackQuestionSubmissionEdit.jsp
                         - Display preview or moderation header" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="preview" type="java.lang.Boolean" required="true" %>
<%@ attribute name="moderation" type="java.lang.Boolean" required="true" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="email" required="true" %>

<c:choose>
    <c:when test="${preview}">
        <nav class="navbar navbar-default navbar-fixed-top">
            <h3 class="text-center">Previewing Session as Student ${name} (${email})</h3>
        </nav>
    </c:when>
    <c:otherwise>
        <c:if test="${moderation}">
            <nav class="navbar navbar-default navbar-fixed-top">
                <h3 class="text-center">Moderating Responses for Student ${name} (${email})</h3>
            </nav>
        </c:if>
    </c:otherwise>
</c:choose>