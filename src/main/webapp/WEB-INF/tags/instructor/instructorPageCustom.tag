<%@ tag description="Generic Instructor Page with Optional Nav Bar and Footer" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ attribute name="pageTitle" required="true" %>
<%@ attribute name="jsIncludes" %>
<%@ attribute name="bodyTitle" required="true" %>
<%@ attribute name="bodyOnload" %>

<%@ attribute name="altNavBar" %>
<%@ attribute name="altFooter" %>
<c:set var="defaultNavBar"><ti:navBar /></c:set>
<c:set var="defaultFooter"><t:bodyFooter /></c:set>

<t:page pageTitle="${pageTitle}" bodyTitle="${bodyTitle}" bodyOnload="${bodyOnload}">
    <jsp:attribute name="jsIncludes">
        ${jsIncludes}
    </jsp:attribute>
    <jsp:attribute name="navBar">
        ${empty altNavBar ? defaultNavBar : altNavBar}
    </jsp:attribute>
    <jsp:attribute name="bodyFooter">
        ${empty altFooter ? defaultFooter : altFooter}
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody />
    </jsp:body>
</t:page>