<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/email/log" prefix="adminEmailLog" %>

<c:set var="jsIncludes">
  <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY_HIGHLIGHT %>"></script>
  <script type="text/javascript" src="/js/adminEmailLog.js"></script>
</c:set>

<ta:adminPage title="Admin Email Log" jsIncludes="${jsIncludes}">
  <adminEmailLog:filterPanel filterQuery="${data.filterQuery}" queryKeywordsForReceiver="${data.queryKeywordsForReceiver}"
      queryKeywordsForSubject="${data.queryKeywordsForSubject}" queryKeywordsForContent="${data.queryKeywordsForContent}"/>

  <%-- this form is used to store parameters for ajaxloader only --%>
  <form id="ajaxLoaderDataForm">
    <input type="hidden" name="offset" value="">
    <%--
      - This parameter determines whether the logs with requests contained in "excludedLogRequestURIs"
      - in AdminActivityLogPageData should be shown. Use "?all=true" in URL to show all logs. This will keep showing all
      - logs despite any action or change in the page unless the page is reloaded with "?all=false"
      - or simply reloaded with this parameter omitted.
      --%>

    <input type="hidden" id="filterQuery" name="filterQuery" value="${data.filterQuery}">
  </form>

  <c:if test="${not empty data.queryMessage}">
    <div class="alert alert-danger" id="queryMessage">
      <span class="glyphicon glyphicon-warning-sign"></span>
      <c:out value=" ${data.queryMessage}"/>
    </div>
  </c:if>

  <br>
  <br>

  <adminEmailLog:emailLogTable logs="${data.logs}"/>
  <t:statusMessage doNotFocusToStatus="${true}" statusMessagesToUser="${data.statusMessagesToUser}" />
</ta:adminPage>
