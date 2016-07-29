<%@ tag description="Individual table entry for past team members" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="imgSuffix" %>
<%@ attribute name="desc" required="true" %>
<li>
    <c:if test="${not empty imgSuffix}">
        <img src="images/teammembers/${imgSuffix}.png" width="80px">
    </c:if>
    ${desc}
</li>
