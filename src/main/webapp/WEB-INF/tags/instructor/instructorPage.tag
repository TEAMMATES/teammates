<%@ tag description="Generic Instructor Page" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ attribute name="pageTitle" required="true" %>
<%@ attribute name="jsIncludes" %>
<%@ attribute name="bodyTitle" required="true" %>
<t:page pageTitle="${pageTitle}" bodyTitle="${bodyTitle}">
    <jsp:attribute name="jsIncludes">
        ${jsIncludes}
    </jsp:attribute>
    <jsp:attribute name="navBar">
        <ti:navBar />
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody />
    </jsp:body>
</t:page>