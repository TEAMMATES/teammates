<%@ tag description="Individual table entry for contributors" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="contribution" %>
<%@ attribute name="ghUsername" %>
<%@ attribute name="name" required="true" %>
<li>
    ${name} [<c:if test="${not empty ghUsername}"><a href="https://github.com/TEAMMATES/teammates/issues?q=involves:${ghUsername}" target="_blank"></c:if>
        <c:choose>
            <c:when test="${empty contribution}">
                bug reporting/fixing, enhancements
            </c:when>
            <c:otherwise>
                ${contribution}
            </c:otherwise>
        </c:choose>
    <c:if test="${not empty ghUsername}"></a></c:if>]
</li>
