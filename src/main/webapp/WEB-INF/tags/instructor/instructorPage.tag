<%@ tag description="Generic Instructor Page" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ attribute name="pageTitle" required="true" %>
<%@ attribute name="jsIncludes" %>
<%@ attribute name="bodyTitle" required="true" %>
<%@ attribute name="bodyOnload" %>
<t:page pageTitle="${pageTitle}" bodyTitle="${bodyTitle}" bodyOnload="${bodyOnload}">
    <jsp:attribute name="jsIncludes">
        ${jsIncludes}
    </jsp:attribute>
    <jsp:attribute name="navBar">
        <ti:navBar />
    </jsp:attribute>
    <jsp:attribute name="bodyFooter">
        <t:bodyFooter />
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody />
    </jsp:body>
</t:page>