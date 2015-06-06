<%@ tag description="Status message" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<c:choose>
    <c:when test="${not empty message}">
        <div id="statusMessage"  class="alert alert-<c:choose><c:when test="${error}">danger</c:when><c:otherwise>warning</c:otherwise></c:choose>">
            ${message}
        </div>
        <script type="text/javascript">
            document.getElementById('statusMessage').scrollIntoView();
        </script>
    </c:when>
    <c:otherwise>
        <div id="statusMessage" style="display: none;"></div>
    </c:otherwise>
</c:choose>