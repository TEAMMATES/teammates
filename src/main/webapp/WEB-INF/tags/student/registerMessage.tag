<%@ tag description="Register message for unregistered students" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="googleId" required="true" %>
<%@ attribute name="registerMessage" required="true" %>

<c:if test="${empty googleId}">
    <div id="registerMessage" class="alert alert-info">
        ${registerMessage}
    </div>
</c:if>
