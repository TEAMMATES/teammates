<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Generic Admin Page" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ attribute name="cssIncludes" %>
<%@ attribute name="jsIncludes" %>
<%@ attribute name="title" required="true" %>
<t:page pageTitle="${title} [Administrator]" bodyTitle="${title}">
  <jsp:attribute name="cssIncludes">
    ${cssIncludes}
  </jsp:attribute>
  <jsp:attribute name="jsIncludes">
    ${jsIncludes}
  </jsp:attribute>
  <jsp:attribute name="navBar">
    <ta:navBar />
  </jsp:attribute>
  <jsp:attribute name="bodyFooter">
    <t:bodyFooter isAdmin="${true}" />
  </jsp:attribute>
  <jsp:body>
    <jsp:doBody />
  </jsp:body>
</t:page>
