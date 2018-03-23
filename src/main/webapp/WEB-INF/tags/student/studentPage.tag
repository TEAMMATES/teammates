<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Generic Student Page" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ attribute name="cssIncludes" %>
<%@ attribute name="jsIncludes" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="altNavBar" %>
<%@ attribute name="altFooter" %>
<c:set var="defaultNavBar">
  <ts:navBar />
</c:set>
<c:set var="defaultFooter">
  <t:bodyFooter />
</c:set>

<t:page pageTitle="${title}" bodyTitle="${title}">
  <jsp:attribute name="cssIncludes">
    ${cssIncludes}
  </jsp:attribute>
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
