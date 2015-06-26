<%@ tag description="Generic Student Page" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ attribute name="pageTitle" required="true" %>
<%@ attribute name="jsIncludes" %>
<%@ attribute name="bodyTitle" required="true" %>
<t:page pageTitle="${pageTitle}" bodyTitle="${bodyTitle}">
    <jsp:attribute name="jsIncludes">
        ${jsIncludes}
    </jsp:attribute>
    <jsp:attribute name="navBar">
        <ts:navBar />
    </jsp:attribute>
    <jsp:attribute name="bodyFooter">
        <t:bodyFooter />
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody />
    </jsp:body>
</t:page>