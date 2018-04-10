<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/search" prefix="adminSearch" %>

<c:set var="jsIncludes">
  <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY_HIGHLIGHT %>"></script>
  <script type="text/javascript" src="/js/adminSearch.js"></script>
</c:set>

<ta:adminPage title="Admin Search" jsIncludes="${jsIncludes}">
  <adminSearch:searchPageInput searchKey="${data.searchKey}"/>

  <c:if test="${not empty data.instructorResultList}">
    <adminSearch:instructorResultsTable instructorResultsTable="${data.instructorTable}"/>
  </c:if>

  <c:if test="${not empty data.studentResultList}">
    <adminSearch:studentResultsTable studentResultsTable="${data.studentTable}"/>
  </c:if>

  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
</ta:adminPage>
