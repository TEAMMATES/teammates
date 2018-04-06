<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/email" prefix="adminEmail" %>

<c:set var="jsIncludes">
  <script type="text/javascript" src="<%= FrontEndLibrary.TINYMCE %>"></script>
  <script type="text/javascript" src="/js/adminEmail.js"></script>
</c:set>

<ta:adminPage title="Admin Email" jsIncludes="${jsIncludes}">
  <adminEmail:navTabs isCompose="${data.adminEmailCompose}" isDraft="${data.adminEmailDraft}"
      isSent="${data.adminEmailSent}" isTrash="${data.adminEmailTrash}"/>

  <c:choose>
    <c:when test="${data.pageState eq 'COMPOSE'}">
      <adminEmail:compose emailToEdit="${data.adminEmailComposePageData.emailToEdit}"/>
    </c:when>

    <c:when test="${data.pageState eq 'SENT'}">
      <adminEmail:sent sentEmailTable="${data.sentEmailTable}"/>
    </c:when>

    <c:when test="${data.pageState eq 'DRAFT'}">
      <adminEmail:draft draftEmailTable="${data.draftEmailTable}"/>
    </c:when>

    <c:otherwise>
      <adminEmail:trash trashEmailTable="${data.trashEmailTable}"/>
    </c:otherwise>
  </c:choose>

  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
</ta:adminPage>
